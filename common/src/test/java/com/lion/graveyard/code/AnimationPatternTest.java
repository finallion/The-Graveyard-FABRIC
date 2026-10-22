package com.lion.graveyard.code;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lion.graveyard.support.Json;
import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.MobAnimations;
import com.lion.graveyard.support.MobAnimations.Clip;
import com.lion.graveyard.support.MobAnimations.FileLoop;
import com.lion.graveyard.support.MobAnimations.Loop;
import com.lion.graveyard.support.MobAnimations.Mob;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimationPatternTest {
    private static final Pattern GEO_IMPLEMENTER = Pattern.compile("implements[^{]*\\bGeoEntity\\b");
    private static final Pattern TRIGGERABLE = Pattern.compile("triggerableAnim\\(\\s*\"[^\"]*\"\\s*,\\s*(\\w+)");
    private static final Pattern ZERO_WHILE = Pattern.compile("^(.*?)\\?\\s*0\\s*:", Pattern.DOTALL);
    private static final Pattern SPAWN_WORD = Pattern.compile("(?i)spawn");
    private static final Pattern SPAWN_OFFSET = Pattern.compile("(?is)(?:\\b|get)SPAWN\\w*\\s*(?:\\(\\))?\\s*-\\s*[^,;]*timer");
    private static final Pattern START_AT_SPAWN = Pattern.compile("\\.startAt\\(\\s*SPAWN\\w*");
    private static final Pattern CURRENT_CLIP = Pattern.compile("(?i)current|previous|prior|came");
    private static final Pattern BARE_ZERO = Pattern.compile("(?<![\\w.])0(?![\\w.])");
    private static final Pattern TIMER_CONSTANT = Pattern.compile("\\bfinal\\s+int\\s+(\\w+_(?:TICKS|DURATION))\\s*=");
    private static final Pattern IF_HEAD = Pattern.compile("\\bif\\s*\\(");
    private static final double SPIN_LIMIT_DEGREES = 90.0D;
    private static final Pattern FIRST_FRAME_NULL_CHECK = Pattern.compile("getCurrentAnimationPoint\\(\\)\\s*==\\s*null");
    private static final Pattern ZERO_BRANCH = Pattern.compile("\\?\\s*0\\s*:");
    private static final Pattern DEATH_REMOVAL = Pattern.compile("deathTime\\s*(?:==|>=)\\s*(\\w+)");
    private static final Pattern SOUND_METHOD = Pattern.compile("\\w*Sound|\\w*AnimationSound\\w*");
    private static final Pattern FORBIDDEN_STATE_SOURCE = Pattern.compile("\\bisAggressive\\(\\)|\\bgetNavigation\\(\\)\\s*\\.\\s*isDone\\(\\)");

    /// Each entry gives the reason that the state source is safe.
    private static final Map<String, String> STATE_SOURCE_ALLOWED = Map.of(
            "GhoulingEntity.when:isAggressive", "The ghouling melee goal never pauses on an idle path, so the flag does not flicker.",
            "TormentedVillagerEntity.getAmbientSound:isDone", "The server picks the ambient sound once per trigger. No clip follows it.",
            "GhoulEntity.NAVIGATING:isDone", "The table ORs the flag with limb swing. A path restart cannot drop the walk clip while the limbs still swing."
    );

    private static final Map<String, Set<String>> UNRESET_CHANNELS_ALLOWED = Map.of(
            "FallingCorpse", Set.of("body.position", "corpse.position", "head.position", "left_arm.position")
    );

    record TimerRule(String mob, String constant, String clip, int entryBlend) {
    }

    private static final List<TimerRule> TIMER_RULES = List.of(
            new TimerRule("AnimatedCorruptedIllager", "SPAWN_TICKS", "spawn", 0),
            new TimerRule("AnimatedCorruptedIllager", "GLITCH_TICKS", "glitch", MobAnimations.TRANSITION_TICKS),
            new TimerRule("AnimatedCorruptedIllager", "STRIKE_TICKS", "strike", MobAnimations.TRANSITION_TICKS),
            new TimerRule("AnimatedCorruptedIllager", "BITE_TICKS", "bite", MobAnimations.TRANSITION_TICKS),
            new TimerRule("GhoulEntity", "SPAWN_ANIMATION_DURATION", "spawn", 0),
            new TimerRule("GhoulEntity", "ATTACK_ANIM_SYNC_TICKS", "attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("GhoulingEntity", "SPAWN_ANIMATION_DURATION", "spawn", 0),
            new TimerRule("GhoulingEntity", "ATTACK_ANIMATION_DURATION", "attack", 0),
            new TimerRule("GhoulingEntity", "ATTACK_ANIM_SYNC_TICKS", "attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("NightmareEntity", "ATTACK_ANIM_SYNC_TICKS", "attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("RevenantEntity", "ATTACK_ANIM_SYNC_TICKS", "attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("RevenantEntity", "REANIMATE_DURATION", "reanimate", MobAnimations.TRANSITION_TICKS),
            new TimerRule("FallingCorpse", "LANDING_TICKS", "landing", 2),
            new TimerRule("FallingCorpse", "SINK_START_TICKS", "despawn", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "SPAWN_CLIP_TICKS", "spawn", 0),
            new TimerRule("LichEntity", "ATTACK_ANIMATION_DURATION", "attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "START_PHASE_TWO_ANIMATION_DURATION", "phase_two", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "START_PHASE_THREE_ANIMATION_DURATION", "phase_three", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "CHARGE_TICKS", "phase_two_charge", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "POUNCE_TICKS", "crawl_pounce", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "CRAWL_STRIKE_TICKS", "crawl_attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("LichEntity", "PHASE_TWO_STRIKE_TICKS", "phase_two_attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("NightmareEntity", "ATTACK_ANIMATION_DURATION", "attack", 0),
            new TimerRule("ReaperEntity", "SPAWN_TICKS", "spawn", 0),
            new TimerRule("ReaperEntity", "ATTACK_TICKS", "attack", MobAnimations.TRANSITION_TICKS),
            new TimerRule("RevenantEntity", "ATTACK_ANIMATION_DURATION", "attack", 0),
            new TimerRule("WraithEntity", "SPAWN_TICKS", "spawn", 0)
    );

    private static final TimerRule GHOUL_RAGE = new TimerRule("GhoulEntity", "RAGE_ANIMATION_DURATION", "rage", MobAnimations.TRANSITION_TICKS);

    @Test
    void everyGeckoLibEntityIsCoveredByTheMobList() {
        Set<String> found = new TreeSet<>();
        for (Path file : ModProject.files(MobAnimations.ENTITIES, ".java")) {
            if (file.getParent().equals(MobAnimations.ENTITIES) && GEO_IMPLEMENTER.matcher(Sources.code(file)).find()) {
                found.add(file.getFileName().toString().replace(".java", ""));
            }
        }
        assertEquals(new TreeSet<>(MobAnimations.declaredMobNames()), found);
    }

    static List<String> controllerHandlerProblems(String code) {
        List<String> problems = new ArrayList<>();
        for (String args : JavaText.callArguments(code, "AnimationClock.controller(")) {
            int at = args.indexOf("setTransitionTicks(");

            if (at < 0) {
                problems.add("handler never calls setTransitionTicks");
                continue;
            }

            String statement = args.substring(at, args.indexOf(';', at));
            String before = args.substring(0, at);

            if (braceDepth(before) != 1) {
                problems.add("setTransitionTicks is not set on every frame: " + statement);
            }

            if (!FIRST_FRAME_NULL_CHECK.matcher(before + statement).find()
                    || !ZERO_BRANCH.matcher(statement).find()) {
                problems.add("setTransitionTicks is not 0 while getCurrentAnimationPoint() is null: " + statement);
            }
        }
        return problems;
    }

    private static int braceDepth(String text) {
        int depth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '"') {
                i = text.indexOf('"', i + 1);
            } else if (c == '{') {
                depth++;
            } else if (c == '}') {
                depth--;
            }
        }
        return depth;
    }

    @Test
    void everyClockControllerCutsToTheFirstFrame() {
        int controllers = 0;
        List<String> problems = new ArrayList<>();
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            String code = Sources.code(file).replaceAll("AnimationClock\\.<[^>]*>controller\\(", "AnimationClock.controller(");
            int count = JavaText.callArguments(code, "AnimationClock.controller(").size();
            controllers += count;
            controllerHandlerProblems(code).forEach(p -> problems.add(ModProject.display(file) + ": " + p));
        }
        assertTrue(controllers >= 2, "AnimationTable and the tormented villager were not found");
        assertEquals(List.of(), problems);
    }

    @Test
    void geckoLibControllersOutsideTheClockUseNoBlend() {
        for (Path file : ModProject.files(ModProject.JAVA, ".java")) {
            String code = Sources.code(file);

            if (file.getFileName().toString().equals("AnimationClock.java")) {
                continue;
            }

            for (String args : JavaText.callArguments(code, "new AnimationController<>(")) {
                assertEquals("0", JavaText.splitTopLevel(args, ',').get(1).trim(), ModProject.display(file));
            }
        }
    }

    @Test
    void firstFrameCheckCatchesBrokenHandlers() {
        String good = "AnimationClock.controller(n, 5, state -> { state.controller().setTransitionTicks(state.controller().getCurrentAnimationPoint() == null ? 0 : 5); return X; })";
        String noNullCheck = "AnimationClock.controller(n, 5, state -> { state.controller().setTransitionTicks(5); return X; })";
        String nested = "AnimationClock.controller(n, 5, state -> { if (c) { state.controller().setTransitionTicks(state.controller().getCurrentAnimationPoint() == null ? 0 : 5); } return X; })";
        String missing = "AnimationClock.controller(n, 5, state -> { return X; })";
        assertEquals(List.of(), controllerHandlerProblems(good));
        assertFalse(controllerHandlerProblems(noNullCheck).isEmpty());
        assertFalse(controllerHandlerProblems(nested).isEmpty());
        assertFalse(controllerHandlerProblems(missing).isEmpty());
    }

    static List<String> spawnTableProblems(String code, boolean startAtRequired) {
        List<String> problems = new ArrayList<>();

        if (!cutsIntoSpawn(code)) {
            problems.add("no transitionTicks lambda that returns 0 while spawning");
        }

        if (startAtRequired) {
            if (!START_AT_SPAWN.matcher(code).find()) {
                problems.add("no startAt(SPAWN...) for a tracked spawn timer");
            } else if (JavaText.callArguments(code, ".startAt(").stream()
                    .noneMatch(args -> SPAWN_OFFSET.matcher(args).find())) {
                problems.add("startAt does not subtract the spawn timer from the spawn length");
            }
        }

        return problems;
    }

    private static boolean cutsIntoSpawn(String code) {
        Map<String, List<String>> methods = JavaText.methods(code);
        for (String args : JavaText.callArguments(code, ".transitionTicks(")) {
            Matcher zero = ZERO_WHILE.matcher(JavaText.reachableText(args, methods, 2));

            if (zero.find() && SPAWN_WORD.matcher(zero.group(1)).find()) {
                return true;
            }
        }
        return false;
    }

    private static List<Mob> mobsWithSpawnClip() {
        return MobAnimations.mobs().stream()
                .filter(mob -> MobAnimations.hasClip(mob.animations(), "spawn"))
                .toList();
    }

    @Test
    void spawnClipMobsWereFound() {
        assertTrue(mobsWithSpawnClip().size() >= 7, mobsWithSpawnClip().toString());
    }

    @TestFactory
    Stream<DynamicTest> spawnClipMobsBlendInstantlyAndResumeMidSpawn() {
        return mobsWithSpawnClip().stream()
                .filter(mob -> !mob.name().equals("TormentedVillagerEntity")
                        && !mob.name().equals("LichEntity"))
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> assertEquals(List.of(), spawnTableProblems(mob.code(), true))));
    }

    @Test
    void tormentedVillagerCutsIntoItsSpawnMood() {
        Mob mob = MobAnimations.mob("TormentedVillagerEntity");
        assertTrue(Pattern.compile("cut\\s*=\\s*mood\\s*==\\s*Mood\\.SPAWNING").matcher(mob.code()).find());
    }

    @Test
    void lichBlendsInstantlyIntoItsSpawn() {
        assertEquals(List.of(), spawnTableProblems(MobAnimations.mob("LichEntity").code(), false));
    }

    @Test
    void lichResumesMidSpawn() {
        assertEquals(List.of(), spawnTableProblems(MobAnimations.mob("LichEntity").code(), true));
    }

    @Test
    void spawnCheckCatchesMissingParts() {
        String good = ".transitionTicks(() -> isSpawning() ? 0 : 5).startAt(SPAWN_ANIMATION, e -> SPAWN_TICKS - e.getSpawnTimer())";
        assertEquals(List.of(), spawnTableProblems(good, true));
        assertEquals(2, spawnTableProblems(".when(a, B)", true).size());
        assertEquals(1, spawnTableProblems(".transitionTicks(() -> 5)", false).size());
        assertEquals(2, spawnTableProblems(".transitionTicks(() -> a ? 0 : 5)", true).size());
        assertEquals(1, spawnTableProblems(".transitionTicks(() -> blind ? 0 : 5)", false).size());
        assertEquals(1, spawnTableProblems(".transitionTicks(() -> isSpawning() ? 0 : 5).startAt(SPAWN_ANIMATION, e -> 0)", true).size());
        assertEquals(1, spawnTableProblems(".transitionTicks(() -> isSpawning() ? 0 : 5).startAt(SPAWN_ANIMATION, e -> e.getSpawnTimer())", true).size());
    }

    static List<String> stateSourceProblems(String owner, String code, boolean wholeFile) {
        List<String> problems = new ArrayList<>();
        Map<String, String> scopes = new LinkedHashMap<>();

        if (wholeFile) {
            scopes.put("file", code);
        } else {
            int index = 0;
            Map<String, List<String>> methods = JavaText.methods(code);
            for (String args : callArguments(code, ".when(", ".stopWhen(")) {
                scopes.put("when#" + index++, JavaText.reachableText(args, methods, 3));
            }
            for (Map.Entry<String, List<String>> method : methods.entrySet()) {
                if (SOUND_METHOD.matcher(method.getKey()).matches() && !method.getKey().startsWith("play")) {
                    scopes.put(method.getKey(), String.join("\n", method.getValue()));
                }
            }
        }

        scopes.forEach((scope, text) -> {
            Matcher m = FORBIDDEN_STATE_SOURCE.matcher(text);
            while (m.find()) {
                String token = m.group().contains("isDone") ? "isDone" : "isAggressive";
                String scopeName = scope.startsWith("when#") ? "when" : scope;

                if (!STATE_SOURCE_ALLOWED.containsKey(owner + "." + scopeName + ":" + token)) {
                    problems.add(owner + " " + scopeName + " reads " + token + "()");
                }
            }
        });
        return problems;
    }

    private static List<String> callArguments(String code, String... prefixes) {
        List<String> all = new ArrayList<>();
        for (String prefix : prefixes) {
            all.addAll(JavaText.callArguments(code, prefix));
        }
        return all;
    }

    static List<String> launderedStateProblems(String owner, String code) {
        Map<String, List<String>> methods = JavaText.methods(code);
        StringBuilder tables = new StringBuilder();
        for (String args : callArguments(code, ".when(", ".stopWhen(")) {
            tables.append('\n').append(JavaText.reachableText(args, methods, 3));
        }
        Set<String> read = JavaText.identifiers(tables.toString());
        List<String> problems = new ArrayList<>();
        for (String args : JavaText.callArguments(code, "entityData.set(")) {
            List<String> parts = JavaText.splitTopLevel(args, ',');
            String tracked = parts.get(0).trim();
            Matcher source = FORBIDDEN_STATE_SOURCE.matcher(String.join(",", parts.subList(1, parts.size())));

            if (!read.contains(tracked) || !source.find()) {
                continue;
            }

            String token = source.group().contains("isDone") ? "isDone" : "isAggressive";

            if (!STATE_SOURCE_ALLOWED.containsKey(owner + "." + tracked + ":" + token)) {
                problems.add(owner + " copies " + token + "() into " + tracked + " and a table reads it");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> trackedFlagsDoNotLaunderUnstableStateSources() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> assertEquals(List.of(), launderedStateProblems(mob.name(), mob.code()))));
    }

    @Test
    void launderingCheckCatchesCopiedStateSources() {
        String washed = "class E { void tick() { this.entityData.set(NAV, !getNavigation().isDone()); } void c() { x.when(() -> entityData.get(NAV), A); } }";
        String viaGetter = "class E { void tick() { entityData.set(HIT, isAggressive()); } boolean hit() { return entityData.get(HIT); } void c() { x.when(this::hit, A); } }";
        String unread = "class E { void tick() { entityData.set(NAV, !getNavigation().isDone()); } void c() { x.when(() -> other, A); } }";
        String stable = "class E { void tick() { entityData.set(NAV, limbSwing > 0.1F); } void c() { x.when(() -> entityData.get(NAV), A); } }";
        assertEquals(1, launderedStateProblems("Canary", washed).size());
        assertEquals(1, launderedStateProblems("Canary", viaGetter).size());
        assertEquals(List.of(), launderedStateProblems("Canary", unread));
        assertEquals(List.of(), launderedStateProblems("Canary", stable));
    }

    @Test
    void tablesRenderersAndModelsWereFound() {
        int conditions = MobAnimations.mobs().stream()
                .mapToInt(mob -> JavaText.callArguments(mob.code(), ".when(").size())
                .sum();
        assertTrue(conditions >= 40, "only " + conditions + " table conditions found");
        assertTrue(ModProject.files(MobAnimations.ENTITIES.resolve("renders"), ".java").size() >= 10);
        assertTrue(ModProject.files(MobAnimations.ENTITIES.resolve("models"), ".java").size() >= 10);
    }

    @TestFactory
    Stream<DynamicTest> tablesAndSoundsAvoidUnstableStateSources() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> assertEquals(List.of(), stateSourceProblems(mob.name(), mob.code(), false))));
    }

    @TestFactory
    Stream<DynamicTest> renderersAndModelsAvoidUnstableStateSources() {
        List<Path> files = new ArrayList<>();
        files.addAll(ModProject.files(MobAnimations.ENTITIES.resolve("renders"), ".java"));
        files.addAll(ModProject.files(MobAnimations.ENTITIES.resolve("models"), ".java"));
        return files.stream()
                .map(file -> DynamicTest.dynamicTest(
                        file.getFileName().toString(),
                        () ->
                                assertEquals(List.of(), stateSourceProblems(file.getFileName().toString().replace(".java", ""), Sources.code(file), true))
                ));
    }

    @Test
    void stateSourceCheckCatchesBadReads() {
        String table = "x.when(() -> isAggressive() && c, A).stopWhen(() -> getNavigation().isDone())";
        assertEquals(2, stateSourceProblems("Canary", table, false).size());
        String sound = "SoundEvent getAmbientSound() { return getNavigation().isDone() ? A : B; } void playHit() { isAggressive(); }";
        assertEquals(1, stateSourceProblems("Canary", sound, false).size());
        assertEquals(1, stateSourceProblems("Canary", "void render(M m) { if (m.isAggressive()) { } }", true).size());
        assertEquals(List.of(), stateSourceProblems("Canary", ".when(() -> chasing, A)", false));
        String viaReference = "class E { boolean running() { return isAggressive(); } void c() { x.when(this::running, A); } }";
        assertEquals(1, stateSourceProblems("Canary", viaReference, false).size());
        String viaCall = "class E { boolean running() { return getNavigation().isDone(); } void c() { x.when(() -> !running(), A); } }";
        assertEquals(1, stateSourceProblems("Canary", viaCall, false).size());
    }

    static List<String> loopProblems(
            List<Clip> clips,
            Set<String> triggerable,
            String controllerBody,
            JsonObject animations
    ) {
        List<String> problems = new ArrayList<>();
        for (Clip clip : clips) {
            JsonObject animation = MobAnimations.animation(animations, clip.clip());

            if (animation == null) {
                problems.add(clip.field() + ": clip " + clip.clip() + " is not in the animation file");
                continue;
            }

            FileLoop file = MobAnimations.fileLoop(animation);
            FileLoop effective = MobAnimations.effective(clip.loop(), file);

            if (triggerable.contains(clip.field())) {
                if (effective != FileLoop.ONCE) {
                    problems.add(clip.field() + ": triggered clip must play once but is " + effective);
                }
            } else if (isReferenced(controllerBody, clip.field())) {
                if (effective == FileLoop.ONCE) {
                    problems.add(clip.field() + ": state clip plays once and then stops; use hold on last frame");
                }

                if (clip.loop() == Loop.LOOP && file != FileLoop.LOOP) {
                    problems.add(clip.field() + ": Java loops a clip the file does not loop");
                }
            }
        }
        return problems;
    }

    private static boolean isReferenced(String text, String name) {
        return Pattern.compile("(?<![\\w])" + Pattern.quote(name) + "(?!\\w)").matcher(text).find();
    }

    static Set<String> triggerableFields(String code) {
        Set<String> names = new TreeSet<>();
        Matcher m = TRIGGERABLE.matcher(code);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names;
    }

    @TestFactory
    Stream<DynamicTest> everyDeclaredClipIsParsed() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(
                        mob.name(),
                        () ->
                                assertEquals(0, MobAnimations.unparsedClipSites(mob.code()), "some RawAnimation.begin( calls are not understood by the test")
                ));
    }

    @TestFactory
    Stream<DynamicTest> clipsAssignedInEnumsExistInTheAnimationFile() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> {
                    JsonObject animations = mob.animations();
                    List<String> missing = mob.enumClips().stream()
                            .filter(clip -> MobAnimations.animation(animations, clip.clip()) == null)
                            .map(Clip::clip)
                            .toList();
                    assertEquals(List.of(), missing);
                }));
    }

    @Test
    void enumClipsWereFound() {
        assertTrue(MobAnimations.mob("CorvidEntity").enumClips().size() >= 9, "three species with three clips each");
        assertTrue(MobAnimations.mob("TormentedVillagerEntity").enumClips().size() >= 7);
    }

    @Test
    void clipSiteCheckCatchesCallsItCannotRead() {
        String field = "RawAnimation A = RawAnimation.begin().thenLoop(\"a\");";
        String inline = "void c() { x.when(c, RawAnimation.begin().thenLoop(\"a\")); }";
        String bare = "void c() { this.fly = RawAnimation.begin().thenLoop(name); }";
        String enumOk = "enum S { A(\"a\"), B(\"b\"); S(String name) { this.walk = RawAnimation.begin().thenLoop(\"animation.\" + name + \".walk\"); } }";
        String enumUnknown = "enum S { A(\"a\"); S(String name) { this.walk = RawAnimation.begin().thenLoop(\"animation.\" + other + \".walk\"); } }";
        assertEquals(0, MobAnimations.unparsedClipSites(field));
        assertEquals(1, MobAnimations.unparsedClipSites(inline));
        assertEquals(1, MobAnimations.unparsedClipSites(bare));
        assertEquals(0, MobAnimations.unparsedClipSites(enumOk));
        assertEquals(1, MobAnimations.unparsedClipSites(enumUnknown));
        assertEquals(
                List.of("animation.a.walk", "animation.b.walk"),
                MobAnimations.enumClips(enumOk).stream()
                        .map(Clip::clip)
                        .toList()
        );
    }

    @TestFactory
    Stream<DynamicTest> javaLoopTypesMatchTheAnimationFile() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> {
                    String controllers = mob.controllerBody();
                    List<Clip> clips = new ArrayList<>(mob.clips());
                    assertEquals(List.of(), loopProblems(clips, triggerableFields(controllers), controllers, mob.animations()));
                }));
    }

    @Test
    void tormentedMoodsMatchTheAnimationFile() {
        Mob mob = MobAnimations.mob("TormentedVillagerEntity");
        List<Clip> moods = MobAnimations.moodClips(mob.code(), mob.constants());
        assertEquals(7, moods.size());
        assertEquals(
                List.of(),
                loopProblems(
                        moods,
                        Set.of(),
                        moods.stream()
                                .map(Clip::field)
                                .reduce("", (a, b) -> a + " " + b),
                        mob.animations()
                )
        );
    }

    @Test
    void loopCheckCatchesMismatches() {
        JsonObject animations = Json.parseObject("{\"animations\":{\"hold\":{\"loop\":\"hold_on_last_frame\",\"animation_length\":1},\"once\":{\"loop\":false,\"animation_length\":1},\"loop\":{\"loop\":true,\"animation_length\":1}}}").getAsJsonObject("animations");
        List<Clip> playOnceOnHold = MobAnimations.clipsIn("RawAnimation A = RawAnimation.begin().then(\"hold\", Animation.LoopType.PLAY_ONCE);");
        assertEquals(1, loopProblems(playOnceOnHold, Set.of(), "A", animations).size());
        List<Clip> plainPlay = MobAnimations.clipsIn("RawAnimation A = RawAnimation.begin().thenPlay(\"once\");");
        assertEquals(1, loopProblems(plainPlay, Set.of(), "A", animations).size());
        assertEquals(List.of(), loopProblems(plainPlay, Set.of("A"), "", animations));
        List<Clip> forcedLoop = MobAnimations.clipsIn("RawAnimation A = RawAnimation.begin().thenLoop(\"once\");");
        assertEquals(1, loopProblems(forcedLoop, Set.of(), "A", animations).size());
        List<Clip> heldTrigger = MobAnimations.clipsIn("RawAnimation A = RawAnimation.begin().thenPlayAndHold(\"once\");");
        assertEquals(1, loopProblems(heldTrigger, Set.of("A"), "", animations).size());
        List<Clip> unknown = MobAnimations.clipsIn("RawAnimation A = RawAnimation.begin().thenLoop(\"missing\");");
        assertEquals(1, loopProblems(unknown, Set.of(), "A", animations).size());
        List<Clip> fine = MobAnimations.clipsIn("RawAnimation A = RawAnimation.begin().thenPlayAndHold(\"hold\"); RawAnimation B = RawAnimation.begin().thenLoop(\"loop\");");
        assertEquals(List.of(), loopProblems(fine, Set.of(), "A B", animations));
    }

    static List<String> timerProblems(
            String label,
            int timer,
            JsonObject animation,
            FileLoop effective,
            int entryBlend
    ) {
        int clip = MobAnimations.lengthTicks(animation);
        List<String> problems = new ArrayList<>();

        if (effective == FileLoop.HOLD && timer < clip + entryBlend) {
            problems.add(label + ": timer " + timer + " ends before clip " + clip + " + blend " + entryBlend);
        }

        if (effective == FileLoop.LOOP && timer > clip + entryBlend) {
            problems.add(label + ": timer " + timer + " starts a partial pass past clip " + clip + " + blend " + entryBlend);
        }

        if (effective == FileLoop.ONCE) {
            problems.add(label + ": clip plays once");
        }

        return problems;
    }

    private static List<String> ruleProblems(TimerRule rule) {
        Mob mob = MobAnimations.mob(rule.mob());
        Integer timer = mob.constants().get(rule.constant());
        assertTrue(timer != null, rule.mob() + "." + rule.constant() + " not found");
        JsonObject animations = mob.animations();
        Clip clip = mob.clips().stream()
                .filter(c -> c.clip().equals(rule.clip()) || c.clip().endsWith("." + rule.clip()))
                .findFirst()
                .orElseThrow();
        JsonObject animation = MobAnimations.animation(animations, clip.clip());
        FileLoop effective = MobAnimations.effective(clip.loop(), MobAnimations.fileLoop(animation));
        return timerProblems(rule.mob() + "." + rule.constant(), timer, animation, effective, rule.entryBlend());
    }

    @TestFactory
    Stream<DynamicTest> stateTimersFitTheirClips() {
        return TIMER_RULES.stream().map(rule -> DynamicTest.dynamicTest(rule.mob() + "." + rule.constant(), () -> assertEquals(List.of(), ruleProblems(rule))));
    }

    @Test
    void ghoulRageTimerFitsItsClip() {
        assertEquals(List.of(), ruleProblems(GHOUL_RAGE));
    }

    private static final String REMOVAL_REASON = "Removal tick. deathClipsOutlastTheRemovalTick covers it.";
    private static final String NO_CLIP_REASON = "Timing of a goal, a sound, a spell or a hit. No clip length.";

    /// Each entry gives the reason that the timer constant is not a clip length.
    private static final Map<String, String> TIMER_EXCEPTIONS = Map.ofEntries(
            Map.entry("AnimatedCorruptedIllager.DEATH_TICKS", REMOVAL_REASON),
            Map.entry("AnimatedCorruptedIllager.STRIKE_HIT_TICKS", "Tick of the hit inside the strike clip."),
            Map.entry("AnimatedCorruptedIllager.BITE_HIT_TICKS", "Tick of the hit inside the bite clip."),
            Map.entry("AnimatedCorruptedIllager.GLITCH_COOLDOWN_TICKS", NO_CLIP_REASON),
            Map.entry("AnimatedCorruptedIllager.WANDER_STUCK_TICKS", NO_CLIP_REASON),
            Map.entry("CorvidEntity.WALK_HOLD_TICKS", "Grace window of the walk state."),
            Map.entry("CorvidEntity.DEATH_ANIMATION_DURATION", REMOVAL_REASON),
            Map.entry("CorvidEntity.IMPACT_REST_TICKS", NO_CLIP_REASON),
            Map.entry("FallingCorpse.LANDING_TRANSITION_TICKS", "Blend length into the landing clip. Not a state timer."),
            Map.entry("GhoulEntity.ATTACK_ANIMATION_DURATION", "Raw clip length. ATTACK_ANIM_SYNC_TICKS carries the blended timer."),
            Map.entry("GhoulEntity.CHASE_SOUND_COOLDOWN_TICKS", NO_CLIP_REASON),
            Map.entry("GhoulEntity.CHASE_END_TICKS", NO_CLIP_REASON),
            Map.entry("GhoulEntity.DEATH_ANIMATION_DURATION", REMOVAL_REASON),
            Map.entry("GhoulingEntity.DEATH_ANIMATION_DURATION", REMOVAL_REASON),
            Map.entry("LichEntity.DEATH_REMOVAL_TICKS", REMOVAL_REASON),
            Map.entry("LichEntity.CORPSE_SPELL_DURATION", "Value from the config file. No fixed clip length."),
            Map.entry("LichEntity.HUNT_DURATION", "Value from the config file. No fixed clip length."),
            Map.entry("LichEntity.HEALING_DURATION", "Value from the config file. No fixed clip length."),
            Map.entry("LichEntity.LEVITATION_DURATION", "Value from the config file. No fixed clip length."),
            Map.entry("ReaperEntity.DEATH_TICKS", REMOVAL_REASON),
            Map.entry("RevenantEntity.CHASE_END_TICKS", NO_CLIP_REASON),
            Map.entry("TormentedVillagerEntity.RAGE_SOUND_TICKS", NO_CLIP_REASON),
            Map.entry("TormentedVillagerEntity.MOOD_TRANSITION_TICKS", "Blend length. Not a state timer."),
            Map.entry("WraithEntity.CHASE_END_TICKS", NO_CLIP_REASON),
            Map.entry("WraithEntity.DEATH_TICKS", REMOVAL_REASON)
    );

    static Set<String> timerConstants(String code) {
        Set<String> names = new TreeSet<>();
        Matcher m = TIMER_CONSTANT.matcher(code);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names;
    }

    static List<String> uncoveredTimers(String mob, String code, Set<String> covered) {
        List<String> problems = new ArrayList<>();
        for (String name : timerConstants(code)) {
            if (!covered.contains(mob + "." + name)) {
                problems.add(mob + "." + name + " has neither a timer rule nor an exception");
            }
        }
        return problems;
    }

    private static Set<String> coveredTimers() {
        Set<String> covered = new TreeSet<>(TIMER_EXCEPTIONS.keySet());
        TIMER_RULES.forEach(rule -> covered.add(rule.mob() + "." + rule.constant()));
        covered.add(GHOUL_RAGE.mob() + "." + GHOUL_RAGE.constant());
        return covered;
    }

    @TestFactory
    Stream<DynamicTest> everyTimerConstantHasARuleOrAnException() {
        Set<String> covered = coveredTimers();
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(
                        mob.name(),
                        () ->
                                assertEquals(List.of(), uncoveredTimers(mob.name(), mob.code(), covered))
                ));
    }

    @Test
    void timerCoverageCheckCatchesAnUnlistedConstant() {
        String code = "private static final int SPAWN_TICKS = 5; public final int NEW_ANIMATION_DURATION = 9; private static final int RANGE = 3;";
        assertEquals(List.of("Canary.NEW_ANIMATION_DURATION has neither a timer rule nor an exception"), uncoveredTimers("Canary", code, Set.of("Canary.SPAWN_TICKS")));
        assertEquals(List.of(), uncoveredTimers("Canary", code, Set.of("Canary.SPAWN_TICKS", "Canary.NEW_ANIMATION_DURATION")));
    }

    @Test
    void tormentedMoodTimersFitTheirClips() {
        Mob mob = MobAnimations.mob("TormentedVillagerEntity");
        List<String> problems = new ArrayList<>();
        for (Clip mood : MobAnimations.moodClips(mob.code(), mob.constants())) {
            if (mood.ticks().orElse(0) <= 0) {
                continue;
            }

            JsonObject animation = MobAnimations.animation(mob.animations(), mood.clip());
            FileLoop effective = MobAnimations.effective(mood.loop(), MobAnimations.fileLoop(animation));
            int blend = mood.field().equals("SPAWNING") ? 0 : mob.constants().get("MOOD_TRANSITION_TICKS");
            problems.addAll(timerProblems(mood.field(), mood.ticks().getAsInt(), animation, effective, blend));
        }
        assertEquals(List.of(), problems);
    }

    @Test
    void timerCheckCatchesShortAndLongTimers() {
        JsonObject animation = Json.parseObject("{\"animation_length\":1.1}");
        assertEquals(List.of(), timerProblems("t", 27, animation, FileLoop.HOLD, 5));
        assertEquals(1, timerProblems("t", 26, animation, FileLoop.HOLD, 5).size());
        assertEquals(List.of(), timerProblems("t", 27, animation, FileLoop.LOOP, 5));
        assertEquals(1, timerProblems("t", 28, animation, FileLoop.LOOP, 5).size());
        assertEquals(List.of(), timerProblems("t", 22, animation, FileLoop.HOLD, 0));
        assertEquals(1, timerProblems("t", 22, animation, FileLoop.ONCE, 0).size());
    }

    static List<String> deathRemovalProblems(String code, JsonObject animations, Map<String, Integer> constants) {
        List<String> problems = new ArrayList<>();
        for (Clip clip : MobAnimations.clipsIn(code)) {
            String name = clip.clip().substring(clip.clip().lastIndexOf('.') + 1);

            if (!name.equals("death") && !name.equals("blow_apart")) {
                continue;
            }

            List<String> bodies = JavaText.methods(code).get("tickDeath");

            if (bodies == null) {
                problems.add(clip.field() + ": a death clip needs a tickDeath override");
                continue;
            }

            int removal = removalTick(String.join("\n", bodies), constants);
            int needed = MobAnimations.lengthTicks(MobAnimations.animation(animations, clip.clip())) + MobAnimations.TRANSITION_TICKS;

            if (removal == Integer.MAX_VALUE) {
                problems.add(clip.field() + ": removal tick not readable");
            } else if (removal < needed) {
                problems.add(clip.field() + ": removal at " + removal + " cuts the death clip, needs " + needed);
            }
        }
        return problems;
    }

    /// Smallest death time that guards a removal call. MAX_VALUE means none.
    private static int removalTick(String body, Map<String, Integer> constants) {
        int removal = Integer.MAX_VALUE;
        Matcher branch = Pattern.compile("\\bif\\s*\\(").matcher(body);
        while (branch.find()) {
            int open = branch.end() - 1;
            int close = JavaText.closing(body, open);
            int block = body.indexOf('{', close);

            if (block < 0 || body.substring(close, block).contains(";")) {
                continue;
            }

            Matcher time = DEATH_REMOVAL.matcher(body.substring(open, close));

            if (time.find() && JavaText.inside(body, block).contains("remove")) {
                removal = Math.min(removal, JavaText.evaluate(time.group(1), constants).orElse(Integer.MAX_VALUE));
            }
        }
        return removal;
    }

    @TestFactory
    Stream<DynamicTest> deathClipsOutlastTheRemovalTick() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(
                        mob.name(),
                        () ->
                                assertEquals(List.of(), deathRemovalProblems(mob.code(), mob.animations(), mob.constants()))
                ));
    }

    @Test
    void deathCheckCatchesEarlyRemovalAndMissingOverride() {
        JsonObject animations = Json.parseObject("{\"animations\":{\"death\":{\"animation_length\":2.2}}}").getAsJsonObject("animations");
        String clip = "RawAnimation D = RawAnimation.begin().thenPlayAndHold(\"death\");";
        Map<String, Integer> constants = Map.of("TRANSITION_TICKS", 5);
        assertEquals(List.of(), deathRemovalProblems(clip + "void tickDeath() { if (deathTime == 49) { remove(); } }", animations, constants));
        assertEquals(1, deathRemovalProblems(clip + "void tickDeath() { if (deathTime == 48) { remove(); } }", animations, constants).size());
        assertEquals(1, deathRemovalProblems(clip, animations, constants).size());
        String sound = clip + "void tickDeath() { if (deathTime == 5) { playSound(); } if (deathTime == 49) { remove(); } }";
        assertEquals(List.of(), deathRemovalProblems(sound, animations, constants));
        assertEquals(List.of(), deathRemovalProblems("RawAnimation I = RawAnimation.begin().thenLoop(\"idle\");", animations, constants));
    }

    static List<String> deathSuperProblems(String code) {
        List<String> problems = new ArrayList<>();
        for (String body : JavaText.methods(code).getOrDefault("tickDeath", List.of())) {
            if (body.contains("super.tickDeath(")) {
                problems.add("tickDeath calls super.tickDeath(), which removes the mob before the clip ends");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> deathOverridesNeverCallSuper() {
        return ModProject.files(MobAnimations.ENTITIES, ".java").stream()
                .filter(file -> file.getParent().equals(MobAnimations.ENTITIES))
                .map(file -> DynamicTest.dynamicTest(file.getFileName().toString(), () -> assertEquals(List.of(), deathSuperProblems(Sources.code(file)))));
    }

    @Test
    void deathSuperCheckCatchesTheSuperCall() {
        assertEquals(1, deathSuperProblems("void tickDeath() { super.tickDeath(); ++deathTime; }").size());
        assertEquals(List.of(), deathSuperProblems("void tickDeath() { ++deathTime; }"));
        assertEquals(List.of(), deathSuperProblems("void tick() { super.tick(); }"));
    }

    private static final Set<String> TERMINAL_CLIPS = Set.of("death", "blow_apart");

    static Set<String> snappingChannels(JsonObject animations) {
        Map<String, Map<String, Boolean>> perClip = new LinkedHashMap<>();
        for (String key : animations.keySet()) {
            Map<String, Boolean> channels = new LinkedHashMap<>();
            JsonObject bones = animations.getAsJsonObject(key).getAsJsonObject("bones");

            if (bones != null) {
                for (String bone : bones.keySet()) {
                    JsonObject bone1 = bones.getAsJsonObject(bone);
                    for (String channel : bone1.keySet()) {
                        channels.put(bone + "." + channel, isNeutral(bone1.get(channel), channel.equals("scale") ? 1.0D : 0.0D));
                    }
                }
            }

            perClip.put(key, channels);
        }
        Set<String> result = new TreeSet<>();
        for (Map.Entry<String, Map<String, Boolean>> entry : perClip.entrySet()) {
            if (TERMINAL_CLIPS.contains(entry.getKey().substring(entry.getKey().lastIndexOf('.') + 1))) {
                continue;
            }

            Map<String, Boolean> source = entry.getValue();
            source.forEach((channel, neutral) -> {
                if (!neutral && perClip.values().stream()
                        .anyMatch(other -> !other.containsKey(channel))) {
                    result.add(channel);
                }
            });
        }
        return result;
    }

    private static boolean isNeutral(JsonElement element, double neutral) {
        if (element.isJsonPrimitive()) {
            return element.getAsJsonPrimitive().isNumber() ? element.getAsDouble() == neutral : isOptionName(element.getAsString());
        }

        if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (!isNeutral(child, neutral)) {
                    return false;
                }
            }
            return true;
        }

        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            if (!entry.getKey().equals("lerp_mode")
                    && !entry.getKey().equals("easing")
                    && !isNeutral(entry.getValue(), neutral)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isOptionName(String text) {
        return text.equals("linear") || text.equals("catmullrom") || text.equals("step");
    }

    private static final Pattern RETURN_APPLIED = Pattern.compile("\\bBoneReturn\\.apply\\(");
    private static final Pattern HARD_CUT = Pattern.compile(
            ZERO_BRANCH.pattern()
                    + "|setTransitionTicks\\(\\s*0\\s*\\)"
                    + "|\\.build\\([^,()]+,\\s*0\\s*\\)"
                    + "|transitionTicks\\(\\(\\)\\s*->\\s*0\\s*\\)"
                    + "|controller\\([^,()]+,\\s*0\\s*,"
    );

    static List<String> boneReturnProblems(String mobName, String entityCode, String rendererCode, int returnTicks) {
        List<String> problems = new ArrayList<>();

        if (returnTicks < 1) {
            problems.add("The bone return lasts " + returnTicks + " ticks, so the bones never ease back.");
        }

        boolean applied = RETURN_APPLIED.matcher(rendererCode).find();

        if (HARD_CUT.matcher(entityCode).find() && !applied) {
            problems.add(mobName + " cuts a clip with a 0-tick blend but its renderer never calls BoneReturn.apply.");
        }

        return problems;
    }

    private static String rendererOf(String mobName) {
        Pattern named = Pattern.compile("\\b" + mobName + "\\b");
        StringBuilder found = new StringBuilder();
        for (Path file : ModProject.files(MobAnimations.ENTITIES.resolve("renders"), ".java")) {
            String code = Sources.code(file);

            if (named.matcher(code).find()) {
                found.append(code);
            }
        }
        return found.toString();
    }

    @Test
    void theClockControllerRecordsTheBoneReturn() {
        String code = Sources.code(MobAnimations.ENTITIES.resolve("animation/AnimationClock.java"));
        String body = JavaText.joinedBodies(JavaText.methods(code), "extractControllerState");
        assertTrue(body.contains("BoneReturn.add("), "AnimationClock.extractControllerState never calls BoneReturn.add");
    }

    @TestFactory
    Stream<DynamicTest> everyMobResetsBonesOrKeepsChannelsInAllClips() {
        int returnTicks = JavaText.intConstants(Sources.code(MobAnimations.ENTITIES.resolve("animation/BoneReturn.java")), Map.of()).getOrDefault("TICKS", 0);
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(mob.name(), () -> {
                    Set<String> snapping = new TreeSet<>(snappingChannels(mob.animations()));
                    snapping.removeAll(UNRESET_CHANNELS_ALLOWED.getOrDefault(mob.name(), Set.of()));

                    if (snapping.isEmpty()) {
                        return;
                    }

                    assertEquals(List.of(), boneReturnProblems(mob.name(), mob.code(), rendererOf(mob.name()), returnTicks));
                }));
    }

    @Test
    void boneReturnCheckCatchesAHardCutWithoutReturn() {
        String cut = "class E { int t() { return fast ? 0 : 5; } }";
        String soft = "class E { int t() { return 5; } }";
        String applied = "class R { void r() { BoneReturn.apply(info, snapshots); } }";
        assertEquals(List.of(), boneReturnProblems("E", cut, applied, 5));
        assertEquals(List.of(), boneReturnProblems("E", soft, "class R { }", 5));
        assertEquals(1, boneReturnProblems("E", cut, "class R { }", 5).size());
        assertEquals(1, boneReturnProblems("E", soft, applied, 0).size());

        for (String form : List.of(
                "c.setTransitionTicks(0);",
                "b.build(\"controller\", 0);",
                "b.transitionTicks(() -> 0);",
                "AnimationClock.controller(name, 0, handler);"
        )) {
            String code = "class E { void t() { " + form + " } }";
            assertEquals(1, boneReturnProblems("E", code, "class R { }", 5).size(), form);
            assertEquals(List.of(), boneReturnProblems("E", code, applied, 5), form);
        }
    }

    @Test
    void boneCheckCatchesAChannelThatSnaps() {
        JsonObject animations = Json.parseObject("{\"animations\":{"
                + "\"a\":{\"bones\":{\"leg\":{\"rotation\":[10,0,0]}}},"
                + "\"b\":{\"bones\":{\"leg\":{\"rotation\":[0,0,0]},\"arm\":{\"rotation\":[0,0,0]}}},"
                + "\"c\":{\"bones\":{\"arm\":{\"scale\":[1,1,1]}}}}}").getAsJsonObject("animations");
        assertEquals(Set.of("leg.rotation"), snappingChannels(animations));
        JsonObject full = Json.parseObject("{\"animations\":{\"a\":{\"bones\":{\"leg\":{\"rotation\":[10,0,0]}}},\"b\":{\"bones\":{\"leg\":{\"rotation\":[0,0,0]}}}}}").getAsJsonObject("animations");
        assertEquals(Set.of(), snappingChannels(full));
    }

    static List<String> rearmProblems(
            String goalName,
            String goalCode,
            String entityCode,
            Map<String, Integer> entityConstants
    ) {
        List<String> problems = new ArrayList<>();
        int duration = Math.max(entityConstants.getOrDefault("ATTACK_ANIMATION_DURATION", 0), entityConstants.getOrDefault("ATTACK_ANIM_SYNC_TICKS", 0));
        Map<String, Integer> constants = JavaText.intConstants(goalCode, entityConstants);
        List<String> rearmPath = rearmPathProblems(entityCode);
        for (String argument : JavaText.callArguments(goalCode, "resetCooldown(")) {
            OptionalInt cooldown = JavaText.evaluate(argument, constants);

            if (cooldown.isEmpty()) {
                problems.add(goalName + ": cooldown '" + argument.trim() + "' is not readable");
            } else if (cooldown.getAsInt() <= duration) {
                for (String problem : rearmPath) {
                    problems.add(goalName + ": cooldown " + cooldown.getAsInt() + " is not longer than the attack clip " + duration + ". " + problem);
                }
            }
        }
        return problems;
    }

    static List<String> rearmPathProblems(String code) {
        List<String> problems = new ArrayList<>();
        int call = code.indexOf("AnimationClock.restart(");

        if (call < 0) {
            problems.add("The entity never calls AnimationClock.restart.");
            return problems;
        }

        boolean guarded = false;
        Matcher head = IF_HEAD.matcher(code);
        while (head.find()) {
            int open = head.end() - 1;
            int close = JavaText.closing(code, open);
            int block = close + 1;
            while (block < code.length() && Character.isWhitespace(code.charAt(block))) {
                block++;
            }

            if (block < code.length() && code.charAt(block) == '{' && call > block && call < JavaText.closing(code, block)) {
                String condition = code.substring(open, close);
                guarded |= Pattern.compile("(?i)timer|ticks").matcher(condition).find()
                        && Pattern.compile("[<>]").matcher(condition).find();
            }
        }

        if (!guarded) {
            problems.add("AnimationClock.restart is not inside a timer comparison.");
        }

        String method = enclosingMethod(code, call);

        if (method == null || Pattern.compile("(?<![\\w.])(?:this\\.)?" + method + "\\s*\\(").matcher(code).results().count() < 2) {
            problems.add("The method around AnimationClock.restart is never called.");
        }

        return problems;
    }

    private static String enclosingMethod(String code, int position) {
        return JavaText.methodSpans(code).stream()
                .filter(span -> span.contains(position))
                .reduce((a, b) -> a.end() - a.start() <= b.end() - b.start() ? a : b)
                .map(JavaText.Span::name)
                .orElse(null);
    }

    @TestFactory
    Stream<DynamicTest> shortMeleeCooldownsRestartTheAttackClip() {
        Pattern owner = Pattern.compile("extends\\s+AbstractGraveyardMeleeGoal<(\\w+)>");
        List<DynamicTest> tests = new ArrayList<>();
        for (Path file : ModProject.files(MobAnimations.ENTITIES.resolve("ai/goals"), ".java")) {
            String goal = Sources.code(file);
            Matcher m = owner.matcher(goal);

            if (m.find()) {
                Mob mob = MobAnimations.mob(m.group(1));
                tests.add(DynamicTest.dynamicTest(
                        file.getFileName().toString(),
                        () ->
                                assertEquals(List.of(), rearmProblems(file.getFileName().toString(), goal, mob.code(), mob.constants()))
                ));
            }
        }
        assertTrue(tests.size() >= 5);
        return tests.stream();
    }

    @Test
    void rearmCheckCatchesMissingReset() {
        String goal = "void attack() { this.resetCooldown(20); }";
        Map<String, Integer> constants = Map.of("ATTACK_ANIMATION_DURATION", 27);
        String rearm = "void tick() { restart(); } void restart() { if (timer > last + 1) { if (c != null) { AnimationClock.restart(c); } } last = timer; }";
        String unguarded = "void tick() { restart(); } void restart() { AnimationClock.restart(c); }";
        String neverCalled = "void restart() { if (timer > last + 1) { AnimationClock.restart(c); } }";
        String wrongCondition = "void tick() { restart(); } void restart() { if (c != null) { AnimationClock.restart(c); } }";
        assertEquals(1, rearmProblems("g", goal, "class E { }", constants).size());
        assertEquals(List.of(), rearmProblems("g", goal, rearm, constants));
        assertEquals(1, rearmProblems("g", goal, unguarded, constants).size());
        assertEquals(2, rearmProblems("g", goal, wrongCondition.replace("restart(); }", "}"), constants).size());
        assertEquals(1, rearmProblems("g", goal, neverCalled, constants).size());
        assertEquals(1, rearmProblems("g", goal, wrongCondition, constants).size());
        assertEquals(List.of(), rearmProblems("g", goal, "class E { }", Map.of("ATTACK_ANIMATION_DURATION", 15)));
    }

    @Test
    void rearmCheckResolvesCooldownConstants() {
        String goal = "static final int PAUSE = 12; void attack() { this.resetCooldown(PAUSE + 10); this.resetCooldown(LONG_PAUSE); }";
        Map<String, Integer> constants = Map.of("ATTACK_ANIMATION_DURATION", 27, "LONG_PAUSE", 40);
        assertEquals(List.of(), rearmProblems("g", goal, "void restart() { }", Map.of("ATTACK_ANIMATION_DURATION", 15, "LONG_PAUSE", 40)));
        assertEquals(1, rearmProblems("g", goal, "class E { }", constants).size());
        assertEquals(1, rearmProblems("g", "void a() { resetCooldown(unknown()); }", "class E { }", constants).size());
    }

    static List<String> unusedClips(String code, String controllerBody) {
        List<String> problems = new ArrayList<>();
        for (Clip clip : MobAnimations.clipsIn(code)) {
            if (!isReferenced(controllerBody, clip.field())) {
                problems.add(clip.field() + " is declared but no controller uses it");
            }
        }
        return problems;
    }

    @TestFactory
    Stream<DynamicTest> everyDeclaredClipIsUsedByTheController() {
        return MobAnimations.mobs().stream()
                .map(mob -> DynamicTest.dynamicTest(
                        mob.name(),
                        () ->
                                assertEquals(List.of(), unusedClips(mob.code(), mob.controllerBody()))
                ));
    }

    @Test
    void unusedClipCheckCatchesADeadClip() {
        String code = "RawAnimation A = RawAnimation.begin().thenLoop(\"a\"); RawAnimation SPAWN = RawAnimation.begin().thenPlayAndHold(\"spawn\");";
        assertEquals(1, unusedClips(code, ".when(x, A)").size());
        assertEquals(List.of(), unusedClips(code, ".when(x, A).otherwise(SPAWN)"));
        assertEquals(2, unusedClips(code, ".when(x, A_OLD).otherwise(SPAWNED)").size());
    }

    private static final Pattern TORMENTED_CUT = Pattern.compile("cut\\s*=\\s*mood\\s*==\\s*Mood\\.SPAWNING");
    private static final String OPEN_FINDING = "Open finding. One axis moves more than 90 degrees within the blend and does not wrap. Review by eye.";

    /// Each entry gives the reason for an unchecked Mob:FROM>TO pair.
    private static final Map<String, String> HANDOVER_ALLOWED = Map.of(
            "RevenantEntity:ATTACK_ANIMATION>REANIMATE_ANIMATION", "The fake death clip always plays between the attack clip and the reanimate clip.",
            "CorvidEntity:LAND>GLIDE", OPEN_FINDING,
            "CorvidEntity:LAND>FLY", OPEN_FINDING,
            "WraithEntity:SPAWN_ANIMATION>IDLE_ANIMATION", OPEN_FINDING,
            "WraithEntity:SPAWN_ANIMATION>MOVE_ANIMATION", OPEN_FINDING,
            "LichEntity:PHASE_2_CHARGE_ANIMATION>STUNNED_ANIMATION", OPEN_FINDING
    );

    private static final Set<String> LICH_CRAWL_CLIPS = Set.of("PHASE_3_ATTACK_ANIMATION", "CRAWL_IDLE_ANIMATION", "CRAWL_POUNCE_ANIMATION", "CRAWL_ATTACK_ANIMATION");

    /// The clips that can follow each hold clip, per mob.
    private static final Map<String, Map<String, Set<String>>> HANDOVER_SUCCESSORS = Map.of(
            "RevenantEntity", Map.of(
                    // Fire on a fake dead Revenant kills it in the fake death pose.
                    "FAKE_DEATH_ANIMATION", Set.of("REANIMATE_ANIMATION")
            ),
            "LichEntity", Map.of(
                    "SPAWN_ANIMATION", Set.of("IDLE_ANIMATION"),
                    "START_PHASE_2_ANIMATION", Set.of("PHASE_2_IDLE_ANIMATION"),
                    "START_PHASE_3_ANIMATION", LICH_CRAWL_CLIPS,
                    "PHASE_2_CHARGE_ANIMATION", Set.of("PHASE_2_IDLE_ANIMATION", "PHASE_2_ATTACK_ANIMATION", "STUNNED_ANIMATION"),
                    "CRAWL_POUNCE_ANIMATION", LICH_CRAWL_CLIPS,
                    "CRAWL_ATTACK_ANIMATION", LICH_CRAWL_CLIPS
            )
    );

    record Jump(double degrees, String where) {
    }

    private static String simpleName(Clip clip) {
        return clip.clip().substring(clip.clip().lastIndexOf('.') + 1);
    }

    private static String normalized(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private static boolean mentions(String text, Clip clip) {
        String haystack = normalized(text);
        String simple = normalized(simpleName(clip));
        return haystack.contains(normalized(clip.field())) || haystack.contains(simple)
                || haystack.contains(simple.replace("three", "3")
                        .replace("two", "2")
                        .replace("one", "1"));
    }

    /// Ticks the code blends into {@code to} after {@code from}. Zero is a hard cut.
    static int blendInto(String code, Clip from, Clip to) {
        if (simpleName(to).equals("spawn") && (cutsIntoSpawn(code) || TORMENTED_CUT.matcher(code).find())) {
            return 0;
        }

        Map<String, List<String>> methods = JavaText.methods(code);
        for (String lambda : JavaText.callArguments(code, ".transitionTicks(")) {
            String text = JavaText.reachableText(lambda, methods, 2);

            if (CURRENT_CLIP.matcher(text).find()
                    && BARE_ZERO.matcher(text).find()
                    && mentions(text, from)) {
                return 0;
            }
        }
        return MobAnimations.TRANSITION_TICKS;
    }

    private static double[] vectorOf(JsonElement element, boolean last) {
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            double[] result = new double[3];

            if (array.size() != 3) {
                return null;
            }

            for (int i = 0; i < 3; i++) {
                if (!array.get(i).isJsonPrimitive() || !array.get(i).getAsJsonPrimitive().isNumber()) {
                    return null;
                }

                result[i] = array.get(i).getAsDouble();
            }
            return result;
        }

        if (element.isJsonPrimitive()) {
            double value = element.getAsJsonPrimitive().isNumber() ? element.getAsDouble() : Double.NaN;
            return Double.isNaN(value) ? null : new double[]{value, value, value};
        }

        JsonObject object = element.getAsJsonObject();
        for (String key : List.of("post", "vector")) {
            if (object.has(key)) {
                return vectorOf(object.get(key), last);
            }
        }
        String chosen = null;
        double chosenTime = 0.0D;
        for (String key : object.keySet()) {
            double time;
            try {
                time = Double.parseDouble(key);
            } catch (NumberFormatException e) {
                continue;
            }

            if (chosen == null || (last ? time > chosenTime : time < chosenTime)) {
                chosen = key;
                chosenTime = time;
            }
        }
        return chosen == null ? null : vectorOf(object.get(chosen), last);
    }

    private static double[] rotationAt(JsonObject animation, String bone, boolean last) {
        JsonObject bones = animation.getAsJsonObject("bones");

        if (bones == null || !bones.has(bone)) {
            return new double[3];
        }

        JsonObject channels = bones.getAsJsonObject(bone);
        return channels.has("rotation") ? vectorOf(channels.get("rotation"), last) : new double[3];
    }

    static Jump rotationJump(JsonObject from, JsonObject to) {
        Set<String> bones = new TreeSet<>();
        for (JsonObject animation : List.of(from, to)) {
            if (animation.getAsJsonObject("bones") != null) {
                bones.addAll(animation.getAsJsonObject("bones").keySet());
            }
        }
        Jump worst = new Jump(0.0D, "");
        for (String bone : bones) {
            double[] end = rotationAt(from, bone, true);
            double[] start = rotationAt(to, bone, false);

            if (end == null || start == null) {
                continue;
            }

            for (int axis = 0; axis < 3; axis++) {
                double delta = Math.abs(end[axis] - start[axis]);

                if (delta > worst.degrees()) {
                    worst = new Jump(delta, bone + " axis " + "xyz".charAt(axis) + " from " + end[axis] + " to " + start[axis]);
                }
            }
        }
        return worst;
    }

    static List<String> handoverProblems(
            String mob,
            List<Clip> clips,
            Set<String> targets,
            JsonObject animations,
            BiFunction<Clip, Clip, Integer> blend,
            Map<String, String> allowed,
            Map<String, Set<String>> successors
    ) {
        List<String> problems = new ArrayList<>();
        for (Clip from : clips) {
            JsonObject start = MobAnimations.animation(animations, from.clip());

            if (start == null || TERMINAL_CLIPS.contains(simpleName(from))
                    || MobAnimations.effective(from.loop(), MobAnimations.fileLoop(start)) != FileLoop.HOLD) {
                continue;
            }

            for (Clip to : clips) {
                JsonObject end = MobAnimations.animation(animations, to.clip());
                String pair = mob + ":" + from.field() + ">" + to.field();
                boolean reachable = !successors.containsKey(from.field()) || successors.get(from.field()).contains(to.field());

                if (end == null || !reachable || !targets.contains(to.field()) || to.clip().equals(from.clip()) || allowed.containsKey(pair)) {
                    continue;
                }

                Jump jump = rotationJump(start, end);

                if (jump.degrees() > SPIN_LIMIT_DEGREES && blend.apply(from, to) > 0) {
                    problems.add(pair + " turns " + Math.round(jump.degrees()) + " degrees (" + jump.where() + ") over a blend");
                }
            }
        }
        return problems;
    }

    private static Set<String> tableTargets(Mob mob) {
        String body = mob.controllerBody();
        return mob.clips().stream()
                .map(Clip::field)
                .filter(field -> isReferenced(body, field))
                .collect(Collectors.toSet());
    }

    @TestFactory
    Stream<DynamicTest> holdClipHandoversDoNotSpinTheBones() {
        return MobAnimations.mobs().stream()
                .filter(mob -> !mob.name().equals("TormentedVillagerEntity")).map(mob -> DynamicTest.dynamicTest(
                        mob.name(),
                        () ->
                                assertEquals(
                                        List.of(),
                                        handoverProblems(
                                                mob.name(),
                                                mob.clips(),
                                                tableTargets(mob),
                                                mob.animations(),
                                                (from, to) -> blendInto(mob.code(), from, to),
                                                HANDOVER_ALLOWED,
                                                HANDOVER_SUCCESSORS.getOrDefault(mob.name(), Map.of())
                                        )
                                )
                ));
    }

    @Test
    void tormentedMoodHandoversDoNotSpinTheBones() {
        Mob mob = MobAnimations.mob("TormentedVillagerEntity");
        List<Clip> moods = MobAnimations.moodClips(mob.code(), mob.constants());
        Set<String> targets = moods.stream()
                .map(Clip::field)
                .collect(Collectors.toSet());
        assertEquals(List.of(), handoverProblems(mob.name(), moods, targets, mob.animations(), (from, to) -> blendInto(mob.code(), from, to), HANDOVER_ALLOWED, Map.of()));
    }

    @Test
    void handoverCheckCatchesAWrapAroundSpin() {
        JsonObject animations = Json.parseObject("{\"animations\":{"
                + "\"hold\":{\"loop\":\"hold_on_last_frame\",\"animation_length\":1,\"bones\":{\"staff\":{\"rotation\":{\"0.0\":[0,0,0],\"1.0\":{\"vector\":[-182,180,-180]}}}}},"
                + "\"idle\":{\"loop\":true,\"animation_length\":1,\"bones\":{\"staff\":{\"rotation\":[-2,0,-0.7]}}},"
                + "\"near\":{\"loop\":true,\"animation_length\":1,\"bones\":{\"staff\":{\"rotation\":{\"0.0\":{\"post\":[-170,150,-120],\"lerp_mode\":\"linear\"}}}}}}}").getAsJsonObject("animations");
        List<Clip> clips = MobAnimations.clipsIn("RawAnimation HOLD = RawAnimation.begin().thenPlayAndHold(\"hold\"); RawAnimation IDLE = RawAnimation.begin().thenLoop(\"idle\");"
                + " RawAnimation NEAR = RawAnimation.begin().thenLoop(\"near\");");
        BiFunction<Clip, Clip, Integer> blends = (from, to) -> 5;
        BiFunction<Clip, Clip, Integer> cuts = (from, to) -> 0;
        assertEquals(1, handoverProblems("Canary", clips, Set.of("HOLD", "IDLE"), animations, blends, Map.of(), Map.of()).size());
        assertEquals(List.of(), handoverProblems("Canary", clips, Set.of("HOLD", "IDLE"), animations, cuts, Map.of(), Map.of()));
        assertEquals(List.of(), handoverProblems("Canary", clips, Set.of("HOLD", "IDLE"), animations, blends, Map.of("Canary:HOLD>IDLE", "reason"), Map.of()));
        assertEquals(List.of(), handoverProblems("Canary", clips, Set.of("HOLD", "IDLE"), animations, blends, Map.of(), Map.of("HOLD", Set.of("NEAR"))));
        assertEquals(List.of(), handoverProblems("Canary", clips, Set.of("HOLD"), animations, blends, Map.of(), Map.of()));
        assertEquals(List.of(), handoverProblems("Canary", clips, Set.of("HOLD", "NEAR"), animations, blends, Map.of(), Map.of()));
        assertTrue(rotationJump(animations.getAsJsonObject("hold"), animations.getAsJsonObject("idle")).degrees() > 179.0D);
    }

    @Test
    void blendRuleReadsSpawnCutsAndClipGuards() {
        Clip hold = new Clip("HOLD", "hold", Loop.HOLD, OptionalInt.empty());
        Clip spawn = new Clip("SPAWN", "spawn", Loop.HOLD, OptionalInt.empty());
        Clip idle = new Clip("IDLE", "idle", Loop.LOOP, OptionalInt.empty());
        String cut = ".transitionTicks(() -> isSpawning() ? 0 : 5)";
        assertEquals(0, blendInto(cut, hold, spawn));
        assertEquals(MobAnimations.TRANSITION_TICKS, blendInto(cut, hold, idle));
        String guard = ".transitionTicks(() -> currentClipIs(\"spawn\") ? 0 : 5)";
        assertEquals(0, blendInto(guard, spawn, idle));
        assertEquals(MobAnimations.TRANSITION_TICKS, blendInto(guard, hold, idle));
        assertEquals(MobAnimations.TRANSITION_TICKS, blendInto(".when(a, B)", spawn, idle));
    }
}
