package com.lion.graveyard.network;

import com.lion.graveyard.Graveyard;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import com.lion.graveyard.platform.ChronicleSync;
import net.minecraftforge.network.simple.SimpleChannel;


public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel SKULL_ENTITY_CHANNEL = NetworkRegistry.newSimpleChannel(Graveyard.SKULL_PACKET_ID, () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static final SimpleChannel CHRONICLE_CHANNEL = NetworkRegistry.newSimpleChannel(ChronicleSync.CHANNEL, () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void registerMessages() {
        CHRONICLE_CHANNEL.registerMessage(0, ChroniclePacket.class, ChroniclePacket::encode, ChroniclePacket::decode, ChroniclePacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        SKULL_ENTITY_CHANNEL.registerMessage(0,
                SkullEntitySpawnPacket.class,
                SkullEntitySpawnPacket::encode,
                SkullEntitySpawnPacket::new,
                SkullEntitySpawnPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

}
