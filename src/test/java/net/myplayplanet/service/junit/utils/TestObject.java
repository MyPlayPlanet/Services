package net.myplayplanet.service.junit.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@Data
public class TestObject implements Serializable {
    String string;
    UUID uuid;
}
