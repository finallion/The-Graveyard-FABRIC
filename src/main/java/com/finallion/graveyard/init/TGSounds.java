package com.finallion.graveyard.init;

import com.finallion.graveyard.TheGraveyard;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TGSounds {
    public static final Identifier GHOUL_ROAR_ID = new Identifier(TheGraveyard.MOD_ID, "entity.ghoul.roar");
    public static SoundEvent GHOUL_ROAR = new SoundEvent(GHOUL_ROAR_ID);

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, GHOUL_ROAR_ID, GHOUL_ROAR);
    }

}
