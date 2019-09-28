package net.myplayplanet.services.checker;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class BadWordElement implements Serializable {
    int id;
    String description;
}
