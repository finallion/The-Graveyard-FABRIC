package com.lion.graveyard.code;

import com.lion.graveyard.support.ModProject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Enforces the layout rules on every Java source: braces on control flow, blank lines around if blocks,
/// one call per line in stream and builder chains. Comments, strings and chars are masked before scanning.
class CodeStyleTest {
    static final String BRACES = "braces";
    static final String BLANK_BEFORE_IF = "blank-before-if";
    static final String BLANK_AFTER_IF = "blank-after-if";
    static final String CHAIN = "chain";

    private static final Pattern CONTROL = Pattern.compile("\\b(if|for|while|else)\\b");
    private static final Pattern CASE_LABEL = Pattern.compile("^(case\\b.*|default)\\s*(:|->)");
    private static final Pattern CALL = Pattern.compile("\\)\\s*\\.\\s*(\\w+)\\s*\\(");
    private static final Pattern STREAM_SOURCE = Pattern.compile("\\.stream\\(\\)|Stream\\.of\\(|Arrays\\.stream\\(|\\.entrySet\\(\\)\\s*\\.\\s*stream");
    private static final Pattern GETTER = Pattern.compile("^(get|is)([A-Z].*)?$");

    record Finding(String rule, int line, String text) {
    }

    static List<Path> sourceFiles() {
        List<Path> roots = List.of(
                ModProject.COMMON_MAIN.resolve("java"),
                ModProject.FABRIC_MAIN.resolve("java"),
                ModProject.NEOFORGE_MAIN.resolve("java"),
                ModProject.ROOT.resolve("common/src/test/java"));
        List<Path> all = new ArrayList<>();

        for (Path root : roots) {
            all.addAll(ModProject.files(root, ".java"));
        }

        return all;
    }

    static String mask(String src) {
        StringBuilder out = new StringBuilder(src.length());
        int length = src.length();

        for (int i = 0; i < length; i++) {
            char c = src.charAt(i);
            char next = i + 1 < length ? src.charAt(i + 1) : '\0';

            if (c == '/' && next == '/') {
                while (i < length && src.charAt(i) != '\n') {
                    out.append(' ');
                    i++;
                }

                if (i < length) {
                    out.append('\n');
                }
            } else if (c == '/' && next == '*') {
                out.append("  ");
                i += 2;

                while (i < length && !(src.charAt(i) == '*' && i + 1 < length && src.charAt(i + 1) == '/')) {
                    out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                    i++;
                }

                out.append("  ");
                i++;
            } else if (c == '"' && src.startsWith("\"\"\"", i)) {
                out.append("   ");
                i += 3;

                while (i < length && !src.startsWith("\"\"\"", i)) {
                    out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                    i++;
                }

                out.append("   ");
                i += 2;
            } else if (c == '"' || c == '\'') {
                out.append(c);
                i++;

                while (i < length && src.charAt(i) != c) {
                    if (src.charAt(i) == '\\') {
                        out.append(' ');
                        i++;
                    }

                    if (i < length) {
                        out.append(src.charAt(i) == '\n' ? '\n' : ' ');
                    }

                    i++;
                }

                out.append(c);
            } else {
                out.append(c);
            }
        }

        return out.toString();
    }

    private static int closing(String text, int open) {
        if (open < 0) {
            return -1;
        }

        char opening = text.charAt(open);
        char closer = opening == '(' ? ')' : opening == '{' ? '}' : ']';
        int depth = 0;

        for (int i = open; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == opening) {
                depth++;
            } else if (c == closer && --depth == 0) {
                return i;
            }
        }

        return -1;
    }

    private static int skipWhitespace(String text, int from) {
        int i = from;

        while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
            i++;
        }

        return i;
    }

    private static int lineOf(int[] starts, int offset) {
        int low = 0;
        int high = starts.length - 1;

        while (low < high) {
            int mid = (low + high + 1) >> 1;

            if (starts[mid] <= offset) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }

        return low;
    }

    static List<Finding> check(String source) {
        String src = source.replace("\r\n", "\n");
        String masked = mask(src);
        String[] lines = src.split("\n", -1);
        String[] maskedLines = masked.split("\n", -1);
        List<Integer> startList = new ArrayList<>();
        startList.add(0);

        for (int i = 0; i < masked.length(); i++) {
            if (masked.charAt(i) == '\n') {
                startList.add(i + 1);
            }
        }

        int[] starts = startList.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        List<Finding> found = new ArrayList<>();
        Matcher control = CONTROL.matcher(masked);

        while (control.find()) {
            String keyword = control.group(1);
            int at = control.start();

            if (keyword.equals("else")) {
                int next = skipWhitespace(masked, at + 4);

                if (next < masked.length() && masked.charAt(next) != '{' && !masked.startsWith("if", next)) {
                    found.add(finding(BRACES, lineOf(starts, at), lines));
                }

                continue;
            }

            int open = skipWhitespace(masked, at + keyword.length());

            if (open >= masked.length() || masked.charAt(open) != '(') {
                continue;
            }

            int close = closing(masked, open);

            if (close < 0) {
                continue;
            }

            String before = masked.substring(0, at).stripTrailing();

            if (keyword.equals("while") && before.endsWith("}")) {
                continue;
            }

            int next = skipWhitespace(masked, close + 1);
            boolean braced = next < masked.length() && masked.charAt(next) == '{';

            if (!braced) {
                found.add(finding(BRACES, lineOf(starts, at), lines));
            }

            if (!keyword.equals("if") || before.endsWith("else")) {
                continue;
            }

            int line = lineOf(starts, at);
            String previous = line > 0 ? lines[line - 1].trim() : "";
            String previousMasked = line > 0 ? maskedLines[line - 1].trim() : "";
            boolean okBefore = previous.isEmpty()
                    || previousMasked.endsWith("{")
                    || previous.startsWith("//")
                    || previous.startsWith("*")
                    || previous.startsWith("@")
                    || CASE_LABEL.matcher(previous).find()
                    || previousMasked.endsWith("->")
                    || !maskedLines[line].trim().startsWith("if");

            if (!okBefore) {
                found.add(finding(BLANK_BEFORE_IF, line, lines));
            }

            if (!braced) {
                continue;
            }

            int end = closing(masked, next);

            while (end > 0) {
                int after = skipWhitespace(masked, end + 1);

                if (!masked.startsWith("else", after)) {
                    break;
                }

                boolean elseIf = masked.startsWith("if", skipWhitespace(masked, after + 4));
                int target = elseIf ? masked.indexOf('{', closing(masked, masked.indexOf('(', after))) : masked.indexOf('{', after);

                if (target < 0) {
                    break;
                }

                end = closing(masked, target);
            }

            if (end < 0) {
                continue;
            }

            int endLine = lineOf(starts, end);

            if (!maskedLines[endLine].substring(end - starts[endLine] + 1).trim().isEmpty()) {
                continue;
            }

            String following = endLine + 1 < lines.length ? lines[endLine + 1].trim() : "";

            if (!following.isEmpty() && !following.startsWith("}") && !following.startsWith(")")) {
                found.add(finding(BLANK_AFTER_IF, endLine, lines));
            }
        }

        for (int i = 0; i < maskedLines.length; i++) {
            if (isChainViolation(maskedLines[i])) {
                found.add(finding(CHAIN, i, lines));
            }
        }

        return found;
    }

    private static Finding finding(String rule, int zeroBasedLine, String[] lines) {
        return new Finding(rule, zeroBasedLine + 1, lines[zeroBasedLine].trim());
    }

    private static boolean isChainViolation(String maskedLine) {
        List<String> calls = new ArrayList<>();
        Matcher call = CALL.matcher(maskedLine);

        while (call.find()) {
            calls.add(call.group(1));
        }

        if (STREAM_SOURCE.matcher(maskedLine).find() && calls.size() >= 2) {
            return true;
        }

        for (int k = 0; k < calls.size(); k++) {
            String name = calls.get(k);

            if (calls.indexOf(name) != k && !GETTER.matcher(name).matches()) {
                return true;
            }
        }

        return false;
    }

    private static List<String> problems(Path file) throws IOException {
        List<String> problems = new ArrayList<>();

        for (Finding f : check(Files.readString(file))) {
            problems.add(ModProject.display(file) + ":" + f.line() + " " + f.rule() + "  " + f.text());
        }

        return problems;
    }

    @TestFactory
    Stream<DynamicTest> everyJavaFileFollowsTheLayoutRules() {
        return sourceFiles().stream()
                .map(file -> DynamicTest.dynamicTest(ModProject.display(file), () -> {
                    List<String> problems = problems(file);
                    assertTrue(problems.isEmpty(), () -> String.join("\n", problems));
                }));
    }

    @Test
    void theSourceRootsAreFullyCovered() {
        int count = sourceFiles().size();
        assertTrue(count >= 250, "Expected at least 250 Java files, found " + count);
    }

    private static final String BROKEN_BRACES_IF = """
            class A {
                void f(boolean x) {
                    if (x) return;
                }
            }
            """;
    private static final String FIXED_BRACES_IF = """
            class A {
                void f(boolean x) {
                    if (x) {
                        return;
                    }
                }
            }
            """;
    private static final String BROKEN_BRACES_ELSE = """
            class A {
                void f(boolean x) {
                    if (x) {
                        g();
                    } else g();
                }
            }
            """;
    private static final String FIXED_BRACES_ELSE = """
            class A {
                void f(boolean x) {
                    if (x) {
                        g();
                    } else {
                        g();
                    }
                }
            }
            """;
    private static final String BROKEN_BRACES_FOR = """
            class A {
                void f(int n) {
                    for (int i = 0; i < n; i++) g();
                }
            }
            """;
    private static final String FIXED_BRACES_FOR = """
            class A {
                void f(int n) {
                    for (int i = 0; i < n; i++) {
                        g();
                    }
                }
            }
            """;
    private static final String BROKEN_BRACES_WHILE = """
            class A {
                void f(boolean x) {
                    while (x) g();
                }
            }
            """;
    private static final String FIXED_BRACES_WHILE = """
            class A {
                void f(boolean x) {
                    while (x) {
                        g();
                    }
                }
            }
            """;
    private static final String BROKEN_BLANK_BEFORE = """
            class A {
                void f(boolean x) {
                    g();
                    if (x) {
                        g();
                    }
                }
            }
            """;
    private static final String FIXED_BLANK_BEFORE = """
            class A {
                void f(boolean x) {
                    g();

                    if (x) {
                        g();
                    }
                }
            }
            """;
    private static final String BROKEN_BLANK_AFTER = """
            class A {
                void f(boolean x) {
                    if (x) {
                        g();
                    }
                    g();
                }
            }
            """;
    private static final String FIXED_BLANK_AFTER = """
            class A {
                void f(boolean x) {
                    if (x) {
                        g();
                    }

                    g();
                }
            }
            """;
    private static final String BROKEN_BLANK_AFTER_ELSE = """
            class A {
                void f(boolean x) {
                    if (x) {
                        g();
                    } else {
                        g();
                    }
                    g();
                }
            }
            """;
    private static final String FIXED_BLANK_AFTER_ELSE = """
            class A {
                void f(boolean x) {
                    if (x) {
                        g();
                    } else {
                        g();
                    }

                    g();
                }
            }
            """;
    private static final String BROKEN_CHAIN_STREAM = """
            class A {
                Object f() {
                    return list.stream().filter(a).map(b).toList();
                }
            }
            """;
    private static final String FIXED_CHAIN_STREAM = """
            class A {
                Object f() {
                    return list.stream()
                            .filter(a)
                            .map(b)
                            .toList();
                }
            }
            """;
    private static final String BROKEN_CHAIN_BUILDER = """
            class A {
                A() {
                    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, NORTH).with(WATERLOGGED, false));
                }
            }
            """;
    private static final String FIXED_CHAIN_BUILDER = """
            class A {
                A() {
                    this.setDefaultState(this.stateManager.getDefaultState()
                            .with(FACING, NORTH)
                            .with(WATERLOGGED, false));
                }
            }
            """;
    private static final String MASKED_TEXT = """
            class A {
                // if (x) return;
                /* for (;;) g(); */
                String s = "if (x) return; list.stream().filter(a).map(b)";
                char c = '"';
                Object root = list.stream()
                        .filter(a)
                        .toList();
                int size() {
                    return a.getB().getC().getD();
                }
            }
            """;

    private static final String MASKED_BLOCK = String.join("\n",
            "class A {",
            "    String block = \"\"\"",
            "        while (x) g();",
            "        \"\"\";",
            "}",
            "");

    private static List<String> rules(String source) {
        return check(source).stream()
                .map(Finding::rule)
                .distinct()
                .toList();
    }

    private static void assertBrokenAndFixed(String rule, String broken, String fixed) {
        assertTrue(rules(broken).contains(rule), "Broken snippet must report " + rule + ":\n" + broken);
        assertEquals(List.of(), check(fixed), "Fixed snippet must pass:\n" + fixed);
    }

    @Test
    void braceRuleCatchesBodiesWithoutBraces() {
        assertBrokenAndFixed(BRACES, BROKEN_BRACES_IF, FIXED_BRACES_IF);
        assertBrokenAndFixed(BRACES, BROKEN_BRACES_ELSE, FIXED_BRACES_ELSE);
        assertBrokenAndFixed(BRACES, BROKEN_BRACES_FOR, FIXED_BRACES_FOR);
        assertBrokenAndFixed(BRACES, BROKEN_BRACES_WHILE, FIXED_BRACES_WHILE);
    }

    @Test
    void blankLineRuleCatchesAnIfGluedToThePreviousStatement() {
        assertBrokenAndFixed(BLANK_BEFORE_IF, BROKEN_BLANK_BEFORE, FIXED_BLANK_BEFORE);
    }

    @Test
    void blankLineRuleCatchesCodeGluedToTheEndOfAnIfBlock() {
        assertBrokenAndFixed(BLANK_AFTER_IF, BROKEN_BLANK_AFTER, FIXED_BLANK_AFTER);
        assertBrokenAndFixed(BLANK_AFTER_IF, BROKEN_BLANK_AFTER_ELSE, FIXED_BLANK_AFTER_ELSE);
    }

    @Test
    void chainRuleCatchesStreamsAndBuildersOnOneLine() {
        assertBrokenAndFixed(CHAIN, BROKEN_CHAIN_STREAM, FIXED_CHAIN_STREAM);
        assertBrokenAndFixed(CHAIN, BROKEN_CHAIN_BUILDER, FIXED_CHAIN_BUILDER);
    }

    @Test
    void commentsStringsAndTextBlocksAreIgnored() {
        assertEquals(List.of(), check(MASKED_TEXT));
        assertEquals(List.of(), check(MASKED_BLOCK));
    }

    @Test
    void ruleViolationsAreReportedWithTheirLine() {
        List<Finding> findings = check(BROKEN_BRACES_IF);
        assertEquals(1, findings.size());
        assertEquals(3, findings.get(0).line());
        assertFalse(findings.get(0).text().isEmpty());
    }
}
