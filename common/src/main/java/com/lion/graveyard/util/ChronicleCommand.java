package com.lion.graveyard.util;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.chronicle.ChronicleState;
import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.item.DiaryPageItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/// `/chronicle find <page> | forget <page> | reset | page <page>` — for testing; pages are found in the world.
public final class ChronicleCommand {
    private ChronicleCommand() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("chronicle")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("find")
                        .then(CommandManager.argument("page", StringArgumentType.word()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String page = StringArgumentType.getString(context, "page");
                            boolean fresh = Chronicle.find(player, page);
                            context.getSource().sendFeedback(() -> Text.literal(page + (fresh ? " found" : " was already found")), false);
                            return 1;
                        })))
                .then(CommandManager.literal("forget")
                        .then(CommandManager.argument("page", StringArgumentType.word()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            String page = StringArgumentType.getString(context, "page");
                            ChronicleState.get(player.server).remove(player.getUuid(), page);
                            Chronicle.syncAll(player);
                            return 1;
                        })))
                .then(CommandManager.literal("reset").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    ChronicleState.get(player.server).clear(player.getUuid());
                    Chronicle.syncAll(player);
                    return 1;
                }))
                .then(CommandManager.literal("page")
                        .then(CommandManager.argument("page", StringArgumentType.word()).executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            player.giveItemStack(DiaryPageItem.of(TGItems.DIARY_PAGE.get(), StringArgumentType.getString(context, "page")));
                            return 1;
                        }))));
    }
}
