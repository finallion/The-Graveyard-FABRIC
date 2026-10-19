package com.lion.graveyard.blockentities;

import com.lion.graveyard.blockentities.animation.SarcophagusLidAnimator;
import com.lion.graveyard.blocks.SarcophagusBlock;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.init.TGSounds;
import com.lion.graveyard.util.LegacyContainerName;
import com.lion.graveyard.util.LegacyItemStacks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
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
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SarcophagusBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private NonNullList<ItemStack> inventory;
    private final ContainerOpenersCounter stateManager;
    private Component legacyName;
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
            protected void openerCountChanged(
                    Level world,
                    BlockPos pos,
                    BlockState state,
                    int oldViewerCount,
                    int newViewerCount
            ) {
                SarcophagusBlockEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
            }

            @Override
            public boolean isOwnContainer(Player player) {
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
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.legacyName = LegacyContainerName.read(input).orElse(null);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        if (!this.tryLoadLootTable(input)) {
            LegacyItemStacks.loadSlots(input, this.inventory);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.legacyName != null) {
            output.store(LegacyContainerName.CUSTOM_NAME_KEY, ComponentSerialization.CODEC, this.legacyName);
        }

        LegacyItemStacks.writeDataVersion(output);

        if (!this.trySaveLootTable(output)) {
            LegacyItemStacks.saveSlots(output, this.inventory);
        }
    }

    @Override
    public Component getName() {
        return this.legacyName != null ? this.legacyName : super.getName();
    }

    @Override
    public Component getCustomName() {
        return this.legacyName != null ? this.legacyName : super.getCustomName();
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        this.legacyName = null;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CUSTOM_NAME, this.getCustomName());
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

    public boolean isCoffin() {
        return this.getBlockState().getValue(SarcophagusBlock.IS_COFFIN);
    }
}
