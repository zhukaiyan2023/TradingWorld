package com.tradingworld.embedding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单的词频-based 嵌入服务（临时实现）。
 * 使用词袋模型生成伪向量，用于没有外部 embedding API 的情况。
 * 后续可替换为真正的 embedding 服务（如 OpenAI、MiniMax）。
 */
public class SimpleEmbeddingService implements EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(SimpleEmbeddingService.class);

    private final int dimensions;
    private final Map<String, Integer> vocabulary;

    public SimpleEmbeddingService(int dimensions) {
        this.dimensions = dimensions;
        this.vocabulary = new HashMap<>();
        log.warn("Using SimpleEmbeddingService - this is a placeholder. Replace with real embedding API for production.");
    }

    @Override
    public float[] embed(String text) {
        if (text == null || text.isEmpty()) {
            return new float[dimensions];
        }

        // 构建词频向量
        float[] vector = new float[dimensions];
        String[] words = text.toLowerCase().split("\\s+");

        for (String word : words) {
            word = word.replaceAll("[^a-z0-9]", "");
            if (word.isEmpty()) continue;

            int index = getOrCreateIndex(word);
            if (index < dimensions) {
                vector[index] += 1;
            }
        }

        // 归一化
        normalize(vector);
        return vector;
    }

    @Override
    public List<float[]> embedAll(List<String> texts) {
        List<float[]> results = new ArrayList<>();
        for (String text : texts) {
            results.add(embed(text));
        }
        return results;
    }

    private int getOrCreateIndex(String word) {
        return vocabulary.computeIfAbsent(word, k -> vocabulary.size());
    }

    private void normalize(float[] vector) {
        float norm = 0;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
}
