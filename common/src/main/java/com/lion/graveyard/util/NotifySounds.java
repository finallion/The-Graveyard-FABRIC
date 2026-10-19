package com.lion.graveyard.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/// Plays a sound to one player only.
public final class NotifySounds {
    private NotifySounds() {}

    public static void play(Player player, SoundEvent sound, SoundSource source, float volume, float pitch) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound),
                source,
                serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                volume,
                pitch,
                serverPlayer.level().getRandom().nextLong()
        ));
    }
}
