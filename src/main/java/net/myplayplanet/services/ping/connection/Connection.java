package net.myplayplanet.services.ping.connection;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Connection implements Serializable {

    private String channel;

    private UUID previousUniqueID;
    private UUID currentUniqueID;

    private Date lastContact;
    private int timeout;
    private int lostPackets;

}
