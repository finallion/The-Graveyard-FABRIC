package com.finallion.graveyard.mixin;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blocks.GravestoneBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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
    private void signUpdate(UpdateSignC2SPacket packet, List<FilteredMessage<String>> signText, CallbackInfo info) {
        this.player.updateLastActionTime();
        ServerWorld serverWorld = this.player.getWorld();
        BlockPos blockPos = packet.getPos();
        if (serverWorld.isChunkLoaded(blockPos)) {
            BlockState blockState = serverWorld.getBlockState(blockPos);
            Block block = serverWorld.getBlockState(blockPos).getBlock();


            if (block instanceof GravestoneBlock) {
                GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity) serverWorld.getBlockEntity(blockPos);

                if (!signBlockEntity.isEditable() || !this.player.getUuid().equals(signBlockEntity.getEditor())) {
                    TheGraveyard.LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getName().getString());
                    return;
                }

                for (int i = 0; i < signText.size(); ++i) {
                    FilteredMessage<Text> filteredMessage = signText.get(i).map(Text::literal);
                    if (this.player.shouldFilterText()) {
                        signBlockEntity.setTextOnRow(i, (Text)filteredMessage.filteredOrElse(ScreenTexts.EMPTY));
                    } else {
                        signBlockEntity.setTextOnRow(i, (Text)filteredMessage.raw(), (Text)filteredMessage.filteredOrElse(ScreenTexts.EMPTY));
                    }
                }

                signBlockEntity.markDirty();
                serverWorld.updateListeners(blockPos, blockState, blockState, 3);
                info.cancel();
            }
        }
    }


}


