package com.finallion.graveyard.init;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.*;
import com.finallion.graveyard.blocks.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class TGBlocks {
    public static List<Block> coffins = new ArrayList<>();


    // helper blocks that getStructure don't getStructure replaced on world generation, generate ores or generate trees on
    public static final Block TG_DEEPSLATE = new TGDeepslateBlock(() -> Blocks.DEEPSLATE, FabricBlockSettings.copyOf(Blocks.DEEPSLATE).drops(Blocks.DEEPSLATE.getLootTableId()));
    public static final Block TG_GRASS_BLOCK = new TGGrassBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).drops(Blocks.GRASS_BLOCK.getLootTableId()));
    public static final Block TG_MOSS_BLOCK = new TGMossBlock(FabricBlockSettings.copyOf(Blocks.MOSS_BLOCK).drops(Blocks.MOSS_BLOCK.getLootTableId()));
    public static final Block TG_DIRT = new TGStoneBlock(() -> Blocks.DIRT, FabricBlockSettings.copyOf(Blocks.DIRT).drops(Blocks.DIRT.getLootTableId()));
    public static final Block TG_COARSE_DIRT = new TGStoneBlock(() -> Blocks.COARSE_DIRT, FabricBlockSettings.copyOf(Blocks.COARSE_DIRT).drops(Blocks.COARSE_DIRT.getLootTableId()));
    public static final Block TG_TUFF = new TGStoneBlock(() -> Blocks.TUFF, FabricBlockSettings.copyOf(Blocks.TUFF).drops(Blocks.TUFF.getLootTableId()));
    public static final Block TG_ANDESITE = new TGStoneBlock(() -> Blocks.ANDESITE, FabricBlockSettings.copyOf(Blocks.ANDESITE).drops(Blocks.ANDESITE.getLootTableId()));
    public static final Block TG_GRANITE = new TGStoneBlock(() -> Blocks.GRANITE, FabricBlockSettings.copyOf(Blocks.GRANITE).drops(Blocks.GRANITE.getLootTableId()));
    public static final Block TG_DIORITE = new TGStoneBlock(() -> Blocks.DIORITE, FabricBlockSettings.copyOf(Blocks.DIORITE).drops(Blocks.DIORITE.getLootTableId()));
    public static final Block TG_STONE = new TGStoneBlock(() -> Blocks.STONE, FabricBlockSettings.copyOf(Blocks.STONE).drops(Blocks.STONE.getLootTableId()));
    public static final Block TG_ROOTED_DIRT = new TGStoneBlock(() -> Blocks.ROOTED_DIRT, FabricBlockSettings.copyOf(Blocks.ROOTED_DIRT).drops(Blocks.ROOTED_DIRT.getLootTableId()));
    public static final Block TG_PODZOL = new TGStoneBlock(() -> Blocks.PODZOL, FabricBlockSettings.copyOf(Blocks.PODZOL).drops(Blocks.PODZOL.getLootTableId()));

    public static final Identifier POLISHED_BASALT_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/polished_basalt_side");
    public static final Identifier COBBLESTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/cobblestone");
    public static final Identifier MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/mossy_cobblestone");
    public static final Identifier DEEPSLATE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier ANCHOR_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier ANDESITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier BLACKSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier BLACKSTONE_BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CHISELED_BLACKSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CHISELED_QUARTZ_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CHISELED_RED_SANDSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CHISELED_SANDSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CRACKED_STONE_BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier DARK_PRISMARINE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier DIORITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier GILDED_BLACKSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier GRANITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier IRON_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier LODESTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier NETHERBRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier NETHERITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier POLISHED_ANDESITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier POLISHED_DIORITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier POLISHED_BLACKSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier POLISHED_GRANITE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier PRISMARINE_BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier QUARTZ_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier QUARTZ_BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier QUARTZ_PILLAR_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier RED_NETHERBRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier RED_SANDSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier SANDSTONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier STONE_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier STONE_BRICKS_GRAVESTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");

    public static final Block DARK_IRON_BARS = new DarkIronBars(FabricBlockSettings.of(Material.METAL).strength(1.0F).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final Block SOUL_FIRE_BRAZIER = new BrazierBlock(FabricBlockSettings.of(Material.METAL).strength(1.0F).nonOpaque().luminance(BrazierBlock.STATE_TO_LUMINANCE).sounds(BlockSoundGroup.METAL), ParticleTypes.SOUL_FIRE_FLAME);
    public static final Block FIRE_BRAZIER = new BrazierBlock(FabricBlockSettings.of(Material.METAL).strength(1.0F).nonOpaque().luminance(BrazierBlock.STATE_TO_LUMINANCE).sounds(BlockSoundGroup.METAL), ParticleTypes.FLAME);
    public static final Block PEDESTAL = new PedestalBlock(FabricBlockSettings.of(Material.STONE).strength(1.0F).sounds(BlockSoundGroup.DEEPSLATE));
    public static final Block CANDLE_HOLDER = new CandleHolderBlock(FabricBlockSettings.of(Material.METAL).strength(1.0F).nonOpaque().sounds(BlockSoundGroup.METAL));
    public static final Block DARK_IRON_TRAPDOOR = new TGTrapDoorBlock(FabricBlockSettings.of(Material.WOOD).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque());
    public static final Block DARK_IRON_DOOR = new TGDoorBlock(FabricBlockSettings.of(Material.WOOD, MapColor.IRON_GRAY).requiresTool().strength(5.0F).sounds(BlockSoundGroup.METAL).nonOpaque());
    public static final Block DARK_IRON_BLOCK = new Block(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL));
    public static final Block SKULL_WITH_RIB_CAGE = new BoneDisplayBlock();
    public static final Block LEANING_SKELETON = new BoneDisplayBlock();
    public static final Block SKULL_PILE = new BoneDisplayBlock();
    public static final Block LYING_SKELETON = new BoneDisplayBlock();
    public static final Block WITHER_SKULL_WITH_RIB_CAGE = new BoneDisplayBlock();
    public static final Block LEANING_WITHER_SKELETON = new BoneDisplayBlock();
    public static final Block WITHER_SKULL_PILE = new BoneDisplayBlock();
    public static final Block LYING_WITHER_SKELETON = new BoneDisplayBlock();
    public static final Block CREEPER_SKELETON = new BoneDisplayBlock();
    public static final Block SKELETON_HAND = new BoneDisplayBlock();
    public static final Block WITHER_SKELETON_HAND = new BoneDisplayBlock();
    public static final Block BONE_REMAINS = new BoneDisplayBlock();
    public static final Block LATERALLY_LYING_SKELETON = new BoneDisplayBlock();
    public static final Block SKULL_ON_PIKE = new BoneDisplayBlock();
    public static final Block TORSO_PILE = new BoneDisplayBlock();

    public static final Block BLACK_URN = new UrnBlock();
    public static final Block WHITE_URN = new UrnBlock();
    public static final Block LIGHT_GRAY_URN = new UrnBlock();
    public static final Block GRAY_URN = new UrnBlock();
    public static final Block BROWN_URN = new UrnBlock();
    public static final Block BLUE_URN = new UrnBlock();
    public static final Block LIGHT_BLUE_URN = new UrnBlock();
    public static final Block CYAN_URN = new UrnBlock();
    public static final Block PURPLE_URN = new UrnBlock();
    public static final Block MAGENTA_URN = new UrnBlock();
    public static final Block PINK_URN = new UrnBlock();
    public static final Block ORANGE_URN = new UrnBlock();
    public static final Block RED_URN = new UrnBlock();
    public static final Block YELLOW_URN = new UrnBlock();
    public static final Block GREEN_URN = new UrnBlock();
    public static final Block LIME_URN = new UrnBlock();

    public static final Block SARCOPHAGUS = new SarcophagusBlock(FabricBlockSettings.of(Material.STONE).nonOpaque().strength(1.5F), false, TGItems.SARCOPHAGUS_LID, TGItems.SARCOPHAGUS_BASE);
    public static final Block OAK_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.OAK_COFFIN_LID, TGItems.OAK_COFFIN_BASE);
    public static final Block SPRUCE_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.SPRUCE_COFFIN_LID, TGItems.SPRUCE_COFFIN_BASE);
    public static final Block BIRCH_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.BIRCH_COFFIN_LID, TGItems.BIRCH_COFFIN_BASE);
    public static final Block DARK_OAK_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.DARK_OAK_COFFIN_LID, TGItems.DARK_OAK_COFFIN_BASE);
    public static final Block JUNGLE_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.JUNGLE_COFFIN_LID, TGItems.JUNGLE_COFFIN_BASE);
    public static final Block ACACIA_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.ACACIA_COFFIN_LID, TGItems.ACACIA_COFFIN_BASE);
    public static final Block WARPED_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.WARPED_COFFIN_LID, TGItems.WARPED_COFFIN_BASE);
    public static final Block CRIMSON_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.CRIMSON_COFFIN_LID, TGItems.CRIMSON_COFFIN_BASE);
    public static final Block MANGROVE_COFFIN = new SarcophagusBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().strength(1.0F), true, TGItems.MANGROVE_COFFIN_LID, TGItems.MANGROVE_COFFIN_BASE);

    public static final Block SMALL_BLACK_URN = new UrnBlock();
    public static final Block SMALL_WHITE_URN = new UrnBlock();
    public static final Block SMALL_LIGHT_GRAY_URN = new UrnBlock();
    public static final Block SMALL_GRAY_URN = new UrnBlock();
    public static final Block SMALL_BROWN_URN = new UrnBlock();
    public static final Block SMALL_BLUE_URN = new UrnBlock();
    public static final Block SMALL_LIGHT_BLUE_URN = new UrnBlock();
    public static final Block SMALL_CYAN_URN = new UrnBlock();
    public static final Block SMALL_PURPLE_URN = new UrnBlock();
    public static final Block SMALL_MAGENTA_URN = new UrnBlock();
    public static final Block SMALL_PINK_URN = new UrnBlock();
    public static final Block SMALL_ORANGE_URN = new UrnBlock();
    public static final Block SMALL_RED_URN = new UrnBlock();
    public static final Block SMALL_YELLOW_URN = new UrnBlock();
    public static final Block SMALL_GREEN_URN = new UrnBlock();
    public static final Block SMALL_LIME_URN = new UrnBlock();

    public static final Block VASE_BLOCK = new VaseBlock();

    public static final Block GRAVESTONE = new GravestoneBlock(POLISHED_BASALT_GRAVESTONE_TEXTURE);
    public static final Block COBBLESTONE_GRAVESTONE = new GravestoneBlock(COBBLESTONE_GRAVESTONE_TEXTURE);
    public static final Block MOSSY_COBBLESTONE_GRAVESTONE = new GravestoneBlock(MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE);
    public static final Block DEEPSLATE_GRAVESTONE = new GravestoneBlock(DEEPSLATE_GRAVESTONE_TEXTURE);
    public static final Block BLACKSTONE_GRAVESTONE = new GravestoneBlock(BLACKSTONE_GRAVESTONE_TEXTURE);
    public static final Block CRACKED_BLACKSTONE_GRAVESTONE = new GravestoneBlock(CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE);
    public static final Block STONE_BRICKS_GRAVESTONE = new GravestoneBlock(STONE_BRICKS_GRAVESTONE_TEXTURE);
    public static final Block MOSSY_STONE_BRICKS_GRAVESTONE = new GravestoneBlock(MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE);
    public static final Block BRICKS_GRAVESTONE = new GravestoneBlock(BRICKS_GRAVESTONE_TEXTURE);

    public static final Block ALTAR = new AltarBlock(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing());
    public static final Block ALTAR_SIDE = new AltarSideBlock(FabricBlockSettings.of(Material.STONE).strength(-1.0F, 3600000.0F).dropsNothing());
    public static final Block LOWER_BONE_STAFF = new OminousBoneStaffFragment();
    public static final Block MIDDLE_BONE_STAFF = new OminousBoneStaffFragment();
    public static final Block UPPER_BONE_STAFF = new OminousBoneStaffFragment();

    //public static final Block ROTTEN_SPRUCE_PLANKS = new Block(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    //public static final Block ROTTEN_SPRUCE_STAIRS = new TGStairsBlock(TGBlocks.ROTTEN_SPRUCE_PLANKS.getDefaultState() ,FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    //public static final Block ROTTEN_SPRUCE_SLAB = new SlabBlock(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    //public static final Block ROTTEN_SPRUCE_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_deepslate"), TG_DEEPSLATE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_stone"), TG_STONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_diorite"), TG_DIORITE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_granite"), TG_GRANITE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_andesite"), TG_ANDESITE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_dirt"), TG_DIRT);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_coarse_dirt"), TG_COARSE_DIRT);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_tuff"), TG_TUFF);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_grass_block"), TG_GRASS_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_moss_block"), TG_MOSS_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_rooted_dirt"), TG_ROOTED_DIRT);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "tg_podzol"), TG_PODZOL);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "altar_side"), ALTAR_SIDE);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_iron_trapdoor"), DARK_IRON_TRAPDOOR);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_iron_door"), DARK_IRON_DOOR);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_iron_block"), DARK_IRON_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_iron_bars"), DARK_IRON_BARS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "soul_fire_brazier"), SOUL_FIRE_BRAZIER);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "fire_brazier"), FIRE_BRAZIER);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "candle_holder"), CANDLE_HOLDER);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "pedestal"), PEDESTAL);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "skull_with_rib_cage"), SKULL_WITH_RIB_CAGE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "leaning_skeleton"), LEANING_SKELETON);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "skull_pile"), SKULL_PILE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "lying_skeleton"), LYING_SKELETON);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "skeleton_hand"), SKELETON_HAND);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "wither_skull_with_rib_cage"), WITHER_SKULL_WITH_RIB_CAGE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "leaning_wither_skeleton"), LEANING_WITHER_SKELETON);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "wither_skull_pile"), WITHER_SKULL_PILE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "lying_wither_skeleton"), LYING_WITHER_SKELETON);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "wither_skeleton_hand"), WITHER_SKELETON_HAND);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "creeper_skeleton"), CREEPER_SKELETON);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "bone_remains"), BONE_REMAINS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "laterally_lying_skeleton"), LATERALLY_LYING_SKELETON);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "skull_on_pike"), SKULL_ON_PIKE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "torso_pile"), TORSO_PILE);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "black_urn"), BLACK_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "brown_urn"), BROWN_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "blue_urn"), BLUE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "light_blue_urn"), LIGHT_BLUE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "cyan_urn"), CYAN_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "gray_urn"), GRAY_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "light_gray_urn"), LIGHT_GRAY_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "white_urn"), WHITE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "purple_urn"), PURPLE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "magenta_urn"), MAGENTA_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "pink_urn"), PINK_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "lime_urn"), LIME_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "green_urn"), GREEN_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "red_urn"), RED_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "orange_urn"), ORANGE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "yellow_urn"), YELLOW_URN);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_black_urn"), SMALL_BLACK_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_brown_urn"), SMALL_BROWN_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_blue_urn"), SMALL_BLUE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_light_blue_urn"), SMALL_LIGHT_BLUE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_cyan_urn"), SMALL_CYAN_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_gray_urn"), SMALL_GRAY_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_light_gray_urn"), SMALL_LIGHT_GRAY_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_white_urn"), SMALL_WHITE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_purple_urn"), SMALL_PURPLE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_magenta_urn"), SMALL_MAGENTA_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_pink_urn"), SMALL_PINK_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_lime_urn"), SMALL_LIME_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_green_urn"), SMALL_GREEN_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_red_urn"), SMALL_RED_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_orange_urn"), SMALL_ORANGE_URN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_yellow_urn"), SMALL_YELLOW_URN);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "vase_block"), VASE_BLOCK);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "gravestone"), GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "cobblestone_gravestone"), COBBLESTONE_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "mossy_cobblestone_gravestone"), MOSSY_COBBLESTONE_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "deepslate_gravestone"), DEEPSLATE_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "blackstone_gravestone"), BLACKSTONE_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "cracked_blackstone_gravestone"), CRACKED_BLACKSTONE_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "stone_bricks_gravestone"), STONE_BRICKS_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "mossy_stone_bricks_gravestone"), MOSSY_STONE_BRICKS_GRAVESTONE);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "bricks_gravestone"), BRICKS_GRAVESTONE);

        /*
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_planks"), ROTTEN_SPRUCE_PLANKS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_stairs"), ROTTEN_SPRUCE_STAIRS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_slab"), ROTTEN_SPRUCE_SLAB);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_log"), ROTTEN_SPRUCE_LOG);
         */

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "sarcophagus"), SARCOPHAGUS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "oak_coffin"), OAK_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "spruce_coffin"), SPRUCE_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_oak_coffin"), DARK_OAK_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "birch_coffin"), BIRCH_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "jungle_coffin"), JUNGLE_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "acacia_coffin"), ACACIA_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "warped_coffin"), WARPED_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "crimson_coffin"), CRIMSON_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "mangrove_coffin"), MANGROVE_COFFIN);

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "altar"), ALTAR);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "lower_bone_staff"), LOWER_BONE_STAFF);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "middle_bone_staff"), MIDDLE_BONE_STAFF);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "upper_bone_staff"), UPPER_BONE_STAFF);


        coffins.add(OAK_COFFIN);
        coffins.add(SPRUCE_COFFIN);
        coffins.add(DARK_OAK_COFFIN);
        coffins.add(BIRCH_COFFIN);
        coffins.add(JUNGLE_COFFIN);
        coffins.add(ACACIA_COFFIN);
        coffins.add(CRIMSON_COFFIN);
        coffins.add(WARPED_COFFIN);
        coffins.add(MANGROVE_COFFIN);
    }

    public static final BlockEntityType<GravestoneBlockEntity> GRAVESTONE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "gravestone_block_entity"), FabricBlockEntityTypeBuilder.create(GravestoneBlockEntity::new,
            GRAVESTONE,
            COBBLESTONE_GRAVESTONE,
            MOSSY_COBBLESTONE_GRAVESTONE,
            DEEPSLATE_GRAVESTONE,
            BLACKSTONE_GRAVESTONE,
            CRACKED_BLACKSTONE_GRAVESTONE,
            MOSSY_STONE_BRICKS_GRAVESTONE,
            STONE_BRICKS_GRAVESTONE,
            BRICKS_GRAVESTONE).build(null));


    public static BlockEntityType<UrnBlockEntity> URN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "urn_block_entity"), FabricBlockEntityTypeBuilder.create(UrnBlockEntity::new,
            BLACK_URN,
            BLUE_URN,
            LIGHT_BLUE_URN,
            CYAN_URN,
            BROWN_URN,
            GRAY_URN,
            LIGHT_GRAY_URN,
            PURPLE_URN,
            MAGENTA_URN,
            PINK_URN,
            RED_URN,
            YELLOW_URN,
            ORANGE_URN,
            GREEN_URN,
            LIME_URN,
            WHITE_URN).build(null));

    public static final BlockEntityType<SarcophagusBlockEntity> SARCOPHAGUS_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "sarcophagus_block_entity"), FabricBlockEntityTypeBuilder.create(SarcophagusBlockEntity::new,
            SARCOPHAGUS,
            OAK_COFFIN,
            DARK_OAK_COFFIN,
            ACACIA_COFFIN,
            JUNGLE_COFFIN,
            BIRCH_COFFIN,
            CRIMSON_COFFIN,
            WARPED_COFFIN,
            SPRUCE_COFFIN,
            MANGROVE_COFFIN).build(null));

    public static final BlockEntityType<BrazierBlockEntity> BRAZIER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "brazier_block_entity"), FabricBlockEntityTypeBuilder.create(BrazierBlockEntity::new,
            SOUL_FIRE_BRAZIER,
            FIRE_BRAZIER).build(null));

}
