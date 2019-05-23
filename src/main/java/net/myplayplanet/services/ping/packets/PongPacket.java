package net.myplayplanet.services.ping.packets;

import net.myplayplanet.packetapi.Packet;
import net.myplayplanet.packetapi.interfaces.IPacket;
import net.myplayplanet.services.ping.PingProvider;
import net.myplayplanet.services.ping.connection.ConnectionObject;

@IPacket.Settings(type = "PONG")
public class PongPacket implements IPacket {
    @Override
    public void execute(Packet packet) {
        ConnectionObject connectionObject = Packet.deserializeToType(packet.getContent());

        if (PingProvider.getInstance().isOutgoingChannelConnected(connectionObject.getConnection().getChannel())) {
            PingProvider.getInstance().updateOutgoingConnection(connectionObject);
        }
        System.out.println("------------------------------------------------- PONG ---------------------------------------------");
    }
}
