package com.lion.graveyard.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// Source and animation file of every GeckoLib entity, with the clips its Java code declares.
public final class MobAnimations {
    public static final Path ENTITIES = ModProject.JAVA.resolve("entities");
    public static final Path ANIMATIONS = ModProject.ASSETS.resolve(ModProject.ANIMATIONS);
    public static final int TRANSITION_TICKS = animationTableConstant("TRANSITION_TICKS");

    private static final Map<String, String> ANIMATION_FILES = new LinkedHashMap<>();

    static {
        ANIMATION_FILES.put("AnimatedCorruptedIllager", "corrupted_illager/corrupted_illager.animation.json");
        ANIMATION_FILES.put("CorvidEntity", "corvid/corvid.animation.json");
        ANIMATION_FILES.put("FallingCorpse", "falling_corpse/falling_corpse.animation.json");
        ANIMATION_FILES.put("GhoulEntity", "ghoul/ghoul.animation.json");
        ANIMATION_FILES.put("GhoulingEntity", "ghouling/ghouling.animation.json");
        ANIMATION_FILES.put("LichEntity", "lich/lich.animation.json");
        ANIMATION_FILES.put("NamelessHangedEntity", "nameless_hanged/nameless_hanged.animation.json");
        ANIMATION_FILES.put("NightmareEntity", "nightmare/nightmare.animation.json");
        ANIMATION_FILES.put("ReaperEntity", "reaper/reaper.animation.json");
        ANIMATION_FILES.put("RevenantEntity", "revenant/revenant.animation.json");
        ANIMATION_FILES.put("TormentedVillagerEntity", "tormented_villager/tormented_villager.animation.json");
        ANIMATION_FILES.put("WraithEntity", "wraith/wraith.animation.json");
    }

    private static final Pattern CLIP_FIELD = Pattern.compile(
            "RawAnimation\\s+(\\w+)\\s*=\\s*RawAnimation\\.begin\\(\\)\\s*\\.(thenPlayAndHold|thenLoop|thenPlay|then)\\(\\s*(?:(\\w+)\\s*\\+\\s*)?\"([^\"]+)\"\\s*(?:,\\s*(?:\\w+\\.)*(\\w+)\\s*)?\\)");
    private static final Pattern STRING_CONSTANT = Pattern.compile("final\\s+String\\s+(\\w+)\\s*=\\s*\"([^\"]*)\"");
    private static final Pattern MOOD_CONSTANT = Pattern.compile("(\\w+)\\(\"(\\w+)\",\\s*(?:\\w+\\.)*(\\w+),\\s*([^()]+)\\)");

    public enum Loop {LOOP, HOLD, PLAY_ONCE, DEFAULT}

    /// What the animation file asks for when Java defers to it.
    public enum FileLoop {LOOP, HOLD, ONCE}

    /// {@code ticks} is the state length a mood or timer gives the clip, or empty.
    public record Clip(String field, String clip, Loop loop, OptionalInt ticks) {
    }

    public record Mob(String name, Path source, String code, Path animationFile) {
        public Map<String, List<String>> methods() {
            return JavaText.methods(code);
        }

        public List<Clip> clips() {
            return clipsIn(code);
        }

        public List<Clip> enumClips() {
            return MobAnimations.enumClips(code);
        }

        public String controllerBody() {
            return JavaText.joinedBodies(methods(), "registerControllers");
        }

        public Map<String, Integer> constants() {
            return JavaText.intConstants(code, Map.of("TRANSITION_TICKS", TRANSITION_TICKS));
        }

        public JsonObject animations() {
            return animationsOf(ModProject.read(animationFile));
        }
    }

    private MobAnimations() {}

    private static int animationTableConstant(String name) {
        String code = Sources.code(ENTITIES.resolve("animation/AnimationTable.java"));
        return JavaText.intConstants(code, Map.of()).get(name);
    }

    public static List<Mob> mobs() {
        List<Mob> mobs = new ArrayList<>();
        ANIMATION_FILES.forEach((name, animationFile) -> mobs.add(mob(name, animationFile)));
        return mobs;
    }

    public static Mob mob(String name) {
        return mob(name, ANIMATION_FILES.get(name));
    }

    public static List<String> declaredMobNames() {
        return new ArrayList<>(ANIMATION_FILES.keySet());
    }

    private static Mob mob(String name, String animationFile) {
        Path source = ENTITIES.resolve(name + ".java");
        return new Mob(name, source, Sources.code(source), ANIMATIONS.resolve(animationFile));
    }

    public static List<Clip> clipsIn(String code) {
        Map<String, String> prefixes = new LinkedHashMap<>();
        Matcher strings = STRING_CONSTANT.matcher(code);
        while (strings.find()) {
            prefixes.put(strings.group(1), strings.group(2));
        }
        List<Clip> clips = new ArrayList<>();
        Matcher m = CLIP_FIELD.matcher(code);
        while (m.find()) {
            String name = prefixes.getOrDefault(m.group(3), "") + m.group(4);
            clips.add(new Clip(m.group(1), name, loopOf(m.group(2), m.group(5)), OptionalInt.empty()));
        }
        return clips;
    }

    private static final Pattern BEGIN = Pattern.compile("RawAnimation\\.begin\\(");
    private static final Pattern ENUM_HEAD = Pattern.compile("\\benum\\s+(\\w+)[^{]*\\{");
    private static final Pattern ENUM_ASSIGNMENT = Pattern.compile("this\\.(\\w+)\\s*=\\s*RawAnimation\\.begin\\(\\)\\s*\\.(thenPlayAndHold|thenLoop|thenPlay|then)\\(");
    private static final Pattern ENUM_CONSTANT = Pattern.compile("^\\s*\\w+\\s*(?:\\((.*)\\))?\\s*$", Pattern.DOTALL);
    private static final Pattern STRING_LITERAL = Pattern.compile("^\\s*\"([^\"]*)\"\\s*$");

    private record EnumScan(int sites, List<Clip> clips) {
    }

    /// Clips that an enum constructor assigns to its fields, one per enum constant.
    public static List<Clip> enumClips(String code) {
        return scanEnums(code).clips();
    }

    /// Number of RawAnimation.begin( calls that neither a field declaration nor an enum constructor explains.
    public static int unparsedClipSites(String code) {
        int sites = (int) BEGIN.matcher(code).results().count();
        int understood = (int) CLIP_FIELD.matcher(code).results().count() + scanEnums(code).sites();
        return Math.max(0, sites - understood);
    }

    private static EnumScan scanEnums(String code) {
        Map<String, String> strings = new LinkedHashMap<>();
        Matcher stringConstants = STRING_CONSTANT.matcher(code);
        while (stringConstants.find()) {
            strings.put(stringConstants.group(1), stringConstants.group(2));
        }
        int sites = 0;
        List<Clip> clips = new ArrayList<>();
        Matcher head = ENUM_HEAD.matcher(code);
        while (head.find()) {
            String body = JavaText.inside(code, head.end() - 1);
            int constantsEnd = body.indexOf(';');
            Matcher constructor = Pattern.compile("(?<![\\w.])" + head.group(1) + "\\s*\\(([^)]*)\\)\\s*\\{").matcher(body);

            if (constantsEnd < 0 || !constructor.find(constantsEnd)) {
                continue;
            }

            List<String> parameters = new ArrayList<>();
            for (String parameter : JavaText.splitTopLevel(constructor.group(1), ',')) {
                String[] words = parameter.trim().split("\\s+");
                parameters.add(words[words.length - 1]);
            }
            List<String> constants = JavaText.splitTopLevel(body.substring(0, constantsEnd), ',');
            String constructorBody = JavaText.inside(body, constructor.end() - 1);
            Matcher assignment = ENUM_ASSIGNMENT.matcher(constructorBody);
            while (assignment.find()) {
                List<String> arguments = JavaText.splitTopLevel(JavaText.inside(constructorBody, assignment.end() - 1), ',');
                List<Clip> local = new ArrayList<>();
                for (String constant : constants) {
                    Map<String, String> values = bindConstant(parameters, constant);
                    String name = resolveText(arguments.get(0), values, strings);

                    if (name == null) {
                        local.clear();
                        break;
                    }

                    String loop = arguments.size() > 1 ? values.getOrDefault(
                            arguments.get(1).trim(),
                            arguments.get(1).trim()
                    ) : null;
                    String explicit = loop == null ? null : loop.substring(loop.lastIndexOf('.') + 1);
                    local.add(new Clip(assignment.group(1), name, loopOf(assignment.group(2), explicit), OptionalInt.empty()));
                }

                if (!local.isEmpty()) {
                    sites++;
                    clips.addAll(local);
                }
            }
        }
        return new EnumScan(sites, clips);
    }

    private static Map<String, String> bindConstant(List<String> parameters, String constant) {
        Map<String, String> values = new LinkedHashMap<>();
        Matcher m = ENUM_CONSTANT.matcher(constant);

        if (m.matches() && m.group(1) != null) {
            List<String> arguments = JavaText.splitTopLevel(m.group(1), ',');
            for (int i = 0; i < Math.min(arguments.size(), parameters.size()); i++) {
                values.put(parameters.get(i), arguments.get(i).trim());
            }
        }

        return values;
    }

    /// Joins literals, bound parameters and string constants, or returns null when a part is unknown.
    private static String resolveText(String expression, Map<String, String> values, Map<String, String> strings) {
        StringBuilder text = new StringBuilder();
        for (String part : JavaText.splitTopLevel(expression, '+')) {
            String token = part.trim();
            Matcher literal = STRING_LITERAL.matcher(token);
            String bound = values.get(token);

            if (literal.matches()) {
                text.append(literal.group(1));
            } else if (bound != null && STRING_LITERAL.matcher(bound).matches()) {
                text.append(bound, 1, bound.length() - 1);
            } else if (strings.containsKey(token)) {
                text.append(strings.get(token));
            } else {
                return null;
            }
        }
        return text.toString();
    }

    private static Loop loopOf(String method, String explicit) {
        return switch (method) {
            case "thenPlayAndHold" -> Loop.HOLD;
            case "thenLoop" -> Loop.LOOP;
            case "thenPlay" -> Loop.DEFAULT;
            default -> switch (explicit == null ? "DEFAULT" : explicit) {
                case "LOOP" -> Loop.LOOP;
                case "HOLD_ON_LAST_FRAME" -> Loop.HOLD;
                case "PLAY_ONCE" -> Loop.PLAY_ONCE;
                default -> Loop.DEFAULT;
            };
        };
    }

    /// Mood constants of the tormented villager: name, clip, loop and state length.
    public static List<Clip> moodClips(String code, Map<String, Integer> constants) {
        List<Clip> clips = new ArrayList<>();
        int enumAt = code.indexOf("enum Mood");

        if (enumAt < 0) {
            return clips;
        }

        int open = code.indexOf('{', enumAt);
        String body = JavaText.inside(code, open);
        String constantsPart = body.substring(0, body.indexOf(';'));
        Matcher m = MOOD_CONSTANT.matcher(constantsPart);
        while (m.find()) {
            Loop loop = loopOf("then", switch (m.group(3)) {
                case "LOOP" -> "LOOP";
                case "HOLD_ON_LAST_FRAME" -> "HOLD_ON_LAST_FRAME";
                case "PLAY_ONCE" -> "PLAY_ONCE";
                default -> "DEFAULT";
            });
            clips.add(new Clip(m.group(1), "animation.tormented_villager." + m.group(2), loop, JavaText.evaluate(m.group(4), constants)));
        }
        return clips;
    }

    public static JsonObject animationsOf(String text) {
        return Json.parseObject(text).getAsJsonObject("animations");
    }

    /// The animation whose key equals {@code clip} or ends with it after a dot, or null.
    public static JsonObject animation(JsonObject animations, String clip) {
        String key = animationKey(animations, clip);
        return key == null ? null : animations.getAsJsonObject(key);
    }

    public static String animationKey(JsonObject animations, String clip) {
        if (animations.has(clip)) {
            return clip;
        }

        for (String key : animations.keySet()) {
            if (key.endsWith("." + clip)) {
                return key;
            }
        }
        return null;
    }

    public static boolean hasClip(JsonObject animations, String name) {
        return animationKey(animations, name) != null;
    }

    public static FileLoop fileLoop(JsonObject animation) {
        JsonElement loop = animation.get("loop");

        if (loop == null || !loop.isJsonPrimitive()) {
            return FileLoop.ONCE;
        }

        String text = loop.getAsString();
        return switch (text) {
            case "true" -> FileLoop.LOOP;
            case "hold_on_last_frame" -> FileLoop.HOLD;
            default -> FileLoop.ONCE;
        };
    }

    public static double lengthSeconds(JsonObject animation) {
        return animation.get("animation_length").getAsDouble();
    }

    /// Whole ticks that cover the clip. A fraction of a tick counts as one more.
    public static int lengthTicks(JsonObject animation) {
        return (int) Math.ceil(lengthSeconds(animation) * 20.0D - 1.0E-6D);
    }

    /// What the clip really does once Java and the file are both read.
    public static FileLoop effective(Loop java, FileLoop file) {
        return switch (java) {
            case LOOP -> FileLoop.LOOP;
            case HOLD -> FileLoop.HOLD;
            case PLAY_ONCE -> FileLoop.ONCE;
            case DEFAULT -> file;
        };
    }
}
