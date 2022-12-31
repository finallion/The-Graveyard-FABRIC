package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class TGSounds {
    public static final Identifier GHOUL_ROAR_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.roar");
    public static SoundEvent GHOUL_ROAR = SoundEvent.of(GHOUL_ROAR_ID);

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
        Registry.register(Registries.SOUND_EVENT, GHOUL_ROAR_ID, GHOUL_ROAR);
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
