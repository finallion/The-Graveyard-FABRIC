package com.lion.graveyard.mixin;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "updateSignText", at = @At(value = "HEAD"), cancellable = true)
    private void signUpdate(ServerboundSignUpdatePacket packet, List<FilteredText> signText, CallbackInfo info) {
        this.player.resetLastActionTime();
        ServerLevel serverWorld = (ServerLevel) this.player.level();
        BlockPos blockPos = packet.getPos();

        if (serverWorld.hasChunkAt(blockPos)) {
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            if (!(blockEntity instanceof GravestoneBlockEntity)) {
                return;
            }

            GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity)blockEntity;
            signBlockEntity.updateSignText(this.player, signText);
        }
    }
}


