package com.TechnicalChallenge.Verve_Service.controller;

import com.TechnicalChallenge.Verve_Service.dto.PostRequestPayload;
import com.TechnicalChallenge.Verve_Service.service.VerveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verve")
public class VerveController {

    private static final Logger log = LoggerFactory.getLogger(VerveController.class);

    @Autowired
    private VerveService verveService;

    @GetMapping("/accept")
    public ResponseEntity<String> acceptRequest(@RequestParam Long id, @RequestParam(required = false) String endpoint)
    {
        try {
            PostRequestPayload payload = new PostRequestPayload(id, endpoint, 0);
            return verveService.processRequest(payload);
        }catch (Exception e)
        {
            log.error("Error in acceptRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed");
        }
    }
}
