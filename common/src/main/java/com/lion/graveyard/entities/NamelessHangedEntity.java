package com.lion.graveyard.entities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.GraveyardConfig;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.trades.NamelessHangedTrades;
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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import com.lion.graveyard.entities.animation.AnimationTable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.object.LoopType;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class NamelessHangedEntity extends AbstractVillager implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private static final String OFFERS_DAY_KEY = "OffersDay";
    private long offersDay = -1L;
    private final RawAnimation IDLE_ANIMATION = RawAnimation.begin().then("idle", LoopType.LOOP);

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
                .build("controller"));
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
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (!this.level().isClientSide() && !this.isRemoved()) {
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
        if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.ASH, this.getRandomX(0.5D), this.getY() + 1.75D, this.getRandomZ(0.5D), 0, 0, 0);
        }

        super.aiStep();
    }

    @Override
    public void tick() {
        if (level().isBrightOutside() && this.offers != null && !this.isTrading()) {
            this.offers = null;
        }

        super.tick();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putLong(OFFERS_DAY_KEY, this.offersDay);
        LegacyEntityData.write(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.offersDay = input.getLongOr(OFFERS_DAY_KEY, -1L);
        LegacyEntityData.read(this, input);
    }

    private long currentDay() {
        return this.level().getDayTime() / 24000L;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.offers != null && this.offersDay != currentDay() && !this.isTrading()) {
            this.offers = null;
        }

        if (level().isBrightOutside() && !this.level().isClientSide()) {
            player.displayClientMessage(Component.translatable("entity.graveyard.nameless_hanged.wait"), true);
            level().playSound(null, player.blockPosition(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundSource.HOSTILE, 0.5F, 1.0F);
        }

        if (this.isAlive() && !this.isTrading() && level().isDarkOutside()) {
            if (!this.level().isClientSide() && !this.getOffers().isEmpty()) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
                level().playSound(null, player.blockPosition(), TGSounds.NAMELESS_HANGED_INTERACT.get(), SoundSource.HOSTILE, 0.5F, 0.8F);
            }

            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        List<VillagerTrades.ItemListing> factorys = NamelessHangedTrades.TRADES_REGISTRY;
        MerchantOffers tradeOfferList = this.getOffers();
        GraveyardConfig config = Graveyard.getConfig();
        int minOffers = Math.max(1, (int) config.number("namelessHangedMinOffers", 7.0D));
        int maxOffers = Math.max(minOffers, (int) config.number("namelessHangedMaxOffers", 9.0D));
        int offers = minOffers + random.nextInt(maxOffers - minOffers + 1);
        this.offersDay = currentDay();
        this.addOffersFromItemListings(level, tradeOfferList, factorys.toArray(new VillagerTrades.ItemListing[0]), offers);
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            int i = 3 + this.random.nextInt(4);
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), i));
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
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.SOUL_ESCAPE.value();
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean sold) {
        return SoundEvents.SOUL_ESCAPE.value();
    }


}
