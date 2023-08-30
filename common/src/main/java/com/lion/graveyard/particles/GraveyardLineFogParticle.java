package com.lion.graveyard.particles;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class GraveyardLineFogParticle extends AbstractSlowingParticle {
    private final SpriteProvider spriteProvider;
    protected boolean sculk;

    GraveyardLineFogParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.maxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 100;
        this.velocityY = this.velocityY * 0.005999999776482582D + velocityY;
        this.y = 2.0F;
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
        this.scale(3.0F);
    }

    public int getBrightness(float tint) {
        return this.sculk ? 240 : super.getBrightness(tint);
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteForAge(this.spriteProvider);
    }


    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            GraveyardLineFogParticle soulParticle = new GraveyardLineFogParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0F);
            return soulParticle;
        }
    }
}
