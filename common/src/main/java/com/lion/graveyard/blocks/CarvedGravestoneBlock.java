package com.lion.graveyard.blocks;

import com.lion.graveyard.blockentities.GravestoneBlockEntity;
import net.minecraft.block.Block;
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

    public CarvedGravestoneBlock(Identifier texture) {
        super(texture);
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
