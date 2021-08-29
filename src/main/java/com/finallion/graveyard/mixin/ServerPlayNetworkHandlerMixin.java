package com.finallion.graveyard.mixin;

import com.finallion.graveyard.blocks.GravestoneBlock;
import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.thread.ThreadExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    public ClientConnection connection;


    /*
    @Inject(method = "(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;)V", at = @At(value = "HEAD"), cancellable = true)
    public void signUpdate(UpdateSignC2SPacket packet, CallbackInfo info) {
        List<String> list = (List)Stream.of(packet.getText()).map(Formatting::strip).collect(Collectors.toList());
        this.filterTexts(list, (listx) -> {
            this.signUpdate(packet, listx, info);
        });
    }

     */



    @Inject(method = "onSignUpdate(Lnet/minecraft/network/packet/c2s/play/UpdateSignC2SPacket;Ljava/util/List;)V", at = @At(value = "HEAD"), cancellable = true)
    private void signUpdate(UpdateSignC2SPacket packet, List<TextStream.Message> signText, CallbackInfo info) {
        this.player.updateLastActionTime();
        ServerWorld serverWorld = this.player.getServerWorld();
        BlockPos blockPos = packet.getPos();
        if (serverWorld.isChunkLoaded(blockPos)) {
            BlockState blockState = serverWorld.getBlockState(blockPos);
            Block block = serverWorld.getBlockState(blockPos).getBlock();


            if (block instanceof GravestoneBlock) {
                GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity) serverWorld.getBlockEntity(blockPos);

                if (!signBlockEntity.isEditable() || !this.player.getUuid().equals(signBlockEntity.getEditor())) {
                    LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getName().getString());
                    return;
                }

                for (int i = 0; i < signText.size(); ++i) {
                    TextStream.Message message = (TextStream.Message)signText.get(i);
                    if (this.player.shouldFilterText()) {
                        signBlockEntity.setTextOnRow(i, new LiteralText(message.getFiltered()));
                    } else {
                        signBlockEntity.setTextOnRow(i, new LiteralText(message.getRaw()), new LiteralText(message.getFiltered()));
                    }
                }

                signBlockEntity.markDirty();
                serverWorld.updateListeners(blockPos, blockState, blockState, 3);
                info.cancel();
            }
        }
    }


    /*
    private <T, R> void filterText(T text, Consumer<R> consumer, BiFunction<TextStream, T, CompletableFuture<R>> backingFilterer) {
        ThreadExecutor<?> threadExecutor = this.player.getServerWorld().getServer();
        Consumer<R> consumer2 = (object) -> {
            if (this.connection.isOpen()) {
                consumer.accept(object);
            } else {
                LOGGER.debug("Ignoring packet due to disconnection");
            }

        };
        ((CompletableFuture)backingFilterer.apply(this.player.getTextStream(), text)).thenAcceptAsync(consumer2, threadExecutor);
    }

    private void filterText(String text, Consumer<TextStream.Message> consumer) {
        this.filterText(text, consumer, TextStream::filterText);
    }

    private void filterTexts(List<String> texts, Consumer<List<TextStream.Message>> consumer) {
        this.filterText(texts, consumer, TextStream::filterTexts);
    }

     */


}
