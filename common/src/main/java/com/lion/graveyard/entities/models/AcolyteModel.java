package com.lion.graveyard.entities.models;

import com.lion.graveyard.entities.CorruptedIllager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

@Environment(EnvType.CLIENT)
public class AcolyteModel<T extends CorruptedIllager> extends HierarchicalModel<T> implements ArmedModel, HeadedModel {
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart arms;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart shortLeftArm;
    private final ModelPart leftArmStomp;
    private final ModelPart root;

    public AcolyteModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.arms = root.getChild("arms");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.shortLeftArm = root.getChild("short_left_arm");
        this.leftArmStomp = root.getChild("left_arm_stomp");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition modelPartData2 = modelPartData.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        modelPartData2.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
        modelPartData2.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        modelPartData.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F).texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition modelPartData3 = modelPartData.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
        modelPartData3.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.ZERO);
        modelPartData.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
        modelPartData.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        modelPartData.addOrReplaceChild("short_left_arm", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 7.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        modelPartData.addOrReplaceChild("left_arm_stomp", CubeListBuilder.create().texOffs(48, 18).addBox(0.0F, 5.0F, -1.0F, 2.0F, 2.0F, 2.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }



    public void setupAnim(T illagerEntity, float f, float g, float h, float i, float j) {

        this.head.yRot = i * 0.017453292F;
        this.head.xRot = j * 0.017453292F;
        if (this.riding) {
            this.rightArm.xRot = -0.62831855F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = -0.62831855F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.shortLeftArm.xRot = -0.62831855F; // new
            this.leftArmStomp.xRot = -0.62831855F; // new
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = 0.31415927F;
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = -0.31415927F;
            this.leftLeg.zRot = -0.07853982F;
        } else {
            this.rightArm.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.shortLeftArm.xRot = Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F; // new
            this.leftArmStomp.xRot = Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F; // new
            this.rightLeg.xRot = Mth.cos(f * 0.6662F) * 1.4F * g * 0.5F;
            this.rightLeg.yRot = 0.0F;
            this.rightLeg.zRot = 0.0F;
            this.leftLeg.xRot = Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.5F;
            this.leftLeg.yRot = 0.0F;
            this.leftLeg.zRot = 0.0F;
        }

        CorruptedIllager.State state = illagerEntity.getState();
        this.leftArmStomp.visible = false;
        this.shortLeftArm.visible = false;

        if (state == CorruptedIllager.State.ATTACKING) {
            if (illagerEntity.getMainHandItem().isEmpty()) {
                AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, h);
            } else {
                AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, illagerEntity, this.attackTime, h);
            }
        } else if (state == CorruptedIllager.State.SPELLCASTING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(h * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(h * 0.6662F) * 0.25F;
            this.rightArm.zRot = 2.3561945F;
            this.leftArm.zRot = -2.3561945F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
        } else if (state == CorruptedIllager.State.BOW_AND_ARROW) {
            this.rightArm.yRot = -0.1F + this.head.yRot;
            this.rightArm.xRot = -1.5707964F + this.head.xRot;
            this.leftArm.xRot = -0.9424779F + this.head.xRot;
            this.leftArm.yRot = this.head.yRot - 0.4F;
            this.leftArm.zRot = 1.5707964F;
        } else if (state == CorruptedIllager.State.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
        } else if (state == CorruptedIllager.State.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, illagerEntity, true);
        } else if (state == CorruptedIllager.State.CELEBRATING) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.rightArm.xRot = Mth.cos(h * 0.6662F) * 0.05F;
            this.rightArm.zRot = 2.670354F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.leftArm.xRot = Mth.cos(h * 0.6662F) * 0.05F;
            this.leftArm.zRot = -2.3561945F;
            this.leftArm.yRot = 0.0F;

            this.shortLeftArm.x = 5.0F; //new
            this.shortLeftArm.xRot = Mth.cos(h * 0.6662F) * 0.05F;
            this.shortLeftArm.zRot = -2.3561945F;
            this.leftArmStomp.x = 5.0F; //new
            this.leftArmStomp.xRot = Mth.cos(h * 0.6662F) * 0.05F;
            this.leftArmStomp.zRot = -2.3561945F;
        } else if (state == CorruptedIllager.State.UNDEAD) {
            if (illagerEntity.isModelDamaged()) {
                this.shortLeftArm.visible = true;
                this.leftArmStomp.visible = true;
                this.arms.visible = false;
                this.leftArm.visible = false;
            }
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, false, this.attackTime, h);
        } else if (state == CorruptedIllager.State.UNDEAD_ATTACKING) {
            if (illagerEntity.isModelDamaged()) {
                this.shortLeftArm.visible = true;
                this.leftArmStomp.visible = true;
                this.leftArm.visible = false;
            }
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, true, this.attackTime, h);
        }

        if (!illagerEntity.isModelDamaged()) {
            boolean bl = state == CorruptedIllager.State.CROSSED;
            this.arms.visible = bl;
            this.leftArm.visible = !bl;
            this.rightArm.visible = !bl;
        }

    }

    private ModelPart getArm(HumanoidArm p_102923_) {
        return p_102923_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public void translateToHand(HumanoidArm p_102925_, PoseStack p_102926_) {
        this.getArm(p_102925_).translateAndRotate(p_102926_);
    }

    public ModelPart root() {
        return this.root;
    }

}
