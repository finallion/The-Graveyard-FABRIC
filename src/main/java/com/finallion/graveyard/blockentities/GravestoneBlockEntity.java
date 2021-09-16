package com.finallion.graveyard.blockentities;

import com.finallion.graveyard.init.TGBlocks;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

public class GravestoneBlockEntity extends BlockEntity {
    private final Text[] texts;
    private boolean editable;
    private PlayerEntity editor;
    private final OrderedText[] textsBeingEdited;
    private DyeColor textColor;

	public GravestoneBlockEntity() {
        super(TGBlocks.GRAVESTONE_BLOCK_ENTITY);
        this.texts = new Text[]{LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY, LiteralText.EMPTY};
        this.editable = true;
        this.textsBeingEdited = new OrderedText[4];
        this.textColor = DyeColor.BLACK;
    }


    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        for(int i = 0; i < 4; ++i) {
            String string = Text.Serializer.toJson(this.texts[i]);
            nbt.putString("Text" + (i + 1), string);
        }

        nbt.putString("Color", this.textColor.getName());
        return nbt;
    }

    public void fromTag(BlockState state, NbtCompound tag) {
        this.editable = false;
        super.fromTag(state, tag);
        this.textColor = DyeColor.byName(tag.getString("Color"), DyeColor.BLACK);

        for(int i = 0; i < 4; ++i) {
            String string = tag.getString("Text" + (i + 1));
            Text text = Text.Serializer.fromJson(string.isEmpty() ? "\"\"" : string);
            if (this.world instanceof ServerWorld) {
                try {
                    this.texts[i] = Texts.parse(this.getCommandSource((ServerPlayerEntity)null), text, (Entity)null, 0);
                } catch (CommandSyntaxException var7) {
                    this.texts[i] = text;
                }
            } else {
                this.texts[i] = text;
            }

            this.textsBeingEdited[i] = null;
        }

    }



    @Environment(EnvType.CLIENT)
    public Text getTextOnRow(int row) {
        return this.texts[row];
    }

    public void setTextOnRow(int row, Text text) {
        this.texts[row] = text;
        this.textsBeingEdited[row] = null;
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public OrderedText getTextBeingEditedOnRow(int row, Function<Text, OrderedText> function) {
        if (this.textsBeingEdited[row] == null && this.texts[row] != null) {
            this.textsBeingEdited[row] = (OrderedText)function.apply(this.texts[row]);
        }

        return this.textsBeingEdited[row];
    }


    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 9, this.toInitialChunkDataNbt());
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        if (!editable) {
            this.editor = null;
        }

    }

    public void setEditor(PlayerEntity player) {
        this.editor = player;
    }

    public PlayerEntity getEditor() {
        return this.editor;
    }

    public boolean onActivate(ServerPlayerEntity player) {
        Text[] var2 = this.texts;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Text text = var2[var4];
            Style style = text.getStyle();
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                player.getServer().getCommandManager().execute(this.getCommandSource(player), clickEvent.getValue());
            }
        }

        return true;
    }

    public ServerCommandSource getCommandSource(@Nullable ServerPlayerEntity player) {
        String string = player == null ? "Sign" : player.getName().getString();
        Text text = player == null ? new LiteralText("Sign") : player.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(this.pos), Vec2f.ZERO, (ServerWorld)this.world, 2, string, (Text)text, this.world.getServer(), player);
    }

    public DyeColor getTextColor() {
        return this.textColor;
    }

    public boolean setTextColor(DyeColor value) {
        if (value != this.getTextColor()) {
            this.textColor = value;
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            return true;
        } else {
            return false;
        }
    }



}
