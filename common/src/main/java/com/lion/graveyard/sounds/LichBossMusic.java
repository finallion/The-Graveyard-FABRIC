package main.java.com.lion.graveyard.sounds;

import com.finallion.graveyard.entities.LichEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;


public class LichBossMusic extends AbstractSoundInstance implements TickableSoundInstance {
    public LichEntity boss;
    private int ticksExisted = 0;
    private boolean done;
    public final SoundEvent soundEvent;

    public LichBossMusic(SoundEvent sound, LichEntity boss, Random random) {
        super(sound, SoundCategory.MUSIC, random);
        this.boss = boss;
        this.soundEvent = sound;
        this.attenuationType = AttenuationType.NONE;
        this.repeat = true;
        this.repeatDelay = 0;
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
            MinecraftClient.getInstance().getMusicTracker().stop();
        }
        ticksExisted++;
    }
}
