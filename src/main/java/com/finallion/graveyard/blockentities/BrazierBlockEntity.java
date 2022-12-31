package com.finallion.graveyard.blockentities;

import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public class BrazierBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public BrazierBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlocks.BRAZIER_BLOCK_ENTITY, pos, state);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

}
