package com.finallion.graveyard.entities;

import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class LichEntity extends HostileEntity implements IAnimatable {
    private AttributeContainer attributeContainer;
    private AnimationFactory factory = new AnimationFactory(this);
    private final AnimationBuilder INVIS_ANIMATION = new AnimationBuilder().addAnimation("invis", true);
    private final AnimationBuilder SPAWN_ANIMATION = new AnimationBuilder().addAnimation("spawn", false);
    protected static final int ANIMATION_SPAWN = 0;
    protected static final int ANIMATION_IDLE = 1;
    private static final TrackedData<Integer> INVUL_TIMER;
    private static final TrackedData<Integer> PHASE;
    protected static final TrackedData<Integer> ANIMATION;
    private static final int SPAWN_INVUL_TIMER = 420;
    private static final int DEFAULT_INVUL_TIMER = 200;
    private final ServerBossBar bossBar;
    private final float HEALTH_PHASE_02 = 200.0F;

    // fog effect from boss bar
    public LichEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = (ServerBossBar) (new ServerBossBar(this.getDisplayName(), BossBar.Color.WHITE, BossBar.Style.PROGRESS)).setDarkenSky(true);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (getAnimationState() == ANIMATION_SPAWN && getInvulnerableTimer() >= 0) {
            event.getController().setAnimation(SPAWN_ANIMATION);
            return PlayState.CONTINUE;
        }

        return PlayState.CONTINUE;

    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public AttributeContainer getAttributes() {
        if (attributeContainer == null) {
            attributeContainer = new AttributeContainer(HostileEntity.createHostileAttributes()
                    .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0D)
                    .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.155D)
                    .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                    .add(EntityAttributes.GENERIC_ARMOR, 3.0D)
                    .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 5.0D).build());
        }
        return attributeContainer;
    }

    @Override
    public void tickMovement() {
        randomDisplayTick(this.getWorld(), this.getBlockPos(), this.getRandom());
        super.tickMovement();
    }

    protected void mobTick() {
        int i;
        if (this.getInvulnerableTimer() > 0) {
            i = this.getInvulnerableTimer() - 1;

            int timer;
            if (getPhase() == 1) {
                timer = SPAWN_INVUL_TIMER;
            } else {
                timer = DEFAULT_INVUL_TIMER;
            }

            this.bossBar.setPercent(1.0F - (float) i / timer);
            this.setInvulTimer(i);
        } else {

            super.mobTick();

            //if (this.age % 20 == 0) {
            //    this.heal(1.0F);
            //}

            this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INVUL_TIMER, 0);
        this.dataTracker.startTracking(ANIMATION, ANIMATION_IDLE);
        this.dataTracker.startTracking(PHASE, 1);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Invul", this.getInvulnerableTimer());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setInvulTimer(nbt.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }

    }

    public void setCustomName(@Nullable Text name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    public void onSummoned(Direction direction, BlockPos altarPos) {
        this.setAnimationState(ANIMATION_SPAWN);
        applyInvulAndResetBossBar(SPAWN_INVUL_TIMER);
        playSpawnSound();
    }

    private void applyInvulAndResetBossBar(int invul) {
        this.setInvulTimer(invul);
        this.bossBar.setPercent(0.0F);
    }

    private void setHealth(int amount) {
        this.setHealth(amount);
    }

    private void playSpawnSound() {
        this.world.playSound(null, this.getBlockPos(), TGSounds.LICH_SPAWN, SoundCategory.HOSTILE, 10.0F, 1.0F);
    }

    public int getInvulnerableTimer() {
        return (Integer) this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    public int getAnimationState() {
        return this.dataTracker.get(ANIMATION);
    }

    public void setAnimationState(int state) {
        this.dataTracker.set(ANIMATION, state);
    }

    public int getPhase() {
        return (Integer) this.dataTracker.get(PHASE);
    }

    public void setPhase(int phase) {
        this.dataTracker.set(PHASE, phase);
    }


    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
        return false;
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    protected boolean canStartRiding(Entity entity) {
        return false;
    }

    public boolean canUsePortals() {
        return false;
    }

    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (this.getInvulnerableTimer() > 0 && source != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {

                if (amount > this.getHealth() && getPhase() < 3) {
                    respawn();
                    return false;
                }

                return super.damage(source, amount);
            }
        }
    }

    private void respawn() {
        this.setPhase(getPhase() + 1);
        this.clearStatusEffects();
        applyInvulAndResetBossBar(DEFAULT_INVUL_TIMER);
        setHealth(HEALTH_PHASE_02);
        //this.world.sendEntityStatus(this, (byte)35);
    }

    public void randomDisplayTick(World world, BlockPos pos, Random random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        double d = (double)i + random.nextInt(3) - random.nextInt(3);
        double e = (double)j + 4.2D;
        double f = (double)k + random.nextInt(3) - random.nextInt(3);
        world.addParticle(ParticleTypes.ASH, d, e, f, 0.0D, 0.0D, 0.0D);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if (random.nextInt(5) == 0) {
            mutable.set(i + MathHelper.nextInt(random, -10, 10), j + random.nextInt(10), k + MathHelper.nextInt(random, -10, 10));
            BlockState blockState = world.getBlockState(mutable);
            if (!blockState.isFullCube(world, mutable)) {
                world.addParticle(ParticleTypes.SMOKE, (double)mutable.getX() + random.nextDouble(), (double)mutable.getY() + random.nextDouble(), (double)mutable.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }



    static {
        INVUL_TIMER = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANIMATION = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
        PHASE = DataTracker.registerData(LichEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

}
