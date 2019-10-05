package net.myplayplanet.services.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class CacheObject<V extends Serializable> implements Serializable {
    long refreshOn;
    V value;
}
