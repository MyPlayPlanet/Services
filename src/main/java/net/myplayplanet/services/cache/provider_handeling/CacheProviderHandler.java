package net.myplayplanet.services.cache.provider_handeling;

import net.myplayplanet.services.cache.Cache;
import net.myplayplanet.services.cache.provider_handeling.providers.MockProvider;
import net.myplayplanet.services.cache.provider_handeling.providers.RedisProvider;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

public class CacheProviderHandler {
    private static CacheProviderHandler instance;
    private static boolean debug = false;

    public static CacheProviderHandler getInstance(){
        if (instance == null) {
            instance = new CacheProviderHandler();
            //todo put the register things somewhere good
            instance.register(MockProvider.class);
            instance.register(RedisProvider.class);
        }
        return instance;
    }

    private List<Class<? extends AbstractCacheProvider>> providerList;

    public CacheProviderHandler() {
        providerList = new ArrayList<>();
        instance = this;
    }

    public void register(Class<? extends AbstractCacheProvider> provider) {
        providerList.add(provider);
    }


    public <K extends Serializable, V extends Serializable> AbstractCacheProvider<K, V> getProvider(Cache<K, V> current) {

        Class<? extends AbstractCacheProvider> abstractCacheProvider = (debug)
                ? providerList.stream().filter(aClass -> aClass.isAssignableFrom(DebugProvider.class)).findFirst().orElse(null)
                : providerList.stream().filter(aClass -> !aClass.isAssignableFrom(DebugProvider.class)).findFirst().orElse(null);

        if (abstractCacheProvider == null) {
            return null;
        }

        try {
            Constructor<? extends AbstractCacheProvider> constructor = abstractCacheProvider.getConstructor(Cache.class);
            return constructor.newInstance(current);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void example() {
        new CacheProviderHandler();



    }

}
