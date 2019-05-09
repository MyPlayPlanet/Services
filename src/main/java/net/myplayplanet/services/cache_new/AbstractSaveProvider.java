package net.myplayplanet.services.cache_new;

import lombok.AccessLevel;
import lombok.Getter;
import net.myplayplanet.services.schedule.IScheduledTask;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @param <K> the key
 * @param <V> the value
 */
public abstract class AbstractSaveProvider<K, V> implements IScheduledTask {

    @Getter(AccessLevel.PACKAGE)
    private HashMap<K, V> savableEntries;

    public AbstractSaveProvider() {
        savableEntries = new HashMap<>();
    }

    public TimeUnit getIntervalUnit() {
        return TimeUnit.MINUTES;
    }
    public int getInterval() {
        return 60;
    }

    /**
     * @return true if successful, false if not
     */
    public abstract boolean save(K key, V value);

    /**
     * @return a list of all the objects that should be loaded while initializing.
     */
    public HashMap<K, V> load() {
        return new HashMap<>();
    }

    /**
     * @param values list of all values that should be saved.
     * @return all the values that where successfully saved.
     */
    public List<K> saveAll(List<AbstractMap.SimpleEntry<K, V>> values) {
        List<K> removedSuccessfully = new ArrayList<>();

        for (AbstractMap.SimpleEntry<K, V> value : values) {
            if (save(value.getKey(), value.getValue())) {
                removedSuccessfully.add(value.getKey());
            }
        }
        return removedSuccessfully;
    }

    /**
     * this will be executed according to {@link #getInterval()} and {@link #getIntervalUnit()}.
     */
    @Override
    public void runLater() {
        saveAll(savableEntries);
    }

    /**
     * @return the object that was removed.
     */
    public V removeValue(K key) {
        return savableEntries.remove(key);
    }

    /**
     * @param values list of all values that should be saved.
     * @return all the values that where successfully saved.
     */
    public List<K> saveAll(HashMap<K, V> values) {
        List<K> removedSuccessfully = new ArrayList<>();

        for (K k : values.keySet()) {
            if (save(k, values.get(k))) {
                removedSuccessfully.add(k);
            }
        }
        return removedSuccessfully;
    }
}