package net.myplayplanet.services.cache.provider_handeling;

import lombok.Getter;
import net.myplayplanet.services.cache.provider_handeling.providers.LocalCacheProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CacheProviderHandler {
    @Getter
    private static CacheProviderHandler instance;

    private List<Class<? extends ICacheProvider>> providerList;

    public CacheProviderHandler() {
        providerList = new ArrayList<>();
        instance = this;
    }

    public void register(Class<? extends ICacheProvider> provider) {
        providerList.add(provider);
    }

    public List<ICacheProvider> getCacheProviders() {
        List<>
        for (Class<? extends ICacheProvider> tClass : providerList) {
            try {
                ICacheProvider provider = tClass.getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    private static void example() {
        new CacheProviderHandler();


        CacheProviderHandler.getInstance().register(LocalCacheProvider.class);

    }

}
