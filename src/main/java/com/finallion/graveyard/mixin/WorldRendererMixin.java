package com.finallion.graveyard.mixin;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.utils.ConfigConsts;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.BiomeKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class WorldRendererMixin {
    // TODO: transition when entering biome
    // smaller values = denser fog
    private float density = ConfigConsts.fogDensity;
    private float densityStart = 1.0F;

    @Shadow
    private ClientWorld world;

    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V", ordinal = 0, shift = At.Shift.AFTER))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
        if (this.client.world.getBiomeKey(camera.getBlockPos()).get().toString().contains("graveyard:haunted_forest") && ConfigConsts.enableFog) {

            float g = gameRenderer.getViewDistance();
            Vec3d vec3d = camera.getPos();
            double d = vec3d.getX();
            double e = vec3d.getY();
            boolean bl2 = this.client.world.getSkyProperties().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();

            applyCustomFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g - 16.0F, 32.0F), bl2, density);
        }
    }

    // copy of the BackgroundRenderer method applyFog with density parameter
    private static void applyCustomFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float density) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();

        if (cameraSubmersionType == CameraSubmersionType.NONE) {
            if (!(entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS))) {
                float o = viewDistance * 0.05F * density;
                float r = Math.min(viewDistance, 192.0F) * 0.5F * density;
                RenderSystem.setShaderFogStart(o);
                RenderSystem.setShaderFogEnd(r);
            }
        }
    }
}
