package com.lion.graveyard.platform.forge;

import com.lion.graveyard.blockentities.renders.CarvedGravestoneRenderer;
import com.lion.graveyard.item.CarvedGravestoneItem;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemFactoryImpl {

    public static CarvedGravestoneItem carvedGravestone(Block block, Item.Settings settings) {
        return new ForgeCarvedGravestoneItem(block, settings);
    }

    /// Forge has no registry for item renderers - the item itself has to hand one out. The nested
    /// classes below are only loaded when the client calls in, so the client-only types they touch
    /// never reach a server.
    private static class ForgeCarvedGravestoneItem extends CarvedGravestoneItem {

        ForgeCarvedGravestoneItem(Block block, Settings settings) {
            super(block, settings);
        }

        @Override
        public void initializeClient(Consumer<IClientItemExtensions> consumer) {
            consumer.accept(new IClientItemExtensions() {
                @Override
                public BuiltinModelItemRenderer getCustomRenderer() {
                    return Renderer.INSTANCE;
                }
            });
        }
    }

    private static class Renderer extends BuiltinModelItemRenderer {
        static final Renderer INSTANCE = new Renderer();

        private Renderer() {
            super(null, null);
        }

        @Override
        public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
            CarvedGravestoneRenderer.renderItem(stack, matrices, vertexConsumers, light, overlay);
        }
    }
}
