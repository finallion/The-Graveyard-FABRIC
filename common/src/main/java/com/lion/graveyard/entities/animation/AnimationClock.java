package com.lion.graveyard.entities.animation;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTimeline;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.ToDoubleFunction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

/// Reports keyframe 0 of each animation start and each loop restart.
public final class AnimationClock {
    private static final int RENDER_GRACE_TICKS = 2;
    private static final double TICKS_PER_SECOND = 20.0D;
    private static final double LOOP_EPSILON = 1.0E-6D;
    private static final Map<Entity, Integer> LAST_FRAME = new WeakHashMap<>();
    @Nullable
    private static Listener listener;

    private AnimationClock() {}

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

    /// Restarts the running clip with a blend from the current pose.
    public static void restart(AnimationController<?> controller) {
        if (controller instanceof ClockController<?> clock) {
            clock.restart();
        }
    }

    public static <T extends GeoAnimatable> AnimationController<T> controller(
            String name,
            int transitionTicks,
            AnimationController.AnimationStateHandler<T> handler
    ) {
        return new ClockController<>(name, transitionTicks, handler, null, null);
    }

    public static <T extends GeoAnimatable> AnimationController<T> controller(
            String name,
            int transitionTicks,
            AnimationController.AnimationStateHandler<T> handler,
            @Nullable String offsetAnimation,
            @Nullable ToDoubleFunction<T> startOffset
    ) {
        return new ClockController<>(name, transitionTicks, handler, offsetAnimation, startOffset);
    }

    private static final class ClockController<T extends GeoAnimatable> extends AnimationController<T> {
        @Nullable
        private final String offsetAnimation;
        @Nullable
        private final ToDoubleFunction<T> startOffset;
        @Nullable
        private String lastStarted;
        @Nullable
        private AnimationTimeline.Stage runningStage;
        private double lastAnimationTime;

        ClockController(
                String name,
                int transitionTicks,
                AnimationStateHandler<T> handler,
                @Nullable String offsetAnimation,
                @Nullable ToDoubleFunction<T> startOffset
        ) {
            super(name, transitionTicks, guard(handler));
            this.offsetAnimation = offsetAnimation;
            this.startOffset = startOffset;
            receiveTriggeredAnimations();
        }

        // A stopped controller must restart the same animation.
        private static <T extends GeoAnimatable> AnimationStateHandler<T> guard(AnimationStateHandler<T> handler) {
            return test -> {
                AnimationController<T> controller = test.controller();

                if (controller instanceof ClockController<T> clock && clock.isRunningTriggeredClip()) {
                    return PlayState.CONTINUE;
                }

                boolean stopped = controller.getPlayState() == PlayState.STOP;
                RawAnimation previous = controller.getCurrentRawAnimation();
                PlayState result = handler.handle(test);

                if (stopped && result != PlayState.STOP && previous != null && previous.equals(controller.getCurrentRawAnimation())) {
                    controller.reset();
                    controller.setAnimation(previous);
                }

                return result;
            };
        }

        void restart() {
            this.currentRawAnimation = null;
        }

        // The base check is false in the first frame of a trigger, while animationPoint is still null.
        boolean isRunningTriggeredClip() {
            if (this.triggeredAnimTime < 0) {
                return false;
            }

            return this.animationPoint == null || this.timeline != null && this.timelineTime >= 0 && this.timelineTime < this.timeline.lastAnimationEndTime();
        }

        @Override
        protected boolean checkControllerState(
                T animatable,
                GeoRenderState renderState,
                AnimatableManager<T> manager,
                GeoModel<T> geoModel
        ) {
            boolean wasStopped = this.playState == PlayState.STOP;

            super.checkControllerState(animatable, renderState, manager, geoModel);

            if (wasStopped && this.playState == PlayState.STOP && isLoopingClip()) {
                reset();
            }

            AnimationTimeline.Stage stage = currentAnimationStage();
            double offset = 0.0D;
            boolean keyframeZero = false;

            if (stage != null && stage != this.runningStage) {
                keyframeZero = true;
                offset = applyStartOffset(animatable, stage);
            } else if (stage != null && this.animationPoint.animTime() + LOOP_EPSILON < this.lastAnimationTime) {
                keyframeZero = true;
            }

            this.runningStage = stage;
            this.lastAnimationTime = stage == null ? 0.0D : this.animationPoint.animTime();

            if (animatable instanceof Entity entity && entity.level().isClientSide()) {
                LAST_FRAME.put(entity, entity.tickCount);

                if (keyframeZero && listener != null) {
                    listener.keyframeZero(entity, stage.animation().name(), offset);
                }
            }

            return isAnimatingBones();
        }

        private boolean isLoopingClip() {
            if (this.animationPoint == null) {
                return false;
            }

            LoopType loop = this.animationPoint.loopType() == LoopType.DEFAULT ? this.animationPoint.animation().loopType() : this.animationPoint.loopType();
            return loop == LoopType.LOOP;
        }

        @Nullable
        private AnimationTimeline.Stage currentAnimationStage() {
            if (this.timeline == null || this.animationPoint == null || this.timelineTime < 0) {
                return null;
            }

            AnimationTimeline.Stage stage = this.timeline.getStage(this.timelineTime);
            return stage.isTransition() ? null : stage;
        }

        private double applyStartOffset(T animatable, AnimationTimeline.Stage stage) {
            String name = stage.animation().name();
            boolean firstStart = !name.equals(this.lastStarted);
            this.lastStarted = name;

            if (!firstStart || this.startOffset == null || !name.equals(this.offsetAnimation)) {
                return 0.0D;
            }

            double offset = Math.max(0.0D, this.startOffset.applyAsDouble(animatable));

            if (offset > 0.0D) {
                this.timelineTime = Math.min(stage.startTime() + offset / TICKS_PER_SECOND, stage.endTime());
                this.animationPoint = this.timeline.createAnimationPoint(this.timelineTime, this.animationPoint, this.easingOverride);
            }

            return offset;
        }
    }
}
