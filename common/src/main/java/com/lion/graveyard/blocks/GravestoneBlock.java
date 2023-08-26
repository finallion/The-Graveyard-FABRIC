package com.lion.graveyard.blocks;;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GravestoneBlock extends SignBlock implements BlockEntityProvider {
    public static final IntProperty ROTATION = Properties.ROTATION;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty FLOOR = Properties.BOTTOM;
    private static final VoxelShape SHAPE_FACING_EW = Block.createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_FACING_NS = Block.createCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    private final Identifier texture;

    public GravestoneBlock(Identifier texture) {
        super(AbstractBlock.Settings.create().noCollision().nonOpaque().sounds(BlockSoundGroup.DEEPSLATE_BRICKS).strength(1.5F), WoodType.OAK);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(FLOOR, true).with(AbstractSignBlock.WATERLOGGED, false));
        this.texture = texture;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FLOOR, AbstractSignBlock.WATERLOGGED, ROTATION);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (Direction.NORTH == state.get(FACING) || Direction.SOUTH == state.get(FACING)) {
            return SHAPE_FACING_NS;
        } else {
            return SHAPE_FACING_EW;
        }
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GravestoneBlockEntity(pos, state);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        Item item2 = itemStack.getItem();
        SignChangingItem signChangingItem;
        if (item2 instanceof SignChangingItem) {
            signChangingItem = (SignChangingItem)item2;
        } else {
            signChangingItem = null;
        }

        SignChangingItem signChangingItem2 = signChangingItem;
        boolean bl = signChangingItem2 != null && player.canModifyBlocks();
        BlockEntity var12 = world.getBlockEntity(pos);
        if (var12 instanceof GravestoneBlockEntity) {
            GravestoneBlockEntity signBlockEntity = (GravestoneBlockEntity)var12;
            if (!world.isClient) {
                SignText signText = signBlockEntity.getText();
                boolean bl3 = signBlockEntity.runCommandClickEvent(player, world, pos);
                if (signBlockEntity.isWaxed()) {
                    world.playSound(null, signBlockEntity.getPos(), SoundEvents.BLOCK_SIGN_WAXED_INTERACT_FAIL, SoundCategory.BLOCKS);
                    return ActionResult.PASS;
                } else if (bl && !this.isOtherPlayerEditing(player, signBlockEntity) && signChangingItem2.canUseOnSignText(signText, player)) {
                    boolean useOnSign = false;
                    if (signChangingItem2 instanceof DyeItem dyeItem) {
                        useOnSign = useDyeOnSign(world, signBlockEntity, dyeItem.getColor());
                    } else if (signChangingItem2 instanceof InkSacItem) {
                        useOnSign = useInkOnSign(world, signBlockEntity);
                    } else if (signChangingItem2 instanceof HoneycombItem) {
                        useOnSign = useWaxOnSign(world, signBlockEntity);
                    } else if (signChangingItem2 instanceof GlowInkSacItem) {
                        useOnSign = useGlowInkOnSign(world, signBlockEntity);
                    }

                    if (useOnSign) {
                        if (!player.isCreative()) {
                            itemStack.decrement(1);
                        }

                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, signBlockEntity.getPos(), GameEvent.Emitter.of(player, signBlockEntity.getCachedState()));
                        player.incrementStat(Stats.USED.getOrCreateStat(item));
                        return ActionResult.SUCCESS;
                    }
                } else if (bl3) {
                    return ActionResult.SUCCESS;
                } else if (!this.isOtherPlayerEditing(player, signBlockEntity) && player.canModifyBlocks() && this.isTextLiteralOrEmpty(player, signBlockEntity)) {
                    this.openEditScreen(player, signBlockEntity, world, pos, state);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            } else {
                return !bl && !signBlockEntity.isWaxed() ? ActionResult.CONSUME : ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public boolean useDyeOnSign(World world, GravestoneBlockEntity signBlockEntity, DyeColor color) {
        if (signBlockEntity.changeText((text) -> text.withColor(color))) {
            world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_DYE_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useInkOnSign(World world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.changeText((text) -> {
            return text.withGlowing(false);
        })) {
            world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useGlowInkOnSign(World world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.changeText((text) -> text.withGlowing(true))) {
            world.playSound(null, signBlockEntity.getPos(), SoundEvents.ITEM_GLOW_INK_SAC_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    public boolean useWaxOnSign(World world, GravestoneBlockEntity signBlockEntity) {
        if (signBlockEntity.setWaxed(true)) {
            world.syncWorldEvent(null, 3003, signBlockEntity.getPos(), 0);
            return true;
        } else {
            return false;
        }
    }

    private boolean isTextLiteralOrEmpty(PlayerEntity player, GravestoneBlockEntity blockEntity) {
        SignText signText = blockEntity.getText();
        return Arrays.stream(signText.getMessages(player.shouldFilterText())).allMatch((message) -> message.equals(ScreenTexts.EMPTY) || message.getContent() instanceof LiteralTextContent);
    }

    public void openEditScreen(PlayerEntity player, GravestoneBlockEntity blockEntity, World world, BlockPos pos, BlockState state) {
        blockEntity.setEditor(player.getUuid());
        openScreen(world, pos, player);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer instanceof PlayerEntity player) {
            openScreen(world, pos, player);
        }
    }

    private void openScreen(World world, BlockPos pos, PlayerEntity placer) {
        GravestoneBlockEntity sign = (GravestoneBlockEntity) world.getBlockEntity(pos);
        if (!world.isClient) {
            sign.setEditor(placer.getUuid());
            ((ServerPlayerEntity) placer).networkHandler.sendPacket(new SignEditorOpenS2CPacket(pos, true));
        }
    }


    private boolean isOtherPlayerEditing(PlayerEntity player, GravestoneBlockEntity blockEntity) {
        UUID uUID = blockEntity.getEditor();
        return uUID != null && !uUID.equals(player.getUuid());
    }


    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        Direction direction = ctx.getHorizontalPlayerFacing();
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, direction.getOpposite()).with(AbstractSignBlock.WATERLOGGED, fluidState.getFluid() == Fluids.WATER));
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }


    public Identifier getTexture() {
        return texture;
    }


    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolid();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (Direction.NORTH == state.get(FACING) || Direction.SOUTH == state.get(FACING)) {
            return SHAPE_FACING_NS;
        } else {
            return SHAPE_FACING_EW;
        }
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }


}