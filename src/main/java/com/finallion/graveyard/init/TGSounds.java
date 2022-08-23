package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TGSounds {
    public static final Identifier ALTAR_AMBIENT_ID = new Identifier(TheGraveyard.MOD_ID, "block.altar.ambient");
    public static SoundEvent ALTAR_AMBIENT = new SoundEvent(ALTAR_AMBIENT_ID);

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

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, ALTAR_AMBIENT_ID, ALTAR_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, BONE_PLACED_ID, BONE_PLACED);
        Registry.register(Registry.SOUND_EVENT, BONE_AMBIENT_ID, BONE_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, VIAL_SPLASH_ID, VIAL_SPLASH);

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
