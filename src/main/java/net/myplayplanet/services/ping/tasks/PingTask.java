package net.myplayplanet.services.ping.tasks;

import net.myplayplanet.packetapi.Packet;
import net.myplayplanet.packetapi.PacketAPI;
import net.myplayplanet.services.ping.InternalPacketType;
import net.myplayplanet.services.ping.PingProvider;
import net.myplayplanet.services.ping.connection.Connection;
import net.myplayplanet.services.ping.connection.ConnectionObject;
import net.myplayplanet.services.schedule.IScheduledTask;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class PingTask implements IScheduledTask {

    @Override
    public TimeUnit getIntervalUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public int getInterval() {
        return 1;
    }

    @Override
    public void runLater() {
        if(PacketAPI.getInstance() == null){
            return;
        }

        PingProvider.getInstance().getOutgoingConnections().forEach(connection -> {
            ForkJoinPool.commonPool().execute(() -> {
                Connection conn = PingProvider.getInstance().getOutgoingConnections().stream().filter(c -> c.getChannel().equalsIgnoreCase(connection.getChannel())).findFirst().orElse(null);

                long timeSinceLastContact = (new Date().getTime() - conn.getLastContact().getTime()) / 1000;

                if(timeSinceLastContact > 1){
                    conn.setLostPackets(conn.getLostPackets() + 1);
                }

                if(timeSinceLastContact  >= conn.getTimeout()){
                    PingProvider.getInstance().callConnectionLostEvent(connection);
                    PingProvider.getInstance().getOutgoingConnections().remove(conn);
                    return;
                }

                Packet pingPacket = new Packet(InternalPacketType.PING, new ConnectionObject(PingProvider.getInstance().getChannel(), connection));
                pingPacket.send(connection.getChannel());
            });
        });
    }
}
