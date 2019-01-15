package com.sap.cache;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class ConcurrentExpiryCache implements ICache {

    @Getter
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    @Getter
    private final DelayQueue<CacheItem> cleanerQueue = new DelayQueue<>();
    @Getter
    private CacheCleanerTask cacheCleanerTask = new CacheCleanerTask(cache, cleanerQueue);

    private long expiryDurationInMillis;
    private int capacity;

    public ConcurrentExpiryCache(int capacity, long expiryTimeMillis) {
        this.capacity = capacity;
        this.expiryDurationInMillis = expiryTimeMillis;

        Thread expirationCollector = new Thread(cacheCleanerTask);
        expirationCollector.setDaemon(true);
        expirationCollector.start();
    }

    public ConcurrentExpiryCache(int capacity, long expiryTime, TimeUnit timeUnit) {
        this(capacity, timeUnit.toMillis(expiryTime));
    }

    public void add(final String key, final Object value) {
        if (isCacheFull()) {
            System.out.println("Cache full, please wait for the expiry period.");
            return;
        }
        if (Objects.isNull(key)) {
            return;
        }
        cache.put(key, value);
        addToDelayQueue(key, value);
    }

    private void addToDelayQueue(final String key, final Object value) {
        long expiryTime = System.currentTimeMillis() + expiryDurationInMillis;
        CacheItem cacheItem = new CacheItem(key, value, expiryTime);
        cleanerQueue.remove(cacheItem);
        cleanerQueue.put(cacheItem);
    }

    private boolean isCacheFull() {
        return size() == capacity;
    }

    public void remove(final String key) {
        cache.remove(key);
    }

    public Object get(final String key) {
        if (Objects.isNull(key)) {
            return null;
        }
        Object value = cache.get(key);
        if (value != null) {
            addToDelayQueue(key, value);
            return value;
        }
        return null;
    }

    public void clear() {
        cache.clear();
    }

    public long size() {
        return cache.size();
    }
}
