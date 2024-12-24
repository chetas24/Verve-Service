package com.TechnicalChallenge.UniqueIDTracker_Service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean saveIfAbsent(String key, String value, int expirySeconds) {
        if (key == null || value == null || expirySeconds <= 0) {
            log.warn("Invalid input: key={}, value={}, expirySeconds={}", key, value, expirySeconds);
            return false;
        }

        try{
            String deduplicationKey = key + ":" + value;

            Boolean isSet = redisTemplate.opsForValue().setIfAbsent(deduplicationKey, "1", Duration.ofSeconds(expirySeconds));

            if (Boolean.TRUE.equals(isSet)) {
                log.info("Key successfully saved in Redis: {}", deduplicationKey);
                return true;
            } else {
                log.info("Key already exists in Redis: {}", deduplicationKey);
                return false;
            }
        } catch (Exception e) {
            log.error("Error saving to Redis. Key: {}, Value: {}, Error: {}", key, value, e.getMessage(), e);
            return false;
        }
    }

    public boolean isPresent(String key, String value)
    {
        if (key == null || value == null) {
            log.warn("Invalid input for presence check: key={}, value={}", key, value);
            return false;
        }
        try{
            String deduplicationKey = key + ":" + value;
            Boolean isMember = redisTemplate.hasKey(deduplicationKey);

            if (Boolean.TRUE.equals(isMember)) {
                log.info("Key is present in Redis: {}", deduplicationKey);
                return true;
            } else {
                log.info("Key is not present in Redis: {}", deduplicationKey);
                return false;
            }
        }catch (Exception e)
        {
            log.error("Error checking Redis presence. Key: {}, Value: {}, Error: {}", key, value, e.getMessage(), e);
            return false;
        }
    }

    public void clearSet(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            log.warn("Invalid pattern for clearing keys: pattern={}", pattern);
            return;
        }

        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions().match(pattern).build())) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                redisTemplate.delete(key);
                log.info("Deleted key from Redis: {}", key);
            }
        } catch (Exception e) {
            log.error("Error clearing Redis keys with pattern {}: {}", pattern, e.getMessage(), e);
        }
    }
}
