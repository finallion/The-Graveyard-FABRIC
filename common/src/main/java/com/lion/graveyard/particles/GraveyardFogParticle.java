package com.lion.graveyard.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class GraveyardFogParticle extends SpriteBillboardParticle {
    private static final float PEAK_ALPHA_MIN = 0.35F;
    private static final float PEAK_ALPHA_RANGE = 0.15F;
    private static final float FADE_IN_END = 0.25F;
    private static final float FADE_OUT_START = 0.65F;
    private static final float GROWTH = 0.4F;
    private static final float MAX_TILT = (float) Math.toRadians(3.0D);

    private final float baseSize;
    private final float peakAlpha;
    private final float spinSpeed;
    private final float tiltX;
    private final float tiltZ;
    private float spin;
    private float prevSpin;

    GraveyardFogParticle(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z, 0.0D, 0.0D, 0.0D);
        this.collidesWithWorld = false;
        this.maxAge = 80 + random.nextInt(61);
        this.baseSize = 1.8F + random.nextFloat() * 1.4F;
        this.peakAlpha = PEAK_ALPHA_MIN + random.nextFloat() * PEAK_ALPHA_RANGE;
        this.spin = random.nextFloat() * (float) (Math.PI * 2.0D);
        this.prevSpin = this.spin;
        this.spinSpeed = (random.nextFloat() - 0.5F) * 0.012F;
        this.tiltX = (random.nextFloat() * 2.0F - 1.0F) * MAX_TILT;
        this.tiltZ = (random.nextFloat() * 2.0F - 1.0F) * MAX_TILT;
        double heading = random.nextDouble() * Math.PI * 2.0D;
        double speed = 0.003D + random.nextDouble() * 0.005D;
        this.velocityX = Math.cos(heading) * speed;
        this.velocityY = 0.001D;
        this.velocityZ = Math.sin(heading) * speed;
        this.setColor(0.93F, 0.95F, 0.96F);
        this.alpha = 0.0F;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.prevSpin = this.spin;

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        double time = this.world.getTime();
        double windX = Math.sin(time * 0.004D) * 0.002D;
        double windZ = Math.cos(time * 0.003D) * 0.002D;
        this.setPos(this.x + this.velocityX + windX, this.y + this.velocityY, this.z + this.velocityZ + windZ);
        this.spin += this.spinSpeed;
    }

    @Override
    public void buildGeometry(VertexConsumer consumer, Camera camera, float tickDelta) {
        float progress = Math.min(1.0F, (this.age + tickDelta) / this.maxAge);
        float fade;
        if (progress < FADE_IN_END) {
            fade = progress / FADE_IN_END;
        } else if (progress > FADE_OUT_START) {
            fade = (1.0F - progress) / (1.0F - FADE_OUT_START);
        } else {
            fade = 1.0F;
        }
        float opacity = this.peakAlpha * fade;
        float half = this.baseSize * (1.0F + GROWTH * progress) * 0.5F;

        Vec3d cam = camera.getPos();
        float px = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cam.getX());
        float py = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cam.getY());
        float pz = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cam.getZ());

        Quaternionf rotation = new Quaternionf().rotationYXZ(MathHelper.lerp(tickDelta, this.prevSpin, this.spin), this.tiltX, this.tiltZ);
        Vector3f[] corners = {
                new Vector3f(-half, 0.0F, -half),
                new Vector3f(-half, 0.0F, half),
                new Vector3f(half, 0.0F, half),
                new Vector3f(half, 0.0F, -half)
        };
        for (Vector3f corner : corners) {
            corner.rotate(rotation).add(px, py, pz);
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        float[] us = {minU, minU, maxU, maxU};
        float[] vs = {minV, maxV, maxV, minV};
        int light = this.getBrightness(tickDelta);

        for (int i = 0; i < 4; i++) {
            emit(consumer, corners[i], us[i], vs[i], opacity, light);
        }
        for (int i = 3; i >= 0; i--) {
            emit(consumer, corners[i], us[i], vs[i], opacity, light);
        }
    }

    private void emit(VertexConsumer consumer, Vector3f p, float u, float v, float opacity, int light) {
        consumer.vertex(p.x(), p.y(), p.z()).texture(u, v).color(this.red, this.green, this.blue, opacity).light(light).next();
    }

    @Environment(EnvType.CLIENT)
    public static class FogFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public FogFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            GraveyardFogParticle fog = new GraveyardFogParticle(clientWorld, d, e, f);
            fog.setSprite(this.spriteProvider);
            return fog;
        }
    }
}
