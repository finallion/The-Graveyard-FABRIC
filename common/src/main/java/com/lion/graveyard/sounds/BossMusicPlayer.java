package com.lion.graveyard.sounds;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.Player;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvent;


public class BossMusicPlayer {
    public static LichBossMusic music;

    public static void playBossMusic(LichEntity entity) {
        if (!Graveyard.getConfig().booleanEntries.get("enableBossMusic")) return;
        SoundEvent soundEvent = entity.getBossMusic();

        if (soundEvent != null && entity.isAlive()) {
            Player player = MinecraftClient.getInstance().player;
            if (music != null) {
                if (MinecraftClient.getInstance().options.getSoundVolume(SoundSource.MUSIC) <= 0) {
                    music = null;
                } else if (music.boss == entity && !entity.canPlayerHearMusic(player)) {
                    music.boss = null;
                } else if (music.boss == null && music.soundEvent == soundEvent) {
                    music.boss = entity;
                }
            } else {
                if (entity.canPlayerHearMusic(player)) {
                    music = new LichBossMusic(soundEvent, entity, entity.getRandom());
                } else {
                    music = null;
                }
            }

            if (music != null && !MinecraftClient.getInstance().getSoundManager().isPlaying(music)) {
                MinecraftClient.getInstance().getSoundManager().play(music);
            }
        }
    }

    public static void stopBossMusic(LichEntity entity) {
        if (music != null && music.boss == entity) {
            music.boss = null;
        }
    }
}
