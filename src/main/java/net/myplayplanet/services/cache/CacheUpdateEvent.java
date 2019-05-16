package net.myplayplanet.services.cache;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CacheUpdateEvent<K, V> {
    boolean isCancelled = false;
    final K key;
    final V value;
}
