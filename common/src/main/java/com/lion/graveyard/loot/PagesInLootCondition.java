package com.lion.graveyard.loot;

import com.lion.graveyard.Graveyard;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/// Lets the chronicle pages sit in the shipped loot tables and still be switched off: a loot table
/// is data and cannot ask the config on its own, so the question is asked here, per roll.
public class PagesInLootCondition implements LootItemCondition {

    public static final String CONFIG_KEY = "chroniclePagesInLoot";

    public static final PagesInLootCondition INSTANCE = new PagesInLootCondition();

    public static final MapCodec<PagesInLootCondition> MAP_CODEC = MapCodec.unit(INSTANCE);

    private PagesInLootCondition() {}

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return MAP_CODEC;
    }

    @Override
    public boolean test(LootContext context) {
        return Graveyard.getConfig().booleanEntries.getOrDefault(CONFIG_KEY, true);
    }
}
