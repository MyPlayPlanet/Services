package net.myplayplanet.services.cache_new;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class CacheObject<V extends Serializable> implements Serializable {
    LocalDate lastModified;
    V value;
}
