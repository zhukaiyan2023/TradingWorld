package com.tradingworld.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BM25Index实现的单元测试。
 * 测试验证算法是否符合预期行为。
 */
class BM25IndexTest {

    private BM25Index bm25;

    @BeforeEach
    void setUp() {
        bm25 = new BM25Index(1.5, 0.75);
    }

    @Test
    void testTokenize_basic() {
        List<String> tokens = BM25Index.tokenize("Hello World");
        assertEquals(2, tokens.size());
        assertTrue(tokens.contains("hello"));
        assertTrue(tokens.contains("world"));
    }

    @Test
    void testTokenize_lowercase() {
        List<String> tokens = BM25Index.tokenize("PYTHON Java RUST");
        assertEquals(3, tokens.size());
        assertTrue(tokens.contains("python"));
        assertTrue(tokens.contains("java"));
        assertTrue(tokens.contains("rust"));
    }

    @Test
    void testTokenize_specialCharacters() {
        List<String> tokens = BM25Index.tokenize("Price: $100.50, that's great!");
        assertTrue(tokens.contains("price"));
        assertTrue(tokens.contains("100"));
        assertTrue(tokens.contains("50"));
        assertTrue(tokens.contains("that"));
        assertTrue(tokens.contains("s"));
        assertTrue(tokens.contains("great"));
    }

    @Test
    void testTokenize_empty() {
        assertTrue(BM25Index.tokenize("").isEmpty());
        assertTrue(BM25Index.tokenize(null).isEmpty());
    }

    @Test
    void testIndex_singleDocument() {
        List<String> docs = Collections.singletonList("stock price rising high");
        bm25.index(docs);
        assertEquals(1, bm25.getDocumentCount());
    }

    @Test
    void testIndex_multipleDocuments() {
        List<String> docs = Arrays.asList(
            "stock price rising",
            "bond yield falling",
            "market volatility increasing"
        );
        bm25.index(docs);
        assertEquals(3, bm25.getDocumentCount());
    }

    @Test
    void testGetScores_emptyIndex() {
        bm25.index(Collections.emptyList());
        double[] scores = bm25.getScores(BM25Index.tokenize("stock"));
        assertEquals(0, scores.length);
    }

    @Test
    void testGetScores_singleTerm() {
        List<String> docs = Arrays.asList(
            "stock price rising",
            "bond yield falling"
        );
        bm25.index(docs);

        double[] scores = bm25.getScores(BM25Index.tokenize("stock"));
        assertEquals(2, scores.length);
        // 第一个文档对"stock"的评分应该更高
        assertTrue(scores[0] > scores[1]);
    }

    @Test
    void testGetScores_multipleTerms() {
        List<String> docs = Arrays.asList(
            "stock price rising market",
            "stock bond correlation"
        );
        bm25.index(docs);

        double[] scores = bm25.getScores(BM25Index.tokenize("stock price"));
        assertEquals(2, scores.length);
        // 两个文档都包含"stock"，第一个还有"price"
        assertTrue(scores[0] > 0);
        assertTrue(scores[1] > 0);
    }

    @Test
    void testGetScores_noMatch() {
        List<String> docs = Arrays.asList(
            "stock price rising",
            "bond yield falling"
        );
        bm25.index(docs);

        double[] scores = bm25.getScores(BM25Index.tokenize("cryptocurrency"));
        assertEquals(2, scores.length);
        // 没有文档包含"cryptocurrency"
        assertEquals(0.0, scores[0], 0.001);
        assertEquals(0.0, scores[1], 0.001);
    }

    @Test
    void testGetTopNIndices() {
        List<String> docs = Arrays.asList(
            "stock price rising",
            "bond yield falling",
            "market volatility high",
            "interest rates low"
        );
        bm25.index(docs);

        double[] scores = bm25.getScores(BM25Index.tokenize("stock price"));
        List<Integer> topIndices = bm25.getTopNIndices(scores, 2);

        assertEquals(2, topIndices.size());
        // 第一个文档应该排第一（匹配两个词）
        assertEquals(0, topIndices.get(0));
    }

    @Test
    void testGetTopNIndices_withScores() {
        List<String> docs = Arrays.asList(
            "stock price rising",
            "bond yield falling",
            "stock bond correlation"
        );
        bm25.index(docs);

        double[] scores = bm25.getScores(BM25Index.tokenize("stock"));
        List<BM25Index.ScoreResult> topResults = bm25.getTopNWithScores(scores, 2);

        assertEquals(2, topResults.size());
        // 包含"stock"的索引0和2的文档应该被排名
        assertTrue(topResults.get(0).getDocumentIndex() == 0 || topResults.get(0).getDocumentIndex() == 2);
        assertTrue(topResults.get(0).getScore() >= topResults.get(1).getScore());
    }

    @Test
    void testGetDocument() {
        List<String> docs = Arrays.asList("first doc", "second doc");
        bm25.index(docs);

        assertEquals("first doc", bm25.getDocument(0));
        assertEquals("second doc", bm25.getDocument(1));
        assertNull(bm25.getDocument(5));
    }

    @Test
    void testAddDocument() {
        List<String> docs = Arrays.asList("initial doc");
        bm25.index(docs);
        assertEquals(1, bm25.getDocumentCount());

        bm25.addDocument("new document");
        assertEquals(2, bm25.getDocumentCount());
        assertEquals("new document", bm25.getDocument(1));
    }

    @Test
    void testBm25Parameters() {
        // 测试不同的k1和b值
        BM25Index customBm25 = new BM25Index(2.0, 0.5);
        List<String> docs = Arrays.asList("test document for bm25");
        customBm25.index(docs);

        double[] scores = customBm25.getScores(BM25Index.tokenize("test"));
        assertEquals(1, scores.length);
        assertTrue(scores[0] > 0);
    }

    @Test
    void testEmptyQuery() {
        List<String> docs = Arrays.asList("stock price");
        bm25.index(docs);

        double[] scores = bm25.getScores(Collections.emptyList());
        assertEquals(1, scores.length);
        assertEquals(0.0, scores[0], 0.001);
    }

    @Test
    void testDuplicateWordsInQuery() {
        List<String> docs = Arrays.asList("stock price");
        bm25.index(docs);

        // 带有重复词的查询
        double[] scores = bm25.getScores(Arrays.asList("stock", "stock"));
        assertEquals(1, scores.length);
        // 评分应该是正的，但不受重复影响
        assertTrue(scores[0] >= 0);
    }
}
