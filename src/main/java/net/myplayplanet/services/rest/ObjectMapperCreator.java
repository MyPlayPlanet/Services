package net.myplayplanet.services.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperCreator {
    public static final ObjectMapper INSTANCE;

    static {
        INSTANCE = new ObjectMapper();
        INSTANCE.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        INSTANCE.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        INSTANCE.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        INSTANCE.registerModule(javaTimeModule);
    }
}
