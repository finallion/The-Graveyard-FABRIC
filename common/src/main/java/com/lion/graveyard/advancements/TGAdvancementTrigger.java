package com.lion.graveyard.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;


public class TGAdvancementTrigger extends SimpleCriterionTrigger<TGAdvancementTrigger.Condition> {
    public final ResourceLocation identifier;

    public TGAdvancementTrigger(ResourceLocation identifier) {
        this.identifier = identifier;
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (conditions) -> {
            return true;
        });
    }

    @Override
    public ResourceLocation getId() {
        return identifier;
    }

    @Override
    protected Condition createInstance(JsonObject obj, ContextAwarePredicate playerPredicate, DeserializationContext predicateDeserializer) {
        return new TGAdvancementTrigger.Condition(playerPredicate, identifier);
    }


    public static class Condition extends AbstractCriterionTriggerInstance {

        public Condition(ContextAwarePredicate player, ResourceLocation identifier) {
            super(identifier, player);
        }

        public JsonObject serializeToJson(SerializationContext predicateSerializer) {
            JsonObject jsonObject = super.serializeToJson(predicateSerializer);
            return jsonObject;
        }
    }
}
