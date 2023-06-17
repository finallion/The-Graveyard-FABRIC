package com.finallion.graveyard.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TGAdvancementTrigger extends AbstractCriterion<TGAdvancementTrigger.Condition> {
    public final Identifier identifier;

    public TGAdvancementTrigger(Identifier identifier) {
        this.identifier = identifier;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, (conditions) -> {
            return true;
        });
    }

    @Override
    public Identifier getId() {
        return identifier;
    }

    @Override
    protected Condition conditionsFromJson(JsonObject obj, LootContextPredicate playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return new TGAdvancementTrigger.Condition(playerPredicate, identifier);
    }


    public static class Condition extends AbstractCriterionConditions {

        public Condition(LootContextPredicate player, Identifier identifier) {
            super(identifier, player);
        }

        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            return jsonObject;
        }
    }
}
