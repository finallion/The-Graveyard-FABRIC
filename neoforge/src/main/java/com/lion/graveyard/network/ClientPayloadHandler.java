package com.lion.graveyard.network;

import com.lion.graveyard.chronicle.client.ClientChronicle;
import com.lion.graveyard.gui.OssuaryScreen;
import com.lion.graveyard.platform.ChronicleSync;
import com.lion.graveyard.platform.OssuaryRecipesSync;

public class ClientPayloadHandler {

    public static void handle(ChronicleSync payload) {
        ClientChronicle.receive(payload.found(), payload.newlyFound());
    }

    public static void handle(OssuaryRecipesSync payload) {
        OssuaryScreen.receive(payload);
    }
}
