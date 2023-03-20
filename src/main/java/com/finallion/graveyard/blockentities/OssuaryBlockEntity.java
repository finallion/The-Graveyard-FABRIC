package com.finallion.graveyard.blockentities;


import com.finallion.graveyard.blocks.OssuaryBlock;
import com.finallion.graveyard.blocks.SarcophagusBlock;
import com.finallion.graveyard.client.gui.OssuaryScreenHandler;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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

    public OssuaryBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlocks.OSSUARY_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, event -> {
            AnimationController.State state = event.getController().getAnimationState();
            if (this.getCachedState().get(OssuaryBlock.OPEN)) {
                if (state == AnimationController.State.STOPPED || state == AnimationController.State.PAUSED)  {
                    event.getController().setAnimation(OPEN);
                }
                return PlayState.CONTINUE;
            } else if (!this.getCachedState().get(OssuaryBlock.OPEN) && state == AnimationController.State.PAUSED) {
                event.getController().setAnimation(CLOSE);
                return PlayState.CONTINUE;
            }
            return PlayState.CONTINUE;
        }));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    public static void tick(World world, BlockPos pos, BlockState state, OssuaryBlockEntity blockEntity) {
        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 3.0D, false);
        if (playerEntity != null && !state.get(OssuaryBlock.OPEN)) {
            world.setBlockState(pos, state.with(OssuaryBlock.OPEN, true), 3);
            world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.25F, -2.0F);
        } else if (playerEntity == null && state.get(OssuaryBlock.OPEN)) {
            world.setBlockState(pos, state.with(OssuaryBlock.OPEN, false), 3);
            //world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, -3.0F);
        }

    }
}
