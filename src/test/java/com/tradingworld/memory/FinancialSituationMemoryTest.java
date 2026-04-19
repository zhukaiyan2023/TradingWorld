package com.tradingworld.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FinancialSituationMemory的单元测试。
 */
class FinancialSituationMemoryTest {

    private FinancialSituationMemory memory;

    @BeforeEach
    void setUp() {
        memory = new FinancialSituationMemory("test_memory");
    }

    @Test
    void testAddSingleSituation() {
        memory.addSituation("High inflation", "Consider defensive stocks");
        assertEquals(1, memory.size());
    }

    @Test
    void testAddSituations() {
        List<FinancialSituationMemory.Pair<String, String>> situations = Arrays.asList(
            FinancialSituationMemory.Pair.of("High inflation", "Consider defensive stocks"),
            FinancialSituationMemory.Pair.of("Tech volatility", "Reduce tech exposure")
        );
        memory.addSituations(situations);
        assertEquals(2, memory.size());
    }

    @Test
    void testGetMemories_empty() {
        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("test query", 3);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetMemories_singleMatch() {
        memory.addSituation("High inflation rate", "Consider consumer staples");
        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("inflation concerns", 1);
        assertEquals(1, results.size());
    }

    @Test
    void testGetMemories_multipleMatches() {
        List<FinancialSituationMemory.Pair<String, String>> situations = Arrays.asList(
            FinancialSituationMemory.Pair.of("High inflation rate", "Consider defensive stocks"),
            FinancialSituationMemory.Pair.of("Tech sector volatility", "Reduce tech exposure"),
            FinancialSituationMemory.Pair.of("Strong dollar", "Hedge currency exposure")
        );
        memory.addSituations(situations);

        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("inflation impact", 2);
        assertEquals(2, results.size());
        // 第一个结果应该是关于通胀的
        assertTrue(results.get(0).getMatchedSituation().contains("inflation"));
    }

    @Test
    void testGetMemories_noMatch() {
        memory.addSituation("Tech stocks rising", "Consider growth stocks");
        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("agricultural futures", 1);
        assertEquals(1, results.size());
        // 仍然会返回一些内容，只是分数较低
        assertTrue(results.get(0).getSimilarityScore() < 1.0);
    }

    @Test
    void testGetMemories_normalizedScore() {
        memory.addSituation("Test situation", "Test advice");
        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("Test", 1);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getSimilarityScore() >= 0.0);
        assertTrue(results.get(0).getSimilarityScore() <= 1.0);
    }

    @Test
    void testGetMemories_respectsN() {
        List<FinancialSituationMemory.Pair<String, String>> situations = Arrays.asList(
            FinancialSituationMemory.Pair.of("First situation", "First advice"),
            FinancialSituationMemory.Pair.of("Second situation", "Second advice"),
            FinancialSituationMemory.Pair.of("Third situation", "Third advice"),
            FinancialSituationMemory.Pair.of("Fourth situation", "Fourth advice")
        );
        memory.addSituations(situations);

        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("test", 2);
        assertEquals(2, results.size());
    }

    @Test
    void testClear() {
        memory.addSituation("Test", "Advice");
        assertEquals(1, memory.size());
        memory.clear();
        assertEquals(0, memory.size());
    }

    @Test
    void testMemoryResult_toString() {
        FinancialSituationMemory.MemoryResult result = new FinancialSituationMemory.MemoryResult(
            "This is a long situation description that should be truncated",
            "This is a long advice description that should be truncated",
            0.75
        );
        String str = result.toString();
        assertTrue(str.contains("0.750"));
        assertTrue(str.contains("..."));
    }

    @Test
    void testMemoryName() {
        assertEquals("test_memory", memory.getName());
    }

    @Test
    void testPair_of() {
        var pair = FinancialSituationMemory.Pair.of("key", "value");
        assertEquals("key", pair.first());
        assertEquals("value", pair.second());
    }

    @Test
    void testBuilder() {
        FinancialSituationMemory built = FinancialSituationMemory.builder()
            .name("custom_memory")
            .k1(2.0)
            .b(0.5)
            .build();

        assertEquals("custom_memory", built.getName());
        assertEquals(0, built.size());
    }

    @Test
    void testExactMatch() {
        memory.addSituation("Exact match situation", "Exact advice");
        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("Exact match situation", 1);
        assertEquals(1, results.size());
        // 完全匹配应该有较高的分数
        assertTrue(results.get(0).getSimilarityScore() > 0.5);
    }

    @Test
    void testCaseInsensitive() {
        memory.addSituation("UPPERCASE SITUATION", "Advice");
        List<FinancialSituationMemory.MemoryResult> results = memory.getMemories("uppercase situation", 1);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getSimilarityScore() > 0);
    }
}
