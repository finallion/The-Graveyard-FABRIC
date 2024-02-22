package com.lion.graveyard.mixin;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.lion.graveyard.gui.GravestoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
        Minecraft client = ((ClientCommonNetworkHandlerAccessor) this).getMinecraft();
        PacketUtils.ensureRunningOnSameThread(packet, (ClientGamePacketListener) this, client);
        BlockPos blockPos = packet.getPos();

        BlockEntity var4 = this.level.getBlockEntity(blockPos);
        if (var4 instanceof GravestoneBlockEntity) {
            BlockState blockState = this.level.getBlockState(blockPos);
            GravestoneBlockEntity signBlockEntity2 = new GravestoneBlockEntity(blockPos, blockState);
            signBlockEntity2.setLevel(this.level);
            Minecraft.getInstance().setScreen(new GravestoneScreen((GravestoneBlockEntity) var4,  false));
            info.cancel();
        }
    }

    @Inject(method = "handleBlockEntityData", at = @At(value = "HEAD"), cancellable = true)
    public void onEntityUpdate(ClientboundBlockEntityDataPacket packet, CallbackInfo info) {
        Minecraft client = ((ClientCommonNetworkHandlerAccessor) this).getMinecraft();
        PacketUtils.ensureRunningOnSameThread(packet, (ClientGamePacketListener) this, client);
        BlockPos blockPos = packet.getPos();
        BlockEntity blockEntity = this.level.getBlockEntity(blockPos);
        if (blockEntity instanceof GravestoneBlockEntity) {
            CompoundTag nbtCompound = packet.getTag();
            if (nbtCompound != null) {
                blockEntity.load(nbtCompound);
            }
            info.cancel();
        }
    }
}
