package com.lion.graveyard.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ConfigSchema {
    public static final List<String> STRUCTURE_IDS = List.of(
            "altar",
            "crypt",
            "dead_tree",
            "giant_mushroom",
            "haunted_house",
            "large_graveyard",
            "lich_prison",
            "medium_graveyard",
            "memorial_tree",
            "mushroom_grave",
            "overrun_village",
            "overrun_desert_village",
            "overrun_plains_village",
            "overrun_savanna_village",
            "overrun_snowy_village",
            "overrun_taiga_village",
            "ruins",
            "small_desert_grave",
            "small_desert_graveyard",
            "small_grave",
            "small_graveyard",
            "small_mountain_grave",
            "small_savanna_grave"
    );

    public static final String NAMELESS_HANGED = "nameless_hanged";

    private static final double MIN_MULTIPLIER = 0.05D;

    private static final List<MobDefaults> MOBS = List.of(
            new MobDefaults("acolyte", true, 0, 0, 0, false),
            new MobDefaults("corrupted_evoker", true, 0, 0, 0, true),
            new MobDefaults("corrupted_pillager", true, 0, 0, 0, true),
            new MobDefaults("corrupted_vindicator", true, 0, 0, 0, true),
            new MobDefaults("crow", true, 10, 4, 9, false),
            new MobDefaults("ghoul", true, 25, 2, 5, true),
            new MobDefaults("magpie", true, 8, 2, 3, false),
            new MobDefaults(NAMELESS_HANGED, true, 0, 0, 0, false),
            new MobDefaults("nightmare", true, 10, 1, 1, false),
            new MobDefaults("raven", true, 8, 2, 4, false),
            new MobDefaults("reaper", false, 5, 2, 3, true),
            new MobDefaults("revenant", true, 25, 5, 8, true),
            new MobDefaults("skeleton_creeper", true, 25, 1, 4, true),
            new MobDefaults("tormented_villager", true, 0, 1, 1, false),
            new MobDefaults("wraith", true, 0, 0, 0, true)
    );

    public static final List<String> FULL_MOB_IDS = MOBS.stream()
            .map(MobDefaults::id)
            .filter(id -> !id.equals(NAMELESS_HANGED))
            .toList();

    public static final List<String> ALL_MOB_IDS = MOBS.stream()
            .map(MobDefaults::id)
            .toList();

    public static final ConfigSchema INSTANCE = new ConfigSchema();

    private final Section root = new Section("", "", "", false);
    private final List<Leaf> leaves = new ArrayList<>();
    private final Map<String, Leaf> leavesByPath = new LinkedHashMap<>();

    private ConfigSchema() {
        buildGeneral();
        buildStructures();
        buildMobs();
        buildHordes();
        buildChampion();
        buildChronicle();
        buildBehaviour();
        collect(root);
    }

    public Section root() {
        return root;
    }

    public List<Leaf> leaves() {
        return leaves;
    }

    public Leaf leaf(String path) {
        return leavesByPath.get(path);
    }

    public List<String> leafPaths() {
        return new ArrayList<>(leavesByPath.keySet());
    }

    public static boolean isFullMob(String id) {
        return FULL_MOB_IDS.contains(id);
    }

    private void collect(Section section) {
        for (Node child : section.children()) {
            if (child instanceof Section nested) {
                collect(nested);
            } else {
                Leaf leaf = (Leaf) child;
                leaves.add(leaf);
                leavesByPath.put(leaf.path(), leaf);
            }
        }
    }

    private void buildGeneral() {
        Section general = root.section("general", "");
        general.bool("structuresOnly", false, "Set true to stop every Graveyard mob from appearing on its own. Spawn eggs and commands still work.");
        general.bool("bossMusic", true, "Play the boss music during the Corrupted Champion fight. Client setting.");
        general.bool("ashParticles", true, "Show ash particles above smoldering blocks. Client setting.");
    }

    private void buildStructures() {
        Section structures = root.table("structures");
        Map<String, String> names = new LinkedHashMap<>();
        names.put("altar", "Altar of the Corrupted Champion.");
        names.put("crypt", "Crypt below the surface.");
        names.put("dead_tree", "Dead tree.");
        names.put("giant_mushroom", "Giant mushroom.");
        names.put("haunted_house", "Haunted house.");
        names.put("large_graveyard", "Large graveyard.");
        names.put("lich_prison", "Prison of the Corrupted Champion in the sky.");
        names.put("medium_graveyard", "Medium graveyard.");
        names.put("memorial_tree", "Memorial tree.");
        names.put("mushroom_grave", "Mushroom grave.");
        names.put("overrun_village", "Overrun village. Its switch and its chance apply to every overrun village variant.");
        names.put("overrun_desert_village", "Overrun desert village.");
        names.put("overrun_plains_village", "Overrun plains village.");
        names.put("overrun_savanna_village", "Overrun savanna village.");
        names.put("overrun_snowy_village", "Overrun snowy village.");
        names.put("overrun_taiga_village", "Overrun taiga village.");
        names.put("ruins", "Ruins.");
        names.put("small_desert_grave", "Small desert grave.");
        names.put("small_desert_graveyard", "Small desert graveyard.");
        names.put("small_grave", "Small grave.");
        names.put("small_graveyard", "Small graveyard.");
        names.put("small_mountain_grave", "Small mountain grave.");
        names.put("small_savanna_grave", "Small savanna grave.");

        for (String id : STRUCTURE_IDS) {
            Section entry = structures.section(id, names.get(id));
            entry.bool("enabled", true, "Set false to stop this structure from generating.");
            entry.real("chance", 1.0D, 0.0D, 1.0D, "Chance from 0 to 1 that a placed structure generates.");

            if (id.equals("crypt")) {
                entry.integer("minY", -40, null, null, "Lowest Y level of the crypt.");
                entry.integer("maxY", -10, null, null, "Highest Y level of the crypt.");
            } else if (id.equals("lich_prison")) {
                entry.integer("minY", 210, null, null, "Lowest Y level of the prison.");
                entry.integer("maxY", 210, null, null, "Highest Y level of the prison.");
            }
        }
    }

    private void buildMobs() {
        Section mobs = root.table("mobs");

        for (MobDefaults mob : MOBS) {
            if (mob.id().equals(NAMELESS_HANGED)) {
                Section entry = mobs.section(mob.id(), "Nameless hanged. This mob has no other settings.");
                entry.bool("enabled", mob.enabled(), "Set false to stop this mob from appearing on its own. Spawn eggs and commands still work.");
                continue;
            }

            String comment = mob.id().equals("reaper") ? "The reaper is off by default." : sentence(mob.id());
            Section entry = mobs.section(mob.id(), comment);
            entry.bool("enabled", mob.enabled(), "Set false to stop this mob from appearing on its own. Spawn eggs and commands still work.");
            Section spawn = entry.section("spawn", "Natural spawn. Weight 0 turns the natural spawn off. Group size is the number of mobs per spawn.");
            spawn.integer("weight", mob.weight(), 0, null, "Weight of the natural spawn.");
            spawn.integer("minGroup", mob.minGroup(), 0, null, "Smallest group size. At least 1 when the weight is above 0.");
            spawn.integer("maxGroup", mob.maxGroup(), 0, null, "Largest group size. Not below minGroup.");
            entry.bool("burnsInSunlight", mob.burns(), "Set true to let the mob burn in daylight.");
            entry.bool("canBeWithered", false, "Set true to let the wither effect hurt this mob.");
            entry.multiplier("health", "Health factor. 1.0 keeps the default. Minimum 0.05. A negative value resets to 1.0.");
            entry.multiplier("damage", "Attack damage factor. 1.0 keeps the default. Minimum 0.05. A negative value resets to 1.0.");
            entry.multiplier("speed", "Movement speed factor. 1.0 keeps the default. Minimum 0.05. A negative value resets to 1.0.");
        }
    }

    private void buildHordes() {
        Section hordes = root.section("hordes", "");
        hordes.bool("enabled", true, "Set false to turn the horde spawn off. Peaceful, the doMobSpawning rule and spawn-monsters=false also stop hordes.");
        hordes.integer("spawnAttempts", 40, 0, 256, "Number of spawn tries per horde. Range 0 to 256.");
        hordes.integer("intervalTicks", 24000, 0, null, "Ticks between two horde checks. 24000 ticks are one day.");
        hordes.integer("randomExtraTicks", 1200, 0, null, "Random extra ticks added to the interval.");
        hordes.real("chancePerCheck", 0.2D, 0.0D, 1.0D, "Chance from 0 to 1 that a check starts a horde.");
        hordes.integer("minDistance", 24, 1, null, "Smallest distance from the player in blocks. Minimum 1.");
        hordes.integer("maxDistance", 48, 1, null, "Largest distance from the player in blocks. Not below minDistance.");
        hordes.bool("nightOnly", true, "Set true to start hordes only at night.");
    }

    private void buildChampion() {
        Section champion = root.section("corruptedChampion", "");

        Section summoning = champion.section("summoning", "");
        summoning.bool("needsStaffFragments", true, "Set true to require staff fragments at the altar.");
        summoning.strings("summonItems", List.of("item.minecraft.debug_stick"), "Items that summon the boss, as translation keys.");
        summoning.strings("bloodEntities", List.of("entity.minecraft.villager"), "Mobs whose death fills the dagger with blood, as translation keys.");
        summoning.integer("spawnInvulnerableTicks", 410, 200, null, "Ticks of invulnerability after the boss spawns. Minimum 200.");

        Section stats = champion.section("stats", "");
        stats.floatAbove("castingHealth", 400.0D, "Health in the casting phase. Must be above 0.");
        stats.floatAbove("huntingHealth", 200.0D, "Health in the hunting phase. Must be above 0.");
        stats.realAtLeast("castingDamage", 30.0D, "Attack damage in the casting phase. Minimum 0.");
        stats.real("huntingDamageBonus", 40.0D, null, null, "Attack damage added in the hunting phase.");
        stats.realAtLeast("armor", 14.0D, "Armor. Minimum 0.");
        stats.realAtLeast("armorToughness", 12.0D, "Armor toughness. Minimum 0.");
        stats.real("huntingSpeedBonus", 0.15D, null, null, "Movement speed added in the hunting phase.");

        Section spells = champion.section("spells", "");
        spells.integer("huntDuration", 800, 1, null, "Duration of the hunting phase in ticks. Minimum 1.");
        spells.integer("corpseDuration", 400, 1, null, "Duration of the falling corpse spell in ticks. Minimum 1.");
        spells.integer("corpseCooldown", 400, 0, null, "Cooldown of the falling corpse spell in ticks. Minimum 0.");
        spells.integer("healingDuration", 700, 1, null, "Duration of the healing spell in ticks. Minimum 1.");
        spells.integer("healingCooldown", 600, 0, null, "Cooldown of the teleport and heal spell in ticks. Minimum 0.");
        spells.floating("healThresholdHealth", 300.0D, null, null, "The boss casts the healing spell below this health.");
        spells.integer("levitationDuration", 150, 1, null, "Duration of the levitation spell in ticks. Minimum 1.");
        spells.integer("levitationCooldown", 400, 0, null, "Cooldown of the levitation spell in ticks. Minimum 0.");
        spells.integer("playerTeleportYOffset", -15, null, null, "Y offset in blocks for players teleported by the healing spell.");
        spells.integer("maxSkulls", 5, 1, 64, "Largest number of skulls per skull spell. Range 1 to 64.");
        spells.floating("skullExplosionRadius", 2.0D, 0.0D, 16.0D, "Explosion radius of a skull. Range 0 to 16.");
        spells.bool("skullsDestroyBlocks", false, "Set true to let skull explosions break blocks.");

        Section fight = champion.section("fight", "");
        fight.bool("multiphase", true, "Set false to skip the phase change of the fight.");
        fight.bool("invulnerableDuringSpells", true, "Set true to make the boss invulnerable during spells.");
        fight.integer("phaseInvulnerableTicks", 200, 0, null, "Ticks of invulnerability at a phase change. Minimum 0.");
        fight.integer("huntCooldown", 600, 200, null, "Cooldown of the hunt in ticks. Minimum 200.");
        fight.integer("chargeCooldown", 160, 40, null, "Cooldown of the charge attack in ticks. Minimum 40.");
        fight.integer("pounceCooldown", 120, 40, null, "Cooldown of the pounce attack in ticks. Minimum 40.");
        fight.integer("maxSummonedMobs", 30, 0, 256, "Largest number of mobs the boss keeps alive. Range 0 to 256.");
        fight.integer("maxSummonGroupSize", 5, 1, 64, "Largest group size per summon. Range 1 to 64.");
        fight.integer("ghoulSpawnTimer", 6000, 0, null, "Ghoul spawn timer in ticks. Minimum 0.");

        Section effects = champion.section("effects", "");
        effects.bool("darkenSky", true, "Set true to darken the sky during the fight.");
        effects.bool("thickenFog", true, "Set true to thicken the fog during the fight.");
        effects.floating("musicVolume", 1.0D, 0.0D, 4.0D, "Volume of the boss music. Range 0 to 4. Client setting.");
    }

    private void buildChronicle() {
        Section chronicle = root.section("chronicle", "");
        chronicle.bool("inventoryButton", true, "Show the chronicle button in the inventory. Client setting.");
        chronicle.bool("toasts", true, "Show a toast for each found chronicle page. Client setting.");
        chronicle.bool("pagesInLoot", true, "Add chronicle pages to chest loot.");
    }

    private void buildBehaviour() {
        Section behaviour = root.section("behaviour", "");
        behaviour.bool("magpiesStealItems", true, "Let magpies pick up items.");
        behaviour.real("magpieTheftChance", 0.1D, 0.0D, 1.0D, "Chance per check that a magpie goes for an item. Range 0 to 1.");
        behaviour.bool("crowsEatCrops", true, "Let crows eat crops.");
        behaviour.bool("ghoulingCollectsItems", true, "Let a ghouling with a coffin pick up items.");
        behaviour.bool("wraithGriefing", true, "Let wraiths put out torches and lights.");
        behaviour.bool("smolderingBlocksInVillages", true, "Generate smoldering blocks in overrun villages.");
        behaviour.bool("skeletonCreeperDestroysBlocks", false, "Let the skeleton creeper explosion break blocks.");
        behaviour.real("skeletonCreeperExplosionRadius", 3.5D, 0.0D, 16.0D, "Explosion radius of the skeleton creeper. Range 0 to 16.");
        behaviour.bool("urnDoubleInventory", true, "Give urns a double chest inventory.");
        behaviour.bool("replaceWitherSkeletonSpawner", false, "Replace the wither skeleton spawner in the large graveyard with a skeleton spawner.");

        Section fog = behaviour.section("fogParticles", "Fog particles that rise from moss.");
        fog.bool("enabled", true, "Set false to turn the fog particles off. Client setting.");
        fog.integer("rarity", 50, 1, 100_000, "Higher numbers mean rarer particles. Range 1 to 100000. Client setting.");

        Section offers = behaviour.section("namelessHangedOffers", "Number of trades the nameless hanged offers.");
        offers.integer("min", 7, 1, null, "Fewest trades. Minimum 1.");
        offers.integer("max", 9, 1, null, "Most trades. Not below min.");
    }

    private static String sentence(String id) {
        String text = id.replace('_', ' ');
        return Character.toUpperCase(text.charAt(0)) + text.substring(1) + ".";
    }

    private record MobDefaults(String id, boolean enabled, int weight, int minGroup, int maxGroup, boolean burns) {
    }

    public enum Kind {
        BOOL,
        INT,
        DOUBLE,
        FLOAT,
        MULTIPLIER,
        STRING_LIST
    }

    public record Coerced(Object value, boolean exact) {
    }

    public abstract static class Node {
        private final String name;
        private final String path;
        private final String comment;

        Node(String name, String path, String comment) {
            this.name = name;
            this.path = path;
            this.comment = comment;
        }

        public String name() {
            return name;
        }

        public String path() {
            return path;
        }

        public String comment() {
            return comment;
        }
    }

    public static final class Section extends Node {
        private final List<Node> children = new ArrayList<>();
        private final boolean table;

        Section(String name, String path, String comment, boolean table) {
            super(name, path, comment);
            this.table = table;
        }

        public List<Node> children() {
            return children;
        }

        public boolean isTable() {
            return table;
        }

        Section section(String name, String comment) {
            Section section = new Section(name, childPath(name), comment, false);
            children.add(section);
            return section;
        }

        Section table(String name) {
            Section section = new Section(name, childPath(name), "", true);
            children.add(section);
            return section;
        }

        void bool(String name, boolean defaultValue, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.BOOL, defaultValue, null, null, false, false));
        }

        void integer(String name, int defaultValue, Integer lower, Integer upper, String comment) {
            add(new Leaf(
                    name,
                    childPath(name),
                    comment,
                    Kind.INT,
                    defaultValue,
                    lower == null ? null : lower.doubleValue(),
                    upper == null ? null : upper.doubleValue(),
                    false,
                    false
            ));
        }

        void real(String name, double defaultValue, Double lower, Double upper, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.DOUBLE, defaultValue, lower, upper, false, false));
        }

        void realAtLeast(String name, double defaultValue, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.DOUBLE, defaultValue, 0.0D, null, true, false));
        }

        void floating(String name, double defaultValue, Double lower, Double upper, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.FLOAT, defaultValue, lower, upper, false, false));
        }

        void floatAbove(String name, double defaultValue, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.FLOAT, defaultValue, 0.0D, null, true, true));
        }

        void multiplier(String name, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.MULTIPLIER, 1.0D, MIN_MULTIPLIER, null, false, false));
        }

        void strings(String name, List<String> defaultValue, String comment) {
            add(new Leaf(name, childPath(name), comment, Kind.STRING_LIST, List.copyOf(defaultValue), null, null, false, false));
        }

        private void add(Leaf leaf) {
            children.add(leaf);
        }

        private String childPath(String childName) {
            return path().isEmpty() ? childName : path() + "." + childName;
        }
    }

    public static final class Leaf extends Node {
        private final Kind kind;
        private final Object defaultValue;
        private final Double lower;
        private final Double upper;
        private final boolean reject;
        private final boolean lowerExclusive;

        Leaf(
                String name,
                String path,
                String comment,
                Kind kind,
                Object defaultValue,
                Double lower,
                Double upper,
                boolean reject,
                boolean lowerExclusive
        ) {
            super(name, path, comment);
            this.kind = kind;
            this.defaultValue = defaultValue;
            this.lower = lower;
            this.upper = upper;
            this.reject = reject;
            this.lowerExclusive = lowerExclusive;
        }

        public Kind kind() {
            return kind;
        }

        public Object defaultValue() {
            return defaultValue;
        }

        public Coerced coerce(JsonElement element) {
            if (!element.isJsonPrimitive() && kind != Kind.STRING_LIST) {
                return invalid();
            }

            return switch (kind) {
                case BOOL -> coerceBool(element);
                case INT -> coerceNumber(element, true);
                case DOUBLE, FLOAT -> coerceNumber(element, false);
                case MULTIPLIER -> coerceMultiplier(element);
                case STRING_LIST -> coerceStrings(element);
            };
        }

        private Coerced coerceBool(JsonElement element) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            return primitive.isBoolean() ? new Coerced(primitive.getAsBoolean(), true) : invalid();
        }

        private Coerced coerceNumber(JsonElement element, boolean integral) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (!primitive.isNumber()) {
                return invalid();
            }

            double value = primitive.getAsDouble();

            if (!Double.isFinite(value)) {
                return invalid();
            }

            if (integral && (value != Math.rint(value) || value > Integer.MAX_VALUE || value < Integer.MIN_VALUE)) {
                return invalid();
            }

            if (kind == Kind.FLOAT) {
                float narrowed = (float) value;

                if (!Float.isFinite(narrowed)) {
                    return invalid();
                }

                value = narrowed;
            }

            return bounded(value);
        }

        public Coerced coerceFloored(JsonElement element) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
                return invalid();
            }

            double value = element.getAsDouble();

            if (!Double.isFinite(value)) {
                return invalid();
            }

            return bounded((int) value);
        }

        private Coerced bounded(double value) {
            boolean below = lower != null && (lowerExclusive ? value <= lower : value < lower);
            boolean above = upper != null && value > upper;

            if ((below || above) && reject) {
                return invalid();
            }

            double result = value;

            if (below) {
                result = lower;
            } else if (above) {
                result = upper;
            }

            return new Coerced(box(result), !below && !above);
        }

        private Object box(double value) {
            return switch (kind) {
                case INT -> (int) value;
                case FLOAT -> Double.parseDouble(Float.toString((float) value));
                default -> value;
            };
        }

        private Coerced coerceMultiplier(JsonElement element) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (!primitive.isNumber() || !Double.isFinite(primitive.getAsDouble()) || primitive.getAsDouble() < 0.0D) {
                return invalid();
            }

            return bounded(primitive.getAsDouble());
        }

        private Coerced coerceStrings(JsonElement element) {
            if (!element.isJsonArray()) {
                return invalid();
            }

            List<String> result = new ArrayList<>();

            for (JsonElement item : element.getAsJsonArray()) {
                if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                    return invalid();
                }

                result.add(item.getAsString());
            }

            return new Coerced(List.copyOf(result), true);
        }

        private Coerced invalid() {
            return new Coerced(defaultValue, false);
        }
    }
}
