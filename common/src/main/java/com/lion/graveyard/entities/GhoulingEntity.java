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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationPreview;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class GhoulingEntity extends GraveyardMinionEntity implements GeoEntity, NamedScreenHandlerFactory {
    /// How often the last known position is recorded; the staff fetches at the same pace.
    public static final int BINDING_INTERVAL = 40;
    public static final int SPAWN_ANIMATION_DURATION = 68;
    private static final int PICKUP_INTERVAL = 5;
    private static final int DEATH_ANIMATION_DURATION = 44 + AnimationTable.TRANSITION_TICKS;

    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Integer> SPAWN_TIMER;
    private static final TrackedData<Integer> TELEPORT_TIMER;
    private static final TrackedData<ItemStack> STAFF;
    private static final TrackedData<Boolean> COFFIN;
    private static final TrackedData<Byte> VARIANT;

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
    protected Inventory inventory;
    private GhoulingCollectItemsGoal collectGoal;
    public boolean playAttackSound = false;
    @Nullable
    private UUID bondId;

    public GhoulingEntity(EntityType<? extends GhoulingEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new GhoulingEntity.GhoulingEscapeDangerGoal(1.5D));
        this.goalSelector.add(2, new SitGoal(this));
        this.collectGoal = new GhoulingCollectItemsGoal(this, 1.2D);
        this.goalSelector.add(4, collectGoal);
        this.goalSelector.add(5, new GhoulingMeleeAttackGoal(this, 1.5D, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createGhoulingAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.5D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.31D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }


    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(STAFF, ItemStack.EMPTY);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(COFFIN, false);
        this.dataTracker.startTracking(SPAWN_TIMER, 0);
        this.dataTracker.startTracking(TELEPORT_TIMER, 0);
        this.dataTracker.startTracking(VARIANT, (byte)0);
    }


    protected void mobTick() {
        // ATTACK TIMER
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

        if (this.age % BINDING_INTERVAL == 0 && this.getWorld() instanceof ServerWorld serverWorld) {
            GhoulingBindings.get(serverWorld).update(this);
        }

        super.mobTick();
    }

    @Override
    public void tickMovement() {
        if (isInSittingPose() && random.nextInt(5) == 0) {
            MathUtil.createParticleCircle(getEntityWorld(), this.getX(), this.getY() + 0.6D, this.getZ(), 0.0D, 0.0D, 0.0D, 1.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, getEntityWorld().random, 0.5F);
        }

        if (isAttacking() && playAttackSound) {
            playAttackSound = false;
            getEntityWorld().playSound(null, this.getBlockPos(), TGSounds.GHOULING_ATTACK.get(), SoundCategory.HOSTILE, 1.0F, -2.0F);
        }

        if (getTeleportTimer() > 0) {
            if (getTeleportTimer() == 10) {
                playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 2.0F, -10.0F);
            }

            MathUtil.createParticleCircle(getEntityWorld(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 1.5F, TGParticles.GRAVEYARD_SOUL_PARTICLE, getEntityWorld().random, 0.5F);
            MathUtil.createParticleCircle(getEntityWorld(), this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D, 1.5F, ParticleTypes.SOUL_FIRE_FLAME, getEntityWorld().random, 0.5F);
        }

        super.tickMovement();
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && getEntityWorld() != null) {
            Random random = this.getRandom();
            BlockState blockState = this.getSteppingBlockState();

            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double) MathHelper.nextBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
                    this.getEntityWorld().addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }

        super.tick();

        if (getEntityWorld().isClient()) {
            restartAttackClipOnRearm();
        }

        if (!getEntityWorld().isClient()) {
            if (getSpawnTimer() > 0) {
                setSpawnTimer(getSpawnTimer() - 1);
            }

            if (age % PICKUP_INTERVAL == 0 && canPickUpPassingItems()) {
                for (ItemEntity item : getEntityWorld().getEntitiesByClass(ItemEntity.class, getBoundingBox().expand(1.0D, 0.5D, 1.0D), item -> !item.cannotPickup() && mayTake(item))) {
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
        return item.getOwner() == null || item.getOwner().getUuid().equals(getOwnerUuid());
    }

    private boolean canPickUpPassingItems() {
        return Graveyard.getConfig().flag("ghoulingCollectsItems", true)
                && hasCoffin() && isAlive() && !isSitting() && getSpawnTimer() <= 0;
    }

    @Override
    public int getSafeFallDistance() {
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
        ItemStack stack = item.getStack();
        int before = stack.getCount();
        ItemStack rest = inventory instanceof SimpleInventory simple ? simple.addStack(stack.copy()) : stack;
        int taken = before - rest.getCount();

        if (taken <= 0) {
            return false;
        }

        sendPickup(item, taken);

        if (rest.isEmpty()) {
            item.discard();
        } else {
            item.setStack(rest);
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

        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int getAnimationState() {
        return this.dataTracker.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.dataTracker.set(ANIMATION, state);
    }

    public int getAttackAnimTimer() {
        return (Integer) this.dataTracker.get(ATTACK_ANIM_TIMER);
    }

    public void setAttackAnimTimer(int time) {
        this.dataTracker.set(ATTACK_ANIM_TIMER, time);
    }

    public int getTeleportTimer() {
        return (Integer) this.dataTracker.get(TELEPORT_TIMER);
    }

    public void setTeleportTimer(int time) {
        this.dataTracker.set(TELEPORT_TIMER, time);
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
        getEntityWorld().playSound(null, this.getBlockPos(), TGSounds.GHOULING_SPAWN.get(), SoundCategory.HOSTILE, 4.0F, 1.5F);
        getEntityWorld().playSound(null, this.getBlockPos(), TGSounds.GHOUL_ROAR.get(), SoundCategory.HOSTILE, 1.0F, -2.0F);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(AnimationTable.<GhoulingEntity>create()
                .transitionTicks(() -> getSpawnTimer() > 0 ? 0 : AnimationTable.TRANSITION_TICKS)
                .when(() -> isDead() || getHealth() < 0.01F, DEATH_ANIMATION)
                .when(() -> getSpawnTimer() > 0, SPAWN_ANIMATION)
                .startAt(SPAWN_ANIMATION, ghouling -> ghouling.getSpawnTimer() > 0 && AnimationPreview.forcedAnimation(ghouling) == null ? Math.max(0, SPAWN_ANIMATION_DURATION - ghouling.getSpawnTimer()) : 0)
                .when(() -> getAttackAnimTimer() > 0, ATTACK_ANIMATION)
                .when(state -> isAttacking() && AnimationTable.isMoving(state), RUN_ANIMATION)
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


    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        boolean client = getEntityWorld().isClient();

        if (!isOwner(player)) {
            return ActionResult.PASS;
        }

        if (itemStack.getItem() instanceof BoneStaffItem staff) {
            if (getSpawnTimer() > 0 || player.getItemCooldownManager().isCoolingDown(staff)) {
                return ActionResult.success(client);
            }

            if (BoneStaffItem.isUnbound(itemStack)) {
                if (!client) {
                    BoneStaffItem.rebind(itemStack, player, this);
                }

                return ActionResult.success(client);
            }

            if (player.isSneaking()) {
                if (hasCoffin() && !client) {
                    player.openHandledScreen(this);
                }

                return ActionResult.success(client);
            }

            if (BoneStaffItem.isBoundTo(itemStack, this) && isAlive() && !isDead()) {
                if (acceptsStaff(itemStack)) {
                    player.setCurrentHand(hand);

                    if (!client) {
                        BoneStaffItem.startDrawing(player, this);
                    }
                } else if (!client) {
                    BoneStaffItem.rejectsStaff(this, itemStack, player);
                }
            }

            return ActionResult.success(client);
        }

        if (hasCoffin() && player.isSneaking()) {
            if (!client) {
                player.openHandledScreen(this);
            }

            return ActionResult.success(client);
        }

        if (!hasCoffin() && itemStack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof SarcophagusBlock) {
            if (!client) {
                this.equipStack(EquipmentSlot.OFFHAND, new ItemStack(itemStack.getItem()));
                this.playSound(SoundEvents.BLOCK_CHEST_CLOSE, 1.0F, -5.0F);
                TGAdvancements.EQUIP_COFFIN.trigger((ServerPlayerEntity) player);

                if (inventory == null) {
                    inventory = new CoffinInventory(this, 54);
                }

                this.setHasCoffin(true);

                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }

            return ActionResult.success(client);
        }

        return super.interactMob(player, hand);
    }

    public void toggleSitting(PlayerEntity player) {
        this.playSound(TGSounds.GHOULING_GROAN.get(), 1.0F, -2.0F);
        boolean sit = !this.isSitting();
        this.setSitting(sit);
        player.sendMessage(Text.translatable(sit ? "entity.graveyard.ghouling.wait" : "entity.graveyard.ghouling.nowait"), true);
        this.jumping = false;
        this.navigation.stop();
        this.setTarget((LivingEntity) null);
    }

    public NbtCompound removeForStorage() {
        closeCoffinScreens();

        if (isLeashed()) {
            detachLeash(true, true);
        }

        stopRiding();
        NbtCompound saved = new NbtCompound();
        saveSelfNbt(saved);
        saved.remove("Pos");
        saved.remove("Motion");
        saved.remove("Staff");

        if (getWorld() instanceof ServerWorld serverWorld) {
            GhoulingBindings.get(serverWorld).remove(getUuid());
            serverWorld.spawnParticles(TGParticles.GRAVEYARD_SOUL_PARTICLE, getX(), getY() + 1.0D, getZ(), 40, 0.4D, 0.6D, 0.4D, 0.02D);
        }

        discard();
        return saved;
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;

        if (this.deathTime == DEATH_ANIMATION_DURATION && !this.getEntityWorld().isClient()) {
            this.getEntityWorld().sendEntityStatus(this, (byte) 60);
            this.remove(RemovalReason.KILLED);
        }

    }

    public int getSpawnTimer() {
        return (Integer) this.dataTracker.get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int ticks) {
        this.dataTracker.set(SPAWN_TIMER, ticks);
    }

    public byte getVariant() {
        return dataTracker.get(VARIANT);
    }

    public void setVariant(byte variant) {
        dataTracker.set(VARIANT, variant);
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

        if (getEntityWorld().isClient()) {
            NbtCompound tracked = getStaff().getNbt();
            known = tracked != null && tracked.containsUuid(BoneStaffItem.BOND_KEY) ? tracked.getUuid(BoneStaffItem.BOND_KEY) : null;
        }

        if (known == null) {
            return true;
        }

        NbtCompound nbt = staff.getNbt();
        return nbt != null && nbt.containsUuid(BoneStaffItem.BOND_KEY) && known.equals(nbt.getUuid(BoneStaffItem.BOND_KEY));
    }

    public ItemStack getStaff() {
        return dataTracker.get(STAFF);
    }

    public void setStaff(ItemStack staff) {
        dataTracker.set(STAFF, staff);
    }

    /* INVENTORY */
    public boolean hasCoffin() {
        return (Boolean) this.dataTracker.get(COFFIN);
    }

    public void setHasCoffin(boolean hasCoffin) {
        this.dataTracker.set(COFFIN, hasCoffin);
    }

    protected void dropInventory() {
        super.dropInventory();

        if (this.hasCoffin()) {
            if (!this.getEntityWorld().isClient) {
                ItemStack coffin = this.getOffHandStack().copy();

                if (!coffin.isEmpty()) {
                    this.dropStack(coffin);
                }

                this.equipStack(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

                if (this.inventory != null) {
                    closeCoffinScreens();
                    for (int i = 0; i < this.inventory.size(); i++) {
                        ItemStack stack = this.inventory.removeStack(i);

                        if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack)) {
                            this.dropStack(stack.copy());
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
        for (PlayerEntity viewer : this.getWorld().getPlayers()) {
            if (viewer instanceof ServerPlayerEntity serverViewer
                    && serverViewer.currentScreenHandler instanceof GenericContainerScreenHandler handler
                    && handler.getInventory() == this.inventory) {
                serverViewer.closeHandledScreen();
            }
        }
    }

    private static final class CoffinInventory extends SimpleInventory {
        private final GhoulingEntity owner;

        private CoffinInventory(GhoulingEntity owner, int size) {
            super(size);
            this.owner = owner;
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return owner.isAlive() && !owner.isRemoved() && owner.getWorld() == player.getWorld()
                    && player.squaredDistanceTo(owner) < 64.0D;
        }
    }

    @Override
    protected float getDropChance(EquipmentSlot slot) {
        return slot == EquipmentSlot.OFFHAND && hasCoffin() ? 0.0F : super.getDropChance(slot);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return getTeleportTimer() <= 0 && super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (getWorld() instanceof ServerWorld serverWorld) {
            GhoulingBindings.get(serverWorld).remove(this.getUuid());
        }

        super.onDeath(damageSource);
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("CoffinGhouling", this.hasCoffin());
        nbt.putByte("ghoulVariant", getVariant());

        if (this.bondId != null) {
            nbt.putUuid(BoneStaffItem.BOND_KEY, this.bondId);
        }

        if (getStaff() != null) {
            nbt.put("Staff", getStaff().writeNbt(new NbtCompound()));
        }

        if (inventory != null) {
            final NbtList inv = new NbtList();
            for (int i = 0; i < this.inventory.size(); i++) {
                inv.add(inventory.getStack(i).writeNbt(new NbtCompound()));
            }
            nbt.put("Inventory", inv);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasCoffin(nbt.getBoolean("CoffinGhouling"));
        this.setVariant(nbt.getByte("ghoulVariant"));
        this.bondId = nbt.containsUuid(BoneStaffItem.BOND_KEY) ? nbt.getUuid(BoneStaffItem.BOND_KEY) : null;

        if (nbt.contains("Staff")) {
            setStaff(ItemStack.fromNbt(nbt.getCompound("Staff")));
        }

        if (nbt.contains("Inventory")) {
            final NbtList inv = nbt.getList("Inventory", 10);
            inventory = new CoffinInventory(this, Math.max(54, inv.size()));
            for (int i = 0; i < inv.size(); i++) {
                inventory.setStack(i, ItemStack.fromNbt(inv.getCompound(i)));
            }
        }
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        ensureInventory();

        if (inventory == null) {
            return null;
        }

        return GenericContainerScreenHandler.createGeneric9x6(syncId, inv, inventory);
    }


    static {
        ATTACK_ANIM_TIMER = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SPAWN_TIMER = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        TELEPORT_TIMER = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        COFFIN = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        VARIANT = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.BYTE);
        STAFF = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }


    class GhoulingEscapeDangerGoal extends EscapeDangerGoal {
        public GhoulingEscapeDangerGoal(double speed) {
            super(GhoulingEntity.this, speed);
        }

        protected boolean isInDanger() {
            return this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
        }
    }
}
