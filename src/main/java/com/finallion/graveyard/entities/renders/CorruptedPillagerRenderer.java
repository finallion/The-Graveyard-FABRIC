package com.finallion.graveyard.entities.renders;

import com.finallion.graveyard.client.TheGraveyardClient;
import com.finallion.graveyard.entities.CorruptedPillager;
import com.finallion.graveyard.entities.models.CorruptedIllagerModel;
import com.finallion.graveyard.entities.renders.features.PillagerEyes;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class CorruptedPillagerRenderer extends CorruptedIllagerRenderer<CorruptedPillager> {
    private static final Identifier TEXTURE = new Identifier("graveyard:textures/entity/corrupted_pillager.png");

    public CorruptedPillagerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CorruptedIllagerModel<>(ctx.getPart(TheGraveyardClient.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        //this.addFeature(new PillagerEyes(this));
        this.model.getHat().visible = false;
    }

    @Override
    public Identifier getTexture(CorruptedPillager entity) {
        return TEXTURE;
    }


}
