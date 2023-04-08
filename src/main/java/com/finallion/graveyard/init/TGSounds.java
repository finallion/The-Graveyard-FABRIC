package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class TGSounds {
    public static final Identifier NAMELESS_HANGED_BREATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nameless_hanged.breath");
    public static SoundEvent NAMELESS_HANGED_BREATH = SoundEvent.of(NAMELESS_HANGED_BREATH_ID);
    public static final Identifier NAMELESS_HANGED_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nameless_hanged.ambient");
    public static SoundEvent NAMELESS_HANGED_AMBIENT = SoundEvent.of(NAMELESS_HANGED_AMBIENT_ID);
    public static final Identifier NAMELESS_HANGED_INTERACT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nameless_hanged.interact");
    public static SoundEvent NAMELESS_HANGED_INTERACT = SoundEvent.of(NAMELESS_HANGED_INTERACT_ID);

    public static final Identifier CORRUPTED_ILLAGER_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.ambient");
    public static SoundEvent CORRUPTED_ILLAGER_AMBIENT = SoundEvent.of(CORRUPTED_ILLAGER_AMBIENT_ID);
    public static final Identifier CORRUPTED_ILLAGER_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.hurt");
    public static SoundEvent CORRUPTED_ILLAGER_HURT = SoundEvent.of(CORRUPTED_ILLAGER_HURT_ID);
    public static final Identifier CORRUPTED_ILLAGER_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.death");
    public static SoundEvent CORRUPTED_ILLAGER_DEATH = SoundEvent.of(CORRUPTED_ILLAGER_DEATH_ID);
    public static final Identifier CORRUPTED_ILLAGER_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.corrupted_illager.step");
    public static SoundEvent CORRUPTED_ILLAGER_STEP = SoundEvent.of(CORRUPTED_ILLAGER_STEP_ID);

    public static final Identifier ACOLYTE_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.acolyte.ambient");
    public static SoundEvent ACOLYTE_AMBIENT = SoundEvent.of(ACOLYTE_AMBIENT_ID);
    public static final Identifier ACOLYTE_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.acolyte.hurt");
    public static SoundEvent ACOLYTE_HURT = SoundEvent.of(ACOLYTE_HURT_ID);
    public static final Identifier ACOLYTE_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.acolyte.death");
    public static SoundEvent ACOLYTE_DEATH = SoundEvent.of(ACOLYTE_DEATH_ID);

    public static final Identifier GHOUL_ROAR_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.roar");
    public static SoundEvent GHOUL_ROAR = SoundEvent.of(GHOUL_ROAR_ID);
    public static final Identifier GHOUL_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.ambient");
    public static SoundEvent GHOUL_AMBIENT = SoundEvent.of(GHOUL_AMBIENT_ID);
    public static final Identifier GHOUL_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.hurt");
    public static SoundEvent GHOUL_HURT = SoundEvent.of(GHOUL_HURT_ID);
    public static final Identifier GHOUL_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.death");
    public static SoundEvent GHOUL_DEATH = SoundEvent.of(GHOUL_DEATH_ID);
    public static final Identifier GHOUL_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.step");
    public static SoundEvent GHOUL_STEP = SoundEvent.of(GHOUL_STEP_ID);

    public static final Identifier GHOULING_GROAN_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.groan");
    public static SoundEvent GHOULING_GROAN = SoundEvent.of(GHOULING_GROAN_ID);
    public static final Identifier GHOULING_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.ambient");
    public static SoundEvent GHOULING_AMBIENT = SoundEvent.of(GHOULING_AMBIENT_ID);
    public static final Identifier GHOULING_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.hurt");
    public static SoundEvent GHOULING_HURT = SoundEvent.of(GHOULING_HURT_ID);
    public static final Identifier GHOULING_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.death");
    public static SoundEvent GHOULING_DEATH = SoundEvent.of(GHOULING_DEATH_ID);
    public static final Identifier GHOULING_ATTACK_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.attack");
    public static SoundEvent GHOULING_ATTACK = SoundEvent.of(GHOULING_ATTACK_ID);
    public static final Identifier GHOULING_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.step");
    public static SoundEvent GHOULING_STEP = SoundEvent.of(GHOULING_STEP_ID);
    public static final Identifier GHOULING_SPAWN_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghouling.spawn");
    public static SoundEvent GHOULING_SPAWN = SoundEvent.of(GHOULING_SPAWN_ID);

    public static final Identifier REVENANT_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.ambient");
    public static SoundEvent REVENANT_AMBIENT = SoundEvent.of(REVENANT_AMBIENT_ID);
    public static final Identifier REVENANT_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.hurt");
    public static SoundEvent REVENANT_HURT = SoundEvent.of(REVENANT_HURT_ID);
    public static final Identifier REVENANT_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.death");
    public static SoundEvent REVENANT_DEATH = SoundEvent.of(REVENANT_DEATH_ID);
    public static final Identifier REVENANT_STEP_ID = new Identifier(TheGraveyard.MOD_ID, "entity.revenant.step");
    public static SoundEvent REVENANT_STEP = SoundEvent.of(REVENANT_STEP_ID);

    public static final Identifier NIGHTMARE_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nightmare.ambient");
    public static SoundEvent NIGHTMARE_AMBIENT = SoundEvent.of(NIGHTMARE_AMBIENT_ID);
    public static final Identifier NIGHTMARE_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nightmare.hurt");
    public static SoundEvent NIGHTMARE_HURT = SoundEvent.of(NIGHTMARE_HURT_ID);
    public static final Identifier NIGHTMARE_DEATH_ID = new Identifier(TheGraveyard.MOD_ID, "entity.nightmare.death");
    public static SoundEvent NIGHTMARE_DEATH = SoundEvent.of(NIGHTMARE_DEATH_ID);

    public static final Identifier WRAITH_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.wraith.ambient");
    public static SoundEvent WRAITH_AMBIENT = SoundEvent.of(WRAITH_AMBIENT_ID);
    public static final Identifier WRAITH_HURT_ID = new Identifier(TheGraveyard.MOD_ID, "entity.wraith.hurt");
    public static SoundEvent WRAITH_HURT = SoundEvent.of(WRAITH_HURT_ID);

    public static final Identifier ALTAR_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "block.altar.ambient");
    public static SoundEvent ALTAR_AMBIENT = SoundEvent.of(ALTAR_AMBIENT_ID);

    public static final Identifier BONE_PLACED_ID = new Identifier(TheGraveyard.MOD_ID, "block.bone.placed");
    public static final Identifier BONE_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "block.bone.ambient");
    public static SoundEvent BONE_PLACED = SoundEvent.of(BONE_PLACED_ID);
    public static SoundEvent BONE_AMBIENT = SoundEvent.of(BONE_AMBIENT_ID);

    public static final Identifier VIAL_SPLASH_ID = new Identifier(TheGraveyard.MOD_ID, "item.vial.splash");
    public static SoundEvent VIAL_SPLASH = SoundEvent.of(VIAL_SPLASH_ID);

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
    public static SoundEvent LICH_SPAWN = SoundEvent.of(LICH_SPAWN_ID);
    public static SoundEvent LICH_MELEE = SoundEvent.of(LICH_MELEE_ID);
    public static SoundEvent LICH_PHASE_02 = SoundEvent.of(LICH_PHASE_02_ID);
    public static SoundEvent LICH_PHASE_03 = SoundEvent.of(LICH_PHASE_03_ID);
    public static SoundEvent LICH_CORPSE_SPELL = SoundEvent.of(LICH_CORPSE_SPELL_ID);
    public static SoundEvent LICH_CAST_SKULL = SoundEvent.of(LICH_CAST_SKULL_ID);
    public static SoundEvent LICH_DEATH = SoundEvent.of(LICH_DEATH_ID);
    public static SoundEvent LICH_HUNT = SoundEvent.of(LICH_HUNT_ID);
    public static SoundEvent LICH_SCARE = SoundEvent.of(LICH_SCARE_ID);
    public static SoundEvent LICH_IDLE = SoundEvent.of(LICH_IDLE_ID);
    public static SoundEvent LICH_HURT = SoundEvent.of(LICH_HURT_ID);
    public static SoundEvent LICH_CAST_LEVITATION = SoundEvent.of(LICH_CAST_LEVITATION_ID);
    public static SoundEvent LICH_CAST_TELEPORT = SoundEvent.of(LICH_CAST_TELEPORT_ID);
    public static SoundEvent LICH_PHASE_03_ATTACK = SoundEvent.of(LICH_PHASE_03_ATTACK_ID);

    public static void init() {
        Registry.register(Registries.SOUND_EVENT, NAMELESS_HANGED_BREATH_ID, NAMELESS_HANGED_BREATH);
        Registry.register(Registries.SOUND_EVENT, NAMELESS_HANGED_INTERACT_ID, NAMELESS_HANGED_INTERACT);
        Registry.register(Registries.SOUND_EVENT, NAMELESS_HANGED_AMBIENT_ID, NAMELESS_HANGED_AMBIENT);

        Registry.register(Registries.SOUND_EVENT, GHOUL_ROAR_ID, GHOUL_ROAR);
        Registry.register(Registries.SOUND_EVENT, GHOUL_AMBIENT_ID, GHOUL_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, GHOUL_HURT_ID, GHOUL_HURT);
        Registry.register(Registries.SOUND_EVENT, GHOUL_DEATH_ID, GHOUL_DEATH);
        Registry.register(Registries.SOUND_EVENT, GHOUL_STEP_ID, GHOUL_STEP);

        Registry.register(Registries.SOUND_EVENT, GHOULING_GROAN_ID, GHOULING_GROAN);
        Registry.register(Registries.SOUND_EVENT, GHOULING_SPAWN_ID, GHOULING_SPAWN);
        Registry.register(Registries.SOUND_EVENT, GHOULING_AMBIENT_ID, GHOULING_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, GHOULING_HURT_ID, GHOULING_HURT);
        Registry.register(Registries.SOUND_EVENT, GHOULING_ATTACK_ID, GHOULING_ATTACK);
        Registry.register(Registries.SOUND_EVENT, GHOULING_DEATH_ID, GHOULING_DEATH);
        Registry.register(Registries.SOUND_EVENT, GHOULING_STEP_ID, GHOULING_STEP);

        Registry.register(Registries.SOUND_EVENT, REVENANT_AMBIENT_ID, REVENANT_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, REVENANT_HURT_ID, REVENANT_HURT);
        Registry.register(Registries.SOUND_EVENT, REVENANT_DEATH_ID, REVENANT_DEATH);
        Registry.register(Registries.SOUND_EVENT, REVENANT_STEP_ID, REVENANT_STEP);

        Registry.register(Registries.SOUND_EVENT, NIGHTMARE_AMBIENT_ID, NIGHTMARE_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, NIGHTMARE_HURT_ID, NIGHTMARE_HURT);
        Registry.register(Registries.SOUND_EVENT, NIGHTMARE_DEATH_ID, NIGHTMARE_DEATH);

        Registry.register(Registries.SOUND_EVENT, WRAITH_AMBIENT_ID, WRAITH_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, WRAITH_HURT_ID, WRAITH_HURT);

        Registry.register(Registries.SOUND_EVENT, CORRUPTED_ILLAGER_AMBIENT_ID, CORRUPTED_ILLAGER_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, CORRUPTED_ILLAGER_HURT_ID, CORRUPTED_ILLAGER_HURT);
        Registry.register(Registries.SOUND_EVENT, CORRUPTED_ILLAGER_STEP_ID, CORRUPTED_ILLAGER_STEP);
        Registry.register(Registries.SOUND_EVENT, CORRUPTED_ILLAGER_DEATH_ID, CORRUPTED_ILLAGER_DEATH);

        Registry.register(Registries.SOUND_EVENT, ACOLYTE_AMBIENT_ID, ACOLYTE_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, ACOLYTE_HURT_ID, ACOLYTE_HURT);
        Registry.register(Registries.SOUND_EVENT, ACOLYTE_DEATH_ID, ACOLYTE_DEATH);

        Registry.register(Registries.SOUND_EVENT, ALTAR_AMBIENT_ID, ALTAR_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, BONE_PLACED_ID, BONE_PLACED);
        Registry.register(Registries.SOUND_EVENT, BONE_AMBIENT_ID, BONE_AMBIENT);
        Registry.register(Registries.SOUND_EVENT, VIAL_SPLASH_ID, VIAL_SPLASH);

        Registry.register(Registries.SOUND_EVENT, LICH_CORPSE_SPELL_ID, LICH_CORPSE_SPELL);
        Registry.register(Registries.SOUND_EVENT, LICH_DEATH_ID, LICH_DEATH);
        Registry.register(Registries.SOUND_EVENT, LICH_HUNT_ID, LICH_HUNT);
        Registry.register(Registries.SOUND_EVENT, LICH_IDLE_ID, LICH_IDLE);
        Registry.register(Registries.SOUND_EVENT, LICH_MELEE_ID, LICH_MELEE);
        Registry.register(Registries.SOUND_EVENT, LICH_PHASE_03_ID, LICH_PHASE_03);
        Registry.register(Registries.SOUND_EVENT, LICH_PHASE_03_ATTACK_ID, LICH_PHASE_03_ATTACK);
        Registry.register(Registries.SOUND_EVENT, LICH_PHASE_02_ID, LICH_PHASE_02);
        Registry.register(Registries.SOUND_EVENT, LICH_SCARE_ID, LICH_SCARE);
        Registry.register(Registries.SOUND_EVENT, LICH_SPAWN_ID, LICH_SPAWN);
        Registry.register(Registries.SOUND_EVENT, LICH_HURT_ID, LICH_HURT);
        Registry.register(Registries.SOUND_EVENT, LICH_CAST_SKULL_ID, LICH_CAST_SKULL);
        Registry.register(Registries.SOUND_EVENT, LICH_CAST_LEVITATION_ID, LICH_CAST_LEVITATION);
        Registry.register(Registries.SOUND_EVENT, LICH_CAST_TELEPORT_ID, LICH_CAST_TELEPORT);


    }

}
