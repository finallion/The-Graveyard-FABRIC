package com.lion.graveyard.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.lion.graveyard.Graveyard;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.JsonSerializer;

/// Lets the chronicle pages sit in the shipped loot tables and still be switched off: a loot table
/// is data and cannot ask the config on its own, so the question is asked here, per roll.
public class PagesInLootCondition implements LootCondition {

    public static final String CONFIG_KEY = "chroniclePagesInLoot";

    public static final PagesInLootCondition INSTANCE = new PagesInLootCondition();

    /// Assigned once at registration; a condition instance has to be able to name its own type.
    public static LootConditionType TYPE;

    private PagesInLootCondition() {}

    @Override
    public LootConditionType getType() {
        return TYPE;
    }

    @Override
    public boolean test(LootContext context) {
        return Graveyard.getConfig().booleanEntries.getOrDefault(CONFIG_KEY, true);
    }

    /// The condition has no arguments, so both directions are empty.
    public static class Serializer implements JsonSerializer<PagesInLootCondition> {
        @Override
        public void toJson(JsonObject json, PagesInLootCondition condition, JsonSerializationContext context) {
        }

        @Override
        public PagesInLootCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return INSTANCE;
        }
    }
}
