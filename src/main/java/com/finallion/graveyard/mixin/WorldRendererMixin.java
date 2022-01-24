package com.finallion.graveyard.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {
    // smaller values = denser fog
    private float fogDensity;

    @Shadow
    private ClientWorld world;

    @Shadow
    private MinecraftClient client;

    //    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix) {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V", ordinal = 0, shift = At.Shift.AFTER))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
        BlockPos pos = camera.getBlockPos();
        String biomeName = this.client.world.getBiomeKey(pos).get().toString();

        if (!ConfigConsts.enableFogLakes && !ConfigConsts.enableFogEroded && !ConfigConsts.enableFogForest) {
            return;
        }


        if (biomeName.contains("graveyard:haunted_lakes") && ConfigConsts.enableFogLakes) {
            if (pos.getY() > ConfigConsts.fogLakesMaxY || pos.getY() < ConfigConsts.fogLakesMinY) {
                return;
            }
            fogDensity = ConfigConsts.fogDensityLakes;
        } else if (biomeName.contains("graveyard:eroded_haunted") && ConfigConsts.enableFogEroded) {
            if (pos.getY() > ConfigConsts.fogErodedMaxY || pos.getY() < ConfigConsts.fogErodedMinY) {
                return;
            }
            fogDensity = ConfigConsts.fogDensityEroded;
        } else if (biomeName.contains("graveyard:haunted_forest") && ConfigConsts.enableFogForest) {
            if (pos.getY() > ConfigConsts.fogForestMaxY || pos.getY() < ConfigConsts.fogForestMinY) {
                return;
            }
            fogDensity = ConfigConsts.fogDensityForest;
        } else {
            return;
        }

        float g = gameRenderer.getViewDistance();
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        boolean bl2 = this.client.world.getSkyProperties().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();

        applyCustomFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g - 16.0F, 32.0F), bl2, fogDensity);

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
