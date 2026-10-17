package com.lion.graveyard.entities.horde;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.config.entries.HordeConfigEntry;
import com.lion.graveyard.entities.HordeGraveyardEntity;
import com.lion.graveyard.entities.HostileGraveyardEntity;
import com.lion.graveyard.entities.MobSpawnGate;
import com.lion.graveyard.init.TGEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class GraveyardHordeSpawner implements CustomSpawner {
    private int ticksUntilNextSpawn;

    public GraveyardHordeSpawner() {}

    public static boolean monstersAllowed(ServerLevel world) {
        return world.getDifficulty() != Difficulty.PEACEFUL && world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
                && world.getServer().isSpawningMonsters();
    }

    @Override
    public int tick(ServerLevel world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        } else if (!Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).enabled || Graveyard.getConfig().structuresOnly()) {
           return 0;
        } else {
            RandomSource random = world.random;
            --this.ticksUntilNextSpawn;

            if (this.ticksUntilNextSpawn > 0) {
                return 0;
            } else {
                int randomizer = Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).additionalRandomizedTicks;
                this.ticksUntilNextSpawn += Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).ticksUntilNextSpawn + random.nextInt(randomizer <= 0 ? 1200 : randomizer);
                HordeConfigEntry horde = Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn"));

                if (world.isNight() || !horde.onlyAtNight()) {
                    if (random.nextDouble() >= horde.chance()) {
                        return 0;
                    } else {
                        int i = world.players().size();

                        if (i < 1) {
                            return 0;
                        } else {
                            Player playerEntity = (Player)world.players().get(random.nextInt(i));

                            if (playerEntity.isSpectator()) {
                                return 0;
                            } else if (world.isCloseToVillage(playerEntity.blockPosition(), 2)) {
                                return 0;
                            } else {
                                int distanceSpan = Math.max(1, horde.farDistance() - horde.nearDistance());
                                int j = (horde.nearDistance() + random.nextInt(distanceSpan)) * (random.nextBoolean() ? -1 : 1);
                                int k = (horde.nearDistance() + random.nextInt(distanceSpan)) * (random.nextBoolean() ? -1 : 1);
                                BlockPos.MutableBlockPos mutable = playerEntity.blockPosition().mutable().move(j, 0, k);

                                if (!world.hasChunksAt(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                                    return 0;
                                } else {
                                    Holder<Biome> registryEntry = world.getBiome(mutable);

                                    if (registryEntry.is(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                        return 0;
                                    } else {
                                        int n = 0;
                                        int spawnAttempts = Graveyard.getConfig().getHorde(Graveyard.id("horde_spawn")).mobSpawnAttempts;
                                        boolean illagerSpawn = random.nextBoolean();

                                        for (int p = 0; p < spawnAttempts; ++p) {
                                            ++n;
                                            mutable.setY(world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mutable).getY());

                                            if (p == 0) {
                                                if (!this.spawnHordeEntity(world, mutable, random, true, illagerSpawn)) {
                                                    break;
                                                }

                                                if (playerEntity instanceof ServerPlayer targeted) {
                                                    targeted.displayClientMessage(Component.translatable("entity.graveyard.horde.appeared"), false);
                                                }
                                            } else {
                                                this.spawnHordeEntity(world, mutable, random, false, illagerSpawn);
                                            }

                                            mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
                                            mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }

                                        return n;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean spawnHordeEntity(ServerLevel world, BlockPos pos, RandomSource random, boolean captain, boolean illagerSpawn) {
        BlockState blockState = world.getBlockState(pos);
        BlockState downState = world.getBlockState(pos.below());

        if (!NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), TGEntities.GHOUL.get()) || !NaturalSpawner.isValidEmptySpawnBlock(world, pos, blockState, blockState.getFluidState(), TGEntities.REVENANT.get())) {
            return false;
        } else if (world.getMaxLocalRawBrightness(pos) > 1) {
            return false;
        } else if (blockState.getFluidState().is(FluidTags.WATER)
                || downState.getFluidState().is(FluidTags.WATER)) {
            return false;
        } else if (!HostileGraveyardEntity.canSpawnInDarkness(TGEntities.GHOUL.get(), world, MobSpawnType.PATROL, pos, random)) {
            return false;
        } else {
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

            if (hordeEntity != null && MobSpawnGate.blocks(hordeEntity.getType(), MobSpawnType.PATROL)) {
                return false;
            }

            if (hordeEntity != null) {
                if (captain) {
                    hordeEntity.setPatrolLeader(true);
                    hordeEntity.setHordeCaptain(true);
                    hordeEntity.setRandomPatrolTarget();
                }

                hordeEntity.setPos((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                hordeEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), MobSpawnType.PATROL, (SpawnGroupData)null, (CompoundTag)null);
                hordeEntity.getNavigation().stop();
                world.addFreshEntityWithPassengers(hordeEntity);
                return true;
            } else {
                return false;
            }
        }
    }
}
