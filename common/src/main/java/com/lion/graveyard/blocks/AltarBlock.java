package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.LichEntity;
import com.lion.graveyard.init.TGBlocks;
import com.lion.graveyard.init.TGEntities;
import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.item.VialOfBlood;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.phys.BlockHitResult;

import java.util.Iterator;
import java.util.Properties;

public class AltarBlock extends Block {
    public static final BooleanProperty BLOODY = BooleanProperty.create("bloody");
    private static BlockPattern COMPLETED_ALTAR;


    public AltarBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(BLOODY, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BLOODY);
    }


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

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        float blood = 0.0F;
        if (stack.is(TGItems.VIAL_OF_BLOOD.get())) {
            blood = VialOfBlood.getBlood(stack);
        }

        if (state.is(TGBlocks.ALTAR.get()) && (blood >= 0.8F || Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").isBossSummonableItem.contains(stack.getItem().getTranslationKey())) && world.getDifficulty() != Difficulty.PEACEFUL && (world.isNight() || world.getDimension().hasFixedTime())) {
            BlockPattern.BlockPatternMatch result = AltarBlock.getCompletedFramePattern().find(world, pos);

            if (!state.getValue(AltarBlock.BLOODY) && (result != null || !Graveyard.getConfig().corruptedChampionConfigEntries.get("corrupted_champion").summoningNeedsStaffFragments)) {
                player.level().playSound((Player)null, player.blockPosition(), TGSounds.VIAL_SPLASH.get(), SoundSource.BLOCKS, 5.0F, 1.0F);
                world.setBlock(pos, state.setValue(AltarBlock.BLOODY, true));
                Direction direction;

                if (result == null) {
                    direction = Direction.NORTH;
                } else {
                    direction = result.getUp();
                }

                if (!world.isClientSide()) {
                    if (!player.isCreative()) {
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        ItemUtils.createFilledResult(stack, player, bottle);
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 430));

                    }


                    BlockPos corner = pos.offset(-8, 0, -8);

                    // searches square around altar
                    for (int i = 0; i < 16; ++i) {
                        for (int j = 0; j < 16; ++j) {
                            for (int k = 0; k < 2; ++k) {
                                BlockPos iteratorPos = new BlockPos(corner.add(i, k, j));
                                BlockState blockState = world.getBlockState(iteratorPos);

                                if (blockState.getBlock() instanceof OminousBoneStaffFragment) {
                                    world.setBlock(iteratorPos, Blocks.AIR.defaultBlockState());
                                }
                            }
                        }
                    }

                    LichEntity lich = (LichEntity) TGEntities.LICH.get().create(world);
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
                    lich.addStatusEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5));

                    return InteractionResult.CONSUME;
                }

                return InteractionResult.success(player.getLevel().isClient);
            }
        }


        return super.onUse(state, world, pos, player, hand, hit);
    }
}
