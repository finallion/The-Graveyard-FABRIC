package com.lion.graveyard.entities.horde;

import com.lion.graveyard.Graveyard;
import com.lion.graveyard.entities.HordeGraveyardEntity;
import com.lion.graveyard.entities.HostileGraveyardEntity;
import com.lion.graveyard.init.TGEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.Player;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.spawner.SpecialSpawner;


public class GraveyardHordeSpawner implements SpecialSpawner {
    private int ticksUntilNextSpawn;

    public GraveyardHordeSpawner() {}

    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) {
            return 0;
        } else if (!Graveyard.getConfig().getHorde(new Identifier(Graveyard.MOD_ID, "horde_spawn")).enabled) {
           return 0;
        } else {
            Random random = world.random;
            --this.ticksUntilNextSpawn;
            if (this.ticksUntilNextSpawn > 0) {
                return 0;
            } else {
                int randomizer = Graveyard.getConfig().getHorde(new Identifier(Graveyard.MOD_ID, "horde_spawn")).additionalRandomizedTicks;
                this.ticksUntilNextSpawn += Graveyard.getConfig().getHorde(new Identifier(Graveyard.MOD_ID, "horde_spawn")).ticksUntilNextSpawn + random.nextInt(randomizer <= 0 ? 1200 : randomizer);
                if (world.isNight()) {
                    if (random.nextInt(5) != 0) {
                        return 0;
                    } else {
                        int i = world.getPlayers().size();
                        if (i < 1) {
                            return 0;
                        } else {
                            Player playerEntity = (Player)world.getPlayers().get(random.nextInt(i));
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
                                    RegistryEntry<Biome> registryEntry = world.getBiome(mutable);
                                    if (registryEntry.isIn(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                                        return 0;
                                    } else {
                                        int n = 0;
                                        // how many entities will spawn
                                        int o = Graveyard.getConfig().getHorde(new Identifier(Graveyard.MOD_ID, "horde_spawn")).mobSpawnAttempts;
                                        boolean illagerSpawn = random.nextBoolean();

                                        for (int p = 0; p < o; ++p) {
                                            ++n;
                                            mutable.setY(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
                                            if (p == 0) {
                                                if (!this.spawnHordeEntity(world, mutable, random, true, illagerSpawn)) {
                                                    break;
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

    private boolean spawnHordeEntity(ServerWorld world, BlockPos pos, Random random, boolean captain, boolean illagerSpawn) {
        BlockState blockState = world.getBlockState(pos);
        BlockState downState = world.getBlockState(pos.down());
        if (!SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), TGEntities.GHOUL.get()) || !SpawnHelper.isClearForSpawn(world, pos, blockState, blockState.getFluidState(), TGEntities.REVENANT.get())) {
            return false;
        } else if (world.getLightLevel(pos) > 1) {
            return false;
        } else if (blockState.getFluidState().isIn(FluidTags.WATER) || downState.getFluidState().isIn(FluidTags.WATER)) {
            return false;
        } else if (!HostileGraveyardEntity.canSpawnInDarkness(TGEntities.GHOUL.get(), world, SpawnReason.PATROL, pos, random)) {
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

            if (hordeEntity != null) {
                if (captain) {
                    hordeEntity.setPatrolLeader(true);
                    hordeEntity.setRandomPatrolTarget();
                    world.getServer().getPlayerManager().broadcast(Text.literal("A large Graveyard horde of undead appeared at " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + "."), false);
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
