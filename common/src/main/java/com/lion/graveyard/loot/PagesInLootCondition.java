package com.lion.graveyard.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
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

    /// The condition has no arguments, so both directions are empty.
    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<PagesInLootCondition> {
        @Override
        public void serialize(JsonObject json, PagesInLootCondition condition, JsonSerializationContext context) {
        }

        @Override
        public PagesInLootCondition deserialize(JsonObject json, JsonDeserializationContext context) {
            return INSTANCE;
        }
    }
}
