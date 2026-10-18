package com.lion.graveyard.init;

import com.lion.graveyard.blockentities.SmolderingDebrisBlockEntity;
import com.lion.graveyard.blocks.SmolderingDoorBlock;
import com.lion.graveyard.blocks.SmolderingFenceBlock;
import com.lion.graveyard.blocks.SmolderingFenceGateBlock;
import com.lion.graveyard.blocks.SmolderingPillarBlock;
import com.lion.graveyard.blocks.SmolderingSlabBlock;
import com.lion.graveyard.blocks.SmolderingStairsBlock;
import com.lion.graveyard.blocks.SmolderingTrapdoorBlock;
import com.lion.graveyard.blocks.SmolderingWood;
import com.lion.graveyard.blocks.SmolderingWoodBlock;
import com.lion.graveyard.blocks.SmolderingDebrisBlock;
import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.world.processors.PoolAliasProcessor;
import com.lion.graveyard.world.processors.RuinProcessor;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class TGOverrunVillage {

    public static final Supplier<Block> CHARRED_PLANKS = RegistryHelper.registerBlock("charred_planks", () -> new SmolderingWoodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light)));
    public static final Supplier<Block> CHARRED_LOG = RegistryHelper.registerBlock("charred_log", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light), () -> TGOverrunVillage.STRIPPED_CHARRED_LOG.get()));
    public static final Supplier<Block> SCORCHED_PLANKS = RegistryHelper.registerBlock("scorched_planks", () -> new SmolderingWoodBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light)));
    public static final Supplier<Block> SCORCHED_LOG = RegistryHelper.registerBlock("scorched_log", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light), () -> TGOverrunVillage.STRIPPED_SCORCHED_LOG.get()));
    public static final Supplier<Block> SMOLDERING_DEBRIS = RegistryHelper.registerBlock("smoldering_debris", () -> new SmolderingDebrisBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.5F, 3.0F).sound(SoundType.BASALT).lightLevel(state -> 3)));

    public static final Supplier<Block> CHARRED_STAIRS = RegistryHelper.registerBlock("charred_stairs", () -> new SmolderingStairsBlock(Blocks.OAK_PLANKS.defaultBlockState(), wood(MapColor.COLOR_BLACK)));
    public static final Supplier<Block> CHARRED_SLAB = RegistryHelper.registerBlock("charred_slab", () -> new SmolderingSlabBlock(wood(MapColor.COLOR_BLACK)));
    public static final Supplier<Block> CHARRED_FENCE = RegistryHelper.registerBlock("charred_fence", () -> new SmolderingFenceBlock(wood(MapColor.COLOR_BLACK).forceSolidOn()));
    public static final Supplier<Block> CHARRED_FENCE_GATE = RegistryHelper.registerBlock("charred_fence_gate", () -> new SmolderingFenceGateBlock(wood(MapColor.COLOR_BLACK).forceSolidOn(), WoodType.OAK));
    public static final Supplier<Block> CHARRED_DOOR = RegistryHelper.registerBlock("charred_door", () -> new SmolderingDoorBlock(wood(MapColor.COLOR_BLACK).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY), BlockSetType.OAK));
    public static final Supplier<Block> CHARRED_TRAPDOOR = RegistryHelper.registerBlock("charred_trapdoor", () -> new SmolderingTrapdoorBlock(wood(MapColor.COLOR_BLACK).strength(3.0F).noOcclusion().isValidSpawn((state, world, pos, type) -> false), BlockSetType.OAK));
    public static final Supplier<Block> SCORCHED_STAIRS = RegistryHelper.registerBlock("scorched_stairs", () -> new SmolderingStairsBlock(Blocks.OAK_PLANKS.defaultBlockState(), wood(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> SCORCHED_SLAB = RegistryHelper.registerBlock("scorched_slab", () -> new SmolderingSlabBlock(wood(MapColor.COLOR_BROWN)));
    public static final Supplier<Block> SCORCHED_FENCE = RegistryHelper.registerBlock("scorched_fence", () -> new SmolderingFenceBlock(wood(MapColor.COLOR_BROWN).forceSolidOn()));
    public static final Supplier<Block> SCORCHED_FENCE_GATE = RegistryHelper.registerBlock("scorched_fence_gate", () -> new SmolderingFenceGateBlock(wood(MapColor.COLOR_BROWN).forceSolidOn(), WoodType.OAK));
    public static final Supplier<Block> SCORCHED_DOOR = RegistryHelper.registerBlock("scorched_door", () -> new SmolderingDoorBlock(wood(MapColor.COLOR_BROWN).strength(3.0F).noOcclusion().pushReaction(PushReaction.DESTROY), BlockSetType.OAK));
    public static final Supplier<Block> SCORCHED_TRAPDOOR = RegistryHelper.registerBlock("scorched_trapdoor", () -> new SmolderingTrapdoorBlock(wood(MapColor.COLOR_BROWN).strength(3.0F).noOcclusion().isValidSpawn((state, world, pos, type) -> false), BlockSetType.OAK));

    public static final Supplier<Block> CHARRED_WOOD_BLOCK = RegistryHelper.registerBlock("charred_wood", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light), () -> TGOverrunVillage.STRIPPED_CHARRED_WOOD.get()));
    public static final Supplier<Block> STRIPPED_CHARRED_LOG = RegistryHelper.registerBlock("stripped_charred_log", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light)));
    public static final Supplier<Block> STRIPPED_CHARRED_WOOD = RegistryHelper.registerBlock("stripped_charred_wood", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light)));
    public static final Supplier<Block> SCORCHED_WOOD_BLOCK = RegistryHelper.registerBlock("scorched_wood", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light), () -> TGOverrunVillage.STRIPPED_SCORCHED_WOOD.get()));
    public static final Supplier<Block> STRIPPED_SCORCHED_LOG = RegistryHelper.registerBlock("stripped_scorched_log", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light)));
    public static final Supplier<Block> STRIPPED_SCORCHED_WOOD = RegistryHelper.registerBlock("stripped_scorched_wood", () -> new SmolderingPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light)));

    public static final List<Supplier<Block>> CHARRED_WOOD = List.of(CHARRED_PLANKS, CHARRED_LOG, CHARRED_STAIRS, CHARRED_SLAB, CHARRED_FENCE, CHARRED_FENCE_GATE, CHARRED_TRAPDOOR, CHARRED_WOOD_BLOCK, STRIPPED_CHARRED_LOG, STRIPPED_CHARRED_WOOD);
    public static final List<Supplier<Block>> SCORCHED_WOOD = List.of(SCORCHED_PLANKS, SCORCHED_LOG, SCORCHED_STAIRS, SCORCHED_SLAB, SCORCHED_FENCE, SCORCHED_FENCE_GATE, SCORCHED_TRAPDOOR, SCORCHED_WOOD_BLOCK, STRIPPED_SCORCHED_LOG, STRIPPED_SCORCHED_WOOD);


    public static final Supplier<Item> CHARRED_PLANKS_ITEM = RegistryHelper.registerItem("charred_planks", () -> new BlockItem(CHARRED_PLANKS.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_LOG_ITEM = RegistryHelper.registerItem("charred_log", () -> new BlockItem(CHARRED_LOG.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_PLANKS_ITEM = RegistryHelper.registerItem("scorched_planks", () -> new BlockItem(SCORCHED_PLANKS.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_LOG_ITEM = RegistryHelper.registerItem("scorched_log", () -> new BlockItem(SCORCHED_LOG.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_WOOD_BLOCK_ITEM = RegistryHelper.registerItem("charred_wood", () -> new BlockItem(CHARRED_WOOD_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> STRIPPED_CHARRED_LOG_ITEM = RegistryHelper.registerItem("stripped_charred_log", () -> new BlockItem(STRIPPED_CHARRED_LOG.get(), new Item.Properties()));
    public static final Supplier<Item> STRIPPED_CHARRED_WOOD_ITEM = RegistryHelper.registerItem("stripped_charred_wood", () -> new BlockItem(STRIPPED_CHARRED_WOOD.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_WOOD_BLOCK_ITEM = RegistryHelper.registerItem("scorched_wood", () -> new BlockItem(SCORCHED_WOOD_BLOCK.get(), new Item.Properties()));
    public static final Supplier<Item> STRIPPED_SCORCHED_LOG_ITEM = RegistryHelper.registerItem("stripped_scorched_log", () -> new BlockItem(STRIPPED_SCORCHED_LOG.get(), new Item.Properties()));
    public static final Supplier<Item> STRIPPED_SCORCHED_WOOD_ITEM = RegistryHelper.registerItem("stripped_scorched_wood", () -> new BlockItem(STRIPPED_SCORCHED_WOOD.get(), new Item.Properties()));
    public static final Supplier<Item> SMOLDERING_DEBRIS_ITEM = RegistryHelper.registerItem("smoldering_debris", () -> new BlockItem(SMOLDERING_DEBRIS.get(), new Item.Properties()));

    public static final Supplier<Item> CHARRED_STAIRS_ITEM = RegistryHelper.registerItem("charred_stairs", () -> new BlockItem(CHARRED_STAIRS.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_SLAB_ITEM = RegistryHelper.registerItem("charred_slab", () -> new BlockItem(CHARRED_SLAB.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_FENCE_ITEM = RegistryHelper.registerItem("charred_fence", () -> new BlockItem(CHARRED_FENCE.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_FENCE_GATE_ITEM = RegistryHelper.registerItem("charred_fence_gate", () -> new BlockItem(CHARRED_FENCE_GATE.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_DOOR_ITEM = RegistryHelper.registerItem("charred_door", () -> new DoubleHighBlockItem(CHARRED_DOOR.get(), new Item.Properties()));
    public static final Supplier<Item> CHARRED_TRAPDOOR_ITEM = RegistryHelper.registerItem("charred_trapdoor", () -> new BlockItem(CHARRED_TRAPDOOR.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_STAIRS_ITEM = RegistryHelper.registerItem("scorched_stairs", () -> new BlockItem(SCORCHED_STAIRS.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_SLAB_ITEM = RegistryHelper.registerItem("scorched_slab", () -> new BlockItem(SCORCHED_SLAB.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_FENCE_ITEM = RegistryHelper.registerItem("scorched_fence", () -> new BlockItem(SCORCHED_FENCE.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_FENCE_GATE_ITEM = RegistryHelper.registerItem("scorched_fence_gate", () -> new BlockItem(SCORCHED_FENCE_GATE.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_DOOR_ITEM = RegistryHelper.registerItem("scorched_door", () -> new DoubleHighBlockItem(SCORCHED_DOOR.get(), new Item.Properties()));
    public static final Supplier<Item> SCORCHED_TRAPDOOR_ITEM = RegistryHelper.registerItem("scorched_trapdoor", () -> new BlockItem(SCORCHED_TRAPDOOR.get(), new Item.Properties()));

    public static final Supplier<BlockEntityType<SmolderingDebrisBlockEntity>> SMOLDERING_DEBRIS_BLOCK_ENTITY = RegistryHelper.registerBlockEntity(
            "smoldering_debris_block_entity",
            () -> BlockEntityType.Builder.of(SmolderingDebrisBlockEntity::new, SMOLDERING_DEBRIS.get()).build(null)
    );

    public static StructureProcessorType<RuinProcessor> RUIN = () -> RuinProcessor.CODEC;
    public static StructureProcessorType<PoolAliasProcessor> POOL_ALIASES = () -> PoolAliasProcessor.CODEC;

    static {
        RegistryHelper.registerStructureProcessor("ruin", RUIN);
        RegistryHelper.registerStructureProcessor("pool_aliases", POOL_ALIASES);
    }

    private static BlockBehaviour.Properties wood(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).lightLevel(SmolderingWood::light);
    }

    public static void init() {}

    public static void addItemsToGroup() {
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_PLANKS_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_LOG_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_PLANKS_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_LOG_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_WOOD_BLOCK_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, STRIPPED_CHARRED_LOG_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, STRIPPED_CHARRED_WOOD_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_STAIRS_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_SLAB_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_FENCE_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_FENCE_GATE_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_DOOR_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, CHARRED_TRAPDOOR_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_WOOD_BLOCK_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, STRIPPED_SCORCHED_LOG_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, STRIPPED_SCORCHED_WOOD_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_STAIRS_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_SLAB_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_FENCE_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_FENCE_GATE_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_DOOR_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SCORCHED_TRAPDOOR_ITEM.get());
        RegistryHelper.addToItemGroup(TGItemGroups.ITEM_GROUP, SMOLDERING_DEBRIS_ITEM.get());
    }
}
