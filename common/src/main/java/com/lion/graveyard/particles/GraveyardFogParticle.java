package com.lion.graveyard.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class GraveyardFogParticle extends SingleQuadParticle {
    private static final float PEAK_ALPHA_MIN = 0.35F;
    private static final float PEAK_ALPHA_RANGE = 0.15F;
    private static final float FADE_IN_END = 0.25F;
    private static final float FADE_OUT_START = 0.65F;
    private static final float GROWTH = 0.4F;
    private static final float MAX_TILT = (float) Math.toRadians(3.0D);
    private static final float FLAT_ROTATION = (float) (-Math.PI / 2.0D);

    private final float baseSize;
    private final float peakAlpha;
    private final float spinSpeed;
    private final float tiltX;
    private final float tiltZ;
    private float spin;
    private float prevSpin;

    GraveyardFogParticle(ClientLevel clientWorld, double x, double y, double z, TextureAtlasSprite sprite) {
        super(clientWorld, x, y, z, 0.0D, 0.0D, 0.0D, sprite);
        this.hasPhysics = false;
        this.lifetime = 80 + random.nextInt(61);
        this.baseSize = 1.8F + random.nextFloat() * 1.4F;
        this.peakAlpha = PEAK_ALPHA_MIN + random.nextFloat() * PEAK_ALPHA_RANGE;
        this.spin = random.nextFloat() * (float) (Math.PI * 2.0D);
        this.prevSpin = this.spin;
        this.spinSpeed = (random.nextFloat() - 0.5F) * 0.012F;
        this.tiltX = (random.nextFloat() * 2.0F - 1.0F) * MAX_TILT;
        this.tiltZ = (random.nextFloat() * 2.0F - 1.0F) * MAX_TILT;
        double heading = random.nextDouble() * Math.PI * 2.0D;
        double speed = 0.003D + random.nextDouble() * 0.005D;
        this.xd = Math.cos(heading) * speed;
        this.yd = 0.001D;
        this.zd = Math.sin(heading) * speed;
        this.setColor(0.93F, 0.95F, 0.96F);
        this.alpha = 0.0F;
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevSpin = this.spin;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double time = this.level.getGameTime();
        double windX = Math.sin(time * 0.004D) * 0.002D;
        double windZ = Math.cos(time * 0.003D) * 0.002D;
        this.setPos(this.x + this.xd + windX, this.y + this.yd, this.z + this.zd + windZ);
        this.spin += this.spinSpeed;
    }

    @Override
    public void extract(QuadParticleRenderState state, Camera camera, float tickDelta) {
        float progress = Math.min(1.0F, (this.age + tickDelta) / this.lifetime);
        float fade;

        if (progress < FADE_IN_END) {
            fade = progress / FADE_IN_END;
        } else if (progress > FADE_OUT_START) {
            fade = (1.0F - progress) / (1.0F - FADE_OUT_START);
        } else {
            fade = 1.0F;
        }

        this.alpha = this.peakAlpha * fade;
        this.quadSize = this.baseSize * (1.0F + GROWTH * progress) * 0.5F;

        Vec3 cam = camera.position();
        float px = (float) (Mth.lerp(tickDelta, this.xo, this.x) - cam.x());
        float py = (float) (Mth.lerp(tickDelta, this.yo, this.y) - cam.y());
        float pz = (float) (Mth.lerp(tickDelta, this.zo, this.z) - cam.z());

        Quaternionf rotation = new Quaternionf()
                .rotationYXZ(Mth.lerp(tickDelta, this.prevSpin, this.spin), this.tiltX, this.tiltZ)
                .rotateX(FLAT_ROTATION);
        this.extractRotatedQuad(state, rotation, px, py, pz, tickDelta);
        this.extractRotatedQuad(state, rotation.rotateY((float) Math.PI), px, py, pz, tickDelta);
    }

    @Environment(EnvType.CLIENT)
    public static class FogFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public FogFactory(SpriteSet spriteProvider) {
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
            return new GraveyardFogParticle(clientWorld, d, e, f, this.spriteProvider.get(random));
        }
    }
}
