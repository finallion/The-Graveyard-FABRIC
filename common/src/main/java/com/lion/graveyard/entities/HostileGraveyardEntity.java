package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class HostileGraveyardEntity extends Monster {
    private String name;
    private static final EntityDataAccessor<Boolean> CAN_BURN_IN_SUNLIGHT;

    public HostileGraveyardEntity(EntityType<? extends Monster> entityType, Level world, String name) {
        super(entityType, world);
        this.name = name;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CAN_BURN_IN_SUNLIGHT, true);
        super.defineSynchedData(builder);
    }

    @Override
    protected boolean isSunBurnTick() {
        return super.isSunBurnTick();
    }

    protected boolean burnsInDaylight() {
        return true;
    }

    private boolean canBurnInSunlight() {
        return entityData.get(CAN_BURN_IN_SUNLIGHT);
    }

    public void setCanBurnInSunlight(boolean bool) {
        entityData.set(CAN_BURN_IN_SUNLIGHT, bool);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putBoolean("canBurn", canBurnInSunlight());
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (!nbt.contains("canBurn")) {
            this.setCanBurnInSunlight(canBurnInSunlight());
        } else {
            this.setCanBurnInSunlight(nbt.getBoolean("canBurn"));
        }

        super.readAdditionalSaveData(nbt);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.is(MobEffects.WITHER)) {
            return Graveyard.getConfig().mobConfigEntries.get(name).canBeWithered;
        }

        return super.canBeAffected(effect);
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean burning = this.burnsInDaylight() && this.isSunBurnTick() && Graveyard.getConfig().mobConfigEntries.get(name).canBurnInSunlight && canBurnInSunlight();

            if (burning) {
                ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);

                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageableItem()) {
                        itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));

                        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    burning = false;
                }

                if (burning) {
                    this.setSecondsOnFire(8);
                }
            }
        }

        super.aiStep();
    }


    public static boolean canSpawnInDarkness(EntityType<? extends Monster> type, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (world.getBiome(pos).is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
            return false;
        }

        return Monster.isDarkEnoughToSpawn(world, pos, random);
    }

    public static boolean canSpawnInLight(EntityType<? extends Monster> type, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, net.minecraft.util.RandomSource random) {
        return Monster.checkAnyLightMonsterSpawnRules(type, world, spawnReason, pos, random);
    }

    static {
        CAN_BURN_IN_SUNLIGHT = SynchedEntityData.defineId(HostileGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
    }



}
