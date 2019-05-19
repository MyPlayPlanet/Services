package net.myplayplanet.services.ping.packets;

import net.myplayplanet.packetapi.Packet;
import net.myplayplanet.packetapi.interfaces.IPacket;
import net.myplayplanet.services.ping.InternalPacketType;
import net.myplayplanet.services.ping.connection.ConnectionObject;

@IPacket.Settings(type = "PING")
public class PingPacket implements IPacket {
    @Override
    public void execute(Packet packet) {
        ConnectionObject connectionObject = Packet.deserializeToType(packet.getContent());

        Packet pong = new Packet(InternalPacketType.PONG, connectionObject);
        pong.send(connectionObject.getSenderChannel());

        System.out.println("------------------------------------------------- PING ---------------------------------------------");
    }
}
