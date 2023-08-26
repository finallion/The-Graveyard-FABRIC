package main.java.com.lion.graveyard.item;

import com.finallion.graveyard.blocks.AltarBlock;
import com.finallion.graveyard.blocks.OminousBoneStaffFragment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VialOfBlood extends Item {
    private static final String BlOOD_KEY = "Blood";

    public VialOfBlood() {
        super(new FabricItemSettings().maxCount(1));
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

        /*
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack stack = context.getStack();
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        Random random = context.getWorld().random;
        float blood = VialOfBlood.getBlood(stack);
        if (blockState.isOf(TGBlocks.ALTAR) && playerEntity!= null && blood >= 0.8F && world.getDifficulty() != Difficulty.PEACEFUL && world.isNight()) {
            BlockPattern.Result result = AltarBlock.getCompletedFramePattern().searchAround(world, context.getBlockPos());

            if (!blockState.get(AltarBlock.BLOODY) && result != null) {
                playerEntity.world.playSound(null, playerEntity.getBlockPos(), TGSounds.VIAL_SPLASH, SoundCategory.BLOCKS, 5.0F, 1.0F);
                world.setBlockState(context.getBlockPos(), blockState.with(AltarBlock.BLOODY, true));
                if (!world.isClient()) {
                    if (!playerEntity.isCreative()) {
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        ItemUsage.exchangeStack(stack, playerEntity, bottle);
                    }

                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 430));

                    BlockPos corner = context.getBlockPos().add(-8, 0, -8);

                    // searches square around altar
                    for(int i = 0; i < 16; ++i) {
                        for(int j = 0; j < 16; ++j) {
                            for(int k = 0; k < 2;++k) {
                                BlockPos iteratorPos = new BlockPos(corner.add(i, k, j));
                                BlockState state = world.getBlockState(iteratorPos);

                                if (state.getBlock() instanceof OminousBoneStaffFragment) {
                                    world.setBlockState(iteratorPos, Blocks.AIR.getDefaultState());
                                }
                            }
                        }
                    }

                    LichEntity lich = (LichEntity) TGEntities.LICH.create(world);
                    BlockPos blockPos = context.getBlockPos().up();
                    lich.setYaw(result.getUp().getOpposite().asRotation());
                    lich.setBodyYaw(result.getUp().getOpposite().asRotation());
                    lich.setHeadYaw(result.getUp().getOpposite().asRotation());
                    lich.refreshPositionAndAngles((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.55D, (double)blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                    lich.onSummoned(result.getUp().getOpposite(), context.getBlockPos().up());

                    Iterator var13 = world.getNonSpectatingEntities(ServerPlayerEntity.class, lich.getBoundingBox().expand(50.0D)).iterator();

                    while(var13.hasNext()) {
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var13.next();
                        Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, lich);
                    }



                    world.spawnEntity(lich);
                    lich.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 5));

                    return ActionResult.CONSUME;
                }

                return ActionResult.success(playerEntity.world.isClient);
            }
        }

        return ActionResult.PASS;
    }
*/

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
