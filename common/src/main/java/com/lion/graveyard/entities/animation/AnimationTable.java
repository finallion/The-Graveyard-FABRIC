package com.lion.graveyard.entities.animation;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.ToDoubleFunction;
import java.util.function.Predicate;

/// Conditions run on the render thread once per frame. They must not change state.
public final class AnimationTable<T extends GeoAnimatable> {

    /// Blend length in ticks. Zero gives a hard cut, which looks like stutter.
    public static final int TRANSITION_TICKS = 5;

    /// Below this the limb swing is noise, not walking.
    private static final float MOVEMENT_THRESHOLD = 0.05F;

    private final List<Entry<T>> entries = new ArrayList<>();
    @Nullable
    private RawAnimation fallback;
    @Nullable
    private IntSupplier transitionTicks;
    @Nullable
    private String offsetAnimation;
    @Nullable
    private ToDoubleFunction<T> startOffset;

    private AnimationTable() {
    }

    public static <T extends GeoAnimatable> AnimationTable<T> create() {
        return new AnimationTable<>();
    }

    public AnimationTable<T> when(Predicate<AnimationState<T>> condition, RawAnimation animation) {
        this.entries.add(new Entry<>(condition, animation));
        return this;
    }

    public AnimationTable<T> when(BooleanSupplier condition, RawAnimation animation) {
        return when(state -> condition.getAsBoolean(), animation);
    }

    public AnimationTable<T> stopWhen(BooleanSupplier condition) {
        this.entries.add(new Entry<>(state -> condition.getAsBoolean(), null));
        return this;
    }

    public AnimationTable<T> otherwise(RawAnimation animation) {
        this.fallback = animation;
        return this;
    }

    public AnimationTable<T> startAt(RawAnimation animation, ToDoubleFunction<T> offset) {
        this.offsetAnimation = animation.getAnimationStages().get(0).animationName();
        this.startOffset = offset;
        return this;
    }

    public AnimationTable<T> transitionTicks(IntSupplier ticks) {
        this.transitionTicks = ticks;
        return this;
    }

    public AnimationController<T> build(T animatable, String name) {
        return build(animatable, name, TRANSITION_TICKS);
    }

    public AnimationController<T> build(T animatable, String name, int transitionTicks) {
        return AnimationClock.controller(animatable, name, transitionTicks, state -> {
            RawAnimation preview = AnimationPreview.frame(state, this::animationNames);

            if (preview != null) {
                state.getController().setAnimation(preview);
                return PlayState.CONTINUE;
            }

            int blend = this.transitionTicks != null ? this.transitionTicks.getAsInt() : transitionTicks;
            state.getController().transitionLength(state.getController().getCurrentAnimation() == null ? 0 : blend);
            for (Entry<T> entry : this.entries) {
                if (entry.condition.test(state)) {
                    if (entry.animation == null) {
                        return PlayState.STOP;
                    }

                    state.getController().setAnimation(entry.animation);
                    return PlayState.CONTINUE;
                }
            }

            if (this.fallback == null) {
                return PlayState.STOP;
            }

            state.getController().setAnimation(this.fallback);
            return PlayState.CONTINUE;
        }, this.offsetAnimation, this.startOffset);
    }

    private List<String> animationNames() {
        Set<String> names = new LinkedHashSet<>();
        for (Entry<T> entry : this.entries) {
            addName(names, entry.animation);
        }
        addName(names, this.fallback);
        return new ArrayList<>(names);
    }

    private static void addName(Set<String> names, @Nullable RawAnimation animation) {
        if (animation != null && !animation.getAnimationStages().isEmpty()) {
            names.add(animation.getAnimationStages().get(0).animationName());
        }
    }

    public static boolean isMoving(AnimationState<?> state) {
        return state.isMoving() || Math.abs(state.getLimbSwingAmount()) > MOVEMENT_THRESHOLD;
    }

    private record Entry<T extends GeoAnimatable>(Predicate<AnimationState<T>> condition, @Nullable RawAnimation animation) {
    }
}
