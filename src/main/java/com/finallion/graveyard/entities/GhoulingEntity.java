package com.finallion.graveyard.entities;

import com.finallion.graveyard.entities.ai.goals.AttackWithOwnerGoal;
import com.finallion.graveyard.entities.ai.goals.FollowOwnerGoal;
import com.finallion.graveyard.entities.ai.goals.GhoulingMeleeAttackGoal;
import com.finallion.graveyard.entities.ai.goals.TrackOwnerAttackerGoal;
import com.finallion.graveyard.init.TGAdvancements;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGItems;
import com.finallion.graveyard.item.BoneStaffItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import java.util.*;


public class GhoulingEntity extends GraveyardMinionEntity implements GeoEntity, NamedScreenHandlerFactory {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Integer> SPAWN_TIMER;
    private static final TrackedData<ItemStack> STAFF;
    private static final TrackedData<Boolean> COFFIN;
    private static final TrackedData<Byte> VARIANT;
    //private static final TrackedData<Boolean> CAN_COLLECT;

    private final RawAnimation SPAWN_ANIMATION = RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    protected static final int ANIMATION_SPAWN = 0;
    protected static final int ANIMATION_IDLE = 1;
    protected static final int ANIMATION_MELEE = 2;
    protected static final int ANIMATION_WALK = 3;
    protected static final int ANIMATION_DEATH = 4;

    public final int ATTACK_ANIMATION_DURATION = 14;
    protected Inventory inventory;
    //private Goal collectGoal;
    private static final List<Item> GHOULING_HOLDABLE = new ArrayList<>();

    public GhoulingEntity(EntityType<? extends GhoulingEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new GhoulingEntity.GhoulingEscapeDangerGoal(1.5D));
        //this.goalSelector.add(3, new WanderAroundGoal(this, 1.0F));
        this.goalSelector.add(5, new GhoulingMeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createGhoulingAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0D)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.4D)
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
        this.dataTracker.startTracking(VARIANT, (byte)0);
        //this.dataTracker.startTracking(CAN_COLLECT, false);
    }


    protected void mobTick() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimationState(ANIMATION_MELEE);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.getSpawnTimer() > 0) {
            this.setSpawnTimer(getSpawnTimer() - 1);
        }

        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(0.01F);
        }

        super.mobTick();
    }

    @Override
    public void tick() {
        if (getSpawnTimer() > 0 && world != null) {
            //MinecraftClient.getInstance().particleManager.addBlockBreakParticles(this.getBlockPos().down(), world.getBlockState(this.getBlockPos().down()));
            Random random = this.getRandom();
            BlockState blockState = this.getSteppingBlockState();
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                for(int i = 0; i < 30; ++i) {
                    double d = this.getX() + (double) MathHelper.nextBetween(random, -0.7F, 0.7F);
                    double e = this.getY();
                    double f = this.getZ() + (double)MathHelper.nextBetween(random, -0.7F, 0.7F);
                    this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), d, e, f, 0.0D, 0.0D, 0.0D);
                }
            }
        }


        //if (isAlive()) {
        //    ((BoneStaffItem)getStaff().getItem()).ownerGhoulingMapping.putIfAbsent(this.getUuid(), getOwnerUuid());
        //}

        super.tick();
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

    public void onSummoned() {
        this.setAnimationState(ANIMATION_SPAWN);
        setSpawnTimer(50);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController(this, "controller", 0, event -> {
            if (getAnimationState() == ANIMATION_SPAWN && getSpawnTimer() >= 0) {
                event.getController().setAnimation(SPAWN_ANIMATION);
                return PlayState.CONTINUE;
            }

            return PlayState.CONTINUE;
        }));
        animationData.add(new AnimationController(this, "controller2", 0, event -> {

            /* DEATH */
            if (this.isDead() || this.getHealth() < 0.01) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01)) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2);
                event.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* WALK */
            if ((getAnimationState() == ANIMATION_WALK || event.isMoving()) && getAttackAnimTimer() <= 0) {
                event.getController().setAnimation(WALK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* IDLE */
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && getSpawnTimer() <= 0) {
                event.getController().setAnimation(IDLE_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* STOPPERS */
            // stops idle animation from looping
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() > 0) {
                setAnimationState(ANIMATION_MELEE);
                return PlayState.STOP;
            }

            // stops attack animation from looping
            if (getAttackAnimTimer() <= 0 && !(this.isDead() || this.getHealth() < 0.01) && getSpawnTimer() <= 0) {
                setAnimationState(ANIMATION_IDLE);
                return PlayState.STOP;
            }


            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (!this.world.isClient()) {
            if (this.hasCoffin() && player.isSneaking()) {
                player.openHandledScreen(this);
                return ActionResult.SUCCESS;
            }

            if (!itemStack.isEmpty()) {
                if (!this.hasCoffin() && GHOULING_HOLDABLE.contains(itemStack.getItem())) {
                    this.equipStack(EquipmentSlot.OFFHAND, itemStack);
                    if (inventory == null) {
                        TGAdvancements.EQUIP_COFFIN.trigger((ServerPlayerEntity) player);
                        inventory = new SimpleInventory(54);
                        this.setHasCoffin(true);
                    }
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
            }

            // bone staff stuff
        }
        return super.interactMob(player, hand);
    }

    @Override
    protected void updatePostDeath() {
        ++this.deathTime;

        if (this.deathTime == 5) {
            //playDeathSound();
        }

        if (this.deathTime == 44 && !this.world.isClient()) {
            this.world.sendEntityStatus(this, (byte) 60);
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

    public ItemStack getStaff() {
        return dataTracker.get(STAFF);
    }

    public void setStaff(ItemStack staff) {
        dataTracker.set(STAFF, staff);
    }

    /*
    public boolean canCollect() {
        return (Boolean) this.dataTracker.get(CAN_COLLECT);
    }

    public void setCanCollect(boolean canCollect) {
        this.dataTracker.set(CAN_COLLECT, canCollect);
    }

    public void setCollectGoal(Goal goal) {
        collectGoal = goal;
        this.goalSelector.add(1, collectGoal);
    }

    public void removeCollectGoal() {
        this.goalSelector.remove(collectGoal);
    }

 */

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
            if (!this.world.isClient) {
                this.dropItem(this.getOffHandStack().getItem());
                if (this.inventory != null) {
                    for (int i = 0; i < this.inventory.size(); i++) {
                        ItemStack stack = this.inventory.getStack(i);
                        if (!stack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(stack)) {
                            this.dropStack(stack);
                        }
                    }
                }
            }
            this.setHasCoffin(false);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!world.isClient()) {
            BoneStaffItem.ownerGhoulingMapping.remove(this.getUuid(), getOwnerUuid());
        }
        super.onDeath(damageSource);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("CoffinGhouling", this.hasCoffin());
        nbt.putByte("ghoulVariant", getVariant());
        //nbt.put("Ghouling", this.writeNbt(new NbtCompound()));
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
        if (nbt.contains("Staff")) {
            setStaff(ItemStack.fromNbt(nbt.getCompound("Staff")));
        }
        if (nbt.contains("Inventory")) {
            final NbtList inv = nbt.getList("Inventory", 10);
            inventory = new SimpleInventory(inv.size());
            for (int i = 0; i < inv.size(); i++) {
                inventory.setStack(i, ItemStack.fromNbt(inv.getCompound(i)));
            }
        }
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        if (inventory == null) {
            return null;
        }
        return GenericContainerScreenHandler.createGeneric9x6(syncId, inv, inventory);
    }


    static {
        GHOULING_HOLDABLE.add(TGBlocks.SARCOPHAGUS.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.OAK_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.DARK_OAK_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.SPRUCE_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.BIRCH_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.JUNGLE_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.ACACIA_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.MANGROVE_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.WARPED_COFFIN.asItem());
        GHOULING_HOLDABLE.add(TGBlocks.CRIMSON_COFFIN.asItem());

        ATTACK_ANIM_TIMER = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        SPAWN_TIMER = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.INTEGER);
        COFFIN = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        VARIANT = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.BYTE);
        STAFF = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        //CAN_COLLECT = DataTracker.registerData(GhoulingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
