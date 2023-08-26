package main.java.com.lion.graveyard.particles;

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
    private double startY;


    GraveyardFogParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
        super(clientWorld, d, e, f, g, h, i);
        this.collidesWithWorld = false;
        this.scale *= 4.0D;
        this.velocityY *= 0.002999999552965164D;
        this.startY = y;
    }

    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }


    // particle starts roughly at blocks y level (see TGMossBlock)
    // it slowly rises and dies when it reaches a certain height above the block
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.move(0, this.velocityY, 0);

        if (this.y >= startY + 0.75D + random.nextDouble()) {
            this.markDead();
        }

    }

    @Environment(EnvType.CLIENT)
    public static class FogFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public FogFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            GraveyardFogParticle suspendParticle = new GraveyardFogParticle(clientWorld, d, e, f, g, h, i);
            suspendParticle.setSprite(this.spriteProvider);
            return suspendParticle;
        }
    }
}
