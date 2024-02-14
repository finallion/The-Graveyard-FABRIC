package com.lion.graveyard.init;

import com.lion.graveyard.item.BoneStaffItem;
import com.lion.graveyard.item.DaggerItem;
import com.lion.graveyard.item.VialOfBlood;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;

import java.util.function.Supplier;


public class TGItems {
    public static final Supplier<Item> SARCOPHAGUS_LID = RegistryHelper.registerItem("sarcophagus_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SARCOPHAGUS_BASE = RegistryHelper.registerItem("sarcophagus_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DARK_OAK_COFFIN_LID = RegistryHelper.registerItem("dark_oak_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DARK_OAK_COFFIN_BASE = RegistryHelper.registerItem("dark_oak_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> OAK_COFFIN_LID = RegistryHelper.registerItem("oak_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> OAK_COFFIN_BASE = RegistryHelper.registerItem("oak_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BIRCH_COFFIN_LID = RegistryHelper.registerItem("birch_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BIRCH_COFFIN_BASE = RegistryHelper.registerItem("birch_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> JUNGLE_COFFIN_LID = RegistryHelper.registerItem("jungle_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> JUNGLE_COFFIN_BASE = RegistryHelper.registerItem("jungle_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ACACIA_COFFIN_LID = RegistryHelper.registerItem("acacia_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ACACIA_COFFIN_BASE = RegistryHelper.registerItem("acacia_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SPRUCE_COFFIN_LID = RegistryHelper.registerItem("spruce_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SPRUCE_COFFIN_BASE = RegistryHelper.registerItem("spruce_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> WARPED_COFFIN_LID = RegistryHelper.registerItem("warped_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> WARPED_COFFIN_BASE = RegistryHelper.registerItem("warped_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CRIMSON_COFFIN_LID = RegistryHelper.registerItem("crimson_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CRIMSON_COFFIN_BASE = RegistryHelper.registerItem("crimson_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MANGROVE_COFFIN_LID = RegistryHelper.registerItem("mangrove_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> MANGROVE_COFFIN_BASE = RegistryHelper.registerItem("mangrove_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BAMBOO_COFFIN_LID = RegistryHelper.registerItem("bamboo_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BAMBOO_COFFIN_BASE = RegistryHelper.registerItem("bamboo_coffin_base", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CHERRY_COFFIN_LID = RegistryHelper.registerItem("cherry_coffin_lid", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CHERRY_COFFIN_BASE = RegistryHelper.registerItem("cherry_coffin_base", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> OSSUARY = RegistryHelper.registerItem("ossuary", () -> new BlockItem(TGBlocks.OSSUARY.get(), new Item.Properties()));
    public static final Supplier<Item> INCARNATED_EVIL_MUSIC_DISC = RegistryHelper.registerMusicDiscItem("incarnated_evil_music_disc", 15, TGSounds.LICH_THEME_01, new Item.Properties().maxCount(1), 270);

    public static final Supplier<Item> SKELETON_CREEPER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("skeleton_creeper_spawn_egg", TGEntities.SKELETON_CREEPER, 7960171, 15263976, new Item.Properties());
    public static final Supplier<Item> ACOLYTE_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("acolyte_spawn_egg", TGEntities.ACOLYTE, 2688830, 5898240, new Item.Properties());
    public static final Supplier<Item> GHOUL_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("ghoul_spawn_egg", TGEntities.GHOUL, 6239802, 16487198, new Item.Properties());
    public static final Supplier<Item> REAPER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("reaper_spawn_egg", TGEntities.REAPER, 1381653, 7456477, new Item.Properties());
    public static final Supplier<Item> REVENANT_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("revenant_spawn_egg", TGEntities.REVENANT, 12965589, 9765908, new Item.Properties());
    public static final Supplier<Item> NIGHTMARE_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("nightmare_spawn_egg", TGEntities.NIGHTMARE, 592137, 4718849, new Item.Properties());
    public static final Supplier<Item> LICH_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("lich_spawn_egg", TGEntities.LICH, 13750223, 4144959, new Item.Properties());
    public static final Supplier<Item> NAMELESS_HANGED_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("nameless_hanged_spawn_egg", TGEntities.NAMELESS_HANGED, 5389367, 9803156, new Item.Properties());
    public static final Supplier<Item> WRAITH_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("wraith_spawn_egg", TGEntities.WRAITH, 1644568, 16777215, new Item.Properties());
    public static final Supplier<Item> CORRUPTED_PILLAGER_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("corrupted_pillager_spawn_egg", TGEntities.CORRUPTED_PILLAGER, 7567737, 4924973, new Item.Properties());
    public static final Supplier<Item> CORRUPTED_VINDICATOR_SPAWN_EGG = RegistryHelper.registerSpawnEggItem("corrupted_vindicator_spawn_egg", TGEntities.CORRUPTED_VINDICATOR, 7567737, 2380632, new Item.Properties());

    public static final Supplier<Item> BONE_DAGGER = RegistryHelper.registerItem("bone_dagger", () -> new DaggerItem(Tiers.STONE, 4, -2.0F, new Item.Properties()));
    public static final Supplier<Item> DARK_IRON_INGOT = RegistryHelper.registerItem("dark_iron_ingot", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CORRUPTION = RegistryHelper.registerItem("corruption", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> TG_ROOTED_DIRT = RegistryHelper.registerItem("tg_rooted_dirt", () -> new BlockItem(TGBlocks.TG_ROOTED_DIRT.get(), new Item.Properties()));
    public static final Supplier<Item> TG_TUFF = RegistryHelper.registerItem("tg_tuff", () -> new BlockItem(TGBlocks.TG_TUFF.get(), new Item.Properties()));
    public static final Supplier<Item> TG_MOSS_BLOCK = RegistryHelper.registerItem("tg_moss_block", () -> new BlockItem(TGBlocks.TG_MOSS_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> TG_DEEPSLATE = RegistryHelper.registerItem("tg_deepslate", () -> new BlockItem(TGBlocks.TG_DEEPSLATE.get(), new Item.Properties()));

    // helper blocks that get don't get replaced on world generation, generate ores or generate trees on
    public static final Supplier<Item> TG_GRASS_BLOCK = RegistryHelper.registerItem("tg_grass_block", () -> new BlockItem(TGBlocks.TG_GRASS_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> TG_DIRT = RegistryHelper.registerItem("tg_dirt", () -> new BlockItem(TGBlocks.TG_DIRT.get(), new Item.Properties()));
    public static final Supplier<Item> TG_COARSE_DIRT = RegistryHelper.registerItem("tg_coarse_dirt", () -> new BlockItem(TGBlocks.TG_COARSE_DIRT.get(), new Item.Properties()));
    public static final Supplier<Item> TG_ANDESITE = RegistryHelper.registerItem("tg_andesite", () -> new BlockItem(TGBlocks.TG_ANDESITE.get(), new Item.Properties()));
    public static final Supplier<Item> TG_GRANITE = RegistryHelper.registerItem("tg_granite", () -> new BlockItem(TGBlocks.TG_GRANITE.get(), new Item.Properties()));
    public static final Supplier<Item> TG_DIORITE = RegistryHelper.registerItem("tg_diorite", () -> new BlockItem(TGBlocks.TG_DIORITE.get(), new Item.Properties()));
    public static final Supplier<Item> TG_STONE = RegistryHelper.registerItem("tg_stone", () -> new BlockItem(TGBlocks.TG_STONE.get(), new Item.Properties()));
    public static final Supplier<Item> TG_PODZOL = RegistryHelper.registerItem("tg_podzol", () -> new BlockItem(TGBlocks.TG_PODZOL.get(), new Item.Properties()));

    public static final Supplier<Item> DARK_IRON_BARS = RegistryHelper.registerItem("dark_iron_bars", () -> new BlockItem(TGBlocks.DARK_IRON_BARS.get(), new Item.Properties()));
    public static final Supplier<Item> SOUL_FIRE_BRAZIER = RegistryHelper.registerItem("soul_fire_brazier", () -> new BlockItem(TGBlocks.SOUL_FIRE_BRAZIER.get(), new Item.Properties()));
    public static final Supplier<Item> FIRE_BRAZIER = RegistryHelper.registerItem("fire_brazier", () -> new BlockItem(TGBlocks.FIRE_BRAZIER.get(), new Item.Properties()));
    public static final Supplier<Item> PEDESTAL = RegistryHelper.registerItem("pedestal", () -> new BlockItem(TGBlocks.PEDESTAL.get(), new Item.Properties()));
    public static final Supplier<Item> CANDLE_HOLDER = RegistryHelper.registerItem("candle_holder", () -> new BlockItem(TGBlocks.CANDLE_HOLDER.get(), new Item.Properties()));
    public static final Supplier<Item> DARK_IRON_TRAPDOOR = RegistryHelper.registerItem("dark_iron_trapdoor", () -> new BlockItem(TGBlocks.DARK_IRON_TRAPDOOR.get(), new Item.Properties()));
    public static final Supplier<Item> DARK_IRON_DOOR = RegistryHelper.registerItem("dark_iron_door", () -> new BlockItem(TGBlocks.DARK_IRON_DOOR.get(), new Item.Properties()));
    public static final Supplier<Item> DARK_IRON_BLOCK = RegistryHelper.registerItem("dark_iron_block", () -> new BlockItem(TGBlocks.DARK_IRON_BLOCK.get(), new Item.Properties()));

    public static final Supplier<Item> SKULL_WITH_RIB_CAGE = RegistryHelper.registerItem("skull_with_rib_cage", () -> new BlockItem(TGBlocks.SKULL_WITH_RIB_CAGE.get(), new Item.Properties()));
    public static final Supplier<Item> LEANING_SKELETON = RegistryHelper.registerItem("leaning_skeleton", () -> new BlockItem(TGBlocks.LEANING_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> SKULL_PILE = RegistryHelper.registerItem("skull_pile", () -> new BlockItem(TGBlocks.SKULL_PILE.get(), new Item.Properties()));
    public static final Supplier<Item> LYING_SKELETON = RegistryHelper.registerItem("lying_skeleton", () -> new BlockItem(TGBlocks.LYING_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> WITHER_SKULL_WITH_RIB_CAGE = RegistryHelper.registerItem("wither_skull_with_rib_cage", () -> new BlockItem(TGBlocks.WITHER_SKULL_WITH_RIB_CAGE.get(), new Item.Properties()));
    public static final Supplier<Item> LEANING_WITHER_SKELETON = RegistryHelper.registerItem("leaning_wither_skeleton", () -> new BlockItem(TGBlocks.LEANING_WITHER_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> WITHER_SKULL_PILE = RegistryHelper.registerItem("wither_skull_pile", () -> new BlockItem(TGBlocks.WITHER_SKULL_PILE.get(), new Item.Properties()));
    public static final Supplier<Item> LYING_WITHER_SKELETON = RegistryHelper.registerItem("lying_wither_skeleton", () -> new BlockItem(TGBlocks.LYING_WITHER_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> CREEPER_SKELETON = RegistryHelper.registerItem("creeper_skeleton", () -> new BlockItem(TGBlocks.CREEPER_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> SKELETON_HAND = RegistryHelper.registerItem("skeleton_hand", () -> new BlockItem(TGBlocks.SKELETON_HAND.get(), new Item.Properties()));
    public static final Supplier<Item> WITHER_SKELETON_HAND = RegistryHelper.registerItem("wither_skeleton_hand", () -> new BlockItem(TGBlocks.WITHER_SKELETON_HAND.get(), new Item.Properties()));
    public static final Supplier<Item> HANGED_SKELETON = RegistryHelper.registerItem("hanged_skeleton", () -> new BlockItem(TGBlocks.HANGED_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> HANGED_WITHER_SKELETON = RegistryHelper.registerItem("hanged_wither_skeleton", () -> new BlockItem(TGBlocks.HANGED_WITHER_SKELETON.get(), new Item.Properties()));

    public static final Supplier<Item> VASE_BLOCK = RegistryHelper.registerItem("vase_block", () -> new BlockItem(TGBlocks.VASE_BLOCK.get(), new Item.Properties()));

    public static final Supplier<Item> GRAVESTONE = RegistryHelper.registerItem("gravestone", () -> new BlockItem(TGBlocks.GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> COBBLESTONE_GRAVESTONE = RegistryHelper.registerItem( "cobblestone_gravestone", () -> new BlockItem(TGBlocks.COBBLESTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> MOSSY_COBBLESTONE_GRAVESTONE = RegistryHelper.registerItem("mossy_cobblestone_gravestone", () -> new BlockItem(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> DEEPSLATE_GRAVESTONE = RegistryHelper.registerItem("deepslate_gravestone", () -> new BlockItem(TGBlocks.DEEPSLATE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> BLACKSTONE_GRAVESTONE = RegistryHelper.registerItem("blackstone_gravestone", () -> new BlockItem(TGBlocks.BLACKSTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> CRACKED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerItem("cracked_blackstone_gravestone", () -> new BlockItem(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> STONE_BRICKS_GRAVESTONE = RegistryHelper.registerItem("stone_bricks_gravestone", () -> new BlockItem(TGBlocks.STONE_BRICKS_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> MOSSY_STONE_BRICKS_GRAVESTONE = RegistryHelper.registerItem("mossy_stone_bricks_gravestone", () -> new BlockItem(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> BRICKS_GRAVESTONE = RegistryHelper.registerItem("bricks_gravestone", () -> new BlockItem(TGBlocks.BRICKS_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> GILDED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerItem("gilded_blackstone_gravestone", () -> new BlockItem(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> SANDSTONE_GRAVESTONE = RegistryHelper.registerItem("sandstone_gravestone", () -> new BlockItem(TGBlocks.SANDSTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> RED_SANDSTONE_GRAVESTONE = RegistryHelper.registerItem("red_sandstone_gravestone", () -> new BlockItem(TGBlocks.RED_SANDSTONE_GRAVESTONE.get(), new Item.Properties()));
    public static final Supplier<Item> QUARTZ_BRICKS_GRAVESTONE = RegistryHelper.registerItem("quartz_bricks_gravestone", () -> new BlockItem(TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(), new Item.Properties()));

    public static final Supplier<Item> BLACK_URN = RegistryHelper.registerItem("black_urn", () -> new BlockItem(TGBlocks.BLACK_URN.get(), new Item.Properties()));
    public static final Supplier<Item> BROWN_URN = RegistryHelper.registerItem("brown_urn", () -> new BlockItem(TGBlocks.BROWN_URN.get(), new Item.Properties()));
    public static final Supplier<Item> WHITE_URN = RegistryHelper.registerItem("white_urn", () -> new BlockItem(TGBlocks.WHITE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> GRAY_URN = RegistryHelper.registerItem("gray_urn", () -> new BlockItem(TGBlocks.GRAY_URN.get(), new Item.Properties()));
    public static final Supplier<Item> LIGHT_GRAY_URN = RegistryHelper.registerItem("light_gray_urn", () -> new BlockItem(TGBlocks.LIGHT_GRAY_URN.get(), new Item.Properties()));
    public static final Supplier<Item> BLUE_URN = RegistryHelper.registerItem("blue_urn", () -> new BlockItem(TGBlocks.BLUE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> CYAN_URN = RegistryHelper.registerItem("cyan_urn", () -> new BlockItem(TGBlocks.CYAN_URN.get(), new Item.Properties()));
    public static final Supplier<Item> LIGHT_BLUE_URN = RegistryHelper.registerItem("light_blue_urn", () -> new BlockItem(TGBlocks.LIGHT_BLUE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> RED_URN = RegistryHelper.registerItem("red_urn", () -> new BlockItem(TGBlocks.RED_URN.get(), new Item.Properties()));
    public static final Supplier<Item> ORANGE_URN = RegistryHelper.registerItem("orange_urn", () -> new BlockItem(TGBlocks.ORANGE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> YELLOW_URN = RegistryHelper.registerItem("yellow_urn", () -> new BlockItem(TGBlocks.YELLOW_URN.get(), new Item.Properties()));
    public static final Supplier<Item> PINK_URN = RegistryHelper.registerItem("pink_urn", () -> new BlockItem(TGBlocks.PINK_URN.get(), new Item.Properties()));
    public static final Supplier<Item> MAGENTA_URN = RegistryHelper.registerItem("magenta_urn", () -> new BlockItem(TGBlocks.MAGENTA_URN.get(), new Item.Properties()));
    public static final Supplier<Item> PURPLE_URN = RegistryHelper.registerItem("purple_urn", () -> new BlockItem(TGBlocks.PURPLE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> GREEN_URN = RegistryHelper.registerItem("green_urn", () -> new BlockItem(TGBlocks.GREEN_URN.get(), new Item.Properties()));
    public static final Supplier<Item> LIME_URN = RegistryHelper.registerItem("lime_urn", () -> new BlockItem(TGBlocks.LIME_URN.get(), new Item.Properties()));

    public static final Supplier<Item> SMALL_BLACK_URN = RegistryHelper.registerItem("small_black_urn", () -> new BlockItem(TGBlocks.SMALL_BLACK_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_BROWN_URN = RegistryHelper.registerItem("small_brown_urn", () -> new BlockItem(TGBlocks.SMALL_BROWN_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_WHITE_URN = RegistryHelper.registerItem("small_white_urn", () -> new BlockItem(TGBlocks.SMALL_WHITE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_GRAY_URN = RegistryHelper.registerItem("small_gray_urn", () -> new BlockItem(TGBlocks.SMALL_GRAY_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_LIGHT_GRAY_URN = RegistryHelper.registerItem("small_light_gray_urn", () -> new BlockItem(TGBlocks.SMALL_LIGHT_GRAY_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_BLUE_URN = RegistryHelper.registerItem("small_blue_urn", () -> new BlockItem(TGBlocks.SMALL_BLUE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_CYAN_URN = RegistryHelper.registerItem("small_cyan_urn", () -> new BlockItem(TGBlocks.SMALL_CYAN_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_LIGHT_BLUE_URN = RegistryHelper.registerItem("small_light_blue_urn", () -> new BlockItem(TGBlocks.SMALL_LIGHT_BLUE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_RED_URN = RegistryHelper.registerItem("small_red_urn", () -> new BlockItem(TGBlocks.SMALL_RED_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_ORANGE_URN = RegistryHelper.registerItem("small_orange_urn", () -> new BlockItem(TGBlocks.SMALL_ORANGE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_YELLOW_URN = RegistryHelper.registerItem("small_yellow_urn", () -> new BlockItem(TGBlocks.SMALL_YELLOW_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_PINK_URN = RegistryHelper.registerItem("small_pink_urn", () -> new BlockItem(TGBlocks.SMALL_PINK_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_MAGENTA_URN = RegistryHelper.registerItem("small_magenta_urn", () -> new BlockItem(TGBlocks.SMALL_MAGENTA_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_PURPLE_URN = RegistryHelper.registerItem("small_purple_urn", () -> new BlockItem(TGBlocks.SMALL_PURPLE_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_GREEN_URN = RegistryHelper.registerItem("small_green_urn", () -> new BlockItem(TGBlocks.SMALL_GREEN_URN.get(), new Item.Properties()));
    public static final Supplier<Item> SMALL_LIME_URN = RegistryHelper.registerItem("small_lime_urn", () -> new BlockItem(TGBlocks.SMALL_LIME_URN.get(), new Item.Properties()));

    public static final Supplier<Item> SARCOPHAGUS = RegistryHelper.registerItem("sarcophagus", () -> new BlockItem(TGBlocks.SARCOPHAGUS.get(), new Item.Properties()));
    public static final Supplier<Item> OAK_COFFIN = RegistryHelper.registerItem("oak_coffin", () -> new BlockItem(TGBlocks.OAK_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> SPRUCE_COFFIN = RegistryHelper.registerItem("spruce_coffin", () -> new BlockItem(TGBlocks.SPRUCE_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> BIRCH_COFFIN = RegistryHelper.registerItem("birch_coffin", () -> new BlockItem(TGBlocks.BIRCH_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> DARK_OAK_COFFIN = RegistryHelper.registerItem("dark_oak_coffin", () -> new BlockItem(TGBlocks.DARK_OAK_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> JUNGLE_COFFIN = RegistryHelper.registerItem("jungle_coffin", () -> new BlockItem(TGBlocks.JUNGLE_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> ACACIA_COFFIN = RegistryHelper.registerItem("acacia_coffin", () -> new BlockItem(TGBlocks.ACACIA_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> WARPED_COFFIN = RegistryHelper.registerItem("warped_coffin", () -> new BlockItem(TGBlocks.WARPED_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> CRIMSON_COFFIN = RegistryHelper.registerItem("crimson_coffin", () -> new BlockItem(TGBlocks.CRIMSON_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> MANGROVE_COFFIN = RegistryHelper.registerItem("mangrove_coffin", () -> new BlockItem(TGBlocks.MANGROVE_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> BAMBOO_COFFIN = RegistryHelper.registerItem("bamboo_coffin", () -> new BlockItem(TGBlocks.BAMBOO_COFFIN.get(), new Item.Properties()));
    public static final Supplier<Item> CHERRY_COFFIN = RegistryHelper.registerItem("cherry_coffin", () -> new BlockItem(TGBlocks.CHERRY_COFFIN.get(), new Item.Properties()));

    public static final Supplier<Item> VIAL_OF_BLOOD = RegistryHelper.registerItem("vial_of_blood", VialOfBlood::new);

    public static final Supplier<Item> TORSO_PILE = RegistryHelper.registerItem("torso_pile", () -> new BlockItem(TGBlocks.TORSO_PILE.get(), new Item.Properties()));
    public static final Supplier<Item> WITHER_TORSO_PILE = RegistryHelper.registerItem("wither_torso_pile", () -> new BlockItem(TGBlocks.WITHER_TORSO_PILE.get(), new Item.Properties()));
    public static final Supplier<Item> SKULL_ON_PIKE = RegistryHelper.registerItem("skull_on_pike", () -> new BlockItem(TGBlocks.SKULL_ON_PIKE.get(), new Item.Properties()));
    public static final Supplier<Item> WITHER_SKULL_ON_PIKE = RegistryHelper.registerItem("wither_skull_on_pike", () -> new BlockItem(TGBlocks.WITHER_SKULL_ON_PIKE.get(), new Item.Properties()));
    public static final Supplier<Item> BONE_REMAINS = RegistryHelper.registerItem("bone_remains", () -> new BlockItem(TGBlocks.BONE_REMAINS.get(), new Item.Properties()));
    public static final Supplier<Item> WITHER_BONE_REMAINS = RegistryHelper.registerItem("wither_bone_remains", () -> new BlockItem(TGBlocks.WITHER_BONE_REMAINS.get(), new Item.Properties()));
    public static final Supplier<Item> LATERALLY_LYING_SKELETON = RegistryHelper.registerItem("laterally_lying_skeleton", () -> new BlockItem(TGBlocks.LATERALLY_LYING_SKELETON.get(), new Item.Properties()));
    public static final Supplier<Item> LATERALLY_LYING_WITHER_SKELETON = RegistryHelper.registerItem("laterally_lying_wither_skeleton", () -> new BlockItem(TGBlocks.LATERALLY_LYING_WITHER_SKELETON.get(), new Item.Properties()));

    public static final Supplier<Item> WHITE_BONE_STAFF = RegistryHelper.registerItem("white_bone_staff", () -> new BoneStaffItem((byte) 1));
    public static final Supplier<Item> BLACK_BONE_STAFF = RegistryHelper.registerItem("black_bone_staff", () -> new BoneStaffItem((byte) 2));
    public static final Supplier<Item> RED_BONE_STAFF = RegistryHelper.registerItem("red_bone_staff", () -> new BoneStaffItem((byte) 3));
    public static final Supplier<Item> CYAN_BONE_STAFF = RegistryHelper.registerItem("cyan_bone_staff", () -> new BoneStaffItem((byte) 4));
    public static final Supplier<Item> PURPLE_BONE_STAFF = RegistryHelper.registerItem("purple_bone_staff", () -> new BoneStaffItem((byte) 5));


    public static final Supplier<Item> ALTAR = RegistryHelper.registerItem("altar", () -> new BlockItem(TGBlocks.ALTAR.get(), new Item.Properties()));
    public static final Supplier<Item> ALTAR_SIDE = RegistryHelper.registerItem("altar_side", () -> new BlockItem(TGBlocks.ALTAR_SIDE.get(), new Item.Properties()));
    public static final Supplier<Item> ALTAR_CORNER = RegistryHelper.registerItem("altar_corner", () -> new BlockItem(TGBlocks.ALTAR_CORNER.get(), new Item.Properties()));
    public static final Supplier<Item> ALTAR_CENTER = RegistryHelper.registerItem("altar_center", () -> new BlockItem(TGBlocks.ALTAR_CENTER.get(), new Item.Properties()));
    public static final Supplier<Item> UPPER_BONE_STAFF = RegistryHelper.registerItem("upper_bone_staff", () -> new BlockItem(TGBlocks.UPPER_BONE_STAFF.get(), new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> MIDDLE_BONE_STAFF = RegistryHelper.registerItem("middle_bone_staff", () -> new BlockItem(TGBlocks.MIDDLE_BONE_STAFF.get(), new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> LOWER_BONE_STAFF = RegistryHelper.registerItem("lower_bone_staff", () -> new BlockItem(TGBlocks.LOWER_BONE_STAFF.get(), new Item.Properties().stacksTo(1)));


    public static void init() {}

    public static void addItemsToGroup() {
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, INCARNATED_EVIL_MUSIC_DISC.get());

        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CORRUPTION.get());
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
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, ACOLYTE_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, GHOUL_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, REAPER_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, REVENANT_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, NIGHTMARE_SPAWN_EGG.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CORRUPTED_VINDICATOR_SPAWN_EGG.get());
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
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, VIAL_OF_BLOOD.get());
    }

}
