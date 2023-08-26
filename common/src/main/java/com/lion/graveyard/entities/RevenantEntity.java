package main.java.com.lion.graveyard.entities;

import com.finallion.graveyard.TheGraveyard;
import main.java.com.lion.graveyard.entities.ai.goals.RevenantMeleeAttackGoal;
import com.finallion.graveyard.init.TGEntities;
import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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

import java.util.UUID;

public class RevenantEntity extends AngerableGraveyardEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final UUID SLOWNESS_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A4");
    private static final EntityAttributeModifier SLOWNESS_EFFECT;

    private static final TrackedData<Integer> ATTACK_ANIM_TIMER;
    private static final TrackedData<Integer> REANIMATE_ANIM_TIMER;
    private static final TrackedData<Integer> ANIMATION;
    private static final TrackedData<Boolean> CAN_REANIMATE;

    private final RawAnimation DEATH_ANIMATION = RawAnimation.begin().then("death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation FAKE_DEATH_ANIMATION = RawAnimation.begin().then("fake_death", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final RawAnimation WALK_ANIMATION = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    private final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().then("attack", Animation.LoopType.LOOP);
    private final RawAnimation RUNNING_ANIMATION = RawAnimation.begin().then("running", Animation.LoopType.LOOP);
    private final RawAnimation REANIMATE_ANIMATION = RawAnimation.begin().then("reanimate", Animation.LoopType.PLAY_ONCE);


    protected static final int ANIMATION_IDLE = 0;
    protected static final int ANIMATION_WALK = 1;
    protected static final int ANIMATION_DEATH = 2;
    protected static final int ANIMATION_RUNNING = 3;
    protected static final int ANIMATION_MELEE = 4;
    protected static final int ANIMATION_REANIMATE = 5;
    protected static final int ANIMATION_FAKE_DEATH = 6; // necessary because anim can't be hold on last frame

    public final int ATTACK_ANIMATION_DURATION = 9;
    public final int REANIMATE_DURATION = 120; // duration reanimation anim: 40 + duration death animation: 30 + waiting time: 30

    public RevenantEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world, "revenant");
    }


    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(ATTACK_ANIM_TIMER, 0);
        this.dataTracker.startTracking(REANIMATE_ANIM_TIMER, 0);
        this.dataTracker.startTracking(CAN_REANIMATE, true);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.goalSelector.add(2, new RevenantMeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0D));
        this.targetSelector.add(1, new RevengeGoal(this, LichEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    public static DefaultAttributeContainer.Builder createRevenantAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.5D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.155D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 25.0D);
    }



    protected void mobTick() {
        // ATTACK TIMER
        if (this.getAttackAnimTimer() == ATTACK_ANIMATION_DURATION) {
            setAnimationState(ANIMATION_MELEE);
        }

        // REANIMATE TIMER
        if (this.getReanimateAnimTimer() > 40) {
            setAnimationState(ANIMATION_FAKE_DEATH);
        }

        if (this.getReanimateAnimTimer() == 40) {
            setAnimationState(ANIMATION_REANIMATE);
        }

        if (this.getAttackAnimTimer() > 0) {
            int animTimer = this.getAttackAnimTimer() - 1;
            this.setAttackAnimTimer(animTimer);
        }

        if (this.getReanimateAnimTimer() > 0) {
            int animTimer = this.getReanimateAnimTimer() - 1;
            this.setReanimateAnimTimer(animTimer);
        }

        super.mobTick();
    }

    @Override
    public void tickMovement() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (getReanimateAnimTimer() > 0) {
            this.getLookControl().lookAt(this.getX(), this.getY(), this.getZ());
            if (!entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.addTemporaryModifier(SLOWNESS_EFFECT);
            }
        } else {
            if (entityAttributeInstance.hasModifier(SLOWNESS_EFFECT)) {
                entityAttributeInstance.removeModifier(SLOWNESS_EFFECT);
            }
        }

        super.tickMovement();
    }


    @Override
    protected void updatePostDeath() {
        ++this.deathTime;
        if (this.deathTime == 30 && !this.getEntityWorld().isClient()) {
            this.getEntityWorld().sendEntityStatus(this, (byte)60);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE) || source.isIn(DamageTypeTags.IS_FIRE)) {
            setCanReanimate(false);
            setReanimateAnimTimer(0);
        }

        if (amount >= getHealth() && canReanimate() && random.nextInt(4) == 0) {
            setCanReanimate(false);
            setReanimateAnimTimer(REANIMATE_DURATION);
            return false;
        }

        if (getReanimateAnimTimer() > 0) { // negate damage while reanimating
            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController(this, "controller", 0, event -> {
            /* DEATH */
            if (this.isDead() || this.getHealth() < 0.01) {
                event.getController().setAnimation(DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* REANIMATION */
            if (getAnimationState() == ANIMATION_FAKE_DEATH && getReanimateAnimTimer() > 39) { // one tick delay
                event.getController().setAnimation(FAKE_DEATH_ANIMATION);
                return PlayState.CONTINUE;
            }

            if (getAnimationState() == ANIMATION_REANIMATE && getReanimateAnimTimer() == 39) { // one tick delay
                event.getController().setAnimation(REANIMATE_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* ATTACK */
            // takes one tick to get to this method (from mobtick)
            if (getAnimationState() == ANIMATION_MELEE && getAttackAnimTimer() == (ATTACK_ANIMATION_DURATION - 1) && isAttacking() && !(this.isDead() || this.getHealth() < 0.01) && getReanimateAnimTimer() <= 0) {
                setAttackAnimTimer(ATTACK_ANIMATION_DURATION - 2);
                event.getController().setAnimation(ATTACK_ANIMATION);
                return PlayState.CONTINUE;
            }

            /* WALK */
            if (((getAnimationState() == ANIMATION_WALK || event.isMoving()) && getAttackAnimTimer() <= 0) && getReanimateAnimTimer() <= 0) {
                if (isAttacking() && !isWet()) {
                    event.getController().setAnimation(RUNNING_ANIMATION);
                } else {
                    event.getController().setAnimation(WALK_ANIMATION);
                }
                return PlayState.CONTINUE;
            }

            /* IDLE */
            if (getAnimationState() == ANIMATION_IDLE && getAttackAnimTimer() <= 0 && !event.isMoving() && getReanimateAnimTimer() <= 0) {
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
            if (getAttackAnimTimer() <= 0 && !(this.isDead() || this.getHealth() < 0.01) && getAnimationState() != ANIMATION_REANIMATE) {
                setAnimationState(ANIMATION_IDLE);
                return PlayState.STOP;
            }

            return PlayState.CONTINUE;
        }));
    }


    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.REVENANT_AMBIENT, 1.0F, 1.0F);
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.playSound(TGSounds.REVENANT_HURT, 1.0F, 1.0F);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.playSound(TGSounds.REVENANT_DEATH, 1.0F, 1.0F);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(TGSounds.REVENANT_STEP, 0.15F, 1.0F);
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

    public int getReanimateAnimTimer() {
        return (Integer) this.dataTracker.get(REANIMATE_ANIM_TIMER);
    }

    public void setReanimateAnimTimer(int time) {
        this.dataTracker.set(REANIMATE_ANIM_TIMER, time);
    }

    public boolean canReanimate() {
        return this.dataTracker.get(CAN_REANIMATE);
    }

    public void setCanReanimate(boolean bool) {
        this.dataTracker.set(CAN_REANIMATE, bool);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("canReanimate", canReanimate());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setCanReanimate(nbt.getBoolean("canReanimate"));
    }

    static {
        ATTACK_ANIM_TIMER = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
        REANIMATE_ANIM_TIMER = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CAN_REANIMATE = DataTracker.registerData(RevenantEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        SLOWNESS_EFFECT = new EntityAttributeModifier(SLOWNESS_ID, "Slowness effect", -0.3D, EntityAttributeModifier.Operation.ADDITION);
    }


}
