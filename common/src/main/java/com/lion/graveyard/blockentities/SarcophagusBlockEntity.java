package com.lion.graveyard.blockentities;

import com.lion.graveyard.blockentities.animation.SarcophagusLidAnimator;
import com.lion.graveyard.blocks.SarcophagusBlock;
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
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class SarcophagusBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;
    private final SarcophagusLidAnimator lidAnimator;

    public SarcophagusBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(54, ItemStack.EMPTY);
        this.stateManager = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                if (state.getValue(SarcophagusBlock.IS_COFFIN)) {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.COFFIN_OPEN.get());
                } else {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.SARCOPHAGUS_USE.get());
                }
            }

            @Override
            protected void onClose(Level world, BlockPos pos, BlockState state) {
                if (state.getValue(SarcophagusBlock.IS_COFFIN)) {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.COFFIN_CLOSE.get());
                } else {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.SARCOPHAGUS_USE.get());
                }
            }

            @Override
            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                SarcophagusBlockEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                } else {
                    Container inventory = ((ChestMenu) player.containerMenu).getContainer();
                    return inventory == SarcophagusBlockEntity.this || inventory instanceof CompoundContainer && ((CompoundContainer) inventory).contains(SarcophagusBlockEntity.this);
                }
            }
        };
        this.lidAnimator = new SarcophagusLidAnimator();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(nbt)) {
            ContainerHelper.loadAllItems(nbt, this.inventory);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (!this.trySaveLootTable(nbt)) {
            ContainerHelper.saveAllItems(nbt, this.inventory);
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
        if (this.getBlockState().getValue(SarcophagusBlock.IS_COFFIN)) {
            return Component.translatable("container.coffin");
        }

        return Component.translatable("container.sarcophagus");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return ChestMenu.sixRows(syncId, playerInventory, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.stateManager.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
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

    protected void onInvOpenOrClose(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.blockEvent(pos, block, 1, newViewerCount);
    }

    static void playSound(Level world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        double d = (double) pos.getX() + 0.5D;
        double e = (double) pos.getY() + 0.5D;
        double f = (double) pos.getZ() + 0.5D;

        world.playSound((Player) null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.75F, -70.0F);
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    @Override
    public boolean triggerEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        } else {
            return super.triggerEvent(type, data);
        }
    }

    @Override
    public float getOpenNess(float tickDelta) {
        return this.lidAnimator.getProgress(tickDelta);
    }

    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos())
                .inflate(1.0D)
                .expandTowards(0.0D, 1.0D, 0.0D);
    }

    public boolean isCoffin() {
        return this.getBlockState().getValue(SarcophagusBlock.IS_COFFIN);
    }
}
