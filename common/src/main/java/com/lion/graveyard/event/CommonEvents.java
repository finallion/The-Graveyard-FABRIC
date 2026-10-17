package com.lion.graveyard.event;

import com.lion.graveyard.blocks.SmolderingPillarBlock;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.entities.ai.goals.FleeGraveyardMobsGoal;
import com.lion.graveyard.item.BoneStaffItem;
import com.lion.graveyard.mixin.MobEntityAccessor;
import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.util.CrowAnchors;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public final class CommonEvents {

    private CommonEvents() {
    }

    public static void onEntityLoad(Entity entity) {
        if (entity.getWorld().isClient) {
            return;
        }

        if (entity instanceof VillagerEntity villager) {
            addFleeGoal(villager, 0.7D, 1.0D);
        } else if (entity instanceof WanderingTraderEntity trader) {
            addFleeGoal(trader, 0.5D, 0.5D);
        } else if (entity instanceof ItemEntity item && item.getStack().getItem() instanceof BoneStaffItem
                && item.getStack().hasNbt() && item.getStack().getNbt().contains("StoredGhouling")) {
            item.setNeverDespawn();
            item.setInvulnerable(true);
        }
    }

    private static void addFleeGoal(MerchantEntity merchant, double slowSpeed, double fastSpeed) {
        GoalSelector goals = ((MobEntityAccessor) merchant).graveyard$getGoalSelector();
        // The same entity instance loads again when its chunk becomes tracked again.
        if (goals.getGoals().stream()
                .noneMatch(goal -> goal.getGoal() instanceof FleeGraveyardMobsGoal)) {
            goals.add(1, new FleeGraveyardMobsGoal(merchant, slowSpeed, fastSpeed));
        }
    }

    public static void onDeath(LivingEntity entity) {
        if (entity instanceof MobEntity && !(entity instanceof CorvidEntity)) {
            CrowAnchors.recordDeath(entity.getWorld(), entity.getBlockPos());
        }
    }

    public static void onUseItem(PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player && !player.isSpectator()) {
            findPage(player, player.getStackInHand(hand));
        }
    }

    /// A lectern book is read without a use in the hand. Opening the lectern marks its page as found.
    public static void onUseBlock(PlayerEntity user, World world, BlockPos pos) {
        if (user instanceof ServerPlayerEntity player && usesBlock(player)
                && world.getBlockEntity(pos) instanceof LecternBlockEntity lectern && lectern.hasBook()) {
            findPage(player, lectern.getBook());
        }
    }

    private static boolean usesBlock(PlayerEntity player) {
        boolean holdsItem = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        return player.isSpectator() || !(player.shouldCancelInteraction() && holdsItem);
    }

    private static void findPage(ServerPlayerEntity player, ItemStack book) {
        String page = Chronicle.pageOf(book);

        if (page != null) {
            Chronicle.find(player, page);
        }
    }

    @Nullable
    public static BlockState smolderingStrippedState(BlockState state) {
        return state.getBlock() instanceof SmolderingPillarBlock pillar ? pillar.strippedState(state) : null;
    }

    /// Strips axes on loaders without a tool-modification event.
    public static ActionResult stripSmolderingPillar(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        ItemStack stack = player.getStackInHand(hand);
        BlockState stripped = smolderingStrippedState(world.getBlockState(pos));

        if (stripped == null || !(stack.getItem() instanceof AxeItem) || player.isSpectator() || shieldTakesPriority(player, hand) || !mayModify(player, world, pos, stack)) {
            return ActionResult.PASS;
        }

        world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!world.isClient) {
            world.setBlockState(pos, stripped, 11);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, stripped));

            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
            }

            stack.damage(1, player, user -> user.sendToolBreakStatus(hand));
        }

        return ActionResult.success(world.isClient);
    }

    private static boolean shieldTakesPriority(PlayerEntity player, Hand hand) {
        return hand == Hand.MAIN_HAND && player.getOffHandStack().isOf(Items.SHIELD) && !player.shouldCancelInteraction();
    }

    private static boolean mayModify(PlayerEntity player, World world, BlockPos pos, ItemStack stack) {
        return player.getAbilities().allowModifyWorld
                || stack.canPlaceOn(world.getRegistryManager().get(RegistryKeys.BLOCK), new CachedBlockPosition(world, pos, false));
    }
}
