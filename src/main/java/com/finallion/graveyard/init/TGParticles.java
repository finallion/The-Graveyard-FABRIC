package com.finallion.graveyard.init;



import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class TGParticles {

    public static DefaultParticleType GRAVEYARD_FOG_PARTICLE;
    public static DefaultParticleType GRAVEYARD_SOUL_PARTICLE;
    public static DefaultParticleType GRAVEYARD_HAND_PARTICLE;
    public static DefaultParticleType GRAVEYARD_LEFT_HAND_PARTICLE;
    public static DefaultParticleType GRAVEYARD_SOUL_BEAM_PARTICLE;

    public static void init() {
        GRAVEYARD_FOG_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, "graveyard:graveyard_fog_particle", FabricParticleTypes.simple(true));
        GRAVEYARD_SOUL_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, "graveyard:graveyard_soul_particle", FabricParticleTypes.simple(true));
        GRAVEYARD_HAND_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, "graveyard:graveyard_hand_particle", FabricParticleTypes.simple(true));
        GRAVEYARD_LEFT_HAND_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, "graveyard:graveyard_left_hand_particle", FabricParticleTypes.simple(true));
        GRAVEYARD_SOUL_BEAM_PARTICLE = Registry.register(Registries.PARTICLE_TYPE, "graveyard:graveyard_soul_beam_particle", FabricParticleTypes.simple(true));
    }

}
