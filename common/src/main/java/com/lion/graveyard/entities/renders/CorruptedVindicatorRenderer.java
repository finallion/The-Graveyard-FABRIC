package main.java.com.lion.graveyard.entities.renders;

import com.finallion.graveyard.client.TheGraveyardClient;
import main.java.com.lion.graveyard.entities.CorruptedVindicator;
import main.java.com.lion.graveyard.entities.models.CorruptedIllagerModel;
import main.java.com.lion.graveyard.entities.renders.features.VindicatorEyes;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class CorruptedVindicatorRenderer extends CorruptedIllagerRenderer<CorruptedVindicator> {
    private static final Identifier TEXTURE = new Identifier("graveyard:textures/entity/corrupted_vindicator.png");

    public CorruptedVindicatorRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CorruptedIllagerModel<>(ctx.getPart(TheGraveyardClient.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        this.addFeature(new VindicatorEyes(this));
        this.model.getHat().visible = false;
    }

    @Override
    public Identifier getTexture(CorruptedVindicator entity) {
        return TEXTURE;
    }


}

