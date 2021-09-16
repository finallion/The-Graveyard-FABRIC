package com.finallion.graveyard.init;

import com.finallion.graveyard.particles.GraveyardFogParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class TGParticles {


    public static DefaultParticleType GRAVEYARD_FOG_PARTICLE;

    public static void init() {

        GRAVEYARD_FOG_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, "graveyard:graveyard_fog_particle", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(GRAVEYARD_FOG_PARTICLE, GraveyardFogParticle.FogFactory::new);
    }

}
