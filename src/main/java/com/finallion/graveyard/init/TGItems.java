package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class TGItems {
    public static final Item INCARNATED_EVIL_MUSIC_DISC = new MusicDiscItem(15, TGSounds.LICH_THEME_01, new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), 270);

    public static final Item SARCOPHAGUS_LID = new Item(new FabricItemSettings());
    public static final Item SARCOPHAGUS_BASE = new Item(new FabricItemSettings());
    public static final Item DARK_OAK_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item DARK_OAK_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item OAK_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item OAK_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item BIRCH_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item BIRCH_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item JUNGLE_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item JUNGLE_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item ACACIA_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item ACACIA_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item SPRUCE_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item SPRUCE_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item WARPED_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item WARPED_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item CRIMSON_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item CRIMSON_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item MANGROVE_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item MANGROVE_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item BAMBOO_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item BAMBOO_COFFIN_BASE = new Item(new FabricItemSettings());
    public static final Item CHERRY_COFFIN_LID = new Item(new FabricItemSettings());
    public static final Item CHERRY_COFFIN_BASE = new Item(new FabricItemSettings());

    public static final Item VIAL_OF_BLOOD = new VialOfBlood();

    public static final Item CORRUPTION = new Item(new FabricItemSettings());
    public static final Item DARK_IRON_BLOCK = new BlockItem(TGBlocks.DARK_IRON_BLOCK, new FabricItemSettings());
    public static final Item DARK_IRON_INGOT = new Item(new FabricItemSettings());
    public static final Item DARK_IRON_DOOR = new BlockItem(TGBlocks.DARK_IRON_DOOR, new FabricItemSettings());
    public static final Item DARK_IRON_TRAPDOOR = new BlockItem(TGBlocks.DARK_IRON_TRAPDOOR, new FabricItemSettings());
    public static final Item DARK_IRON_BARS = new BlockItem(TGBlocks.DARK_IRON_BARS, new FabricItemSettings());
    public static final Item SOUL_FIRE_BRAZIER = new BlockItem(TGBlocks.SOUL_FIRE_BRAZIER, new FabricItemSettings());
    public static final Item FIRE_BRAZIER = new BlockItem(TGBlocks.FIRE_BRAZIER, new FabricItemSettings());
    public static final Item PEDESTAL = new BlockItem(TGBlocks.PEDESTAL, new FabricItemSettings());
    public static final Item CANDLE_HOLDER = new BlockItem(TGBlocks.CANDLE_HOLDER, new FabricItemSettings());
    public static final Item GRAVESTONE = new BlockItem(TGBlocks.GRAVESTONE, new FabricItemSettings());
    public static final Item COBBLESTONE_GRAVESTONE = new BlockItem(TGBlocks.COBBLESTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item MOSSY_COBBLESTONE_GRAVESTONE = new BlockItem(TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item DEEPSLATE_GRAVESTONE = new BlockItem(TGBlocks.DEEPSLATE_GRAVESTONE, new FabricItemSettings());
    public static final Item BLACKSTONE_GRAVESTONE = new BlockItem(TGBlocks.BLACKSTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item CRACKED_BLACKSTONE_GRAVESTONE = new BlockItem(TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item STONE_BRICKS_GRAVESTONE = new BlockItem(TGBlocks.STONE_BRICKS_GRAVESTONE, new FabricItemSettings());
    public static final Item MOSSY_STONE_BRICKS_GRAVESTONE = new BlockItem(TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE, new FabricItemSettings());
    public static final Item BRICKS_GRAVESTONE = new BlockItem(TGBlocks.BRICKS_GRAVESTONE, new FabricItemSettings());

    public static final Item GILDED_BLACKSTONE_GRAVESTONE = new BlockItem(TGBlocks.GILDED_BLACKSTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item SANDSTONE_GRAVESTONE = new BlockItem(TGBlocks.SANDSTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item RED_SANDSTONE_GRAVESTONE = new BlockItem(TGBlocks.RED_SANDSTONE_GRAVESTONE, new FabricItemSettings());
    public static final Item QUARTZ_BRICKS_GRAVESTONE = new BlockItem(TGBlocks.QUARTZ_BRICKS_GRAVESTONE, new FabricItemSettings());

    public static final Item SKULL_WITH_RIB_CAGE = new BlockItem(TGBlocks.SKULL_WITH_RIB_CAGE, new FabricItemSettings());
    public static final Item LEANING_SKELETON = new BlockItem(TGBlocks.LEANING_SKELETON, new FabricItemSettings());
    public static final Item SKULL_PILE = new BlockItem(TGBlocks.SKULL_PILE, new FabricItemSettings());
    public static final Item LYING_SKELETON = new BlockItem(TGBlocks.LYING_SKELETON, new FabricItemSettings());
    public static final Item WITHER_SKULL_WITH_RIB_CAGE = new BlockItem(TGBlocks.WITHER_SKULL_WITH_RIB_CAGE, new FabricItemSettings());
    public static final Item LEANING_WITHER_SKELETON = new BlockItem(TGBlocks.LEANING_WITHER_SKELETON, new FabricItemSettings());
    public static final Item WITHER_SKULL_PILE = new BlockItem(TGBlocks.WITHER_SKULL_PILE, new FabricItemSettings());
    public static final Item LYING_WITHER_SKELETON = new BlockItem(TGBlocks.LYING_WITHER_SKELETON, new FabricItemSettings());
    public static final Item CREEPER_SKELETON = new BlockItem(TGBlocks.CREEPER_SKELETON, new FabricItemSettings());
    public static final Item SKELETON_HAND = new BlockItem(TGBlocks.SKELETON_HAND, new FabricItemSettings());
    public static final Item WITHER_SKELETON_HAND = new BlockItem(TGBlocks.WITHER_SKELETON_HAND, new FabricItemSettings());
    public static final Item TORSO_PILE = new BlockItem(TGBlocks.TORSO_PILE, new FabricItemSettings());
    public static final Item WITHER_TORSO_PILE = new BlockItem(TGBlocks.WITHER_TORSO_PILE, new FabricItemSettings());
    public static final Item SKULL_ON_PIKE = new BlockItem(TGBlocks.SKULL_ON_PIKE, new FabricItemSettings());
    public static final Item WITHER_SKULL_ON_PIKE = new BlockItem(TGBlocks.WITHER_SKULL_ON_PIKE, new FabricItemSettings());
    public static final Item BONE_REMAINS = new BlockItem(TGBlocks.BONE_REMAINS, new FabricItemSettings());
    public static final Item WITHER_BONE_REMAINS = new BlockItem(TGBlocks.WITHER_BONE_REMAINS, new FabricItemSettings());
    public static final Item LATERALLY_LYING_SKELETON = new BlockItem(TGBlocks.LATERALLY_LYING_SKELETON, new FabricItemSettings());
    public static final Item LATERALLY_LYING_WITHER_SKELETON = new BlockItem(TGBlocks.LATERALLY_LYING_WITHER_SKELETON, new FabricItemSettings());
    public static final Item HANGED_SKELETON = new BlockItem(TGBlocks.HANGED_SKELETON, new FabricItemSettings());
    public static final Item HANGED_WITHER_SKELETON = new BlockItem(TGBlocks.HANGED_WITHER_SKELETON, new FabricItemSettings());

    public static final Item BLACK_URN = new BlockItem(TGBlocks.BLACK_URN, new FabricItemSettings());
    public static final Item GRAY_URN = new BlockItem(TGBlocks.GRAY_URN, new FabricItemSettings());
    public static final Item LIGHT_GRAY_URN = new BlockItem(TGBlocks.LIGHT_GRAY_URN, new FabricItemSettings());
    public static final Item WHITE_URN = new BlockItem(TGBlocks.WHITE_URN, new FabricItemSettings());
    public static final Item LIGHT_BLUE_URN = new BlockItem(TGBlocks.LIGHT_BLUE_URN, new FabricItemSettings());
    public static final Item BLUE_URN = new BlockItem(TGBlocks.BLUE_URN, new FabricItemSettings());
    public static final Item CYAN_URN = new BlockItem(TGBlocks.CYAN_URN, new FabricItemSettings());
    public static final Item GREEN_URN = new BlockItem(TGBlocks.GREEN_URN, new FabricItemSettings());
    public static final Item LIME_URN = new BlockItem(TGBlocks.LIME_URN, new FabricItemSettings());
    public static final Item PINK_URN = new BlockItem(TGBlocks.PINK_URN, new FabricItemSettings());
    public static final Item MAGENTA_URN = new BlockItem(TGBlocks.MAGENTA_URN, new FabricItemSettings());
    public static final Item PURPLE_URN = new BlockItem(TGBlocks.PURPLE_URN, new FabricItemSettings());
    public static final Item RED_URN = new BlockItem(TGBlocks.RED_URN, new FabricItemSettings());
    public static final Item ORANGE_URN = new BlockItem(TGBlocks.ORANGE_URN, new FabricItemSettings());
    public static final Item YELLOW_URN = new BlockItem(TGBlocks.YELLOW_URN, new FabricItemSettings());
    public static final Item BROWN_URN = new BlockItem(TGBlocks.BROWN_URN, new FabricItemSettings());


    public static final Item SMALL_BLACK_URN = new BlockItem(TGBlocks.SMALL_BLACK_URN, new FabricItemSettings());
    public static final Item SMALL_GRAY_URN = new BlockItem(TGBlocks.SMALL_GRAY_URN, new FabricItemSettings());
    public static final Item SMALL_LIGHT_GRAY_URN = new BlockItem(TGBlocks.SMALL_LIGHT_GRAY_URN, new FabricItemSettings());
    public static final Item SMALL_WHITE_URN = new BlockItem(TGBlocks.SMALL_WHITE_URN, new FabricItemSettings());
    public static final Item SMALL_LIGHT_BLUE_URN = new BlockItem(TGBlocks.SMALL_LIGHT_BLUE_URN, new FabricItemSettings());
    public static final Item SMALL_BLUE_URN = new BlockItem(TGBlocks.SMALL_BLUE_URN, new FabricItemSettings());
    public static final Item SMALL_CYAN_URN = new BlockItem(TGBlocks.SMALL_CYAN_URN, new FabricItemSettings());
    public static final Item SMALL_GREEN_URN = new BlockItem(TGBlocks.SMALL_GREEN_URN, new FabricItemSettings());
    public static final Item SMALL_LIME_URN = new BlockItem(TGBlocks.SMALL_LIME_URN, new FabricItemSettings());
    public static final Item SMALL_PINK_URN = new BlockItem(TGBlocks.SMALL_PINK_URN, new FabricItemSettings());
    public static final Item SMALL_MAGENTA_URN = new BlockItem(TGBlocks.SMALL_MAGENTA_URN, new FabricItemSettings());
    public static final Item SMALL_PURPLE_URN = new BlockItem(TGBlocks.SMALL_PURPLE_URN, new FabricItemSettings());
    public static final Item SMALL_RED_URN = new BlockItem(TGBlocks.SMALL_RED_URN, new FabricItemSettings());
    public static final Item SMALL_ORANGE_URN = new BlockItem(TGBlocks.SMALL_ORANGE_URN, new FabricItemSettings());
    public static final Item SMALL_YELLOW_URN = new BlockItem(TGBlocks.SMALL_YELLOW_URN, new FabricItemSettings());
    public static final Item SMALL_BROWN_URN = new BlockItem(TGBlocks.SMALL_BROWN_URN, new FabricItemSettings());

    public static final Item VASE_BLOCK = new BlockItem(TGBlocks.VASE_BLOCK, new FabricItemSettings());

    public static final Item SARCOPHAGUS = new SarcophagusItem(new FabricItemSettings(), TGBlocks.SARCOPHAGUS);
    public static final Item OAK_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.OAK_COFFIN);
    public static final Item DARK_OAK_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.DARK_OAK_COFFIN);
    public static final Item SPRUCE_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.SPRUCE_COFFIN);
    public static final Item BIRCH_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.BIRCH_COFFIN);
    public static final Item JUNGLE_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.JUNGLE_COFFIN);
    public static final Item ACACIA_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.ACACIA_COFFIN);
    public static final Item WARPED_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.WARPED_COFFIN);
    public static final Item CRIMSON_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.CRIMSON_COFFIN);
    public static final Item MANGROVE_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.MANGROVE_COFFIN);
    public static final Item BAMBOO_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.BAMBOO_COFFIN);
    public static final Item CHERRY_COFFIN = new SarcophagusItem(new FabricItemSettings(), TGBlocks.CHERRY_COFFIN);

    public static final Item SKELETON_CREEPER_SPAWN_EGG = new SpawnEggItem(TGEntities.SKELETON_CREEPER, 7960171, 15263976, new FabricItemSettings());
    public static final Item ACOLYTE_SPAWN_EGG = new SpawnEggItem(TGEntities.ACOLYTE, 2688830, 5898240, new FabricItemSettings());
    public static final Item GHOUL_SPAWN_EGG = new SpawnEggItem(TGEntities.GHOUL, 6239802, 16487198, new FabricItemSettings());
    public static final Item REAPER_SPAWN_EGG = new SpawnEggItem(TGEntities.REAPER, 1381653, 7456477, new FabricItemSettings());
    public static final Item REVENANT_SPAWN_EGG = new SpawnEggItem(TGEntities.REVENANT, 12965589, 9765908, new FabricItemSettings());
    public static final Item NIGHTMARE_SPAWN_EGG = new SpawnEggItem(TGEntities.NIGHTMARE, 592137, 4718849, new FabricItemSettings());
    public static final Item CORRUPTED_VINDICATOR_SPAWN_EGG = new SpawnEggItem(TGEntities.CORRUPTED_VINDICATOR, 7567737, 2380632, new FabricItemSettings());
    public static final Item CORRUPTED_PILLAGER_SPAWN_EGG = new SpawnEggItem(TGEntities.CORRUPTED_PILLAGER, 7567737, 4924973, new FabricItemSettings());
    public static final Item WRAITH_SPAWN_EGG = new SpawnEggItem(TGEntities.WRAITH, 16777215, 1644568, new FabricItemSettings());
    public static final Item LICH_SPAWN_EGG = new SpawnEggItem(TGEntities.LICH, 13750223, 4144959, new FabricItemSettings());
    public static final Item NAMELESS_HANGED_SPAWN_EGG = new SpawnEggItem(TGEntities.NAMELESS_HANGED, 5389367, 9803156, new FabricItemSettings());

    public static final Item BONE_DAGGER = new DaggerItem(ToolMaterials.STONE, 4.0F, -2.0F, new Item.Settings().maxCount(1));
    public static final Item WHITE_BONE_STAFF = new BoneStaffItem((byte) 1);
    public static final Item BLACK_BONE_STAFF = new BoneStaffItem((byte) 2);
    public static final Item RED_BONE_STAFF = new BoneStaffItem((byte) 3);
    public static final Item CYAN_BONE_STAFF = new BoneStaffItem((byte) 4);
    public static final Item PURPLE_BONE_STAFF = new BoneStaffItem((byte) 5);

    public static final Item ALTAR = new BlockItem(TGBlocks.ALTAR, new FabricItemSettings());
    public static final Item ALTAR_SIDE = new BlockItem(TGBlocks.ALTAR_SIDE, new FabricItemSettings());
    public static final Item ALTAR_CORNER = new BlockItem(TGBlocks.ALTAR_CORNER, new FabricItemSettings());
    public static final Item ALTAR_CENTER = new BlockItem(TGBlocks.ALTAR_CENTER, new FabricItemSettings());
    public static final Item UPPER_BONE_STAFF = new BlockItem(TGBlocks.UPPER_BONE_STAFF, new FabricItemSettings().maxCount(1));
    public static final Item MIDDLE_BONE_STAFF = new BlockItem(TGBlocks.MIDDLE_BONE_STAFF, new FabricItemSettings().maxCount(1));
    public static final Item LOWER_BONE_STAFF = new BlockItem(TGBlocks.LOWER_BONE_STAFF, new FabricItemSettings().maxCount(1));

    public static final Item OSSUARY = new BlockItem(TGBlocks.OSSUARY, new FabricItemSettings());


    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "incarnated_evil_music_disc"), INCARNATED_EVIL_MUSIC_DISC);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_deepslate"), new BlockItem(TGBlocks.TG_DEEPSLATE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_grass_block"), new BlockItem(TGBlocks.TG_GRASS_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_moss_block"), new BlockItem(TGBlocks.TG_MOSS_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_stone"), new BlockItem(TGBlocks.TG_STONE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_andesite"), new BlockItem(TGBlocks.TG_ANDESITE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_granite"), new BlockItem(TGBlocks.TG_GRANITE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_diorite"), new BlockItem(TGBlocks.TG_DIORITE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_tuff"), new BlockItem(TGBlocks.TG_TUFF, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_dirt"), new BlockItem(TGBlocks.TG_DIRT, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_coarse_dirt"), new BlockItem(TGBlocks.TG_COARSE_DIRT, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_rooted_dirt"), new BlockItem(TGBlocks.TG_ROOTED_DIRT, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "tg_podzol"), new BlockItem(TGBlocks.TG_PODZOL, new FabricItemSettings()));

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "corruption"), CORRUPTION);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_iron_block"), DARK_IRON_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_iron_ingot"), DARK_IRON_INGOT);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_iron_door"), DARK_IRON_DOOR);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_iron_trapdoor"), DARK_IRON_TRAPDOOR);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_iron_bars"), DARK_IRON_BARS);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "soul_fire_brazier"), SOUL_FIRE_BRAZIER);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "fire_brazier"), FIRE_BRAZIER);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "pedestal"), PEDESTAL);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "candle_holder"), CANDLE_HOLDER);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "gravestone"), GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cobblestone_gravestone"), COBBLESTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "mossy_cobblestone_gravestone"), MOSSY_COBBLESTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "deepslate_gravestone"), DEEPSLATE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "blackstone_gravestone"), BLACKSTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cracked_blackstone_gravestone"), CRACKED_BLACKSTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "stone_bricks_gravestone"), STONE_BRICKS_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "mossy_stone_bricks_gravestone"), MOSSY_STONE_BRICKS_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "bricks_gravestone"), BRICKS_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "gilded_blackstone_gravestone"), GILDED_BLACKSTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "sandstone_gravestone"), SANDSTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "red_sandstone_gravestone"), RED_SANDSTONE_GRAVESTONE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "quartz_bricks_gravestone"), QUARTZ_BRICKS_GRAVESTONE);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "skull_with_rib_cage"), SKULL_WITH_RIB_CAGE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "leaning_skeleton"), LEANING_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "skull_pile"), SKULL_PILE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "lying_skeleton"), LYING_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wither_skull_with_rib_cage"), WITHER_SKULL_WITH_RIB_CAGE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "leaning_wither_skeleton"), LEANING_WITHER_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wither_skull_pile"), WITHER_SKULL_PILE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "lying_wither_skeleton"), LYING_WITHER_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "creeper_skeleton"), CREEPER_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "skeleton_hand"), SKELETON_HAND);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wither_skeleton_hand"), WITHER_SKELETON_HAND);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "torso_pile"), TORSO_PILE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wither_torso_pile"), WITHER_TORSO_PILE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "skull_on_pike"), SKULL_ON_PIKE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wither_skull_on_pike"), WITHER_SKULL_ON_PIKE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "bone_remains"), BONE_REMAINS);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wither_bone_remains"), WITHER_BONE_REMAINS);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "laterally_lying_skeleton"), LATERALLY_LYING_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "laterally_lying_wither_skeleton"), LATERALLY_LYING_WITHER_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "hanged_skeleton"), HANGED_SKELETON);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "hanged_wither_skeleton"), HANGED_WITHER_SKELETON);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "black_urn"), BLACK_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "gray_urn"), GRAY_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "light_gray_urn"), LIGHT_GRAY_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "white_urn"), WHITE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "light_blue_urn"), LIGHT_BLUE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "blue_urn"), BLUE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cyan_urn"), CYAN_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "green_urn"), GREEN_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "lime_urn"), LIME_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "pink_urn"), PINK_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "magenta_urn"), MAGENTA_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "purple_urn"), PURPLE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "red_urn"), RED_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "orange_urn"), ORANGE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "yellow_urn"), YELLOW_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "brown_urn"), BROWN_URN);


        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_black_urn"), SMALL_BLACK_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_gray_urn"), SMALL_GRAY_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_light_gray_urn"), SMALL_LIGHT_GRAY_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_white_urn"), SMALL_WHITE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_light_blue_urn"), SMALL_LIGHT_BLUE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_blue_urn"), SMALL_BLUE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_cyan_urn"), SMALL_CYAN_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_green_urn"), SMALL_GREEN_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_lime_urn"), SMALL_LIME_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_pink_urn"), SMALL_PINK_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_magenta_urn"), SMALL_MAGENTA_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_purple_urn"), SMALL_PURPLE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_red_urn"), SMALL_RED_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_orange_urn"), SMALL_ORANGE_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_yellow_urn"), SMALL_YELLOW_URN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "small_brown_urn"), SMALL_BROWN_URN);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "vase_block"), VASE_BLOCK);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "sarcophagus"), SARCOPHAGUS);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "oak_coffin"), OAK_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_oak_coffin"), DARK_OAK_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "spruce_coffin"), SPRUCE_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "birch_coffin"), BIRCH_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "jungle_coffin"), JUNGLE_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "acacia_coffin"), ACACIA_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "warped_coffin"), WARPED_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "crimson_coffin"), CRIMSON_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "mangrove_coffin"), MANGROVE_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "bamboo_coffin"), BAMBOO_COFFIN);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cherry_coffin"), CHERRY_COFFIN);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "skeleton_creeper_spawn_egg"), SKELETON_CREEPER_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "acolyte_spawn_egg"), ACOLYTE_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "ghoul_spawn_egg"), GHOUL_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "reaper_spawn_egg"), REAPER_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "revenant_spawn_egg"), REVENANT_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "nightmare_spawn_egg"), NIGHTMARE_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "corrupted_vindicator_spawn_egg"), CORRUPTED_VINDICATOR_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "corrupted_pillager_spawn_egg"), CORRUPTED_PILLAGER_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "wraith_spawn_egg"), WRAITH_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "lich_spawn_egg"), LICH_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "nameless_hanged_spawn_egg"), NAMELESS_HANGED_SPAWN_EGG);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "bone_dagger"), BONE_DAGGER);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "white_bone_staff"), WHITE_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "black_bone_staff"), BLACK_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "red_bone_staff"), RED_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cyan_bone_staff"), CYAN_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "purple_bone_staff"), PURPLE_BONE_STAFF);


        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "sarcophagus_lid"), SARCOPHAGUS_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "sarcophagus_base"), SARCOPHAGUS_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_oak_coffin_lid"), DARK_OAK_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "dark_oak_coffin_base"), DARK_OAK_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "oak_coffin_lid"), OAK_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "oak_coffin_base"), OAK_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "birch_coffin_lid"), BIRCH_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "birch_coffin_base"), BIRCH_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "jungle_coffin_lid"), JUNGLE_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "jungle_coffin_base"), JUNGLE_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "acacia_coffin_lid"), ACACIA_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "acacia_coffin_base"), ACACIA_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "spruce_coffin_lid"), SPRUCE_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "spruce_coffin_base"), SPRUCE_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "warped_coffin_lid"), WARPED_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "warped_coffin_base"), WARPED_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "crimson_coffin_lid"), CRIMSON_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "crimson_coffin_base"), CRIMSON_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "mangrove_coffin_lid"), MANGROVE_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "mangrove_coffin_base"), MANGROVE_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "bamboo_coffin_lid"), BAMBOO_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "bamboo_coffin_base"), BAMBOO_COFFIN_BASE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cherry_coffin_lid"), CHERRY_COFFIN_LID);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "cherry_coffin_base"), CHERRY_COFFIN_BASE);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "altar"), ALTAR);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "altar_side"), ALTAR_SIDE);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "altar_corner"), ALTAR_CORNER);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "altar_center"), ALTAR_CENTER);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "upper_bone_staff"), UPPER_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "middle_bone_staff"), MIDDLE_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "lower_bone_staff"), LOWER_BONE_STAFF);
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "vial_of_blood"), VIAL_OF_BLOOD);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "ossuary"), OSSUARY);

        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "soil"), new BlockItem(TGBlocks.SOIL, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "turf"), new BlockItem(TGBlocks.TURF, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "gloom_stone"), new BlockItem(TGBlocks.GLOOM_STONE, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "scarlet_heart"), new BlockItem(TGBlocks.SCARLET_HEART, new FabricItemSettings()));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "great_scarlet_heart"), new BlockItem(TGBlocks.GREAT_SCARLET_HEART, new FabricItemSettings()));


        /*
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_planks"), new BlockItem(TGBlocks.ROTTEN_SPRUCE_PLANKS, new FabricItemSettings().group(TheGraveyard.GROUP)));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_stairs"), new BlockItem(TGBlocks.ROTTEN_SPRUCE_STAIRS, new FabricItemSettings().group(TheGraveyard.GROUP)));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_slab"), new BlockItem(TGBlocks.ROTTEN_SPRUCE_SLAB, new FabricItemSettings().group(TheGraveyard.GROUP)));
        Registry.register(Registries.ITEM, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_log"), new BlockItem(TGBlocks.ROTTEN_SPRUCE_LOG, new FabricItemSettings().group(TheGraveyard.GROUP)));
         */

    }


}
