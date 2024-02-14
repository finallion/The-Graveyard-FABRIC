package com.lion.graveyard.init;


import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.advancements.critereon.PlayerTrigger;

import java.util.function.Supplier;

public class TGCriteria {

    public static Supplier<PlayerTrigger> KILLED_BY_BONE_DAGGER;
    public static Supplier<PlayerTrigger> KILL_WHILE_BLINDED;
    public static Supplier<PlayerTrigger> DIM_LIGHT;
    public static Supplier<PlayerTrigger> KILL_HORDE;
    public static Supplier<PlayerTrigger> SPAWN_WRAITH;
    public static Supplier<PlayerTrigger> EQUIP_COFFIN;



    static {
        KILLED_BY_BONE_DAGGER = RegistryHelper.registerCriterion("killed_by_bone_dagger", new PlayerTrigger());
        KILL_WHILE_BLINDED = RegistryHelper.registerCriterion("kill_while_blinded", new PlayerTrigger());
        DIM_LIGHT = RegistryHelper.registerCriterion("dim_light", new PlayerTrigger());
        KILL_HORDE = RegistryHelper.registerCriterion("kill_horde", new PlayerTrigger());
        SPAWN_WRAITH = RegistryHelper.registerCriterion("spawn_wraith", new PlayerTrigger());
        EQUIP_COFFIN = RegistryHelper.registerCriterion("equip_coffin", new PlayerTrigger());

    }

    public static void init() {
    }

}
