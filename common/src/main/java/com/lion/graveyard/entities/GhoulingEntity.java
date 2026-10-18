package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.ai.goals.GhoulingCollectItemsGoal;
import com.lion.graveyard.entities.ai.goals.GhoulingMeleeAttackGoal;
import com.lion.graveyard.init.TGAdvancements;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGParticles;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.item.BoneStaffItem;
import com.lion.graveyard.util.MathUtil;
import com.lion.graveyard.entities.ai.goals.SitGoal;
import com.lion.graveyard.entities.ai.goals.FollowOwnerGoal;
import com.lion.graveyard.entities.ai.goals.AttackWithOwnerGoal;
import com.lion.graveyard.entities.ai.goals.TrackOwnerAttackerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class GhoulingEntity extends GraveyardMinionEntity implements GeoEntity, MenuProvider {
    public static final int BINDING_INTERVAL = 40;
    public static final int SPAWN_ANIMATION_DURATION = 68;
    private static final int PICKUP_INTERVAL = 5;
    private static final int DEATH_ANIMATION_DURATION = 44 + AnimationTable.TRANSITION_TICKS;

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> ATTACK_ANIM_TIMER;
    private static final EntityDataAccessor<Integer> ANIMATION;
    private static final EntityDataAccessor<Integer> SPAWN_TIMER;
    private static final EntityDataAccessor<Integer> TELEPORT_TIMER;
    private static final EntityDataAccessor<ItemStack> STAFF;
    private static final EntityDataAccessor<Boolean> COFFIN;
    private static final EntityDataAccessor<Byte> VARIANT;

    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation RUN_ANIMATION = RawAnimation.begin().then("run", Animation.LoopType.LOOP);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME);
    protected static final int ANIMATION_SPAWN = 0;
    protected static final int ANIMATION_IDLE = 1;
    protected static final int ANIMATION_MELEE = 2;
    protected static final int ANIMATION_WALK = 3;
    protected static final int ANIMATION_DEATH = 4;

    public final int ATTACK_ANIMATION_DURATION = 16;
    public final int ATTACK_ANIM_SYNC_TICKS = ATTACK_ANIMATION_DURATION + AnimationTable.TRANSITION_TICKS;
    private int lastAttackAnimTimer;
    protected Container inventory;
    private GhoulingCollectItemsGoal collectGoal;
    public boolean playAttackSound = false;
    @Nullable
    private UUID bondId;

    public GhoulingEntity(EntityType<? extends GhoulingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GhoulingEntity.GhoulingEscapeDangerGoal(1.5D));
        this.goalSelector.addGoal(2, new SitGoal(this));
        this.collectGoal = new GhoulingCollectItemsGoal(this, 1.2D);
        this.goalSelector.addGoal(4, collectGoal);
        this.goalSelector.addGoal(5, new GhoulingMeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.addGoal(2, new AttackWithOwnerGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder createGhoulingAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.FOLLOW_RANGE, 35.0D);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION, ANIMATION_IDLE);
        builder.define(STAFF, ItemStack.EMPTY);
        builder.define(ATTACK_ANIM_TIMER, 0);
        builder.define(COFFIN, false);
        builder.define(SPAWN_TIMER, 0);
        builder.define(TELEPORT_TIMER, 0);
        builder.define(VARIANT, (byte)0);
    }


    @Override
    protected void customServerAiStep() {
        if (this.getAttackAnimTimer() == ATTACK_ANIM_SYNC_TICKS) {
            setAnimationState(ANIMATION_MELEE);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.getTeleportTimer() > 0) {
            this.setTeleportTimer(getTeleportTimer() - 1);
        }

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(0.01F);
        }

        if (this.tickCount % BINDING_INTERVAL == 0 && this.level() instanceof ServerLevel serverWorld) {
            GhoulingBindings.get(serverWorld).update(this);
        }

        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        if (isInSittingPose() && random.nextInt(5) == 0) {
            MathUtil.createParticleCircle(getCommandSenderWorld(), this.getX(), this.getY() + 0.6D, this.getZ(), 0.0D, 0.0D, 0.0D, 1.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, getCommandSenderWorld().random, 0.5F);
        }

        if (isAggressive() && playAttackSound) {
            playAttackSound = false;
            getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.GHOULING_ATTACK.get(), SoundSource.HOSTILE, 1.0F, -2.0F);
        }

        if (getTeleportTimer() > 0) {
            if (getTeleportTimer() == 10) {
                playSound(SoundEvents.SOUL_ESCAPE.value(), 2.0F, -10.0F);
            }

            MathUtil.createParticleCircle(getCommandSenderWorld(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 1.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, getCommandSenderWorld().random, 0.5F);
            MathUtil.createParticleCircle(getCommandSenderWorld(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 1.5F, ParticleTypes.SOUL_FIRE_FLAME, getCommandSenderWorld().random, 0.5F);
        }

        super.aiStep();
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && getCommandSenderWorld() != null) {
            RandomSource random = this.getRandom();
            BlockState blockState = this.getBlockStateOn();

            if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double) Mth.randomBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)Mth.randomBetween(random, -0.7F, 0.7F);
                    this.getCommandSenderWorld().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        super.tick();

        if (getCommandSenderWorld().isClientSide()) {
            restartAttackClipOnRearm();
        }

        if (!getCommandSenderWorld().isClientSide()) {
            if (getSpawnTimer() > 0) {
                setSpawnTimer(getSpawnTimer() - 1);
            }

            if (tickCount % PICKUP_INTERVAL == 0 && canPickUpPassingItems()) {
                for (ItemEntity item : getCommandSenderWorld().getEntitiesOfClass(ItemEntity.class, getBoundingBox().inflate(1.0D, 0.5D, 1.0D), item -> !item.hasPickUpDelay() && mayTake(item))) {
                    storeInCoffin(item);
                }
            }
        }
    }

    // The controller does not restart a running clip.
    private void restartAttackClipOnRearm() {
        int timer = this.getAttackAnimTimer();

        if (timer > this.lastAttackAnimTimer + 1) {
            AnimationController<?> controller = this.getAnimatableInstanceCache().getManagerForId(this.getId()).getAnimationControllers().get("controller");

            if (controller != null) {
                controller.forceAnimationReset();
            }
        }

        this.lastAttackAnimTimer = timer;
    }

    private boolean mayTake(ItemEntity item) {
        return item.getOwner() == null || item.getOwner().getUUID().equals(getOwnerUuid());
    }

    private boolean canPickUpPassingItems() {
        return Graveyard.getConfig().flag("ghoulingCollectsItems", true)
                && hasCoffin() && isAlive() && !isSitting() && getSpawnTimer() <= 0;
    }

    @Override
    public int getMaxFallDistance() {
        return 3;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || getSpawnTimer() > 0;
    }

    public boolean storeInCoffin(ItemEntity item) {
        if (!hasCoffin() || item.isRemoved()) {
            return false;
        }

        ensureInventory();
        ItemStack stack = item.getItem();
        int before = stack.getCount();
        ItemStack rest = inventory instanceof SimpleContainer simple ? simple.addItem(stack.copy()) : stack;
        int taken = before - rest.getCount();

        if (taken <= 0) {
            return false;
        }

        take(item, taken);

        if (rest.isEmpty()) {
            item.discard();
        } else {
            item.setItem(rest);
        }

        return rest.isEmpty();
    }

    public void collect(ItemEntity item) {
        collectGoal.collect(item);
    }

    public boolean isCoffinFull() {
        ensureInventory();

        if (inventory == null) {
            return true;
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int getAnimationState() {
        return this.entityData.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.entityData.set(ANIMATION, state);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.entityData.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.entityData.set(ATTACK_ANIM_TIMER, time);
    }

    public int getTeleportTimer() {
        return (Integer) this.entityData.get(TELEPORT_TIMER);
    }

    public void setTeleportTimer(int time) {
        this.entityData.set(TELEPORT_TIMER, time);
    }

    public void prepareSpawn() {
        this.setAnimationState(ANIMATION_SPAWN);
        setSpawnTimer(SPAWN_ANIMATION_DURATION);
    }

    public void onSummoned() {
        prepareSpawn();
        playSpawnSounds();
    }

    public void playSpawnSounds() {
        getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.GHOULING_SPAWN.get(), SoundSource.HOSTILE, 4.0F, 1.5F);
        getCommandSenderWorld().playSound(null, this.blockPosition(), TGSounds.GHOUL_ROAR.get(), SoundSource.HOSTILE, 1.0F, -2.0F);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(AnimationTable.<GhoulingEntity>create()
                .transitionTicks(() -> getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS)
                .when(() -> isDeadOrDying() || getHealth() < 0.01F, DEATH_ANIMATION)
                .when(() -> getSpawnTimer() > 0, SPAWN_ANIMATION)
                .startAt(SPAWN_ANIMATION, ghouling -> ghouling.getSpawnTimer() > 0 && AnimationPreview.forcedAnimation(ghouling) == null ? Math.max(0, SPAWN_ANIMATION_DURATION - ghouling.getSpawnTimer()) : 0)
                .when(() -> getAttackAnimTimer() > 0, ATTACK_ANIMATION)
                .when(state -> isAggressive() && AnimationTable.isMoving(state), RUN_ANIMATION)
                .when(AnimationTable::isMoving, WALK_ANIMATION)
                .otherwise(IDLE_ANIMATION)
                .build(this, "controller"));
    }

    @Override
    public double getBoneResetTime() {
        return AnimationTable.TRANSITION_TICKS;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        boolean client = getCommandSenderWorld().isClientSide();

        if (!isOwner(player)) {
            return InteractionResult.PASS;
        }

        if (itemStack.getItem() instanceof BoneStaffItem staff) {
            if (getSpawnTimer() > 0 || player.getCooldowns().isOnCooldown(staff)) {
                return InteractionResult.sidedSuccess(client);
            }

            if (BoneStaffItem.isUnbound(itemStack)) {
                if (!client) {
                    BoneStaffItem.rebind(itemStack, player, this);
                }

                return InteractionResult.sidedSuccess(client);
            }

            if (player.isShiftKeyDown()) {
                if (hasCoffin() && !client) {
                    player.openMenu(this);
                }

                return InteractionResult.sidedSuccess(client);
            }

            if (BoneStaffItem.isBoundTo(itemStack, this) && isAlive() && !isDeadOrDying()) {
                if (acceptsStaff(itemStack)) {
                    player.startUsingItem(hand);

                    if (!client) {
                        BoneStaffItem.startDrawing(player, this);
                    }
                } else if (!client) {
                    BoneStaffItem.rejectsStaff(this, itemStack, player);
                }
            }

            return InteractionResult.sidedSuccess(client);
        }

        if (hasCoffin() && player.isShiftKeyDown()) {
            if (!client) {
                player.openMenu(this);
            }

            return InteractionResult.sidedSuccess(client);
        }

        if (!hasCoffin() && itemStack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SarcophagusBlock) {
            if (!client) {
                this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(itemStack.getItem()));
                this.playSound(SoundEvents.CHEST_CLOSE, 1.0F, -5.0F);
                TGAdvancements.EQUIP_COFFIN.trigger((ServerPlayer) player);

                if (inventory == null) {
                    inventory = new CoffinInventory(this, 54);
                }

                this.setHasCoffin(true);

                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(client);
        }

        return super.mobInteract(player, hand);
    }

    public void toggleSitting(Player player) {
        this.playSound(TGSounds.GHOULING_GROAN.get(), 1.0F, -2.0F);
        boolean sit = !this.isSitting();
        this.setSitting(sit);
        player.displayClientMessage(Component.translatable(sit ? "entity.graveyard.ghouling.wait" : "entity.graveyard.ghouling.nowait"), true);
        this.jumping = false;
        this.navigation.stop();
        this.setTarget((LivingEntity) null);
    }

    public CompoundTag removeForStorage() {
        closeCoffinScreens();

        if (isLeashed()) {
            dropLeash(true, true);
        }

        stopRiding();
        CompoundTag saved = new CompoundTag();
        saveAsPassenger(saved);
        saved.remove("Pos");
        saved.remove("Motion");
        saved.remove("Staff");

        if (level() instanceof ServerLevel serverWorld) {
            GhoulingBindings.get(serverWorld).remove(getUUID());
            serverWorld.sendParticles(TGParticles.GRAVEYARD_SOUL_PARTICLE, getX(), getY() + 1.0D, getZ(), 40, 0.4D, 0.6D, 0.4D, 0.02D);
        }

        discard();
        return saved;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_ANIMATION_DURATION && !this.getCommandSenderWorld().isClientSide()) {
            this.getCommandSenderWorld().broadcastEntityEvent(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }

    }

    public int getSpawnTimer() {
        return (Integer) this.entityData.get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int ticks) {
        this.entityData.set(SPAWN_TIMER, ticks);
    }

    public byte getVariant() {
        return entityData.get(VARIANT);
    }

    public void setVariant(byte variant) {
        entityData.set(VARIANT, variant);
    }

    @Nullable
    public UUID getBondId() {
        return this.bondId;
    }

    public void setBondId(@Nullable UUID bondId) {
        this.bondId = bondId;
    }

    public boolean acceptsStaff(ItemStack staff) {
        UUID known = this.bondId;

        if (getCommandSenderWorld().isClientSide()) {
            CompoundTag tracked = getStaff().getTag();
            known = tracked != null && tracked.hasUUID(BoneStaffItem.BOND_KEY) ? tracked.getUUID(BoneStaffItem.BOND_KEY) : null;
        }

        if (known == null) {
            return true;
        }

        CompoundTag nbt = staff.getTag();
        return nbt != null && nbt.hasUUID(BoneStaffItem.BOND_KEY) && known.equals(nbt.getUUID(BoneStaffItem.BOND_KEY));
    }

    public ItemStack getStaff() {
        return entityData.get(STAFF);
    }

    public void setStaff(ItemStack staff) {
        entityData.set(STAFF, staff);
    }

    public boolean hasCoffin() {
        return (Boolean) this.entityData.get(COFFIN);
    }

    public void setHasCoffin(boolean hasCoffin) {
        this.entityData.set(COFFIN, hasCoffin);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();

        if (this.hasCoffin()) {
            if (!this.getCommandSenderWorld().isClientSide) {
                ItemStack coffin = this.getOffhandItem().copy();

                if (!coffin.isEmpty()) {
                    this.spawnAtLocation(coffin);
                }

                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

                if (this.inventory != null) {
                    closeCoffinScreens();
                    for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                        ItemStack stack = this.inventory.removeItemNoUpdate(i);

                        if (!stack.isEmpty() && !EnchantmentHelper.has(stack, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
                            this.spawnAtLocation(stack.copy());
                        }
                    }
                    this.inventory = null;
                }
            }

            this.setHasCoffin(false);
        }
    }

    private void ensureInventory() {
        if (hasCoffin() && inventory == null) {
            inventory = new CoffinInventory(this, 54);
        }
    }

    private void closeCoffinScreens() {
        for (Player viewer : this.level().players()) {
            if (viewer instanceof ServerPlayer serverViewer
                    && serverViewer.containerMenu instanceof ChestMenu handler
                    && handler.getContainer() == this.inventory) {
                serverViewer.closeContainer();
            }
        }
    }

    private static final class CoffinInventory extends SimpleContainer {
        private final GhoulingEntity owner;

        private CoffinInventory(GhoulingEntity owner, int size) {
            super(size);
            this.owner = owner;
        }

        @Override
        public boolean stillValid(Player player) {
            return owner.isAlive() && !owner.isRemoved() && owner.level() == player.level()
                    && player.distanceToSqr(owner) < 64.0D;
        }
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return slot == EquipmentSlot.OFFHAND && hasCoffin() ? 0.0F : super.getEquipmentDropChance(slot);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return getTeleportTimer() <= 0 && super.causeFallDamage(fallDistance, damageMultiplier, damageSource);
    }

    @Override
    public void die(DamageSource damageSource) {
        if (level() instanceof ServerLevel serverWorld) {
            GhoulingBindings.get(serverWorld).remove(this.getUUID());
        }

        super.die(damageSource);
        this.playSound(TGSounds.GHOULING_DEATH.get(), 1.0F, -2.0F);
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.GHOULING_AMBIENT.get(), 1.0F, -1.0F);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.GHOULING_STEP.get(), 0.3F, 0.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.GHOULING_HURT.get(), 1.0F, -1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("CoffinGhouling", this.hasCoffin());
        nbt.putByte("ghoulVariant", getVariant());

        if (this.bondId != null) {
            nbt.putUUID(BoneStaffItem.BOND_KEY, this.bondId);
        }

        if (getStaff() != null) {
            nbt.put("Staff", getStaff().save(new CompoundTag()));
        }

        if (inventory != null) {
            final ListTag inv = new ListTag();
            for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                inv.add(inventory.getItem(i).save(new CompoundTag()));
            }
            nbt.put("Inventory", inv);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setHasCoffin(nbt.getBoolean("CoffinGhouling"));
        this.setVariant(nbt.getByte("ghoulVariant"));
        this.bondId = nbt.hasUUID(BoneStaffItem.BOND_KEY) ? nbt.getUUID(BoneStaffItem.BOND_KEY) : null;

        if (nbt.contains("Staff")) {
            setStaff(ItemStack.of(nbt.getCompound("Staff")));
        }

        if (nbt.contains("Inventory")) {
            final ListTag inv = nbt.getList("Inventory", 10);
            inventory = new CoffinInventory(this, Math.max(54, inv.size()));
            for (int i = 0; i < inv.size(); i++) {
                inventory.setItem(i, ItemStack.of(inv.getCompound(i)));
            }
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        ensureInventory();

        if (inventory == null) {
            return null;
        }

        return ChestMenu.sixRows(syncId, inv, inventory);
    }


    static {
        ATTACK_ANIM_TIMER = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.INT);
        ANIMATION = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.INT);
        SPAWN_TIMER = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.INT);
        TELEPORT_TIMER = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.INT);
        COFFIN = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.BOOLEAN);
        VARIANT = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.BYTE);
        STAFF = SynchedEntityData.defineId(GhoulingEntity.class, EntityDataSerializers.ITEM_STACK);
    }


    class GhoulingEscapeDangerGoal extends PanicGoal {
        public GhoulingEscapeDangerGoal(double speed) {
            super(GhoulingEntity.this, speed);
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isFreezing() || this.mob.isOnFire();
        }
    }
}
