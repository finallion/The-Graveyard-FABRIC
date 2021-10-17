package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.GhoulEntity;
import com.finallion.graveyard.entities.models.BaseGhoulModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class GhoulRenderer extends GeoEntityRenderer<GhoulEntity> {

    public GhoulRenderer(EntityRendererFactory.Context context) {
        super(context, new BaseGhoulModel());
        this.shadowRadius = 0.7F;
    }

    // stops the vanilla death animation
    @Override
    protected float getDeathMaxRotation(GhoulEntity entityLivingBaseIn) {
        return 0.0F;
    }

}
