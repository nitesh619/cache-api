package com.sap.cache;

public class EntryPoint {

    public static void main(String[] args) throws InterruptedException {
        INotification iNotification = value -> {
            System.out.println("Evicted " + value + " from the cache.");
        };
        ConcurrentExpiryCache cache =
                new ConcurrentExpiryCache(5, 5000, iNotification);
        cache.add("1", "batman");
        Thread.sleep(1000);
        cache.add("2", "ironman");
        Thread.sleep(1000);
        cache.add("3", "captain");
        Thread.sleep(1000);
        cache.add("4", "abc");
        Thread.sleep(1000);
        cache.add("5", "flash");
        Thread.sleep(1000);

        System.out.println(cache.getCache());

        new Thread(() -> {
            while (true) {

                System.out.println(cache.getCache());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cache.get("3");
            }
        }).start();

    }

}
