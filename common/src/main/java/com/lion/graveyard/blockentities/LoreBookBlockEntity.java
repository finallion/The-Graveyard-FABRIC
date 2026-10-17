package com.lion.graveyard.blockentities;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LoreBookBlockEntity extends BlockEntity {
    public static final String PAGE_KEY = "page";

    @Nullable private String page;

    public LoreBookBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.LORE_BOOK_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    public String getPage() {
        return this.page;
    }

    public void setPage(@Nullable String page) {
        this.page = Chronicle.isValidId(page) ? page : null;
        this.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);

        if (this.page != null) {
            nbt.putString(PAGE_KEY, this.page);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        String stored = nbt.contains(PAGE_KEY, Tag.TAG_STRING) ? nbt.getString(PAGE_KEY) : null;
        this.page = Chronicle.isValidId(stored) ? stored : null;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
