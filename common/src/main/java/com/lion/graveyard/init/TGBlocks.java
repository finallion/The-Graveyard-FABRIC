package com.lion.graveyard.init;

import com.lion.graveyard.blockentities.*;
import com.lion.graveyard.blocks.*;
import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.util.GravestoneIdentifier;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TGBlocks {
    public static List<Block> coffins = new ArrayList<>();

    private static BlockBehaviour.Properties boneDisplayProperties() {
        return BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK);
    }

    private static BlockBehaviour.Properties urnProperties() {
        return BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.DEEPSLATE_BRICKS).strength(0.3F);
    }

    private static BlockBehaviour.Properties vaseProperties() {
        return BlockBehaviour.Properties.of().instabreak().noCollision().noOcclusion().sound(SoundType.GLASS);
    }

    private static BlockBehaviour.Properties boneStaffFragmentProperties() {
        return BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK).lightLevel(light -> 7);
    }

    private static BlockBehaviour.Properties loreBookProperties() {
        return BlockBehaviour.Properties.of().noOcclusion().noCollision().strength(0.5F).sound(SoundType.WOOL);
    }

    private static BlockBehaviour.Properties gravestoneProperties() {
        return BlockBehaviour.Properties.of().noCollision().noOcclusion().sound(SoundType.DEEPSLATE_BRICKS).strength(1.5F);
    }


    public static final Supplier<Block> TG_ROOTED_DIRT = RegistryHelper.registerBlock("tg_rooted_dirt", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.ROOTED_DIRT).overrideLootTable(Blocks.ROOTED_DIRT.getLootTable()));
    public static final Supplier<Block> TG_TUFF = RegistryHelper.registerBlock("tg_tuff", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF).overrideLootTable(Blocks.TUFF.getLootTable()));
    public static final Supplier<Block> TG_MOSS_BLOCK = RegistryHelper.registerBlock("tg_moss_block", TGMossBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).overrideLootTable(Blocks.MOSS_BLOCK.getLootTable()));
    public static final Supplier<Block> TG_DEEPSLATE = RegistryHelper.registerBlock("tg_deepslate", props -> new TGDeepslateBlock(() -> Blocks.DEEPSLATE, props), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE).overrideLootTable(Blocks.DEEPSLATE.getLootTable()));

    public static final Supplier<Block> TG_GRASS_BLOCK = RegistryHelper.registerBlock("tg_grass_block", TGGrassBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).overrideLootTable(Blocks.GRASS_BLOCK.getLootTable()));
    public static final Supplier<Block> TG_DIRT = RegistryHelper.registerBlock("tg_dirt", props -> new TGStoneBlock(() -> Blocks.DIRT, props), BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).overrideLootTable(Blocks.DIRT.getLootTable()));
    public static final Supplier<Block> TG_COARSE_DIRT = RegistryHelper.registerBlock("tg_coarse_dirt", props -> new TGStoneBlock(() -> Blocks.COARSE_DIRT, props), BlockBehaviour.Properties.ofFullCopy(Blocks.COARSE_DIRT).overrideLootTable(Blocks.COARSE_DIRT.getLootTable()));
    public static final Supplier<Block> TG_ANDESITE = RegistryHelper.registerBlock("tg_andesite", props -> new TGStoneBlock(() -> Blocks.ANDESITE, props), BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE).overrideLootTable(Blocks.ANDESITE.getLootTable()));
    public static final Supplier<Block> TG_GRANITE = RegistryHelper.registerBlock("tg_granite", props -> new TGStoneBlock(() -> Blocks.GRANITE, props), BlockBehaviour.Properties.ofFullCopy(Blocks.GRANITE).overrideLootTable(Blocks.GRANITE.getLootTable()));
    public static final Supplier<Block> TG_DIORITE = RegistryHelper.registerBlock("tg_diorite", props -> new TGStoneBlock(() -> Blocks.DIORITE, props), BlockBehaviour.Properties.ofFullCopy(Blocks.DIORITE).overrideLootTable(Blocks.DIORITE.getLootTable()));
    public static final Supplier<Block> TG_STONE = RegistryHelper.registerBlock("tg_stone", props -> new TGStoneBlock(() -> Blocks.STONE, props), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).overrideLootTable(Blocks.STONE.getLootTable()));
    public static final Supplier<Block> TG_PODZOL = RegistryHelper.registerBlock("tg_podzol", props -> new TGStoneBlock(() -> Blocks.PODZOL, props), BlockBehaviour.Properties.ofFullCopy(Blocks.PODZOL).overrideLootTable(Blocks.PODZOL.getLootTable()));

    public static final Supplier<Block> DARK_IRON_BARS = RegistryHelper.registerBlock("dark_iron_bars", DarkIronBars::new, BlockBehaviour.Properties.of().strength(1.0F).noOcclusion());
    public static final Supplier<Block> SOUL_FIRE_BRAZIER = RegistryHelper.registerBlock("soul_fire_brazier", props -> new BrazierBlock(props, ParticleTypes.SOUL_FIRE_FLAME), BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().lightLevel(BrazierBlock.STATE_TO_LUMINANCE).sound(SoundType.METAL));
    public static final Supplier<Block> FIRE_BRAZIER = RegistryHelper.registerBlock("fire_brazier", props -> new BrazierBlock(props, ParticleTypes.FLAME), BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().lightLevel(BrazierBlock.STATE_TO_LUMINANCE).sound(SoundType.METAL));
    public static final Supplier<Block> PEDESTAL = RegistryHelper.registerBlock("pedestal", PillarBlock::new, BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.DEEPSLATE));
    public static final Supplier<Block> CANDLE_HOLDER = RegistryHelper.registerBlock("candle_holder", CandleHolderBlock::new, BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.METAL));
    public static final Supplier<Block> DARK_IRON_TRAPDOOR = RegistryHelper.registerBlock("dark_iron_trapdoor", TGTrapDoorBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion());
    public static final Supplier<Block> DARK_IRON_DOOR = RegistryHelper.registerBlock("dark_iron_door", TGDoorBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion());
    public static final Supplier<Block> DARK_IRON_BLOCK = RegistryHelper.registerBlock("dark_iron_block", Block::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL));
    public static final Supplier<Block> SKULL_WITH_RIB_CAGE = RegistryHelper.registerBlock("skull_with_rib_cage", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> LEANING_SKELETON = RegistryHelper.registerBlock("leaning_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> SKULL_PILE = RegistryHelper.registerBlock("skull_pile", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> LYING_SKELETON = RegistryHelper.registerBlock("lying_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_SKULL_WITH_RIB_CAGE = RegistryHelper.registerBlock("wither_skull_with_rib_cage", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> LEANING_WITHER_SKELETON = RegistryHelper.registerBlock("leaning_wither_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_SKULL_PILE = RegistryHelper.registerBlock("wither_skull_pile", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> LYING_WITHER_SKELETON = RegistryHelper.registerBlock("lying_wither_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> CREEPER_SKELETON = RegistryHelper.registerBlock("creeper_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> SKELETON_HAND = RegistryHelper.registerBlock("skeleton_hand", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_SKELETON_HAND = RegistryHelper.registerBlock("wither_skeleton_hand", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> BONE_REMAINS = RegistryHelper.registerBlock("bone_remains", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_BONE_REMAINS = RegistryHelper.registerBlock("wither_bone_remains", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> LATERALLY_LYING_SKELETON = RegistryHelper.registerBlock("laterally_lying_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> LATERALLY_LYING_WITHER_SKELETON = RegistryHelper.registerBlock("laterally_lying_wither_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> SKULL_ON_PIKE = RegistryHelper.registerBlock("skull_on_pike", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_SKULL_ON_PIKE = RegistryHelper.registerBlock("wither_skull_on_pike", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> TORSO_PILE = RegistryHelper.registerBlock("torso_pile", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_TORSO_PILE = RegistryHelper.registerBlock("wither_torso_pile", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> HANGED_SKELETON = RegistryHelper.registerBlock("hanged_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> HANGED_WITHER_SKELETON = RegistryHelper.registerBlock("hanged_wither_skeleton", BoneDisplayBlock::new, boneDisplayProperties());

    public static final Supplier<Block> SEATED_FULL_SKELETON = RegistryHelper.registerBlock("seated_full_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> SEATED_FULL_WITHER_SKELETON = RegistryHelper.registerBlock("seated_full_wither_skeleton", BoneDisplayBlock::new, boneDisplayProperties());

    public static final Supplier<Block> SKELETON_SKULL_WITH_AXE = RegistryHelper.registerBlock("skeleton_skull_with_axe", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> WITHER_SKELETON_SKULL_WITH_AXE = RegistryHelper.registerBlock("wither_skeleton_skull_with_axe", BoneDisplayBlock::new, boneDisplayProperties());

    public static final Supplier<Block> SWORD_IN_SKELETON = RegistryHelper.registerBlock("sword_in_skeleton", BoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> SWORD_IN_WITHER_SKELETON = RegistryHelper.registerBlock("sword_in_wither_skeleton", BoneDisplayBlock::new, boneDisplayProperties());

    public static final Supplier<Block> SMALL_SKELETON_REMAINS = RegistryHelper.registerBlock("small_skeleton_remains", VariantBoneDisplayBlock::new, boneDisplayProperties());
    public static final Supplier<Block> SMALL_WITHER_SKELETON_REMAINS = RegistryHelper.registerBlock("small_wither_skeleton_remains", VariantBoneDisplayBlock::new, boneDisplayProperties());

    public static final Supplier<Block> HANGED_FULL_SKELETON = RegistryHelper.registerBlock(
            "hanged_full_skeleton",
            props -> new TallDecoBlock(
                    props,
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
                    true
            ),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );
    public static final Supplier<Block> HANGED_FULL_WITHER_SKELETON = RegistryHelper.registerBlock(
            "hanged_full_wither_skeleton",
            props -> new TallDecoBlock(
                    props,
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
                    Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
                    true
            ),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );

    public static final Supplier<Block> LYING_FULL_SKELETON = RegistryHelper.registerBlock(
            "lying_full_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );
    public static final Supplier<Block> LYING_FULL_WITHER_SKELETON = RegistryHelper.registerBlock(
            "lying_full_wither_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );

    public static final Supplier<Block> BACKSTABBED_FULL_SKELETON = RegistryHelper.registerBlock(
            "backstabbed_full_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );
    public static final Supplier<Block> BACKSTABBED_FULL_WITHER_SKELETON = RegistryHelper.registerBlock(
            "backstabbed_full_wither_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );

    public static final Supplier<Block> SHOT_FULL_SKELETON = RegistryHelper.registerBlock(
            "shot_full_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );
    public static final Supplier<Block> SHOT_FULL_WITHER_SKELETON = RegistryHelper.registerBlock(
            "shot_full_wither_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );

    public static final Supplier<Block> SIDEWAYS_FULL_SKELETON = RegistryHelper.registerBlock(
            "sideways_full_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );
    public static final Supplier<Block> SIDEWAYS_FULL_WITHER_SKELETON = RegistryHelper.registerBlock(
            "sideways_full_wither_skeleton",
            props -> new LongDecoBlock(props, Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)),
            BlockBehaviour.Properties.of().noOcclusion().noCollision().sound(SoundType.BONE_BLOCK)
    );

    public static final Supplier<Block> BLACK_URN = RegistryHelper.registerBlock("black_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> BROWN_URN = RegistryHelper.registerBlock("brown_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> WHITE_URN = RegistryHelper.registerBlock("white_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> GRAY_URN = RegistryHelper.registerBlock("gray_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> LIGHT_GRAY_URN = RegistryHelper.registerBlock("light_gray_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> BLUE_URN = RegistryHelper.registerBlock("blue_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> CYAN_URN = RegistryHelper.registerBlock("cyan_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> LIGHT_BLUE_URN = RegistryHelper.registerBlock("light_blue_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> RED_URN = RegistryHelper.registerBlock("red_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> ORANGE_URN = RegistryHelper.registerBlock("orange_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> YELLOW_URN = RegistryHelper.registerBlock("yellow_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> PINK_URN = RegistryHelper.registerBlock("pink_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> MAGENTA_URN = RegistryHelper.registerBlock("magenta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> PURPLE_URN = RegistryHelper.registerBlock("purple_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> GREEN_URN = RegistryHelper.registerBlock("green_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> LIME_URN = RegistryHelper.registerBlock("lime_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> TERRACOTTA_URN = RegistryHelper.registerBlock("terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> BLACK_TERRACOTTA_URN = RegistryHelper.registerBlock("black_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> BLUE_TERRACOTTA_URN = RegistryHelper.registerBlock("blue_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> BROWN_TERRACOTTA_URN = RegistryHelper.registerBlock("brown_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> CYAN_TERRACOTTA_URN = RegistryHelper.registerBlock("cyan_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> GRAY_TERRACOTTA_URN = RegistryHelper.registerBlock("gray_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> GREEN_TERRACOTTA_URN = RegistryHelper.registerBlock("green_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> LIGHT_BLUE_TERRACOTTA_URN = RegistryHelper.registerBlock("light_blue_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> LIGHT_GRAY_TERRACOTTA_URN = RegistryHelper.registerBlock("light_gray_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> LIME_TERRACOTTA_URN = RegistryHelper.registerBlock("lime_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> MAGENTA_TERRACOTTA_URN = RegistryHelper.registerBlock("magenta_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> ORANGE_TERRACOTTA_URN = RegistryHelper.registerBlock("orange_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> PINK_TERRACOTTA_URN = RegistryHelper.registerBlock("pink_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> PURPLE_TERRACOTTA_URN = RegistryHelper.registerBlock("purple_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> RED_TERRACOTTA_URN = RegistryHelper.registerBlock("red_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> WHITE_TERRACOTTA_URN = RegistryHelper.registerBlock("white_terracotta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> YELLOW_TERRACOTTA_URN = RegistryHelper.registerBlock("yellow_terracotta_urn", UrnBlock::new, urnProperties());

    public static final Supplier<Block> SMALL_BLACK_URN = RegistryHelper.registerBlock("small_black_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_BROWN_URN = RegistryHelper.registerBlock("small_brown_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_WHITE_URN = RegistryHelper.registerBlock("small_white_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_GRAY_URN = RegistryHelper.registerBlock("small_gray_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_LIGHT_GRAY_URN = RegistryHelper.registerBlock("small_light_gray_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_BLUE_URN = RegistryHelper.registerBlock("small_blue_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_CYAN_URN = RegistryHelper.registerBlock("small_cyan_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_LIGHT_BLUE_URN = RegistryHelper.registerBlock("small_light_blue_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_RED_URN = RegistryHelper.registerBlock("small_red_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_ORANGE_URN = RegistryHelper.registerBlock("small_orange_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_YELLOW_URN = RegistryHelper.registerBlock("small_yellow_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_PINK_URN = RegistryHelper.registerBlock("small_pink_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_MAGENTA_URN = RegistryHelper.registerBlock("small_magenta_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_PURPLE_URN = RegistryHelper.registerBlock("small_purple_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_GREEN_URN = RegistryHelper.registerBlock("small_green_urn", UrnBlock::new, urnProperties());
    public static final Supplier<Block> SMALL_LIME_URN = RegistryHelper.registerBlock("small_lime_urn", UrnBlock::new, urnProperties());

    public static final Supplier<Block> SARCOPHAGUS = RegistryHelper.registerBlock("sarcophagus", props -> new SarcophagusBlock(props, false, TGItems.SARCOPHAGUS_LID, TGItems.SARCOPHAGUS_BASE), BlockBehaviour.Properties.of().noOcclusion().strength(1.5F));
    public static final Supplier<Block> OAK_COFFIN = RegistryHelper.registerBlock("oak_coffin", props -> new SarcophagusBlock(props, true, TGItems.OAK_COFFIN_LID, TGItems.OAK_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> SPRUCE_COFFIN = RegistryHelper.registerBlock("spruce_coffin", props -> new SarcophagusBlock(props, true, TGItems.SPRUCE_COFFIN_LID, TGItems.SPRUCE_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> BIRCH_COFFIN = RegistryHelper.registerBlock("birch_coffin", props -> new SarcophagusBlock(props, true, TGItems.BIRCH_COFFIN_LID, TGItems.BIRCH_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> DARK_OAK_COFFIN = RegistryHelper.registerBlock("dark_oak_coffin", props -> new SarcophagusBlock(props, true, TGItems.DARK_OAK_COFFIN_LID, TGItems.DARK_OAK_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> JUNGLE_COFFIN = RegistryHelper.registerBlock("jungle_coffin", props -> new SarcophagusBlock(props, true, TGItems.JUNGLE_COFFIN_LID, TGItems.JUNGLE_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> ACACIA_COFFIN = RegistryHelper.registerBlock("acacia_coffin", props -> new SarcophagusBlock(props, true, TGItems.ACACIA_COFFIN_LID, TGItems.ACACIA_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> WARPED_COFFIN = RegistryHelper.registerBlock("warped_coffin", props -> new SarcophagusBlock(props, true, TGItems.WARPED_COFFIN_LID, TGItems.WARPED_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> CRIMSON_COFFIN = RegistryHelper.registerBlock("crimson_coffin", props -> new SarcophagusBlock(props, true, TGItems.CRIMSON_COFFIN_LID, TGItems.CRIMSON_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> MANGROVE_COFFIN = RegistryHelper.registerBlock("mangrove_coffin", props -> new SarcophagusBlock(props, true, TGItems.MANGROVE_COFFIN_LID, TGItems.MANGROVE_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> BAMBOO_COFFIN = RegistryHelper.registerBlock("bamboo_coffin", props -> new SarcophagusBlock(props, true, TGItems.BAMBOO_COFFIN_LID, TGItems.BAMBOO_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));
    public static final Supplier<Block> CHERRY_COFFIN = RegistryHelper.registerBlock("cherry_coffin", props -> new SarcophagusBlock(props, true, TGItems.CHERRY_COFFIN_LID, TGItems.CHERRY_COFFIN_BASE), BlockBehaviour.Properties.of().ignitedByLava().noOcclusion().strength(1.0F));

    public static final Supplier<Block> VASE_BLOCK = RegistryHelper.registerBlock("vase_block", VaseBlock::new, vaseProperties());

    public static final Supplier<Block> GRAVESTONE = RegistryHelper.registerBlock("gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.POLISHED_BASALT_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> COBBLESTONE_GRAVESTONE = RegistryHelper.registerBlock("cobblestone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.COBBLESTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> MOSSY_COBBLESTONE_GRAVESTONE = RegistryHelper.registerBlock("mossy_cobblestone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.MOSSY_COBBLESTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> DEEPSLATE_GRAVESTONE = RegistryHelper.registerBlock("deepslate_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.DEEPSLATE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> BLACKSTONE_GRAVESTONE = RegistryHelper.registerBlock("blackstone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.BLACKSTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> CRACKED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerBlock("cracked_blackstone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.CRACKED_BLACKSTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> STONE_BRICKS_GRAVESTONE = RegistryHelper.registerBlock("stone_bricks_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.STONE_BRICKS_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> MOSSY_STONE_BRICKS_GRAVESTONE = RegistryHelper.registerBlock("mossy_stone_bricks_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.MOSSY_STONE_BRICKS_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> BRICKS_GRAVESTONE = RegistryHelper.registerBlock("bricks_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.BRICKS_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> SANDSTONE_GRAVESTONE = RegistryHelper.registerBlock("sandstone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.SANDSTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> RED_SANDSTONE_GRAVESTONE = RegistryHelper.registerBlock("red_sandstone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.RED_SANDSTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> GILDED_BLACKSTONE_GRAVESTONE = RegistryHelper.registerBlock("gilded_blackstone_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.GILDED_BLACKSTONE_GRAVESTONE_TEXTURE), gravestoneProperties());
    public static final Supplier<Block> QUARTZ_BRICKS_GRAVESTONE = RegistryHelper.registerBlock("quartz_bricks_gravestone", props -> new GravestoneBlock(props, GravestoneIdentifier.QUARTZ_BRICKS_GRAVESTONE_TEXTURE), gravestoneProperties());

    public static final Supplier<Block> ALTAR = RegistryHelper.registerBlock("altar", AltarBlock::new, BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable());
    public static final Supplier<Block> ALTAR_SIDE = RegistryHelper.registerBlock("altar_side", AltarDirectionBlock::new, BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).noLootTable());
    public static final Supplier<Block> ALTAR_CORNER = RegistryHelper.registerBlock("altar_corner", AltarDirectionBlock::new, BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).noLootTable());
    public static final Supplier<Block> ALTAR_CENTER = RegistryHelper.registerBlock("altar_center", AltarParticleBlock::new, BlockBehaviour.Properties.of().strength(50.0F, 1200.0F).noLootTable());

    public static final Supplier<Block> LOWER_BONE_STAFF = RegistryHelper.registerBlock("lower_bone_staff", OminousBoneStaffFragment::new, boneStaffFragmentProperties());
    public static final Supplier<Block> MIDDLE_BONE_STAFF = RegistryHelper.registerBlock("middle_bone_staff", OminousBoneStaffFragment::new, boneStaffFragmentProperties());
    public static final Supplier<Block> UPPER_BONE_STAFF = RegistryHelper.registerBlock("upper_bone_staff", OminousBoneStaffFragment::new, boneStaffFragmentProperties());

    public static final Supplier<Block> OSSUARY = RegistryHelper.registerBlock("ossuary", OssuaryBlock::new, BlockBehaviour.Properties.of().requiresCorrectToolForDrops().noOcclusion().strength(3.5F));


    // One block for every chronicle page: the page id sits in the block entity, the look is a state.
    public static final Supplier<Block> LORE_BOOK = RegistryHelper.registerBlock("lore_book", LoreBookBlock::new, loreBookProperties());

    public static void init() {

    }


    public static void postInit() {
        coffins.add(OAK_COFFIN.get());
        coffins.add(SPRUCE_COFFIN.get());
        coffins.add(DARK_OAK_COFFIN.get());
        coffins.add(BIRCH_COFFIN.get());
        coffins.add(JUNGLE_COFFIN.get());
        coffins.add(ACACIA_COFFIN.get());
        coffins.add(CRIMSON_COFFIN.get());
        coffins.add(WARPED_COFFIN.get());
        coffins.add(MANGROVE_COFFIN.get());
        coffins.add(CHERRY_COFFIN.get());
        coffins.add(BAMBOO_COFFIN.get());
    }

}
