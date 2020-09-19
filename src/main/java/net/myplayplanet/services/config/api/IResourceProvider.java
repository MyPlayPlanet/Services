package net.myplayplanet.services.config.api;

import net.myplayplanet.services.config.provider.resouce.JavaResourceProvider;
import net.myplayplanet.services.config.provider.resouce.MockResourceProvider;
import net.myplayplanet.services.config.provider.resouce.SpringResourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface IResourceProvider {
    static IResourceProvider getResourceProvider() {
        return getResourceProvider(null);
    }

    static IResourceProvider getResourceProvider(Properties mock) {
        if (mock != null) {
            return new MockResourceProvider(mock);
        }

        try {
            Class.forName("org.springframework.core.io.ClassPathResource");
            return new SpringResourceProvider();
        } catch (ClassNotFoundException e) {
            return new JavaResourceProvider();
        }
    }

    InputStream getResourceFile(String fileName) throws IOException;
}
