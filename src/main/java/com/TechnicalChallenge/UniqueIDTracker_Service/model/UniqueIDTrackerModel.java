package com.TechnicalChallenge.UniqueIDTracker_Service.model;

public class UniqueIDTrackerModel {

    private Long id;
    private String endpoint;

    public UniqueIDTrackerModel(Long id, String endpoint) {
        this.id = id;
        this.endpoint = endpoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
