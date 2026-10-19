package com.lion.graveyard.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class TGAdvancementTrigger extends SimpleCriterionTrigger<TGAdvancementTrigger.Condition> {

    public void trigger(ServerPlayer player) {
        this.trigger(player, (conditions) -> {
            return true;
        });
    }

    @Override
    public Codec<Condition> codec() {
        return Condition.CODEC;
    }

    public record Condition(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Condition::player)
        ).apply(instance, Condition::new));
    }
}
