package com.lion.graveyard.blockentities;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/// A placed chronicle page. Only the page id is stored; the text lives in the chronicle assets, so
/// a structure can drop one of these anywhere with a single NBT key.
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
        this.markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.page != null) nbt.putString(PAGE_KEY, this.page);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        String stored = nbt.contains(PAGE_KEY, NbtElement.STRING_TYPE) ? nbt.getString(PAGE_KEY) : null;
        this.page = Chronicle.isValidId(stored) ? stored : null;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
