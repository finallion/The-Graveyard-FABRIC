package com.lion.graveyard.network;

import com.lion.graveyard.GraveyardClient;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel SKULL_ENTITY_CHANNEL = NetworkRegistry.newSimpleChannel(GraveyardClient.SKULL_PACKET_ID, () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void registerMessages() {
        SKULL_ENTITY_CHANNEL.registerMessage(0,
                SkullEntitySpawnPacket.class,
                SkullEntitySpawnPacket::encode,
                SkullEntitySpawnPacket::new,
                SkullEntitySpawnPacket::handle);
    }

}
