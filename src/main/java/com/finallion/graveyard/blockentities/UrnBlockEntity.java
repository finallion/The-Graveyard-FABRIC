package com.finallion.graveyard.blockentities;

import com.finallion.graveyard.blocks.UrnBlock;
import com.finallion.graveyard.init.TGBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestStateManager;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class UrnBlockEntity extends LootableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory;
    private ChestStateManager stateManager;

    public UrnBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlocks.URN_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
        this.stateManager = new ChestStateManager() {
            protected void onChestOpened(World world, BlockPos pos, BlockState state) {
                UrnBlockEntity.this.playSound(state, SoundEvents.BLOCK_SHULKER_BOX_OPEN);
                UrnBlockEntity.this.setOpen(state, true);
            }

            protected void onChestClosed(World world, BlockPos pos, BlockState state) {
                UrnBlockEntity.this.playSound(state, SoundEvents.BLOCK_SHULKER_BOX_CLOSE);
                UrnBlockEntity.this.setOpen(state, false);
            }

            protected void onInteracted(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                    Inventory inventory = ((GenericContainerScreenHandler) player.currentScreenHandler).getInventory();
                    return inventory == UrnBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }

    }

    public int size() {
        return 27;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    protected Text getContainerName() {
        return new TranslatableText("container.urn");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openChest(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeChest(player, this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }

    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), (BlockState) state.with(UrnBlock.OPEN, open), 3);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = ((Direction) state.get(UrnBlock.FACING)).getVector();
        double d = (double) this.pos.getX() + 0.5D + (double) vec3i.getX() / 2.0D;
        double e = (double) this.pos.getY() + 0.5D + (double) vec3i.getY() / 2.0D;
        double f = (double) this.pos.getZ() + 0.5D + (double) vec3i.getZ() / 2.0D;
        this.world.playSound((PlayerEntity) null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }
}
