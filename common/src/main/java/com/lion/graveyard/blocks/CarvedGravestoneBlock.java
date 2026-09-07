package com.lion.graveyard.blocks;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/// A gravestone that takes its look from whatever block it was carved out of, instead of shipping
/// one block, model and texture per material. The material travels with the item, so breaking and
/// replacing keeps it.
public class CarvedGravestoneBlock extends GravestoneBlock {

    /// Deliberately not inside `BlockEntityTag`: vanilla only copies that tag onto a block entity
    /// for creative operators, because this one also carries sign text.
    private static final String MATERIAL_KEY = "GravestoneMaterial";

    private final boolean fromItemModel;
    private final double textHeight;
    private final double textDepth;
    private final float textPitch;

    public CarvedGravestoneBlock(Identifier texture) {
        this(texture, true, 0.3333333432674408D, 0.23D, 0.0F);
    }

    /// How the stone is drawn and where its inscription sits. Constructor arguments rather than
    /// subclass overrides, so a differently shaped stone costs a registration line.
    public CarvedGravestoneBlock(Identifier texture, boolean fromItemModel, double textHeight, double textDepth, float textPitch) {
        super(texture);
        this.fromItemModel = fromItemModel;
        this.textHeight = textHeight;
        this.textDepth = textDepth;
        this.textPitch = textPitch;
    }

    /// The block entity draws the whole stone with the material's texture. Leaving the blockstate
    /// model on top of that would render a second, placeholder-textured copy inside it.
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    /// The first carved gravestone took the item-model path over from the thirteen before it, which
    /// carries an item transformation and a scale factor. Anything authored since is drawn straight
    /// from its block model, at the size it was modelled.
    public boolean drawsFromItemModel() {
        return this.fromItemModel;
    }

    /// Where the inscription sits, measured from the bottom of the block.
    public double textHeight() {
        return this.textHeight;
    }

    /// How far the inscription stands off the stone's face.
    public double textDepth() {
        return this.textDepth;
    }

    /// Tilt of the inscription plate around its own anchor, in degrees. A stone lying on its back
    /// carries its text on the upward face, which no offset alone can express.
    public float textPitch() {
        return this.textPitch;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof GravestoneBlockEntity gravestone) {
            gravestone.setMaterial(readMaterial(itemStack));
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        ItemStack stack = new ItemStack(this);
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof GravestoneBlockEntity gravestone) {
            writeMaterial(stack, gravestone.getMaterial());
        }

        return Collections.singletonList(stack);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        if (world.getBlockEntity(pos) instanceof GravestoneBlockEntity gravestone) {
            writeMaterial(stack, gravestone.getMaterial());
        }

        return stack;
    }

    @Nullable
    public static Block readMaterial(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(MATERIAL_KEY)) {
            return null;
        }

        Identifier id = Identifier.tryParse(nbt.getString(MATERIAL_KEY));
        return id == null ? null : Registries.BLOCK.getOrEmpty(id).orElse(null);
    }

    public static void writeMaterial(ItemStack stack, @Nullable Block material) {
        if (material == null) {
            return;
        }

        stack.getOrCreateNbt().putString(MATERIAL_KEY, Registries.BLOCK.getId(material).toString());
    }
}
