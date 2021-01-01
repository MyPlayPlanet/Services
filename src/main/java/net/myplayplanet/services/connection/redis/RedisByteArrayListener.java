package net.myplayplanet.services.connection.redis;

import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.Getter;
import net.myplayplanet.packetapi.Packet;
import net.myplayplanet.packetapi.PacketAPI;
import net.myplayplanet.packetapi.handlers.PacketHandler;

import java.nio.charset.Charset;

@Getter
public class RedisByteArrayListener implements RedisPubSubListener<byte[], byte[]> {

    private RedisPubSubService service;

    public RedisByteArrayListener(RedisPubSubService service) {
        this.service = service;
    }

    public void message(byte[] channel, byte[] message) {
        service.execute(channel, message);
    }

    public void message(byte[] pattern, byte[] channel, byte[] message) {

    }

    public void subscribed(byte[] channel, long count) {

    }

    public void psubscribed(byte[] pattern, long count) {

    }

    public void unsubscribed(byte[] channel, long count) {

    }

    public void punsubscribed(byte[] pattern, long count) {

    }
}
