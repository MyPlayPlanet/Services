package net.myplayplanet.services.config.provider.resouce;

import net.myplayplanet.services.config.api.IResourceProvider;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

public class SpringResourceProvider implements IResourceProvider {
    public InputStream getResourceFile(String fileName) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        System.out.println("trying to find resource: " + fileName + " - " + classPathResource);

        if (classPathResource.exists()) {
            return classPathResource.getInputStream();
        } else {
            return null;
        }
    }
}
