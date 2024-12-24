package com.TechnicalChallenge.UniqueIDTracker_Service.dto;

public class PostRequestPayload {

    private Long id;
    private String endpoint;
    private int uniqueRequestCount;

    public PostRequestPayload(Long id, String endpoint, int uniqueRequestCount) {
        this.id = id;
        this.endpoint = endpoint;
        this.uniqueRequestCount = uniqueRequestCount;
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

    public int getUniqueRequestCount() {
        return uniqueRequestCount;
    }

    public void setUniqueRequestCount(int uniqueRequestCount) {
        this.uniqueRequestCount = uniqueRequestCount;
    }
}
