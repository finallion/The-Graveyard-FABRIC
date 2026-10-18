package com.lion.graveyard.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// Light text scanning of comment-free Java source. No parser, no type information.
public final class JavaText {
    private static final Set<String> NOT_METHODS = Set.of("if", "for", "while", "switch", "catch", "synchronized", "return", "new", "else", "try", "do");
    private static final Pattern METHOD = Pattern.compile("(\\w+)\\s*\\(([^;{}]*?)\\)\\s*(?:throws\\s+[\\w.,\\s]+)?\\{");
    private static final Pattern INT_CONSTANT = Pattern.compile("(?:(?:public|private|protected)\\s+)?(?:static\\s+)?final\\s+int\\s+(\\w+)\\s*=\\s*([^;]+);");

    private JavaText() {}

    /// Index of the bracket that closes the one at {@code open}. Strings and chars are skipped.
    public static int closing(String text, int open) {
        char openChar = text.charAt(open);
        char closeChar = openChar == '(' ? ')' : openChar == '{' ? '}' : ']';
        int depth = 0;
        for (int i = open; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '"' || c == '\'') {
                i = skipLiteral(text, i);
            } else if (c == openChar) {
                depth++;
            } else if (c == closeChar && --depth == 0) {
                return i;
            }
        }
        return text.length() - 1;
    }

    private static int skipLiteral(String text, int start) {
        char quote = text.charAt(start);
        int i = start + 1;
        while (i < text.length() && text.charAt(i) != quote) {
            i += text.charAt(i) == '\\' ? 2 : 1;
        }
        return i;
    }

    /// Text between the bracket at {@code open} and its partner.
    public static String inside(String text, int open) {
        return text.substring(open + 1, closing(text, open));
    }

    /// Arguments of every call that starts with {@code prefix}, such as ".when(".
    public static List<String> callArguments(String code, String prefix) {
        List<String> result = new ArrayList<>();
        int from = 0;
        while (true) {
            int at = code.indexOf(prefix, from);

            if (at < 0) {
                return result;
            }

            int open = at + prefix.length() - 1;
            result.add(inside(code, open));
            from = open + 1;
        }
    }

    public static List<String> splitTopLevel(String text, char separator) {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '"' || c == '\'') {
                i = skipLiteral(text, i);
            } else if (c == '(' || c == '{' || c == '[') {
                depth++;
            } else if (c == ')' || c == '}' || c == ']') {
                depth--;
            } else if (c == separator && depth == 0) {
                parts.add(text.substring(start, i));
                start = i + 1;
            }
        }
        parts.add(text.substring(start));
        return parts;
    }

    /// Position of a method or constructor body: from the opening brace to the closing brace.
    public record Span(String name, int start, int end) {
        public boolean contains(int position) {
            return position > start && position < end;
        }
    }

    public static List<Span> methodSpans(String code) {
        List<Span> spans = new ArrayList<>();
        Matcher m = METHOD.matcher(code);
        while (m.find()) {
            String name = m.group(1);

            if (NOT_METHODS.contains(name) || precededByNew(code, m.start())) {
                continue;
            }

            int open = m.end() - 1;
            spans.add(new Span(name, open, closing(code, open)));
        }
        return spans;
    }

    /// Method and constructor bodies by name. Overloads and nested classes add to the same list.
    public static Map<String, List<String>> methods(String code) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Span span : methodSpans(code)) {
            result.computeIfAbsent(span.name(), k -> new ArrayList<>()).add(code.substring(span.start() + 1, span.end()));
        }
        return result;
    }

    private static boolean precededByNew(String code, int start) {
        int i = start - 1;
        while (i >= 0 && Character.isWhitespace(code.charAt(i))) {
            i--;
        }
        return i >= 2 && code.startsWith("new", i - 2) && (i < 3 || !Character.isLetterOrDigit(code.charAt(i - 3)));
    }

    public static String joinedBodies(Map<String, List<String>> methods, String name) {
        return String.join("\n", methods.getOrDefault(name, List.of()));
    }

    /// Integer constants of one class, resolved against {@code known} and each other.
    public static Map<String, Integer> intConstants(String code, Map<String, Integer> known) {
        Map<String, String> raw = new LinkedHashMap<>();
        Matcher m = INT_CONSTANT.matcher(code);
        while (m.find()) {
            raw.put(m.group(1), m.group(2).trim());
        }
        Map<String, Integer> resolved = new HashMap<>(known);
        boolean progress = true;
        while (progress) {
            progress = false;
            for (Map.Entry<String, String> entry : raw.entrySet()) {
                if (!resolved.containsKey(entry.getKey())) {
                    OptionalInt value = evaluate(entry.getValue(), resolved);

                    if (value.isPresent()) {
                        resolved.put(entry.getKey(), value.getAsInt());
                        progress = true;
                    }
                }
            }
        }
        return resolved;
    }

    /// Integer arithmetic over literals, constants, plus, minus, times and parentheses.
    public static OptionalInt evaluate(String expression, Map<String, Integer> constants) {
        try {
            Parser parser = new Parser(expression.replaceAll("\\s+", ""), constants);
            int value = parser.sum();
            return parser.atEnd() ? OptionalInt.of(value) : OptionalInt.empty();
        } catch (RuntimeException e) {
            return OptionalInt.empty();
        }
    }

    private static final class Parser {
        private final String text;
        private final Map<String, Integer> constants;
        private int index;

        Parser(String text, Map<String, Integer> constants) {
            this.text = text;
            this.constants = constants;
        }

        boolean atEnd() {
            return index == text.length();
        }

        int sum() {
            int value = product();
            while (index < text.length() && (text.charAt(index) == '+' || text.charAt(index) == '-')) {
                char op = text.charAt(index++);
                int right = product();
                value = op == '+' ? value + right : value - right;
            }
            return value;
        }

        int product() {
            int value = atom();
            while (index < text.length() && text.charAt(index) == '*') {
                index++;
                value *= atom();
            }
            return value;
        }

        int atom() {
            if (text.charAt(index) == '(') {
                index++;
                int value = sum();

                if (text.charAt(index++) != ')') {
                    throw new IllegalStateException();
                }

                return value;
            }

            int start = index;
            while (index < text.length() && (Character.isLetterOrDigit(text.charAt(index)) || text.charAt(index) == '_' || text.charAt(index) == '.')) {
                index++;
            }
            String token = text.substring(start, index);

            if (token.isEmpty()) {
                throw new IllegalStateException();
            }

            if (Character.isDigit(token.charAt(0))) {
                return Integer.parseInt(token);
            }

            Integer value = constants.get(token.substring(token.lastIndexOf('.') + 1));

            if (value == null) {
                throw new IllegalStateException();
            }

            return value;
        }
    }

    private static final Pattern CALLED = Pattern.compile("(?<![\\w.])(?:this\\.)?(\\w+)\\s*\\(|\\b\\w+::(\\w+)");

    /// The text plus the bodies of every method it calls or references, down to {@code depth} levels.
    public static String reachableText(String text, Map<String, List<String>> methods, int depth) {
        StringBuilder out = new StringBuilder(text);
        collectCalled(text, methods, new HashSet<>(), depth, out);
        return out.toString();
    }

    private static void collectCalled(
            String text,
            Map<String, List<String>> methods,
            Set<String> visited,
            int depth,
            StringBuilder out
    ) {
        if (depth <= 0) {
            return;
        }

        Matcher called = CALLED.matcher(text);
        while (called.find()) {
            String name = called.group(1) != null ? called.group(1) : called.group(2);

            if (methods.containsKey(name) && visited.add(name)) {
                String body = String.join("\n", methods.get(name));
                out.append('\n').append(body);
                collectCalled(body, methods, visited, depth - 1, out);
            }
        }
    }

    public static Set<String> identifiers(String text) {
        Set<String> names = new HashSet<>();
        Matcher m = Pattern.compile("(?<![\\w.])(?:this\\.)?([A-Za-z_]\\w*)").matcher(text);
        while (m.find()) {
            names.add(m.group(1));
        }
        return names;
    }
}
