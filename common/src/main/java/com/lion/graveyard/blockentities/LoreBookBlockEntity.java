package com.lion.graveyard.blockentities;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.init.TGBlockEntities;
import com.lion.graveyard.util.LegacyItemStacks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (this.page != null) {
            output.putString(PAGE_KEY, this.page);
        }

        LegacyItemStacks.writeDataVersion(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String stored = input.getString(PAGE_KEY).orElse(null);
        this.page = Chronicle.isValidId(stored) ? stored : null;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
