package com.lion.graveyard.blockentities.models;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/// The bone parts, built once. A pose only ever moves these - it never describes geometry, which is
/// what keeps a new pose a text file.
///
/// The parts hang side by side under the root rather than in a chain: poses are fixed, so nothing
/// has to inherit a parent's rotation, and a flat list is what the pose file can address by name.
/// They are cut from the vanilla skeleton texture, so the wither variant is a second texture and
/// not a second set of parts.
@Environment(EnvType.CLIENT)
public class SkeletonPartsModel {

    public static final String SKULL = "skull";
    public static final String JAW = "jaw";
    public static final String RIBCAGE = "ribcage";
    public static final String SPINE = "spine";
    public static final String PELVIS = "pelvis";

    private final Map<String, ModelPart> parts = new HashMap<>();

    public SkeletonPartsModel(ModelPart root) {
        for (String name : names()) {
            this.parts.put(name, root.getChild(name));
        }
    }

    @Nullable
    public ModelPart part(String name) {
        return this.parts.get(name);
    }

    private static String[] names() {
        return new String[]{SKULL, JAW, RIBCAGE, SPINE, PELVIS,
                "upper_arm_right", "upper_arm_left", "lower_arm_right", "lower_arm_left",
                "hand_right", "hand_left",
                "upper_leg_right", "upper_leg_left", "lower_leg_right", "lower_leg_left",
                "foot_right", "foot_left"};
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        root.addChild(SKULL, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
        root.addChild(JAW, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -2.0F, -4.0F, 8.0F, 2.0F, 8.0F), ModelTransform.NONE);
        root.addChild(RIBCAGE, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 4.0F), ModelTransform.NONE);
        root.addChild(SPINE, ModelPartBuilder.create().uv(16, 16).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F), ModelTransform.NONE);
        root.addChild(PELVIS, ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 3.0F, 4.0F), ModelTransform.NONE);

        limb(root, "upper_arm_right", 40, 16, 6.0F);
        limb(root, "upper_arm_left", 40, 16, 6.0F);
        limb(root, "lower_arm_right", 40, 16, 6.0F);
        limb(root, "lower_arm_left", 40, 16, 6.0F);
        limb(root, "hand_right", 40, 16, 2.0F);
        limb(root, "hand_left", 40, 16, 2.0F);
        limb(root, "upper_leg_right", 0, 16, 6.0F);
        limb(root, "upper_leg_left", 0, 16, 6.0F);
        limb(root, "lower_leg_right", 0, 16, 6.0F);
        limb(root, "lower_leg_left", 0, 16, 6.0F);

        root.addChild("foot_right", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 3.0F), ModelTransform.NONE);
        root.addChild("foot_left", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 3.0F), ModelTransform.NONE);

        return TexturedModelData.of(data, 64, 32);
    }

    /// Every bone of a limb is the same two-by-two stick, only its length differs.
    private static void limb(ModelPartData root, String name, int u, int v, float length) {
        root.addChild(name, ModelPartBuilder.create().uv(u, v).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, length, 2.0F), ModelTransform.NONE);
    }
}
