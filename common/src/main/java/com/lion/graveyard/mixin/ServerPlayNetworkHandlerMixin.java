package main.java.com.lion.graveyard.mixin;


import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V", at = @At(value = "HEAD"), cancellable = true)
    private void signUpdate(UpdateSignC2SPacket packet, List<FilteredMessage> signText, CallbackInfo info) {
        this.player.updateLastActionTime();
        ServerWorld serverWorld = this.player.getServerWorld();
        BlockPos blockPos = packet.getPos();

        if (serverWorld.isChunkLoaded(blockPos)) {
            BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
            if (!(blockEntity instanceof GravestoneBlockEntity)) {
                return;
            }

            GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity)blockEntity;
            signBlockEntity.tryChangeText(this.player, signText);
        }



    }


}


