package com.tradingworld.embedding;

import java.util.List;

/**
 * 向量嵌入服务接口。
 * 用于将文本转换为向量表示，支持语义搜索。
 */
public interface EmbeddingService {

    /**
     * 将单个文本转换为向量
     *
     * @param text 输入文本
     * @return 向量数组
     */
    float[] embed(String text);

    /**
     * 将多个文本批量转换为向量
     *
     * @param texts 输入文本列表
     * @return 向量列表
     */
    List<float[]> embedAll(List<String> texts);

    /**
     * 计算两个向量之间的余弦相似度
     *
     * @param a 向量 a
     * @param b 向量 b
     * @return 相似度分数 (0-1)
     */
    default float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vector dimensions must match");
        }

        float dotProduct = 0;
        float normA = 0;
        float normB = 0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        normA = (float) Math.sqrt(normA);
        normB = (float) Math.sqrt(normB);

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dotProduct / (normA * normB);
    }
}
