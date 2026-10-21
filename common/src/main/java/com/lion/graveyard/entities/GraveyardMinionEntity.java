package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GraveyardMinionEntity extends PathfinderMob {
    protected static final EntityDataAccessor<String> OWNER_UUID;
    protected static final EntityDataAccessor<Byte> TAMEABLE_FLAGS;
    private boolean sitting;

    public GraveyardMinionEntity(EntityType<? extends GraveyardMinionEntity> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.ON_TOP_OF_POWDER_SNOW, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, "");
        builder.define(TAMEABLE_FLAGS, (byte)0);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        if (this.getOwnerUuid() != null) {
            output.store("Owner", UUIDUtil.CODEC, this.getOwnerUuid());
        }

        output.putBoolean("Sitting", this.sitting);
        LegacyEntityData.write(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        UUID ownerId = input.read("Owner", UUIDUtil.CODEC).orElse(null);

        if (ownerId == null) {
            ownerId = OldUsersConverter.convertMobOwnerIfNecessary(this.level().getServer(), input.getStringOr("Owner", ""));
        }

        if (ownerId != null) {
            try {
                this.setOwnerUuid(ownerId);
            } catch (Throwable exception) {
                Graveyard.LOGGER.warn("Cannot set the owner {}", ownerId, exception);
            }
        }

        this.sitting = input.getBooleanOr("Sitting", false);
        this.setInSittingPose(this.sitting);
        LegacyEntityData.read(this, input);
    }

    @Nullable
    public UUID getOwnerUuid() {
        String stored = this.entityData.get(OWNER_UUID);

        if (stored.isEmpty()) {
            return null;
        }

        try {
            return UUID.fromString(stored);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, uuid == null ? "" : uuid.toString());
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
            return ownerId == null ? null : this.level().getPlayerByUUID(ownerId);
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

        return target instanceof TamableAnimal tameable && tameable.getOwnerReference() != null && owner.equals(tameable.getOwnerReference().getUUID());
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    protected boolean considersEntityAsAlly(Entity other) {
        LivingEntity livingEntity = this.getOwner();

        if (other == livingEntity) {
            return true;
        }

        if (livingEntity != null) {
            return livingEntity.isAlliedTo(other.getTeam());
        }

        return super.considersEntityAsAlly(other);
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
                        && (!this.isPvpAllowed() || !ownerPlayer.canHarmPlayer(otherPlayer)));
            } else if (target instanceof Player && owner instanceof Player && (!this.isPvpAllowed() || !((Player)owner).canHarmPlayer((Player)target))) {
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

    private boolean isPvpAllowed() {
        return this.level() instanceof ServerLevel serverLevel && serverLevel.isPvpAllowed();
    }

    static {
        TAMEABLE_FLAGS = SynchedEntityData.defineId(GraveyardMinionEntity.class, EntityDataSerializers.BYTE);
        OWNER_UUID = SynchedEntityData.defineId(GraveyardMinionEntity.class, EntityDataSerializers.STRING);
    }

}
