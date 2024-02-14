package com.lion.graveyard.blockentities;

import com.lion.graveyard.blockentities.animation.SarcophagusLidAnimator;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.entity.player.Player;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.chat.Component;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.World;
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

public class SarcophagusBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;
    private final SarcophagusLidAnimator lidAnimator;

    public SarcophagusBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.SARCOPHAGUS_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(54, ItemStack.EMPTY);
        this.stateManager = new ContainerOpenersCounter() {
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                if (state.getValue(SarcophagusBlock.IS_COFFIN)) { // internal property is_coffin
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.COFFIN_OPEN.get());
                } else {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.SARCOPHAGUS_USE.get());
                }
            }

            protected void onClose(Level world, BlockPos pos, BlockState state) {
                if (state.getValue(SarcophagusBlock.IS_COFFIN)) {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.COFFIN_CLOSE.get());
                } else {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.SARCOPHAGUS_USE.get());
                }
            }

            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                SarcophagusBlockEntity.this.signalOpenCount(world, pos, state, oldViewerCount, newViewerCount);
            }

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

    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(this.size(), ItemStack.EMPTY);
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

    public int size() {
        return 54;
    }

    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    protected void setItems(NonNullList<ItemStack> list) {
        this.inventory = list;
    }

    protected Component getDefaultName() {
        if (this.getBlockState().getValue(SarcophagusBlock.IS_COFFIN)) {
            return Component.translatable("container.coffin");
        }
        return Component.translatable("container.sarcophagus");
    }

    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return ChestMenu.sixRows(syncId, playerInventory, this);
    }

    @Override
    public int getContainerSize() {
        return 54;
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

        world.playSound((Player) null, d, e, f, soundEvent, SoundSource.BLOCKS, 0.75F, -70.0F);
    }

    /* ANIMATION STUFF */
    public static void clientTick(Level world, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

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

    public boolean isCoffin() {
        return this.getBlockState().getValue(SarcophagusBlock.IS_COFFIN);
    }
}
