package com.lion.graveyard.code;

import com.lion.graveyard.support.JavaText;
import com.lion.graveyard.support.ModProject;
import com.lion.graveyard.support.Sources;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// A goal does not survive a world reload, but a timer in NBT does.
/// A goal that runs while such a timer is active must restart from that timer.
class SavedGoalTimerTest {
    private static final Pattern SAVED_GETTER = Pattern.compile("(?:nbt|output)\\s*\\.\\s*put\\w+\\(\\s*\"[^\"]+\"\\s*,\\s*(?:this\\.)?(get\\w+)\\(\\s*\\)\\s*\\)");
    private static final Pattern GOAL_CLASS = Pattern.compile("class\\s+(\\w+)\\s+extends\\s+\\w*Goal\\b[^{]*\\{");

    record GoalBody(String name, String code) {
    }

    static List<String> problems(String entityCode) {
        Set<String> saved = new TreeSet<>(Sources.matches(SAVED_GETTER, entityCode, 1));
        List<String> problems = new ArrayList<>();
        for (GoalBody goal : goals(entityCode)) {
            String canContinueToUse = JavaText.joinedBodies(JavaText.methods(goal.code()), "canContinueToUse");
            String canUse = JavaText.joinedBodies(JavaText.methods(goal.code()), "canUse");
            for (String getter : saved) {
                if (activeCheck(getter).matcher(canContinueToUse).find()
                        && !activeCheck(getter).matcher(canUse).find()) {
                    problems.add(goal.name() + " runs on saved " + getter + " but canUse never resumes it");
                }
            }
        }
        return problems;
    }

    static int checkedGoals(String entityCode) {
        Set<String> saved = new TreeSet<>(Sources.matches(SAVED_GETTER, entityCode, 1));
        int count = 0;
        for (GoalBody goal : goals(entityCode)) {
            String canContinueToUse = JavaText.joinedBodies(JavaText.methods(goal.code()), "canContinueToUse");

            if (saved.stream()
                    .anyMatch(getter -> activeCheck(getter).matcher(canContinueToUse).find())) {
                count++;
            }
        }
        return count;
    }

    private static Pattern activeCheck(String getter) {
        return Pattern.compile("\\b" + getter + "\\(\\s*\\)\\s*>\\s*[01]\\b");
    }

    private static List<GoalBody> goals(String code) {
        List<GoalBody> goals = new ArrayList<>();
        Matcher m = GOAL_CLASS.matcher(code);
        while (m.find()) {
            goals.add(new GoalBody(m.group(1), JavaText.inside(code, m.end() - 1)));
        }
        return goals;
    }

    private static List<Path> entityFiles() {
        try (Stream<Path> files = Files.list(ModProject.JAVA.resolve("entities"))) {
            return files.filter(p -> p.toString().endsWith(".java")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @TestFactory
    Stream<DynamicTest> goalsOnSavedTimersResumeAfterReload() {
        return entityFiles().stream()
                .map(file -> DynamicTest.dynamicTest(
                        file.getFileName().toString(),
                        () -> assertEquals(List.of(), problems(Sources.code(file)))
                ));
    }

    @Test
    void lichSpellGoalsAreChecked() {
        assertTrue(checkedGoals(Sources.code(ModProject.JAVA.resolve("entities/LichEntity.java"))) >= 4);
    }

    @Test
    void checkCatchesAGoalWithoutResume() {
        String saved = "void addAdditionalSaveData(CompoundTag nbt) { nbt.putInt(\"HealTimer\", getHealTimer()); }\n";
        String broken = saved + "class HealGoal extends Goal { public boolean canUse() { return getHealTimer() <= -600; }\n"
                + " public boolean canContinueToUse() { return getHealTimer() > 0; } }";
        String fixed = saved + "class HealGoal extends Goal { public boolean canUse() { if (getHealTimer() > 1) { return true; } return false; }\n"
                + " public boolean canContinueToUse() { return getHealTimer() > 0; } }";
        assertEquals(1, problems(broken).size());
        assertEquals(List.of(), problems(fixed));
    }
}
