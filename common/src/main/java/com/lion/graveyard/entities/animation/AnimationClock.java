package com.lion.graveyard.entities.animation;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.ToDoubleFunction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

/// Reports keyframe 0 of each animation start and each loop restart.
public final class AnimationClock {
    private static final int RENDER_GRACE_TICKS = 2;
    private static final Map<Entity, Integer> LAST_FRAME = new WeakHashMap<>();
    @Nullable
    private static Listener listener;

    private AnimationClock() {
    }

    public interface Listener {
        void keyframeZero(Entity entity, String animation, double startOffsetTicks);

        void playTracked(Entity entity, @Nullable SoundEvent sound);

        void playDetached(Entity entity, @Nullable SoundEvent sound);
    }

    public static void setListener(@Nullable Listener value) {
        listener = value;
    }

    public static boolean isRendered(Entity entity) {
        Integer frame = LAST_FRAME.get(entity);
        return frame != null && entity.tickCount - frame <= RENDER_GRACE_TICKS;
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
        return new ClockController<>(animatable, name, transitionTicks, handler, null, null);
    }

    public static <T extends GeoAnimatable> AnimationController<T> controller(T animatable, String name, int transitionTicks, AnimationController.AnimationStateHandler<T> handler, @Nullable String offsetAnimation, @Nullable ToDoubleFunction<T> startOffset) {
        return new ClockController<>(animatable, name, transitionTicks, handler, offsetAnimation, startOffset);
    }

    private static final class ClockController<T extends GeoAnimatable> extends AnimationController<T> {
        @Nullable
        private final String offsetAnimation;
        @Nullable
        private final ToDoubleFunction<T> startOffset;
        @Nullable
        private String lastStarted;

        ClockController(T animatable, String name, int transitionTicks, AnimationStateHandler<T> handler, @Nullable String offsetAnimation, @Nullable ToDoubleFunction<T> startOffset) {
            super(animatable, name, transitionTicks, handler);
            this.offsetAnimation = offsetAnimation;
            this.startOffset = startOffset;
        }

        @Override
        protected double adjustTick(double tick) {
            boolean reset = this.shouldResetTick;
            double adjusted = super.adjustTick(tick);
            double offset = 0.0D;

            if (reset && getAnimationState() == State.RUNNING && getCurrentAnimation() != null) {
                String name = getCurrentAnimation().animation().name();
                boolean firstStart = !name.equals(this.lastStarted);
                this.lastStarted = name;

                if (firstStart && startOffset != null && name.equals(offsetAnimation)) {
                    offset = Math.max(0.0D, startOffset.applyAsDouble(this.animatable));
                    double speed = getAnimationSpeed();
                    this.tickOffset -= speed > 0.0D ? offset / speed : offset;
                    adjusted = offset;
                }
            }

            if (this.animatable instanceof Entity entity && entity.level().isClientSide()) {
                LAST_FRAME.put(entity, entity.tickCount);

                if (reset && listener != null && getAnimationState() == State.RUNNING && getCurrentAnimation() != null) {
                    listener.keyframeZero(entity, getCurrentAnimation().animation().name(), offset);
                }
            }

            return adjusted;
        }
    }
}
