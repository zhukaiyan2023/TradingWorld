package com.tradingworld.memory;

import com.tradingworld.embedding.EmbeddingService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 基于向量嵌入的记忆系统。
 * 使用语义搜索替代词匹配，可以理解同义词和上下文。
 */
public class EmbeddingMemory {

    private final String name;
    private final EmbeddingService embeddingService;
    private final List<String> situations;
    private final List<String> recommendations;
    private final List<float[]> embeddings;

    public EmbeddingMemory(String name, EmbeddingService embeddingService) {
        this.name = name;
        this.embeddingService = embeddingService;
        this.situations = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.embeddings = new ArrayList<>();
    }

    /**
     * 添加情境-建议配对
     */
    public void addSituation(String situation, String recommendation) {
        situations.add(situation);
        recommendations.add(recommendation);
        embeddings.add(embeddingService.embed(situation));
    }

    /**
     * 添加多个情境-建议配对
     */
    public void addSituations(List<Pair<String, String>> pairs) {
        for (Pair<String, String> pair : pairs) {
            addSituation(pair.first(), pair.second());
        }
    }

    /**
     * 检索相似情境
     *
     * @param currentSituation 当前情境
     * @param nMatches 返回数量
     * @return 相似结果列表
     */
    public List<MemoryResult> getMemories(String currentSituation, int nMatches) {
        if (situations.isEmpty()) {
            return new ArrayList<>();
        }

        float[] queryEmbedding = embeddingService.embed(currentSituation);
        List<ScoreResult> scores = new ArrayList<>();

        for (int i = 0; i < embeddings.size(); i++) {
            float similarity = embeddingService.cosineSimilarity(queryEmbedding, embeddings.get(i));
            scores.add(new ScoreResult(i, similarity));
        }

        // 排序并取前 N 个
        scores.sort(Comparator.comparingDouble(ScoreResult::score).reversed());

        List<MemoryResult> results = new ArrayList<>();
        for (int i = 0; i < Math.min(nMatches, scores.size()); i++) {
            int index = scores.get(i).index();
            results.add(new MemoryResult(
                situations.get(index),
                recommendations.get(index),
                scores.get(i).score()
            ));
        }

        return results;
    }

    /**
     * 清除所有记忆
     */
    public void clear() {
        situations.clear();
        recommendations.clear();
        embeddings.clear();
    }

    /**
     * 获取记忆数量
     */
    public int size() {
        return situations.size();
    }

    /**
     * 获取记忆名称
     */
    public String getName() {
        return name;
    }

    /**
     * 简单的对类
     */
    public record Pair<F, S>(F first, S second) {}

    /**
     * 记忆检索结果
     */
    public record MemoryResult(String matchedSituation, String recommendation, float similarityScore) {}

    /**
     * 分数结果
     */
    private record ScoreResult(int index, float score) {}
}
