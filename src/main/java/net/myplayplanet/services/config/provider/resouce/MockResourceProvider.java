package net.myplayplanet.services.config.provider.resouce;

import net.myplayplanet.services.config.api.IResourceProvider;
import org.apache.commons.lang3.Validate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MockResourceProvider implements IResourceProvider {
    Properties properties;

    public MockResourceProvider(Properties properties) {
        this.properties = properties;
        Validate.isTrue(this.properties.containsKey("mpp.basic.debug"));
        Validate.isTrue(this.properties.containsKey("mpp.basic.config-path"));
    }

    @Override
    public InputStream getResourceFile(String fileName) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        this.properties.store(os, "comments");

        return new ByteArrayInputStream(os.toByteArray());
    }
}
