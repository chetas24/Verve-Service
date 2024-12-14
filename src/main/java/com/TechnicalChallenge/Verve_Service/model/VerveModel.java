package com.TechnicalChallenge.Verve_Service.model;

public class VerveModel {

    private Long id;
    private String endpoint;

    public VerveModel(Long id, String endpoint) {
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
