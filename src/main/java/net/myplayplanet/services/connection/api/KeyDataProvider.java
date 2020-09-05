package net.myplayplanet.services.connection.api;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public class KeyDataProvider {
    private final String key;
    private final IDataProvider iDataProvider;

    public KeyDataProvider(String key, IDataProvider iDataProvider) {
        this.key = key;
        this.iDataProvider = iDataProvider;
    }

    public <T extends Serializable> CompletableFuture<T> get(String field) {
        return iDataProvider.get(key, field);
    }

    public <T extends Serializable> CompletableFuture<Map<String, T>> getAll() {
        return iDataProvider.getAll(key);
    }

    public <T extends Serializable> CompletableFuture<List<T>> getAllValues() {
        return iDataProvider.getAllValues(key);
    }

    public <T extends Serializable> CompletableFuture<Boolean> set(String field, T data) {
        return iDataProvider.set(key, field, data);
    }
}
