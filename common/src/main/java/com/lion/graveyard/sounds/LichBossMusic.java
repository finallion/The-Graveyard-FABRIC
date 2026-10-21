package com.lion.graveyard.sounds;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class LichBossMusic extends AbstractSoundInstance implements TickableSoundInstance {
    public LichEntity boss;
    private int ticksExisted = 0;
    private boolean done;
    private final float fullVolume;
    public final SoundEvent soundEvent;

    public LichBossMusic(SoundEvent sound, LichEntity boss, RandomSource random) {
        super(sound, SoundSource.MUSIC, random);
        this.boss = boss;
        this.soundEvent = sound;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.fullVolume = Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").bossMusicVolume();
        this.volume = this.fullVolume;
        this.x = boss.getX();
        this.y = boss.getY();
        this.z = boss.getZ();
    }

    @Override
    public boolean canPlaySound() {
        return BossMusicPlayer.music == this;
    }

    @Override
    public boolean isStopped() {
        return this.done;
    }

    protected final void setDone() {
        this.done = true;
    }

    private boolean isAudibleBoss() {
        Minecraft minecraft = Minecraft.getInstance();
        return boss != null
                && boss.isAlive()
                && !boss.isSilent()
                && boss.level() == minecraft.level
                && boss.canPlayerHearMusic(minecraft.player);
    }

    @Override
    public void tick() {
        if (!isAudibleBoss()) {
            boss = null;

            if (volume >= 0) {
                volume -= 0.03F;
            } else {
                BossMusicPlayer.music = null;
                setDone();
            }
        } else if (volume < fullVolume) {
            volume = Math.min(fullVolume, volume + 0.03F);
        }

        if (ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
        }

        ticksExisted++;
    }
}
