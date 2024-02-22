package com.lion.graveyard.particles;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
public class GraveyardSoulParticle extends RisingParticle {
    private final SpriteSet spriteProvider;
    protected boolean sculk;

    GraveyardSoulParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.scale(world.getRandom().nextFloat() + 1.25F);
        this.setSpriteFromAge(spriteProvider);
    }

    public int getLightColor(float p_234080_) {
        return this.sculk ? 240 : super.getLightColor(p_234080_);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteProvider);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_107739_) {
            this.sprite = p_107739_;
        }

        public Particle createParticle(SimpleParticleType p_107750_, ClientLevel p_107751_, double p_107752_, double p_107753_, double p_107754_, double p_107755_, double p_107756_, double p_107757_) {
            GraveyardSoulParticle soulparticle = new GraveyardSoulParticle(p_107751_, p_107752_, p_107753_, p_107754_, p_107755_, p_107756_, p_107757_, this.sprite);
            soulparticle.setAlpha(1.0F);
            return soulparticle;
        }
    }
}