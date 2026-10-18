package com.lion.graveyard.network;

import com.lion.graveyard.platform.ChronicleSync;
import com.lion.graveyard.platform.OssuaryRecipesSync;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .playToClient(
                        ChronicleSync.TYPE,
                        ChronicleSync.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> ClientPayloadHandler.handle(payload))
                )
                .playToClient(
                        OssuaryRecipesSync.TYPE,
                        OssuaryRecipesSync.STREAM_CODEC,
                        (payload, context) -> context.enqueueWork(() -> ClientPayloadHandler.handle(payload))
                );
    }
}
