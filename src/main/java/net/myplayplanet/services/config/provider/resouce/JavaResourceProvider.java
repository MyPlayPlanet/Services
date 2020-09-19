package net.myplayplanet.services.config.provider.resouce;

import net.myplayplanet.services.config.api.IResourceProvider;

import java.io.*;
import java.net.URL;

public class JavaResourceProvider implements IResourceProvider {
    public InputStream getResourceFile(String fileName) throws IOException, FileNotFoundException {
        System.out.println("trying to find resource file: " + fileName);
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource == null) {
            throw new FileNotFoundException("file " + fileName + " not found.");
        }
        return new FileInputStream(new File(resource.getFile()));
    }
}
