package com.finallion.graveyard.mixin;


import com.finallion.graveyard.TheGraveyard;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
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
    private static float fogStart = 32.0F;

    @Shadow
    private ClientWorld world;

    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V", ordinal = 0, shift = At.Shift.AFTER))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo callback) {
        BlockPos pos = camera.getBlockPos();
        String biomeName = this.client.world.getBiomeKey(pos).get().getValue().getPath();

        if (!TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID,"haunted_forest_fog")) &&
                !TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID,"haunted_lakes_fog")) &&
                !TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID,"eroded_haunted_forest_fog"))) {
            return;
        }


        if (biomeName.equals("haunted_lakes") && TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID,"haunted_lakes_fog"))) {
            if (pos.getY() > TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"haunted_lakes_fog")).maxY
                    || pos.getY() < TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"haunted_lakes_fog")).minY) {
                // TODO: own method, no time, need sleep
                // fade fog out
                if (fogStart < 32.0F) {
                    fogStart *= 1.05F;

                } else {
                    fogStart = 32.0F;
                    return;
                }
            }
            fogDensity = TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"haunted_lakes_fog")).density;
        } else if (biomeName.equals("eroded_haunted_forest") && TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID,"eroded_haunted_forest_fog"))) {
            if (pos.getY() > TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"eroded_haunted_forest_fog")).maxY
                    || pos.getY() < TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"eroded_haunted_forest_fog")).minY) {
                // fade fog out
                if (fogStart < 32.0F) {
                    fogStart *= 1.05F;

                } else {
                    fogStart = 32.0F;
                    return;
                }
            }
            fogDensity = TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"eroded_haunted_forest_fog")).density;
        } else if (biomeName.equals("haunted_forest") && TheGraveyard.config.biomeFogSpawn(new Identifier(TheGraveyard.MOD_ID,"haunted_forest_fog"))) {
            if (pos.getY() > TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"haunted_forest_fog")).maxY
                    || pos.getY() < TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"haunted_forest_fog")).minY) {
                // fade fog out
                if (fogStart < 32.0F) {
                    fogStart *= 1.05F;

                } else {
                    fogStart = 32.0F;
                    return;
                }
            }
            fogDensity = TheGraveyard.config.getFog(new Identifier(TheGraveyard.MOD_ID,"haunted_forest_fog")).density;
        } else {

            // fade fog out
            if (fogStart < 32.0F) {
                fogStart *= 1.05F;

            } else {
                fogStart = 32.0F;
                return;
            }

        }

        float g = gameRenderer.getViewDistance();
        Vec3d vec3d = camera.getPos();
        double d = vec3d.getX();
        double e = vec3d.getY();
        boolean bl2 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d), MathHelper.floor(e)) || this.client.inGameHud.getBossBarHud().shouldThickenFog();

        applyCustomFog(camera, BackgroundRenderer.FogType.FOG_TERRAIN, Math.max(g - 16.0F, 32.0F), bl2, fogDensity);

    }

    // copy of the BackgroundRenderer method applyFog with density parameter
    private static void applyCustomFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float density) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();

        if (cameraSubmersionType == CameraSubmersionType.NONE) {
            if (!(entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS))) {

                // fade fog in
                if (fogStart > density) {
                    fogStart *= 0.975F;
                }

                float o = viewDistance * 0.05F * fogStart;
                float r = Math.min(viewDistance, 192.0F) * 0.5F * fogStart;
                RenderSystem.setShaderFogStart(o);
                RenderSystem.setShaderFogEnd(r);

            }
        }
    }
}


