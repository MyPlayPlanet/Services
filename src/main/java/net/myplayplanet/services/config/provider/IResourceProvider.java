package net.myplayplanet.services.config.provider;

import net.myplayplanet.services.config.provider.resouce.JavaResourceProvider;
import net.myplayplanet.services.config.provider.resouce.SpringResourceProvider;

import java.io.IOException;
import java.io.InputStream;

public interface IResourceProvider {
    InputStream getResourceFile(String fileName) throws IOException;

    static IResourceProvider getResourceProvider() {
        boolean exists;
        try {
            Class.forName("org.springframework.core.io.ClassPathResource");
            exists = true;
        } catch (ClassNotFoundException e) {
            exists = false;
        }

        if (exists) {
            return new SpringResourceProvider();
        } else {
            return new JavaResourceProvider();
        }
    }
}
