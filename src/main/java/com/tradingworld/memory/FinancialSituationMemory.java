package com.tradingworld.memory;

/**
 * 使用BM25存储和检索金融情境的记忆系统。
 * 这是Python FinancialSituationMemory类的Java移植。
 *
 * 使用BM25（最佳匹配25）算法进行检索 - 无API调用，
 * 无令牌限制，可与任何LLM提供商离线工作。
 */
public class FinancialSituationMemory {

    private final String name;
    private final double k1;
    private final double b;

    private final List<String> documents;       // 情境描述
    private final List<String> recommendations; // 相应建议
    private final BM25Index bm25Index;

    /**
     * 使用默认BM25参数创建记忆。
     */
    public FinancialSituationMemory(String name) {
        this(name, 1.5, 0.75);
    }

    /**
     * 使用自定义BM25参数创建记忆。
     */
    public FinancialSituationMemory(String name, double k1, double b) {
        this.name = name;
        this.k1 = k1;
        this.b = b;
        this.documents = new ArrayList<>();
        this.recommendations = new ArrayList<>();
        this.bm25Index = new BM25Index(k1, b);
    }

    /**
     * 添加金融情境及其相应建议。
     *
     * @param situationsAndAdvice (情境, 建议)对列表
     */
    public void addSituations(List<Pair<String, String>> situationsAndAdvice) {
        for (Pair<String, String> pair : situationsAndAdvice) {
            documents.add(pair.first());
            recommendations.add(pair.second());
        }

        // 使用所有文档重建BM25索引
        bm25Index.index(documents);
    }

    /**
     * 添加单个情境及其建议。
     */
    public void addSituation(String situation, String recommendation) {
        documents.add(situation);
        recommendations.add(recommendation);
        bm25Index.addDocument(situation);
    }

    /**
     * 使用BM25相似度找到匹配的建议。
     *
     * @param currentSituation 要匹配当前金融情境
     * @param nMatches         要返回的顶部匹配数
     * @return 包含匹配情境、建议和分数的MemoryResult列表
     */
    public List<MemoryResult> getMemories(String currentSituation, int nMatches) {
        if (documents.isEmpty() || bm25Index.getDocumentCount() == 0) {
            return new ArrayList<>();
        }

        // 分词查询
        List<String> queryTokens = BM25Index.tokenize(currentSituation);

        // 获取BM25分数
        double[] scores = bm25Index.getScores(queryTokens);

        // 获取前N个索引
        List<BM25Index.ScoreResult> topResults = bm25Index.getTopNWithScores(scores, nMatches);

        // 构建结果
        List<MemoryResult> results = new ArrayList<>();
        double maxScore = topResults.isEmpty() ? 1.0 : topResults.get(0).getScore();
        if (maxScore == 0) {
            maxScore = 1.0; // 避免除以零
        }

        for (BM25Index.ScoreResult sr : topResults) {
            // 将分数归一化到0-1范围
            double normalizedScore = sr.getScore() / maxScore;
            int docIndex = sr.getDocumentIndex();

            results.add(new MemoryResult(
                    documents.get(docIndex),
                    recommendations.get(docIndex),
                    normalizedScore
            ));
        }

        return results;
    }

    /**
     * 清除所有存储的记忆。
     */
    public void clear() {
        documents.clear();
        recommendations.clear();
        // 创建新的空索引
        bm25Index.index(new ArrayList<>());
    }

    /**
     * 获取存储情境的数量。
     */
    public int size() {
        return documents.size();
    }

    /**
     * 获取此记忆的名称。
     */
    public String getName() {
        return name;
    }

    /**
     * 用于情境-建议元组的简单对类。
     */
    public static class Pair<F, S> {
        private final F first;
        private final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F first() { return first; }
        public S second() { return second; }

        public static <F, S> Pair<F, S> of(F first, S second) {
            return new Pair<>(first, second);
        }
    }

    /**
     * 记忆检索的结果。
     */
    public static class MemoryResult {
        private final String matchedSituation;
        private final String recommendation;
        private final double similarityScore;

        public MemoryResult(String matchedSituation, String recommendation, double similarityScore) {
            this.matchedSituation = matchedSituation;
            this.recommendation = recommendation;
            this.similarityScore = similarityScore;
        }

        public String getMatchedSituation() { return matchedSituation; }
        public String getRecommendation() { return recommendation; }
        public double getSimilarityScore() { return similarityScore; }

        @Override
        public String toString() {
            return String.format(
                    "MemoryResult{similarity=%.3f, situation='%s', recommendation='%s'}",
                    similarityScore,
                    matchedSituation.length() > 50 ? matchedSituation.substring(0, 50) + "..." : matchedSituation,
                    recommendation.length() > 50 ? recommendation.substring(0, 50) + "..." : recommendation
            );
        }
    }

    // 方便的Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name = "memory";
        private double k1 = 1.5;
        private double b = 0.75;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder k1(double k1) {
            this.k1 = k1;
            return this;
        }

        public Builder b(double b) {
            this.b = b;
            return this;
        }

        public FinancialSituationMemory build() {
            return new FinancialSituationMemory(name, k1, b);
        }
    }
}
