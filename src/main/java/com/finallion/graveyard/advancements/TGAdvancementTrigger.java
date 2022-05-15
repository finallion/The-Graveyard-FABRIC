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
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TGAdvancementTrigger extends AbstractCriterion<TGAdvancementTrigger.Condition> {
    public final Identifier identifier;

    public TGAdvancementTrigger(Identifier identifier) {
        this.identifier = identifier;
    }

    public TGAdvancementTrigger.Condition conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new TGAdvancementTrigger.Condition(extended, identifier);
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


    public static class Condition extends AbstractCriterionConditions {

        public Condition(EntityPredicate.Extended player, Identifier identifier) {
            super(identifier, player);
        }


        public static ConstructBeaconCriterion.Conditions forLevel(NumberRange.IntRange range) {
            return new ConstructBeaconCriterion.Conditions(EntityPredicate.Extended.EMPTY, range);
        }


        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            return jsonObject;
        }
    }
}
