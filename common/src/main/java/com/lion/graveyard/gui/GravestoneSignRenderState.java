package com.lion.graveyard.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.Block;

@Environment(EnvType.CLIENT)
public record GravestoneSignRenderState(
        Model.Simple model,
        Block block,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        ScreenRectangle scissorArea,
        ScreenRectangle bounds
) implements PictureInPictureRenderState {

    public GravestoneSignRenderState(
            Model.Simple model,
            Block block,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            ScreenRectangle scissorArea
    ) {
        this(model, block, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }
}
