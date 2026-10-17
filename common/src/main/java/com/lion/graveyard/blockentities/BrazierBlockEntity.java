package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public class BrazierBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public BrazierBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.BRAZIER_BLOCK_ENTITY.get(), pos, state);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

}
