package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class TGTags {

    public static final Tag<Block> DEAD_CORAL_BLOCKS = TagFactory.BLOCK.create(new Identifier(TheGraveyard.MOD_ID, "dead_coral_blocks"));
    public static final Tag<Block> DEAD_CORAL_PLANTS = TagFactory.BLOCK.create(new Identifier(TheGraveyard.MOD_ID, "dead_coral_plants"));
    public static final Tag<Block> DEAD_CORALS = TagFactory.BLOCK.create(new Identifier(TheGraveyard.MOD_ID, "dead_corals"));
    public static final Tag<Block> DEAD_WALL_CORALS = TagFactory.BLOCK.create(new Identifier(TheGraveyard.MOD_ID, "dead_wall_corals"));
}
