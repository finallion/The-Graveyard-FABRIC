package com.lion.graveyard.event;

import com.lion.graveyard.blocks.SmolderingPillarBlock;
import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.entities.ai.goals.FleeGraveyardMobsGoal;
import com.lion.graveyard.item.BoneStaffItem;
import com.lion.graveyard.item.LegacyItemData;
import com.lion.graveyard.mixin.MobEntityAccessor;
import com.lion.graveyard.entities.CorvidEntity;
import com.lion.graveyard.util.CrowAnchors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class CommonEvents {

    private CommonEvents() {}

    public static void onEntityLoad(Entity entity) {
        if (entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof Villager villager) {
            addFleeGoal(villager, 0.7D, 1.0D);
        } else if (entity instanceof WanderingTrader trader) {
            addFleeGoal(trader, 0.5D, 0.5D);
        } else if (entity instanceof ItemEntity item && item.getItem().getItem() instanceof BoneStaffItem
                && LegacyItemData.isStored(item.getItem())) {
            item.setUnlimitedLifetime();
            item.setInvulnerable(true);
        }
    }

    private static void addFleeGoal(AbstractVillager merchant, double slowSpeed, double fastSpeed) {
        GoalSelector goals = ((MobEntityAccessor) merchant).graveyard$getGoalSelector();
        // The same entity instance loads again when its chunk becomes tracked again.
        if (goals.getAvailableGoals().stream()
                .noneMatch(goal -> goal.getGoal() instanceof FleeGraveyardMobsGoal)) {
            goals.addGoal(1, new FleeGraveyardMobsGoal(merchant, slowSpeed, fastSpeed));
        }
    }

    public static void onDeath(LivingEntity entity) {
        if (entity instanceof Mob && !(entity instanceof CorvidEntity)) {
            CrowAnchors.recordDeath(entity.level(), entity.blockPosition());
        }
    }

    public static void onUseItem(Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player && !player.isSpectator()) {
            findPage(player, player.getItemInHand(hand));
        }
    }

    /// A lectern book is read without a use in the hand. Opening the lectern marks its page as found.
    public static void onUseBlock(Player user, Level world, BlockPos pos) {
        if (user instanceof ServerPlayer player && usesBlock(player)
                && world.getBlockEntity(pos) instanceof LecternBlockEntity lectern && lectern.hasBook()) {
            findPage(player, lectern.getBook());
        }
    }

    private static boolean usesBlock(Player player) {
        boolean holdsItem = !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
        return player.isSpectator() || !(player.isSecondaryUseActive() && holdsItem);
    }

    private static void findPage(ServerPlayer player, ItemStack book) {
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
    public static InteractionResult stripSmolderingPillar(
            Player player,
            Level world,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        BlockPos pos = hit.getBlockPos();
        ItemStack stack = player.getItemInHand(hand);
        BlockState stripped = smolderingStrippedState(world.getBlockState(pos));

        if (stripped == null || !(stack.getItem() instanceof AxeItem) || player.isSpectator() || shieldTakesPriority(player, hand) || !mayModify(player, world, pos, stack)) {
            return InteractionResult.PASS;
        }

        world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!world.isClientSide()) {
            world.setBlock(pos, stripped, 11);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, stripped));

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
            }

            stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean shieldTakesPriority(Player player, InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND && player.getOffhandItem().is(Items.SHIELD) && !player.isSecondaryUseActive();
    }

    private static boolean mayModify(Player player, Level world, BlockPos pos, ItemStack stack) {
        return player.getAbilities().mayBuild
                || stack.canPlaceOnBlockInAdventureMode(new BlockInWorld(world, pos, false));
    }
}
