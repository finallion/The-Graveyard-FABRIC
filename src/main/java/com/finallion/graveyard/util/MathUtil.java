package com.finallion.graveyard.util;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MathUtil {

    public static void createParticleDisk(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float radius, DefaultParticleType type, Random random) {
        int surfaceArea = MathHelper.ceil(Math.PI * radius * radius); // circle surface area
        for (int j = 0; j < surfaceArea; ++j) {
            float h = random.nextFloat() * 6.2831855F; // two pi const (one period of sin and cos), returns value 0 to two pi
            float valueRange = MathHelper.sqrt(random.nextFloat()) * radius; // returns value 0 to radius
            double xCoord = x + (double) (MathHelper.cos(h) * valueRange);
            double zCoord = z + (double) (MathHelper.sin(h) * valueRange);

            world.addParticle(type, xCoord, y, zCoord, velocityX, velocityY, velocityZ);
        }
    }

    public static void createParticleCircle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float radius, DefaultParticleType type, Random random, float thickness) {
        int surfaceArea = MathHelper.ceil(Math.PI * radius * radius); // circle surface area
        for (int j = 0; j < surfaceArea; ++j) {
            float h = random.nextFloat() * 6.2831855F; // two pi const (one period of sin and cos), returns value 0 to two pi
            float valueRange = MathHelper.sqrt(random.nextFloat()) * radius; // returns value 0 to radius
            double xCoord = x + (double) (MathHelper.cos(h) * valueRange);
            double zCoord = z + (double) (MathHelper.sin(h) * valueRange);

            if (Math.abs(MathHelper.cos(h) * valueRange) + Math.abs(MathHelper.sin(h) * valueRange) >= radius - thickness) {
                world.addParticle(type, xCoord, y, zCoord, velocityX, velocityY, velocityZ);
            }
        }
    }

    public static void createParticleFlare(World world, double x, double y, double z, int length, DefaultParticleType type, Random random) {
        double yCoord = y + 1.1D; // top of block

        double yDeviation = random.nextDouble() / 8 + 0.01; // pitch
        double xzDeviation = -1.0 + 2.0D * random.nextDouble(); // random double between -1 and 1 to cover all x-z variations
        double curvatureFactor = random.nextInt(5) + 1;


        for (int j = 1; j < length; ++j) {
            float densityFactor = (float)j/25;
            double deviation = (densityFactor * xzDeviation);

            double curvature = MathHelper.cos(densityFactor); // multiplies cos multiple times with itself to add more bendiness
            for (int k = 0; k < curvatureFactor; k++) {
                curvature *= curvature;
            }

            double xCoord = x - 0.5D + curvature + deviation;
            yCoord += yDeviation; // density of the flare
            double zCoord = z + 0.5D + MathHelper.cos(densityFactor) + deviation; // z-curvature

            world.addParticle(type, xCoord, yCoord, zCoord, 0, 0, 0);
        }
    }


}
