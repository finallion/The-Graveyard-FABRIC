package com.finallion.graveyard.init;


import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.blockentities.CoffinBlockEntity;
import com.finallion.graveyard.blockentities.GravestoneBlockEntity;
import com.finallion.graveyard.blockentities.UrnBlockEntity;
import com.finallion.graveyard.blocks.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TGBlocks {



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
    public static final Identifier CRACKED_BLACKSTONE_TEXTURE = new Identifier("minecraft", "block/deepslate");
    public static final Identifier CRACKED_STONE_BRICKS_TEXTURE = new Identifier("minecraft", "block/deepslate");
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

    public static final Block DARK_IRON_BARS = new DarkIronBars(FabricBlockSettings.of(Material.METAL).strength(1.0F).nonOpaque());
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

    public static final Block SARCOPHAGUS = new CoffinBlock();
    public static final Block OAK_COFFIN = new CoffinBlock();
    public static final Block SPRUCE_COFFIN = new CoffinBlock();
    public static final Block BIRCH_COFFIN = new CoffinBlock();
    public static final Block DARK_OAK_COFFIN = new CoffinBlock();
    public static final Block JUNGLE_COFFIN = new CoffinBlock();
    public static final Block ACACIA_COFFIN = new CoffinBlock();

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

    public static final Block ROTTEN_SPRUCE_PLANKS = new Block(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block ROTTEN_SPRUCE_STAIRS = new TGStairsBlock(TGBlocks.ROTTEN_SPRUCE_PLANKS.getDefaultState() ,FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block ROTTEN_SPRUCE_SLAB = new SlabBlock(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));
    public static final Block ROTTEN_SPRUCE_LOG = new PillarBlock(FabricBlockSettings.of(Material.WOOD, MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD));

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

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_iron_bars"), DARK_IRON_BARS);
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

        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "sarcophagus"), SARCOPHAGUS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "oak_coffin"), OAK_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "spruce_coffin"), SPRUCE_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "dark_oak_coffin"), DARK_OAK_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "birch_coffin"), BIRCH_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "jungle_coffin"), JUNGLE_COFFIN);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "acacia_coffin"), ACACIA_COFFIN);

        /*
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_planks"), ROTTEN_SPRUCE_PLANKS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_stairs"), ROTTEN_SPRUCE_STAIRS);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_slab"), ROTTEN_SPRUCE_SLAB);
        Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "rotten_spruce_log"), ROTTEN_SPRUCE_LOG);


         */
        //Registry.register(Registry.BLOCK, new Identifier(TheGraveyard.MOD_ID, "small_graveyard_spruce_sapling"), SMALL_GRAVEYARD_SPRUCE_SAPLING);
    }

    public static final BlockEntityType<GravestoneBlockEntity> GRAVESTONE_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "gravestone_block_entity"), FabricBlockEntityTypeBuilder.create(GravestoneBlockEntity::new,
            GRAVESTONE,
            COBBLESTONE_GRAVESTONE,
            MOSSY_COBBLESTONE_GRAVESTONE,
            DEEPSLATE_GRAVESTONE).build(null));

    public static final BlockEntityType<CoffinBlockEntity> COFFIN_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TheGraveyard.MOD_ID, "coffin_block_entity"), FabricBlockEntityTypeBuilder.create(CoffinBlockEntity::new,
            OAK_COFFIN,
            DARK_OAK_COFFIN,
            SPRUCE_COFFIN,
            BIRCH_COFFIN,
            JUNGLE_COFFIN,
            ACACIA_COFFIN,
            SARCOPHAGUS).build(null));

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


}
