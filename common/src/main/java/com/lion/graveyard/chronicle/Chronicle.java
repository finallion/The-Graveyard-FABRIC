package com.lion.graveyard.chronicle;

import com.lion.graveyard.platform.ChronicleSync;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// Server-side entry points: a page gets found, the player gets told.
public final class Chronicle {

    /// The books that already lie in the structures, keyed by their signed title.
    private static final Map<String, String> SIGNED_BOOKS = Map.ofEntries(
            Map.entry("The Arrival", "the_arrival"),
            Map.entry("The First Omen", "the_first_omen"),
            Map.entry("The Spreading I", "the_spreading_1"),
            Map.entry("The Spreading 2", "the_spreading_2"),
            Map.entry("The Spreading 3", "the_spreading_3"),
            Map.entry("Tome of Rituals", "tome_of_rituals"),
            Map.entry("The Consequence", "the_consequence"),
            Map.entry("Roberts Diary", "roberts_diary"),
            Map.entry("Notes Nr. 63", "notes_63"));

    /// The unsigned diary leaves have no title; their first page starts with one of these.
    private static final Map<String, String> UNSIGNED_BOOKS = Map.of(
            "Day 112:", "day_112",
            "Day 132:", "day_132",
            "Day 140:", "day_140",
            "It is done.", "the_last_book");

    private Chronicle() {}

    /// Returns true if the page was new for this player.
    public static boolean find(ServerPlayerEntity player, String page) {
        ChronicleState state = ChronicleState.get(player.server);
        boolean added = state.add(player.getUuid(), page);
        ChronicleSync.send(player, state.found(player.getUuid()), added ? page : null);
        if (added) {
            player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
        return added;
    }

    public static void syncAll(ServerPlayerEntity player) {
        ChronicleSync.send(player, ChronicleState.get(player.server).found(player.getUuid()), null);
    }

    /// Which chronicle page a vanilla book stands for, or null if it is just a book.
    @Nullable
    public static String pageOf(ItemStack book) {
        NbtCompound nbt = book.getNbt();
        if (nbt == null) return null;
        if (book.isOf(Items.WRITTEN_BOOK)) {
            return SIGNED_BOOKS.get(nbt.getString("title"));
        }
        if (book.isOf(Items.WRITABLE_BOOK)) {
            NbtList pages = nbt.getList("pages", NbtElement.STRING_TYPE);
            if (pages.isEmpty()) return null;
            String first = pages.getString(0);
            for (Map.Entry<String, String> entry : UNSIGNED_BOOKS.entrySet()) {
                if (first.startsWith(entry.getKey())) return entry.getValue();
            }
        }
        return null;
    }
}
