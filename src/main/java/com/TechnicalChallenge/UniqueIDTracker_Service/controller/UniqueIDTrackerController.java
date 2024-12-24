package com.TechnicalChallenge.UniqueIDTracker_Service.controller;

import com.TechnicalChallenge.UniqueIDTracker_Service.dto.PostRequestPayload;
import com.TechnicalChallenge.UniqueIDTracker_Service.service.UniqueIDTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/UniqueIDTracker")
public class UniqueIDTrackerController {

    private static final Logger log = LoggerFactory.getLogger(UniqueIDTrackerController.class);

    @Autowired
    private UniqueIDTrackerService uniqueIDTrackerService;

    @GetMapping("/accept")
    public ResponseEntity<String> acceptRequest(@RequestParam Long id, @RequestParam(required = false) String endpoint)
    {
        try {
            PostRequestPayload payload = new PostRequestPayload(id, endpoint, 0);
            return uniqueIDTrackerService.processRequest(payload);
        }catch (Exception e)
        {
            log.error("Error in acceptRequest: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Failed");
        }
    }
}
