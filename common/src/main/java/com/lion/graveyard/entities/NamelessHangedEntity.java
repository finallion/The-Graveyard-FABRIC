package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.trades.NamelessHangedTrades;
import com.lion.villagertradingplus.tradeoffers.gui.TradeControl;
import net.minecraft.core.particles.ParticleTypes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NamelessHangedEntity extends AbstractVillager implements GeoEntity, TradeControl {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final String OFFERS_DAY_KEY = "OffersDay";
    private long offersDay = -1L;
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);

    public NamelessHangedEntity(EntityType<? extends NamelessHangedEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
    }

    public static AttributeSupplier.Builder createNamelessHangedAttributes() {
        return AgeableMob.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 0.0D)
                .add(Attributes.MAX_HEALTH, 45.0D);
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
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.getCommandSenderWorld().isClientSide && !this.isRemoved()) {
            if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.remove(RemovalReason.DISCARDED);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt lightning) {
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public void aiStep() {
        if (this.getCommandSenderWorld().isClientSide) {
            this.getCommandSenderWorld().addParticle(ParticleTypes.ASH, this.getRandomX(0.5D), this.getY() + 1.75D, this.getRandomZ(0.5D), 0, 0, 0);
        }

        super.aiStep();
    }

    @Override
    public void tick() {
        if (getCommandSenderWorld().isDay() && this.offers != null && !this.isTrading()) {
            this.offers = null;
        }

        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putLong(OFFERS_DAY_KEY, this.offersDay);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.offersDay = nbt.contains(OFFERS_DAY_KEY) ? nbt.getLong(OFFERS_DAY_KEY) : -1L;
    }

    private long currentDay() {
        return this.getCommandSenderWorld().getDayTime() / 24000L;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.offers != null && this.offersDay != currentDay() && !this.isTrading()) {
            this.offers = null;
        }

        if (getCommandSenderWorld().isDay() && !this.getCommandSenderWorld().isClientSide) {
            player.displayClientMessage(Component.translatable("entity.graveyard.nameless_hanged.wait"), true);
            getCommandSenderWorld().playSound(null, player.blockPosition(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundSource.HOSTILE, 0.5F, 1.0F);
        }

        if (this.isAlive() && !this.isTrading() && getCommandSenderWorld().isNight()) {
            if (!this.getOffers().isEmpty()) {
                if (!this.getCommandSenderWorld().isClientSide) {
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 1);
                    getCommandSenderWorld().playSound(null, player.blockPosition(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundSource.HOSTILE, 0.5F, 0.8F);
                }
            }

            return InteractionResult.sidedSuccess(this.getCommandSenderWorld().isClientSide);
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    protected void updateTrades() {
        List<VillagerTrades.ItemListing> factorys = NamelessHangedTrades.TRADES_REGISTRY;
        MerchantOffers tradeOfferList = this.getOffers();
        int offers = minOffers() + random.nextInt(maxOffers() - minOffers() + 1);
        this.offersDay = currentDay();
        this.addOffersFromItemListings(tradeOfferList, factorys.toArray(new VillagerTrades.ItemListing[0]), offers);
    }

    private static int minOffers() {
        return Math.max(1, (int) Graveyard.getConfig().number("namelessHangedMinOffers", 7.0D));
    }

    private static int maxOffers() {
        return Math.max(minOffers(), (int) Graveyard.getConfig().number("namelessHangedMaxOffers", 9.0D));
    }

    @Override
    public void villagertradingplus$rerollAll() {
        this.getOffers().clear();
        this.updateTrades();
    }

    @Override
    public VillagerTrades.ItemListing[] villagertradingplus$catalogPool(int level) {
        return NamelessHangedTrades.TRADES_REGISTRY.toArray(new VillagerTrades.ItemListing[0]);
    }

    @Override
    public int villagertradingplus$catalogMaxLevel() {
        return 1;
    }

    @Override
    public int villagertradingplus$catalogPicks(int level) {
        return maxOffers();
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.getCommandSenderWorld().addFreshEntity(new ExperienceOrb(this.getCommandSenderWorld(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    @Override
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

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.SOUL_ESCAPE;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean sold) {
        return SoundEvents.SOUL_ESCAPE;
    }


}
