package main.java.com.lion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class HostileGraveyardEntity extends HostileEntity {
    private String name;
    private static final TrackedData<Boolean> CAN_BURN_IN_SUNLIGHT;

    public HostileGraveyardEntity(EntityType<? extends HostileEntity> entityType, World world, String name) {
        super(entityType, world);
        this.name = name;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(CAN_BURN_IN_SUNLIGHT, true);
        super.initDataTracker();
    }

    @Override
    protected boolean isAffectedByDaylight() {
        return super.isAffectedByDaylight();
    }

    protected boolean burnsInDaylight() {
        return true;
    }

    private boolean canBurnInSunlight() {
        return dataTracker.get(CAN_BURN_IN_SUNLIGHT);
    }

    public void setCanBurnInSunlight(boolean bool) {
        dataTracker.set(CAN_BURN_IN_SUNLIGHT, bool);
    }


    // on game stop
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("canBurn", canBurnInSunlight());
        super.writeCustomDataToNbt(nbt);
    }

    // on game load
    public void readCustomDataFromNbt(NbtCompound nbt) {
        if (!nbt.contains("canBurn")) {
            this.setCanBurnInSunlight(canBurnInSunlight());
        } else {
            this.setCanBurnInSunlight(nbt.getBoolean("canBurn"));
        }

        super.readCustomDataFromNbt(nbt);
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.WITHER) {
            if (TheGraveyard.config.mobConfigEntries.get(name).canBeWithered) {
                return true;
            } else {
                return false;
            }
        }

        return super.canHaveStatusEffect(effect);
    }

    @Override
    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight() && TheGraveyard.config.mobConfigEntries.get(name).canBurnInSunlight && canBurnInSunlight();
            if (bl) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }
        super.tickMovement();
    }


    public static boolean canSpawnInDarkness(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
        return HostileEntity.isSpawnDark(world, pos, random);
    }

    public static boolean canSpawnInLight(EntityType<? extends HostileEntity> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, net.minecraft.util.math.random.Random random) {
        return HostileEntity.canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
    }

    static {
        CAN_BURN_IN_SUNLIGHT = DataTracker.registerData(HostileGraveyardEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }



}
