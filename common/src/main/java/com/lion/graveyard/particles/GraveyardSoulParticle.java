package com.lion.graveyard.particles;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GraveyardSoulParticle extends RisingParticle {
    private final SpriteSet spriteProvider;
    protected boolean sculk;

    GraveyardSoulParticle(
            ClientLevel world,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ,
            SpriteSet spriteProvider
    ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.first());
        this.spriteProvider = spriteProvider;
        this.scale(world.getRandom().nextFloat() + 1.25F);
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    protected int getLightCoords(float tint) {
        return this.sculk ? 240 : super.getLightCoords(tint);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteProvider);
    }

    @Environment(EnvType.CLIENT)
    public static class SculkSoulFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public SculkSoulFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType defaultParticleType,
                ClientLevel clientWorld,
                double d,
                double e,
                double f,
                double g,
                double h,
                double i,
                RandomSource random
        ) {
            GraveyardSoulParticle soulParticle = new GraveyardSoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0F);
            soulParticle.sculk = true;
            return soulParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType defaultParticleType,
                ClientLevel clientWorld,
                double d,
                double e,
                double f,
                double g,
                double h,
                double i,
                RandomSource random
        ) {
            GraveyardSoulParticle soulParticle = new GraveyardSoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0F);
            return soulParticle;
        }
    }
}
