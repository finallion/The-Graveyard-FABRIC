package com.lion.graveyard.entities.renders;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.TormentedVillagerEntity;
import com.lion.graveyard.entities.models.TormentedVillagerModel;
import com.lion.graveyard.entities.renders.features.GraveyardEyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class TormentedVillagerRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<TormentedVillagerEntity, R> {
    private static final Identifier EYES = Graveyard.id("textures/entity/tormented_villager_eyes.png");

    public TormentedVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new TormentedVillagerModel());
        this.addRenderLayer(new GraveyardEyesLayer<>(this, EYES));
        this.shadowRadius = 0.4F;
    }

    @Override
    protected float getDeathMaxRotation(GeoRenderState renderState) {
        return 0.0F;
    }

    @Override
    public RenderType getRenderType(R renderState, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
