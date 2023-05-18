package com.finallion.graveyard.blockentities;


import com.finallion.graveyard.blocks.OssuaryBlock;
import com.finallion.graveyard.blocks.SarcophagusBlock;
import com.finallion.graveyard.client.gui.OssuaryScreenHandler;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGSounds;
import com.finallion.graveyard.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.builder.RawAnimation;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;


public class OssuaryBlockEntity extends BlockEntity implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final AnimationBuilder OPEN = new AnimationBuilder().addAnimation("open", ILoopType.EDefaultLoopTypes.PLAY_ONCE).addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    private final AnimationBuilder CLOSE = new AnimationBuilder().addAnimation("close", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME);
    private final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
    private boolean playedSound = false;
    private boolean canClose = false; // ugly fast workaround for hold-on-last-frame not working in Geckolib3
    private int timer = 0;

    public OssuaryBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlocks.OSSUARY_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationState state = event.getController().getAnimationState();
        if (timer > 0) timer--;
        if (this.getCachedState().get(OssuaryBlock.OPEN)) {
            if (state == AnimationState.Stopped)  {
                if (world != null && !playedSound) {
                    PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 4.0D, false);
                    if (playerEntity != null) {
                        playerEntity.playSound(TGSounds.OSSUARY_OPEN, SoundCategory.BLOCKS, 1.0F, -2.0F);
                        playedSound = true;
                        timer = 160;
                    }
                }

                event.getController().setAnimation(OPEN);
            }
            return PlayState.CONTINUE;
        } else if (!this.getCachedState().get(OssuaryBlock.OPEN) && timer == 0) {
            event.getController().setAnimation(CLOSE);
            if (playedSound) playedSound = false;


            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }



    public static void tick(World world, BlockPos pos, BlockState state, OssuaryBlockEntity blockEntity) {
        if (world.random.nextInt(100) == 0) {
            world.playSound(null, pos, SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.BLOCKS, 4.0F, -3.0F);
        }

        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 4.0D, false);
        if (playerEntity != null && !state.get(OssuaryBlock.OPEN)) {
            world.setBlockState(pos, state.with(OssuaryBlock.OPEN, true), 3);
        } else if (playerEntity == null && state.get(OssuaryBlock.OPEN)) {
            world.setBlockState(pos, state.with(OssuaryBlock.OPEN, false), 3);
            //world.playSound((PlayerEntity) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, -3.0F);
        }

    }
}
