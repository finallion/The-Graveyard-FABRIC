package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGBlockEntities;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class GravestoneBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Nullable
    private UUID editor;
    private SignText text;
    private boolean waxed;

	public GravestoneBlockEntity(BlockPos pos, BlockState state) {
        this(TGBlockEntities.GRAVESTONE_BLOCK_ENTITY.get(), pos, state);
    }

    public GravestoneBlockEntity(BlockEntityType blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.text = this.createText();
    }

    protected SignText createText() {
        return new SignText();
    }

    public SignText getText() {
        return this.text;
    }

    public int getTextLineHeight() {
        return 10;
    }

    public int getMaxTextWidth() {
        return 90;
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        DataResult<NbtElement> var10000 = SignText.CODEC.encodeStart(NbtOps.INSTANCE, this.text);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((frontText) -> {
            nbt.put("front_text", frontText);
        });
        nbt.putBoolean("is_waxed", this.waxed);
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        DataResult<SignText> var10000;
        Logger var10001;
        if (nbt.contains("front_text")) {
            var10000 = SignText.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("front_text"));
            var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var10000.resultOrPartial(var10001::error).ifPresent((signText) -> {
                this.text = this.parseLines(signText);
            });
        }

        this.waxed = nbt.getBoolean("is_waxed");
    }

    private SignText parseLines(SignText signText) {
        for(int i = 0; i < 4; ++i) {
            Text text = this.parseLine(signText.getMessage(i, false));
            Text text2 = this.parseLine(signText.getMessage(i, true));
            signText = signText.withMessage(i, text, text2);
        }

        return signText;
    }

    private Text parseLine(Text text) {
        World var3 = this.world;
        if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;

            try {
                return Texts.parse(createCommandSource((PlayerEntity)null, serverWorld, this.pos), text, (Entity)null, 0);
            } catch (CommandSyntaxException ignored) {
            }
        }

        return text;
    }

    public void tryChangeText(PlayerEntity player, List<FilteredMessage> messages) {
        if (!this.isWaxed() && player.getUuid().equals(this.getEditor()) && this.world != null) {
            this.changeText((text) -> {
                return this.getTextWithMessages(player, messages, text);
            });
            this.setEditor((UUID)null);
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        } else {
            LOGGER.warn("Player {} just tried to change non-editable sign", player.getName().getString());
        }
    }

    public boolean changeText(UnaryOperator<SignText> textChanger) {
        SignText signText = this.getText();
        return this.setText((SignText)textChanger.apply(signText));
    }

    private SignText getTextWithMessages(PlayerEntity player, List<FilteredMessage> messages, SignText text) {
        for(int i = 0; i < messages.size(); ++i) {
            FilteredMessage filteredMessage = (FilteredMessage)messages.get(i);
            Style style = text.getMessage(i, player.shouldFilterText()).getStyle();
            if (player.shouldFilterText()) {
                text = text.withMessage(i, Text.literal(filteredMessage.getString()).setStyle(style));
            } else {
                text = text.withMessage(i, Text.literal(filteredMessage.raw()).setStyle(style), Text.literal(filteredMessage.getString()).setStyle(style));
            }
        }

        return text;
    }

    public boolean setText(SignText text) {
        return this.setFrontText(text);
    }

    private boolean setFrontText(SignText frontText) {
        if (frontText != this.text) {
            this.text = frontText;
            this.updateListeners();
            return true;
        } else {
            return false;
        }
    }

    public boolean runCommandClickEvent(PlayerEntity player, World world, BlockPos pos) {
        boolean bl = false;
        Text[] var6 = this.getText().getMessages(player.shouldFilterText());

        for (Text text : var6) {
            Style style = text.getStyle();
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                player.getServer().getCommandManager().executeWithPrefix(createCommandSource(player, world, pos), clickEvent.getValue());
                bl = true;
            }
        }

        return bl;
    }

    private static ServerCommandSource createCommandSource(@Nullable PlayerEntity player, World world, BlockPos pos) {
        String string = player == null ? "Sign" : player.getName().getString();
        Text text = player == null ? Text.literal("Sign") : player.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ofCenter(pos), Vec2f.ZERO, (ServerWorld)world, 2, string, (Text)text, world.getServer(), player);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    public void setEditor(@Nullable UUID editor) {
        this.editor = editor;
    }

    @Nullable
    public UUID getEditor() {
        return this.editor;
    }

    private void updateListeners() {
        this.markDirty();
        this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public boolean setWaxed(boolean waxed) {
        if (this.waxed != waxed) {
            this.waxed = waxed;
            this.updateListeners();
            return true;
        } else {
            return false;
        }
    }

    public boolean isPlayerTooFarToEdit(UUID uuid) {
        PlayerEntity playerEntity = this.world.getPlayerByUuid(uuid);
        return playerEntity == null || playerEntity.squaredDistanceTo((double)this.getPos().getX(), (double)this.getPos().getY(), (double)this.getPos().getZ()) > 64.0D;
    }

    public static void tick(World world, BlockPos pos, BlockState state, GravestoneBlockEntity blockEntity) {
        UUID uUID = blockEntity.getEditor();
        if (uUID != null) {
            blockEntity.tryClearInvalidEditor(blockEntity, world, uUID);
        }
    }

    private void tryClearInvalidEditor(GravestoneBlockEntity blockEntity, World world, UUID uuid) {
        if (blockEntity.isPlayerTooFarToEdit(uuid)) {
            blockEntity.setEditor((UUID)null);
        }
    }
}
