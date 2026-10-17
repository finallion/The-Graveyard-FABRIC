package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import net.minecraft.core.particles.SimpleParticleType;

public class TGParticles {

    public static SimpleParticleType GRAVEYARD_FOG_PARTICLE = new SimpleParticleType(false);
    public static SimpleParticleType GRAVEYARD_SOUL_PARTICLE = new SimpleParticleType(false);
    public static SimpleParticleType GRAVEYARD_HAND_PARTICLE = new SimpleParticleType(false);
    public static SimpleParticleType GRAVEYARD_LEFT_HAND_PARTICLE = new SimpleParticleType(false);
    public static SimpleParticleType GRAVEYARD_SOUL_BEAM_PARTICLE = new SimpleParticleType(false);

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
