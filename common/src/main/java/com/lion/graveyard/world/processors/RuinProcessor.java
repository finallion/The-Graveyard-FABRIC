package com.lion.graveyard.world.processors;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.blocks.LongDecoBlock;
import com.lion.graveyard.blocks.SmolderingWood;
import com.lion.graveyard.blocks.VariantBoneDisplayBlock;
import com.lion.graveyard.init.TGOverrunVillage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

/// The game places a piece once for each chunk that it touches. Each of these runs must give the
/// same ruin. Thus all randomness comes from the world seed and the piece origin. A step that reads
/// the world decides for one column only, and only for a column in the chunk of the current run.
/// A piece that follows the terrain gets different input in each run. There, each cell uses a hash
/// of its own position.
public class RuinProcessor extends StructureProcessor {

    private static final TagKey<Block> DEFAULT_FLOOR_REMAINS = villageTag("floor_remains");
    private static final TagKey<Block> DEFAULT_WALL_REMAINS = villageTag("wall_remains");
    private static final TagKey<Block> DEFAULT_LONG_REMAINS = villageTag("long_remains");
    private static final TagKey<Block> DEFAULT_LITTER = villageTag("litter");
    private static final TagKey<Block> RUBBLE_STONE = villageTag("rubble_stone");
    private static final TagKey<Block> SCORCHABLE_STONE = villageTag("scorchable_stone");
    private static final TagKey<Block> SCORCHABLE_TERRACOTTA = villageTag("scorchable_terracotta");
    private static final int LOG_PART = 1;
    private static final int WOOD_PART = 7;
    private static final int STRIPPED_LOG_PART = 8;
    private static final int STRIPPED_WOOD_PART = 9;
    private static final List<TagKey<Block>> WOOD_PARTS = List.of(BlockTags.PLANKS, BlockTags.LOGS_THAT_BURN, BlockTags.WOODEN_STAIRS, BlockTags.WOODEN_SLABS, BlockTags.WOODEN_FENCES, BlockTags.FENCE_GATES, BlockTags.WOODEN_TRAPDOORS);

    public static final MapCodec<RuinProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("collapse", true).forGetter(processor -> processor.collapse),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_severity", 0.15F).forGetter(processor -> processor.minSeverity),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("max_severity", 0.75F).forGetter(processor -> processor.maxSeverity),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("burn_chance", 0.5F).forGetter(processor -> processor.burnChance),
            Codec.intRange(0, 16).optionalFieldOf("max_floor_remains", 3).forGetter(processor -> processor.maxFloorRemains),
            Codec.intRange(0, 16).optionalFieldOf("max_long_remains", 2).forGetter(processor -> processor.maxLongRemains),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("floor_remains", DEFAULT_FLOOR_REMAINS).forGetter(processor -> processor.floorRemains),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("wall_remains", DEFAULT_WALL_REMAINS).forGetter(processor -> processor.wallRemains),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("long_remains", DEFAULT_LONG_REMAINS).forGetter(processor -> processor.longRemains),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("litter", DEFAULT_LITTER).forGetter(processor -> processor.litter),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("litter_chance", 0.0F).forGetter(processor -> processor.litterChance),
            Codec.BOOL.optionalFieldOf("follows_terrain", false).forGetter(processor -> processor.followsTerrain),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("remains_chance", 0.0F).forGetter(processor -> processor.remainsChance),
            Codec.BOOL.optionalFieldOf("on_ground", false).forGetter(processor -> processor.onGround),
            Codec.BOOL.optionalFieldOf("as_unit", false).forGetter(processor -> processor.asUnit),
            Extras.CODEC.forGetter(processor -> new Extras(processor.dry, processor.variant, processor.smolderChance))
    ).apply(instance, RuinProcessor::new));

    private static final float LOG_RESISTANCE = 1.5F;
    private static final int NOISE_CELL = 4;
    private static final float ROOF_HOLE_CHANCE = 0.25F;
    private static final float MIN_BITE_RADIUS = 2.5F;
    private static final float BITE_STEEPNESS = 1.6F;
    private static final int SMALL_HEAP_BLOCKS = 10;
    private static final int LARGE_HEAP_BLOCKS = 40;
    private static final int MAX_HEAP_LAYERS = 3;
    private static final float HEAP_ROUGHNESS = 0.7F;
    private static final float GRAVEL_CHANCE = 0.2F;
    private static final float STRAY_CHAR_CHANCE = 0.25F;
    private static final float BURNT_EDGE_CHAR_CHANCE = 0.9F;
    private static final float EDGE_CHAR_CHANCE = 0.5F;
    private static final float VEIN_CHANCE = 0.3F;
    private static final float VEIN_STRAIGHT_CHANCE = 0.6F;
    private static final int MIN_VEIN_LENGTH = 3;
    private static final int VEIN_LENGTH_SPREAD = 6;
    private static final int LONE_SCORCH_MARKS = 3;
    private static final float STRAY_EMBER_CHANCE = 0.1F;
    private static final float MAGMA_CHANCE = 0.35F;
    private static final float WALL_REMAINS_CHANCE = 0.5F;
    private static final float SINGE_CHANCE = 0.75F;
    private static final float SINGED_RUBBLE_CHANCE = 0.2F;
    private static final int MIN_EMBER_NEIGHBORS = 2;
    private static final int SMALL_PIECE_COLUMNS = 25;
    private static final int LARGE_PIECE_COLUMNS = 80;
    private static final int FEATHER_REACH = 3;
    private static final float PATH_FRAY_CHANCE = 0.2F;
    private static final int MAX_SINK = 3;
    private static final int MAX_LONE_STACK = 2;
    private static final float FEATHER_PATH_CHANCE = 0.25F;
    private static final float FEATHER_COARSE_DIRT_CHANCE = 0.75F;
    private static final int GROUND_SEARCH_UP = 2;
    private static final int GROUND_SEARCH_DOWN = 4;
    private static final int NO_GROUND = Integer.MIN_VALUE;
    private static final long SMOLDER_SEED = 0x5EED5A0BL;

    private final boolean collapse;
    private final float minSeverity;
    private final float maxSeverity;
    private final float burnChance;
    private final int maxFloorRemains;
    private final int maxLongRemains;
    private final TagKey<Block> floorRemains;
    private final TagKey<Block> wallRemains;
    private final TagKey<Block> longRemains;
    private final TagKey<Block> litter;
    private final float litterChance;
    private final boolean followsTerrain;
    private final float remainsChance;
    private final boolean onGround;
    private final boolean asUnit;
    private final DryMode dry;
    private final Variant variant;
    private final float smolderChance;

    public RuinProcessor(
            boolean collapse,
            float minSeverity,
            float maxSeverity,
            float burnChance,
            int maxFloorRemains,
            int maxLongRemains,
            TagKey<Block> floorRemains,
            TagKey<Block> wallRemains,
            TagKey<Block> longRemains,
            TagKey<Block> litter,
            float litterChance,
            boolean followsTerrain,
            float remainsChance,
            boolean onGround,
            boolean asUnit,
            Extras extras
    ) {
        this.collapse = collapse;
        this.minSeverity = minSeverity;
        this.maxSeverity = maxSeverity;
        this.burnChance = burnChance;
        this.maxFloorRemains = maxFloorRemains;
        this.maxLongRemains = maxLongRemains;
        this.floorRemains = floorRemains;
        this.wallRemains = wallRemains;
        this.longRemains = longRemains;
        this.litter = litter;
        this.litterChance = litterChance;
        this.followsTerrain = followsTerrain;
        this.remainsChance = remainsChance;
        this.onGround = onGround;
        this.asUnit = asUnit;
        this.dry = extras.dry();
        this.variant = extras.variant();
        this.smolderChance = extras.smolderChance();
    }

    private static TagKey<Block> villageTag(String name) {
        return TagKey.create(Registries.BLOCK, Graveyard.id("overrun_village/" + name));
    }

    @Override
    public List<StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor world,
            BlockPos pos,
            BlockPos pivot,
            List<StructureBlockInfo> originalBlockInfos,
            List<StructureBlockInfo> currentBlockInfos,
            StructurePlaceSettings data
    ) {
        if (currentBlockInfos.isEmpty()) {
            return currentBlockInfos;
        }

        if (this.dry != DryMode.OFF) {
            currentBlockInfos = dried(originalBlockInfos, currentBlockInfos, pos, data);

            if (this.dry == DryMode.ONLY) {
                return currentBlockInfos;
            }
        }

        if (this.onGround) {
            return dropToGround(world, currentBlockInfos, data, this.asUnit);
        }

        long worldSeed = world.getLevel().getSeed();
        RandomSource random = RandomSource.create(worldSeed).forkPositional().at(pos);
        Ruin ruin = new Ruin(world, currentBlockInfos, data, random, this.followsTerrain, this.variant, this.smolderChance);

        if (this.followsTerrain) {
            ruin.scatterByCell(members(this.litter), this.litterChance, members(this.floorRemains), this.remainsChance);
            ruin.featherPathEdges(worldSeed);
            return ruin.result();
        }

        boolean burnt = random.nextFloat() < this.burnChance;

        if (this.collapse) {
            ruin.collapse(Mth.lerp(random.nextFloat(), this.minSeverity, this.maxSeverity));
        }

        ruin.scorch(burnt);
        ruin.singe();
        ruin.settle();
        ruin.pileRubble(burnt);
        ruin.placeEmbers(burnt);
        ruin.scatterLitter(members(this.litter), this.litterChance);
        ruin.placeLongRemains(members(this.longRemains), ruin.isSmall() ? 0 : this.maxLongRemains);
        ruin.placeRemains(members(this.floorRemains), members(this.wallRemains), ruin.isSmall() ? Math.min(1, this.maxFloorRemains) : this.maxFloorRemains);
        ruin.disconnect();
        ruin.drain();
        ruin.sinkLoneColumns();
        ruin.dropOrphanSnow();

        return ruin.result();
    }

    /// Only water that the template itself holds dries. Water that an earlier rule put in from the world stays.
    private static List<StructureBlockInfo> dried(
            List<StructureBlockInfo> originals,
            List<StructureBlockInfo> infos,
            BlockPos origin,
            StructurePlaceSettings data
    ) {
        Set<BlockPos> templateWater = new HashSet<>();
        for (StructureBlockInfo info : originals) {
            if (info.state().is(Blocks.WATER) || (info.state().hasProperty(BlockStateProperties.WATERLOGGED) && info.state().getValue(BlockStateProperties.WATERLOGGED))) {
                templateWater.add(StructureTemplate.calculateRelativePosition(data, info.pos()).offset(origin));
            }
        }
        Map<BlockPos, BlockState> states = new HashMap<>();
        for (StructureBlockInfo info : infos) {
            states.put(info.pos(), info.state());
        }

        List<StructureBlockInfo> result = new ArrayList<>(infos.size());
        for (StructureBlockInfo info : infos) {
            BlockState state = info.state();

            if (!templateWater.contains(info.pos())) {
                result.add(info);
            } else if (state.is(Blocks.WATER)) {
                result.add(new StructureBlockInfo(info.pos(), (leavesMud(states, info.pos()) ? Blocks.MUD : Blocks.AIR).defaultBlockState(), info.nbt()));
            } else if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
                result.add(new StructureBlockInfo(info.pos(), state.setValue(BlockStateProperties.WATERLOGGED, false), info.nbt()));
            } else {
                result.add(info);
            }
        }
        return result;
    }

    /// Only the lowest water of a contained pool leaves mud. A shaft that continues below the template stays open.
    private static boolean leavesMud(Map<BlockPos, BlockState> states, BlockPos pos) {
        BlockState below = states.get(pos.below());

        if (below == null) {
            BlockState above = states.get(pos.above());
            return above == null || !above.is(Blocks.WATER);
        }

        if (below.isAir() || below.is(Blocks.WATER) || below.is(Blocks.STRUCTURE_VOID)) {
            return false;
        }

        for (Direction side : Direction.Plane.HORIZONTAL) {
            BlockState beside = states.get(pos.relative(side));

            if (beside != null && !beside.isAir()) {
                return true;
            }
        }
        return false;
    }

    /// A piece with a block of two parts moves as one unit. All other pieces move column by column.
    private static List<StructureBlockInfo> dropToGround(
            ServerLevelAccessor world,
            List<StructureBlockInfo> infos,
            StructurePlaceSettings data,
            boolean asUnit
    ) {
        BoundingBox chunkBox = data.getBoundingBox();

        if (chunkBox == null) {
            return infos;
        }

        int base = Integer.MAX_VALUE;
        boolean oneUnit = false;
        List<StructureBlockInfo> solid = new ArrayList<>(infos);
        solid.removeIf(info -> info.state().isAir());
        for (StructureBlockInfo info : solid) {
            base = Math.min(base, info.pos().getY());
            oneUnit |= info.state().getBlock() instanceof LongDecoBlock;
        }

        if (asUnit) {
            return dropAsUnit(world, solid, base);
        }

        Map<Long, Integer> shifts = new HashMap<>();
        for (StructureBlockInfo info : solid) {
            BlockPos pos = info.pos();
            long column = ChunkPos.pack(pos.getX(), pos.getZ());

            if (isColumnIn(chunkBox, pos.getX(), pos.getZ()) && !shifts.containsKey(column)) {
                shifts.put(column, groundShift(world, pos.getX(), base, pos.getZ()));
            }
        }

        Integer unitShift = null;

        if (oneUnit && !shifts.isEmpty()) {
            Set<Integer> distinct = new HashSet<>(shifts.values());

            if (distinct.size() > 1 || distinct.contains(NO_GROUND)) {
                return new ArrayList<>();
            }

            unitShift = distinct.iterator().next();
        }

        List<StructureBlockInfo> result = new ArrayList<>();
        Map<BlockPos, BlockPos> origins = new HashMap<>();
        for (StructureBlockInfo info : solid) {
            BlockPos pos = info.pos();
            Integer shift = unitShift != null ? unitShift : shifts.get(ChunkPos.pack(pos.getX(), pos.getZ()));

            if (shift == null) {
                result.add(info);
                origins.put(pos, pos);
            } else if (shift != NO_GROUND) {
                result.add(new StructureBlockInfo(pos.above(shift), info.state(), info.nbt()));
                origins.put(pos.above(shift), pos);
            }
        }
        return reconnected(result, origins, solid, data, chunkBox);
    }

    /// Columns shift apart on uneven ground, so a fence or pane follows its neighbors after the shift. Template states are not rotated yet.
    private static List<StructureBlockInfo> reconnected(
            List<StructureBlockInfo> placed,
            Map<BlockPos, BlockPos> origins,
            List<StructureBlockInfo> template,
            StructurePlaceSettings data,
            BoundingBox chunkBox
    ) {
        Map<BlockPos, BlockState> states = new HashMap<>();
        for (StructureBlockInfo info : placed) {
            states.put(info.pos(), info.state());
        }
        Set<BlockPos> templateCells = new HashSet<>();
        for (StructureBlockInfo info : template) {
            templateCells.add(info.pos());
        }
        List<StructureBlockInfo> result = new ArrayList<>();
        for (StructureBlockInfo info : placed) {
            BlockState state = info.state();

            if (state.getBlock() instanceof FenceBlock || state.getBlock() instanceof IronBarsBlock) {
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    Direction toward = data.getRotation().rotate(data.getMirror().mirror(side));
                    BlockPos beside = info.pos().relative(toward);

                    if (!isColumnIn(chunkBox, beside.getX(), beside.getZ())) {
                        continue;
                    }

                    BlockState neighbor = states.get(beside);
                    boolean inTemplate = templateCells.contains(origins.get(info.pos()).relative(toward));

                    if (neighbor != null) {
                        BlockState placedNeighbor = neighbor.mirror(data.getMirror()).rotate(data.getRotation());
                        state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side), connectsTo(state, placedNeighbor, toward));
                    } else if (inTemplate) {
                        state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side), false);
                    }
                }
            }

            result.add(state == info.state() ? info : new StructureBlockInfo(info.pos(), state, info.nbt()));
        }
        return result;
    }

    private static boolean connectsTo(BlockState state, BlockState neighbor, Direction direction) {
        boolean sideSolid = neighbor.isFaceSturdy(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, direction.getOpposite());

        if (state.getBlock() instanceof FenceBlock fence) {
            return fence.connectsTo(neighbor, sideSolid, direction);
        }

        return state.getBlock() instanceof IronBarsBlock pane && pane.attachsTo(neighbor, sideSolid);
    }

    /// The terrain height comes from the heightmap, which no piece changes during placement.
    private static List<StructureBlockInfo> dropAsUnit(
            ServerLevelAccessor world,
            List<StructureBlockInfo> solid,
            int base
    ) {
        int shift = 0;
        for (StructureBlockInfo anchor : solid) {
            if (anchor.pos().getY() == base) {
                int toTerrain = world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, anchor.pos().getX(), anchor.pos().getZ()) - base;
                shift = Math.abs(toTerrain) <= MAX_SINK ? toTerrain : 0;
                break;
            }
        }

        List<StructureBlockInfo> result = new ArrayList<>();
        for (StructureBlockInfo info : solid) {
            result.add(new StructureBlockInfo(info.pos().above(shift), info.state(), info.nbt()));
        }
        return result;
    }

    private static int groundShift(ServerLevelAccessor world, int x, int base, int z) {
        boolean blocked = isGround(world, new BlockPos(x, base + GROUND_SEARCH_UP, z));
        for (int y = base + GROUND_SEARCH_UP; y >= base - GROUND_SEARCH_DOWN; y--) {
            boolean onGround = isGround(world, new BlockPos(x, y - 1, z));

            if (onGround && !blocked) {
                return y - base;
            }

            blocked = onGround;
        }
        return NO_GROUND;
    }

    private static boolean isGround(ServerLevelAccessor world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.is(Blocks.DIRT_PATH) || state.isFaceSturdy(world, pos, Direction.UP);
    }

    private static boolean isColumnIn(BoundingBox box, int x, int z) {
        return x >= box.minX() && x <= box.maxX() && z >= box.minZ() && z <= box.maxZ();
    }

    private static List<Block> members(TagKey<Block> tag) {
        List<Block> blocks = new ArrayList<>();
        for (Holder<Block> entry : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
            blocks.add(entry.value());
        }
        return blocks;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return TGOverrunVillage.RUIN;
    }

    private record Bite(float x, float z, float radiusX, float radiusZ, float depth) {
        float depthAt(int blockX, int blockZ) {
            float offsetX = (blockX - this.x) / this.radiusX;
            float offsetZ = (blockZ - this.z) / this.radiusZ;
            float inside = 1.0F - offsetX * offsetX - offsetZ * offsetZ;
            return inside <= 0.0F ? 0.0F : this.depth * Math.min(1.0F, BITE_STEEPNESS * inside);
        }
    }

    private record Heap(float x, float z, int height, float radius) {}

    private record Ember(BlockPos spot, StructureBlockInfo replaced, StructureBlockInfo cap) {}

    public enum DryMode implements StringRepresentable {
        OFF("off"),
        ON("on"),
        ONLY("only");

        private final String name;

        DryMode(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private record Extras(DryMode dry, Variant variant, float smolderChance) {
        static final MapCodec<Extras> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                StringRepresentable.fromEnum(DryMode::values).optionalFieldOf("dry", DryMode.OFF).forGetter(Extras::dry),
                Variant.CODEC.optionalFieldOf("variant", Variant.DEFAULT).forGetter(Extras::variant),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("smolder_chance", 0.0F).forGetter(Extras::smolderChance)
        ).apply(instance, Extras::new));
    }

    /// Material choices of one village type. The default gives the plains village.
    public record Variant(
            List<Block> charStone,
            Block stoneFallback,
            Block stoneCap,
            Block looseRubble,
            boolean scorchTerracotta,
            boolean woodRubble,
            boolean rubble
    ) {
        public static final Variant DEFAULT = new Variant(List.of(Blocks.BLACKSTONE), Blocks.COBBLESTONE, Blocks.COBBLESTONE_SLAB, Blocks.GRAVEL, false, true, true);

        public static final Codec<Variant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.nonEmptyList(BuiltInRegistries.BLOCK.byNameCodec().listOf()).optionalFieldOf("char_stone", DEFAULT.charStone).forGetter(Variant::charStone),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("stone_fallback", DEFAULT.stoneFallback).forGetter(Variant::stoneFallback),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("stone_cap", DEFAULT.stoneCap).forGetter(Variant::stoneCap),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("loose_rubble", DEFAULT.looseRubble).forGetter(Variant::looseRubble),
                Codec.BOOL.optionalFieldOf("scorch_terracotta", DEFAULT.scorchTerracotta).forGetter(Variant::scorchTerracotta),
                Codec.BOOL.optionalFieldOf("wood_rubble", DEFAULT.woodRubble).forGetter(Variant::woodRubble),
                Codec.BOOL.optionalFieldOf("rubble", DEFAULT.rubble).forGetter(Variant::rubble)
        ).apply(instance, Variant::new));
    }

    private static final class Ruin {
        private final Map<BlockPos, StructureBlockInfo> blocks = new LinkedHashMap<>();
        private final List<StructureBlockInfo> air = new ArrayList<>();
        private final Set<BlockPos> heapBlocks = new HashSet<>();
        private final Map<BlockPos, StructureBlockInfo> outside = new LinkedHashMap<>();
        private final Set<BlockPos> templateBlocks;
        private final Set<BlockPos> templateCells = new HashSet<>();
        private final Set<BlockPos> fallen = new HashSet<>();
        private final Map<Long, BlockState> debris = new LinkedHashMap<>();
        private final List<Heap> heaps = new ArrayList<>();
        private final List<BlockPos> rubble = new ArrayList<>();
        private final List<Ember> embers = new ArrayList<>();
        private final Map<Long, Integer> columnBottoms = new HashMap<>();
        private final BlockState woodRubble;
        private final BlockState woodCap;
        private final BlockState stoneRubble;
        private final BlockState stoneCap;
        private final ServerLevelAccessor world;
        private final StructurePlaceSettings data;
        private final RandomSource random;
        private final boolean followsTerrain;
        private final Variant variant;
        private final float smolderChance;
        private int minX = Integer.MAX_VALUE;
        private int minY = Integer.MAX_VALUE;
        private int minZ = Integer.MAX_VALUE;
        private int maxX = Integer.MIN_VALUE;
        private int maxY = Integer.MIN_VALUE;
        private int maxZ = Integer.MIN_VALUE;

        Ruin(
                ServerLevelAccessor world,
                List<StructureBlockInfo> infos,
                StructurePlaceSettings data,
                RandomSource random,
                boolean followsTerrain,
                Variant variant,
                float smolderChance
        ) {
            this.world = world;
            this.smolderChance = smolderChance;
            this.variant = variant;
            this.data = data;
            this.random = random;
            this.followsTerrain = followsTerrain;
            for (StructureBlockInfo info : infos) {
                this.templateCells.add(info.pos());

                if (info.state().isAir()) {
                    this.air.add(info);
                    continue;
                }

                BlockPos pos = info.pos();
                this.blocks.put(pos, info);
                this.columnBottoms.merge(ChunkPos.pack(pos.getX(), pos.getZ()), pos.getY(), Math::min);
                this.minX = Math.min(this.minX, pos.getX());
                this.minY = Math.min(this.minY, pos.getY());
                this.minZ = Math.min(this.minZ, pos.getZ());
                this.maxX = Math.max(this.maxX, pos.getX());
                this.maxY = Math.max(this.maxY, pos.getY());
                this.maxZ = Math.max(this.maxZ, pos.getZ());
            }
            this.templateBlocks = new HashSet<>(this.blocks.keySet());
            Block planks = dominantBlock(infos, BlockTags.PLANKS, null);

            if (planks == null) {
                planks = planksOf(dominantBlock(infos, BlockTags.LOGS_THAT_BURN, null));
            }

            Block stone = dominantBlock(infos, RUBBLE_STONE, variant.stoneFallback());
            this.woodRubble = planks.defaultBlockState();
            this.woodCap = slabOf(planks, Blocks.OAK_SLAB).defaultBlockState();
            this.stoneRubble = stone.defaultBlockState();
            this.stoneCap = slabOf(stone, variant.stoneCap()).defaultBlockState();
        }

        private static Block dominantBlock(List<StructureBlockInfo> infos, TagKey<Block> tag, Block fallback) {
            Map<Block, Integer> counts = new HashMap<>();
            for (StructureBlockInfo info : infos) {
                if (info.state().is(tag)) {
                    counts.merge(info.state().getBlock(), 1, Integer::sum);
                }
            }
            return counts.entrySet().stream()
                    .max(Map.Entry.<Block, Integer>comparingByValue().thenComparing(entry -> BuiltInRegistries.BLOCK.getKey(entry.getKey()).toString(), Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .orElse(fallback);
        }

        private static Block planksOf(Block log) {
            if (log == null) {
                return Blocks.OAK_PLANKS;
            }

            Identifier id = BuiltInRegistries.BLOCK.getKey(log);
            String stem = id.getPath()
                    .replaceFirst("^stripped_", "")
                    .replaceFirst("_(log|wood)$", "");
            return BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(id.getNamespace(), stem + "_planks")).orElse(Blocks.OAK_PLANKS);
        }

        private static Block slabOf(Block block, Block fallback) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            String path = id.getPath();
            String stem = path.endsWith("_planks") ? path.substring(0, path.length() - 7) : (path.endsWith("_bricks") ? path.substring(0, path.length() - 1) : path);
            return BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(id.getNamespace(), stem + "_slab")).orElse(fallback);
        }

        boolean isSmall() {
            return this.columnBottoms.size() < SMALL_PIECE_COLUMNS;
        }

        boolean isLarge() {
            return this.columnBottoms.size() >= LARGE_PIECE_COLUMNS;
        }

        List<StructureBlockInfo> result() {
            Map<BlockPos, StructureBlockInfo> result = new LinkedHashMap<>();
            for (StructureBlockInfo info : this.air) {
                result.put(info.pos(), info);
            }
            result.putAll(this.outside);
            result.putAll(this.blocks);

            if (!smolderingAllowed()) {
                this.quenchEmbers(result);
            }

            if (this.smolderChance > 0.0F && smolderingAllowed()) {
                result.replaceAll((pos, info) -> this.smoldering(info));
            }

            return new ArrayList<>(result.values());
        }

        private void quenchEmbers(Map<BlockPos, StructureBlockInfo> result) {
            for (Ember ember : this.embers) {
                StructureBlockInfo placed = result.get(ember.spot());

                if (placed != null && placed.state().is(TGOverrunVillage.SMOLDERING_DEBRIS.get())) {
                    result.put(ember.spot(), ember.replaced());
                }

                if (ember.cap() != null && !result.containsKey(ember.cap().pos())) {
                    result.put(ember.cap().pos(), ember.cap());
                }
            }
        }

        private static boolean smolderingAllowed() {
            return Graveyard.getConfig().flag("smolderingBlocksInVillages", true);
        }

        private static boolean isCharredWood(BlockState state) {
            return indexIn(TGOverrunVillage.CHARRED_WOOD, state) >= 0 || state.is(TGOverrunVillage.CHARRED_DOOR.get());
        }

        private StructureBlockInfo smoldering(StructureBlockInfo info) {
            BlockState state = info.state();

            if (!state.hasProperty(SmolderingWood.SMOLDERING) || !isCharredWood(state)) {
                return info;
            }

            BlockPos pos = info.pos();
            BlockPos hashed = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;

            if (hash(SMOLDER_SEED + hashed.getY(), hashed.getX(), hashed.getZ()) < this.smolderChance) {
                return new StructureBlockInfo(pos, state.setValue(SmolderingWood.SMOLDERING, true), info.nbt());
            }

            return info;
        }

        /// Each bite removes one connected part of the building, from the roof down.
        void collapse(float severity) {
            int count = this.isSmall() ? 1 : 1 + this.random.nextInt(this.isLarge() ? 3 : 2);
            List<Bite> bites = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                bites.add(this.randomBite(severity));
            }

            long noiseSeed = this.random.nextLong();
            float[] sumX = new float[count];
            float[] sumZ = new float[count];
            int[] fallenBlocks = new int[count];

            for (StructureBlockInfo info : new ArrayList<>(this.blocks.values())) {
                BlockPos pos = info.pos();

                if (pos.getY() <= this.minY || this.isProtected(info)) {
                    continue;
                }

                int deepest = -1;
                float depth = 0.0F;
                for (int i = 0; i < count; i++) {
                    float candidate = bites.get(i).depthAt(pos.getX(), pos.getZ());

                    if (candidate > depth) {
                        depth = candidate;
                        deepest = i;
                    }
                }

                if (deepest < 0) {
                    continue;
                }

                float surface = this.maxY - depth - valueNoise(noiseSeed, pos.getX(), pos.getZ());

                if (pos.getY() > surface + resistance(info.state())) {
                    this.blocks.remove(pos);

                    if (isRubbleMaterial(info.state())) {
                        this.fallen.add(pos);
                    }

                    if (this.piles(info.state())) {
                        this.debris.putIfAbsent(ChunkPos.pack(pos.getX(), pos.getZ()), info.state());
                        sumX[deepest] += pos.getX();
                        sumZ[deepest] += pos.getZ();
                        fallenBlocks[deepest]++;
                    }
                }
            }

            for (int i = 0; i < count; i++) {
                if (fallenBlocks[i] > 0) {
                    Bite bite = bites.get(i);
                    int height = fallenBlocks[i] < SMALL_HEAP_BLOCKS ? 1 : (fallenBlocks[i] < LARGE_HEAP_BLOCKS ? 2 : MAX_HEAP_LAYERS);
                    float x = (sumX[i] / fallenBlocks[i] + bite.x()) / 2.0F;
                    float z = (sumZ[i] / fallenBlocks[i] + bite.z()) / 2.0F;
                    this.heaps.add(new Heap(x, z, height, height + 1.5F));
                }
            }
        }

        private boolean piles(BlockState state) {
            return this.variant.rubble() && isRubbleMaterial(state) && (this.variant.woodRubble() || !state.ignitedByLava());
        }

        private Bite randomBite(float severity) {
            float x;
            float z;

            if (this.random.nextFloat() < ROOF_HOLE_CHANCE) {
                x = Mth.lerpInt(this.random.nextFloat(), this.minX, this.maxX);
                z = Mth.lerpInt(this.random.nextFloat(), this.minZ, this.maxZ);
            } else {
                boolean alongX = this.random.nextBoolean();
                boolean farSide = this.random.nextBoolean();
                float along = this.random.nextFloat();
                x = alongX ? Mth.lerpInt(along, this.minX, this.maxX) : (farSide ? this.maxX : this.minX);
                z = alongX ? (farSide ? this.maxZ : this.minZ) : Mth.lerpInt(along, this.minZ, this.maxZ);
            }

            float reach = 0.4F + 0.6F * severity;
            float radiusX = Math.max(MIN_BITE_RADIUS, reach * (this.maxX - this.minX + 1) / 2.0F * this.spread());
            float radiusZ = Math.max(MIN_BITE_RADIUS, reach * (this.maxZ - this.minZ + 1) / 2.0F * this.spread());
            float depth = (0.4F + 0.9F * severity) * (this.maxY - this.minY) * this.spread();
            return new Bite(x, z, radiusX, radiusZ, depth);
        }

        private float spread() {
            return 0.7F + 0.6F * this.random.nextFloat();
        }

        /// Blocks adjacent to fallen blocks char first. A burnt building chars further along random paths.
        void scorch(boolean burnt) {
            List<BlockPos> edge = new ArrayList<>();
            for (BlockPos pos : this.blocks.keySet()) {
                if (this.touchesFracture(pos)) {
                    edge.add(pos);
                }
            }

            float edgeChance = burnt ? BURNT_EDGE_CHAR_CHANCE : EDGE_CHAR_CHANCE;
            for (BlockPos pos : edge) {
                if (this.random.nextFloat() < edgeChance) {
                    this.scorchBlock(pos);
                }
            }

            if (!burnt) {
                return;
            }

            if (edge.isEmpty()) {
                List<BlockPos> flammable = new ArrayList<>();
                for (StructureBlockInfo info : this.blocks.values()) {
                    if (this.canChar(info.state())) {
                        flammable.add(info.pos());
                    }
                }
                for (int i = 0; i < LONE_SCORCH_MARKS && !flammable.isEmpty(); i++) {
                    this.growVein(flammable.get(this.random.nextInt(flammable.size())), MIN_VEIN_LENGTH + VEIN_LENGTH_SPREAD);
                }
                return;
            }

            for (BlockPos pos : edge) {
                if (this.random.nextFloat() < VEIN_CHANCE) {
                    this.growVein(pos, MIN_VEIN_LENGTH + this.random.nextInt(VEIN_LENGTH_SPREAD));
                }
            }
        }

        private boolean touchesFracture(BlockPos pos) {
            for (Direction direction : Direction.values()) {
                if (this.fallen.contains(pos.relative(direction))) {
                    return true;
                }
            }
            return false;
        }

        private void growVein(BlockPos start, int length) {
            BlockPos current = start;
            Direction heading = Direction.getRandom(this.random);
            this.scorchBlock(current);

            for (int step = 0; step < length; step++) {
                List<Direction> open = new ArrayList<>();
                for (Direction direction : Direction.values()) {
                    StructureBlockInfo info = this.blocks.get(current.relative(direction));

                    if (info != null && this.canChar(info.state())) {
                        open.add(direction);
                    }
                }

                if (open.isEmpty()) {
                    return;
                }

                boolean straight = open.contains(heading) && this.random.nextFloat() < VEIN_STRAIGHT_CHANCE;
                heading = straight ? heading : open.get(this.random.nextInt(open.size()));
                current = current.relative(heading);
                this.scorchBlock(current);
            }
        }

        void singe() {
            List<StructureBlockInfo> candidates = new ArrayList<>();
            for (StructureBlockInfo info : this.blocks.values()) {
                if (singed(info.state()) != null && (this.touchesFracture(info.pos()) || this.touchesChar(info.pos()))) {
                    candidates.add(info);
                }
            }
            for (StructureBlockInfo info : candidates) {
                if (this.random.nextFloat() < SINGE_CHANCE) {
                    this.put(info.pos(), singed(info.state()));
                }
            }
        }

        private boolean touchesChar(BlockPos pos) {
            for (Direction direction : Direction.values()) {
                StructureBlockInfo info = this.blocks.get(pos.relative(direction));

                if (info != null && isCharred(info.state())) {
                    return true;
                }
            }
            return false;
        }

        private boolean isCharred(BlockState state) {
            return this.variant.charStone().contains(state.getBlock()) || (this.variant.scorchTerracotta() && state.is(Blocks.BLACK_TERRACOTTA)) || indexIn(TGOverrunVillage.CHARRED_WOOD, state) >= 0;
        }

        private BlockState singed(BlockState state) {
            if (this.variant.scorchTerracotta() && state.is(SCORCHABLE_TERRACOTTA)) {
                return Blocks.GRAY_TERRACOTTA.defaultBlockState();
            }

            int part = woodPart(state);
            boolean darker = indexIn(TGOverrunVillage.CHARRED_WOOD, state) >= 0 || indexIn(TGOverrunVillage.SCORCHED_WOOD, state) >= 0;
            return part < 0 || darker ? null : TGOverrunVillage.SCORCHED_WOOD.get(logKind(part, state)).get().withPropertiesOf(state);
        }

        private static int woodPart(BlockState state) {
            for (int part = 0; part < WOOD_PARTS.size(); part++) {
                if (state.is(WOOD_PARTS.get(part))) {
                    return part;
                }
            }
            return Math.max(indexIn(TGOverrunVillage.SCORCHED_WOOD, state), indexIn(TGOverrunVillage.CHARRED_WOOD, state));
        }

        /// Log, wood, stripped log and stripped wood share one tag. Their targets sit behind the base set in the list.
        private static int logKind(int part, BlockState state) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(state.getBlock());

            if (part != LOG_PART || id.getNamespace().equals(Graveyard.MOD_ID)) {
                return part;
            }

            String path = id.getPath();
            boolean stripped = path.startsWith("stripped_");
            boolean wood = path.endsWith("_wood");
            return stripped ? (wood ? STRIPPED_WOOD_PART : STRIPPED_LOG_PART) : (wood ? WOOD_PART : LOG_PART);
        }

        private static int indexIn(List<Supplier<Block>> set, BlockState state) {
            for (int part = 0; part < set.size(); part++) {
                if (state.is(set.get(part).get())) {
                    return part;
                }
            }
            return -1;
        }

        private void scorchBlock(BlockPos pos) {
            StructureBlockInfo info = this.blocks.get(pos);
            BlockState charred = info == null ? null : this.charred(info.state(), pos);

            if (charred != null) {
                this.put(pos, charred);
            }
        }

        private boolean canChar(BlockState state) {
            return this.charred(state, BlockPos.ZERO) != null;
        }

        private BlockState charred(BlockState state, BlockPos pos) {
            if (state.is(SCORCHABLE_STONE)) {
                return this.charStone(hash(pos.getY(), pos.getX(), pos.getZ()));
            }

            if (this.variant.scorchTerracotta() && state.is(SCORCHABLE_TERRACOTTA)) {
                return Blocks.BLACK_TERRACOTTA.defaultBlockState();
            }

            int part = woodPart(state);
            boolean done = indexIn(TGOverrunVillage.CHARRED_WOOD, state) >= 0;
            return part < 0 || done ? null : TGOverrunVillage.CHARRED_WOOD.get(logKind(part, state)).get().withPropertiesOf(state);
        }

        private BlockState charStone(float roll) {
            List<Block> stones = this.variant.charStone();
            return stones.get(Math.min(stones.size() - 1, (int) (roll * stones.size()))).defaultBlockState();
        }

        void settle() {
            List<BlockPos> ascending = new ArrayList<>(this.blocks.keySet());
            ascending.sort(Comparator.comparingInt(BlockPos::getY));

            boolean changed;
            do {
                changed = false;
                for (BlockPos pos : ascending) {
                    StructureBlockInfo info = this.blocks.get(pos);

                    if (info != null && (this.lostSupport(pos, info.state()) || this.lostOtherHalf(pos, info.state()))) {
                        this.blocks.remove(pos);
                        changed = true;
                    }
                }
            } while (changed);
        }

        private boolean lostSupport(BlockPos pos, BlockState state) {
            if (state.getBlock() instanceof LadderBlock) {
                return this.wasRemoved(pos.relative(this.toWorld(state.getValue(LadderBlock.FACING)).getOpposite()));
            }

            if (state.hasProperty(BlockStateProperties.BELL_ATTACHMENT) && state.getValue(BlockStateProperties.BELL_ATTACHMENT) != BellAttachType.FLOOR) {
                boolean atCeiling = state.getValue(BlockStateProperties.BELL_ATTACHMENT) == BellAttachType.CEILING;
                return this.wasRemoved(atCeiling ? pos.above() : pos.relative(this.toWorld(state.getValue(BlockStateProperties.HORIZONTAL_FACING))));
            }

            if (state.getBlock() instanceof WallTorchBlock) {
                return this.wasRemoved(pos.relative(this.toWorld(state.getValue(WallTorchBlock.FACING)).getOpposite()));
            }

            if (state.hasProperty(BlockStateProperties.HANGING) && state.getValue(BlockStateProperties.HANGING)) {
                return this.wasRemoved(pos.above());
            }

            return pos.getY() > this.minY && !isFullCube(state) && this.wasRemoved(pos.below());
        }

        private boolean lostOtherHalf(BlockPos pos, BlockState state) {
            BlockPos other;

            if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                other = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            } else if (state.getBlock() instanceof BedBlock) {
                Direction towardHead = this.toWorld(state.getValue(BedBlock.FACING));
                other = pos.relative(state.getValue(BedBlock.PART) == BedPart.FOOT ? towardHead : towardHead.getOpposite());
            } else {
                return false;
            }

            StructureBlockInfo otherInfo = this.blocks.get(other);
            return otherInfo == null || !otherInfo.state().is(state.getBlock());
        }

        private boolean isProtected(StructureBlockInfo info) {
            return info.nbt() != null && info.pos().getY() <= this.minY + 1;
        }

        /// A snow layer needs a solid block below. The collapse can remove that block.
        void dropOrphanSnow() {
            List<BlockPos> layers = new ArrayList<>();
            for (StructureBlockInfo info : this.blocks.values()) {
                if (info.state().is(Blocks.SNOW)) {
                    layers.add(info.pos());
                }
            }
            for (BlockPos pos : layers) {
                BlockPos below = pos.below();

                if (!this.templateCells.contains(below)) {
                    continue;
                }

                StructureBlockInfo support = this.blocks.get(below);

                if (support == null || !support.state().isFaceSturdy(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP)) {
                    this.blocks.remove(pos);
                }
            }
        }

        private boolean wasRemoved(BlockPos pos) {
            return this.templateBlocks.contains(pos) && !this.blocks.containsKey(pos);
        }

        /// The debris of each bite makes one heap below it. A column that is still full gets no debris.
        void pileRubble(boolean burnt) {
            long seed = this.random.nextLong();
            Map<Long, Float> amounts = new LinkedHashMap<>();
            for (Heap heap : this.heaps) {
                int reach = Mth.ceil(heap.radius());
                int centerX = Mth.floor(heap.x());
                int centerZ = Mth.floor(heap.z());
                for (int x = centerX - reach; x <= centerX + reach; x++) {
                    for (int z = centerZ - reach; z <= centerZ + reach; z++) {
                        float distance = Mth.sqrt((x - heap.x()) * (x - heap.x()) + (z - heap.z()) * (z - heap.z()));
                        float amount = (heap.height() + 0.5F) * (1.0F - distance / heap.radius()) + (hash(seed, x, z) - 0.5F) * HEAP_ROUGHNESS;
                        amounts.merge(ChunkPos.pack(x, z), amount, Math::max);
                    }
                }
            }

            for (Map.Entry<Long, Float> column : amounts.entrySet()) {
                int x = ChunkPos.getX(column.getKey());
                int z = ChunkPos.getZ(column.getKey());
                float amount = column.getValue();

                if (amount < 0.5F) {
                    continue;
                }

                boolean inWorld = !this.standsOnTemplate(x, z);
                BlockPos base = inWorld ? this.worldBase(x, z) : this.templateBase(x, z);

                if (base == null) {
                    continue;
                }

                BlockState source = this.nearestDebris(x, z);
                int layers = Math.min(MAX_HEAP_LAYERS, (int) amount);
                BlockPos free = base;
                for (int layer = 0; layer < layers && this.isFree(free, inWorld); layer++) {
                    this.pile(free, rubbleBlock(source, burnt, hash(seed + 1 + layer, x, z)), inWorld);
                    free = free.above();
                }

                if (!inWorld && !free.equals(base)) {
                    this.rubble.add(free.below());
                }

                if (amount - layers >= 0.5F && this.isFree(free, inWorld)) {
                    this.pile(free, turned(rubbleCap(source, burnt), hash(seed + MAX_HEAP_LAYERS + 1, x, z)), inWorld);
                }
            }
        }

        private boolean standsOnTemplate(int x, int z) {
            Integer bottom = this.columnBottoms.get(ChunkPos.pack(x, z));
            return bottom != null && bottom <= this.minY + 1;
        }

        /// World blocks stay out of the block map. Thus the steps that draw random numbers do not see them.
        private void pile(BlockPos pos, BlockState state, boolean inWorld) {
            if (inWorld) {
                this.outside.put(pos, new StructureBlockInfo(pos, state, null));
            } else {
                this.put(pos, state);
                this.heapBlocks.add(pos);
            }
        }

        private boolean isFree(BlockPos pos, boolean inWorld) {
            return !this.blocks.containsKey(pos) && (!inWorld || this.world.getBlockState(pos).canBeReplaced());
        }

        private BlockPos templateBase(int x, int z) {
            for (int y = this.columnBottoms.get(ChunkPos.pack(x, z)) + 1; y <= this.minY + MAX_HEAP_LAYERS; y++) {
                BlockPos pos = new BlockPos(x, y, z);
                StructureBlockInfo below = this.blocks.get(pos.below());

                if (below == null) {
                    return null;
                }

                if (!this.blocks.containsKey(pos)) {
                    return isFloor(below.state()) ? pos : null;
                }
            }
            return null;
        }

        private BlockPos worldBase(int x, int z) {
            BoundingBox chunkBox = this.data.getBoundingBox();

            if (chunkBox == null || !isColumnIn(chunkBox, x, z)) {
                return null;
            }

            int y = this.world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);

            if (y < this.minY - 2 || y > this.minY + MAX_HEAP_LAYERS) {
                return null;
            }

            BlockPos pos = new BlockPos(x, y, z);
            return isFloor(this.world.getBlockState(pos.below())) ? pos : null;
        }

        private BlockState nearestDebris(int x, int z) {
            for (int reach = 0; reach <= MAX_HEAP_LAYERS; reach++) {
                for (int offsetX = -reach; offsetX <= reach; offsetX++) {
                    for (int offsetZ = -reach; offsetZ <= reach; offsetZ++) {
                        BlockState state = this.debris.get(ChunkPos.pack(x + offsetX, z + offsetZ));

                        if (state != null) {
                            return state;
                        }
                    }
                }
            }
            return this.variant.stoneFallback().defaultBlockState();
        }

        private BlockState rubbleBlock(BlockState fallen, boolean burnt, float roll) {
            if (roll < GRAVEL_CHANCE) {
                return this.variant.looseRubble().defaultBlockState();
            }

            boolean scorched = burnt || roll > 1.0F - STRAY_CHAR_CHANCE;

            if (fallen.ignitedByLava()) {
                Block planks = scorched ? TGOverrunVillage.CHARRED_PLANKS.get() : (roll < GRAVEL_CHANCE + SINGED_RUBBLE_CHANCE ? TGOverrunVillage.SCORCHED_PLANKS.get() : this.woodRubble.getBlock());
                return planks.defaultBlockState();
            }

            if (scorched && roll > 0.5F) {
                return this.charStone((roll - 0.5F) * 2.0F);
            }

            return isFullCube(fallen) ? fallen : this.stoneRubble;
        }

        private static BlockState turned(BlockState state, float roll) {
            if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                return state;
            }

            BlockState turned = state.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.from2DDataValue((int) (roll * 4.0F)));
            return turned.hasProperty(BlockStateProperties.STAIRS_SHAPE) ? turned.setValue(BlockStateProperties.STAIRS_SHAPE, StairsShape.STRAIGHT) : turned;
        }

        private BlockState rubbleCap(BlockState fallen, boolean burnt) {
            if (burnt && fallen.ignitedByLava()) {
                return TGOverrunVillage.CHARRED_SLAB.get().defaultBlockState();
            }

            if (fallen.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                return fallen.setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
            }

            if (fallen.hasProperty(BlockStateProperties.HALF)) {
                return fallen.setValue(BlockStateProperties.HALF, Half.BOTTOM);
            }

            return fallen.ignitedByLava() ? this.woodCap : this.stoneCap;
        }

        void placeEmbers(boolean burnt) {
            if (this.isSmall()) {
                return;
            }

            int fires = this.isLarge() ? 3 : 2;
            int count = burnt ? fires : (this.random.nextFloat() < STRAY_EMBER_CHANCE ? 1 : 0);
            List<BlockPos> spots = new ArrayList<>(this.rubble);
            spots.removeIf(spot -> this.heapNeighbors(spot) < MIN_EMBER_NEIGHBORS);

            for (int i = 0; i < count && !spots.isEmpty(); i++) {
                BlockPos spot = spots.remove(this.random.nextInt(spots.size()));
                StructureBlockInfo cap = this.heapBlocks.contains(spot.above()) ? this.blocks.get(spot.above()) : null;
                this.embers.add(new Ember(spot, this.blocks.get(spot), cap));
                this.put(spot, TGOverrunVillage.SMOLDERING_DEBRIS.get().defaultBlockState());

                if (cap != null) {
                    this.blocks.remove(spot.above());
                }

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BlockPos ground = spot.below().relative(direction);
                    StructureBlockInfo info = this.blocks.get(ground);
                    boolean open = !this.blocks.containsKey(ground.above());

                    if (info != null && open && isFullCube(info.state()) && this.random.nextFloat() < MAGMA_CHANCE) {
                        this.put(ground, Blocks.MAGMA_BLOCK.defaultBlockState());
                    }
                }
            }
        }

        private int heapNeighbors(BlockPos spot) {
            int neighbors = 0;
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos beside = spot.relative(direction);

                if (this.heapBlocks.contains(beside) || this.heapBlocks.contains(beside.below())) {
                    neighbors++;
                }
            }
            return neighbors;
        }

        /// A fence or a pane keeps its template connections. The collapse removes some of the neighbors.
        void disconnect() {
            for (StructureBlockInfo info : new ArrayList<>(this.blocks.values())) {
                BlockState state = info.state();

                if (state.getBlock() instanceof WallBlock) {
                    state = this.disconnectedWall(info);

                    if (state != info.state()) {
                        this.blocks.put(info.pos(), new StructureBlockInfo(info.pos(), state, info.nbt()));
                    }

                    continue;
                }

                if (!(state.getBlock() instanceof CrossCollisionBlock)) {
                    continue;
                }

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    BooleanProperty connection = PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
                    BlockPos neighbor = info.pos().relative(this.toWorld(direction));

                    if (state.getValue(connection) && this.isDisconnected(neighbor)) {
                        state = state.setValue(connection, false);
                    }
                }

                if (state != info.state()) {
                    this.blocks.put(info.pos(), new StructureBlockInfo(info.pos(), state, info.nbt()));
                }
            }
        }

        private BlockState disconnectedWall(StructureBlockInfo info) {
            BlockState state = info.state();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                EnumProperty<WallSide> side = wallSide(direction);

                if (state.getValue(side) != WallSide.NONE && this.isDisconnected(info.pos().relative(this.toWorld(direction)))) {
                    state = state.setValue(side, WallSide.NONE);
                }
            }
            return state == info.state() ? state : state.setValue(WallBlock.UP, state.getValue(WallBlock.UP) || needsPost(state));
        }

        private static boolean needsPost(BlockState wall) {
            boolean north = wall.getValue(BlockStateProperties.NORTH_WALL) == WallSide.NONE;
            boolean east = wall.getValue(BlockStateProperties.EAST_WALL) == WallSide.NONE;
            boolean south = wall.getValue(BlockStateProperties.SOUTH_WALL) == WallSide.NONE;
            boolean west = wall.getValue(BlockStateProperties.WEST_WALL) == WallSide.NONE;
            return north && east && south && west || north != south || east != west;
        }

        private static EnumProperty<WallSide> wallSide(Direction direction) {
            return switch (direction) {
                case NORTH -> BlockStateProperties.NORTH_WALL;
                case EAST -> BlockStateProperties.EAST_WALL;
                case SOUTH -> BlockStateProperties.SOUTH_WALL;
                default -> BlockStateProperties.WEST_WALL;
            };
        }

        private boolean isDisconnected(BlockPos neighbor) {
            StructureBlockInfo info = this.blocks.get(neighbor);

            if (info == null) {
                return this.templateCells.contains(neighbor);
            }

            return this.heapBlocks.contains(neighbor) && !isFullCube(info.state());
        }

        void drain() {
            for (StructureBlockInfo info : new ArrayList<>(this.blocks.values())) {
                if (info.state().hasProperty(BlockStateProperties.WATERLOGGED) && info.state().getValue(BlockStateProperties.WATERLOGGED)) {
                    this.blocks.put(info.pos(), new StructureBlockInfo(info.pos(), info.state().setValue(BlockStateProperties.WATERLOGGED, false), info.nbt()));
                }
            }
        }

        /// A lone block of the base layer stays on a pillar where the terrain is lower.
        void sinkLoneColumns() {
            BoundingBox chunkBox = this.data.getBoundingBox();

            if (chunkBox == null) {
                return;
            }

            for (BlockPos pos : new ArrayList<>(this.blocks.keySet())) {
                if (pos.getY() != this.minY || !isColumnIn(chunkBox, pos.getX(), pos.getZ()) || !this.isLone(pos) || this.columnHeight(pos) > MAX_LONE_STACK) {
                    continue;
                }

                int drop = pos.getY() + 1 - this.world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, pos.getX(), pos.getZ());

                if (drop <= 0 || drop > MAX_SINK) {
                    continue;
                }

                for (BlockPos part = pos; this.blocks.containsKey(part); part = part.above()) {
                    StructureBlockInfo info = this.blocks.remove(part);
                    this.blocks.put(part.below(drop), new StructureBlockInfo(part.below(drop), info.state(), info.nbt()));
                }
            }
        }

        private int columnHeight(BlockPos base) {
            int top = base.getY();
            for (int y = base.getY(); y <= this.maxY; y++) {
                if (this.blocks.containsKey(new BlockPos(base.getX(), y, base.getZ()))) {
                    top = y;
                }
            }
            return top - base.getY() + 1;
        }

        private boolean isLone(BlockPos pos) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (this.templateBlocks.contains(pos.relative(direction))) {
                    return false;
                }
            }
            return true;
        }

        void scatterLitter(List<Block> litter, float chance) {
            if (litter.isEmpty() || chance <= 0.0F) {
                return;
            }

            for (BlockPos cell : this.floorCells()) {
                if (this.random.nextFloat() >= chance) {
                    continue;
                }

                Block block = litter.get(this.random.nextInt(litter.size()));
                this.put(cell, this.oriented(block, Direction.Plane.HORIZONTAL.getRandomDirection(this.random), 0));
            }
        }

        void scatterByCell(List<Block> litter, float litterChance, List<Block> remains, float remainsChance) {
            long seed = this.random.nextLong();
            for (BlockPos cell : this.floorCells()) {
                float roll = hash(seed, cell.getX(), cell.getZ());
                int pick = (int) (hash(seed + 1, cell.getX(), cell.getZ()) * 4096.0F);
                Direction facing = Direction.from2DDataValue(pick >> 6);

                if (roll < remainsChance) {
                    if (!remains.isEmpty()) {
                        this.put(cell, this.oriented(remains.get(pick % remains.size()), facing, (pick >> 8) & 3));
                    }
                } else if (roll < remainsChance + litterChance && !litter.isEmpty()) {
                    this.put(cell, this.oriented(litter.get(pick % litter.size()), facing, 0));
                }
            }
        }

        /// The street template holds only the path. The grass beside it is in the world.
        void featherPathEdges(long worldSeed) {
            BoundingBox chunkBox = this.data.getBoundingBox();

            if (chunkBox == null) {
                return;
            }

            Map<Long, Float> distances = new LinkedHashMap<>();
            for (StructureBlockInfo info : this.blocks.values()) {
                if (!info.state().is(Blocks.DIRT_PATH)) {
                    continue;
                }

                for (int offsetX = -FEATHER_REACH; offsetX <= FEATHER_REACH; offsetX++) {
                    for (int offsetZ = -FEATHER_REACH; offsetZ <= FEATHER_REACH; offsetZ++) {
                        int x = info.pos().getX() + offsetX;
                        int z = info.pos().getZ() + offsetZ;
                        long column = ChunkPos.pack(x, z);

                        if (!this.columnBottoms.containsKey(column) && isColumnIn(chunkBox, x, z)) {
                            distances.merge(column, Mth.sqrt(offsetX * offsetX + offsetZ * offsetZ), Math::min);
                        }
                    }
                }
            }

            for (Map.Entry<Long, Float> column : distances.entrySet()) {
                int x = ChunkPos.getX(column.getKey());
                int z = ChunkPos.getZ(column.getKey());
                float distance = column.getValue();
                float chance = 1.25F - 0.3F * distance + 0.35F * valueNoise(worldSeed, x, z);

                if (hash(worldSeed, x, z) >= chance) {
                    continue;
                }

                BlockPos surface = new BlockPos(x, this.world.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1, z);

                if (this.world.getBlockState(surface).is(Blocks.GRASS_BLOCK)) {
                    this.outside.put(surface, new StructureBlockInfo(surface, featherState(hash(worldSeed + 1, x, z), distance), null));
                }
            }
            this.frayPath(worldSeed, chunkBox);
        }

        private void frayPath(long worldSeed, BoundingBox chunkBox) {
            for (StructureBlockInfo info : new ArrayList<>(this.blocks.values())) {
                BlockPos pos = info.pos();
                boolean frays = info.state().is(Blocks.DIRT_PATH) && isColumnIn(chunkBox, pos.getX(), pos.getZ()) && hash(worldSeed + 2, pos.getX(), pos.getZ()) < PATH_FRAY_CHANCE;

                if (frays && this.isPathEdge(pos)) {
                    this.put(pos, featherState(hash(worldSeed + 3, pos.getX(), pos.getZ()), FEATHER_REACH));
                }
            }
        }

        /// At the bounds of the piece the path continues in the adjacent piece.
        private boolean isPathEdge(BlockPos pos) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos neighbor = pos.relative(direction);
                boolean inBounds = neighbor.getX() >= this.minX && neighbor.getX() <= this.maxX && neighbor.getZ() >= this.minZ && neighbor.getZ() <= this.maxZ;

                if (inBounds && !this.columnBottoms.containsKey(ChunkPos.pack(neighbor.getX(), neighbor.getZ()))) {
                    return true;
                }
            }
            return false;
        }

        private static BlockState featherState(float roll, float distance) {
            if (roll < FEATHER_PATH_CHANCE && distance < 1.5F) {
                return Blocks.DIRT_PATH.defaultBlockState();
            }

            return (roll < FEATHER_COARSE_DIRT_CHANCE ? Blocks.COARSE_DIRT : Blocks.ROOTED_DIRT).defaultBlockState();
        }

        private BlockState oriented(Block block, Direction worldFacing, int variant) {
            BlockState state = block.defaultBlockState();

            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, this.toTemplate(worldFacing));
            }

            if (state.hasProperty(BlockStateProperties.AXIS)) {
                state = state.setValue(BlockStateProperties.AXIS, worldFacing.getAxis());
            }

            if (state.hasProperty(VariantBoneDisplayBlock.VARIANT)) {
                state = state.setValue(VariantBoneDisplayBlock.VARIANT, variant);
            }

            return state;
        }

        void placeLongRemains(List<Block> candidates, int max) {
            candidates.removeIf(block -> !(block instanceof LongDecoBlock));

            if (candidates.isEmpty() || max == 0) {
                return;
            }

            List<BlockPos> cells = this.floorCells();
            Set<BlockPos> free = new HashSet<>(cells);
            int count = this.random.nextInt(max + 1);

            for (int i = 0; i < count && !cells.isEmpty(); i++) {
                BlockPos foot = cells.remove(this.random.nextInt(cells.size()));
                Direction towardHead = this.freeNeighbor(foot, free);

                if (!free.contains(foot) || towardHead == null) {
                    continue;
                }

                BlockPos head = foot.relative(towardHead);

                BlockState state = candidates.get(this.random.nextInt(candidates.size())).defaultBlockState()
                        .setValue(LongDecoBlock.FACING, this.toTemplate(towardHead));
                this.put(foot, state.setValue(LongDecoBlock.PART, BedPart.FOOT));
                this.put(head, state.setValue(LongDecoBlock.PART, BedPart.HEAD));
                free.remove(foot);
                free.remove(head);
            }
        }

        void placeRemains(List<Block> onFloor, List<Block> atWall, int max) {
            if (onFloor.isEmpty() || max == 0) {
                return;
            }

            List<BlockPos> cells = this.floorCells();
            int count = this.random.nextInt(max + 1);

            for (int i = 0; i < count && !cells.isEmpty(); i++) {
                BlockPos cell = cells.remove(this.random.nextInt(cells.size()));
                Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
                Direction wall = this.adjacentWall(cell);
                boolean leans = wall != null && !atWall.isEmpty() && this.random.nextFloat() < WALL_REMAINS_CHANCE;

                List<Block> candidates = leans ? atWall : onFloor;
                Block block = candidates.get(this.random.nextInt(candidates.size()));
                this.put(cell, this.oriented(block, leans ? wall.getOpposite() : facing, this.random.nextInt(4)));
            }
        }

        private Direction freeNeighbor(BlockPos cell, Set<BlockPos> free) {
            Direction start = Direction.Plane.HORIZONTAL.getRandomDirection(this.random);
            for (int turns = 0; turns < 4; turns++) {
                Direction direction = Direction.from2DDataValue(start.get2DDataValue() + turns);

                if (free.contains(cell.relative(direction))) {
                    return direction;
                }
            }
            return null;
        }

        private Direction adjacentWall(BlockPos cell) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                StructureBlockInfo info = this.blocks.get(cell.relative(direction));

                if (info != null && isFullCube(info.state())) {
                    return direction;
                }
            }
            return null;
        }

        private List<BlockPos> floorCells() {
            List<BlockPos> cells = new ArrayList<>();
            for (Map.Entry<Long, Integer> column : this.columnTops().entrySet()) {
                int top = column.getValue();
                BlockPos ground = new BlockPos(ChunkPos.getX(column.getKey()), top, ChunkPos.getZ(column.getKey()));

                if (top <= this.groundLevel(column.getKey()) + 1 && isFloor(this.blocks.get(ground).state())) {
                    cells.add(ground.above());
                }
            }
            return cells;
        }

        /// A piece that follows the terrain has no common base layer. Each column has its own.
        private int groundLevel(long column) {
            return this.followsTerrain ? this.columnBottoms.get(column) : this.minY;
        }

        private Map<Long, Integer> columnTops() {
            Map<Long, Integer> tops = new LinkedHashMap<>();
            for (BlockPos pos : this.blocks.keySet()) {
                tops.merge(ChunkPos.pack(pos.getX(), pos.getZ()), pos.getY(), Math::max);
            }
            return tops;
        }

        private void put(BlockPos pos, BlockState state) {
            this.blocks.put(pos, new StructureBlockInfo(pos, state, null));
        }

        /// The template stores directions before the piece rotation. The positions are already rotated.
        private Direction toWorld(Direction templateDirection) {
            return this.data.getRotation().rotate(this.data.getMirror().mirror(templateDirection));
        }

        private Direction toTemplate(Direction worldDirection) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (this.toWorld(direction) == worldDirection) {
                    return direction;
                }
            }
            return worldDirection;
        }

        private static float resistance(BlockState state) {
            boolean post = state.is(BlockTags.LOGS) && state.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
            return post ? LOG_RESISTANCE : 0.0F;
        }

        private static boolean isRubbleMaterial(BlockState state) {
            return isFullCube(state) || state.getBlock() instanceof StairBlock || state.getBlock() instanceof SlabBlock;
        }

        private static boolean isFloor(BlockState state) {
            return isFullCube(state) || state.is(Blocks.DIRT_PATH);
        }

        private static boolean isFullCube(BlockState state) {
            return state.isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        }

        private static float valueNoise(long seed, int x, int z) {
            int cellX = Math.floorDiv(x, NOISE_CELL);
            int cellZ = Math.floorDiv(z, NOISE_CELL);
            float deltaX = Math.floorMod(x, NOISE_CELL) / (float) NOISE_CELL;
            float deltaZ = Math.floorMod(z, NOISE_CELL) / (float) NOISE_CELL;
            float near = Mth.lerp(deltaX, hash(seed, cellX, cellZ), hash(seed, cellX + 1, cellZ));
            float far = Mth.lerp(deltaX, hash(seed, cellX, cellZ + 1), hash(seed, cellX + 1, cellZ + 1));
            return Mth.lerp(deltaZ, near, far) * 2.0F - 1.0F;
        }

        private static float hash(long seed, int x, int z) {
            long mixed = seed + x * 0x9E3779B97F4A7C15L + z * 0xC2B2AE3D27D4EB4FL;
            mixed = (mixed ^ (mixed >>> 30)) * 0xBF58476D1CE4E5B9L;
            mixed = (mixed ^ (mixed >>> 27)) * 0x94D049BB133111EBL;
            mixed ^= mixed >>> 31;
            return (mixed >>> 40) / (float) (1 << 24);
        }
    }
}
