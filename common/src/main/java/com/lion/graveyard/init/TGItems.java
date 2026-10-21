package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.item.DiaryPageItem;
import com.lion.graveyard.item.BoneStaffItem;
import com.lion.graveyard.item.DaggerItem;
import com.lion.graveyard.item.TooltipBlockItem;
import com.lion.graveyard.item.VialOfBlood;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TGItems {
    public static final ResourceKey<JukeboxSong> INCARNATED_EVIL_SONG = ResourceKey.create(Registries.JUKEBOX_SONG, Graveyard.id("incarnated_evil"));

    public static final Supplier<Item> SARCOPHAGUS_LID = RegistryHelper.registerItem("sarcophagus_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> SARCOPHAGUS_BASE = RegistryHelper.registerItem("sarcophagus_base", Item::new, new Item.Properties());
    public static final Supplier<Item> DARK_OAK_COFFIN_LID = RegistryHelper.registerItem("dark_oak_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> DARK_OAK_COFFIN_BASE = RegistryHelper.registerItem("dark_oak_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> OAK_COFFIN_LID = RegistryHelper.registerItem("oak_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> OAK_COFFIN_BASE = RegistryHelper.registerItem("oak_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> BIRCH_COFFIN_LID = RegistryHelper.registerItem("birch_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> BIRCH_COFFIN_BASE = RegistryHelper.registerItem("birch_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> JUNGLE_COFFIN_LID = RegistryHelper.registerItem("jungle_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> JUNGLE_COFFIN_BASE = RegistryHelper.registerItem("jungle_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> ACACIA_COFFIN_LID = RegistryHelper.registerItem("acacia_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> ACACIA_COFFIN_BASE = RegistryHelper.registerItem("acacia_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> SPRUCE_COFFIN_LID = RegistryHelper.registerItem("spruce_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> SPRUCE_COFFIN_BASE = RegistryHelper.registerItem("spruce_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> WARPED_COFFIN_LID = RegistryHelper.registerItem("warped_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> WARPED_COFFIN_BASE = RegistryHelper.registerItem("warped_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> CRIMSON_COFFIN_LID = RegistryHelper.registerItem("crimson_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> CRIMSON_COFFIN_BASE = RegistryHelper.registerItem("crimson_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> MANGROVE_COFFIN_LID = RegistryHelper.registerItem("mangrove_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> MANGROVE_COFFIN_BASE = RegistryHelper.registerItem("mangrove_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> BAMBOO_COFFIN_LID = RegistryHelper.registerItem("bamboo_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> BAMBOO_COFFIN_BASE = RegistryHelper.registerItem("bamboo_coffin_base", Item::new, new Item.Properties());
    public static final Supplier<Item> CHERRY_COFFIN_LID = RegistryHelper.registerItem("cherry_coffin_lid", Item::new, new Item.Properties());
    public static final Supplier<Item> CHERRY_COFFIN_BASE = RegistryHelper.registerItem("cherry_coffin_base", Item::new, new Item.Properties());

    public static final Supplier<Item> OSSUARY = RegistryHelper.registerItem("ossuary", props -> new TooltipBlockItem(TGBlocks.OSSUARY.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> INCARNATED_EVIL_MUSIC_DISC = RegistryHelper.registerMusicDiscItem("incarnated_evil_music_disc", new Item.Properties().stacksTo(1).jukeboxPlayable(INCARNATED_EVIL_SONG));

    public static final Supplier<Item> SKELETON_CREEPER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("skeleton_creeper_spawn_egg", TGEntities.SKELETON_CREEPER, new Item.Properties());
    public static final Supplier<Item> RAVEN_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("raven_spawn_egg", TGEntities.RAVEN, new Item.Properties());
    public static final Supplier<Item> MAGPIE_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("magpie_spawn_egg", TGEntities.MAGPIE, new Item.Properties());
    public static final Supplier<Item> CROW_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("crow_spawn_egg", TGEntities.CROW, new Item.Properties());
    public static final Supplier<Item> ACOLYTE_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("acolyte_spawn_egg", TGEntities.ACOLYTE, new Item.Properties());
    public static final Supplier<Item> GHOUL_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("ghoul_spawn_egg", TGEntities.GHOUL, new Item.Properties());
    public static final Supplier<Item> REAPER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("reaper_spawn_egg", TGEntities.REAPER, new Item.Properties());
    public static final Supplier<Item> TORMENTED_VILLAGER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("tormented_villager_spawn_egg", TGEntities.TORMENTED_VILLAGER, new Item.Properties());
    public static final Supplier<Item> REVENANT_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("revenant_spawn_egg", TGEntities.REVENANT, new Item.Properties());
    public static final Supplier<Item> NIGHTMARE_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("nightmare_spawn_egg", TGEntities.NIGHTMARE, new Item.Properties());
    public static final Supplier<Item> LICH_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("lich_spawn_egg", TGEntities.LICH, new Item.Properties());
    public static final Supplier<Item> NAMELESS_HANGED_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("nameless_hanged_spawn_egg", TGEntities.NAMELESS_HANGED, new Item.Properties());
    public static final Supplier<Item> WRAITH_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("wraith_spawn_egg", TGEntities.WRAITH, new Item.Properties());
    public static final Supplier<Item> CORRUPTED_PILLAGER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("corrupted_pillager_spawn_egg", TGEntities.CORRUPTED_PILLAGER, new Item.Properties());
    public static final Supplier<Item> CORRUPTED_VINDICATOR_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("corrupted_vindicator_spawn_egg", TGEntities.CORRUPTED_VINDICATOR, new Item.Properties());
    public static final Supplier<Item> CORRUPTED_EVOKER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("corrupted_evoker_spawn_egg", TGEntities.CORRUPTED_EVOKER, new Item.Properties());

    public static final Supplier<Item> BONE_DAGGER = RegistryHelper.registerItem("bone_dagger", DaggerItem::new, new Item.Properties());
    public static final Supplier<Item> DARK_IRON_INGOT = RegistryHelper.registerItem("dark_iron_ingot", Item::new, new Item.Properties());
    public static final Supplier<Item> CORRUPTION = RegistryHelper.registerItem("corruption", Item::new, new Item.Properties());
    public static final Supplier<Item> DIARY_PAGE = RegistryHelper.registerItem("diary_page", props -> new DiaryPageItem(TGBlocks.LORE_BOOK.get(), props), new Item.Properties().stacksTo(16).overrideDescription("block.graveyard.lore_book"));

    public static final Supplier<Item> TG_ROOTED_DIRT = RegistryHelper.registerItem("tg_rooted_dirt", props -> new BlockItem(TGBlocks.TG_ROOTED_DIRT.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_TUFF = RegistryHelper.registerItem("tg_tuff", props -> new BlockItem(TGBlocks.TG_TUFF.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_MOSS_BLOCK = RegistryHelper.registerItem("tg_moss_block", props -> new BlockItem(TGBlocks.TG_MOSS_BLOCK.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_DEEPSLATE = RegistryHelper.registerItem("tg_deepslate", props -> new BlockItem(TGBlocks.TG_DEEPSLATE.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> TG_GRASS_BLOCK = RegistryHelper.registerItem("tg_grass_block", props -> new BlockItem(TGBlocks.TG_GRASS_BLOCK.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_DIRT = RegistryHelper.registerItem("tg_dirt", props -> new BlockItem(TGBlocks.TG_DIRT.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_COARSE_DIRT = RegistryHelper.registerItem("tg_coarse_dirt", props -> new BlockItem(TGBlocks.TG_COARSE_DIRT.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_ANDESITE = RegistryHelper.registerItem("tg_andesite", props -> new BlockItem(TGBlocks.TG_ANDESITE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_GRANITE = RegistryHelper.registerItem("tg_granite", props -> new BlockItem(TGBlocks.TG_GRANITE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_DIORITE = RegistryHelper.registerItem("tg_diorite", props -> new BlockItem(TGBlocks.TG_DIORITE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_STONE = RegistryHelper.registerItem("tg_stone", props -> new BlockItem(TGBlocks.TG_STONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TG_PODZOL = RegistryHelper.registerItem("tg_podzol", props -> new BlockItem(TGBlocks.TG_PODZOL.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> DARK_IRON_BARS = RegistryHelper.registerItem("dark_iron_bars", props -> new BlockItem(TGBlocks.DARK_IRON_BARS.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SOUL_FIRE_BRAZIER = RegistryHelper.registerItem("soul_fire_brazier", props -> new BlockItem(TGBlocks.SOUL_FIRE_BRAZIER.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> FIRE_BRAZIER = RegistryHelper.registerItem("fire_brazier", props -> new BlockItem(TGBlocks.FIRE_BRAZIER.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> PEDESTAL = RegistryHelper.registerItem("pedestal", props -> new BlockItem(TGBlocks.PEDESTAL.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CANDLE_HOLDER = RegistryHelper.registerItem("candle_holder", props -> new BlockItem(TGBlocks.CANDLE_HOLDER.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> DARK_IRON_TRAPDOOR = RegistryHelper.registerItem("dark_iron_trapdoor", props -> new BlockItem(TGBlocks.DARK_IRON_TRAPDOOR.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> DARK_IRON_DOOR = RegistryHelper.registerItem("dark_iron_door", props -> new BlockItem(TGBlocks.DARK_IRON_DOOR.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> DARK_IRON_BLOCK = RegistryHelper.registerItem("dark_iron_block", props -> new BlockItem(TGBlocks.DARK_IRON_BLOCK.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SKULL_WITH_RIB_CAGE = RegistryHelper.registerItem("skull_with_rib_cage", props -> new BlockItem(TGBlocks.SKULL_WITH_RIB_CAGE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LEANING_SKELETON = RegistryHelper.registerItem("leaning_skeleton", props -> new BlockItem(TGBlocks.LEANING_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SKULL_PILE = RegistryHelper.registerItem("skull_pile", props -> new BlockItem(TGBlocks.SKULL_PILE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LYING_SKELETON = RegistryHelper.registerItem("lying_skeleton", props -> new BlockItem(TGBlocks.LYING_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_SKULL_WITH_RIB_CAGE = RegistryHelper.registerItem("wither_skull_with_rib_cage", props -> new BlockItem(TGBlocks.WITHER_SKULL_WITH_RIB_CAGE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LEANING_WITHER_SKELETON = RegistryHelper.registerItem("leaning_wither_skeleton", props -> new BlockItem(TGBlocks.LEANING_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_SKULL_PILE = RegistryHelper.registerItem("wither_skull_pile", props -> new BlockItem(TGBlocks.WITHER_SKULL_PILE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LYING_WITHER_SKELETON = RegistryHelper.registerItem("lying_wither_skeleton", props -> new BlockItem(TGBlocks.LYING_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CREEPER_SKELETON = RegistryHelper.registerItem("creeper_skeleton", props -> new BlockItem(TGBlocks.CREEPER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SKELETON_HAND = RegistryHelper.registerItem("skeleton_hand", props -> new BlockItem(TGBlocks.SKELETON_HAND.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_SKELETON_HAND = RegistryHelper.registerItem("wither_skeleton_hand", props -> new BlockItem(TGBlocks.WITHER_SKELETON_HAND.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> HANGED_SKELETON = RegistryHelper.registerItem("hanged_skeleton", props -> new BlockItem(TGBlocks.HANGED_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> HANGED_WITHER_SKELETON = RegistryHelper.registerItem("hanged_wither_skeleton", props -> new BlockItem(TGBlocks.HANGED_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SEATED_FULL_SKELETON = RegistryHelper.registerItem("seated_full_skeleton", props -> new BlockItem(TGBlocks.SEATED_FULL_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SEATED_FULL_WITHER_SKELETON = RegistryHelper.registerItem("seated_full_wither_skeleton", props -> new BlockItem(TGBlocks.SEATED_FULL_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SKELETON_SKULL_WITH_AXE = RegistryHelper.registerItem("skeleton_skull_with_axe", props -> new BlockItem(TGBlocks.SKELETON_SKULL_WITH_AXE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_SKELETON_SKULL_WITH_AXE = RegistryHelper.registerItem("wither_skeleton_skull_with_axe", props -> new BlockItem(TGBlocks.WITHER_SKELETON_SKULL_WITH_AXE.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SWORD_IN_SKELETON = RegistryHelper.registerItem("sword_in_skeleton", props -> new BlockItem(TGBlocks.SWORD_IN_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SWORD_IN_WITHER_SKELETON = RegistryHelper.registerItem("sword_in_wither_skeleton", props -> new BlockItem(TGBlocks.SWORD_IN_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SMALL_SKELETON_REMAINS = RegistryHelper.registerItem("small_skeleton_remains", props -> new BlockItem(TGBlocks.SMALL_SKELETON_REMAINS.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_WITHER_SKELETON_REMAINS = RegistryHelper.registerItem("small_wither_skeleton_remains", props -> new BlockItem(TGBlocks.SMALL_WITHER_SKELETON_REMAINS.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> HANGED_FULL_SKELETON = RegistryHelper.registerItem("hanged_full_skeleton", props -> new BlockItem(TGBlocks.HANGED_FULL_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> HANGED_FULL_WITHER_SKELETON = RegistryHelper.registerItem("hanged_full_wither_skeleton", props -> new BlockItem(TGBlocks.HANGED_FULL_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> LYING_FULL_SKELETON = RegistryHelper.registerItem("lying_full_skeleton", props -> new BlockItem(TGBlocks.LYING_FULL_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LYING_FULL_WITHER_SKELETON = RegistryHelper.registerItem("lying_full_wither_skeleton", props -> new BlockItem(TGBlocks.LYING_FULL_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> BACKSTABBED_FULL_SKELETON = RegistryHelper.registerItem("backstabbed_full_skeleton", props -> new BlockItem(TGBlocks.BACKSTABBED_FULL_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BACKSTABBED_FULL_WITHER_SKELETON = RegistryHelper.registerItem("backstabbed_full_wither_skeleton", props -> new BlockItem(TGBlocks.BACKSTABBED_FULL_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SHOT_FULL_SKELETON = RegistryHelper.registerItem("shot_full_skeleton", props -> new BlockItem(TGBlocks.SHOT_FULL_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SHOT_FULL_WITHER_SKELETON = RegistryHelper.registerItem("shot_full_wither_skeleton", props -> new BlockItem(TGBlocks.SHOT_FULL_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SIDEWAYS_FULL_SKELETON = RegistryHelper.registerItem("sideways_full_skeleton", props -> new BlockItem(TGBlocks.SIDEWAYS_FULL_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SIDEWAYS_FULL_WITHER_SKELETON = RegistryHelper.registerItem("sideways_full_wither_skeleton", props -> new BlockItem(TGBlocks.SIDEWAYS_FULL_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> VASE_BLOCK = RegistryHelper.registerItem("vase_block", props -> new BlockItem(TGBlocks.VASE_BLOCK.get(), props), new Item.Properties().useBlockDescriptionPrefix());


    public static final Supplier<Item> GRAVESTONE = RegistryHelper.registerItem("gravestone", props -> new BlockItem(TGBlocks.GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> COBBLESTONE_GRAVESTONE = RegistryHelper.registerItem("cobblestone_gravestone", props -> new BlockItem(TGBlocks.COBBLESTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> MOSSY_COBBLESTONE_GRAVESTONE = RegistryHelper.registerItem("mossy_cobblestone_gravestone", props -> new BlockItem(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> DEEPSLATE_GRAVESTONE = RegistryHelper.registerItem("deepslate_gravestone", props -> new BlockItem(TGBlocks.DEEPSLATE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BLACKSTONE_GRAVESTONE = RegistryHelper.registerItem("blackstone_gravestone", props -> new BlockItem(TGBlocks.BLACKSTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CRACKED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerItem("cracked_blackstone_gravestone", props -> new BlockItem(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> STONE_BRICKS_GRAVESTONE = RegistryHelper.registerItem("stone_bricks_gravestone", props -> new BlockItem(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> MOSSY_STONE_BRICKS_GRAVESTONE = RegistryHelper.registerItem("mossy_stone_bricks_gravestone", props -> new BlockItem(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BRICKS_GRAVESTONE = RegistryHelper.registerItem("bricks_gravestone", props -> new BlockItem(TGBlocks.BRICKS_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> GILDED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerItem("gilded_blackstone_gravestone", props -> new BlockItem(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SANDSTONE_GRAVESTONE = RegistryHelper.registerItem("sandstone_gravestone", props -> new BlockItem(TGBlocks.SANDSTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> RED_SANDSTONE_GRAVESTONE = RegistryHelper.registerItem("red_sandstone_gravestone", props -> new BlockItem(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> QUARTZ_BRICKS_GRAVESTONE = RegistryHelper.registerItem("quartz_bricks_gravestone", props -> new BlockItem(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> BLACK_URN = RegistryHelper.registerItem("black_urn", props -> new BlockItem(TGBlocks.BLACK_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BROWN_URN = RegistryHelper.registerItem("brown_urn", props -> new BlockItem(TGBlocks.BROWN_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WHITE_URN = RegistryHelper.registerItem("white_urn", props -> new BlockItem(TGBlocks.WHITE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> GRAY_URN = RegistryHelper.registerItem("gray_urn", props -> new BlockItem(TGBlocks.GRAY_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LIGHT_GRAY_URN = RegistryHelper.registerItem("light_gray_urn", props -> new BlockItem(TGBlocks.LIGHT_GRAY_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BLUE_URN = RegistryHelper.registerItem("blue_urn", props -> new BlockItem(TGBlocks.BLUE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CYAN_URN = RegistryHelper.registerItem("cyan_urn", props -> new BlockItem(TGBlocks.CYAN_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LIGHT_BLUE_URN = RegistryHelper.registerItem("light_blue_urn", props -> new BlockItem(TGBlocks.LIGHT_BLUE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> RED_URN = RegistryHelper.registerItem("red_urn", props -> new BlockItem(TGBlocks.RED_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> ORANGE_URN = RegistryHelper.registerItem("orange_urn", props -> new BlockItem(TGBlocks.ORANGE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> YELLOW_URN = RegistryHelper.registerItem("yellow_urn", props -> new BlockItem(TGBlocks.YELLOW_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> PINK_URN = RegistryHelper.registerItem("pink_urn", props -> new BlockItem(TGBlocks.PINK_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> MAGENTA_URN = RegistryHelper.registerItem("magenta_urn", props -> new BlockItem(TGBlocks.MAGENTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> PURPLE_URN = RegistryHelper.registerItem("purple_urn", props -> new BlockItem(TGBlocks.PURPLE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> GREEN_URN = RegistryHelper.registerItem("green_urn", props -> new BlockItem(TGBlocks.GREEN_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LIME_URN = RegistryHelper.registerItem("lime_urn", props -> new BlockItem(TGBlocks.LIME_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> TERRACOTTA_URN = RegistryHelper.registerItem("terracotta_urn", props -> new BlockItem(TGBlocks.TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BLACK_TERRACOTTA_URN = RegistryHelper.registerItem("black_terracotta_urn", props -> new BlockItem(TGBlocks.BLACK_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BLUE_TERRACOTTA_URN = RegistryHelper.registerItem("blue_terracotta_urn", props -> new BlockItem(TGBlocks.BLUE_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BROWN_TERRACOTTA_URN = RegistryHelper.registerItem("brown_terracotta_urn", props -> new BlockItem(TGBlocks.BROWN_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CYAN_TERRACOTTA_URN = RegistryHelper.registerItem("cyan_terracotta_urn", props -> new BlockItem(TGBlocks.CYAN_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> GRAY_TERRACOTTA_URN = RegistryHelper.registerItem("gray_terracotta_urn", props -> new BlockItem(TGBlocks.GRAY_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> GREEN_TERRACOTTA_URN = RegistryHelper.registerItem("green_terracotta_urn", props -> new BlockItem(TGBlocks.GREEN_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LIGHT_BLUE_TERRACOTTA_URN = RegistryHelper.registerItem("light_blue_terracotta_urn", props -> new BlockItem(TGBlocks.LIGHT_BLUE_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LIGHT_GRAY_TERRACOTTA_URN = RegistryHelper.registerItem("light_gray_terracotta_urn", props -> new BlockItem(TGBlocks.LIGHT_GRAY_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LIME_TERRACOTTA_URN = RegistryHelper.registerItem("lime_terracotta_urn", props -> new BlockItem(TGBlocks.LIME_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> MAGENTA_TERRACOTTA_URN = RegistryHelper.registerItem("magenta_terracotta_urn", props -> new BlockItem(TGBlocks.MAGENTA_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> ORANGE_TERRACOTTA_URN = RegistryHelper.registerItem("orange_terracotta_urn", props -> new BlockItem(TGBlocks.ORANGE_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> PINK_TERRACOTTA_URN = RegistryHelper.registerItem("pink_terracotta_urn", props -> new BlockItem(TGBlocks.PINK_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> PURPLE_TERRACOTTA_URN = RegistryHelper.registerItem("purple_terracotta_urn", props -> new BlockItem(TGBlocks.PURPLE_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> RED_TERRACOTTA_URN = RegistryHelper.registerItem("red_terracotta_urn", props -> new BlockItem(TGBlocks.RED_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WHITE_TERRACOTTA_URN = RegistryHelper.registerItem("white_terracotta_urn", props -> new BlockItem(TGBlocks.WHITE_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> YELLOW_TERRACOTTA_URN = RegistryHelper.registerItem("yellow_terracotta_urn", props -> new BlockItem(TGBlocks.YELLOW_TERRACOTTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SMALL_BLACK_URN = RegistryHelper.registerItem("small_black_urn", props -> new BlockItem(TGBlocks.SMALL_BLACK_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_BROWN_URN = RegistryHelper.registerItem("small_brown_urn", props -> new BlockItem(TGBlocks.SMALL_BROWN_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_WHITE_URN = RegistryHelper.registerItem("small_white_urn", props -> new BlockItem(TGBlocks.SMALL_WHITE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_GRAY_URN = RegistryHelper.registerItem("small_gray_urn", props -> new BlockItem(TGBlocks.SMALL_GRAY_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_LIGHT_GRAY_URN = RegistryHelper.registerItem("small_light_gray_urn", props -> new BlockItem(TGBlocks.SMALL_LIGHT_GRAY_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_BLUE_URN = RegistryHelper.registerItem("small_blue_urn", props -> new BlockItem(TGBlocks.SMALL_BLUE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_CYAN_URN = RegistryHelper.registerItem("small_cyan_urn", props -> new BlockItem(TGBlocks.SMALL_CYAN_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_LIGHT_BLUE_URN = RegistryHelper.registerItem("small_light_blue_urn", props -> new BlockItem(TGBlocks.SMALL_LIGHT_BLUE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_RED_URN = RegistryHelper.registerItem("small_red_urn", props -> new BlockItem(TGBlocks.SMALL_RED_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_ORANGE_URN = RegistryHelper.registerItem("small_orange_urn", props -> new BlockItem(TGBlocks.SMALL_ORANGE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_YELLOW_URN = RegistryHelper.registerItem("small_yellow_urn", props -> new BlockItem(TGBlocks.SMALL_YELLOW_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_PINK_URN = RegistryHelper.registerItem("small_pink_urn", props -> new BlockItem(TGBlocks.SMALL_PINK_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_MAGENTA_URN = RegistryHelper.registerItem("small_magenta_urn", props -> new BlockItem(TGBlocks.SMALL_MAGENTA_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_PURPLE_URN = RegistryHelper.registerItem("small_purple_urn", props -> new BlockItem(TGBlocks.SMALL_PURPLE_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_GREEN_URN = RegistryHelper.registerItem("small_green_urn", props -> new BlockItem(TGBlocks.SMALL_GREEN_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SMALL_LIME_URN = RegistryHelper.registerItem("small_lime_urn", props -> new BlockItem(TGBlocks.SMALL_LIME_URN.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> SARCOPHAGUS = RegistryHelper.registerItem("sarcophagus", props -> new BlockItem(TGBlocks.SARCOPHAGUS.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> OAK_COFFIN = RegistryHelper.registerItem("oak_coffin", props -> new BlockItem(TGBlocks.OAK_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SPRUCE_COFFIN = RegistryHelper.registerItem("spruce_coffin", props -> new BlockItem(TGBlocks.SPRUCE_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BIRCH_COFFIN = RegistryHelper.registerItem("birch_coffin", props -> new BlockItem(TGBlocks.BIRCH_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> DARK_OAK_COFFIN = RegistryHelper.registerItem("dark_oak_coffin", props -> new BlockItem(TGBlocks.DARK_OAK_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> JUNGLE_COFFIN = RegistryHelper.registerItem("jungle_coffin", props -> new BlockItem(TGBlocks.JUNGLE_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> ACACIA_COFFIN = RegistryHelper.registerItem("acacia_coffin", props -> new BlockItem(TGBlocks.ACACIA_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WARPED_COFFIN = RegistryHelper.registerItem("warped_coffin", props -> new BlockItem(TGBlocks.WARPED_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CRIMSON_COFFIN = RegistryHelper.registerItem("crimson_coffin", props -> new BlockItem(TGBlocks.CRIMSON_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> MANGROVE_COFFIN = RegistryHelper.registerItem("mangrove_coffin", props -> new BlockItem(TGBlocks.MANGROVE_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BAMBOO_COFFIN = RegistryHelper.registerItem("bamboo_coffin", props -> new BlockItem(TGBlocks.BAMBOO_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> CHERRY_COFFIN = RegistryHelper.registerItem("cherry_coffin", props -> new BlockItem(TGBlocks.CHERRY_COFFIN.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> VIAL_OF_BLOOD = RegistryHelper.registerItem("vial_of_blood", VialOfBlood::new, new Item.Properties().stacksTo(1));

    public static final Supplier<Item> TORSO_PILE = RegistryHelper.registerItem("torso_pile", props -> new BlockItem(TGBlocks.TORSO_PILE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_TORSO_PILE = RegistryHelper.registerItem("wither_torso_pile", props -> new BlockItem(TGBlocks.WITHER_TORSO_PILE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> SKULL_ON_PIKE = RegistryHelper.registerItem("skull_on_pike", props -> new BlockItem(TGBlocks.SKULL_ON_PIKE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_SKULL_ON_PIKE = RegistryHelper.registerItem("wither_skull_on_pike", props -> new BlockItem(TGBlocks.WITHER_SKULL_ON_PIKE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> BONE_REMAINS = RegistryHelper.registerItem("bone_remains", props -> new BlockItem(TGBlocks.BONE_REMAINS.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> WITHER_BONE_REMAINS = RegistryHelper.registerItem("wither_bone_remains", props -> new BlockItem(TGBlocks.WITHER_BONE_REMAINS.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LATERALLY_LYING_SKELETON = RegistryHelper.registerItem("laterally_lying_skeleton", props -> new BlockItem(TGBlocks.LATERALLY_LYING_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> LATERALLY_LYING_WITHER_SKELETON = RegistryHelper.registerItem("laterally_lying_wither_skeleton", props -> new BlockItem(TGBlocks.LATERALLY_LYING_WITHER_SKELETON.get(), props), new Item.Properties().useBlockDescriptionPrefix());

    public static final Supplier<Item> WHITE_BONE_STAFF = RegistryHelper.registerItem("white_bone_staff", props -> new BoneStaffItem(props, (byte) 1), new Item.Properties().stacksTo(1));
    public static final Supplier<Item> BLACK_BONE_STAFF = RegistryHelper.registerItem("black_bone_staff", props -> new BoneStaffItem(props, (byte) 2), new Item.Properties().stacksTo(1));
    public static final Supplier<Item> RED_BONE_STAFF = RegistryHelper.registerItem("red_bone_staff", props -> new BoneStaffItem(props, (byte) 3), new Item.Properties().stacksTo(1));
    public static final Supplier<Item> CYAN_BONE_STAFF = RegistryHelper.registerItem("cyan_bone_staff", props -> new BoneStaffItem(props, (byte) 4), new Item.Properties().stacksTo(1));
    public static final Supplier<Item> PURPLE_BONE_STAFF = RegistryHelper.registerItem("purple_bone_staff", props -> new BoneStaffItem(props, (byte) 5), new Item.Properties().stacksTo(1));


    public static final Supplier<Item> ALTAR = RegistryHelper.registerItem("altar", props -> new BlockItem(TGBlocks.ALTAR.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> ALTAR_SIDE = RegistryHelper.registerItem("altar_side", props -> new BlockItem(TGBlocks.ALTAR_SIDE.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> ALTAR_CORNER = RegistryHelper.registerItem("altar_corner", props -> new BlockItem(TGBlocks.ALTAR_CORNER.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> ALTAR_CENTER = RegistryHelper.registerItem("altar_center", props -> new BlockItem(TGBlocks.ALTAR_CENTER.get(), props), new Item.Properties().useBlockDescriptionPrefix());
    public static final Supplier<Item> UPPER_BONE_STAFF = RegistryHelper.registerItem("upper_bone_staff", props -> new TooltipBlockItem(TGBlocks.UPPER_BONE_STAFF.get(), props), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix());
    public static final Supplier<Item> MIDDLE_BONE_STAFF = RegistryHelper.registerItem("middle_bone_staff", props -> new TooltipBlockItem(TGBlocks.MIDDLE_BONE_STAFF.get(), props), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix());
    public static final Supplier<Item> LOWER_BONE_STAFF = RegistryHelper.registerItem("lower_bone_staff", props -> new TooltipBlockItem(TGBlocks.LOWER_BONE_STAFF.get(), props), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix());


    public static void init() {}

    public static void addItemsToGroup() {
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, INCARNATED_EVIL_MUSIC_DISC.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CORRUPTION.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DIARY_PAGE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DARK_IRON_BLOCK.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DARK_IRON_INGOT.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DARK_IRON_DOOR.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DARK_IRON_TRAPDOOR.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DARK_IRON_BARS.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SOUL_FIRE_BRAZIER.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, FIRE_BRAZIER.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, PEDESTAL.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CANDLE_HOLDER.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, COBBLESTONE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MOSSY_COBBLESTONE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DEEPSLATE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BLACKSTONE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GILDED_BLACKSTONE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CRACKED_BLACKSTONE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, STONE_BRICKS_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MOSSY_STONE_BRICKS_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BRICKS_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, QUARTZ_BRICKS_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, RED_SANDSTONE_GRAVESTONE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SANDSTONE_GRAVESTONE.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SKULL_WITH_RIB_CAGE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LEANING_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SKULL_PILE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LYING_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_SKULL_WITH_RIB_CAGE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LEANING_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_SKULL_PILE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LYING_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CREEPER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SKELETON_HAND.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_SKELETON_HAND.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, TORSO_PILE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_TORSO_PILE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SKULL_ON_PIKE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_SKULL_ON_PIKE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BONE_REMAINS.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_BONE_REMAINS.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LATERALLY_LYING_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LATERALLY_LYING_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, HANGED_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, HANGED_WITHER_SKELETON.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SEATED_FULL_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SEATED_FULL_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SKELETON_SKULL_WITH_AXE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WITHER_SKELETON_SKULL_WITH_AXE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SWORD_IN_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SWORD_IN_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_SKELETON_REMAINS.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_WITHER_SKELETON_REMAINS.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, HANGED_FULL_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, HANGED_FULL_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LYING_FULL_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LYING_FULL_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BACKSTABBED_FULL_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BACKSTABBED_FULL_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SHOT_FULL_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SHOT_FULL_WITHER_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SIDEWAYS_FULL_SKELETON.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SIDEWAYS_FULL_WITHER_SKELETON.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, OSSUARY.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BLACK_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GRAY_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LIGHT_GRAY_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WHITE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LIGHT_BLUE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BLUE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CYAN_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GREEN_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LIME_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BLACK_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BLUE_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BROWN_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CYAN_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GRAY_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GREEN_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LIGHT_BLUE_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LIGHT_GRAY_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LIME_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MAGENTA_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ORANGE_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, PINK_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, PURPLE_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, RED_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WHITE_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, YELLOW_TERRACOTTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, PINK_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MAGENTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, PURPLE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, RED_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ORANGE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, YELLOW_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BROWN_URN.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_BLACK_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_GRAY_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_LIGHT_GRAY_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_WHITE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_LIGHT_BLUE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_BLUE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_CYAN_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_GREEN_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_LIME_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_PINK_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_MAGENTA_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_PURPLE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_RED_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_ORANGE_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_YELLOW_URN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMALL_BROWN_URN.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, VASE_BLOCK.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SARCOPHAGUS.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, OAK_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, DARK_OAK_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SPRUCE_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BIRCH_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, JUNGLE_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ACACIA_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WARPED_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CRIMSON_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MANGROVE_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BAMBOO_COFFIN.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHERRY_COFFIN.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SKELETON_CREEPER_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, RAVEN_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MAGPIE_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CROW_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ACOLYTE_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GHOUL_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, REAPER_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, REVENANT_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, TORMENTED_VILLAGER_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, NIGHTMARE_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CORRUPTED_VINDICATOR_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CORRUPTED_EVOKER_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CORRUPTED_PILLAGER_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WRAITH_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LICH_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, NAMELESS_HANGED_SPAWN_EGG.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BONE_DAGGER.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, WHITE_BONE_STAFF.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, BLACK_BONE_STAFF.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, RED_BONE_STAFF.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CYAN_BONE_STAFF.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, PURPLE_BONE_STAFF.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ALTAR.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ALTAR_SIDE.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ALTAR_CORNER.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ALTAR_CENTER.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, UPPER_BONE_STAFF.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, MIDDLE_BONE_STAFF.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, LOWER_BONE_STAFF.get());
        RegistryHelper.addStacksToItemGroup(TGItemGroups.ITEM_GROUP, TGItems::vialStages);
    }

    private static List<ItemStack> vialStages() {
        List<ItemStack> stages = new ArrayList<>();
        float blood = 0.1F;

        for (int stage = 1; stage <= 8; stage++) {
            ItemStack vial = new ItemStack(VIAL_OF_BLOOD.get());
            VialOfBlood.setBlood(vial, blood);
            stages.add(vial);
            blood += 0.1F;
        }

        return stages;
    }
}
