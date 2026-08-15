package com.lion.graveyard.entities.animation;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/// Declares which animation plays when, as an ordered table instead of a hand-written if-chain.
/// The first entry whose condition holds wins, so the order *is* the priority and no condition has
/// to spell out which other cases it is not.
///
/// Two rules this shape enforces, both of which the if-chains broke:
///
/// 1. **Conditions must not change anything.** They run on the render thread, once per frame - not
///    once per tick. State that has to change belongs in the entity's tick, where it is counted and
///    synchronised.
/// 2. **Animations blend.** A controller built here transitions over [#TRANSITION_TICKS] ticks.
///
/// A condition may read the frame's [AnimationState] (for movement) or only the entity (for timers
/// and flags), which is why both `when` overloads exist.
public final class AnimationTable<T extends GeoAnimatable> {

    /// Ticks a controller blends over when the animation changes. Zero is a hard cut between poses,
    /// which is what reads as stuttering.
    public static final int TRANSITION_TICKS = 5;

    /// Below this the limb swing is noise, not walking.
    private static final float MOVEMENT_THRESHOLD = 0.05F;

    private final List<Entry<T>> entries = new ArrayList<>();
    @Nullable
    private RawAnimation fallback;

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

    /// Stops the controller instead of playing anything, so another controller on the same model
    /// can take over. Needed where an entity switches between whole sets of animations.
    public AnimationTable<T> stopWhen(BooleanSupplier condition) {
        this.entries.add(new Entry<>(state -> condition.getAsBoolean(), null));
        return this;
    }

    /// Plays when nothing above matched. Without one the controller stops instead, which leaves the
    /// model in its last pose.
    public AnimationTable<T> otherwise(RawAnimation animation) {
        this.fallback = animation;
        return this;
    }

    public AnimationController<T> build(T animatable, String name) {
        return build(animatable, name, TRANSITION_TICKS);
    }

    public AnimationController<T> build(T animatable, String name, int transitionTicks) {
        return new AnimationController<>(animatable, name, transitionTicks, state -> {
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
        });
    }

    /// One answer to "is it moving", because the entities used to disagree: some asked GeckoLib,
    /// some measured the limb swing, two did both and combined them differently.
    public static boolean isMoving(AnimationState<?> state) {
        return state.isMoving() || Math.abs(state.getLimbSwingAmount()) > MOVEMENT_THRESHOLD;
    }

    /// A null animation means "stop here", see [#stopWhen].
    private record Entry<T extends GeoAnimatable>(Predicate<AnimationState<T>> condition, @Nullable RawAnimation animation) {
    }
}
