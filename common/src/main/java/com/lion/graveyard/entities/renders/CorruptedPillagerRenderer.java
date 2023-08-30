package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.CorruptedPillager;
import com.lion.graveyard.entities.models.CorruptedIllagerModel;
import com.lion.graveyard.init.TGEntityModelLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class CorruptedPillagerRenderer extends CorruptedIllagerRenderer<CorruptedPillager> {
    private static final Identifier TEXTURE = new Identifier(Graveyard.MOD_ID,"textures/entity/corrupted_pillager.png");

    public CorruptedPillagerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CorruptedIllagerModel<>(ctx.getPart(TGEntityModelLayers.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        this.model.getHat().visible = false;
    }

    @Override
    public Identifier getTexture(CorruptedPillager entity) {
        return TEXTURE;
    }


}
