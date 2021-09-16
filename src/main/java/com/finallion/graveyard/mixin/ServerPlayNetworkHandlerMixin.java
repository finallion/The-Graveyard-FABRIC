package com.finallion.graveyard.mixin;

import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blocks.GravestoneBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "method_31282(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V", at = @At(value = "HEAD"), cancellable = true)
    private void signUpdate(UpdateSignC2SPacket packet, List<String> signText, CallbackInfo info) {
        this.player.updateLastActionTime();
        ServerWorld serverWorld = this.player.getServerWorld();
        BlockPos blockPos = packet.getPos();
        if (serverWorld.isChunkLoaded(blockPos)) {
            BlockState blockState = serverWorld.getBlockState(blockPos);
            Block block = serverWorld.getBlockState(blockPos).getBlock();


            if (block instanceof GravestoneBlock) {
                GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity) serverWorld.getBlockEntity(blockPos);

                if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
                    LOGGER.warn((String)"Player {} just tried to change non-editable sign", (Object)this.player.getName().getString());
                    return;
                }


                for(int i = 0; i < signText.size(); ++i) {
                    signBlockEntity.setTextOnRow(i, new LiteralText((String)signText.get(i)));
                }

                signBlockEntity.markDirty();
                serverWorld.updateListeners(blockPos, blockState, blockState, 3);
                info.cancel();
            }
        }
    }


}
