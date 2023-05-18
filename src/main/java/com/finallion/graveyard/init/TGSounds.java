package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TGSounds {
    public static final Identifier NAMELESS_HANGED_BREATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nameless_hanged.breath");
    public static SoundEvent NAMELESS_HANGED_BREATH = new SoundEvent(NAMELESS_HANGED_BREATH_ID);
    public static final Identifier NAMELESS_HANGED_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nameless_hanged.ambient");
    public static SoundEvent NAMELESS_HANGED_AMBIENT = new SoundEvent(NAMELESS_HANGED_AMBIENT_ID);
    public static final Identifier NAMELESS_HANGED_INTERACT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nameless_hanged.interact");
    public static SoundEvent NAMELESS_HANGED_INTERACT = new SoundEvent(NAMELESS_HANGED_INTERACT_ID);

    public static final Identifier CORRUPTED_ILLAGER_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.ambient");
    public static SoundEvent CORRUPTED_ILLAGER_AMBIENT = new SoundEvent(CORRUPTED_ILLAGER_AMBIENT_ID);
    public static final Identifier CORRUPTED_ILLAGER_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.hurt");
    public static SoundEvent CORRUPTED_ILLAGER_HURT = new SoundEvent(CORRUPTED_ILLAGER_HURT_ID);
    public static final Identifier CORRUPTED_ILLAGER_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.death");
    public static SoundEvent CORRUPTED_ILLAGER_DEATH = new SoundEvent(CORRUPTED_ILLAGER_DEATH_ID);
    public static final Identifier CORRUPTED_ILLAGER_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.step");
    public static SoundEvent CORRUPTED_ILLAGER_STEP = new SoundEvent(CORRUPTED_ILLAGER_STEP_ID);

    public static final Identifier ACOLYTE_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.acolyte.ambient");
    public static SoundEvent ACOLYTE_AMBIENT = new SoundEvent(ACOLYTE_AMBIENT_ID);
    public static final Identifier ACOLYTE_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.acolyte.hurt");
    public static SoundEvent ACOLYTE_HURT = new SoundEvent(ACOLYTE_HURT_ID);
    public static final Identifier ACOLYTE_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.acolyte.death");
    public static SoundEvent ACOLYTE_DEATH = new SoundEvent(ACOLYTE_DEATH_ID);

    public static final Identifier REAPER_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.reaper.ambient");
    public static SoundEvent REAPER_AMBIENT = new SoundEvent(REAPER_AMBIENT_ID);
    public static final Identifier REAPER_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.reaper.hurt");
    public static SoundEvent REAPER_HURT = new SoundEvent(REAPER_HURT_ID);
    public static final Identifier REAPER_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.reaper.death");
    public static SoundEvent REAPER_DEATH = new SoundEvent(REAPER_DEATH_ID);
    public static final Identifier REAPER_CHARGE_ID = new Identifier(TheGraveyard.MOD_ID, "entity.reaper.charge");
    public static SoundEvent REAPER_CHARGE = new SoundEvent(REAPER_CHARGE_ID);

    public static final Identifier GHOUL_ROAR_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.roar");
    public static SoundEvent GHOUL_ROAR = new SoundEvent(GHOUL_ROAR_ID);
    public static final Identifier GHOUL_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.ambient");
    public static SoundEvent GHOUL_AMBIENT = new SoundEvent(GHOUL_AMBIENT_ID);
    public static final Identifier GHOUL_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.hurt");
    public static SoundEvent GHOUL_HURT = new SoundEvent(GHOUL_HURT_ID);
    public static final Identifier GHOUL_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.death");
    public static SoundEvent GHOUL_DEATH = new SoundEvent(GHOUL_DEATH_ID);
    public static final Identifier GHOUL_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.step");
    public static SoundEvent GHOUL_STEP = new SoundEvent(GHOUL_STEP_ID);

    public static final Identifier GHOULING_GROAN_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.groan");
    public static SoundEvent GHOULING_GROAN = new SoundEvent(GHOULING_GROAN_ID);
    public static final Identifier GHOULING_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.ambient");
    public static SoundEvent GHOULING_AMBIENT = new SoundEvent(GHOULING_AMBIENT_ID);
    public static final Identifier GHOULING_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.hurt");
    public static SoundEvent GHOULING_HURT = new SoundEvent(GHOULING_HURT_ID);
    public static final Identifier GHOULING_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.death");
    public static SoundEvent GHOULING_DEATH = new SoundEvent(GHOULING_DEATH_ID);
    public static final Identifier GHOULING_ATTACK_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.attack");
    public static SoundEvent GHOULING_ATTACK = new SoundEvent(GHOULING_ATTACK_ID);
    public static final Identifier GHOULING_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.step");
    public static SoundEvent GHOULING_STEP = new SoundEvent(GHOULING_STEP_ID);
    public static final Identifier GHOULING_SPAWN_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.spawn");
    public static SoundEvent GHOULING_SPAWN = new SoundEvent(GHOULING_SPAWN_ID);

    public static final Identifier REVENANT_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.ambient");
    public static SoundEvent REVENANT_AMBIENT = new SoundEvent(REVENANT_AMBIENT_ID);
    public static final Identifier REVENANT_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.hurt");
    public static SoundEvent REVENANT_HURT = new SoundEvent(REVENANT_HURT_ID);
    public static final Identifier REVENANT_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.death");
    public static SoundEvent REVENANT_DEATH = new SoundEvent(REVENANT_DEATH_ID);
    public static final Identifier REVENANT_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.step");
    public static SoundEvent REVENANT_STEP = new SoundEvent(REVENANT_STEP_ID);

    public static final Identifier NIGHTMARE_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nightmare.ambient");
    public static SoundEvent NIGHTMARE_AMBIENT = new SoundEvent(NIGHTMARE_AMBIENT_ID);
    public static final Identifier NIGHTMARE_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nightmare.hurt");
    public static SoundEvent NIGHTMARE_HURT = new SoundEvent(NIGHTMARE_HURT_ID);
    public static final Identifier NIGHTMARE_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nightmare.death");
    public static SoundEvent NIGHTMARE_DEATH = new SoundEvent(NIGHTMARE_DEATH_ID);

    public static final Identifier WRAITH_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.wraith.ambient");
    public static SoundEvent WRAITH_AMBIENT = new SoundEvent(WRAITH_AMBIENT_ID);
    public static final Identifier WRAITH_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.wraith.hurt");
    public static SoundEvent WRAITH_HURT = new SoundEvent(WRAITH_HURT_ID);

    public static final Identifier ALTAR_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "block.altar.ambient");
    public static SoundEvent ALTAR_AMBIENT = new SoundEvent(ALTAR_AMBIENT_ID);

    public static final Identifier COFFIN_OPEN_ID = new Identifier(TheGraveyard.MOD_ID, "block.coffin.open");
    public static SoundEvent COFFIN_OPEN = new SoundEvent(COFFIN_OPEN_ID);
    public static final Identifier COFFIN_CLOSE_ID = new Identifier(TheGraveyard.MOD_ID, "block.coffin.close");
    public static SoundEvent COFFIN_CLOSE = new SoundEvent(COFFIN_CLOSE_ID);

    public static final Identifier URN_OPEN_ID = new Identifier(TheGraveyard.MOD_ID, "block.urn.open");
    public static SoundEvent URN_OPEN = new SoundEvent(URN_OPEN_ID);
    public static final Identifier URN_CLOSE_ID = new Identifier(TheGraveyard.MOD_ID, "block.urn.close");
    public static SoundEvent URN_CLOSE = new SoundEvent(URN_CLOSE_ID);

    public static final Identifier SARCOPHAGUS_USE_ID = new Identifier(TheGraveyard.MOD_ID, "block.sarcophagus.use");
    public static SoundEvent SARCOPHAGUS_USE = new SoundEvent(SARCOPHAGUS_USE_ID);

    public static final Identifier OSSUARY_OPEN_ID = new Identifier(TheGraveyard.MOD_ID, "block.ossuary.open");
    public static SoundEvent OSSUARY_OPEN = new SoundEvent(OSSUARY_OPEN_ID);

    public static final Identifier BONE_PLACED_ID = new Identifier(TheGraveyard.MOD_ID, "block.bone.placed");
    public static final Identifier BONE_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "block.bone.ambient");
    public static SoundEvent BONE_PLACED = new SoundEvent(BONE_PLACED_ID);
    public static SoundEvent BONE_AMBIENT = new SoundEvent(BONE_AMBIENT_ID);

    public static final Identifier VIAL_SPLASH_ID = new Identifier(TheGraveyard.MOD_ID, "item.vial.splash");
    public static SoundEvent VIAL_SPLASH = new SoundEvent(VIAL_SPLASH_ID);

    public static final Identifier LICH_SPAWN_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.spawn");
    public static final Identifier LICH_MELEE_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.melee");
    public static final Identifier LICH_PHASE_02_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.phase_two");
    public static final Identifier LICH_PHASE_03_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.phase_three");
    public static final Identifier LICH_CORPSE_SPELL_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.corpse_spell");
    public static final Identifier LICH_CAST_SKULL_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.shoot_skull");
    public static final Identifier LICH_CAST_LEVITATION_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.levitation");
    public static final Identifier LICH_CAST_TELEPORT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.heal");
    public static final Identifier LICH_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.death");
    public static final Identifier LICH_HUNT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.hunt");
    public static final Identifier LICH_SCARE_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.scare");
    public static final Identifier LICH_PHASE_03_ATTACK_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.phase_three_attack");
    public static final Identifier LICH_IDLE_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.idle");
    public static final Identifier LICH_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.hurt");
    public static SoundEvent LICH_SPAWN = new SoundEvent(LICH_SPAWN_ID);
    public static SoundEvent LICH_MELEE = new SoundEvent(LICH_MELEE_ID);
    public static SoundEvent LICH_PHASE_02 = new SoundEvent(LICH_PHASE_02_ID);
    public static SoundEvent LICH_PHASE_03 = new SoundEvent(LICH_PHASE_03_ID);
    public static SoundEvent LICH_CORPSE_SPELL = new SoundEvent(LICH_CORPSE_SPELL_ID);
    public static SoundEvent LICH_CAST_SKULL = new SoundEvent(LICH_CAST_SKULL_ID);
    public static SoundEvent LICH_DEATH = new SoundEvent(LICH_DEATH_ID);
    public static SoundEvent LICH_HUNT = new SoundEvent(LICH_HUNT_ID);
    public static SoundEvent LICH_SCARE = new SoundEvent(LICH_SCARE_ID);
    public static SoundEvent LICH_IDLE = new SoundEvent(LICH_IDLE_ID);
    public static SoundEvent LICH_HURT = new SoundEvent(LICH_HURT_ID);
    public static SoundEvent LICH_CAST_LEVITATION = new SoundEvent(LICH_CAST_LEVITATION_ID);
    public static SoundEvent LICH_CAST_TELEPORT = new SoundEvent(LICH_CAST_TELEPORT_ID);
    public static SoundEvent LICH_PHASE_03_ATTACK = new SoundEvent(LICH_PHASE_03_ATTACK_ID);

    public static final SoundEvent LICH_THEME_01 = new SoundEvent(new Identifier(TheGraveyard.MOD_ID, "entity.lich.theme_01"));

    // better method to register, todo!
    public void register() {
        registerSoundEvent(LICH_THEME_01);
    }

    private static void registerSoundEvent(SoundEvent soundEvent) {
        Registry.register(Registry.SOUND_EVENT, soundEvent.getId(), soundEvent);
    }


    public static void init() {
        registerSoundEvent(LICH_THEME_01);

        Registry.register(Registry.SOUND_EVENT, NAMELESS_HANGED_BREATH_ID, NAMELESS_HANGED_BREATH);
        Registry.register(Registry.SOUND_EVENT, NAMELESS_HANGED_INTERACT_ID, NAMELESS_HANGED_INTERACT);
        Registry.register(Registry.SOUND_EVENT, NAMELESS_HANGED_AMBIENT_ID, NAMELESS_HANGED_AMBIENT);

        Registry.register(Registry.SOUND_EVENT, GHOUL_ROAR_ID, GHOUL_ROAR);
        Registry.register(Registry.SOUND_EVENT, GHOUL_AMBIENT_ID, GHOUL_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, GHOUL_HURT_ID, GHOUL_HURT);
        Registry.register(Registry.SOUND_EVENT, GHOUL_DEATH_ID, GHOUL_DEATH);
        Registry.register(Registry.SOUND_EVENT, GHOUL_STEP_ID, GHOUL_STEP);

        Registry.register(Registry.SOUND_EVENT, GHOULING_GROAN_ID, GHOULING_GROAN);
        Registry.register(Registry.SOUND_EVENT, GHOULING_SPAWN_ID, GHOULING_SPAWN);
        Registry.register(Registry.SOUND_EVENT, GHOULING_AMBIENT_ID, GHOULING_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, GHOULING_HURT_ID, GHOULING_HURT);
        Registry.register(Registry.SOUND_EVENT, GHOULING_ATTACK_ID, GHOULING_ATTACK);
        Registry.register(Registry.SOUND_EVENT, GHOULING_DEATH_ID, GHOULING_DEATH);
        Registry.register(Registry.SOUND_EVENT, GHOULING_STEP_ID, GHOULING_STEP);

        Registry.register(Registry.SOUND_EVENT, REVENANT_AMBIENT_ID, REVENANT_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, REVENANT_HURT_ID, REVENANT_HURT);
        Registry.register(Registry.SOUND_EVENT, REVENANT_DEATH_ID, REVENANT_DEATH);
        Registry.register(Registry.SOUND_EVENT, REVENANT_STEP_ID, REVENANT_STEP);

        Registry.register(Registry.SOUND_EVENT, NIGHTMARE_AMBIENT_ID, NIGHTMARE_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, NIGHTMARE_HURT_ID, NIGHTMARE_HURT);
        Registry.register(Registry.SOUND_EVENT, NIGHTMARE_DEATH_ID, NIGHTMARE_DEATH);

        Registry.register(Registry.SOUND_EVENT, WRAITH_AMBIENT_ID, WRAITH_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, WRAITH_HURT_ID, WRAITH_HURT);

        Registry.register(Registry.SOUND_EVENT, CORRUPTED_ILLAGER_AMBIENT_ID, CORRUPTED_ILLAGER_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, CORRUPTED_ILLAGER_HURT_ID, CORRUPTED_ILLAGER_HURT);
        Registry.register(Registry.SOUND_EVENT, CORRUPTED_ILLAGER_STEP_ID, CORRUPTED_ILLAGER_STEP);
        Registry.register(Registry.SOUND_EVENT, CORRUPTED_ILLAGER_DEATH_ID, CORRUPTED_ILLAGER_DEATH);

        Registry.register(Registry.SOUND_EVENT, ACOLYTE_AMBIENT_ID, ACOLYTE_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, ACOLYTE_HURT_ID, ACOLYTE_HURT);
        Registry.register(Registry.SOUND_EVENT, ACOLYTE_DEATH_ID, ACOLYTE_DEATH);

        Registry.register(Registry.SOUND_EVENT, REAPER_AMBIENT_ID, REAPER_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, REAPER_HURT_ID, REAPER_HURT);
        Registry.register(Registry.SOUND_EVENT, REAPER_DEATH_ID, REAPER_DEATH);
        Registry.register(Registry.SOUND_EVENT, REAPER_CHARGE_ID, REAPER_CHARGE);

        Registry.register(Registry.SOUND_EVENT, ALTAR_AMBIENT_ID, ALTAR_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, BONE_PLACED_ID, BONE_PLACED);
        Registry.register(Registry.SOUND_EVENT, BONE_AMBIENT_ID, BONE_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, VIAL_SPLASH_ID, VIAL_SPLASH);

        Registry.register(Registry.SOUND_EVENT, COFFIN_CLOSE_ID, COFFIN_CLOSE);
        Registry.register(Registry.SOUND_EVENT, COFFIN_OPEN_ID, COFFIN_OPEN);
        Registry.register(Registry.SOUND_EVENT, SARCOPHAGUS_USE_ID, SARCOPHAGUS_USE);
        Registry.register(Registry.SOUND_EVENT, URN_CLOSE_ID, URN_CLOSE);
        Registry.register(Registry.SOUND_EVENT, URN_OPEN_ID, URN_OPEN);

        Registry.register(Registry.SOUND_EVENT, LICH_CORPSE_SPELL_ID, LICH_CORPSE_SPELL);
        Registry.register(Registry.SOUND_EVENT, LICH_DEATH_ID, LICH_DEATH);
        Registry.register(Registry.SOUND_EVENT, LICH_HUNT_ID, LICH_HUNT);
        Registry.register(Registry.SOUND_EVENT, LICH_IDLE_ID, LICH_IDLE);
        Registry.register(Registry.SOUND_EVENT, LICH_MELEE_ID, LICH_MELEE);
        Registry.register(Registry.SOUND_EVENT, LICH_PHASE_03_ID, LICH_PHASE_03);
        Registry.register(Registry.SOUND_EVENT, LICH_PHASE_03_ATTACK_ID, LICH_PHASE_03_ATTACK);
        Registry.register(Registry.SOUND_EVENT, LICH_PHASE_02_ID, LICH_PHASE_02);
        Registry.register(Registry.SOUND_EVENT, LICH_SCARE_ID, LICH_SCARE);
        Registry.register(Registry.SOUND_EVENT, LICH_SPAWN_ID, LICH_SPAWN);
        Registry.register(Registry.SOUND_EVENT, LICH_HURT_ID, LICH_HURT);
        Registry.register(Registry.SOUND_EVENT, LICH_CAST_SKULL_ID, LICH_CAST_SKULL);
        Registry.register(Registry.SOUND_EVENT, LICH_CAST_LEVITATION_ID, LICH_CAST_LEVITATION);
        Registry.register(Registry.SOUND_EVENT, LICH_CAST_TELEPORT_ID, LICH_CAST_TELEPORT);

    }

}
