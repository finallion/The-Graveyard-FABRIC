package com.lion.graveyard.recipe;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blocks.CarvedGravestoneBlock;
import com.lion.graveyard.init.TGRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;

/// Carves a gravestone out of whatever block is put in. Shares the ossuary's recipe type so it
/// shows up in the same screen, but the result is not fixed - it carries the input material.
public class GravestoneCarvingRecipe extends OssuaryRecipe {

    /// Blocks that must not become a gravestone even though they are full cubes. Empty by default;
    /// it exists so a datapack can rule one out without a code change.
    private static final TagKey<Block> NOT_CARVABLE =
            TagKey.of(RegistryKeys.BLOCK, new Identifier(Graveyard.MOD_ID, "not_carvable_into_gravestone"));

    public GravestoneCarvingRecipe(Identifier id, String group, Ingredient input, ItemStack output) {
        super(id, group, input, output);
    }

    /// Accepts any full cube, which no ingredient list can express - so the `ingredient` in the
    /// recipe file only serves the recipe book and is not consulted here. The full-cube rule is what
    /// keeps a gravestone made of dead bush out of the game: the model wraps a 16x16 block texture,
    /// and anything that is not a solid block has none worth wrapping.
    @Override
    public boolean matches(Inventory inventory, World world) {
        if (!(inventory.getStack(0).getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        Block block = blockItem.getBlock();
        return !(block instanceof CarvedGravestoneBlock)
                && !block.getDefaultState().isIn(NOT_CARVABLE)
                && block.getDefaultState().isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack result = super.craft(inventory, registryManager);
        if (inventory.getStack(0).getItem() instanceof BlockItem blockItem) {
            CarvedGravestoneBlock.writeMaterial(result, blockItem.getBlock());
        }

        return result;
    }

    /// The parent hardcodes the ossuary serializer; this recipe needs its own so it survives the
    /// trip to the client as the subclass and keeps its dynamic result.
    @Override
    public RecipeSerializer<?> getSerializer() {
        return TGRecipeTypes.GRAVESTONE_CARVING_SERIALIZER.get();
    }
}
