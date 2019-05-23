package net.myplayplanet.services.ping.connection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ConnectionObject implements Serializable {

    private String senderChannel;
    private Connection connection;

}
