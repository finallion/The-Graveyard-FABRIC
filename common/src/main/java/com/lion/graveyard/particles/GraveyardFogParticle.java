package com.lion.graveyard.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class GraveyardFogParticle extends TextureSheetParticle {
    private double startY;

    GraveyardFogParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.hasPhysics = false;
        this.quadSize *= 5.0D;
        this.yd *= 0.002999999552965164D;
        this.startY = y;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    // particle starts roughly at blocks y level (see TGMossBlock)
    // it slowly rises and dies when it reaches a certain height above the block
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.move(0, this.yd, 0);


        if (this.y >= startY + 1.0D + random.nextDouble()) {
            this.remove();
        }

    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Provider(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }


        public Particle createParticle(SimpleParticleType p_199234_1_, ClientLevel p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
            GraveyardFogParticle suspendParticle = new GraveyardFogParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
            suspendParticle.pickSprite(this.spriteProvider);
            return suspendParticle;
        }
    }
}

