package com.lion.graveyard.entities.models;

import com.lion.graveyard.entities.CorruptedIllager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CorruptedIllagerModel<T extends CorruptedIllager> extends BipedEntityModel<T> implements ModelWithArms, ModelWithHead {
    private final ModelPart arms;
    private final ModelPart jacket;


    public CorruptedIllagerModel(ModelPart root) {
        super(root);

        this.arms = root.getChild("arms");
        this.jacket = this.body.getChild("jacket");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData modelPartData4 = modelPartData.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
        modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new Dilation(0.45F)), ModelTransform.NONE);
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
        ModelPartData body = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        body.addChild("jacket", ModelPartBuilder.create().uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        ModelPartData modelPartData3 = modelPartData.addChild("arms", ModelPartBuilder.create().uv(44, 22).cuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).uv(40, 38).cuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), ModelTransform.of(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
        modelPartData3.addChild("left_shoulder", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), ModelTransform.NONE);
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-2.0F, 12.0F, 0.0F));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(2.0F, 12.0F, 0.0F));
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 46).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }



    public void setAngles(T illagerEntity, float f, float g, float h, float i, float j) {
        boolean wearsChestArmor = illagerEntity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
        this.jacket.visible = !wearsChestArmor;


        this.head.yaw = i * 0.017453292F;
        this.head.pitch = j * 0.017453292F;
        if (this.riding) {
            this.rightArm.pitch = -0.62831855F;
            this.rightArm.yaw = 0.0F;
            this.rightArm.roll = 0.0F;
            this.leftArm.pitch = -0.62831855F;
            this.leftArm.yaw = 0.0F;
            this.leftArm.roll = 0.0F;
            //this.shortLeftArm.pitch = -0.62831855F; // new
            //this.leftArmStomp.pitch = -0.62831855F; // new
            this.rightLeg.pitch = -1.4137167F;
            this.rightLeg.yaw = 0.31415927F;
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.4137167F;
            this.leftLeg.yaw = -0.31415927F;
            this.leftLeg.roll = -0.07853982F;
        } else {
            this.rightArm.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.5F;
            this.rightArm.yaw = 0.0F;
            this.rightArm.roll = 0.0F;
            this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
            this.leftArm.yaw = 0.0F;
            this.leftArm.roll = 0.0F;
            //this.shortLeftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F; // new
            //this.leftArmStomp.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F; // new
            this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
            this.rightLeg.yaw = 0.0F;
            this.rightLeg.roll = 0.0F;
            this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.5F;
            this.leftLeg.yaw = 0.0F;
            this.leftLeg.roll = 0.0F;
        }

        CorruptedIllager.State state = illagerEntity.getState();
        //this.leftArmStomp.visible = false;
        //this.shortLeftArm.visible = false;

        if (state == CorruptedIllager.State.ATTACKING) {
            if (illagerEntity.getMainHandStack().isEmpty()) {
                CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, true, this.handSwingProgress, h);
            } else {
                CrossbowPosing.meleeAttack(this.rightArm, this.leftArm, illagerEntity, this.handSwingProgress, h);
            }
        } else if (state == CorruptedIllager.State.SPELLCASTING) {
            this.rightArm.pivotZ = 0.0F;
            this.rightArm.pivotX = -5.0F;
            this.leftArm.pivotZ = 0.0F;
            this.leftArm.pivotX = 5.0F;
            this.rightArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
            this.leftArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
            this.rightArm.roll = 2.3561945F;
            this.leftArm.roll = -2.3561945F;
            this.rightArm.yaw = 0.0F;
            this.leftArm.yaw = 0.0F;
        } else if (state == CorruptedIllager.State.BOW_AND_ARROW) {
            this.rightArm.yaw = -0.1F + this.head.yaw;
            this.rightArm.pitch = -1.5707964F + this.head.pitch;
            this.leftArm.pitch = -0.9424779F + this.head.pitch;
            this.leftArm.yaw = this.head.yaw - 0.4F;
            this.leftArm.roll = 1.5707964F;
        } else if (state == CorruptedIllager.State.CROSSBOW_HOLD) {
            CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
        } else if (state == CorruptedIllager.State.CROSSBOW_CHARGE) {
            CrossbowPosing.charge(this.rightArm, this.leftArm, illagerEntity, true);
        } else if (state == CorruptedIllager.State.CELEBRATING) {
            this.rightArm.pivotZ = 0.0F;
            this.rightArm.pivotX = -5.0F;
            this.rightArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
            this.rightArm.roll = 2.670354F;
            this.rightArm.yaw = 0.0F;
            this.leftArm.pivotZ = 0.0F;
            this.leftArm.pivotX = 5.0F;
            this.leftArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
            this.leftArm.roll = -2.3561945F;
            this.leftArm.yaw = 0.0F;
        } else if (state == CorruptedIllager.State.UNDEAD) {
            CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, false, this.handSwingProgress, h);
        } else if (state == CorruptedIllager.State.UNDEAD_ATTACKING) {
            CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, true, this.handSwingProgress, h);
        }

        if (!illagerEntity.isModelDamaged()) {
            boolean bl = state == CorruptedIllager.State.CROSSED;
            this.arms.visible = bl;
            this.leftArm.visible = !bl;
            this.rightArm.visible = !bl;
        }

    }

    private ModelPart getAttackingArm(Arm arm) {
        return arm == Arm.LEFT ? this.leftArm : this.rightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    public ModelPart getHead() {
        return this.head;
    }

    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.getAttackingArm(arm).rotate(matrices);
    }
}
