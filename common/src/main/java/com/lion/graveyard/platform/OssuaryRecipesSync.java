package com.lion.graveyard.platform;

import com.lion.graveyard.Graveyard;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record OssuaryRecipesSync(int containerId, List<ItemStack> results) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OssuaryRecipesSync> TYPE = new CustomPacketPayload.Type<>(Graveyard.id("ossuary_recipes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OssuaryRecipesSync> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            OssuaryRecipesSync::containerId,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
            OssuaryRecipesSync::results,
            OssuaryRecipesSync::new
    );

    @Override
    public CustomPacketPayload.Type<OssuaryRecipesSync> type() {
        return TYPE;
    }

    public static void send(ServerPlayer player, int containerId, List<ItemStack> results) {
        player.connection.send(new ClientboundCustomPayloadPacket(new OssuaryRecipesSync(containerId, results)));
    }
}
