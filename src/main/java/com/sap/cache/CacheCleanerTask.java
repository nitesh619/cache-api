package com.sap.cache;

import java.util.Map;
import java.util.concurrent.DelayQueue;

public class CacheCleanerTask implements Runnable {

    private final DelayQueue<CacheItem> delayQueue;
    private final Map<String, Object> cache;

    public CacheCleanerTask(final Map<String, Object> cache,
            final DelayQueue<CacheItem> delayQueue) {
        this.delayQueue = delayQueue;
        this.cache = cache;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                CacheItem delayedCacheItem = delayQueue.take();
                cache.remove(delayedCacheItem.getKey(), delayedCacheItem.getValue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
