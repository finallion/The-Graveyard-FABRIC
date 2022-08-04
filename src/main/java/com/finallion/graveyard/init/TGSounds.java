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
    public static final Identifier LICH_SUMMON_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.summon");
    public static final Identifier LICH_MELEE_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.melee");
    public static final Identifier LICH_PHASE_02_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.phase02");
    public static final Identifier LICH_PHASE_03_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.phase03");
    public static final Identifier LICH_CAST_01_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.cast01");
    public static final Identifier LICH_CAST_02_ID = new Identifier(TheGraveyard.MOD_ID, "entity.lich.cast02");
    public static SoundEvent LICH_SPAWN = new SoundEvent(LICH_SPAWN_ID);
    public static SoundEvent LICH_SUMMON = new SoundEvent(LICH_SUMMON_ID);
    public static SoundEvent LICH_MELEE = new SoundEvent(LICH_MELEE_ID);
    public static SoundEvent LICH_PHASE_02 = new SoundEvent(LICH_PHASE_02_ID);
    public static SoundEvent LICH_PHASE_03 = new SoundEvent(LICH_PHASE_03_ID);
    public static SoundEvent LICH_CAST_01 = new SoundEvent(LICH_CAST_01_ID);
    public static SoundEvent LICH_CAST_02 = new SoundEvent(LICH_CAST_02_ID);

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, ALTAR_AMBIENT_ID, ALTAR_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, BONE_PLACED_ID, BONE_PLACED);
        Registry.register(Registry.SOUND_EVENT, BONE_AMBIENT_ID, BONE_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, VIAL_SPLASH_ID, VIAL_SPLASH);

        Registry.register(Registry.SOUND_EVENT, LICH_SPAWN_ID, LICH_SPAWN);
        Registry.register(Registry.SOUND_EVENT, LICH_SUMMON_ID, LICH_SUMMON);
        Registry.register(Registry.SOUND_EVENT, LICH_MELEE_ID, LICH_MELEE);
        Registry.register(Registry.SOUND_EVENT, LICH_PHASE_02_ID, LICH_PHASE_02);
        Registry.register(Registry.SOUND_EVENT, LICH_PHASE_03_ID, LICH_PHASE_03);
        Registry.register(Registry.SOUND_EVENT, LICH_CAST_01_ID, LICH_CAST_01);
        Registry.register(Registry.SOUND_EVENT, LICH_CAST_02_ID, LICH_CAST_02);

    }
}
