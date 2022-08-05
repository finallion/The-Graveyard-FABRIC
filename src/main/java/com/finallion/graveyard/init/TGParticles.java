package com.finallion.graveyard.init;


import com.finallion.graveyard.particles.GraveyardFogParticle;
import com.finallion.graveyard.particles.GraveyardHandParticle;
import com.finallion.graveyard.particles.GraveyardSoulParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class TGParticles {


    public static DefaultParticleType GRAVEYARD_FOG_PARTICLE;
    public static DefaultParticleType GRAVEYARD_SOUL_PARTICLE;
    public static DefaultParticleType GRAVEYARD_HAND_PARTICLE;

    public static void init() {

        GRAVEYARD_FOG_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, "graveyard:graveyard_fog_particle", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);

        GRAVEYARD_SOUL_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, "graveyard:graveyard_soul_particle", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(GRAVEYARD_SOUL_PARTICLE, GraveyardSoulParticle.Factory::new);

        GRAVEYARD_HAND_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, "graveyard:graveyard_hand_particle", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(GRAVEYARD_HAND_PARTICLE, GraveyardHandParticle.Factory::new);
    }

}
