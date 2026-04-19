package com.tradingworld.memory;

import com.tradingworld.embedding.EmbeddingService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 混合记忆系统。
 * 结合 BM25 精确匹配和向量嵌入语义搜索的优点。
 */
public class HybridMemory {

    private final String name;
    private final FinancialSituationMemory bm25Memory;
    private final EmbeddingMemory embeddingMemory;

    // BM25 和向量搜索的权重比例
    private final double bm25Weight;
    private final double embeddingWeight;

    public HybridMemory(String name, EmbeddingService embeddingService) {
        this(name, embeddingService, 0.5, 0.5);
    }

    public HybridMemory(String name, EmbeddingService embeddingService, double bm25Weight, double embeddingWeight) {
        this.name = name;
        this.bm25Memory = new FinancialSituationMemory(name);
        this.embeddingMemory = new EmbeddingMemory(name, embeddingService);
        this.bm25Weight = bm25Weight;
        this.embeddingWeight = embeddingWeight;
    }

    /**
     * 添加情境-建议配对
     */
    public void addSituation(String situation, String recommendation) {
        bm25Memory.addSituation(situation, recommendation);
        embeddingMemory.addSituation(situation, recommendation);
    }

    /**
     * 添加多个情境-建议配对
     */
    public void addSituations(List<FinancialSituationMemory.Pair<String, String>> pairs) {
        for (FinancialSituationMemory.Pair<String, String> pair : pairs) {
            addSituation(pair.first(), pair.second());
        }
    }

    /**
     * 混合搜索 - 结合 BM25 和向量搜索的结果
     *
     * @param currentSituation 当前情境
     * @param nMatches 返回数量
     * @return 混合检索结果列表
     */
    public List<HybridMemoryResult> getMemories(String currentSituation, int nMatches) {
        // BM25 搜索
        List<FinancialSituationMemory.MemoryResult> bm25Results = bm25Memory.getMemories(currentSituation, nMatches * 2);

        // 向量嵌入搜索
        List<EmbeddingMemory.MemoryResult> embeddingResults = embeddingMemory.getMemories(currentSituation, nMatches * 2);

        // 合并结果，使用加权分数
        List<HybridMemoryResult> hybridResults = new ArrayList<>();
        List<String> processed = new ArrayList<>();

        // 首先处理 BM25 结果
        for (FinancialSituationMemory.MemoryResult bm25 : bm25Results) {
            String key = bm25.getMatchedSituation();
            if (processed.contains(key)) continue;

            float embeddingScore = findEmbeddingScore(embeddingResults, key);
            float hybridScore = (float) (bm25Weight * bm25.getSimilarityScore() + embeddingWeight * embeddingScore);

            hybridResults.add(new HybridMemoryResult(
                key,
                bm25.getRecommendation(),
                bm25.getSimilarityScore(),
                embeddingScore,
                hybridScore
            ));
            processed.add(key);
        }

        // 添加仅在向量搜索中的结果
        for (EmbeddingMemory.MemoryResult embedding : embeddingResults) {
            String key = embedding.matchedSituation();
            if (processed.contains(key)) continue;

            hybridResults.add(new HybridMemoryResult(
                key,
                embedding.recommendation(),
                0, // BM25 分数为 0
                embedding.similarityScore(),
                (float) (embeddingWeight * embedding.similarityScore())
            ));
            processed.add(key);
        }

        // 按混合分数排序
        hybridResults.sort(Comparator.comparingDouble(HybridMemoryResult::hybridScore).reversed());

        // 返回前 N 个
        return hybridResults.subList(0, Math.min(nMatches, hybridResults.size()));
    }

    /**
     * 仅使用 BM25 搜索
     */
    public List<FinancialSituationMemory.MemoryResult> searchBm25(String currentSituation, int nMatches) {
        return bm25Memory.getMemories(currentSituation, nMatches);
    }

    /**
     * 仅使用向量嵌入搜索
     */
    public List<EmbeddingMemory.MemoryResult> searchEmbedding(String currentSituation, int nMatches) {
        return embeddingMemory.getMemories(currentSituation, nMatches);
    }

    /**
     * 清除所有记忆
     */
    public void clear() {
        bm25Memory.clear();
        embeddingMemory.clear();
    }

    /**
     * 获取记忆数量
     */
    public int size() {
        return bm25Memory.size();
    }

    /**
     * 获取记忆名称
     */
    public String getName() {
        return name;
    }

    private float findEmbeddingScore(List<EmbeddingMemory.MemoryResult> results, String situation) {
        for (EmbeddingMemory.MemoryResult r : results) {
            if (r.matchedSituation().equals(situation)) {
                return r.similarityScore();
            }
        }
        return 0;
    }

    /**
     * 混合搜索结果
     */
    public record HybridMemoryResult(
        String matchedSituation,
        String recommendation,
        double bm25Score,
        double embeddingScore,
        double hybridScore
    ) {}
}
