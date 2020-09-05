package net.myplayplanet.services.connection.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IDataProvider {
    <T extends Serializable> CompletableFuture<T> get(String key, String field);

    <T extends Serializable> CompletableFuture<Map<String, T>> getAll(String key);

    <T extends Serializable> CompletableFuture<List<T>> getAllValues(String key);

    <T extends Serializable> CompletableFuture<Boolean> set(String key, String field, T data);

    KeyDataProvider withKey(String key);
}
