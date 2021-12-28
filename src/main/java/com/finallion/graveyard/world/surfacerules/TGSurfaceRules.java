package com.finallion.graveyard.world.surfacerules;

import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.VanillaSurfaceRules;

public class TGSurfaceRules {
    private static final MaterialRules.MaterialRule TG_MOSS_BLOCK = block(TGBlocks.TG_MOSS_BLOCK);
    private static final MaterialRules.MaterialRule MOSS_BLOCK = block(Blocks.MOSS_BLOCK);
    private static final MaterialRules.MaterialRule ROOTED_DIRT = block(Blocks.ROOTED_DIRT);
    private static final MaterialRules.MaterialRule COARSE_DIRT = block(Blocks.COARSE_DIRT);
    private static final MaterialRules.MaterialRule DIRT = block(Blocks.DIRT);
    private static final MaterialRules.MaterialRule SOUL_SAND = block(Blocks.SOUL_SAND);
    private static final MaterialRules.MaterialRule SOUL_SOIL = block(Blocks.SOUL_SOIL);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = block(Blocks.GRASS_BLOCK);

    private static MaterialRules.MaterialRule block(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

}
