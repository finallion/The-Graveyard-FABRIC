package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.item.VialOfBlood;
import net.minecraft.advancements.CriteriaTriggers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import java.util.function.Predicate;

public class AltarBlock extends Block {
    public static final BooleanProperty BLOODY = BooleanProperty.create("bloody");
    private static BlockPattern COMPLETED_ALTAR;
    private static BlockPattern ANY_ORDER_ALTAR;


    public AltarBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(BLOODY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOODY);
    }


    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(state, world, pos, random);

        if (state.getValue(BLOODY)) {
            if (random.nextInt(10) == 0) {
                world.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, TGSounds.ALTAR_AMBIENT.get(), SoundSource.BLOCKS, 0.05F, random.nextFloat() * 0.4F + 0.8F, true);
            }
        }

    }

    public static BlockPattern getCompletedFramePattern() {
        if (COMPLETED_ALTAR == null) {
            COMPLETED_ALTAR = BlockPatternBuilder.start().aisle("???x???", "???????", "???????", "???????", "???????", "???????", "???????", "a??b??c")
                    .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .where('a', BlockInWorld.hasState(BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF.get()).or(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF.get()))))
                    .where('b', BlockInWorld.hasState(BlockStatePredicate.forBlock(TGBlocks.MIDDLE_BONE_STAFF.get())))
                    .where('c', BlockInWorld.hasState(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF.get()).or(BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF.get()))))
                    .where('x', BlockInWorld.hasState(BlockStatePredicate.forBlock(TGBlocks.ALTAR.get())))
                    .build();
        }

        return COMPLETED_ALTAR;
    }

    private static BlockPattern getAnyOrderFramePattern() {
        if (ANY_ORDER_ALTAR == null) {
            Predicate<BlockState> anyFragment = BlockStatePredicate.forBlock(TGBlocks.LOWER_BONE_STAFF.get())
                    .or(BlockStatePredicate.forBlock(TGBlocks.MIDDLE_BONE_STAFF.get()))
                    .or(BlockStatePredicate.forBlock(TGBlocks.UPPER_BONE_STAFF.get()));
            ANY_ORDER_ALTAR = BlockPatternBuilder.start().aisle("???x???", "???????", "???????", "???????", "???????", "???????", "???????", "f??f??f")
                    .where('?', BlockInWorld.hasState(BlockStatePredicate.ANY))
                    .where('f', BlockInWorld.hasState(anyFragment))
                    .where('x', BlockInWorld.hasState(BlockStatePredicate.forBlock(TGBlocks.ALTAR.get())))
                    .build();
        }

        return ANY_ORDER_ALTAR;
    }

    private static String findSummonFailure(Level world, BlockPos pos, float blood) {
        if (world.getDifficulty() == Difficulty.PEACEFUL) {
            return "block.graveyard.altar.peaceful";
        }

        if (!world.isDarkOutside() && !world.dimensionType().hasFixedTime()) {
            return "block.graveyard.altar.night_only";
        }

        if (blood < 0.8F) {
            return "block.graveyard.altar.vial_not_full";
        }

        if (Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").summoningNeedsStaffFragments
                && getCompletedFramePattern().find(world, pos) == null) {
            return getAnyOrderFramePattern().find(world, pos) != null
                    ? "block.graveyard.altar.wrong_order"
                    : "block.graveyard.altar.missing_fragments";
        }

        return null;
    }

    private static boolean hasLivingLich(Level world, BlockPos altarPos) {
        BlockPos home = altarPos.above();

        return world.getEntitiesOfClass(LichEntity.class, new AABB(altarPos).inflate(64.0D), lich -> lich.isAlive() && !lich.isRemoved())
                .stream()
                .anyMatch(lich -> !lich.isHomeKnown() || lich.getHomePos().equals(home));
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!player.mayBuild()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!world.isClientSide() && state.is(TGBlocks.ALTAR.get()) && state.getValue(BLOODY) && !hasLivingLich(world, pos)) {
            state = state.setValue(BLOODY, false);
            world.setBlock(pos, state, Block.UPDATE_ALL);
        }

        float blood = 0.0F;

        if (stack.is(TGItems.VIAL_OF_BLOOD.get())) {
            blood = VialOfBlood.getBlood(stack);
        }

        if (!world.isClientSide() && stack.is(TGItems.VIAL_OF_BLOOD.get()) && state.is(TGBlocks.ALTAR.get()) && !state.getValue(BLOODY)) {
            String failure = findSummonFailure(world, pos, blood);

            if (failure != null) {
                player.sendOverlayMessage(Component.translatable(failure));
            }
        }

        if (state.is(TGBlocks.ALTAR.get()) && (blood >= 0.8F || Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isBossSummonableItem.contains(stack.getItem().getDescriptionId())) && world.getDifficulty() != Difficulty.PEACEFUL && (world.isDarkOutside() || world.dimensionType().hasFixedTime())) {
            BlockPattern.BlockPatternMatch result = AltarBlock.getCompletedFramePattern().find(world, pos);

            if (!state.getValue(AltarBlock.BLOODY) && (result != null || !Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").summoningNeedsStaffFragments)) {
                if (!world.isClientSide()) {
                    world.playSound(null, player.blockPosition(), TGSounds.VIAL_SPLASH.get(), SoundSource.BLOCKS, 5.0F, 1.0F);
                }

                world.setBlockAndUpdate(pos, state.setValue(AltarBlock.BLOODY, true));
                Direction direction;

                if (result == null) {
                    direction = Direction.NORTH;
                } else {
                    direction = result.getUp();
                }

                if (!world.isClientSide()) {
                    if (stack.is(TGItems.VIAL_OF_BLOOD.get())) {
                        VialOfBlood.setBlood(stack, 0.1F);
                    }

                    if (!player.isCreative()) {
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 430));
                    }


                    BlockPos corner = pos.offset(-8, 0, -8);

                    for (int i = 0; i < 16; ++i) {
                        for (int j = 0; j < 16; ++j) {
                            for (int k = 0; k < 2; ++k) {
                                BlockPos iteratorPos = new BlockPos(corner.offset(i, k, j));
                                BlockState blockState = world.getBlockState(iteratorPos);

                                if (blockState.getBlock() instanceof OminousBoneStaffFragment) {
                                    world.setBlockAndUpdate(iteratorPos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }

                    LichEntity lich = (LichEntity) TGEntities.LICH.get().create(world, EntitySpawnReason.TRIGGERED);
                    BlockPos blockPos = pos.above();
                    lich.setYRot(direction.getOpposite().toYRot());
                    lich.setYBodyRot(direction.getOpposite().toYRot());
                    lich.setYHeadRot(direction.getOpposite().toYRot());
                    lich.snapTo((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.55D, (double) blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                    lich.onSummoned(direction.getOpposite(), pos.above());

                    for (ServerPlayer serverPlayerEntity : world.getEntitiesOfClass(ServerPlayer.class, lich.getBoundingBox().inflate(50.0D))) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayerEntity, lich);
                    }


                    lich.setInvisible(true);
                    world.addFreshEntity(lich);

                    return InteractionResult.CONSUME;
                }

                return InteractionResult.SUCCESS;
            }
        }


        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }
}
