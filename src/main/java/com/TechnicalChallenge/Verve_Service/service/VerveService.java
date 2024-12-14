package com.TechnicalChallenge.Verve_Service.service;

import com.TechnicalChallenge.Verve_Service.dto.PostRequestPayload;
import com.TechnicalChallenge.Verve_Service.model.VerveModel;
import com.TechnicalChallenge.Verve_Service.repository.VerveRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class VerveService {

    private static final Logger log = LoggerFactory.getLogger(VerveService.class);
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private VerveRepository verveRepository;

    public ResponseEntity<String> processRequest(PostRequestPayload payload)
    {

        try{
            Long id = payload.getId();
            String endpoint = payload.getEndpoint();

            String redisKey = "uniqueIds";
            String value = String.valueOf(id);

            if(redisService.isPresent(redisKey, value))
            {
                return ResponseEntity.ok("failed");
            }

            boolean isNew = redisService.saveIfAbsent(redisKey, value, 60);
            if(isNew)
            {
                VerveModel verveModel = new VerveModel(id, endpoint);
                verveRepository.save(verveModel);
                atomicInteger.incrementAndGet();
            }

            if(endpoint != null)
            {
                int count = atomicInteger.get();
                sendAsyncRequest(endpoint, count, id);
            }

            return ResponseEntity.ok("ok");
        }
        catch (Exception e) {
            log.error("Error processing request with ID {}: {}", payload.getId(), e.getMessage(), e);
            return ResponseEntity.status(500).body("failed");
        }
    }

    public void sendAsyncRequest(String endpoint, int uniqueRequestCount, Long id)
    {
        try
        {
            PostRequestPayload payload = new PostRequestPayload(id, endpoint, uniqueRequestCount);

            webClientBuilder.build()
                    .post()
                    .uri(endpoint)
                    .bodyValue(payload)
                    .retrieve()
                    .toEntity(String.class)
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)))
                    .doOnSuccess(response -> log.info("POST Response from {}: HTTP Status: {}, Body: {}",
                            endpoint, response.getStatusCode(), response.getBody()))
                    .doOnError(error -> log.error("Error posting to endpoint {}: {}", endpoint, error.getMessage()))
                    .subscribe();
        } catch (Exception e) {
            log.error("Error sending async request to endpoint {}: {}", endpoint, e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = 60000)
    public void reportUniqueRequestCount()
    {
        try{
            int count = atomicInteger.getAndSet(0);
            log.info("Reporting unique request count: {}", count);
            kafkaService.sendCountToKafka(count);
            redisService.clearSet("uniqueIds");
            verveRepository.clearCache();
        }catch (Exception e) {
            log.error("Error during scheduled report: {}", e.getMessage(), e);
        }
    }
}
