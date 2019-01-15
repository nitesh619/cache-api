package com.sap.cache;

public interface ICache {

    void add(String key, Object value);

    void remove(String key);

    Object get(String key);

    void clear();

    long size();
}
