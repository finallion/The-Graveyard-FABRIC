package com.finallion.graveyard.entites.renders;

import com.finallion.graveyard.entites.renders.features.AcolyteEyes;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

public class AcolyteRender extends IllagerEntityRenderer<VindicatorEntity> {
    private static final Identifier TEXTURE = new Identifier("graveyard:textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererFactory.Context ctx) {
        super(ctx, new IllagerEntityModel<>(ctx.getPart(EntityModelLayers.VINDICATOR)), 0.5F);
        this.addFeature(new AcolyteEyes(this));
        this.model.getHat().visible = true;
    }

    @Override
    public Identifier getTexture(VindicatorEntity entity) {
        return TEXTURE;
    }
}
