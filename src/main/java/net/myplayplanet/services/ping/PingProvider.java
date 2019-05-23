package net.myplayplanet.services.ping;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.packetapi.PacketAPI;
import net.myplayplanet.services.connection.ConnectionManager;
import net.myplayplanet.services.logger.Log;
import net.myplayplanet.services.ping.connection.Connection;
import net.myplayplanet.services.ping.connection.ConnectionObject;
import net.myplayplanet.services.ping.events.ConnectionLostEvent;
import net.myplayplanet.services.ping.packets.PingPacket;
import net.myplayplanet.services.ping.packets.PongPacket;
import net.myplayplanet.services.ping.tasks.PingTask;
import net.myplayplanet.services.schedule.ScheduledTaskProvider;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
public class PingProvider {

    @Getter
    private static PingProvider instance;
    private PacketAPI packetAPI;
    private List<Connection> outgoingConnections;
    @Getter(AccessLevel.PRIVATE)
    private List<ConnectionLostEvent> events;
    private String channel;

    public PingProvider(String channel) {
        instance = this;

        this.outgoingConnections = new ArrayList<>();
        this.events = new ArrayList<>();
        this.channel = channel;
        this.packetAPI = new PacketAPI(ConnectionManager.getInstance().getBytePubSubConnection(), ConnectionManager.getInstance().getByteConnection(), this.getChannel());

        try {
            this.getPacketAPI().register(new PingPacket());
            this.getPacketAPI().register(new PongPacket());
        } catch (InstantiationException | IllegalAccessException e) {
            Log.getLog(log).error(e, "Error while registering Incoming Channel!");
        }

        ScheduledTaskProvider.getInstance().register(new PingTask());
    }

    public void registerEvent(ConnectionLostEvent connectionLostEvent){
        if(!(this.getEvents().contains(connectionLostEvent))){
            this.getEvents().add(connectionLostEvent);
        }
    }
     public void unregisterEvent(ConnectionLostEvent connectionLostEvent){
        if(this.getEvents().contains(connectionLostEvent)){
            this.getEvents().remove(connectionLostEvent);
        }
    }

    public void registerOutgoingChannel(String channel, int timeout) {
        Validate.notNull(PacketAPI.getInstance(), "Instance of PacketAPI is null!");
        Validate.isTrue(!isOutgoingChannelConnected(channel), "Channel {channel} is already registered!", channel);
        this.getOutgoingConnections().add(new Connection(channel, UUID.fromString("00000000-0000-0000-0000-000000000000"), UUID.randomUUID(), new Date(), timeout, 0));
        return;
    }

    public void unregisterOutgoingChannel(String channel) {
        Connection connectionToRemove = null;

        for (Connection connection : this.getOutgoingConnections()) {
            if(connection.getChannel().equalsIgnoreCase(channel)){
                connectionToRemove = connection;
            }
        }

        if(connectionToRemove != null){
            this.getOutgoingConnections().remove(connectionToRemove);
        }

        return;
    }

    public boolean isOutgoingChannelConnected(String channel) {
        for (Connection connection : this.getOutgoingConnections()) {
            if (connection.getChannel().equalsIgnoreCase(channel)) {
                return true;
            }
        }
        return false;
    }

    public void updateOutgoingConnection(ConnectionObject connectionObject) {
        for (Connection connection : this.getOutgoingConnections()) {
            if (connection.getChannel().equalsIgnoreCase(connectionObject.getConnection().getChannel())) {
                connection.setPreviousUniqueID(connectionObject.getConnection().getCurrentUniqueID());
                connection.setCurrentUniqueID(UUID.randomUUID());
                connection.setLastContact(new Date());
                break;
            }
        }
    }

    public void callConnectionLostEvent(Connection connection){
        this.getEvents().forEach(connectionLostEvent -> {
            connectionLostEvent.onConnectionLost(connection);
        });
    }

}
