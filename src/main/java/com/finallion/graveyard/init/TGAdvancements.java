package com.finallion.graveyard.init;

import com.finallion.graveyard.advancements.TGAdvancementTrigger;
import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.minecraft.util.Identifier;

public class TGAdvancements {

    public static TGAdvancementTrigger KILLED_BY_BONE_DAGGER = CriterionRegistry.register(new TGAdvancementTrigger(new Identifier("graveyard:killed_by_bone_dagger")));

    public static void init() {}
}
