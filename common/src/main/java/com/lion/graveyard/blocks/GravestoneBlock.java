package com.lion.graveyard.blocks;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.resources.Identifier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.GlowInkSacItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.InkSacItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GravestoneBlock extends StandingSignBlock implements EntityBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FLOOR = BlockStateProperties.BOTTOM;
    private static final VoxelShape SHAPE_FACING_EW = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_FACING_NS = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private final Identifier texture;

    public GravestoneBlock(BlockBehaviour.Properties properties, Identifier texture) {
        super(WoodType.OAK, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLOOR, true)
                .setValue(SignBlock.WATERLOGGED, false));
        this.texture = texture;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FLOOR, SignBlock.WATERLOGGED, ROTATION);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (Direction.NORTH == state.getValue(FACING) || Direction.SOUTH == state.getValue(FACING)) {
            return SHAPE_FACING_NS;
        } else {
            return SHAPE_FACING_EW;
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GravestoneBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level world,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        Item item = itemStack.getItem();
        Item item2 = itemStack.getItem();
        SignApplicator signChangingItem;

        if (item2 instanceof SignApplicator) {
            signChangingItem = (SignApplicator)item2;
        } else {
            signChangingItem = null;
        }

        SignApplicator signChangingItem2 = signChangingItem;
        boolean canApplySignItem = signChangingItem2 != null && player.mayBuild();
        BlockEntity foundEntity = world.getBlockEntity(pos);

        if (foundEntity instanceof GravestoneBlockEntity) {
            GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity)foundEntity;

            if (!world.isClientSide()) {
                SignText signText = signBlockEntity.getText();
                boolean ranCommand = signBlockEntity.runCommandClickEvent(player, world, pos);

                if (signBlockEntity.isWaxed()) {
                    world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.WAXED_SIGN_INTERACT_FAIL, SoundSource.BLOCKS);
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                } else if (canApplySignItem && !this.isOtherPlayerEditing(player, signBlockEntity) && signChangingItem2.canApplyToSign(signText, itemStack, player)) {
                    boolean useOnSign = false;

                    if (signChangingItem2 instanceof DyeItem) {
                        DyeColor dyeColor = itemStack.get(DataComponents.DYE);
                        useOnSign = dyeColor != null && useDyeOnSign(world, signBlockEntity, dyeColor);
                    } else if (signChangingItem2 instanceof InkSacItem) {
                        useOnSign = useInkOnSign(world, signBlockEntity);
                    } else if (signChangingItem2 instanceof HoneycombItem) {
                        useOnSign = useWaxOnSign(world, signBlockEntity);
                    } else if (signChangingItem2 instanceof GlowInkSacItem) {
                        useOnSign = useGlowInkOnSign(world, signBlockEntity);
                    }

                    if (useOnSign) {
                        if (!player.isCreative()) {
                            itemStack.shrink(1);
                        }

                        world.gameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getBlockPos(), GameEvent.Context.of(player, signBlockEntity.getBlockState()));
                        player.awardStat(Stats.ITEM_USED.get(item));
                        return InteractionResult.SUCCESS;
                    }
                } else if (ranCommand) {
                    return InteractionResult.SUCCESS;
                } else if (!this.isOtherPlayerEditing(player, signBlockEntity) && player.mayBuild() && this.isTextLiteralOrEmpty(player, signBlockEntity)) {
                    this.openEditScreen(player, signBlockEntity, world, pos, state);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.TRY_WITH_EMPTY_HAND;
                }
            } else {
                return !canApplySignItem && !signBlockEntity.isWaxed() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public boolean useDyeOnSign(Level world, GravestoneBlockEntity signBlockEntity, DyeColor color) {
        if (signBlockEntity.changeText((text) -> text.setColor(color))) {
            world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useInkOnSign(Level world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.changeText((text) -> {
            return text.setHasGlowingText(false);
        })) {
            world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useGlowInkOnSign(Level world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.changeText((text) -> text.setHasGlowingText(true))) {
            world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useWaxOnSign(Level world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.setWaxed(true)) {
            world.levelEvent(null, 3003, signBlockEntity.getBlockPos(), 0);
            return true;
        } else {
            return false;
        }
    }

    private boolean isTextLiteralOrEmpty(Player player, GravestoneBlockEntity blockEntity) {
        SignText signText = blockEntity.getText();
        return Arrays.stream(signText.getMessages(player.isTextFilteringEnabled())).allMatch((message) -> message.equals(CommonComponents.EMPTY) || message.getContents() instanceof PlainTextContents);
    }

    public void openEditScreen(
            Player player,
            GravestoneBlockEntity blockEntity,
            Level world,
            BlockPos pos,
            BlockState state
    ) {
        blockEntity.setEditor(player.getUUID());
        openScreen(world, pos, player);
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack
    ) {
        if (placer instanceof Player player) {
            openScreen(world, pos, player);
        }
    }

    private void openScreen(Level world, BlockPos pos, Player placer) {
        GravestoneBlockEntity sign = (GravestoneBlockEntity) world.getBlockEntity(pos);

        if (!world.isClientSide()) {
            sign.setEditor(placer.getUUID());
            ((ServerPlayer) placer).connection.send(new ClientboundOpenSignEditorPacket(pos, true));
        }
    }


    private boolean isOtherPlayerEditing(Player player, GravestoneBlockEntity blockEntity) {
        UUID editorId = blockEntity.getEditor();
        return editorId != null && !editorId.equals(player.getUUID()) && !blockEntity.isPlayerTooFarToEdit(editorId);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        Direction direction = ctx.getHorizontalDirection();
        return this.defaultBlockState()
                .setValue(FACING, direction.getOpposite())
                .setValue(SignBlock.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }


    public Identifier getTexture() {
        return texture;
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isSolid();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (Direction.NORTH == state.getValue(FACING) || Direction.SOUTH == state.getValue(FACING)) {
            return SHAPE_FACING_NS;
        } else {
            return SHAPE_FACING_EW;
        }
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }


}