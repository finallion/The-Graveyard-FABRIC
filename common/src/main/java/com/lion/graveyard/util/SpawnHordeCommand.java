package com.lion.graveyard.util;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.HordeGraveyardEntity;
import com.lion.graveyard.init.TGEntities;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

public class SpawnHordeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register(CommandManager.literal("horde").requires((source) -> source.hasPermissionLevel(3)).then(CommandManager.literal("trigger").executes((context) -> executeSpawn(context.getSource()))));
    }

    public static int executeSpawn(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal("Spawned a graveyard horde."), false);
        int o = Graveyard.getConfig().getHorde(new Identifier(Graveyard.MOD_ID, "horde_spawn")).mobSpawnAttempts;
        BlockPos.Mutable mutable = new BlockPos.Mutable(source.getPosition().getX(), source.getPosition().getY(), source.getPosition().getZ());
        ServerWorld world = source.getLevel();
        Random random = source.getLevel().getRandom();
        boolean illagerSpawn = random.nextBoolean();

        for (int p = 0; p < o; ++p) {
            mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
            if (p == 0) {
                if (!spawnHordeEntity(world, mutable, random, true, illagerSpawn)) {
                    break;
                }
            } else {
                spawnHordeEntity(world, mutable, random, false, illagerSpawn);
            }

            mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
            mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
        }

        return 1;
    }

    private static boolean spawnHordeEntity(ServerWorld world, BlockPos pos, Random random, boolean captain, boolean illagerSpawn) {
        HordeGraveyardEntity hordeEntity;

        if (!illagerSpawn) {
            if (random.nextBoolean()) {
                hordeEntity = TGEntities.GHOUL.get().create(world);
            } else {
                hordeEntity = TGEntities.REVENANT.get().create(world);
            }
        } else {
            int rand = random.nextInt(5);
            switch (rand) {
                case 0, 1 -> hordeEntity = TGEntities.CORRUPTED_PILLAGER.get().create(world);
                case 2, 3 -> hordeEntity = TGEntities.CORRUPTED_VINDICATOR.get().create(world);
                default -> hordeEntity = TGEntities.ACOLYTE.get().create(world);
            }
        }

        if (hordeEntity != null) {
            if (captain) {
                hordeEntity.setPatrolLeader(true);
                hordeEntity.setRandomPatrolTarget();
                world.getServer().getPlayerManager().broadcast(Text.literal("A large Graveyard horde of undead appeared at x:" + pos.getX() + " y:" + pos.getY() + " z:" + pos.getZ() + "."), false);
            }

            hordeEntity.setPosition((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
            hordeEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.PATROL, (EntityData) null, (NbtCompound) null);
            hordeEntity.getNavigation().stop();
            world.spawnEntityAndPassengers(hordeEntity);
            return true;
        } else {
            return false;
        }
    }

}
