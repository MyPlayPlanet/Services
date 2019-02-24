package net.myplayplanet.services.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionSettings {
    String database;
    String hostname;
    String password;
    Integer port;
    String username;
}