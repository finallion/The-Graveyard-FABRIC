package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.trades.NamelessHangedTrades;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NamelessHangedEntity extends MerchantEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final String OFFERS_DAY_KEY = "OffersDay";
    private long offersDay = -1L;
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);

    public NamelessHangedEntity(EntityType<? extends NamelessHangedEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new LookAtCustomerGoal(this));
        this.goalSelector.add(9, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
    }

    public static DefaultAttributeContainer.Builder createNamelessHangedAttributes() {
        return PassiveEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 0.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 45.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(AnimationTable.<NamelessHangedEntity>create()
                .otherwise(IDLE_ANIMATION)
                .build(this, "controller"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public boolean canTakeDamage() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    protected void pushAway(Entity entity) {
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getEntityWorld().isClient && !this.isRemoved()) {
            if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.remove(RemovalReason.DISCARDED);
                return true;
            }
        }
        return false;
    }

    public boolean isImmuneToExplosion() {
        return true;
    }

    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
    }

    public boolean isAffectedBySplashPotions() {
        return false;
    }

    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public boolean isLeveledMerchant() {
        return false;
    }

    public void tickMovement() {
        if (this.getEntityWorld().isClient) {
            this.getEntityWorld().addParticle(ParticleTypes.ASH, this.getParticleX(0.5D), this.getY() + 1.75D, this.getParticleZ(0.5D), 0, 0, 0);
        }

        super.tickMovement();
    }

    @Override
    public void tick() {
        if (getEntityWorld().isDay() && this.offers != null) {
            this.offers = null;
        }

        super.tick();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putLong(OFFERS_DAY_KEY, this.offersDay);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.offersDay = nbt.contains(OFFERS_DAY_KEY) ? nbt.getLong(OFFERS_DAY_KEY) : -1L;
    }

    private long currentDay() {
        return this.getEntityWorld().getTimeOfDay() / 24000L;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.offers != null && this.offersDay != currentDay() && !this.hasCustomer()) {
            this.offers = null;
        }

        if (getEntityWorld().isDay() && !this.getEntityWorld().isClient) {
            player.sendMessage(Text.translatable("entity.graveyard.nameless_hanged.wait"), true);
            getEntityWorld().playSound(null, player.getBlockPos(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundCategory.HOSTILE, 0.5F, 1.0F);
        }

        if (this.isAlive() && !this.hasCustomer() && getEntityWorld().isNight()) {
            if (!this.getOffers().isEmpty()) {
                if (!this.getEntityWorld().isClient) {
                    this.setCustomer(player);
                    this.sendOffers(player, this.getDisplayName(), 1);
                    getEntityWorld().playSound(null, player.getBlockPos(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundCategory.HOSTILE, 0.5F, 0.8F);
                }
            }
            return ActionResult.success(this.getEntityWorld().isClient);
        } else {
            return super.interactMob(player, hand);
        }
    }

    protected void fillRecipes() {
        List<TradeOffers.Factory> factorys = NamelessHangedTrades.TRADES_REGISTRY;
        TradeOfferList tradeOfferList = this.getOffers();
        GraveyardConfig config = Graveyard.getConfig();
        int minOffers = Math.max(1, (int) config.number("namelessHangedMinOffers", 7.0D));
        int maxOffers = Math.max(minOffers, (int) config.number("namelessHangedMaxOffers", 9.0D));
        int offers = minOffers + random.nextInt(maxOffers - minOffers + 1);
        this.offersDay = currentDay();
        this.fillRecipesFromPool(tradeOfferList, factorys.toArray(new TradeOffers.Factory[0]), offers);
    }

    protected void afterUsing(TradeOffer offer) {
        if (offer.shouldRewardPlayerExperience()) {
            int i = 3 + this.random.nextInt(4);
            this.getEntityWorld().spawnEntity(new ExperienceOrbEntity(this.getEntityWorld(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.NAMELESS_HANGED_AMBIENT.get(), 0.3F, 1.0F);

        if (this.random.nextBoolean()) {
            this.playSound(TGSounds.NAMELESS_HANGED_BREATH.get(), 0.5F, 1.0F);
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.0F;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.PARTICLE_SOUL_ESCAPE;
    }

    @Override
    protected SoundEvent getTradingSound(boolean sold) {
        return SoundEvents.PARTICLE_SOUL_ESCAPE;
    }


}
