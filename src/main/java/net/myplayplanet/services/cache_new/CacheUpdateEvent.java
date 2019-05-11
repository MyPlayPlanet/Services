package net.myplayplanet.services.cache_new;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CacheUpdateEvent<K, V> {
    boolean isCancelled = false;
    final K key;
    final V value;
}
