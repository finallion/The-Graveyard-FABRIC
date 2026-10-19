package com.lion.graveyard.util;

import com.lion.graveyard.chronicle.Chronicle;
import com.lion.graveyard.chronicle.ChronicleState;
import com.lion.graveyard.init.TGItems;
import com.lion.graveyard.item.DiaryPageItem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ChronicleCommand {
    private ChronicleCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chronicle")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("find")
                        .then(Commands.argument("page", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String page = StringArgumentType.getString(context, "page");
                            boolean fresh = Chronicle.find(player, page);
                            context.getSource().sendSuccess(() -> Component.literal(page + (fresh ? " found" : " was already found")), false);
                            return 1;
                        })))
                .then(Commands.literal("forget")
                        .then(Commands.argument("page", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            String page = StringArgumentType.getString(context, "page");
                            ChronicleState.get(player.level().getServer()).remove(player.getUUID(), page);
                            Chronicle.syncAll(player);
                            return 1;
                        })))
                .then(Commands.literal("reset").executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ChronicleState.get(player.level().getServer()).clear(player.getUUID());
                    Chronicle.syncAll(player);
                    return 1;
                }))
                .then(Commands.literal("page")
                        .then(Commands.argument("page", StringArgumentType.word()).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            player.addItem(DiaryPageItem.of(TGItems.DIARY_PAGE.get(), StringArgumentType.getString(context, "page")));
                            return 1;
                        }))));
    }
}
