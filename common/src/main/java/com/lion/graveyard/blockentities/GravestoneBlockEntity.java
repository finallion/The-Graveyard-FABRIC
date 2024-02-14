package com.lion.graveyard.blockentities;

import com.lion.graveyard.init.TGBlockEntities;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.SignBlockEntity;
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

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        DataResult var10000 = SignText.DIRECT_CODEC.encodeStart(NbtOps.INSTANCE, this.text);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial((error) -> var10001.error("%s", error)).ifPresent((tagx) -> {
            tag.put("front_text", (Tag) tagx);
        });
        tag.putBoolean("is_waxed", this.waxed);
    }

    public void load(CompoundTag nbt) {
        super.load(nbt);
        DataResult<SignText> var10000;
        Logger var10001;
        if (nbt.contains("front_text")) {
            var10000 = SignText.DIRECT_CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("front_text"));
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
            Component text = this.parseLine(signText.getMessage(i, false));
            Component text2 = this.parseLine(signText.getMessage(i, true));
            signText = signText.setMessage(i, text, text2);
        }

        return signText;
    }

    private Component parseLine(Component text) {
        Level var3 = this.level;
        if (var3 instanceof ServerLevel serverLevel) {
            try {
                return ComponentUtils.updateForEntity(createCommandSourceStack((Player)null, serverLevel, this.worldPosition), text, (Entity)null, 0);
            } catch (CommandSyntaxException var4) {
            }
        }

        return text;
    }

    public void updateSignText(Player player, List<FilteredText> filteredText) {
        if (!this.isWaxed() && player.getUUID().equals(this.getPlayerWhoMayEdit()) && this.level != null) {
            this.updateText((signText) -> {
                return this.setMessages(player, filteredText, signText);
            });
            this.setAllowedPlayerEditor((UUID)null);
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        } else {
            LOGGER.warn("Player {} just tried to change non-editable sign", player.getName().getString());
        }
    }

    public boolean updateText(UnaryOperator<SignText> textChanger) {
        SignText signText = this.getText();
        return this.setText((SignText)textChanger.apply(signText));
    }

    private SignText setMessages(Player player, List<FilteredText> filteredText, SignText text) {
        for(int i = 0; i < filteredText.size(); ++i) {
            FilteredText filteredText2 = (FilteredText)filteredText.get(i);
            Style style = text.getMessage(i, player.isTextFilteringEnabled()).getStyle();
            if (player.isTextFilteringEnabled()) {
                text = text.setMessage(i, Component.literal(filteredText2.filteredOrEmpty()).setStyle(style));
            } else {
                text = text.setMessage(i, Component.literal(filteredText2.raw()).setStyle(style), Component.literal(filteredText2.filteredOrEmpty()).setStyle(style));
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
            this.markUpdated();
            return true;
        } else {
            return false;
        }
    }

    public boolean executeClickCommandsIfPresent(Player player, Level level, BlockPos pos) {
        boolean bl = false;
        Component[] var6 = this.getText().getMessages(player.isTextFilteringEnabled());
        int var7 = var6.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            Component component = var6[var8];
            Style style = component.getStyle();
            ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                player.getServer().getCommands().performPrefixedCommand(createCommandSourceStack(player, level, pos), clickEvent.getValue());
                bl = true;
            }
        }

        return bl;
    }

    private static CommandSourceStack createCommandSourceStack(@Nullable Player player, Level level, BlockPos pos) {
        String string = player == null ? "Sign" : player.getName().getString();
        Component component = player == null ? Component.literal("Sign") : player.getDisplayName();
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), Vec2.ZERO, (ServerLevel)level, 2, string, (Component)component, level.getServer(), player);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public void setAllowedPlayerEditor(@Nullable UUID playWhoMayEdit) {
        this.editor = playWhoMayEdit;
    }

    @Nullable
    public UUID getPlayerWhoMayEdit() {
        return this.editor;
    }

    private void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public boolean setWaxed(boolean waxed) {
        if (this.waxed != waxed) {
            this.waxed = waxed;
            this.markUpdated();
            return true;
        } else {
            return false;
        }
    }

    public boolean playerIsTooFarAwayToEdit(UUID uuid) {
        Player player = this.level.getPlayerByUUID(uuid);
        return player == null || player.distanceToSqr((double)this.getBlockPos().getX(), (double)this.getBlockPos().getY(), (double)this.getBlockPos().getZ()) > 64.0;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GravestoneBlockEntity sign) {
        UUID uUID = sign.getPlayerWhoMayEdit();
        if (uUID != null) {
            sign.clearInvalidPlayerWhoMayEdit(sign, level, uUID);
        }

    }

    private void clearInvalidPlayerWhoMayEdit(GravestoneBlockEntity sign, Level level, UUID uuid) {
        if (sign.playerIsTooFarAwayToEdit(uuid)) {
            sign.setAllowedPlayerEditor((UUID)null);
        }

    }

}
