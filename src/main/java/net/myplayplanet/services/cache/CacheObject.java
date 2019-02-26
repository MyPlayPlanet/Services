package net.myplayplanet.services.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CacheObject implements Serializable {

    private UUID cachedObjectID;
    private byte[] data;

}
