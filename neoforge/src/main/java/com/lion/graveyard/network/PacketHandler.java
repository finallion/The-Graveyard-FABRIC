package com.lion.graveyard.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
                .playToClient(ChroniclePayload.TYPE, ChroniclePayload.CODEC,
                        (payload, context) -> context.enqueueWork(() -> ClientPayloadHandler.handle(payload)));
    }
}
