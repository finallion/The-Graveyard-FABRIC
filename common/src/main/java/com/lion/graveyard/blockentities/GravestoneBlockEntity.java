package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGBlockEntities;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.FilteredText;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
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

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        DataResult<Tag> encodedText = SignText.DIRECT_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), this.text);
        Logger errorLogger = LOGGER;
        Objects.requireNonNull(errorLogger);
        encodedText.resultOrPartial(errorLogger::error).ifPresent((frontText) -> {
            nbt.put("front_text", frontText);
        });
        nbt.putBoolean("is_waxed", this.waxed);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        DataResult<SignText> parsedText;
        Logger errorLogger;

        if (nbt.contains("front_text")) {
            parsedText = SignText.DIRECT_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), nbt.getCompound("front_text"));
            errorLogger = LOGGER;
            Objects.requireNonNull(errorLogger);
            parsedText.resultOrPartial(errorLogger::error).ifPresent((signText) -> {
                this.text = this.parseLines(signText);
            });
        }

        this.waxed = nbt.getBoolean("is_waxed");
    }

    private SignText parseLines(SignText signText) {
        for(int i = 0; i < 4; ++i) {
            Component text = this.parseLine(signText.getMessage(i, false));
            Component text2 = this.parseLine(signText.getMessage(i, true));
            signText = signText.setMessage(i, text, text2);
        }

        return signText;
    }

    private Component parseLine(Component text) {
        Level world = this.level;

        if (world instanceof ServerLevel) {
            ServerLevel serverWorld = (ServerLevel)world;

            try {
                return ComponentUtils.updateForEntity(createCommandSource((Player)null, serverWorld, this.worldPosition), text, (Entity)null, 0);
            } catch (CommandSyntaxException exception) {
                LOGGER.debug("Cannot resolve the sign text {}", text.getString(), exception);
            }
        }

        return text;
    }

    public void tryChangeText(Player player, List<FilteredText> messages) {
        if (!this.isWaxed() && player.getUUID().equals(this.getEditor()) && this.level != null && !this.isPlayerTooFarToEdit(player.getUUID())) {
            this.changeText((text) -> {
                return this.getTextWithMessages(player, messages, text);
            });
            this.setEditor((UUID)null);
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        } else {
            LOGGER.warn("Player {} just tried to change non-editable sign", player.getName().getString());
        }
    }

    public boolean changeText(UnaryOperator<SignText> textChanger) {
        SignText signText = this.getText();
        return this.setText((SignText)textChanger.apply(signText));
    }

    private SignText getTextWithMessages(Player player, List<FilteredText> messages, SignText text) {
        for(int i = 0; i < messages.size(); ++i) {
            FilteredText filteredMessage = (FilteredText)messages.get(i);
            Style style = text.getMessage(i, player.isTextFilteringEnabled()).getStyle();

            if (player.isTextFilteringEnabled()) {
                text = text.setMessage(i, Component.literal(filteredMessage.filteredOrEmpty()).setStyle(style));
            } else {
                text = text.setMessage(
                        i,
                        Component.literal(filteredMessage.raw()).setStyle(style),
                        Component.literal(filteredMessage.filteredOrEmpty()).setStyle(style)
                );
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

    public boolean runCommandClickEvent(Player player, Level world, BlockPos pos) {
        boolean ranCommand = false;
        Component[] lines = this.getText().getMessages(player.isTextFilteringEnabled());

        for (Component text : lines) {
            Style style = text.getStyle();
            ClickEvent clickEvent = style.getClickEvent();

            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                player.getServer().getCommands().performPrefixedCommand(createCommandSource(player, world, pos), clickEvent.getValue());
                ranCommand = true;
            }
        }

        return ranCommand;
    }

    private static CommandSourceStack createCommandSource(@Nullable Player player, Level world, BlockPos pos) {
        String string = player == null ? "Sign" : player.getName().getString();
        Component text = player == null ? Component.literal("Sign") : player.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, (ServerLevel)world, 2, string, (Component)text, world.getServer(), player);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public boolean onlyOpCanSetNbt() {
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
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
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
        Player playerEntity = this.level.getPlayerByUUID(uuid);
        return playerEntity == null || playerEntity.distanceToSqr((double)this.getBlockPos().getX(), (double)this.getBlockPos().getY(), (double)this.getBlockPos().getZ()) > 64.0D;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, GravestoneBlockEntity blockEntity) {
        UUID editorId = blockEntity.getEditor();

        if (editorId != null) {
            blockEntity.tryClearInvalidEditor(blockEntity, world, editorId);
        }
    }

    private void tryClearInvalidEditor(GravestoneBlockEntity blockEntity, Level world, UUID uuid) {
        if (blockEntity.isPlayerTooFarToEdit(uuid)) {
            blockEntity.setEditor((UUID)null);
        }
    }
}
