package net.myplayplanet.services.cache_new;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class CacheObject<V extends Serializable> implements Serializable {
    long lastModified;
    V value;
}
