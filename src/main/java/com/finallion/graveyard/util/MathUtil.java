package com.finallion.graveyard.util;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MathUtil {

    public static <T extends ParticleEffect> void createParticleDisk(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float radius, T type, Random random) {
        int surfaceArea = MathHelper.ceil(Math.PI * radius * radius); // circle surface area
        for (int j = 0; j < surfaceArea; ++j) {
            float h = random.nextFloat() * 6.2831855F; // two pi const (one period of sin and cos), returns value 0 to two pi
            float valueRange = MathHelper.sqrt(random.nextFloat()) * radius; // returns value 0 to radius
            double xCoord = x + (double) (MathHelper.cos(h) * valueRange);
            double zCoord = z + (double) (MathHelper.sin(h) * valueRange);

            world.addParticle(type, xCoord, y, zCoord, velocityX, velocityY, velocityZ);
        }
    }

    public static <T extends ParticleEffect> void createParticleCircle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float radius, T type, Random random, float thickness) {
        int surfaceArea = MathHelper.ceil(Math.PI * radius * radius); // circle surface area
        for (int i = 0; i < surfaceArea; ++i) {
            float h = random.nextFloat() * 6.2831855F;
            double circleThickness = radius - thickness + (radius - (radius - thickness)) * random.nextDouble(); // random value between radius and radius-thickness
            double xCoord = x + MathHelper.sin(h) * circleThickness;
            double zCoord = z + MathHelper.cos(h) * circleThickness;
            world.addParticle(type, xCoord, y + 0.5, zCoord, velocityX, velocityY, velocityZ);
        }
    }

    public static <T extends ParticleEffect> void createParticleSpiral(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int length, T type, Random random) {
        double yCoord = y + 1.1D; // top of block

        for (int i = 0; i < length; i++) {
            float densityFactor = (float)i/25;
            double xCoord = x + MathHelper.sin(densityFactor);
            yCoord += 0.025;
            double zCoord = z + MathHelper.cos(densityFactor);
            if (random.nextInt(10) == 0) {
                world.addParticle(type, xCoord, yCoord, zCoord, velocityX, velocityY, velocityZ);
            }
        }
    }

    public static <T extends ParticleEffect> void createParticleFlare(World world, double x, double y, double z, int length, T type, T type2, Random random, boolean inverted) {
        double yCoord = y + 1.1D; // top of block

        double yDeviation = !inverted ? random.nextDouble() / 8 + 0.01 : -random.nextDouble() / 8 - 0.01; // pitch
        double xDeviation = -1.0 + 2.0D * random.nextDouble(); // random double between -1 and 1 to cover all x-z variations
        double zDeviation = -1.0 + 2.0D * random.nextDouble(); // random double between -1 and 1 to cover all x-z variations
        double curvatureFactor = random.nextInt(5) + 1;

        for (int j = 1; j < length; ++j) {
            float densityFactor = (float)j/25;
            double xDeviationVector = (densityFactor * xDeviation);
            double zDeviationVector = (densityFactor * zDeviation);

            double curvature = MathHelper.cos(densityFactor); // multiplies cos multiple times with itself to add more bendiness
            for (int k = 0; k < curvatureFactor; k++) {
                curvature *= curvature;
            }

            double xCoord = x + (curvature + xDeviationVector); // curvature * deviation is also possible to distort even more
            yCoord += yDeviation; // density of the flare
            double zCoord = z + (MathHelper.cos(densityFactor) + zDeviationVector); // z-curvature

            world.addParticle(random.nextBoolean() ? type : type2, xCoord, yCoord, zCoord, 0, 0, 0);
        }
    }
}
