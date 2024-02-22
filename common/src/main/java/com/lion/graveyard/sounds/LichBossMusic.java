package com.lion.graveyard.sounds;

import com.lion.graveyard.entities.LichEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;


public class LichBossMusic extends AbstractTickableSoundInstance {
    public LichEntity boss;
    private int ticksExisted = 0;
    private boolean done;
    public final SoundEvent soundEvent;

    public LichBossMusic(SoundEvent sound, LichEntity boss, RandomSource random) {
        super(sound, SoundSource.MUSIC, random);
        this.boss = boss;
        this.soundEvent = sound;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.volume = 4.5F;
        this.x = boss.getX();
        this.y = boss.getY();
        this.z = boss.getZ();
    }

    public boolean canPlay() {
        return BossMusicPlayer.music == this;
    }

    public boolean isDone() {
        return this.done;
    }

    protected final void setDone() {
        this.done = true;
    }

    public void tick() {
        if (boss == null || !boss.isAlive() || boss.isSilent()) {
            boss = null;
            if (volume >= 0) {
                volume -= 0.03F;
            } else {
                BossMusicPlayer.music = null;
                setDone();
            }
        }

        if (ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }
        ticksExisted++;
    }
}
