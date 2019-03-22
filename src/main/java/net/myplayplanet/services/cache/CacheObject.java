package net.myplayplanet.services.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CacheObject<T extends Serializable> implements Serializable {

    private UUID cachedObjectID;
    private byte[] data;

    public T toType() {
        return SerializationUtils.deserialize(getData());
    }
}
