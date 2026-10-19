package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    protected boolean isSunBurnTick() {
        if (this.level().isClientSide()) {
            return false;
        }

        if (!this.level().environmentAttributes().getValue(EnvironmentAttributes.MONSTERS_BURN, this.position())) {
            return false;
        }

        float brightness = this.getLightLevelDependentMagicValue();
        BlockPos eyePos = BlockPos.containing(this.getX(), this.getEyeY(), this.getZ());
        boolean wet = this.isInWaterOrRain() || this.isInPowderSnow || this.wasInPowderSnow;

        return brightness > 0.5F
                && this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && !wet
                && this.level().canSeeSky(eyePos);
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
    public void addAdditionalSaveData(ValueOutput output) {
        output.putBoolean("canBurn", canBurnInSunlight());
        super.addAdditionalSaveData(output);
        LegacyEntityData.write(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        input.read("canBurn", Codec.BOOL).ifPresent(this::setCanBurnInSunlight);
        super.readAdditionalSaveData(input);
        LegacyEntityData.read(this, input);
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
                            this.onEquippedItemBroken(itemStack.getItem(), EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    burning = false;
                }

                if (burning) {
                    this.igniteForSeconds(8.0F);
                }
            }
        }

        super.aiStep();
    }


    public static boolean canSpawnInDarkness(
            EntityType<? extends Monster> type,
            ServerLevelAccessor world,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            net.minecraft.util.RandomSource random
    ) {
        if (!world.environmentAttributes().getValue(EnvironmentAttributes.CAN_PILLAGER_PATROL_SPAWN, pos)) {
            return false;
        }

        return Monster.isDarkEnoughToSpawn(world, pos, random);
    }

    public static boolean canSpawnInLight(
            EntityType<? extends Monster> type,
            ServerLevelAccessor world,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            net.minecraft.util.RandomSource random
    ) {
        return Monster.checkAnyLightMonsterSpawnRules(type, world, spawnReason, pos, random);
    }

    static {
        CAN_BURN_IN_SUNLIGHT = SynchedEntityData.defineId(HostileGraveyardEntity.class, EntityDataSerializers.BOOLEAN);
    }



}
