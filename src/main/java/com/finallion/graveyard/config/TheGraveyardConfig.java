package com.finallion.graveyard.config;

import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TheGraveyardConfig implements Config {

    /*
    @Comment("""
             Welcome to The Graveyard Config!
             //
             // Here, you can disable structures to spawn, change their chance of spawning, change their separation and also change their salt.
             // Additionally, you can turn the graveyard fog particles on and set the chance of spawning them (higher numbers = lower chance of spawning).
             // Graveyard fog particles spawn in graveyards and add an atmospheric and spooky element while exploring.
             //
             // To disable a structure to spawn, simply go to the corresponding entry and set `enabled` to false.
             //
             // The Graveyard uses the vanilla structure spawning system. That is -
             // - Separation is the minimum chunks between structures
             // - Spacing is the average chunks between structures (Spacing value needs to be higher than Separation value!)
             // - Salt is a special field that gives structures unique spawning positions. (Only change if you know what you are doing!)
             //
             // Notice: reducing the generation settings will increase the risk of structures overlapping. Especially jigsaw structures are unpredictable!  
            """)


     */

    @Comment("Graveyard structures can spawn:")
    public Structures structures = new Structures();

    @Comment("Structure generation settings. Separation is the minimum distance between two structures of this type in chunks; must be less than spacing. Spacing is the average distance between two structure placement attempts of this type in chunks. Change salt-value only if you know what you are doing!")
    public StructureChances structureChances = new StructureChances();

    @Comment("Graveyard fog particle settings:")
    public FogParticle fogParticle = new FogParticle();


    @Override
    public String getName() {
        return "the-graveyard-config";
    }

    @Override
    public String getExtension() {
        return "json5";
    }

    public static class FogParticle {
        @Comment("Graveyard fog particle can generate from graveyard moss:")
        public boolean canGenerate = true;
        @Comment("Graveyard fog particle chance to spawn 1 in ...")
        public int fog_particle_spawn = 50;
    }


    public static class StructureChances {
        @Comment("Large Birch Tree settings:")
        public int large_birch_tree_separation = 12;
        public int large_birch_tree_spacing = 14;
        public int large_birch_tree_salt = 304812394;
        @Comment("Medium Walled Graveyard settings:")
        public int medium_walled_graveyard_separation = 14;
        public int medium_walled_graveyard_spacing = 16;
        public int medium_walled_graveyard_salt = 379123039;
        @Comment("Mushroom Grave settings:")
        public int mushroom_grave_separation = 18;
        public int mushroom_grave_spacing = 24;
        public int mushroom_grave_salt = 598017285;
        @Comment("Small Grave settings:")
        public int small_grave_separation = 8;
        public int small_grave_spacing = 12;
        public int small_grave_salt = 240451934;
        @Comment("Small Walled Graveyard settings:")
        public int small_walled_graveyard_separation = 18;
        public int small_walled_graveyard_spacing = 20;
        public int small_walled_graveyard_salt = 1690192399;
        @Comment("Small Walled Graveyard (Desert) settings:")
        public int small_walled_graveyard_desert_separation = 28;
        public int small_walled_graveyard_desert_spacing = 32;
        public int small_walled_graveyard_desert_salt = 661903018;
        @Comment("Small Walled Graveyard (Savanna) settings:")
        public int small_walled_graveyard_savanna_separation = 12;
        public int small_walled_graveyard_savanna_spacing = 14;
        public int small_walled_graveyard_savanna_salt = 451235912;
        @Comment("Large Walled Graveyard settings:")
        public int large_walled_graveyard_separation = 9;
        public int large_walled_graveyard_spacing = 11;
        public int large_walled_graveyard_salt = 739017628;
    }


    public static class Structures {
        public boolean large_birch_tree = true;
        public boolean medium_walled_graveyard = true;
        public boolean mushroom_grave = true;
        public boolean small_grave = true;
        public boolean small_walled_graveyard = true;
        public boolean small_walled_graveyard_desert = true;
        public boolean small_walled_graveyard_savanna = true;
        public boolean large_walled_graveyard = true;
    }


}
