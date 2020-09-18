package net.myplayplanet.services.config.provider.resouce;

import net.myplayplanet.services.config.api.IResourceProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class JavaResourceProvider implements IResourceProvider {
    public InputStream getResourceFile(String fileName) throws IOException {
        return new FileInputStream(new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile()
        ));
    }
}
