package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.advancements.TGAdvancementTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class TGAdvancements {

    public static TGAdvancementTrigger KILLED_BY_BONE_DAGGER = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("killed_by_bone_dagger")));
    public static TGAdvancementTrigger KILL_WHILE_BLINDED = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("kill_while_blinded")));
    public static TGAdvancementTrigger DIM_LIGHT = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("dim_light")));
    public static TGAdvancementTrigger KILL_HORDE = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("kill_horde")));
    public static TGAdvancementTrigger SPAWN_WRAITH = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("spawn_wraith")));
    public static TGAdvancementTrigger EQUIP_COFFIN = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("equip_coffin")));

    public static TGAdvancementTrigger REDEEM_TORMENTED_VILLAGER = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("redeem_tormented_villager")));
    public static TGAdvancementTrigger TORMENTED_VILLAGER_DAWN = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("tormented_villager_dawn")));
    public static TGAdvancementTrigger EXTINGUISH_SMOLDERING = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("extinguish_smoldering")));
    public static TGAdvancementTrigger IGNITE_SMOLDERING = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("ignite_smoldering")));
    public static TGAdvancementTrigger SCARE_CROWS = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("scare_crows")));
    public static TGAdvancementTrigger MAGPIE_LOOT = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("magpie_loot")));
    public static TGAdvancementTrigger RAVEN_ON_GRAVESTONE = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("raven_on_gravestone")));
    public static TGAdvancementTrigger SCARECROW = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("scarecrow")));
    public static TGAdvancementTrigger FIRST_CHRONICLE_PAGE = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("first_chronicle_page")));
    public static TGAdvancementTrigger CHRONICLE_ACT_ONE = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("chronicle_act_one")));
    public static TGAdvancementTrigger CHRONICLE_ACT_TWO = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("chronicle_act_two")));
    public static TGAdvancementTrigger PLACE_LORE_BOOK = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("place_lore_book")));
    public static TGAdvancementTrigger BONE_COLLECTOR = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("bone_collector")));
    public static TGAdvancementTrigger GHOUL_EMERGES = CriteriaTriggers.register(new TGAdvancementTrigger(Graveyard.id("ghoul_emerges")));

    public static final ResourceLocation REDEEM_TEN_ADVANCEMENT = Graveyard.id("graveyard/redeem_ten_tormented_villagers");
    public static final int REDEEM_TEN_STEPS = 10;

    public static void init() {}

    public static void triggerNearby(ServerLevel world, Vec3 center, double radius, TGAdvancementTrigger trigger) {
        double radiusSquared = radius * radius;
        for (ServerPlayer player : world.getPlayers(candidate -> !candidate.isSpectator() && candidate.distanceToSqr(center) <= radiusSquared)) {
            trigger.trigger(player);
        }
    }

    public static void grantNextStep(ServerPlayer player, ResourceLocation advancementId, int steps) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(advancementId);

        if (advancement == null) {
            return;
        }

        var progress = player.getAdvancements().getOrStartProgress(advancement);
        for (int step = 1; step <= steps; step++) {
            String criterion = Integer.toString(step);
            var criterionProgress = progress.getCriterion(criterion);

            if (criterionProgress != null && !criterionProgress.isDone()) {
                player.getAdvancements().award(advancement, criterion);
                return;
            }
        }
    }
}
