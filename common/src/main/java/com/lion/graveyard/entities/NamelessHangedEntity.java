package com.lion.graveyard.entities;

import com.lion.graveyard.init.TGSounds;;
import com.lion.graveyard.trades.TradeOfferManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NamelessHangedEntity extends AbstractVillager implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);

    public NamelessHangedEntity(EntityType<? extends NamelessHangedEntity> entityType, Level world) {
        super(entityType, world);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
    }

    public static AttributeSupplier.Builder createNamelessHangedAttributes() {
        return PathfinderMob.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 0.0D)
                .add(Attributes.MAX_HEALTH, 45.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animationData) {
        animationData.add(new AnimationController<>(this, "controller", 0, event -> {
            event.getController().setAnimation(IDLE_ANIMATION);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity p_21294_) {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && !this.isRemoved()) {
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.remove(RemovalReason.DISCARDED);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void thunderHit(ServerLevel p_19927_, LightningBolt p_19928_) {
    }

    @Nullable
    public AgeableMob getBreedOffspring(ServerLevel p_150046_, AgeableMob p_150047_) {
        return null;
    }

    public boolean showProgressBar() {
        return false;
    }


    public void aiStep() {
        if (this.level().isClientSide) {
            this.level().addParticle(ParticleTypes.ASH, this.getRandomX(0.5D), this.getY() + 1.75D, this.getRandomZ(0.5D), 0, 0, 0);
        }

        super.aiStep();
    }

    @Override
    public void tick() {
        if (level().isDay() && this.offers != null) {
            this.offers = null;
        }

        super.tick();
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isDay() && !this.level().isClientSide) {
            player.displayClientMessage(Component.translatable("entity.graveyard.nameless_hanged.wait"), true);
            if (!this.isSilent()) level().playSound(null, player.blockPosition(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundSource.HOSTILE, 0.5F, 1.0F);
            //player.playSound(TGSounds.NAMELESS_HANGED_INTERACT, 1.0F, 1.0F);
        }

        if (this.isAlive() && !this.isTrading() && level().isNight()) {
            if (!this.getOffers().isEmpty()) {
                if (!this.level().isClientSide) {
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 1);
                    if (!this.isSilent()) level().playSound(null, player.blockPosition(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundSource.HOSTILE, 0.5F, 0.8F);
                    //player.playSound(TGSounds.NAMELESS_HANGED_INTERACT, 1.0F, 1.0F);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    protected void updateTrades() {
        List<VillagerTrades.ItemListing> factorys = TradeOfferManager.TRADES_REGISTRY;
        MerchantOffers tradeOfferList = this.getOffers();
        int offers = random.nextInt(3) + 7;
        this.addOffersFromItemListings(tradeOfferList, factorys.toArray(new VillagerTrades.ItemListing[0]), offers);
    }

    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    public boolean removeWhenFarAway(double distanceSquared) {
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
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.0F;
    }

    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.SOUL_ESCAPE;
    }

    protected SoundEvent getTradeUpdatedSound(boolean p_35890_) {
        return SoundEvents.SOUL_ESCAPE;
    }


}
