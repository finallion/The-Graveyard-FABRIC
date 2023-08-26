package com.finallion.graveyard.util;

import com.finallion.graveyard.config.GraveyardConfig;
import com.finallion.graveyard.entities.HordeGraveyardEntity;
import com.finallion.graveyard.init.TGEntities;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;


public class TGCommands {

    public static void init() {

    }

    public static void register(CommandDispatcher<CommandSourceStack> p_138815_) {
        p_138815_.register(Commands.literal("horde").requires((p_138819_) -> {
            return p_138819_.hasPermission(3);
        }).then(Commands.literal("trigger").executes((context -> {
            return TGCommands.executeSpawn(context.getSource());
        }))));
    }

    public static int executeSpawn(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Spawned a graveyard horde."), false);
        int o = GraveyardConfig.COMMON.mobSpawnAttempts.get();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(source.getPosition().x, source.getPosition().y, source.getPosition().z);
        ServerLevel level = source.getLevel();
        RandomSource random = source.getLevel().getRandom();
        boolean illagerSpawn = random.nextBoolean();

        for (int l1 = 0; l1 < o; ++l1) {
            mutable.setY(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
            if (l1 == 0) {
                if (!spawnHordeEntity(level, mutable, random, true, illagerSpawn)) {
                    break;
                }
            } else {
                spawnHordeEntity(level, mutable, random, false, illagerSpawn);
            }

            mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
            mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
        }

        return 1;
    }

    private static boolean spawnHordeEntity(Level p_64565_, BlockPos p_64566_, RandomSource p_64567_, boolean p_64568_, boolean illagerSpawn) {
        HordeGraveyardEntity hordeEntity;

        if (!illagerSpawn) {
            if (p_64567_.nextBoolean()) {
                hordeEntity = TGEntities.GHOUL.get().create(p_64565_);
            } else {
                hordeEntity = TGEntities.REVENANT.get().create(p_64565_);
            }
        } else {
            int rand = p_64567_.nextInt(5);
            switch (rand) {
                case 0, 1 -> hordeEntity = TGEntities.CORRUPTED_PILLAGER.get().create(p_64565_);
                case 2, 3 -> hordeEntity = TGEntities.CORRUPTED_VINDICATOR.get().create(p_64565_);
                default -> hordeEntity = TGEntities.ACOLYTE.get().create(p_64565_);
            }
        }

        if (hordeEntity != null) {
            if (p_64568_) {
                hordeEntity.setPatrolLeader(true);
                hordeEntity.findPatrolTarget();
            }
            hordeEntity.setPos((double)p_64566_.getX(), (double)p_64566_.getY(), (double)p_64566_.getZ());
            //if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(hordeEntity, p_64565_, p_64566_.getX(), p_64566_.getY(), p_64566_.getZ(), null, MobSpawnType.PATROL) == -1) return false;
            hordeEntity.finalizeSpawn((ServerLevel)p_64565_, p_64565_.getCurrentDifficultyAt(p_64566_), MobSpawnType.PATROL, (SpawnGroupData)null, (CompoundTag)null);
            ((ServerLevel)p_64565_).addFreshEntityWithPassengers(hordeEntity);
            return true;
        } else {
            return false;
        }
    }

}
