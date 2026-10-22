package com.lion.graveyard.support;

import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class Floors {
    private Floors() {}

    public static Stream<DynamicTest> atLeast(int minimum, String what, Stream<DynamicTest> tests) {
        List<DynamicTest> collected = tests.collect(Collectors.toList());
        assertTrue(collected.size() >= minimum, "Only " + collected.size() + " " + what + " found, expected at least " + minimum);
        return collected.stream();
    }
}
