package com.lion.graveyard.network;

import com.lion.graveyard.chronicle.client.ClientChronicle;

public class ClientPayloadHandler {

    public static void handle(ChroniclePayload payload) {
        ClientChronicle.receive(payload.found(), payload.newlyFound());
    }
}
