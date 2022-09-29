package com.finallion.graveyard.init;

import com.finallion.graveyard.advancements.TGAdvancementTrigger;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.minecraft.util.Identifier;

public class TGAdvancements {

    public static TGAdvancementTrigger KILLED_BY_BONE_DAGGER = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:killed_by_bone_dagger")));
    public static TGAdvancementTrigger KILL_WHILE_BLINDED = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:kill_while_blinded")));
    public static TGAdvancementTrigger DIM_LIGHT = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:dim_light")));
    public static TGAdvancementTrigger KILL_HORDE = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:kill_horde")));
    public static TGAdvancementTrigger SPAWN_WRAITH = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:spawn_wraith")));
    public static TGAdvancementTrigger EQUIP_COFFIN = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:equip_coffin")));

    public static void init() {}
}
