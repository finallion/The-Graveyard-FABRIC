package com.finallion.graveyard.blockentities;

import com.finallion.graveyard.blockentities.animation.SarcophagusLidAnimator;
import com.finallion.graveyard.blocks.SarcophagusBlock;
import com.finallion.graveyard.init.TGBlocks;
import com.finallion.graveyard.init.TGSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SarcophagusBlockEntity extends LootableContainerBlockEntity implements LidOpenable {
    private DefaultedList<ItemStack> inventory;
    private final ViewerCountManager stateManager;
    private final SarcophagusLidAnimator lidAnimator;

    public SarcophagusBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlocks.SARCOPHAGUS_BLOCK_ENTITY, pos, state);
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
        this.stateManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                if (state.get(SarcophagusBlock.IS_COFFIN)) { // internal property is_coffin
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.COFFIN_OPEN);
                } else {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.SARCOPHAGUS_USE);
                }
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                if (state.get(SarcophagusBlock.IS_COFFIN)) {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.COFFIN_CLOSE);
                } else {
                    SarcophagusBlockEntity.playSound(world, pos, state, TGSounds.SARCOPHAGUS_USE);
                }
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                SarcophagusBlockEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                    return false;
                } else {
                    Inventory inventory = ((GenericContainerScreenHandler) player.currentScreenHandler).getInventory();
                    return inventory == SarcophagusBlockEntity.this || inventory instanceof DoubleInventory && ((DoubleInventory) inventory).isPart(SarcophagusBlockEntity.this);
                }
            }
        };
        this.lidAnimator = new SarcophagusLidAnimator();
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
    }

    public int size() {
        return 54;
    }

    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    protected Text getContainerName() {
        if (this.getCachedState().get(SarcophagusBlock.IS_COFFIN)) {
            return Text.translatable("container.coffin");
        }
        return Text.translatable("container.sarcophagus");
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    protected void onInvOpenOrClose(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
    }

    static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        double d = (double) pos.getX() + 0.5D;
        double e = (double) pos.getY() + 0.5D;
        double f = (double) pos.getZ() + 0.5D;

        world.playSound((PlayerEntity) null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.75F, -70.0F);
    }

    /* ANIMATION STUFF */
    public static void clientTick(World world, BlockPos pos, BlockState state, SarcophagusBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        } else {
            return super.onSyncedBlockEvent(type, data);
        }
    }

    public float getAnimationProgress(float tickDelta) {
        return this.lidAnimator.getProgress(tickDelta);
    }

    public boolean isCoffin() {
        return this.getCachedState().get(SarcophagusBlock.IS_COFFIN);
    }
}
