package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.advancements.TGAdvancementTrigger;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class TGAdvancements {

    public static TGAdvancementTrigger KILLED_BY_BONE_DAGGER = trigger("killed_by_bone_dagger");
    public static TGAdvancementTrigger KILL_WHILE_BLINDED = trigger("kill_while_blinded");
    public static TGAdvancementTrigger DIM_LIGHT = trigger("dim_light");
    public static TGAdvancementTrigger KILL_HORDE = trigger("kill_horde");
    public static TGAdvancementTrigger SPAWN_WRAITH = trigger("spawn_wraith");
    public static TGAdvancementTrigger EQUIP_COFFIN = trigger("equip_coffin");

    public static TGAdvancementTrigger REDEEM_TORMENTED_VILLAGER = trigger("redeem_tormented_villager");
    public static TGAdvancementTrigger TORMENTED_VILLAGER_DAWN = trigger("tormented_villager_dawn");
    public static TGAdvancementTrigger EXTINGUISH_SMOLDERING = trigger("extinguish_smoldering");
    public static TGAdvancementTrigger IGNITE_SMOLDERING = trigger("ignite_smoldering");
    public static TGAdvancementTrigger SCARE_CROWS = trigger("scare_crows");
    public static TGAdvancementTrigger MAGPIE_LOOT = trigger("magpie_loot");
    public static TGAdvancementTrigger RAVEN_ON_GRAVESTONE = trigger("raven_on_gravestone");
    public static TGAdvancementTrigger SCARECROW = trigger("scarecrow");
    public static TGAdvancementTrigger FIRST_CHRONICLE_PAGE = trigger("first_chronicle_page");
    public static TGAdvancementTrigger CHRONICLE_ACT_ONE = trigger("chronicle_act_one");
    public static TGAdvancementTrigger CHRONICLE_ACT_TWO = trigger("chronicle_act_two");
    public static TGAdvancementTrigger PLACE_LORE_BOOK = trigger("place_lore_book");
    public static TGAdvancementTrigger BONE_COLLECTOR = trigger("bone_collector");
    public static TGAdvancementTrigger GHOUL_EMERGES = trigger("ghoul_emerges");

    public static final Identifier REDEEM_TEN_ADVANCEMENT = Graveyard.id("graveyard/redeem_ten_tormented_villagers");
    public static final int REDEEM_TEN_STEPS = 10;

    public static void init() {}

    private static TGAdvancementTrigger trigger(String name) {
        TGAdvancementTrigger trigger = new TGAdvancementTrigger();

        RegistryHelper.registerCriterion(name, trigger);

        return trigger;
    }

    public static void triggerNearby(ServerLevel world, Vec3 center, double radius, TGAdvancementTrigger trigger) {
        double radiusSquared = radius * radius;
        for (ServerPlayer player : world.getPlayers(candidate -> !candidate.isSpectator() && candidate.distanceToSqr(center) <= radiusSquared)) {
            trigger.trigger(player);
        }
    }

    public static void grantNextStep(ServerPlayer player, Identifier advancementId, int steps) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(advancementId);

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
