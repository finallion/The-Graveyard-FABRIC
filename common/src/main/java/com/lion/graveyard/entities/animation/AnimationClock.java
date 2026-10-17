package com.lion.graveyard.entities.animation;

import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.Map;
import java.util.WeakHashMap;

/// Reports keyframe 0 of each animation start and each loop restart.
public final class AnimationClock {
    private static final int RENDER_GRACE_TICKS = 2;
    private static final Map<Entity, Integer> LAST_FRAME = new WeakHashMap<>();
    @Nullable
    private static Listener listener;

    private AnimationClock() {
    }

    public interface Listener {
        void keyframeZero(Entity entity, String animation);

        void playTracked(Entity entity, @Nullable SoundEvent sound);

        void playDetached(Entity entity, @Nullable SoundEvent sound);
    }

    public static void setListener(@Nullable Listener value) {
        listener = value;
    }

    public static boolean isRendered(Entity entity) {
        Integer frame = LAST_FRAME.get(entity);
        return frame != null && entity.age - frame <= RENDER_GRACE_TICKS;
    }

    public static void playTracked(Entity entity, @Nullable SoundEvent sound) {
        if (listener != null) {
            listener.playTracked(entity, sound);
        }
    }

    public static void playDetached(Entity entity, @Nullable SoundEvent sound) {
        if (listener != null && sound != null) {
            listener.playDetached(entity, sound);
        }
    }

    public static <T extends GeoAnimatable> AnimationController<T> controller(T animatable, String name, int transitionTicks, AnimationController.AnimationStateHandler<T> handler) {
        return new ClockController<>(animatable, name, transitionTicks, handler);
    }

    private static final class ClockController<T extends GeoAnimatable> extends AnimationController<T> {
        ClockController(T animatable, String name, int transitionTicks, AnimationStateHandler<T> handler) {
            super(animatable, name, transitionTicks, handler);
        }

        @Override
        protected double adjustTick(double tick) {
            boolean reset = this.shouldResetTick;
            double adjusted = super.adjustTick(tick);
            if (this.animatable instanceof Entity entity && entity.getWorld().isClient()) {
                LAST_FRAME.put(entity, entity.age);
                if (reset && listener != null && getAnimationState() == State.RUNNING && getCurrentAnimation() != null) {
                    listener.keyframeZero(entity, getCurrentAnimation().animation().name());
                }
            }
            return adjusted;
        }
    }
}
