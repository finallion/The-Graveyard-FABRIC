package com.finallion.graveyard.blockentities;

import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class BrazierBlockEntity extends BlockEntity implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public BrazierBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlocks.BRAZIER_BLOCK_ENTITY, pos, state);
    }

    @SuppressWarnings("unchecked")
    private <E extends BlockEntity & IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
