package com.lion.graveyard.entities.renders;

import com.lion.graveyard.entities.CorruptedIllager;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.SwingAnimationType;

@Environment(EnvType.CLIENT)
public abstract class CorruptedIllagerRenderer<T extends CorruptedIllager>
        extends MobRenderer<T, CorruptedIllagerRenderState, CorruptedIllagerModel> {
    protected CorruptedIllagerRenderer(
            EntityRendererProvider.Context ctx,
            CorruptedIllagerModel model,
            float shadowRadius
    ) {
        super(ctx, model, shadowRadius);
        this.addLayer(new CustomHeadLayer<>(this, ctx.getModelSet(), ctx.getPlayerSkinRenderCache()));
    }

    @Override
    public CorruptedIllagerRenderState createRenderState() {
        return new CorruptedIllagerRenderState();
    }

    @Override
    public void extractRenderState(T illager, CorruptedIllagerRenderState renderState, float partialTick) {
        super.extractRenderState(illager, renderState, partialTick);
        ArmedEntityRenderState.extractArmedEntityRenderState(illager, renderState, this.itemModelResolver, partialTick);
        renderState.swingAnimationType = SwingAnimationType.WHACK;
        renderState.mainArm = illager.getMainArm();
        renderState.isRiding = illager.isPassenger();
        renderState.attackAnim = illager.getAttackAnim(partialTick);
        renderState.ticksUsingItem = illager.getTicksUsingItem();
        renderState.maxCrossbowChargeDuration = CrossbowItem.getChargeDuration(illager.getUseItem(), illager);
        renderState.state = illager.getState();
        renderState.modelDamaged = illager.isModelDamaged();
        renderState.mainHandEmpty = illager.getMainHandItem().isEmpty();
        renderState.chasing = illager.isChasing();
    }

    @Override
    protected void scale(CorruptedIllagerRenderState renderState, PoseStack matrixStack) {
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}
