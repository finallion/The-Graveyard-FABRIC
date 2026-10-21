package com.lion.graveyard.entities.horde;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.entries.HordeConfigEntry;
import com.lion.graveyard.entities.HordeGraveyardEntity;
import com.lion.graveyard.entities.HostileGraveyardEntity;
import com.lion.graveyard.entities.MobSpawnGate;
import com.lion.graveyard.init.TGEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class GraveyardHordeSpawner implements CustomSpawner {
    private static final int CAPTAIN_SPOTS = 10;
    private int ticksUntilNextSpawn;

    public GraveyardHordeSpawner() {}

    public static boolean monstersAllowed(ServerLevel world) {
        return world.getDifficulty() != Difficulty.PEACEFUL && world.getGameRules().get(GameRules.SPAWN_MOBS)
                && world.isSpawningMonsters();
    }

    @Override
    public void tick(ServerLevel world, boolean spawnMonsters) {
        if (!spawnMonsters) {
            return;
        } else if (!Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).enabled || Graveyard.getConfig().structuresOnly()) {
           return;
        } else {
            RandomSource random = world.getRandom();
            --this.ticksUntilNextSpawn;

            if (this.ticksUntilNextSpawn > 0) {
                return;
            } else {
                int randomizer = Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).additionalRandomizedTicks;
                this.ticksUntilNextSpawn += Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).ticksUntilNextSpawn + random.nextInt(randomizer <= 0 ? 1200 : randomizer);
                HordeConfigEntry horde = Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn"));

                if (world.isDarkOutside() || !horde.onlyAtNight()) {
                    if (random.nextDouble() >= horde.chance()) {
                        return;
                    } else {
                        int i = world.players().size();

                        if (i < 1) {
                            return;
                        } else {
                            Player playerEntity = (Player)world.players().get(random.nextInt(i));

                            if (playerEntity.isSpectator()) {
                                return;
                            } else if (world.isCloseToVillage(playerEntity.blockPosition(), 2)) {
                                return;
                            } else {
                                int spawnAttempts = Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).mobSpawnAttempts;

                                if (spawnAttempts <= 0) {
                                    return;
                                }

                                boolean illagerSpawn = random.nextBoolean();

                                for (int spot = 0; spot < CAPTAIN_SPOTS; ++spot) {
                                    int distanceSpan = Math.max(1, horde.farDistance() - horde.nearDistance());
                                    int j = (horde.nearDistance() + random.nextInt(distanceSpan)) * (random.nextBoolean() ? -1 : 1);
                                    int k = (horde.nearDistance() + random.nextInt(distanceSpan)) * (random.nextBoolean() ? -1 : 1);
                                    BlockPos.MutableBlockPos mutable = playerEntity.blockPosition().mutable().move(j, 0, k);

                                    if (!world.hasChunksAt(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                                        continue;
                                    }

                                    if (!world.environmentAttributes().getValue(EnvironmentAttributes.CAN_PILLAGER_PATROL_SPAWN, mutable)) {
                                        continue;
                                    }

                                    mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());

                                    if (!this.spawnHordeEntity(world, mutable, random, true, illagerSpawn)) {
                                        continue;
                                    }

                                    if (playerEntity instanceof ServerPlayer targeted) {
                                        targeted.sendSystemMessage(Component.translatable("entity.graveyard.horde.appeared"));
                                    }

                                    this.spawnFollowers(world, mutable, random, illagerSpawn, spawnAttempts);
                                    return;
                                }

                                return;
                            }
                        }
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void spawnFollowers(
            ServerLevel world,
            BlockPos.MutableBlockPos mutable,
            RandomSource random,
            boolean illagerSpawn,
            int spawnAttempts
    ) {
        for (int p = 1; p < spawnAttempts; ++p) {
            mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
            mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
            mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
            this.spawnHordeEntity(world, mutable, random, false, illagerSpawn);
        }
    }

    private boolean spawnHordeEntity(
            ServerLevel world,
            BlockPos pos,
            RandomSource random,
            boolean captain,
            boolean illagerSpawn
    ) {
        BlockState blockState = world.getBlockState(pos);
        BlockState downState = world.getBlockState(pos.below());

        if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), TGEntities.GHOUL.get()) || !NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), TGEntities.REVENANT.get())) {
            return false;
        } else if (blockState.getFluidState().is(FluidTags.WATER)
                || downState.getFluidState().is(FluidTags.WATER)) {
            return false;
        } else if (!HostileGraveyardEntity.canSpawnInDarkness(TGEntities.GHOUL.get(), world, EntitySpawnReason.PATROL, pos, random)) {
            return false;
        } else {
            HordeGraveyardEntity hordeEntity;

            if (!illagerSpawn) {
                if (random.nextBoolean()) {
                    hordeEntity = TGEntities.GHOUL.get().create(world, EntitySpawnReason.PATROL);
                } else {
                    hordeEntity = TGEntities.REVENANT.get().create(world, EntitySpawnReason.PATROL);
                }
            } else {
                int rand = random.nextInt(5);
                switch (rand) {
                    case 0, 1 -> hordeEntity = TGEntities.CORRUPTED_PILLAGER.get().create(world, EntitySpawnReason.PATROL);
                    case 2, 3 -> hordeEntity = TGEntities.CORRUPTED_VINDICATOR.get().create(world, EntitySpawnReason.PATROL);
                    default -> hordeEntity = TGEntities.ACOLYTE.get().create(world, EntitySpawnReason.PATROL);
                }
            }

            if (hordeEntity != null && MobSpawnGate.blocks(hordeEntity.getType(), EntitySpawnReason.PATROL)) {
                return false;
            }

            if (hordeEntity != null) {
                if (captain) {
                    hordeEntity.setPatrolLeader(true);
                    hordeEntity.setHordeCaptain(true);
                    hordeEntity.setRandomPatrolTarget();
                }

                hordeEntity.setPos((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                hordeEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), EntitySpawnReason.PATROL, (SpawnGroupData)null);
                hordeEntity.getNavigation().stop();
                world.addFreshEntityWithPassengers(hordeEntity);
                return true;
            } else {
                return false;
            }
        }
    }
}
