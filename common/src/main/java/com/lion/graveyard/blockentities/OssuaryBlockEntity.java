package com.lion.graveyard.blockentities;

import com.lion.graveyard.blocks.OssuaryBlock;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class OssuaryBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation OPEN = RawAnimation.begin().then("open", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation CLOSE = RawAnimation.begin().then("close", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation IDLE = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private boolean playedSound = false;

    public OssuaryBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.OSSUARY_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, event -> {
            AnimationController.State state = event.getController().getAnimationState();
            if (this.getBlockState().getValue(OssuaryBlock.OPEN)) {
                if (state == AnimationController.State.STOPPED || state == AnimationController.State.PAUSED)  {
                    if (level != null && !playedSound) {
                        Player playerEntity = level.getNearestPlayer((double)getBlockPos().getX() + 0.5D, (double)getBlockPos().getY() + 0.5D, (double)getBlockPos().getZ() + 0.5D, 4.0D, false);
                        if (playerEntity != null) {
                            playerEntity.playNotifySound(TGSounds.OSSUARY_OPEN.get(), SoundSource.BLOCKS, 1.0F, -2.0F);
                            playedSound = true;
                        }
                    }

                    event.getController().setAnimation(OPEN);
                }
                return PlayState.CONTINUE;
            } else if (!this.getBlockState().getValue(OssuaryBlock.OPEN) && state == AnimationController.State.PAUSED) {
                event.getController().setAnimation(CLOSE);
                if (playedSound) playedSound = false;

                return PlayState.CONTINUE;
            }
            return PlayState.CONTINUE;
        }));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, OssuaryBlockEntity blockEntity) {
        if (world.random.nextInt(100) == 0) {
            world.playSound(null, pos, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 4.0F, -3.0F);
        }

        Player playerEntity = world.getNearestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 4.0D, false);
        if (playerEntity != null && !state.getValue(OssuaryBlock.OPEN)) {
            world.setBlock(pos, state.setValue(OssuaryBlock.OPEN, true), 3);
        } else if (playerEntity == null && state.getValue(OssuaryBlock.OPEN)) {
            world.setBlock(pos, state.setValue(OssuaryBlock.OPEN, false), 3);
        }

    }
}
