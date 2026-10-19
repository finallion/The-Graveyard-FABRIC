package com.lion.graveyard.init;

import com.lion.graveyard.blockentities.*;
import com.lion.graveyard.platform.RegistryHelper;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TGBlockEntities {

    public static final Supplier<BlockEntityType<GravestoneBlockEntity>> GRAVESTONE_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "gravestone_block_entity",
            () -> new BlockEntityType<>(
                    GravestoneBlockEntity::new,
                    Set.of(
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
                    )
            )
    );

    public static Supplier<BlockEntityType<UrnBlockEntity>> URN_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "urn_block_entity",
            () -> new BlockEntityType<>(
                    UrnBlockEntity::new,
                    Set.of(
                            TGBlocks.SMALL_BLACK_URN.get(),
                            TGBlocks.SMALL_BROWN_URN.get(),
                            TGBlocks.SMALL_WHITE_URN.get(),
                            TGBlocks.SMALL_GRAY_URN.get(),
                            TGBlocks.SMALL_LIGHT_GRAY_URN.get(),
                            TGBlocks.SMALL_BLUE_URN.get(),
                            TGBlocks.SMALL_CYAN_URN.get(),
                            TGBlocks.SMALL_LIGHT_BLUE_URN.get(),
                            TGBlocks.SMALL_RED_URN.get(),
                            TGBlocks.SMALL_ORANGE_URN.get(),
                            TGBlocks.SMALL_YELLOW_URN.get(),
                            TGBlocks.SMALL_PINK_URN.get(),
                            TGBlocks.SMALL_MAGENTA_URN.get(),
                            TGBlocks.SMALL_PURPLE_URN.get(),
                            TGBlocks.SMALL_GREEN_URN.get(),
                            TGBlocks.SMALL_LIME_URN.get(),
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
                            TGBlocks.WHITE_URN.get(),
                            TGBlocks.TERRACOTTA_URN.get(),
                            TGBlocks.BLACK_TERRACOTTA_URN.get(),
                            TGBlocks.BLUE_TERRACOTTA_URN.get(),
                            TGBlocks.BROWN_TERRACOTTA_URN.get(),
                            TGBlocks.CYAN_TERRACOTTA_URN.get(),
                            TGBlocks.GRAY_TERRACOTTA_URN.get(),
                            TGBlocks.GREEN_TERRACOTTA_URN.get(),
                            TGBlocks.LIGHT_BLUE_TERRACOTTA_URN.get(),
                            TGBlocks.LIGHT_GRAY_TERRACOTTA_URN.get(),
                            TGBlocks.LIME_TERRACOTTA_URN.get(),
                            TGBlocks.MAGENTA_TERRACOTTA_URN.get(),
                            TGBlocks.ORANGE_TERRACOTTA_URN.get(),
                            TGBlocks.PINK_TERRACOTTA_URN.get(),
                            TGBlocks.PURPLE_TERRACOTTA_URN.get(),
                            TGBlocks.RED_TERRACOTTA_URN.get(),
                            TGBlocks.WHITE_TERRACOTTA_URN.get(),
                            TGBlocks.YELLOW_TERRACOTTA_URN.get()
                    )
            )
    );

    public static final Supplier<BlockEntityType<SarcophagusBlockEntity>> SARCOPHAGUS_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "sarcophagus_block_entity",
            () -> new BlockEntityType<>(
                    SarcophagusBlockEntity::new,
                    Set.of(
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
                    )
            )
    );

    public static final Supplier<BlockEntityType<BrazierBlockEntity>> BRAZIER_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "brazier_block_entity",
            () -> new BlockEntityType<>(
                    BrazierBlockEntity::new,
                    Set.of(TGBlocks.SOUL_FIRE_BRAZIER.get(), TGBlocks.FIRE_BRAZIER.get())
            )
    );

    public static final Supplier<BlockEntityType<LoreBookBlockEntity>> LORE_BOOK_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "lore_book_block_entity",
            () -> new BlockEntityType<>(LoreBookBlockEntity::new, Set.of(TGBlocks.LORE_BOOK.get()))
    );

    public static final Supplier<BlockEntityType<OssuaryBlockEntity>> OSSUARY_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "ossuary_block_entity",
            () -> new BlockEntityType<>(OssuaryBlockEntity::new, Set.of(TGBlocks.OSSUARY.get()))
    );

    public static void init() {

    }

}
