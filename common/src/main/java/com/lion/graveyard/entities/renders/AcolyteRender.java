package main.java.com.lion.graveyard.entities.renders;

import com.finallion.graveyard.client.TheGraveyardClient;
import main.java.com.lion.graveyard.entities.AcolyteEntity;
import main.java.com.lion.graveyard.entities.models.CorruptedIllagerModel;
import main.java.com.lion.graveyard.entities.renders.features.AcolyteEyes;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AcolyteRender extends CorruptedIllagerRenderer<AcolyteEntity> {
    private static final Identifier TEXTURE = new Identifier("graveyard:textures/entity/acolyte.png");

    public AcolyteRender(EntityRendererFactory.Context ctx) {
        super(ctx, new CorruptedIllagerModel<>(ctx.getPart(TheGraveyardClient.CORRUPTED_ILLAGER_MODEL_LAYER)), 0.5F);
        this.addFeature(new AcolyteEyes(this));
        this.model.getHat().visible = true;
        this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()) {
            public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AcolyteEntity acolyte, float f, float g, float h, float j, float k, float l) {
                if (acolyte.isAttacking()) {
                    super.render(matrixStack, vertexConsumerProvider, i, acolyte, f, g, h, j, k, l);
                }

            }
        });
    }

    @Override
    public Identifier getTexture(AcolyteEntity entity) {
        return TEXTURE;
    }


}
