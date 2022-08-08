package com.finallion.graveyard.util;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MathUtil {

    public static void createParticleCircle(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float radius, DefaultParticleType type, Random random) {
        int surfaceArea = MathHelper.ceil(Math.PI * radius * radius); // circle surface area
        for (int j = 0; j < surfaceArea; ++j) {
            float h = random.nextFloat() * 6.2831855F; // two pi const (one period of sin and cos), returns value 0 to two pi
            float valueRange = MathHelper.sqrt(random.nextFloat()) * radius; // returns value 0 to radius
            double xCoord = x + (double) (MathHelper.cos(h) * valueRange);
            double yCoord = z + (double) (MathHelper.sin(h) * valueRange);

            world.addParticle(type, xCoord, y, yCoord, velocityX, velocityY, velocityZ);
        }
    }

}
