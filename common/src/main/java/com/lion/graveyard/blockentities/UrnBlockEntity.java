package com.lion.graveyard.blockentities;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.util.LegacyItemStacks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class UrnBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;

    public UrnBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.URN_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(54, ItemStack.EMPTY);
        this.stateManager = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                UrnBlockEntity.playSound(world, pos, state, TGSounds.URN_OPEN.get());
            }

            @Override
            protected void onClose(Level world, BlockPos pos, BlockState state) {
                UrnBlockEntity.playSound(world, pos, state, TGSounds.URN_CLOSE.get());
            }

            @Override
            protected void openerCountChanged(
                    Level world,
                    BlockPos pos,
                    BlockState state,
                    int oldViewerCount,
                    int newViewerCount
            ) {
                UrnBlockEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
            }

            @Override
            public boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                } else {
                    Container inventory = ((ChestMenu) player.containerMenu).getContainer();
                    return inventory == UrnBlockEntity.this || inventory instanceof CompoundContainer && ((CompoundContainer) inventory).contains(UrnBlockEntity.this);
                }
            }
        };
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(input)) {
            LegacyItemStacks.loadSlots(input, this.inventory);
        }

    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (!this.trySaveLootTable(output)) {
            LegacyItemStacks.saveSlots(output, this.inventory);
        }

    }

    @Override
    public int getContainerSize() {
        return 54;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.urn");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        boolean large = Graveyard.CONFIG.booleanEntries.get("urnHasDoubleInventory");

        if (large) {
            return ChestMenu.sixRows(syncId, playerInventory, this);
        }

        return ChestMenu.threeRows(syncId, playerInventory, this);
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.stateManager.incrementOpeners(
                    user.getLivingEntity(),
                    this.getLevel(),
                    this.getBlockPos(),
                    this.getBlockState(),
                    user.getContainerInteractionRange()
            );
        }

    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (!this.remove && !user.getLivingEntity().isSpectator()) {
            this.stateManager.decrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    public void onScheduledTick() {
        if (!this.remove) {
            this.stateManager.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }


    protected void onInvOpenOrClose(
            Level world,
            BlockPos pos,
            BlockState state,
            int oldViewerCount,
            int newViewerCount
    ) {
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
