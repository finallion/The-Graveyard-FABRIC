package com.finallion.graveyard.blocks;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.init.*;
import com.finallion.graveyard.item.VialOfBlood;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class AltarBlock extends Block {
    public static final BooleanProperty BLOODY = BooleanProperty.of("bloody");
    private static BlockPattern COMPLETED_ALTAR;


    public AltarBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(BLOODY, false)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BLOODY);
    }


    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (state.get(BLOODY)) {
            if (random.nextInt(10) == 0) {
                world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, TGSounds.ALTAR_AMBIENT, SoundCategory.BLOCKS, 0.05F, random.nextFloat() * 0.4F + 0.8F, true);
            }
        }

    }

    public static BlockPattern getCompletedFramePattern() {
        if (COMPLETED_ALTAR == null) {
            COMPLETED_ALTAR = BlockPatternBuilder.start().aisle("???x???", "???????", "???????", "???????", "???????", "???????", "???????", "a??b??c")
                    .where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.ANY))
                    .where('a', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF).or(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF))))
                    .where('b', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.MIDDLE_BONE_STAFF)))
                    .where('c', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF).or(BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF))))
                    .where('x', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(TGBlocks.ALTAR)))
                    .build();
        }

        return COMPLETED_ALTAR;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        float blood = 0.0F;
        if (stack.isOf(TGItems.VIAL_OF_BLOOD)) {
            blood = VialOfBlood.getBlood(stack);
        }

        if (state.isOf(TGBlocks.ALTAR) && (blood >= 0.8F || TheGraveyard.config.corruptedChampionConfigEntries.get("corrupted_champion").isBossSummonableItem.contains(stack.getItem().getTranslationKey())) && world.getDifficulty() != Difficulty.PEACEFUL && (world.isNight() || world.getDimension().hasFixedTime())) {
            BlockPattern.Result result = AltarBlock.getCompletedFramePattern().searchAround(world, pos);

            if (!state.get(AltarBlock.BLOODY) && (result != null || !TheGraveyard.config.corruptedChampionConfigEntries.get("corrupted_champion").summoningNeedsStaffFragments)) {
                player.getWorld().playSound(null, player.getBlockPos(), TGSounds.VIAL_SPLASH, SoundCategory.BLOCKS, 5.0F, 1.0F);
                world.setBlockState(pos, state.with(AltarBlock.BLOODY, true));
                Direction direction;

                if (result == null) {
                    direction = Direction.NORTH;
                } else {
                    direction = result.getUp();
                }

                if (!world.isClient()) {
                    if (!player.isCreative()) {
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        ItemUsage.exchangeStack(stack, player, bottle);
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 430));

                    }


                    BlockPos corner = pos.add(-8, 0, -8);

                    // searches square around altar
                    for (int i = 0; i < 16; ++i) {
                        for (int j = 0; j < 16; ++j) {
                            for (int k = 0; k < 2; ++k) {
                                BlockPos iteratorPos = new BlockPos(corner.add(i, k, j));
                                BlockState blockState = world.getBlockState(iteratorPos);

                                if (blockState.getBlock() instanceof OminousBoneStaffFragment) {
                                    world.setBlockState(iteratorPos, Blocks.AIR.getDefaultState());
                                }
                            }
                        }
                    }

                    LichEntity lich = (LichEntity) TGEntities.LICH.create(world);
                    BlockPos blockPos = pos.up();
                    lich.setYaw(direction.getOpposite().asRotation());
                    lich.setBodyYaw(direction.getOpposite().asRotation());
                    lich.setHeadYaw(direction.getOpposite().asRotation());
                    lich.refreshPositionAndAngles((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.55D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                    lich.onSummoned(direction.getOpposite(), pos.up());

                    Iterator var13 = world.getNonSpectatingEntities(ServerPlayerEntity.class, lich.getBoundingBox().expand(50.0D)).iterator();

                    while (var13.hasNext()) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) var13.next();
                        Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, lich);
                    }


                    world.spawnEntity(lich);
                    lich.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 5));

                    return ActionResult.CONSUME;
                }

                return ActionResult.success(player.getWorld().isClient);
            }
        }


        return super.onUse(state, world, pos, player, hand, hit);
    }
}
