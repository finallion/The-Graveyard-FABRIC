package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.goals.FlyOntoPerchGoal;
import com.lion.graveyard.entities.ai.goals.HopAwayGoal;
import com.lion.graveyard.entities.ai.goals.HopGoal;
import com.lion.graveyard.entities.ai.goals.PickTreasureGoal;
import com.lion.graveyard.entities.ai.goals.StartleGoal;
import com.lion.graveyard.entities.ai.goals.WalkGoal;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.init.TGTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class MagpieEntity extends CorvidEntity {

    private boolean hopping;
    private boolean walking;

    public MagpieEntity(EntityType<? extends CorvidEntity> type, Level world) {
        super(type, world);
        this.setCanPickUpLoot(true);
    }

    @Override
    public Species getSpecies() {
        return Species.MAGPIE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StartleGoal(this, 3.5D));
        this.goalSelector.addGoal(2, new HopAwayGoal(this));
        this.goalSelector.addGoal(3, new PickTreasureGoal(this));
        this.goalSelector.addGoal(4, new FlyOntoPerchGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new HopGoal(this));
        this.goalSelector.addGoal(5, new WalkGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isHopping() {
        return this.hopping;
    }

    public void setHopping(boolean hopping) {
        this.hopping = hopping;
    }

    @Override
    protected boolean canWalk() {
        return true;
    }

    @Override
    public boolean isWalking() {
        return this.walking;
    }

    public void setWalking(boolean walking) {
        this.walking = walking;
    }

    @Override
    public boolean wantsToPickUp(ServerLevel serverLevel, ItemStack stack) {
        return Graveyard.getConfig().flag("corvidsStealItems", true)
                && stack.is(TGTags.MAGPIE_TREASURES) && this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
    }

    @Override
    protected void pickUpItem(ServerLevel serverLevel, ItemEntity item) {
        ItemStack stack = item.getItem();

        if (this.wantsToPickUp(serverLevel, stack)) {
            ItemStack taken = stack.split(1);
            this.setItemSlot(EquipmentSlot.MAINHAND, taken);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.setPersistenceRequired();
            this.take(item, 1);
            this.onItemPickup(item);
        }

        if (stack.isEmpty()) {
            item.discard();
        } else {
            // The DataTracker syncs only a new instance, not a changed one.
            item.setItem(stack.copy());
        }
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(serverLevel, source, amount);

        if (hurt && !this.level().isClientSide() && !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            if (source.getEntity() instanceof ServerPlayer player) {
                TGAdvancements.MAGPIE_LOOT.trigger(player);
            }

            this.spawnAtLocation(serverLevel, this.getItemBySlot(EquipmentSlot.MAINHAND));
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        return hurt;
    }
}
