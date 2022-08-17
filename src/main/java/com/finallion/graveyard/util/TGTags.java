package com.finallion.graveyard.util;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class TGTags {

    public static void init() {}

    public static TagKey<Biome> IS_OVERWORLD = TagKey.of(Registry.BIOME_KEY,
            new Identifier(TheGraveyard.MOD_ID, "is_overworld"));

}
