package com.lion.graveyard.chronicle;

import com.lion.graveyard.init.TGAdvancements;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Chronicle {

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

    /// Unsigned books have no title. Match their first page text instead.
    private static final Map<String, String> UNSIGNED_BOOKS = Map.of(
            "Day 112:", "day_112",
            "Day 132:", "day_132",
            "Day 140:", "day_140",
            "It is done.", "the_last_book");

    /// The server cannot read the chapter assets, so it keeps the ids itself.
    private static final List<String> ACT_ONE = List.of(
            "on_bells", "the_shroud", "the_gravedigger", "the_red_moon", "fog", "the_quiet", "plague_rain",
            "on_the_scarecrow", "letter_unsent_1", "letter_unsent_2", "letter_unsent_3", "roberts_diary_last",
            "notes_64", "the_mycelium_1", "the_mycelium_2", "the_tree", "the_column");

    private static final List<String> ACT_TWO = List.of(
            "the_arrival", "the_first_omen", "the_spreading_1", "the_spreading_2", "the_spreading_3", "day_112",
            "day_115", "day_119", "day_124", "the_bell_ringer", "day_127", "day_132", "tome_of_rituals", "day_136",
            "day_140", "roberts_diary", "the_consequence", "notes_63", "the_last_book");

    private static final java.util.regex.Pattern PAGE_ID = java.util.regex.Pattern.compile("[a-z0-9_]{1,64}");

    private Chronicle() {}

    public static boolean isValidId(@Nullable String page) {
        return page != null && PAGE_ID.matcher(page).matches();
    }

    public static List<String> allPages() {
        List<String> pages = new ArrayList<>(ACT_ONE);
        pages.addAll(ACT_TWO);
        return pages;
    }

    public static boolean find(ServerPlayerEntity player, String page) {
        if (!isValidId(page)) return false;
        ChronicleState state = ChronicleState.get(player.server);
        boolean added = state.add(player.getUuid(), page);
        ChronicleSync.send(player, state.found(player.getUuid()), added ? page : null);
        if (added) {
            player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.0f);
            TGAdvancements.FIRST_CHRONICLE_PAGE.trigger(player);
            Set<String> pages = state.found(player.getUuid());
            if (pages.containsAll(ACT_ONE)) {
                TGAdvancements.CHRONICLE_ACT_ONE.trigger(player);
            }
            if (pages.containsAll(ACT_TWO)) {
                TGAdvancements.CHRONICLE_ACT_TWO.trigger(player);
            }
        }
        return added;
    }

    public static void syncAll(ServerPlayerEntity player) {
        ChronicleSync.send(player, ChronicleState.get(player.server).found(player.getUuid()), null);
    }

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
