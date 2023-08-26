package com.lion.graveyard.init;

import com.lion.graveyard.advancements.TGAdvancementTrigger;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.util.Identifier;

public class TGAdvancements {

    public static TGAdvancementTrigger KILLED_BY_BONE_DAGGER = Criteria.register(new TGAdvancementTrigger(new Identifier("graveyard:killed_by_bone_dagger")));
    public static TGAdvancementTrigger KILL_WHILE_BLINDED = Criteria.register(new TGAdvancementTrigger(new Identifier("graveyard:kill_while_blinded")));
    public static TGAdvancementTrigger DIM_LIGHT = Criteria.register(new TGAdvancementTrigger(new Identifier("graveyard:dim_light")));
    public static TGAdvancementTrigger KILL_HORDE = Criteria.register(new TGAdvancementTrigger(new Identifier("graveyard:kill_horde")));
    public static TGAdvancementTrigger SPAWN_WRAITH = Criteria.register(new TGAdvancementTrigger(new Identifier("graveyard:spawn_wraith")));
    public static TGAdvancementTrigger EQUIP_COFFIN = Criteria.register(new TGAdvancementTrigger(new Identifier("graveyard:equip_coffin")));

    public static void init() {}
}
