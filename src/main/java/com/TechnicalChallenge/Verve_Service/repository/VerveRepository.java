package com.TechnicalChallenge.Verve_Service.repository;

import com.TechnicalChallenge.Verve_Service.model.VerveModel;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class VerveRepository{
    private final ConcurrentHashMap<String, Boolean> requestCache = new ConcurrentHashMap<>();

    public void save(VerveModel verveModel)
    {
        requestCache.put(String.valueOf(verveModel.getId()), true);
    }

    public void clearCache()
    {
        requestCache.clear();
    }
}
