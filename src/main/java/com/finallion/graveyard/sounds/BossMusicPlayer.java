package com.finallion.graveyard.sounds;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.entities.LichEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;


public class BossMusicPlayer {
    public static LichBossMusic music;

    public static void playBossMusic(LichEntity entity) {
        if (!TheGraveyard.config.booleanEntries.get("enableBossMusic")) return;
        SoundEvent soundEvent = entity.getBossMusic();

        if (soundEvent != null && entity.isAlive()) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (music != null) {
                if (MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MUSIC) <= 0) {
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
