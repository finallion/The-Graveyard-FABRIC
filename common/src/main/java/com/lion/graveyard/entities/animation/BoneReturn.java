package com.lion.graveyard.entities.animation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.animation.state.AnimationPoint;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.animation.state.ControllerState;
import software.bernie.geckolib.cache.animation.BoneAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

/// Eases bones that only the previous clip animated back to the bind pose.
public final class BoneReturn {
    public static final int TICKS = 5;
    private static final DataTicket<ControllerState[]> RETURNS = DataTicket.create("graveyard_bone_return", ControllerState[].class);

    private BoneReturn() {}

    static void add(GeoRenderState renderState, ControllerState resetState) {
        ControllerState[] known = renderState.getOrDefaultGeckolibData(RETURNS, new ControllerState[0]);
        ControllerState[] all = Arrays.copyOf(known, known.length + 1);
        all[known.length] = resetState;
        renderState.addGeckolibData(RETURNS, all);
    }

    public static void apply(RenderPassInfo<?> renderPassInfo, BoneSnapshots snapshots) {
        ControllerState[] returns = renderPassInfo.getOrDefaultGeckolibData(RETURNS, new ControllerState[0]);

        if (returns.length == 0) {
            return;
        }

        Set<String> animated = new HashSet<>();

        for (ControllerState state : renderPassInfo.getOrDefaultGeckolibData(DataTickets.ANIMATION_CONTROLLER_STATES, new ControllerState[0])) {
            for (BoneAnimation bone : state.animationPoint().animation().boneAnimations()) {
                animated.add(bone.boneName());
            }
        }

        for (ControllerState resetState : returns) {
            BoneAnimation[] bones = resetState.animationPoint().animation().boneAnimations();

            for (int index = 0; index < bones.length; index++) {
                if (!animated.contains(bones[index].boneName())) {
                    int boneIndex = index;
                    snapshots.ifPresent(bones[index].boneName(), snapshot -> easeToBind(snapshot, boneIndex, resetState));
                }
            }
        }
    }

    private static void easeToBind(BoneSnapshot snapshot, int boneIndex, ControllerState state) {
        snapshot.setScale(
                value(snapshot, boneIndex, state, AnimationPoint.Transform.SCALE, AnimationPoint.Axis.X),
                value(snapshot, boneIndex, state, AnimationPoint.Transform.SCALE, AnimationPoint.Axis.Y),
                value(snapshot, boneIndex, state, AnimationPoint.Transform.SCALE, AnimationPoint.Axis.Z)
        );
        snapshot.setRotation(
                value(snapshot, boneIndex, state, AnimationPoint.Transform.ROTATION, AnimationPoint.Axis.X),
                value(snapshot, boneIndex, state, AnimationPoint.Transform.ROTATION, AnimationPoint.Axis.Y),
                value(snapshot, boneIndex, state, AnimationPoint.Transform.ROTATION, AnimationPoint.Axis.Z)
        );
        snapshot.setTranslation(
                value(snapshot, boneIndex, state, AnimationPoint.Transform.TRANSLATION, AnimationPoint.Axis.X),
                value(snapshot, boneIndex, state, AnimationPoint.Transform.TRANSLATION, AnimationPoint.Axis.Y),
                value(snapshot, boneIndex, state, AnimationPoint.Transform.TRANSLATION, AnimationPoint.Axis.Z)
        );
    }

    private static float value(
            BoneSnapshot snapshot,
            int boneIndex,
            ControllerState state,
            AnimationPoint.Transform transform,
            AnimationPoint.Axis axis
    ) {
        return AnimationProcessor.findAnimationPointValue(snapshot, state, state.animationPoint(), null, boneIndex, transform, axis, state.easingOverride());
    }
}
