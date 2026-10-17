package com.lion.graveyard.entities.animation;

import com.lion.graveyard.platform.DevEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public final class AnimationPreview {
    public static final String ALL = "*";
    private static final String PREFIX = "anim:";
    private static final double UNKNOWN_STEP_TICKS = 40.0D;
    private static final Map<String, RawAnimation> LOOPS = new HashMap<>();
    private static final Map<String, RawAnimation> PLAYS = new HashMap<>();
    private static final Map<AnimationController<?>, Step> STEPS = new WeakHashMap<>();

    private AnimationPreview() {
    }

    @Nullable
    public static String forcedAnimation(Entity entity) {
        if (!DevEnvironment.isDevelopment()) {
            return null;
        }
        Text name = entity.getCustomName();
        if (name == null) {
            return null;
        }
        String text = name.getString();
        return text.startsWith(PREFIX) ? text.substring(PREFIX.length()).trim() : null;
    }

    @Nullable
    public static RawAnimation frame(AnimationState<?> state, Supplier<List<String>> sequence) {
        if (!(state.getAnimatable() instanceof Entity entity)) {
            return null;
        }
        String forced = forcedAnimation(entity);
        if (forced == null) {
            return null;
        }
        AnimationController<?> controller = state.getController();
        controller.transitionLength(AnimationTable.TRANSITION_TICKS);
        if (!ALL.equals(forced)) {
            return LOOPS.computeIfAbsent(forced, key -> RawAnimation.begin().thenLoop(key));
        }
        List<String> names = sequence.get();
        if (names.isEmpty()) {
            return null;
        }
        String name = names.get(nextIndex(controller, names, entity.age + state.getPartialTick()));
        return PLAYS.computeIfAbsent(name, key -> RawAnimation.begin().then(key, Animation.LoopType.PLAY_ONCE));
    }

    private static int nextIndex(AnimationController<?> controller, List<String> names, double clock) {
        Step step = STEPS.computeIfAbsent(controller, key -> new Step());
        if (step.index >= names.size()) {
            step.index = 0;
        }
        if (step.started < 0.0D) {
            step.started = clock;
        } else if (controller.hasAnimationFinished() || isStalled(controller, names.get(step.index), clock - step.started)) {
            step.index = (step.index + 1) % names.size();
            step.started = clock;
            controller.forceAnimationReset();
        }
        return step.index;
    }

    private static boolean isStalled(AnimationController<?> controller, String name, double elapsed) {
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        boolean known = current != null && current.animation().name().equals(name);
        return !known && elapsed >= UNKNOWN_STEP_TICKS;
    }

    private static final class Step {
        int index;
        double started = -1.0D;
    }
}
