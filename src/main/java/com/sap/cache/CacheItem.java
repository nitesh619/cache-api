package com.sap.cache;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CacheItem implements Delayed {

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final long expiryTime;
    private String key;
    private Object value;

    public long getDelay(final TimeUnit unit) {
        return unit.convert(expiryTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    public int compareTo(final Delayed o) {
        return Long.valueOf(expiryTime - ((CacheItem) o).expiryTime).intValue();
    }
}
