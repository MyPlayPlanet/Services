package net.myplayplanet.services.config.provider.resouce;

import net.myplayplanet.services.config.api.IResourceProvider;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SpringResourceProvider implements IResourceProvider {
    public InputStream getResourceFile(String fileName) throws IOException, FileNotFoundException {
        ClassPathResource classPathResource = new ClassPathResource(fileName);
        System.out.println("trying to find resource: " + fileName + " - " + classPathResource);

        if (classPathResource.exists()) {
            return classPathResource.getInputStream();
        } else {
            throw new FileNotFoundException("file " + fileName + " not found.");
        }
    }
}
