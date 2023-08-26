package com.lion.graveyard.platform.forge;

import com.lion.graveyard.Graveyard;
import net.minecraft.particle.ParticleType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ClientRegistryHelperImpl {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Graveyard.MOD_ID);
    public static final DeferredRegister<ScreenHandlerType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Graveyard.MOD_ID);



}
