package com.lion.graveyard.util;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.HordeGraveyardEntity;
import com.lion.graveyard.init.TGEntities;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnHordeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("horde").requires((source) -> source.hasPermission(3)).then(Commands.literal("trigger").executes((context) -> executeSpawn(context.getSource()))));
    }

    public static int executeSpawn(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("Spawned a graveyard horde."), false);
        int o = Graveyard.getConfig().getHorde(new ResourceLocation(Graveyard.MOD_ID, "horde_spawn")).mobSpawnAttempts;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(source.getPosition().x, source.getPosition().y, source.getPosition().z);
        ServerLevel world = source.getLevel();
        RandomSource random = source.getLevel().getRandom();
        boolean illagerSpawn = random.nextBoolean();

        for (int p = 0; p < o; ++p) {
            mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
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

    private static boolean spawnHordeEntity(ServerLevel world, BlockPos pos, RandomSource random, boolean captain, boolean illagerSpawn) {
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
                world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("A large Graveyard horde of undead appeared at x:" + pos.getX() + " y:" + pos.getY() + " z:" + pos.getZ() + "."), false);
            }

            hordeEntity.setPos((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
            hordeEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, (SpawnGroupData) null, (CompoundTag) null);
            hordeEntity.getNavigation().stop();
            world.addFreshEntityWithPassengers(hordeEntity);
            return true;
        } else {
            return false;
        }
    }

}
