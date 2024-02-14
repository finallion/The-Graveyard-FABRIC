package com.lion.graveyard.platform.neoforge;

/*
public record ModSpawnModifier(RegistryEntryList<Biome> biomes, SpawnSettings.SpawnEntry spawn) implements BiomeModifier {
    private static final MobConfigEntry ghoul = Graveyard.CONFIG.mobConfigEntries.get("ghoul");
    private static final MobConfigEntry revenant = Graveyard.CONFIG.mobConfigEntries.get("revenant");
    private static final MobConfigEntry reaper = Graveyard.CONFIG.mobConfigEntries.get("reaper");
    private static final MobConfigEntry nightmare = Graveyard.CONFIG.mobConfigEntries.get("nightmare");
    private static final MobConfigEntry skeleton_creeper = Graveyard.CONFIG.mobConfigEntries.get("skeleton_creeper");
    @Override
    public void modify(RegistryEntry<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD) {
            if (biome.isIn(BiomeTags.WITHOUT_PATROL_SPAWNS)) {
                return;
            }

            if (this.spawn.type == TGEntities.GHOUL.get() && ghoul.enabled && biome.isIn(TGTags.GHOUL_SPAWNS)) {
                SpawnSettings.SpawnEntry data = new SpawnSettings.SpawnEntry(this.spawn.type,
                        ghoul.weight,
                        ghoul.minGroup,
                        ghoul.maxGroup);
                builder.getMobSpawnSettings().spawn(this.spawn.type.getSpawnGroup(), data);
            } else if (this.spawn.type == TGEntities.REVENANT.get() && revenant.enabled && biome.isIn(TGTags.REVENANT_SPAWNS)) {
                SpawnSettings.SpawnEntry data = new SpawnSettings.SpawnEntry(this.spawn.type,
                        revenant.weight,
                        revenant.minGroup,
                        revenant.maxGroup);
                builder.getMobSpawnSettings().spawn(this.spawn.type.getSpawnGroup(), data);
            } else if (this.spawn.type == TGEntities.REAPER.get() && reaper.enabled && biome.isIn(TGTags.REAPER_SPAWNS)) {
                SpawnSettings.SpawnEntry data = new SpawnSettings.SpawnEntry(this.spawn.type,
                        reaper.weight,
                        reaper.minGroup,
                        reaper.maxGroup);
                builder.getMobSpawnSettings().spawn(this.spawn.type.getSpawnGroup(), data);
            } else if (this.spawn.type == TGEntities.NIGHTMARE.get() && nightmare.enabled && biome.isIn(TGTags.NIGHTMARE_SPAWNS)) {
                SpawnSettings.SpawnEntry data = new SpawnSettings.SpawnEntry(this.spawn.type,
                        nightmare.weight,
                        nightmare.minGroup,
                        nightmare.maxGroup);
                builder.getMobSpawnSettings().spawn(this.spawn.type.getSpawnGroup(), data);
            } else if (this.spawn.type == TGEntities.SKELETON_CREEPER.get() && skeleton_creeper.enabled && biome.isIn(TGTags.SKELETON_CREEPER_SPAWNS)) {
                SpawnSettings.SpawnEntry data = new SpawnSettings.SpawnEntry(this.spawn.type,
                        skeleton_creeper.weight,
                        skeleton_creeper.minGroup,
                        skeleton_creeper.maxGroup);
                builder.getMobSpawnSettings().spawn(this.spawn.type.getSpawnGroup(), data);

            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return makeCodec();
    }

    public static Codec<ModSpawnModifier> makeCodec() {
        return RecordCodecBuilder.create(builder -> builder.group(
                Biome.REGISTRY_ENTRY_LIST_CODEC.fieldOf("biomes").forGetter(ModSpawnModifier::biomes),
                SpawnSettings.SpawnEntry.CODEC.fieldOf("spawn").forGetter(ModSpawnModifier::spawn)
        ).apply(builder, ModSpawnModifier::new));
    }
}

 */

