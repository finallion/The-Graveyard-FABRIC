package com.lion.graveyard.init;

import com.lion.graveyard.blocks.*;
import com.lion.graveyard.blocks.PillarBlock;
import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.util.GravestoneIdentifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;


public class TGBlocks {
    public static final Supplier<Block> TG_ROOTED_DIRT = RegistryHelper.registerBlock("tg_rooted_dirt", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.ROOTED_DIRT).dropsLike(Blocks.ROOTED_DIRT)));
    public static final Supplier<Block> TG_TUFF = RegistryHelper.registerBlock("tg_tuff", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF).dropsLike(Blocks.TUFF)));
    public static final Supplier<Block> TG_MOSS_BLOCK = RegistryHelper.registerBlock("tg_moss_block", () -> new TGMossBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).dropsLike(Blocks.MOSS_BLOCK)));
    public static final Supplier<Block> TG_DEEPSLATE = RegistryHelper.registerBlock("tg_deepslate", () -> new TGDeepslateBlock(() -> Blocks.DEEPSLATE, BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).dropsLike(Blocks.DEEPSLATE)));

    // helper blocks that get don't get replaced on world generation, generate ores or generate trees on
    public static final Supplier<Block> TG_GRASS_BLOCK = RegistryHelper.registerBlock("tg_grass_block", () -> new TGGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).dropsLike(Blocks.GRASS_BLOCK)));
    public static final Supplier<Block> TG_DIRT = RegistryHelper.registerBlock("tg_dirt", () -> new TGStoneBlock(() -> Blocks.DIRT, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).dropsLike(Blocks.DIRT)));
    public static final Supplier<Block> TG_COARSE_DIRT = RegistryHelper.registerBlock("tg_coarse_dirt", () -> new TGStoneBlock(() -> Blocks.COARSE_DIRT, BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).dropsLike(Blocks.COARSE_DIRT)));
    public static final Supplier<Block> TG_ANDESITE = RegistryHelper.registerBlock("tg_andesite", () -> new TGStoneBlock(() -> Blocks.ANDESITE, BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE).dropsLike(Blocks.ANDESITE)));
    public static final Supplier<Block> TG_GRANITE = RegistryHelper.registerBlock("tg_granite", () -> new TGStoneBlock(() -> Blocks.GRANITE, BlockBehaviour.Properties.ofFullCopy(Blocks.GRANITE).dropsLike(Blocks.GRANITE)));
    public static final Supplier<Block> TG_DIORITE = RegistryHelper.registerBlock("tg_diorite", () -> new TGStoneBlock(() -> Blocks.DIORITE, BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE).dropsLike(Blocks.DIORITE)));
    public static final Supplier<Block> TG_STONE = RegistryHelper.registerBlock("tg_stone", () -> new TGStoneBlock(() -> Blocks.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).dropsLike(Blocks.STONE)));
    public static final Supplier<Block> TG_PODZOL = RegistryHelper.registerBlock("tg_podzol", () -> new TGStoneBlock(() -> Blocks.PODZOL, BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL).dropsLike(Blocks.PODZOL)));

    public static final Supplier<Block> DARK_IRON_BARS = RegistryHelper.registerBlock("dark_iron_bars", () -> new DarkIronBars(BlockBehaviour.Properties.of().strength(1.0F).noOcclusion()));
    public static final Supplier<Block> SOUL_FIRE_BRAZIER = RegistryHelper.registerBlock("soul_fire_brazier", () -> new BrazierBlock(BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().lightLevel(BrazierBlock.STATE_TO_LUMINANCE).sound(SoundType.METAL), ParticleTypes.SOUL_FIRE_FLAME));
    public static final Supplier<Block> FIRE_BRAZIER = RegistryHelper.registerBlock("fire_brazier", () -> new BrazierBlock(BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().lightLevel(BrazierBlock.STATE_TO_LUMINANCE).sound(SoundType.METAL), ParticleTypes.FLAME));
    public static final Supplier<Block> PEDESTAL = RegistryHelper.registerBlock("pedestal", () -> new PillarBlock(BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.DEEPSLATE)));
    public static final Supplier<Block> CANDLE_HOLDER = RegistryHelper.registerBlock("candle_holder", () -> new CandleHolderBlock(BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.METAL)));
    public static final Supplier<Block> DARK_IRON_TRAPDOOR = RegistryHelper.registerBlock("dark_iron_trapdoor", () -> new TGTrapDoorBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final Supplier<Block> DARK_IRON_DOOR = RegistryHelper.registerBlock("dark_iron_door", () -> new TGDoorBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final Supplier<Block> DARK_IRON_BLOCK = RegistryHelper.registerBlock("dark_iron_block", () -> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final Supplier<Block> SKULL_WITH_RIB_CAGE = RegistryHelper.registerBlock("skull_with_rib_cage", BoneDisplayBlock::new);
    public static final Supplier<Block> LEANING_SKELETON = RegistryHelper.registerBlock("leaning_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> SKULL_PILE = RegistryHelper.registerBlock("skull_pile", BoneDisplayBlock::new);
    public static final Supplier<Block> LYING_SKELETON = RegistryHelper.registerBlock("lying_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> WITHER_SKULL_WITH_RIB_CAGE = RegistryHelper.registerBlock("wither_skull_with_rib_cage", BoneDisplayBlock::new);
    public static final Supplier<Block> LEANING_WITHER_SKELETON = RegistryHelper.registerBlock("leaning_wither_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> WITHER_SKULL_PILE = RegistryHelper.registerBlock("wither_skull_pile", BoneDisplayBlock::new);
    public static final Supplier<Block> LYING_WITHER_SKELETON = RegistryHelper.registerBlock("lying_wither_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> CREEPER_SKELETON = RegistryHelper.registerBlock("creeper_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> SKELETON_HAND = RegistryHelper.registerBlock("skeleton_hand", BoneDisplayBlock::new);
    public static final Supplier<Block> WITHER_SKELETON_HAND = RegistryHelper.registerBlock("wither_skeleton_hand", BoneDisplayBlock::new);
    public static final Supplier<Block> BONE_REMAINS = RegistryHelper.registerBlock("bone_remains", BoneDisplayBlock::new);
    public static final Supplier<Block> WITHER_BONE_REMAINS = RegistryHelper.registerBlock("wither_bone_remains", BoneDisplayBlock::new);
    public static final Supplier<Block> LATERALLY_LYING_SKELETON = RegistryHelper.registerBlock("laterally_lying_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> LATERALLY_LYING_WITHER_SKELETON = RegistryHelper.registerBlock("laterally_lying_wither_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> SKULL_ON_PIKE = RegistryHelper.registerBlock("skull_on_pike", BoneDisplayBlock::new);
    public static final Supplier<Block> WITHER_SKULL_ON_PIKE = RegistryHelper.registerBlock("wither_skull_on_pike", BoneDisplayBlock::new);
    public static final Supplier<Block> TORSO_PILE = RegistryHelper.registerBlock("torso_pile", BoneDisplayBlock::new);
    public static final Supplier<Block> WITHER_TORSO_PILE = RegistryHelper.registerBlock("wither_torso_pile", BoneDisplayBlock::new);
    public static final Supplier<Block> HANGED_SKELETON = RegistryHelper.registerBlock("hanged_skeleton", BoneDisplayBlock::new);
    public static final Supplier<Block> HANGED_WITHER_SKELETON = RegistryHelper.registerBlock("hanged_wither_skeleton", BoneDisplayBlock::new);

    public static final Supplier<Block> BLACK_URN = RegistryHelper.registerBlock("black_urn", UrnBlock::new);
    public static final Supplier<Block> BROWN_URN = RegistryHelper.registerBlock("brown_urn", UrnBlock::new);
    public static final Supplier<Block> WHITE_URN = RegistryHelper.registerBlock("white_urn", UrnBlock::new);
    public static final Supplier<Block> GRAY_URN = RegistryHelper.registerBlock("gray_urn", UrnBlock::new);
    public static final Supplier<Block> LIGHT_GRAY_URN = RegistryHelper.registerBlock("light_gray_urn", UrnBlock::new);
    public static final Supplier<Block> BLUE_URN = RegistryHelper.registerBlock("blue_urn", UrnBlock::new);
    public static final Supplier<Block> CYAN_URN = RegistryHelper.registerBlock("cyan_urn", UrnBlock::new);
    public static final Supplier<Block> LIGHT_BLUE_URN = RegistryHelper.registerBlock("light_blue_urn", UrnBlock::new);
    public static final Supplier<Block> RED_URN = RegistryHelper.registerBlock("red_urn", UrnBlock::new);
    public static final Supplier<Block> ORANGE_URN = RegistryHelper.registerBlock("orange_urn", UrnBlock::new);
    public static final Supplier<Block> YELLOW_URN = RegistryHelper.registerBlock("yellow_urn", UrnBlock::new);
    public static final Supplier<Block> PINK_URN = RegistryHelper.registerBlock("pink_urn", UrnBlock::new);
    public static final Supplier<Block> MAGENTA_URN = RegistryHelper.registerBlock("magenta_urn", UrnBlock::new);
    public static final Supplier<Block> PURPLE_URN = RegistryHelper.registerBlock("purple_urn", UrnBlock::new);
    public static final Supplier<Block> GREEN_URN = RegistryHelper.registerBlock("green_urn", UrnBlock::new);
    public static final Supplier<Block> LIME_URN = RegistryHelper.registerBlock("lime_urn", UrnBlock::new);

    public static final Supplier<Block> SMALL_BLACK_URN = RegistryHelper.registerBlock("small_black_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_BROWN_URN = RegistryHelper.registerBlock("small_brown_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_WHITE_URN = RegistryHelper.registerBlock("small_white_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_GRAY_URN = RegistryHelper.registerBlock("small_gray_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_LIGHT_GRAY_URN = RegistryHelper.registerBlock("small_light_gray_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_BLUE_URN = RegistryHelper.registerBlock("small_blue_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_CYAN_URN = RegistryHelper.registerBlock("small_cyan_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_LIGHT_BLUE_URN = RegistryHelper.registerBlock("small_light_blue_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_RED_URN = RegistryHelper.registerBlock("small_red_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_ORANGE_URN = RegistryHelper.registerBlock("small_orange_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_YELLOW_URN = RegistryHelper.registerBlock("small_yellow_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_PINK_URN = RegistryHelper.registerBlock("small_pink_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_MAGENTA_URN = RegistryHelper.registerBlock("small_magenta_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_PURPLE_URN = RegistryHelper.registerBlock("small_purple_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_GREEN_URN = RegistryHelper.registerBlock("small_green_urn", UrnBlock::new);
    public static final Supplier<Block> SMALL_LIME_URN = RegistryHelper.registerBlock("small_lime_urn", UrnBlock::new);

    public static final Supplier<Block> SARCOPHAGUS = RegistryHelper.registerBlock("sarcophagus", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().noOcclusion().strength(1.5F), false, TGItems.SARCOPHAGUS_LID, TGItems.SARCOPHAGUS_BASE));
    public static final Supplier<Block> OAK_COFFIN = RegistryHelper.registerBlock("oak_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.OAK_COFFIN_LID, TGItems.OAK_COFFIN_BASE));
    public static final Supplier<Block> SPRUCE_COFFIN = RegistryHelper.registerBlock("spruce_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.SPRUCE_COFFIN_LID, TGItems.SPRUCE_COFFIN_BASE));
    public static final Supplier<Block> BIRCH_COFFIN = RegistryHelper.registerBlock("birch_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.BIRCH_COFFIN_LID, TGItems.BIRCH_COFFIN_BASE));
    public static final Supplier<Block> DARK_OAK_COFFIN = RegistryHelper.registerBlock("dark_oak_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.DARK_OAK_COFFIN_LID, TGItems.DARK_OAK_COFFIN_BASE));
    public static final Supplier<Block> JUNGLE_COFFIN = RegistryHelper.registerBlock("jungle_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.JUNGLE_COFFIN_LID, TGItems.JUNGLE_COFFIN_BASE));
    public static final Supplier<Block> ACACIA_COFFIN = RegistryHelper.registerBlock("acacia_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.ACACIA_COFFIN_LID, TGItems.ACACIA_COFFIN_BASE));
    public static final Supplier<Block> WARPED_COFFIN = RegistryHelper.registerBlock("warped_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.WARPED_COFFIN_LID, TGItems.WARPED_COFFIN_BASE));
    public static final Supplier<Block> CRIMSON_COFFIN = RegistryHelper.registerBlock("crimson_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.CRIMSON_COFFIN_LID, TGItems.CRIMSON_COFFIN_BASE));
    public static final Supplier<Block> MANGROVE_COFFIN = RegistryHelper.registerBlock("mangrove_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.MANGROVE_COFFIN_LID, TGItems.MANGROVE_COFFIN_BASE));
    public static final Supplier<Block> BAMBOO_COFFIN = RegistryHelper.registerBlock("bamboo_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.BAMBOO_COFFIN_LID, TGItems.BAMBOO_COFFIN_BASE));
    public static final Supplier<Block> CHERRY_COFFIN = RegistryHelper.registerBlock("cherry_coffin", () -> new SarcophagusBlock(BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F), true, TGItems.CHERRY_COFFIN_LID, TGItems.CHERRY_COFFIN_BASE));

    public static final Supplier<Block> VASE_BLOCK = RegistryHelper.registerBlock("vase_block", VaseBlock::new);

    public static final Supplier<Block> GRAVESTONE = RegistryHelper.registerBlock("gravestone", () -> new GravestoneBlock(GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> COBBLESTONE_GRAVESTONE = RegistryHelper.registerBlock("cobblestone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> MOSSY_COBBLESTONE_GRAVESTONE = RegistryHelper.registerBlock("mossy_cobblestone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> DEEPSLATE_GRAVESTONE = RegistryHelper.registerBlock("deepslate_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> BLACKSTONE_GRAVESTONE = RegistryHelper.registerBlock("blackstone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> CRACKED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerBlock("cracked_blackstone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> STONE_BRICKS_GRAVESTONE = RegistryHelper.registerBlock("stone_bricks_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> MOSSY_STONE_BRICKS_GRAVESTONE = RegistryHelper.registerBlock("mossy_stone_bricks_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> BRICKS_GRAVESTONE = RegistryHelper.registerBlock("bricks_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> SANDSTONE_GRAVESTONE = RegistryHelper.registerBlock("sandstone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> RED_SANDSTONE_GRAVESTONE = RegistryHelper.registerBlock("red_sandstone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> GILDED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerBlock("gilded_blackstone_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE));
    public static final Supplier<Block> QUARTZ_BRICKS_GRAVESTONE = RegistryHelper.registerBlock("quartz_bricks_gravestone", () -> new GravestoneBlock(GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE));

    public static final Supplier<Block> ALTAR = RegistryHelper.registerBlock("altar", () -> new AltarBlock(BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable()));
    public static final Supplier<Block> ALTAR_SIDE = RegistryHelper.registerBlock("altar_side", () -> new AltarDirectionBlock(BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).noLootTable()));
    public static final Supplier<Block> ALTAR_CORNER = RegistryHelper.registerBlock("altar_corner", () -> new AltarDirectionBlock(BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).noLootTable()));
    public static final Supplier<Block> ALTAR_CENTER = RegistryHelper.registerBlock("altar_center", () -> new AltarParticleBlock(BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).noLootTable()));

    public static final Supplier<Block> LOWER_BONE_STAFF = RegistryHelper.registerBlock("lower_bone_staff", OminousBoneStaffFragment::new);
    public static final Supplier<Block> MIDDLE_BONE_STAFF = RegistryHelper.registerBlock("middle_bone_staff", OminousBoneStaffFragment::new);
    public static final Supplier<Block> UPPER_BONE_STAFF = RegistryHelper.registerBlock("upper_bone_staff", OminousBoneStaffFragment::new);

    public static final Supplier<Block> OSSUARY = RegistryHelper.registerBlock("ossuary", () -> new OssuaryBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(3.5F)));


    public static final Supplier<Block> SOIL = RegistryHelper.registerBlock("soil", () -> new Block(BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.GRAVEL)));
    public static final Supplier<Block> TURF = RegistryHelper.registerBlock("turf", () -> new TGTurfBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).randomTicks().strength(0.6F).sound(SoundType.GRASS)));
    public static final Supplier<Block> GLOOM_STONE = RegistryHelper.registerBlock("gloom_stone", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Supplier<Block> SCARLET_HEART = RegistryHelper.registerBlock("scarlet_heart", () -> new PinkPetalsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY).lightLevel((state) -> {
        if (state.getValue(BlockStateProperties.FLOWER_AMOUNT) <= 2) {
            return 5;
        } else if (state.getValue(BlockStateProperties.FLOWER_AMOUNT) == 3) {
            return 7;
        } else {
            return 10;
        }
    })));
    public static final Supplier<Block> GREAT_SCARLET_HEART = RegistryHelper.registerBlock("great_scarlet_heart", () -> new TallFlowerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).noCollission().instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava().pushReaction(PushReaction.DESTROY).lightLevel((state) -> 10)));

    public static void init() {}

    public static void postInit() {}

}
