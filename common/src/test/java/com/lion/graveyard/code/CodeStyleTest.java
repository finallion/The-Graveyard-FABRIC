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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/// Enforces the layout rules on every Java source: braces on control flow, blank lines around if blocks,
/// one call per line in stream and builder chains, empty constructors on one line, long parameter and argument
/// lists one element per line. Comments, strings and chars are masked before scanning.
class CodeStyleTest {
    static final String BRACES = "braces";
    static final String BLANK_BEFORE_IF = "blank-before-if";
    static final String BLANK_AFTER_IF = "blank-after-if";
    static final String CHAIN = "chain";
    static final String EMPTY_CTOR = "empty-ctor";
    static final String PARAMS = "params";
    static final String ARGS = "args";
    private static final int MAX_WIDTH = 120;

    private static final Pattern CONTROL = Pattern.compile("\\b(if|for|while|else)\\b");
    private static final Pattern CASE_LABEL = Pattern.compile("^(case\\b.*|default)\\s*(:|->)");
    private static final Pattern CALL = Pattern.compile("\\)\\s*\\.\\s*(\\w+)\\s*\\(");
    private static final Pattern STREAM_SOURCE = Pattern.compile("\\.stream\\(\\)|Stream\\.of\\(|Arrays\\.stream\\(|\\.entrySet\\(\\)\\s*\\.\\s*stream");
    private static final Pattern GETTER = Pattern.compile("^(get|is)([A-Z].*)?$");
    private static final Pattern EMPTY_CONSTRUCTOR = Pattern.compile(
            "^[ \\t]*((?:public|private|protected)[ \\t]+)?([A-Z]\\w*)[ \\t]*\\([ \\t]*\\)[ \\t]*\\{[ \\t]*\\n[ \\t]*\\}",
            Pattern.MULTILINE
    );
    private static final Pattern LAST_WORD = Pattern.compile("(\\w+)\\s*$");
    private static final Pattern MAP_BEFORE = Pattern.compile("Map\\s*\\.\\s*$");
    private static final Pattern PREVIOUS_WORD = Pattern.compile("(\\w+)$");
    private static final Set<String> KEYWORDS = Set.of(
            "if",
            "while",
            "for",
            "switch",
            "catch",
            "synchronized",
            "try",
            "return",
            "assert",
            "throw",
            "else",
            "do",
            "new",
            "case",
            "yield"
    );

    record Finding(String rule, int line, String text) {
    }

    private record Span(int start, int end) {}

    static List<Path> sourceFiles() {
        List<Path> roots = List.of(
                ModProject.COMMON_MAIN.resolve("java"),
                ModProject.FABRIC_MAIN.resolve("java"),
                ModProject.NEOFORGE_MAIN.resolve("java"),
                ModProject.ROOT.resolve("common/src/test/java")
        );
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

        Matcher constructor = EMPTY_CONSTRUCTOR.matcher(masked);

        while (constructor.find()) {
            int brace = masked.indexOf('{', constructor.start());
            boolean enumConstant = constructor.group(1) == null && constructor.group(2).equals(constructor.group(2).toUpperCase())
                    || masked.substring(0, constructor.start()).stripTrailing().endsWith(",");

            if (!enumConstant && src.substring(brace + 1, constructor.end() - 1).isBlank()) {
                found.add(finding(EMPTY_CTOR, lineOf(starts, constructor.start()), lines));
            }
        }

        found.addAll(badLists(src, masked, lines, starts));
        return found;
    }

    private static List<Span> elementsOf(String masked, int open, int close) {
        List<int[]> ranges = new ArrayList<>();
        int depth = 0;
        int generic = 0;
        int begin = open + 1;

        for (int i = open + 1; i < close; i++) {
            char c = masked.charAt(i);

            if ("([{".indexOf(c) >= 0) {
                depth++;
            } else if (")]}".indexOf(c) >= 0) {
                depth--;
            } else if (depth == 0) {
                if (c == '<' && isWordChar(masked.charAt(i - 1)) && isGenericStart(masked.charAt(i + 1))) {
                    generic++;
                } else if (c == '>' && generic > 0 && masked.charAt(i - 1) != '-') {
                    generic--;
                } else if (c == ',' && generic == 0) {
                    ranges.add(new int[] {begin, i});
                    begin = i + 1;
                }
            }
        }

        ranges.add(new int[] {begin, close});
        List<Span> spans = new ArrayList<>();

        for (int[] range : ranges) {
            int s = range[0];
            int e = range[1];

            while (s < e && Character.isWhitespace(masked.charAt(s))) {
                s++;
            }

            while (e > s && Character.isWhitespace(masked.charAt(e - 1))) {
                e--;
            }

            if (e > s) {
                spans.add(new Span(s, e));
            }
        }

        return spans;
    }

    private static boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private static boolean isGenericStart(char c) {
        return isWordChar(c) || c == '?';
    }

    private static List<Finding> badLists(String src, String masked, String[] lines, int[] starts) {
        List<Finding> found = new ArrayList<>();

        for (int open = masked.indexOf('('); open >= 0; open = masked.indexOf('(', open + 1)) {
            String head = masked.substring(Math.max(0, open - 200), open);
            Matcher word = LAST_WORD.matcher(head);
            boolean hasWord = word.find();

            if (hasWord && KEYWORDS.contains(word.group(1))) {
                continue;
            }

            int close = closing(masked, open);

            if (close < 0 || masked.substring(open + 1, close).indexOf('{') >= 0 || src.substring(open, close).contains("\"\"\"")) {
                continue;
            }

            List<Span> elements = elementsOf(masked, open, close);

            if (elements.isEmpty()) {
                continue;
            }

            int first = lineOf(starts, open);
            int last = lineOf(starts, close);
            int indent = lines[first].length() - lines[first].stripLeading().length();
            boolean declaration = isDeclaration(masked, head, word, hasWord, close);
            String rule = declaration ? PARAMS : ARGS;

            if (first == last) {
                if (declaration && close - starts[first] + 1 + 2 > MAX_WIDTH) {
                    found.add(finding(rule, first, lines));
                }

                continue;
            }

            if (elements.size() < 2) {
                continue;
            }

            int cut = declaration ? close + 1 : starts[last] + lines[last].length();
            String flat = src.substring(starts[first], cut)
                    .replaceAll("\\s*\\n\\s*", " ")
                    .replaceAll("\\(\\s+", "(")
                    .replaceAll("\\s+\\)", ")");

            if (flat.length() + (declaration ? 2 : 0) <= MAX_WIDTH) {
                String listText = src.substring(open, close);
                boolean commented = listText.contains("//") || listText.contains("/*");

                if (!hasMultiLineElement(masked, elements) && !commented && !isChainViolation(flat)) {
                    found.add(finding(rule, first, lines));
                }

                continue;
            }

            boolean mapOf = hasWord && word.group(1).equals("of") && MAP_BEFORE.matcher(head.substring(0, word.start(1))).find();
            int unitSize = mapOf ? 2 : 1;
            List<Integer> unitLines = new ArrayList<>();
            boolean shared = false;

            for (int k = 0; k < elements.size(); k += unitSize) {
                int unitLine = lineOf(starts, elements.get(k).start());
                unitLines.add(unitLine);

                if (unitSize == 2 && k + 1 < elements.size() && lineOf(starts, elements.get(k + 1).start()) != unitLine) {
                    shared = true;
                }
            }

            for (int k = 0; k < unitLines.size(); k++) {
                shared |= unitLines.indexOf(unitLines.get(k)) != k;
            }

            String closeText = masked.substring(starts[last], close);
            boolean clean = !shared && unitLines.get(0) > first && closeText.isBlank() && closeText.length() == indent;

            if (!clean) {
                found.add(finding(rule, first, lines));
            }
        }

        return found;
    }

    private static boolean hasMultiLineElement(String masked, List<Span> elements) {
        for (Span element : elements) {
            if (masked.substring(element.start(), element.end()).indexOf('\n') >= 0) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDeclaration(String masked, String head, Matcher word, boolean hasWord, int close) {
        if (!hasWord) {
            return false;
        }

        String before = head.substring(0, word.start(1)).stripTrailing();
        Matcher previous = PREVIOUS_WORD.matcher(before);
        String after = masked.substring(close + 1, Math.min(masked.length(), close + 40)).stripLeading();
        boolean blockNext = after.startsWith("{") || after.startsWith("throws");
        boolean typed = previous.find() && !KEYWORDS.contains(previous.group(1))
                || (before.endsWith(">") || before.endsWith("]")) && !before.endsWith("->");
        boolean statementStart = before.isEmpty() || before.endsWith(";") || before.endsWith("{") || before.endsWith("}");

        if (before.endsWith(".") || before.endsWith("@")) {
            return false;
        }

        return typed || statementStart && blockNext;
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
    private static final String BROKEN_EMPTY_CTOR = """
            class A {
                private A() {
                }
            }
            """;
    private static final String FIXED_EMPTY_CTOR = """
            class A {
                private A() {}
            }
            """;
    private static final String BROKEN_PARAMS = """
            class A {
                void configure(String firstParameterName, String secondParameterName, String thirdParameterName,
                        String fourthParameterName) {
                }
            }
            """;
    private static final String FIXED_PARAMS = """
            class A {
                void configure(
                        String firstParameterName,
                        String secondParameterName,
                        String thirdParameterName,
                        String fourthParameterName
                ) {
                }
            }
            """;
    private static final String BROKEN_ARGS_WRAPPED = """
            class A {
                static final java.util.List<String> NAMES = java.util.List.of(
                        "first_long_element_name", "second_long_element_name", "third_long_element_name",
                        "fourth_long_element_name", "fifth_long_element_name");
            }
            """;
    private static final String BROKEN_ARGS_LAST_CLOSING = """
            class A {
                static final java.util.List<String> NAMES = java.util.List.of(
                        "first_long_element_name",
                        "second_long_element_name",
                        "third_long_element_name",
                        "fourth_long_element_name",
                        "fifth_long_element_name");
            }
            """;
    private static final String BROKEN_MAP_PAIRS = """
            class A {
                static final java.util.Map<String, String> NAMES = java.util.Map.of(
                        "first_long_key_name", "first_long_value_name", "second_long_key_name", "second_long_value_name",
                        "third_long_key_name", "third_long_value_name");
            }
            """;
    private static final String FIXED_MAP_PAIRS = """
            class A {
                static final java.util.Map<String, String> NAMES = java.util.Map.of(
                        "first_long_key_name", "first_long_value_name",
                        "second_long_key_name", "second_long_value_name",
                        "third_long_key_name", "third_long_value_name"
                );
            }
            """;
    private static final String FIXED_ARGS = """
            class A {
                static final java.util.List<String> NAMES = java.util.List.of(
                        "first_long_element_name",
                        "second_long_element_name",
                        "third_long_element_name",
                        "fourth_long_element_name",
                        "fifth_long_element_name"
                );
                static final java.util.List<String> SHORT = java.util.List.of("a", "b");
            }
            """;
    private static final String BROKEN_LONG_DECLARATION = """
            class A {
                void configure(String firstParameterName, String secondParameterName, String thirdParameterName, String fourthParameterName) {
                }
            }
            """;
    private static final String BROKEN_FITS_ON_ONE_LINE = """
            class A {
                void f() {
                    g(
                            "a",
                            "b"
                    );
                }
            }
            """;
    private static final String FIXED_FITS_ON_ONE_LINE = """
            class A {
                void f() {
                    g("a", "b");
                }
            }
            """;
    private static final String BROKEN_FIRST_ON_OPENING_LINE = """
            class A {
                static final java.util.List<String> NAMES = java.util.List.of("first_long_element_name",
                        "second_long_element_name",
                        "third_long_element_name",
                        "fourth_long_element_name",
                        "fifth_long_element_name"
                );
                static final java.util.List<String> SHORT = java.util.List.of("a", "b");
            }
            """;
    private static final String EXCLUDED_FROM_THE_LIST_RULES = """
            class A {
                enum E {
                    FOO() {
                    },
                    BAR;
                }
                String text = String.format(\"\"\"
                        a very long text block line that goes past the usual width limit of the layout rules for sure yes yes
                        \"\"\", first, second);
                void f() {
                    run(a, () -> {
                        g();
                    });
                    register("first_long_element_name", "second_long_element_name", "third_long_element_name", "fourth_long_element_name");
                }
                @Deprecated(since = "1", forRemoval = true)
                void h() {}
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

    private static final String MASKED_BLOCK = String.join(
            "\n",
            "class A {",
            "    String block = \"\"\"",
            "        while (x) g();",
            "        \"\"\";",
            "}",
            ""
    );

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
    void emptyConstructorRuleCatchesAnEmptyBodyOnTwoLines() {
        assertBrokenAndFixed(EMPTY_CTOR, BROKEN_EMPTY_CTOR, FIXED_EMPTY_CTOR);
    }

    @Test
    void parameterRuleCatchesLongListsThatAreNotOnePerLine() {
        assertBrokenAndFixed(PARAMS, BROKEN_PARAMS, FIXED_PARAMS);
    }

    @Test
    void argumentRuleCatchesWrappedElementsAndAClosingParenthesisOnTheLastElement() {
        assertBrokenAndFixed(ARGS, BROKEN_ARGS_WRAPPED, FIXED_ARGS);
        assertBrokenAndFixed(ARGS, BROKEN_ARGS_LAST_CLOSING, FIXED_ARGS);
    }

    @Test
    void mapOfRuleWantsOneKeyValuePairPerLine() {
        assertBrokenAndFixed(ARGS, BROKEN_MAP_PAIRS, FIXED_MAP_PAIRS);
    }

    @Test
    void parameterRuleCatchesALongDeclarationOnOneLine() {
        assertBrokenAndFixed(PARAMS, BROKEN_LONG_DECLARATION, FIXED_PARAMS);
    }

    @Test
    void listRulesWantAWrappedListThatFitsBackOnOneLine() {
        assertBrokenAndFixed(ARGS, BROKEN_FITS_ON_ONE_LINE, FIXED_FITS_ON_ONE_LINE);
    }

    @Test
    void listRulesWantTheOpeningParenthesisAloneOnItsLine() {
        assertBrokenAndFixed(ARGS, BROKEN_FIRST_ON_OPENING_LINE, FIXED_ARGS);
    }

    @Test
    void listRulesIgnoreOneLineCallsTextBlocksEnumConstantsAndBlockLambdas() {
        assertEquals(List.of(), check(EXCLUDED_FROM_THE_LIST_RULES));
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
