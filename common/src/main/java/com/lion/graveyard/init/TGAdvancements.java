package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.advancements.TGAdvancementTrigger;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class TGAdvancements {

    public static TGAdvancementTrigger KILLED_BY_BONE_DAGGER = Criteria.register(new TGAdvancementTrigger(Graveyard.id("killed_by_bone_dagger")));
    public static TGAdvancementTrigger KILL_WHILE_BLINDED = Criteria.register(new TGAdvancementTrigger(Graveyard.id("kill_while_blinded")));
    public static TGAdvancementTrigger DIM_LIGHT = Criteria.register(new TGAdvancementTrigger(Graveyard.id("dim_light")));
    public static TGAdvancementTrigger KILL_HORDE = Criteria.register(new TGAdvancementTrigger(Graveyard.id("kill_horde")));
    public static TGAdvancementTrigger SPAWN_WRAITH = Criteria.register(new TGAdvancementTrigger(Graveyard.id("spawn_wraith")));
    public static TGAdvancementTrigger EQUIP_COFFIN = Criteria.register(new TGAdvancementTrigger(Graveyard.id("equip_coffin")));

    public static TGAdvancementTrigger REDEEM_TORMENTED_VILLAGER = Criteria.register(new TGAdvancementTrigger(Graveyard.id("redeem_tormented_villager")));
    public static TGAdvancementTrigger TORMENTED_VILLAGER_DAWN = Criteria.register(new TGAdvancementTrigger(Graveyard.id("tormented_villager_dawn")));
    public static TGAdvancementTrigger EXTINGUISH_SMOLDERING = Criteria.register(new TGAdvancementTrigger(Graveyard.id("extinguish_smoldering")));
    public static TGAdvancementTrigger IGNITE_SMOLDERING = Criteria.register(new TGAdvancementTrigger(Graveyard.id("ignite_smoldering")));
    public static TGAdvancementTrigger SCARE_CROWS = Criteria.register(new TGAdvancementTrigger(Graveyard.id("scare_crows")));
    public static TGAdvancementTrigger MAGPIE_LOOT = Criteria.register(new TGAdvancementTrigger(Graveyard.id("magpie_loot")));
    public static TGAdvancementTrigger RAVEN_ON_GRAVESTONE = Criteria.register(new TGAdvancementTrigger(Graveyard.id("raven_on_gravestone")));
    public static TGAdvancementTrigger SCARECROW = Criteria.register(new TGAdvancementTrigger(Graveyard.id("scarecrow")));
    public static TGAdvancementTrigger FIRST_CHRONICLE_PAGE = Criteria.register(new TGAdvancementTrigger(Graveyard.id("first_chronicle_page")));
    public static TGAdvancementTrigger CHRONICLE_ACT_ONE = Criteria.register(new TGAdvancementTrigger(Graveyard.id("chronicle_act_one")));
    public static TGAdvancementTrigger CHRONICLE_ACT_TWO = Criteria.register(new TGAdvancementTrigger(Graveyard.id("chronicle_act_two")));
    public static TGAdvancementTrigger PLACE_LORE_BOOK = Criteria.register(new TGAdvancementTrigger(Graveyard.id("place_lore_book")));
    public static TGAdvancementTrigger BONE_COLLECTOR = Criteria.register(new TGAdvancementTrigger(Graveyard.id("bone_collector")));
    public static TGAdvancementTrigger GHOUL_EMERGES = Criteria.register(new TGAdvancementTrigger(Graveyard.id("ghoul_emerges")));

    public static final Identifier REDEEM_TEN_ADVANCEMENT = Graveyard.id("graveyard/redeem_ten_tormented_villagers");
    public static final int REDEEM_TEN_STEPS = 10;

    public static void init() {}

    public static void triggerNearby(ServerWorld world, Vec3d center, double radius, TGAdvancementTrigger trigger) {
        double radiusSquared = radius * radius;
        for (ServerPlayerEntity player : world.getPlayers(candidate -> !candidate.isSpectator() && candidate.squaredDistanceTo(center) <= radiusSquared)) {
            trigger.trigger(player);
        }
    }

    public static void grantNextStep(ServerPlayerEntity player, Identifier advancementId, int steps) {
        Advancement advancement = player.server.getAdvancementLoader().get(advancementId);

        if (advancement == null) {
            return;
        }

        var progress = player.getAdvancementTracker().getProgress(advancement);
        for (int step = 1; step <= steps; step++) {
            String criterion = Integer.toString(step);
            var criterionProgress = progress.getCriterionProgress(criterion);

            if (criterionProgress != null && !criterionProgress.isObtained()) {
                player.getAdvancementTracker().grantCriterion(advancement, criterion);
                return;
            }
        }
    }
}
