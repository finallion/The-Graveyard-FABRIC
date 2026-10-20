package com.lion.graveyard.blocks;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGAdvancements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/// Shared behavior of the charred and scorched blocks that can smolder.
/// The player lights them with fire and puts them out with a shovel or water.
public final class SmolderingWood {

    public static final BooleanProperty SMOLDERING = BooleanProperty.create("smoldering");
    public static final String ASH_CONFIG_KEY = "ashParticles";

    private static final int LIGHT_LEVEL = 3;
    private static final int ASH_REACH = 6;
    private static final int ASH_PER_CALL = 3;
    private static final int WHITE_ASH_ONE_IN = 8;

    private SmolderingWood() {}

    public static int light(BlockState state) {
        return state.getValue(SMOLDERING) ? LIGHT_LEVEL : 0;
    }

    private static boolean isFlooded(BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    public static InteractionResult use(
            ItemStack stack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand
    ) {

        if (!player.getAbilities().mayBuild) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        boolean lit = state.getValue(SMOLDERING);

        if (!lit && !isFlooded(state) && (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE))) {
            boolean charge = stack.is(Items.FIRE_CHARGE);
            SoundEvent sound = charge ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            world.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);

            if (!world.isClientSide()) {
                setLit(world, pos, state, true);

                if (player instanceof ServerPlayer serverPlayer) {
                    TGAdvancements.IGNITE_SMOLDERING.trigger(serverPlayer);
                }

                if (charge) {
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                } else {
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                }
            }

            return InteractionResult.SUCCESS;
        }

        if (lit && (stack.getItem() instanceof ShovelItem || stack.is(Items.WATER_BUCKET))) {
            world.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (world.isClientSide()) {
                RandomSource random = world.getRandom();

                for (int i = 0; i < 6; i++) {
                    world.addParticle(ParticleTypes.SMOKE, pos.getX() + random.nextDouble(), pos.getY() + 1.0D, pos.getZ() + random.nextDouble(), 0.0D, 0.05D, 0.0D);
                }
            } else {
                setLit(world, pos, state, false);

                if (player instanceof ServerPlayer serverPlayer) {
                    TGAdvancements.EXTINGUISH_SMOLDERING.trigger(serverPlayer);
                }

                if (!stack.is(Items.WATER_BUCKET)) {
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public static void setLit(Level world, BlockPos pos, BlockState state, boolean lit) {
        world.setBlockAndUpdate(pos, state.setValue(SMOLDERING, lit));

        if (!state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            return;
        }

        BlockPos other = pos.relative(state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN);
        BlockState otherState = world.getBlockState(other);

        if (otherState.is(state.getBlock()) && otherState.hasProperty(SMOLDERING)) {
            world.setBlockAndUpdate(other, otherState.setValue(SMOLDERING, lit));
        }
    }

    public static void extinguish(Level world, BlockPos pos, Entity owner) {
        BlockState state = world.getBlockState(pos);

        if (!state.hasProperty(SMOLDERING) || !state.getValue(SMOLDERING)) {
            return;
        }

        setLit(world, pos, state, false);
        world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (owner instanceof ServerPlayer player) {
            TGAdvancements.EXTINGUISH_SMOLDERING.trigger(player);
        }
    }

    public static void displayTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!state.getValue(SMOLDERING) || isFlooded(state)) {
            return;
        }

        if (random.nextInt(10) == 0) {
            world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
        }

        if (random.nextInt(2) == 0) {
            world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.25D + random.nextDouble() * 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.25D + random.nextDouble() * 0.5D, 0.0D, 0.04D, 0.0D);
        }

        if (random.nextInt(5) == 0) {
            world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, random.nextFloat() / 2.0F, 5.0E-5D, random.nextFloat() / 2.0F);
        }

        ash(world, pos, random);
    }

    public static void ash(Level world, BlockPos pos, RandomSource random) {
        if (!Graveyard.getConfig().booleanEntries.getOrDefault(ASH_CONFIG_KEY, true)) {
            return;
        }

        for (int i = 0; i < ASH_PER_CALL; i++) {
            double x = pos.getX() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * ASH_REACH;
            double y = pos.getY() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * ASH_REACH / 2.0D;
            double z = pos.getZ() + 0.5D + (random.nextDouble() * 2.0D - 1.0D) * ASH_REACH;
            world.addParticle(random.nextInt(WHITE_ASH_ONE_IN) == 0 ? ParticleTypes.WHITE_ASH : ParticleTypes.ASH, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
