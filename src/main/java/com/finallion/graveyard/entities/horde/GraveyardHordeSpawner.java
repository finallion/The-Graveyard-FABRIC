package com.finallion.graveyard.entities.horde;

import com.finallion.graveyard.TheGraveyard;
import com.finallion.graveyard.entities.AnimatedGraveyardEntity;
import com.finallion.graveyard.init.TGEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Spawner;

import java.util.Random;

public class GraveyardHordeSpawner implements Spawner {
    private int ticksUntilNextSpawn;

    public GraveyardHordeSpawner() {}

    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        } else if (!TheGraveyard.config.getHorde(new Identifier(TheGraveyard.MOD_ID + "horde_spawn")).enabled) {
           return 0;
        } else {
            Random random = world.random;
            --this.ticksUntilNextSpawn;
            if (this.ticksUntilNextSpawn > 0) {
                return 0;
            } else {
                this.ticksUntilNextSpawn += TheGraveyard.config.getHorde(new Identifier(TheGraveyard.MOD_ID + "horde_spawn")).ticksUntilNextSpawn + random.nextInt(1200);
                if (world.isNight()) {
                    if (random.nextInt(5) != 0) {
                        return 0;
                    } else {
                        int i = world.getPlayers().size();
                        if (i < 1) {
                            return 0;
                        } else {
                            PlayerEntity playerEntity = (PlayerEntity)world.getPlayers().get(random.nextInt(i));
                            if (playerEntity.isSpectator()) {
                                return 0;
                            } else if (world.isNearOccupiedPointOfInterest(playerEntity.getBlockPos(), 2)) {
                                return 0;
                            } else {
                                int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                                int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                                BlockPos.Mutable mutable = playerEntity.getBlockPos().mutableCopy().move(j, 0, k);
                                if (!world.isRegionLoaded(mutable.getX() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getZ() + 10)) {
                                    return 0;
                                } else {
                                    Biome biome = world.getBiome(mutable);
                                    Biome.Category category = biome.getCategory();
                                    if (category == Biome.Category.MUSHROOM) {
                                        return 0;
                                    } else {
                                        int n = 0;
                                        // how many entities will spawn
                                        int o = TheGraveyard.config.getHorde(new Identifier(TheGraveyard.MOD_ID + "horde_spawn")).size;

                                        for (int p = 0; p < o; ++p) {
                                            ++n;
                                            mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
                                            if (p == 0) {
                                                if (!this.spawnHordeEntity(world, mutable, random, true)) {
                                                    break;
                                                }
                                            } else {
                                                this.spawnHordeEntity(world, mutable, random, false);
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

    private boolean spawnHordeEntity(ServerWorld world, BlockPos pos, Random random, boolean captain) {
        BlockState blockState = world.getBlockState(pos);
        if (!SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), TGEntities.GHOUL) || !SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), TGEntities.REVENANT)) {
            return false;
        } else if (!AnimatedGraveyardEntity.canSpawn(TGEntities.GHOUL, world, SpawnReason.PATROL, pos, random)) {
            return false;
        } else {
            GraveyardHordeEntity hordeEntity;

            if (random.nextBoolean()) {
                hordeEntity = TGEntities.GHOUL.create(world);
            } else {
                hordeEntity = TGEntities.REVENANT.create(world);
            }

            if (hordeEntity != null) {
                if (captain) {
                    hordeEntity.setPatrolLeader(true);
                    hordeEntity.setRandomPatrolTarget();
                }

                hordeEntity.setPosition((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
                hordeEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.PATROL, (EntityData)null, (NbtCompound)null);
                hordeEntity.getNavigation().stop();
                world.spawnEntityAndPassengers(hordeEntity);
                return true;
            } else {
                return false;
            }
        }
    }
}
