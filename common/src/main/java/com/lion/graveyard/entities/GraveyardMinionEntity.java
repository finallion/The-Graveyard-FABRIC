package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;

public class GraveyardMinionEntity extends PathfinderMob {
    protected static final EntityDataAccessor<Optional<UUID>> OWNER_UUID;
    protected static final EntityDataAccessor<Byte> TAMEABLE_FLAGS;
    private boolean sitting;

    public GraveyardMinionEntity(EntityType<? extends GraveyardMinionEntity> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(TAMEABLE_FLAGS, (byte)0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        if (this.getOwnerUuid() != null) {
            nbt.putUUID("Owner", this.getOwnerUuid());
        }

        nbt.putBoolean("Sitting", this.sitting);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        UUID ownerId;

        if (nbt.hasUUID("Owner")) {
            ownerId = nbt.getUUID("Owner");
        } else {
            String string = nbt.getString("Owner");
            ownerId = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string);
        }

        if (ownerId != null) {
            try {
                this.setOwnerUuid(ownerId);
            } catch (Throwable exception) {
                Graveyard.LOGGER.warn("Cannot set the owner {}", ownerId, exception);
            }
        }

        this.sitting = nbt.getBoolean("Sitting");
        this.setInSittingPose(this.sitting);
    }

    @Nullable
    public UUID getOwnerUuid() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setOwner(Player player) {
        this.setOwnerUuid(player.getUUID());
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;

        if (!sitting) {
            this.setInSittingPose(false);
        }
    }

    public boolean isInSittingPose() {
        return ((Byte)this.entityData.get(TAMEABLE_FLAGS) & 1) != 0;
    }

    public void setInSittingPose(boolean inSittingPose) {
        byte b = (Byte)this.entityData.get(TAMEABLE_FLAGS);

        if (inSittingPose) {
            this.entityData.set(TAMEABLE_FLAGS, (byte)(b | 1));
        } else {
            this.entityData.set(TAMEABLE_FLAGS, (byte)(b & -2));
        }
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID ownerId = this.getOwnerUuid();
            return ownerId == null ? null : this.getCommandSenderWorld().getPlayerByUUID(ownerId);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.isOwner(target) || this.sharesOwner(target) ? false : super.canAttack(target);
    }

    /// Every target path, revenge included, goes through this check.
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target != null && !this.canAttack(target) ? null : target);
    }

    private boolean sharesOwner(LivingEntity target) {
        UUID owner = this.getOwnerUuid();

        if (owner == null) {
            return false;
        }

        if (target instanceof GraveyardMinionEntity minion) {
            return owner.equals(minion.getOwnerUuid());
        }

        return target instanceof TamableAnimal tameable && owner.equals(tameable.getOwnerUUID());
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        LivingEntity livingEntity = this.getOwner();

        if (other == livingEntity) {
            return true;
        }

        if (livingEntity != null) {
            return livingEntity.isAlliedTo(other);
        }

        return super.isAlliedTo(other);
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast)) {
            if (target instanceof GraveyardMinionEntity) {
                GraveyardMinionEntity minion = (GraveyardMinionEntity)target;
                LivingEntity otherOwner = minion.getOwner();

                if (otherOwner == owner) {
                    return false;
                }

                return !(otherOwner instanceof Player otherPlayer && owner instanceof Player ownerPlayer
                        && (!this.level().getServer().isPvpAllowed() || !ownerPlayer.canHarmPlayer(otherPlayer)));
            } else if (target instanceof Player && owner instanceof Player && (!this.level().getServer().isPvpAllowed() || !((Player)owner).canHarmPlayer((Player)target))) {
                return false;
            } else if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
                return false;
            } else {
                return !(target instanceof TamableAnimal) || !((TamableAnimal)target).isTame();
            }
        } else {
            return false;
        }
    }

    static {
        TAMEABLE_FLAGS = SynchedEntityData.defineId(GraveyardMinionEntity.class, EntityDataSerializers.BYTE);
        OWNER_UUID = SynchedEntityData.defineId(GraveyardMinionEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    }

}
