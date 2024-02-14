package com.lion.graveyard.init;

import com.lion.graveyard.blockentities.*;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class TGBlockEntities {

    public static final Supplier<BlockEntityType<GravestoneBlockEntity>> GRAVESTONE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("gravestone_block_entity", () -> BlockEntityType.Builder.create(GravestoneBlockEntity::new,
            TGBlocks.GRAVESTONE.get(),
            TGBlocks.COBBLESTONE_GRAVESTONE.get(),
            TGBlocks.MOSSY_COBBLESTONE_GRAVESTONE.get(),
            TGBlocks.DEEPSLATE_GRAVESTONE.get(),
            TGBlocks.BLACKSTONE_GRAVESTONE.get(),
            TGBlocks.CRACKED_BLACKSTONE_GRAVESTONE.get(),
            TGBlocks.MOSSY_STONE_BRICKS_GRAVESTONE.get(),
            TGBlocks.STONE_BRICKS_GRAVESTONE.get(),
            TGBlocks.BRICKS_GRAVESTONE.get(),
            TGBlocks.QUARTZ_BRICKS_GRAVESTONE.get(),
            TGBlocks.RED_SANDSTONE_GRAVESTONE.get(),
            TGBlocks.SANDSTONE_GRAVESTONE.get(),
            TGBlocks.GILDED_BLACKSTONE_GRAVESTONE.get()
    ).build(null));

    public static Supplier<BlockEntityType<UrnBlockEntity>> URN_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("urn_block_entity", () -> BlockEntityType.Builder.create(UrnBlockEntity::new,
            TGBlocks.BLACK_URN.get(),
            TGBlocks.BLUE_URN.get(),
            TGBlocks.LIGHT_BLUE_URN.get(),
            TGBlocks.CYAN_URN.get(),
            TGBlocks.BROWN_URN.get(),
            TGBlocks.GRAY_URN.get(),
            TGBlocks.LIGHT_GRAY_URN.get(),
            TGBlocks.PURPLE_URN.get(),
            TGBlocks.MAGENTA_URN.get(),
            TGBlocks.PINK_URN.get(),
            TGBlocks.RED_URN.get(),
            TGBlocks.YELLOW_URN.get(),
            TGBlocks.ORANGE_URN.get(),
            TGBlocks.GREEN_URN.get(),
            TGBlocks.LIME_URN.get(),
            TGBlocks.WHITE_URN.get()
    ).build(null));

    public static final Supplier<BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("sarcophagus_block_entity", () -> BlockEntityType.Builder.create(SarcophagusBlockEntity::new,
            TGBlocks.SARCOPHAGUS.get(),
            TGBlocks.OAK_COFFIN.get(),
            TGBlocks.DARK_OAK_COFFIN.get(),
            TGBlocks.ACACIA_COFFIN.get(),
            TGBlocks.JUNGLE_COFFIN.get(),
            TGBlocks.BIRCH_COFFIN.get(),
            TGBlocks.CRIMSON_COFFIN.get(),
            TGBlocks.WARPED_COFFIN.get(),
            TGBlocks.SPRUCE_COFFIN.get(),
            TGBlocks.CHERRY_COFFIN.get(),
            TGBlocks.BAMBOO_COFFIN.get(),
            TGBlocks.MANGROVE_COFFIN.get()
    ).build(null));

    public static final Supplier<BlockEntityType<BrazierBlockEntity>> BRAZIER_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("brazier_block_entity", () -> BlockEntityType.Builder.create(BrazierBlockEntity::new,
            TGBlocks.SOUL_FIRE_BRAZIER.get(),
            TGBlocks.FIRE_BRAZIER.get()
    ).build(null));

    public static final Supplier<BlockEntityType<OssuaryBlockEntity>> OSSUARY_BLOCK_ENTITY = RegistryHelper.registerBlockEntity("ossuary_block_entity", () -> BlockEntityType.Builder.create(OssuaryBlockEntity::new,
            TGBlocks.OSSUARY.get()
    ).build(null));

    public static void init() {

    }

}
