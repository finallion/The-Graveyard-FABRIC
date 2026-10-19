package com.lion.graveyard.mixin;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.gui.GravestoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    private ClientLevel level;

    @Inject(method = "handleOpenSignEditor", at = @At(value = "HEAD"), cancellable = true)
    public void openSignEditor(ClientboundOpenSignEditorPacket packet, CallbackInfo info) {
        PacketUtils.ensureRunningOnSameThread(packet, (ClientGamePacketListener) this, Minecraft.getInstance().packetProcessor());
        BlockPos blockPos = packet.getPos();

        BlockEntity foundEntity = this.level.getBlockEntity(blockPos);

        if (foundEntity instanceof GravestoneBlockEntity) {
            BlockState blockState = this.level.getBlockState(blockPos);
            GravestoneBlockEntity signBlockEntity2 = new GravestoneBlockEntity(blockPos, blockState);
            signBlockEntity2.setLevel(this.level);
            Minecraft.getInstance().setScreen(new GravestoneScreen((GravestoneBlockEntity) foundEntity,  false));
            info.cancel();
        }
    }
}
