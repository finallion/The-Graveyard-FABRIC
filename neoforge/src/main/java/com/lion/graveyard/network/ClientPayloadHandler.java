package com.lion.graveyard.network;

import com.lion.graveyard.chronicle.client.ClientChronicle;
import com.lion.graveyard.platform.ChronicleSync;

public class ClientPayloadHandler {

    public static void handle(ChronicleSync payload) {
        ClientChronicle.receive(payload.found(), payload.newlyFound());
    }
}
