package com.finallion.graveyard.entites.renders;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.client.TheGraveyardClient;
import com.finallion.graveyard.entites.Ghoul;
import com.finallion.graveyard.entites.models.GhoulModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class DecayingGhoulRenderer extends MobEntityRenderer<Ghoul, GhoulModel> {

    public DecayingGhoulRenderer(EntityRendererFactory.Context context) {
        super(context, new GhoulModel(context.getPart(TheGraveyardClient.GHOUL_MODEL_LAYER)), 0.5f);
    }

    @Override
    public Identifier getTexture(Ghoul entity) {
        return new Identifier(TheGraveyard.MOD_ID, "textures/entity/decaying_ghoul.png");
    }
}