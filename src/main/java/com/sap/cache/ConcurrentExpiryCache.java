package com.sap.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import lombok.Getter;

public class ConcurrentExpiryCache implements ICache {

    @Getter
    private final ConcurrentHashMap<String, Object> cache =
            new ConcurrentHashMap<>();
    @Getter
    private final DelayQueue<CacheItem> cleanerQueue = new DelayQueue<>();
    private long expiryDurationInMillis;
    private int capacity;

    public ConcurrentExpiryCache(int capacity, long expiryTime) {
        this.capacity = capacity;
        this.expiryDurationInMillis = expiryTime;

        Runnable runnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CacheItem delayedCacheItem = cleanerQueue.take();
                    cache.remove(delayedCacheItem.getKey(), delayedCacheItem.getValue());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        Thread cleaner = new Thread(runnable);
        cleaner.setDaemon(true);
        cleaner.start();
    }

    public void add(final String key, final Object value) {
        //size full
        if (isCacheFull()) {
            System.out.println("Cache full, please wait for expiry period.");
            return;
        }
        if (key == null) {
            return;
        }
        if (value == null) {
            cache.remove(key);
        } else {
            long expiryTime = System.currentTimeMillis() + expiryDurationInMillis;
            cache.put(key, value);
            cleanerQueue.put(new CacheItem(key, value, expiryTime));
        }
    }

    private boolean isCacheFull() {
        return cache.size() == capacity;
    }

    public void remove(final String key) {
        cache.remove(key);
    }

    public Object get(final String key) {
        Object val = cache.get(key);
        if (val != null) {
            long expiryTime = System.currentTimeMillis() + expiryDurationInMillis;
            CacheItem cacheItem = new CacheItem(key, val, expiryTime);
            if (cleanerQueue.remove(cacheItem)) {
                cleanerQueue.add(cacheItem);
            }
            return cacheItem.getValue();
        }
        return val;
    }

    public void clear() {
        cache.clear();
    }

    public long size() {
        return cache.size();
    }
}
