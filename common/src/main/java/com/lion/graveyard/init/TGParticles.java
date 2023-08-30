package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.particle.DefaultParticleType;

public class TGParticles {

    public static DefaultParticleType GRAVEYARD_FOG_PARTICLE = new DefaultParticleType(false);
    public static DefaultParticleType GRAVEYARD_SOUL_PARTICLE = new DefaultParticleType(false);
    public static DefaultParticleType GRAVEYARD_HAND_PARTICLE = new DefaultParticleType(false);
    public static DefaultParticleType GRAVEYARD_LEFT_HAND_PARTICLE = new DefaultParticleType(false);
    public static DefaultParticleType GRAVEYARD_SOUL_BEAM_PARTICLE = new DefaultParticleType(false);

    static {
        RegistryHelper.registerParticleType("graveyard_fog_particle", GRAVEYARD_FOG_PARTICLE);
        RegistryHelper.registerParticleType("graveyard_soul_particle", GRAVEYARD_SOUL_PARTICLE);
        RegistryHelper.registerParticleType("graveyard_hand_particle", GRAVEYARD_HAND_PARTICLE);
        RegistryHelper.registerParticleType("graveyard_left_hand_particle", GRAVEYARD_LEFT_HAND_PARTICLE);
        RegistryHelper.registerParticleType("graveyard_soul_beam_particle", GRAVEYARD_SOUL_BEAM_PARTICLE);
    }

    public static void init() {

    }

}
