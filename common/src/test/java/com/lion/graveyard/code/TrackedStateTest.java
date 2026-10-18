package com.lion.graveyard.code;

import com.lion.graveyard.support.Floors;
import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.MobAnimations;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Source-level guards for state that the server and the client must see the same way.
class TrackedStateTest {
    private static final Pattern SPAWN_CALL = Pattern.compile("\\.addFreshEntity(?:WithPassengers)?\\(\\s*(\\w+)\\s*\\)");
    private static final Pattern SPAWN_ANY = Pattern.compile("\\.addFreshEntity(?:WithPassengers)?\\(");
    private static final Pattern SPAWNED_VARIABLE = Pattern.compile("^\\s*(?:\\(\\s*[\\w.<>]+\\s*\\)\\s*)?(\\w+)\\s*$");
    private static final String SPAWN_STATE_SETTERS = "onSummoned|startSpawnAnimation|prepareSpawn|finalizeSpawn|set\\w*|addEffect";
    private static final Pattern STALE_TARGET_FLAG = Pattern.compile("entityData\\s*\\.\\s*set\\(\\s*\\w*(?:CHAS|RUN|HUNT|WANDER|NAVIG|PURSU)\\w*|\\bset(?:Chas|Runn|Hunt|Wander|Pursu)\\w*\\(");
    private static final Pattern RANDOM = Pattern.compile("\\brandom\\b|\\bgetRandom\\(|Math\\.random|randomUUID|\\bnext(?:Int|Float|Double|Boolean|Long|Gaussian)\\(");
    private static final Pattern TARGET_CALL = Pattern.compile("\\bgetTarget\\(\\)");
    private static final Pattern FIELD_INITIALIZER = Pattern.compile("(?m)^ {4}(?=\\S)([^\\n;=(){}]+?)\\s+(\\w+)\\s*=\\s*([^;]*);");
    private static final Pattern FIELD =Pattern.compile("(?m)^ {4}(?=\\S)([^\\n;=(){}]+?)\\s+(\\w+)\\s*(?:=[^;]*)?;");
    private static final Pattern NOT_INSTANCE_FIELD = Pattern.compile("\\b(?:static|final|return|import|package|abstract)\\b");

    private static final Set<String> SPAWN_SITE_FILES = Set.of(
            "blocks/AltarBlock.java",
            "blocks/SarcophagusBlock.java",
            "item/BoneStaffItem.java",
            "entities/LichEntity.java",
            "entities/horde/GraveyardHordeSpawner.java",
            "util/SpawnHordeCommand.java"
    );

    /// Reason per entry. A field listed here is read by a table although the server does not track it.
    private static final Map<String, String> UNTRACKED_READS_ALLOWED = Map.of();

    // ---------------------------------------------------------------- check 8: spawn after state

    static List<String> spawnOrderProblems(String code) {
        List<String> problems = new ArrayList<>();
        List<JavaText.Span> spans = JavaText.methodSpans(code);
        Matcher spawn = SPAWN_ANY.matcher(code);
        while (spawn.find()) {
            int open = spawn.end() - 1;
            Matcher named = SPAWNED_VARIABLE.matcher(JavaText.inside(code, open));

            if (!named.matches()) {
                continue;
            }

            String variable = named.group(1);
            int at = JavaText.closing(code, open) + 1;
            JavaText.Span method = spans.stream()
                    .filter(s -> s.contains(spawn.start()))
                    .reduce((a, b) -> a.end() - a.start() <= b.end() - b.start() ? a : b)
                    .orElse(null);

            if (method == null) {
                continue;
            }

            String after = code.substring(at, method.end());
            Matcher late = Pattern.compile("\\b" + variable + "\\s*\\.\\s*(" + SPAWN_STATE_SETTERS + ")\\s*\\(").matcher(after);
            while (late.find()) {
                problems.add(variable + "." + late.group(1) + "() runs after the spawn in " + method.name());
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> mobStateIsSetBeforeTheEntityIsSpawned() {
        return Floors.atLeast(
                5,
                "files that spawn entities",
                ModProject.files(ModProject.JAVA, ".java").stream()
                        .filter(file -> SPAWN_CALL.matcher(Sources.code(file)).find())
                        .map(file -> DynamicTest.dynamicTest(ModProject.display(file), () -> assertEquals(List.of(), spawnOrderProblems(Sources.code(file)))))
        );
    }

    @Test
    void knownSpawnSitesAreAllScanned() {
        Set<String> scanned = new TreeSet<>();
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            if (SPAWN_CALL.matcher(Sources.code(file)).find()) {
                scanned.add(ModProject.JAVA.relativize(file).toString().replace('\\', '/'));
            }
        }
        Set<String> missing = new TreeSet<>(SPAWN_SITE_FILES);
        missing.removeAll(scanned);
        assertEquals(Set.of(), missing);
    }

    @Test
    void spawnOrderCheckCatchesLateState() {
        String late = "void a() { Foo f = make(); world.addFreshEntity(f); f.onSummoned(); }";
        String lateEffect = "void a() { world.addFreshEntity(lich); lich.addEffect(x); lich.setInvisible(true); }";
        String fine = "void a() { f.onSummoned(); f.setSpawnTimer(3); world.addFreshEntityWithPassengers(f); f.playSpawnSounds(); }";
        String otherMethod = "void a() { world.addFreshEntity(f); } void b() { f.onSummoned(); }";
        String lateSetter = "void a() { Foo f = make(); world.addFreshEntity(f); f.setTarget(player); }";
        String lateCast = "void a() { world.addFreshEntity((Foo) f); f.setVariant(2); }";
        String factory = "void a() { world.addFreshEntity(make()); other.setThing(1); }";
        assertEquals(1, spawnOrderProblems(lateSetter).size());
        assertEquals(1, spawnOrderProblems(lateCast).size());
        assertEquals(List.of(), spawnOrderProblems(factory));
        assertEquals(1, spawnOrderProblems(late).size());
        assertEquals(2, spawnOrderProblems(lateEffect).size());
        assertEquals(List.of(), spawnOrderProblems(fine));
        assertEquals(List.of(), spawnOrderProblems(otherMethod));
    }

    // ---------------------------------------------------------------- check 9: deterministic defaults

    private static List<String> defineArguments(String code) {
        List<String> all = new ArrayList<>(JavaText.callArguments(code, "entityData.define("));
        all.addAll(JavaText.callArguments(code, "builder.define("));
        return all;
    }

    static List<String> trackedDefaultProblems(String code) {
        List<String> problems = new ArrayList<>();
        for (String args : defineArguments(code)) {
            List<String> parts = JavaText.splitTopLevel(args, ',');

            if (parts.size() >= 2 && RANDOM.matcher(parts.get(1)).find()) {
                problems.add("entityData.define(" + args.trim() + ") has a random default");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> trackedDefaultsAreConstants() {
        return Floors.atLeast(
                12,
                "files that define tracked data",
                ModProject.files(ModProject.JAVA, ".java").stream()
                        .filter(file -> !defineArguments(Sources.code(file)).isEmpty())
                        .map(file -> DynamicTest.dynamicTest(ModProject.display(file), () -> assertEquals(List.of(), trackedDefaultProblems(Sources.code(file)))))
        );
    }

    @Test
    void trackedDefaultCheckCatchesRandomValues() {
        assertEquals(1, trackedDefaultProblems("entityData.define(VARIANT, (byte) (this.random.nextInt(8) + 1));").size());
        assertEquals(1, trackedDefaultProblems("entityData.define(ID, UUID.randomUUID());").size());
        assertEquals(List.of(), trackedDefaultProblems("entityData.define(VARIANT, (byte) 0); entityData.define(T, SPAWN_TICKS);"));
    }

    static List<String> initDataTrackerProblems(String code) {
        List<String> problems = new ArrayList<>();
        for (String body : JavaText.methods(code).getOrDefault("defineSynchedData", List.of())) {
            if (RANDOM.matcher(body).find()) {
                problems.add("defineSynchedData draws a random value");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> initDataTrackerDrawsNoRandomValues() {
        return Floors.atLeast(
                12,
                "files that define synched data",
                ModProject.files(ModProject.JAVA, ".java").stream()
                        .filter(file -> Sources.code(file).contains("defineSynchedData"))
                        .map(file -> DynamicTest.dynamicTest(ModProject.display(file), () -> assertEquals(List.of(), initDataTrackerProblems(Sources.code(file)))))
        );
    }

    @Test
    void initDataTrackerCheckCatchesRandomValues() {
        String bad = "protected void defineSynchedData() { super.defineSynchedData(); int v = this.random.nextInt(4); this.entityData.define(V, (byte) v); }";
        String viaHelper = "protected void defineSynchedData() { this.entityData.define(ID, UUID.randomUUID()); }";
        String fine = "protected void defineSynchedData() { super.defineSynchedData(); this.entityData.define(V, (byte) 0); } void tick() { random.nextInt(3); }";
        assertEquals(1, initDataTrackerProblems(bad).size());
        assertEquals(1, initDataTrackerProblems(viaHelper).size());
        assertEquals(List.of(), initDataTrackerProblems(fine));
    }

    // ---------------------------------------------------------------- check 10: table reads are tracked

    static Set<String> plainFields(String code) {
        Set<String> names = new TreeSet<>();
        Matcher m = FIELD.matcher(code);
        while (m.find()) {
            if (!NOT_INSTANCE_FIELD.matcher(m.group(1)).find()) {
                names.add(m.group(2));
            }
        }
        return names;
    }

    static List<String> untrackedReads(String code) {
        Map<String, List<String>> methods = JavaText.methods(code);
        Set<String> fields = plainFields(code);
        List<String> problems = new ArrayList<>();
        List<String> tables = new ArrayList<>(JavaText.callArguments(code, ".when("));
        tables.addAll(JavaText.callArguments(code, ".stopWhen("));
        for (String table : tables) {
            scanForFields(table, methods, fields, new HashSet<>(), 0, problems);
        }
        return problems.stream()
                .distinct()
                .toList();
    }

    private static void scanForFields(
            String text,
            Map<String, List<String>> methods,
            Set<String> fields,
            Set<String> visited,
            int depth,
            List<String> problems
    ) {
        for (String name : JavaText.identifiers(text)) {
            if (fields.contains(name)) {
                problems.add(name);
            }
        }

        if (depth >= 3) {
            return;
        }

        Matcher called = Pattern.compile("(?<![\\w.])(?:this\\.)?(\\w+)\\s*\\(|this::(\\w+)").matcher(text);
        while (called.find()) {
            String method = called.group(1) != null ? called.group(1) : called.group(2);

            if (methods.containsKey(method) && visited.add(method)) {
                scanForFields(String.join("\n", methods.get(method)), methods, fields, visited, depth + 1, problems);
            }
        }
    }

    @TestFactory
    Stream<DynamicTest> tablesReadOnlyTrackedState() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> {
                    Set<String> reads = new TreeSet<>(untrackedReads(mob.code()));
                    reads.removeIf(field -> UNTRACKED_READS_ALLOWED.containsKey(mob.name() + "." + field));
                    assertEquals(Set.of(), reads);
                }));
    }

    @Test
    void untrackedReadCheckCatchesDirectAndIndirectReads() {
        String direct = "class E {\n    private int spawnTimer = 20;\n    void c() { x.when(() -> spawnTimer > 0, A); }\n}";
        String indirect = "class E {\n    private int spawnTimer = 20;\n    boolean isSpawning() { return this.spawnTimer > 0; }\n    void c() { x.when(this::isSpawning, A).when(() -> isSpawning(), B); }\n}";
        String tracked = "class E {\n    private final Cache cache = make();\n    boolean isSpawning() { return entityData.get(T) > 0; }\n    void c() { x.when(this::isSpawning, A); }\n}";
        assertEquals(List.of("spawnTimer"), untrackedReads(direct));
        assertEquals(List.of("spawnTimer"), untrackedReads(indirect));
        assertEquals(List.of(), untrackedReads(tracked));
    }

    /// Reason per entry, keyed Class.method. The client does not know the target, so a table must not read it.
    private static final Map<String, String> TARGET_READS_ALLOWED = Map.of();

    static List<String> targetReadProblems(String code) {
        Map<String, List<String>> methods = JavaText.methods(code);
        List<String> roots = new ArrayList<>();
        for (String prefix : List.of(".when(", ".stopWhen(", ".transitionTicks(", ".startAt(")) {
            roots.addAll(JavaText.callArguments(code, prefix));
        }
        List<String> problems = new ArrayList<>();
        for (String root : roots) {
            if (TARGET_CALL.matcher(JavaText.reachableText(root, methods, 3)).find()) {
                problems.add("a table reads getTarget(), which the client does not know: " + root.strip().replaceAll("\\s+", " "));
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> tablesNeverReadTheTarget() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> {
                    if (!TARGET_READS_ALLOWED.containsKey(mob.name())) {
                        assertEquals(List.of(), targetReadProblems(mob.code()));
                    }
                }));
    }

    @Test
    void targetReadCheckCatchesDirectAndIndirectReads() {
        String direct = "void c() { x.when(() -> getTarget() != null, A); }";
        String indirect = "boolean hunting() { return this.getTarget() != null; } void c() { x.when(this::hunting, A); }";
        String deeper = "boolean aware() { return getTarget() == null; } boolean hunting() { return !aware(); } void c() { x.transitionTicks(() -> hunting() ? 0 : 5); }";
        String tracked = "boolean hunting() { return entityData.get(CHASING); } void c() { x.when(this::hunting, A); } void goal() { getTarget(); }";
        assertEquals(1, targetReadProblems(direct).size());
        assertEquals(1, targetReadProblems(indirect).size());
        assertEquals(1, targetReadProblems(deeper).size());
        assertEquals(List.of(), targetReadProblems(tracked));
    }

    // ---------------------------------------------------------------- stale target

    /// Reason per entry, keyed Class.method. A tracked chase flag set from getTarget() without ChaseTarget.isLive.
    private static final Map<String, String> STALE_TARGET_ALLOWED = Map.of();

    static List<String> staleTargetProblems(String className, String code) {
        List<String> problems = new ArrayList<>();
        for (Map.Entry<String, List<String>> method : JavaText.methods(code).entrySet()) {
            String body = String.join("\n", method.getValue());
            boolean setsFlag = STALE_TARGET_FLAG.matcher(body).find();

            if (setsFlag && TARGET_CALL.matcher(body).find() && !body.contains("ChaseTarget.isLive(")
                    && !STALE_TARGET_ALLOWED.containsKey(className + "." + method.getKey())) {
                problems.add(className + "." + method.getKey() + " sets a tracked chase flag from getTarget() without ChaseTarget.isLive");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> trackedChaseFlagsGoThroughTheLiveTargetCheck() {
        return Floors.atLeast(
                4,
                "files with chase flags",
                ModProject.files(MobAnimations.ENTITIES, ".java").stream()
                        .filter(file -> !file.toString().replace('\\', '/').contains("/entities/renders/")
                                && !file.toString().replace('\\', '/').contains("/entities/models/"))
                        .filter(file -> STALE_TARGET_FLAG.matcher(Sources.code(file)).find())
                        .map(file -> DynamicTest.dynamicTest(
                                file.getFileName().toString(),
                                () ->
                                        assertEquals(List.of(), staleTargetProblems(file.getFileName().toString().replace(".java", ""), Sources.code(file)))
                        ))
        );
    }

    @Test
    void staleTargetCheckCatchesAFlagFromARawTarget() {
        String raw = "void trackChase() { if (this.getTarget() != null) { this.entityData.set(CHASING, true); } }";
        String live = "void trackChase() { if (ChaseTarget.isLive(this)) { this.entityData.set(CHASING, true); } }";
        String setter = "void tick() { setChasing(getTarget() != null); }";
        String wander = "void customServerAiStep() { boolean n = getTarget() == null; entityData.set(WANDERING, n); }";
        String unrelated = "void tick() { entityData.set(VARIANT, getTarget() == null ? 1 : 2); }";
        assertEquals(1, staleTargetProblems("E", raw).size());
        assertEquals(List.of(), staleTargetProblems("E", live));
        assertEquals(1, staleTargetProblems("E", setter).size());
        assertEquals(1, staleTargetProblems("E", wander).size());
        assertEquals(List.of(), staleTargetProblems("E", unrelated));
    }

    // ---------------------------------------------------------------- check 13: constructors

    static List<String> constructorRandomProblems(String className, String code) {
        List<String> problems = new ArrayList<>();
        for (String body : JavaText.methods(code).getOrDefault(className, List.of())) {
            if (RANDOM.matcher(body).find()) {
                problems.add(className + " constructor draws a random value");
            }
        }
        return problems;
    }

    private static List<String> entityClassNames() {
        List<String> names = new ArrayList<>(MobAnimations.declaredMobNames());
        names.addAll(List.of("CrowEntity", "MagpieEntity", "CorruptedPillager", "CorruptedVindicator", "CorruptedEvoker"));
        return names;
    }

    @TestFactory
    Stream<DynamicTest> geckoLibEntityConstructorsDrawNoRandomValues() {
        return entityClassNames().stream()
                .map(name -> DynamicTest.dynamicTest(
                        name,
                        () ->
                                assertEquals(List.of(), constructorRandomProblems(name, Sources.code(MobAnimations.ENTITIES.resolve(name + ".java"))))
                ));
    }

    @Test
    void constructorCheckCatchesRandomValues() {
        String bad = "class Corpse { Corpse(Level w) { super(w); this.yaw = this.random.nextFloat() * 360; } void tick() { random.nextInt(3); } }";
        String fine = "class Corpse { Corpse(Level w) { super(w); this.yaw = 0; } void tick() { random.nextInt(3); } }";
        assertEquals(1, constructorRandomProblems("Corpse", bad).size());
        assertEquals(List.of(), constructorRandomProblems("Corpse", fine));
    }

    static List<String> fieldInitializerRandomProblems(String className, String code) {
        List<String> problems = new ArrayList<>();
        Matcher m = FIELD_INITIALIZER.matcher(code);
        while (m.find()) {
            boolean isStatic = Pattern.compile("\\bstatic\\b").matcher(m.group(1)).find();

            if (!isStatic && RANDOM.matcher(m.group(3)).find()) {
                problems.add(className + "." + m.group(2) + " draws a random value in its initializer");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> geckoLibEntityFieldInitializersDrawNoRandomValues() {
        return entityClassNames().stream()
                .map(name -> DynamicTest.dynamicTest(
                        name,
                        () ->
                                assertEquals(List.of(), fieldInitializerRandomProblems(name, Sources.code(MobAnimations.ENTITIES.resolve(name + ".java"))))
                ));
    }

    @Test
    void fieldInitializerCheckCatchesRandomValues() {
        String bad = "class Corpse {\n    private float spin = this.random.nextFloat() * 360;\n    private int age = 0;\n}";
        String multiline = "class Corpse {\n    private final int wait = 20\n            + random.nextInt(40);\n}";
        String fine = "class Corpse {\n    private int age = 0;\n    private static final RandomSource SHARED = RandomSource.create();\n    void tick() { random.nextInt(3); }\n}";
        assertEquals(1, fieldInitializerRandomProblems("Corpse", bad).size());
        assertEquals(1, fieldInitializerRandomProblems("Corpse", multiline).size());
        assertEquals(List.of(), fieldInitializerRandomProblems("Corpse", fine));
    }
}
