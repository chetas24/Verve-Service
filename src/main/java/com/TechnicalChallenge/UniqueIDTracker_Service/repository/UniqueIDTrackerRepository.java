package com.TechnicalChallenge.UniqueIDTracker_Service.repository;

import com.TechnicalChallenge.UniqueIDTracker_Service.model.UniqueIDTrackerModel;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UniqueIDTrackerRepository{
    private final ConcurrentHashMap<String, Boolean> requestCache = new ConcurrentHashMap<>();

    public void save(UniqueIDTrackerModel uniqueIDTrackerModel)
    {
        requestCache.put(String.valueOf(uniqueIDTrackerModel.getId()), true);
    }

    public void clearCache()
    {
        requestCache.clear();
    }
}
