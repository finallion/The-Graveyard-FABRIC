package com.lion.graveyard.sounds;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;


public class BossMusicPlayer {
    public static LichBossMusic music;

    public static void playBossMusic(LichEntity entity) {
        if (!Graveyard.getConfig().booleanEntries.get("enableBossMusic")) return;
        SoundEvent soundEvent = entity.getBossMusic();

        if (soundEvent != null && entity.isAlive()) {
            Player player = Minecraft.getInstance().player;
            if (music != null) {
                if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) <= 0) {
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

            if (music != null && !Minecraft.getInstance().getSoundManager().isActive(music)) {
                Minecraft.getInstance().getSoundManager().play(music);
            }
        }
    }

    public static void stopBossMusic(LichEntity entity) {
        if (music != null && music.boss == entity) {
            music.boss = null;
        }
    }
}
