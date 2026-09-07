package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

/// Bone remains that arrange themselves from a pose.
///
/// The pose is **not** stored. It follows from the block position, so a piece placed by a structure
/// carries one without anything having written it, and the world file stays as small as it was. The
/// only thing kept is how often a player has clicked to the next pose - one optional key, absent on
/// everything placed before it existed.
public class SkeletalRemainsBlockEntity extends BlockEntity {

    private static final String VARIANT_KEY = "Variant";

    private int variant;

    public SkeletalRemainsBlockEntity(BlockPos pos, BlockState state) {
        super(TGBlockEntities.SKELETAL_REMAINS_BLOCK_ENTITY.get(), pos, state);
    }

    /// Position and click count together; the renderer turns this into one of the known poses.
    public long poseSeed() {
        BlockPos pos = this.getPos();
        long seed = pos.getX() * 3129871L ^ pos.getZ() * 116129781L ^ pos.getY();
        return seed + this.variant * 0x9E3779B97F4A7C15L;
    }

    public void nextPose() {
        this.variant++;
        this.markDirty();

        if (this.world != null) {
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.variant != 0) {
            nbt.putInt(VARIANT_KEY, this.variant);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.variant = nbt.contains(VARIANT_KEY) ? nbt.getInt(VARIANT_KEY) : 0;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }
}
