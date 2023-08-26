package main.java.com.lion.graveyard.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AngerableGraveyardEntity extends HordeGraveyardEntity implements Angerable {
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST;
    private static final TrackedData<Boolean> ANGRY;
    private static final TrackedData<Boolean> PROVOKED;
    private static final UniformIntProvider ANGER_TIME;
    private UUID target;
    private int ageWhenTargetSet;
    private int angerTime;

    public AngerableGraveyardEntity(EntityType<? extends HostileEntity> entityType, World world, String name) {
        super(entityType, world, name);
    }

    public void tickMovement() {
        if (!this.getEntityWorld().isClient()) {
            this.tickAngerLogic((ServerWorld)this.getEntityWorld(), true);
        }
        super.tickMovement();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(ANGRY, false);
        this.dataTracker.startTracking(PROVOKED, false);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.getEntityWorld(), nbt);
    }

    @Override
    public boolean hasAngerTime() {
        return this.dataTracker.get(ANGRY);
    }

    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    public int getAngerTime() {
        return this.angerTime;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.target;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.target = uuid;
    }

    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME.get(this.random));
    }

    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (target == null) {
            this.ageWhenTargetSet = 0;
            this.dataTracker.set(ANGRY, false);
            this.setAngerTime(0);
            this.dataTracker.set(PROVOKED, false);
            entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
        } else {
            this.ageWhenTargetSet = this.age;
            this.dataTracker.set(ANGRY, true);
            if (!entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    static {
        ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, EntityAttributeModifier.Operation.ADDITION);
        ANGRY = DataTracker.registerData(AngerableGraveyardEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        PROVOKED = DataTracker.registerData(AngerableGraveyardEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ANGER_TIME = TimeHelper.betweenSeconds(20, 39);
    }



}
