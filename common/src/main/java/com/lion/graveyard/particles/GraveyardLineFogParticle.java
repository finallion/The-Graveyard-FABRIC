package com.lion.graveyard.particles;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class GraveyardLineFogParticle extends RisingParticle {
    private final SpriteSet spriteProvider;
    protected boolean sculk;

    GraveyardLineFogParticle(
            ClientLevel world,
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ,
            SpriteSet spriteProvider
    ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 100;
        this.yd = this.yd * 0.005999999776482582D + velocityY;
        this.y = 2.0F;
        this.spriteProvider = spriteProvider;
        this.setSpriteFromAge(spriteProvider);
        this.scale(3.0F);
    }

    @Override
    public int getLightColor(float tint) {
        return this.sculk ? 240 : super.getLightColor(tint);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteProvider);
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
                double i
        ) {
            GraveyardLineFogParticle soulParticle = new GraveyardLineFogParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0F);
            return soulParticle;
        }
    }
}
