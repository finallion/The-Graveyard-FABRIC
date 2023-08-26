package main.java.com.lion.graveyard.particles;


import net.minecraft.client.particle.*;
import net.minecraft.particle.DefaultParticleType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public class GraveyardSoulParticle extends AbstractSlowingParticle {
    private final SpriteProvider spriteProvider;
    protected boolean sculk;

    GraveyardSoulParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.scale(world.getRandom().nextFloat() + 1.25F);
        this.setSpriteForAge(spriteProvider);
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
    public static class SculkSoulFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public SculkSoulFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            GraveyardSoulParticle soulParticle = new GraveyardSoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0F);
            soulParticle.sculk = true;
            return soulParticle;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            GraveyardSoulParticle soulParticle = new GraveyardSoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0F);
            return soulParticle;
        }
    }
}
