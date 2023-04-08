package com.finallion.graveyard.entities;

import com.finallion.graveyard.init.TGSounds;
import com.finallion.graveyard.trades.NamelessHangedTradeOffers;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
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

public class NamelessHangedEntity extends MerchantEntity implements GeoEntity {
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
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
        return source.isIn(DamageTypeTags.BYPASSES_RESISTANCE);
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
        if (this.world.isClient) {
            this.world.addParticle(ParticleTypes.ASH, this.getParticleX(0.5D), this.getY() + 1.75D, this.getParticleZ(0.5D), 0, 0, 0);
        }

        super.tickMovement();
    }

    @Override
    public void tick() {
        if (world.isDay() && this.offers != null) {
            this.offers = null;
        }

        super.tick();
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (world.isDay() && !this.world.isClient) {
            player.sendMessage(Text.translatable("entity.graveyard.nameless_hanged.wait"), true);
            world.playSound(null, player.getBlockPos(), TGSounds.NAMELESS_HANGED_INTERACT, SoundCategory.HOSTILE, 0.6F, 1.0F);
            //player.playSound(TGSounds.NAMELESS_HANGED_INTERACT, 1.0F, 1.0F);
        }

        if (this.isAlive() && !this.hasCustomer() && world.isNight()) {
            if (!this.getOffers().isEmpty()) {
                if (!this.world.isClient) {
                    this.setCustomer(player);
                    this.sendOffers(player, this.getDisplayName(), 1);
                    world.playSound(null, player.getBlockPos(), TGSounds.NAMELESS_HANGED_INTERACT, SoundCategory.HOSTILE, 0.6F, 1.0F);
                    //player.playSound(TGSounds.NAMELESS_HANGED_INTERACT, 1.0F, 1.0F);
                }
            }
            return ActionResult.success(this.world.isClient);
        } else {
            return super.interactMob(player, hand);
        }
    }

    protected void fillRecipes() {
        TradeOffers.Factory[] factorys = NamelessHangedTradeOffers.NAMELESS_HANGED_TRADES.get(1);
        //TradeOffers.Factory[] factorys2 = NamelessHangedTradeOffers.NAMELESS_HANGED_TRADES.get(2);
        if (factorys != null /*&& factorys2 != null*/) {
            TradeOfferList tradeOfferList = this.getOffers();
            int offers = random.nextInt(3) + 7;
            this.fillRecipesFromPool(tradeOfferList, factorys, offers);
            /*
            TradeOffers.Factory factory = factorys2[0];
            TradeOffer tradeOffer = factory.create(this, this.random);
            if (tradeOffer != null) {
                tradeOfferList.add(tradeOffer);
            }

             */
        }
    }

    protected void afterUsing(TradeOffer offer) {
        if (offer.shouldRewardPlayerExperience()) {
            int i = 3 + this.random.nextInt(4);
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    public void playAmbientSound() {
        this.playSound(TGSounds.NAMELESS_HANGED_AMBIENT, 0.3F, 1.0F);

        if (this.random.nextBoolean()) {
            this.playSound(TGSounds.NAMELESS_HANGED_BREATH, 0.5F, 1.0F);
        }
    }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 2.0F;
    }

    @Override
    public SoundEvent getYesSound() {
        return null;
    }

    @Override
    protected SoundEvent getTradingSound(boolean sold) {
        return null;
    }


}
