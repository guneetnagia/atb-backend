package com.guneet.atb.util;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

@Component
public class CustomCacheManager extends ConcurrentMapCacheManager {

    public void setValue(String cacheName, Object name, Object value){
        Cache cache = getCache(cacheName);
        assert cache != null;
        cache.put(name, value);
    }

    public Object getValue(String cacheName, Object name){
        Cache cache = getCache(cacheName);
        if(cache != null){
            Cache.ValueWrapper cacheValue = cache.get(name);
            if(cacheValue != null) return cacheValue.get();
        }
        return null;
    }
}
