package com.finallion.graveyard.item;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blocks.AltarBlock;
import com.finallion.graveyard.blocks.MysteriousBoneBlock;
import com.finallion.graveyard.entities.LichEntity;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class VialOfBlood extends Item {
    private static final String BlOOD_KEY = "Blood";

    public VialOfBlood() {
        super(new FabricItemSettings().maxCount(1).group(TheGraveyard.GROUP));
    }

    public static float getBlood(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return 0.1F;
        } else {
            return nbtCompound.getFloat(BlOOD_KEY);
        }
    }

    public static void setBlood(ItemStack stack, float blood) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (blood < 0.9F) {
            nbtCompound.putFloat(BlOOD_KEY, blood);
        }
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack stack = context.getStack();
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        Random random = context.getWorld().random;
        float blood = VialOfBlood.getBlood(stack);
        if (blockState.isOf(TGBlocks.ALTAR) && playerEntity!= null && blood >= 0.8F) {
            BlockPattern.Result result = AltarBlock.getCompletedFramePattern().searchAround(world, context.getBlockPos());

            if (!blockState.get(AltarBlock.BLOODY) && result != null) {
                playerEntity.world.playSound(null, playerEntity.getBlockPos(), TGSounds.VIAL_SPLASH, SoundCategory.BLOCKS, 5.0F, 1.0F);
                world.setBlockState(context.getBlockPos(), blockState.with(AltarBlock.BLOODY, true));
                if (!world.isClient()) {
                    // TODO Not in creative
                    ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                    ItemUsage.exchangeStack(stack, playerEntity, bottle);

                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 430));

                    BlockPos corner = context.getBlockPos().add(-8, 0, -8);

                    // searches square around altar
                    for(int i = 0; i < 16; ++i) {
                        for(int j = 0; j < 16; ++j) {
                            for(int k = 0; k < 2;++k) {
                                BlockPos iteratorPos = new BlockPos(corner.add(i, k, j));
                                BlockState state = world.getBlockState(iteratorPos);

                                if (state.getBlock() instanceof MysteriousBoneBlock) {
                                    world.setBlockState(iteratorPos, Blocks.AIR.getDefaultState());
                                    world.setBlockState(iteratorPos, Blocks.SOUL_FIRE.getDefaultState());
                                }

                                if (state.getBlock() instanceof CandleBlock) {
                                    world.setBlockState(iteratorPos, state.with(CandleBlock.LIT, false));
                                    playerEntity.world.playSound(null, playerEntity.getBlockPos(), SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                                }
                            }
                        }
                    }

                    LichEntity lich = (LichEntity) TGEntities.LICH.create(world);
                    BlockPos blockPos = context.getBlockPos().up();
                    lich.refreshPositionAndAngles((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.55D, (double)blockPos.getZ() + 0.5D, result.getUp().getOpposite().asRotation(), 0.0F);
                    lich.onSummoned(result.getUp().getOpposite(), context.getBlockPos().up());
                    /*
                    Iterator var13 = world.getNonSpectatingEntities(ServerPlayerEntity.class, lich.getBoundingBox().expand(50.0D)).iterator();

                    while(var13.hasNext()) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var13.next();
                        Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, lich);
                    }

                     */

                    world.spawnEntity(lich);
                    lich.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 5));

                    return ActionResult.CONSUME;
                }

                return ActionResult.success(playerEntity.world.isClient);
            }
        }

        return ActionResult.PASS;
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        float blood = 0;
        if (stack.hasNbt()) {
            blood = stack.getNbt().getFloat(BlOOD_KEY);
        }

        if (blood > 0.8F && blood < 0.9F) {
            tooltip.add(Text.translatable("Blood level: full").formatted(Formatting.GRAY));
        } else {
            int level = (int)(blood * 10);
            if (level == 0) {
                tooltip.add(Text.translatable("Blood level: 1/8").formatted(Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable("Blood level: " + level + "/8").formatted(Formatting.GRAY));
            }
        }

    }
}
