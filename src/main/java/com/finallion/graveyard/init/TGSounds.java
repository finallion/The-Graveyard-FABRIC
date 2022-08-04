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

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, ALTAR_AMBIENT_ID, ALTAR_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, BONE_PLACED_ID, BONE_PLACED);
        Registry.register(Registry.SOUND_EVENT, BONE_AMBIENT_ID, BONE_AMBIENT);
        Registry.register(Registry.SOUND_EVENT, VIAL_SPLASH_ID, VIAL_SPLASH);

    }
}
