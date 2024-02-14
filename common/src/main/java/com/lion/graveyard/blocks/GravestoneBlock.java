package com.lion.graveyard.blocks;;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GravestoneBlock extends SignBlock implements EntityBlock {
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty FLOOR = BlockStateProperties.BOTTOM;
    private static final VoxelShape SHAPE_FACING_EW = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_FACING_NS = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private final ResourceLocation texture;

    public GravestoneBlock(ResourceLocation texture) {
        super(WoodType.OAK, BlockBehaviour.Properties.of().noCollission().noOcclusion().sound(SoundType.BASALT).strength(1.5F));
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(FLOOR, true).setValue(WATERLOGGED, false));
        this.texture = texture;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FLOOR, WATERLOGGED, ROTATION);
    }

    @Override
    protected MapCodec<? extends SignBlock> codec() {
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_220053_2_, BlockPos p_220053_3_, CollisionContext p_220053_4_) {
        if (Direction.NORTH == state.getValue(FACING) || Direction.SOUTH == state.getValue(FACING)) {
            return SHAPE_FACING_NS;
        } else {
            return SHAPE_FACING_EW;
        }
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GravestoneBlockEntity(pos, state);
    }


    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        Item item2 = itemStack.getItem();
        SignApplicator signChangingItem;
        if (item2 instanceof SignApplicator) {
            signChangingItem = (SignApplicator)item2;
        } else {
            signChangingItem = null;
        }

        SignApplicator signChangingItem2 = signChangingItem;
        boolean bl = signChangingItem2 != null && player.mayBuild();
        BlockEntity var12 = world.getBlockEntity(pos);
        if (var12 instanceof GravestoneBlockEntity) {
            GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity)var12;
            if (!world.isClientSide()) {
                SignText signText = signBlockEntity.getText();
                boolean bl3 = signBlockEntity.executeClickCommandsIfPresent(player, world, pos);
                if (signBlockEntity.isWaxed()) {
                    world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.WAXED_SIGN_INTERACT_FAIL, SoundSource.BLOCKS);
                    return InteractionResult.PASS;
                } else if (bl && !this.otherPlayerIsEditingSign(player, signBlockEntity) && signChangingItem2.canApplyToSign(signText, player)) {
                    boolean useOnSign = false;
                    if (signChangingItem2 instanceof DyeItem dyeItem) {
                        useOnSign = useDyeOnSign(world, signBlockEntity, dyeItem.getDyeColor());
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
                } else if (bl3) {
                    return InteractionResult.SUCCESS;
                } else if (!this.otherPlayerIsEditingSign(player, signBlockEntity) && player.mayBuild() && this.isTextLiteralOrEmpty(player, signBlockEntity)) {
                    this.openEditScreen(player, signBlockEntity, world, pos, state);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.PASS;
                }
            } else {
                return !bl && !signBlockEntity.isWaxed() ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public float getYRotationDegrees(BlockState state) {
        return ((Direction)state.getValue(FACING)).toYRot();
    }

    public boolean useDyeOnSign(Level world, GravestoneBlockEntity signBlockEntity, DyeColor color) {
        if (signBlockEntity.updateText((text) -> text.setColor(color))) {
            world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useInkOnSign(Level world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.updateText((text) -> {
            return text.setHasGlowingText(false);
        })) {
            world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useGlowInkOnSign(Level world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.updateText((text) -> text.setHasGlowingText(true))) {
            world.playSound(null, signBlockEntity.getBlockPos(), SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useWaxOnSign(Level world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.setWaxed(true)) {
            world.levelEvent((Player)null, 3003, signBlockEntity.getBlockPos(), 0);
            return true;
        } else {
            return false;
        }
    }

    private boolean isTextLiteralOrEmpty(Player player, GravestoneBlockEntity blockEntity) {
        SignText signText = blockEntity.getText();
        return Arrays.stream(signText.getMessages(player.isTextFilteringEnabled())).allMatch((message) -> message.equals(CommonComponents.EMPTY) || message.getContents() instanceof PlainTextContents);
    }

    public void openEditScreen(Player player, GravestoneBlockEntity blockEntity, Level world, BlockPos pos, BlockState state) {
        blockEntity.setAllowedPlayerEditor(player.getUUID());
        openScreen(world, pos, player);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @org.jetbrains.annotations.Nullable LivingEntity placer, ItemStack p_49851_) {
        if (placer instanceof Player player) {
            openScreen(world, pos, player);
        }
    }


    private void openScreen(Level world, BlockPos pos, Player placer) {
        GravestoneBlockEntity sign = (GravestoneBlockEntity) world.getBlockEntity(pos);
        if (!world.isClientSide) {
            sign.setAllowedPlayerEditor(placer.getUUID());
            ((ServerPlayer) placer).connection.send(new ClientboundOpenSignEditorPacket(pos, true));
        }
    }

    private boolean otherPlayerIsEditingSign(Player player, GravestoneBlockEntity signEntity) {
        UUID uUID = signEntity.getPlayerWhoMayEdit();
        return uUID != null && !uUID.equals(player.getUUID());
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        Direction direction = context.getHorizontalDirection();
        return this.defaultBlockState().setValue(FACING, direction.getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return Collections.singletonList(new ItemStack(this));
    }

    public ResourceLocation getTexture() {
        return texture;
    }


    public boolean canSurvive(BlockState p_196260_1_, LevelReader p_196260_2_, BlockPos p_196260_3_) {
        return p_196260_2_.getBlockState(p_196260_3_.below()).isSolid();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
        if (Direction.NORTH == state.getValue(FACING) || Direction.SOUTH == state.getValue(FACING)) {
            return SHAPE_FACING_NS;
        } else {
            return SHAPE_FACING_EW;
        }
    }

    @Override
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
        return false;
    }
}