package com.lion.graveyard.init;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class TGSounds {

    public static final Supplier<SoundEvent> LICH_SPAWN = register("entity.lich.spawn");
    public static final Supplier<SoundEvent> LICH_MELEE = register("entity.lich.melee");
    public static final Supplier<SoundEvent> LICH_PHASE_02 = register("entity.lich.phase_two");
    public static final Supplier<SoundEvent> LICH_PHASE_03 = register("entity.lich.phase_three");
    public static final Supplier<SoundEvent> LICH_CORPSE_SPELL = register("entity.lich.corpse_spell");
    public static final Supplier<SoundEvent> LICH_CAST_SKULL = register("entity.lich.shoot_skull");
    public static final Supplier<SoundEvent> LICH_CAST_LEVITATION = register("entity.lich.levitation");
    public static final Supplier<SoundEvent> LICH_CAST_TELEPORT = register("entity.lich.heal");
    public static final Supplier<SoundEvent> LICH_DEATH = register("entity.lich.death");
    public static final Supplier<SoundEvent> LICH_HUNT = register("entity.lich.hunt");
    public static final Supplier<SoundEvent> LICH_SCARE = register("entity.lich.scare");
    public static final Supplier<SoundEvent> LICH_PHASE_03_ATTACK = register("entity.lich.phase_three_attack");
    public static final Supplier<SoundEvent> LICH_IDLE = register("entity.lich.idle");
    public static final Supplier<SoundEvent> LICH_HURT = register("entity.lich.hurt");

    public static final Supplier<SoundEvent> NAMELESS_HANGED_BREATH = register("entity.nameless_hanged.breath");
    public static final Supplier<SoundEvent> NAMELESS_HANGED_AMBIENT = register("entity.nameless_hanged.ambient");
    public static final Supplier<SoundEvent> NAMELESS_HANGED_INTERACT = register("entity.nameless_hanged.interact");

    public static final Supplier<SoundEvent> CORRUPTED_ILLAGER_AMBIENT = register("entity.corrupted_illager.ambient");
    public static final Supplier<SoundEvent> CORRUPTED_ILLAGER_HURT = register("entity.corrupted_illager.hurt");
    public static final Supplier<SoundEvent> CORRUPTED_ILLAGER_DEATH = register("entity.corrupted_illager.death");
    public static final Supplier<SoundEvent> CORRUPTED_ILLAGER_STEP = register("entity.corrupted_illager.step");

    public static final Supplier<SoundEvent> ACOLYTE_AMBIENT = register("entity.acolyte.ambient");
    public static final Supplier<SoundEvent> ACOLYTE_HURT = register("entity.acolyte.hurt");
    public static final Supplier<SoundEvent> ACOLYTE_DEATH = register("entity.acolyte.death");

    public static final Supplier<SoundEvent> REAPER_AMBIENT = register("entity.reaper.ambient");
    public static final Supplier<SoundEvent> REAPER_HURT = register("entity.reaper.hurt");
    public static final Supplier<SoundEvent> REAPER_DEATH = register("entity.reaper.death");
    public static final Supplier<SoundEvent> REAPER_CHARGE = register("entity.reaper.charge");

    public static final Supplier<SoundEvent> GHOUL_ROAR = register("entity.ghoul.roar");
    public static final Supplier<SoundEvent> GHOUL_AMBIENT = register("entity.ghoul.ambient");
    public static final Supplier<SoundEvent> GHOUL_HURT = register("entity.ghoul.hurt");
    public static final Supplier<SoundEvent> GHOUL_DEATH = register("entity.ghoul.death");
    public static final Supplier<SoundEvent> GHOUL_STEP = register("entity.ghoul.step");

    public static final Supplier<SoundEvent> GHOULING_GROAN = register("entity.ghouling.groan");
    public static final Supplier<SoundEvent> GHOULING_AMBIENT = register("entity.ghouling.ambient");
    public static final Supplier<SoundEvent> GHOULING_HURT = register("entity.ghouling.hurt");
    public static final Supplier<SoundEvent> GHOULING_DEATH = register("entity.ghouling.death");
    public static final Supplier<SoundEvent> GHOULING_ATTACK = register("entity.ghouling.attack");
    public static final Supplier<SoundEvent> GHOULING_STEP = register("entity.ghouling.step");
    public static final Supplier<SoundEvent> GHOULING_SPAWN = register("entity.ghouling.spawn");

    public static final Supplier<SoundEvent> REVENANT_AMBIENT = register("entity.revenant.ambient");
    public static final Supplier<SoundEvent> REVENANT_HURT = register("entity.revenant.hurt");
    public static final Supplier<SoundEvent> REVENANT_DEATH = register("entity.revenant.death");
    public static final Supplier<SoundEvent> REVENANT_STEP = register("entity.revenant.step");

    public static final Supplier<SoundEvent> NIGHTMARE_AMBIENT = register("entity.nightmare.ambient");
    public static final Supplier<SoundEvent> NIGHTMARE_HURT = register("entity.nightmare.hurt");
    public static final Supplier<SoundEvent> NIGHTMARE_DEATH = register("entity.nightmare.death");

    public static final Supplier<SoundEvent> WRAITH_AMBIENT = register("entity.wraith.ambient");
    public static final Supplier<SoundEvent> WRAITH_HURT = register("entity.wraith.hurt");

    public static final Supplier<SoundEvent> ALTAR_AMBIENT = register("block.altar.ambient");

    public static final Supplier<SoundEvent> COFFIN_OPEN = register("block.coffin.open");
    public static final Supplier<SoundEvent> COFFIN_CLOSE = register("block.coffin.close");

    public static final Supplier<SoundEvent> URN_OPEN = register("block.urn.open");
    public static final Supplier<SoundEvent> URN_CLOSE = register("block.urn.close");

    public static final Supplier<SoundEvent> OSSUARY_OPEN = register("block.ossuary.open");

    public static final Supplier<SoundEvent> SARCOPHAGUS_USE = register("block.sarcophagus.use");

    public static final Supplier<SoundEvent> BONE_PLACED = register("block.bone.placed");
    public static final Supplier<SoundEvent> BONE_AMBIENT = register("block.bone.ambient");


    public static final Supplier<SoundEvent> VIAL_SPLASH = register("item.vial.splash");

    public static final Supplier<SoundEvent> LICH_THEME_01 = register("entity.lich.theme_01");



    public static void init() {

    }

    private static Supplier<SoundEvent> register(String name) {
        SoundEvent soundEvent = SoundEvent.createVariableRangeEvent(new ResourceLocation(Graveyard.MOD_ID, name));

        return RegistryHelper.registerSoundEvent(name, () -> soundEvent);
    }

}
