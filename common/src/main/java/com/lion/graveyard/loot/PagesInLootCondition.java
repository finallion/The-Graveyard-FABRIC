package com.lion.graveyard.loot;

import com.lion.graveyard.Graveyard;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/// Lets the chronicle pages sit in the shipped loot tables and still be switched off: a loot table
/// is data and cannot ask the config on its own, so the question is asked here, per roll.
public class PagesInLootCondition implements LootItemCondition {

    public static final String CONFIG_KEY = "chroniclePagesInLoot";

    public static final PagesInLootCondition INSTANCE = new PagesInLootCondition();

    public static LootItemConditionType TYPE;

    private PagesInLootCondition() {}

    @Override
    public LootItemConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext context) {
        return Graveyard.getConfig().booleanEntries.getOrDefault(CONFIG_KEY, true);
    }
}
