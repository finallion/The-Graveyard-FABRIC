package com.lion.graveyard.blockentities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class UrnBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;

    public UrnBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.URN_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(54, ItemStack.EMPTY);
        this.stateManager = new ContainerOpenersCounter() {
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                UrnBlockEntity.playSound(world, pos, state, TGSounds.URN_OPEN.get());
            }

            protected void onClose(Level world, BlockPos pos, BlockState state) {
                UrnBlockEntity.playSound(world, pos, state, TGSounds.URN_CLOSE.get());
            }

            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                UrnBlockEntity.this.signalOpenCount(world, pos, state, oldViewerCount, newViewerCount);
            }

            protected boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                } else {
                    Container inventory = ((ChestMenu) player.containerMenu).getContainer();
                    return inventory == UrnBlockEntity.this || inventory instanceof CompoundContainer && ((CompoundContainer) inventory).contains(UrnBlockEntity.this);
                }
            }
        };
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt)) {
            ContainerHelper.loadAllItems(nbt, this.inventory);
        }

    }

    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (!this.trySaveLootTable(nbt)) {
            ContainerHelper.saveAllItems(nbt, this.inventory);
        }

    }

    public int getContainerSize() {
        return 54;
    }

    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    protected void setItems(NonNullList<ItemStack> list) {
        this.inventory = list;
    }

    protected Component getDefaultName() {
        return Component.translatable("container.urn");
    }

    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        boolean large = Graveyard.CONFIG.booleanEntries.get("urnHasDoubleInventory");
        if (large) {
            return ChestMenu.sixRows(syncId, playerInventory, this);
        }

        return ChestMenu.threeRows(syncId, playerInventory, this);
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.stateManager.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.stateManager.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void onScheduledTick() {
        if (!this.remove) {
            this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    protected void signalOpenCount(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.blockEvent(pos, block, 1, newViewerCount);
    }


    static void playSound(Level world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        double d = (double) pos.getX() + 0.5D;
        double e = (double) pos.getY() + 0.5D;
        double f = (double) pos.getZ() + 0.5D;


        world.playSound((Player) null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);

    }


}
