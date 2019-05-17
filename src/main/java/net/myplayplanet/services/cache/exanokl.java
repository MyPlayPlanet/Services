package net.myplayplanet.services.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.myplayplanet.services.cache.advanced.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class exanokl {
    public static void main(String[] args) {
    }


    private static void list_cache_example() {
        MapCache<Integer, Baum> cache = new ListCache<>("list-cache",
                i -> {
                    //get value from db via key
                    return new Baum("baum", i);
                },
                new AbstractSaveProvider<Integer, Baum>() {
                    @Override
                    public boolean save(Integer key, Baum value) { // Must have
                        //save to db.
                        return false;
                    }

                    @Override
                    public HashMap<Integer, Baum> load() { //Must have
                        //this Method will be used to check for Consistency within the list so no items get Lost
                        return super.load();
                    }

                    @Override
                    public List<Integer> saveAll(HashMap<Integer, Baum> values) { //not necessary.
                        return super.saveAll(values);
                    }
                },
                baum -> {
                    //get key via object
                    return baum.getKey();
                }
        );

        Collection<Baum> values = cache.getList();
    }

    private static void example_mapCache() {
        MapCache<UUID, String> cache = new MapCache<>("example",
                uuid -> {
                    //load single entry from database.
                    return uuid.toString();
                },
                new AbstractSaveProvider<UUID, String>() {
                    @Override
                    public boolean save(UUID key, String value) { //Must have
                        //save single entry to database
                        return true;
                    }

                    @Override
                    public HashMap<UUID, String> load() { //Must have
                        //this Method will be used to check for Consistency within the list so no items get Lost
                        return super.load();
                    }

                    @Override
                    public List<UUID> saveAll(HashMap<UUID, String> values) { //not necessary.
                        //save all objects that are not saved yet at once.
                        return super.saveAll(values);
                    }
                });

        HashMap<UUID, String> map = cache.getMap();
    }


    private static void example_map_cache_collection() {
        HashMap<Integer, HashMap<String, String>> r = null;

        MapCacheCollection<Integer, String, String> mapCacheCollection = new MapCacheCollection<>("example",
                (integer, s) -> {
                    //get value from db.
                    return null;

                },
                new CacheCollectionSaveProvider<Integer, String, String>() {
                    @Override
                    public boolean save(Integer masterKey, String key, String value) { //must have
                        //save single entry so db.
                        return false;
                    }

                    @Override
                    public HashMap<String, String> load(Integer masterKey) { //must have
                        //load every Entry from db. (this Method is really important because if this Method contains nothing the list will contain Nothing.)
                        return null;
                    }

                    @Override
                    public List<String> saveAll(Integer masterKey, HashMap<String, String> values) { //not necessary.
                        //save all to db.
                        return null;
                    }
                }
        );

        MapCache<String, String> mapCache = mapCacheCollection.getMapCache(10);

        Collection<String> list = mapCache.getList();
    }


    private static void example_list_cache_collection() {
        HashMap<Integer, HashMap<Integer, Baum>> r = null;

        ListCacheCollection<Integer, Integer, Baum> listCacheCollection = new ListCacheCollection<>("example",
                (a, b) -> {
                    //get value from db.
                    return null;

                },
                (a, baum) -> {
                    return baum.getKey();
                },
                new CacheCollectionSaveProvider<Integer, Integer, Baum>() {
                    @Override
                    public boolean save(Integer masterKey, Integer key, Baum value) { //must have
                        //save single entry to db.
                        return false;
                    }

                    @Override
                    public HashMap<Integer, Baum> load(Integer masterKey) { //must have
                        //load every Entry from db. (this Method is really important because if this Method contains nothing the list will contain Nothing.)
                        return null;
                    }

                    @Override
                    public List<Integer> saveAll(Integer masterKey, HashMap<Integer, Baum> values) { //not necessary
                        //save all to db.
                        return null;
                    }
                }
        );

        ListCache<Integer, Baum> mapCache = listCacheCollection.getCache(10);

        Collection<Baum> list = mapCache.getList();
    }

}


@AllArgsConstructor
@Data
class Baum implements Serializable {
    String value;
    int key;
}