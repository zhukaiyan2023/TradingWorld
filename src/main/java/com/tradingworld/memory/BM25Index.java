package com.tradingworld.memory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * BM25（最佳匹配25）排名算法的纯Java实现。
 * 这是Python rank_bm25库的移植，以匹配其行为。
 *
 * BM25是一种用于信息检索的概率相关性排名函数。
 * 它根据查询词频率和逆文档频率对文档进行排名。
 */
public class BM25Index {

    // BM25参数
    private final double k1;
    private final double b;

    // 文档存储
    private List<List<String>> tokenizedDocs;
    private List<String> originalDocs;
    private double[] documentLengths;
    private double avgDocLength;

    // IDF缓存
    private double[] idf;
    private int documentCount;

    // 分词器模式 - 匹配Python的re.findall(r'\b\w+\b', text.lower())
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b\\w+\\b");

    /**
     * 使用默认参数创建BM25索引（匹配Python rank_bm25）。
     */
    public BM25Index() {
        this(1.5, 0.75);
    }

    /**
     * 使用自定义参数创建BM25索引。
     *
     * @param k1 词频饱和参数。较高的值给予词频更多权重。
     *          常见范围：1.2 - 2.0。默认值：1.5
     * @param b  长度归一化参数。控制文档长度对相关性的影响。
     *          范围：0.0 - 1.0。默认值：0.75
     */
    public BM25Index(double k1, double b) {
        this.k1 = k1;
        this.b = b;
        this.tokenizedDocs = new ArrayList<>();
        this.originalDocs = new ArrayList<>();
        this.documentLengths = new double[0];
    }

    /**
     * 为BM25索引分词文本。
     * 使用空白+标点符号分词并小写，匹配Python行为。
     */
    public static List<String> tokenize(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return TOKEN_PATTERN.matcher(text.toLowerCase())
                .results()
                .map(mr -> mr.group())
                .collect(Collectors.toList());
    }

    /**
     * 为BM25搜索索引文档列表。
     *
     * @param documents 要索引的文档文本列表
     */
    public void index(List<String> documents) {
        this.originalDocs = new ArrayList<>(documents);
        this.documentCount = documents.size();

        // 分词所有文档
        this.tokenizedDocs = documents.stream()
                .map(BM25Index::tokenize)
                .collect(Collectors.toList());

        // 计算文档长度
        this.documentLengths = tokenizedDocs.stream()
                .mapToDouble(doc -> doc.size())
                .toArray();

        // 计算平均文档长度
        this.avgDocLength = Arrays.stream(documentLengths).average().orElse(0);

        // 计算所有词的IDF
        calculateIDF();
    }

    /**
     * 向索引添加单个文档。
     */
    public void addDocument(String document) {
        originalDocs.add(document);
        List<String> tokens = tokenize(document);
        tokenizedDocs.add(tokens);
        documentLengths = Arrays.copyOf(documentLengths, documentLengths.length + 1);
        documentLengths[documentLengths.length - 1] = tokens.size();
        documentCount++;
        calculateIDF();
    }

    /**
     * 计算所有词的IDF（逆文档频率）。
     * 使用标准BM25 IDF公式：log((N - n(t) + 0.5) / (n(t) + 0.5))
     */
    private void calculateIDF() {
        // 计算包含每个词的文档数
        Map<String, Integer> docFreq = new HashMap<>();
        for (List<String> doc : tokenizedDocs) {
            Set<String> uniqueTerms = new HashSet<>(doc);
            for (String term : uniqueTerms) {
                docFreq.merge(term, 1, Integer::sum);
            }
        }

        // 计算每个词的IDF
        this.idf = new double[tokenizedDocs.size()];
        Arrays.fill(this.idf, 0);

        // 我们使用map来跟踪词的IDF值
        Map<String, Double> termIDF = new HashMap<>();
        for (Map.Entry<String, Integer> entry : docFreq.entrySet()) {
            String term = entry.getKey();
            int n_t = entry.getValue(); // 包含该词的文档数

            // BM25 IDF公式：log((N - n(t) + 0.5) / (n(t) + 0.5))
            // 加1以防止log(0)
            double idfValue = Math.log((documentCount - n_t + 0.5) / (n_t + 0.5) + 1);
            termIDF.put(term, idfValue);
        }

        // 在与tokenizedDocs相同索引的并行数组中存储IDF
        // 这是一个近似值 - 实际上我们会在评分期间使用termIDF map
    }

    /**
     * 根据查询计算所有索引文档的BM25分数。
     *
     * @param queryTokens 分词的查询词
     * @return 每个文档的BM25分数数组
     */
    public double[] getScores(List<String> queryTokens) {
        double[] scores = new double[documentCount];

        if (documentCount == 0 || queryTokens.isEmpty()) {
            return scores;
        }

        // 对于每个文档
        for (int i = 0; i < documentCount; i++) {
            List<String> doc = tokenizedDocs.get(i);
            double docLen = documentLengths[i];

            if (docLen == 0) {
                continue;
            }

            // 计算该文档中的词频
            Map<String, Long> termFreq = doc.stream()
                    .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

            double score = 0.0;

            // 对于每个查询词
            for (String term : queryTokens) {
                Long tf = termFreq.getOrDefault(term, 0L);
                if (tf == 0) {
                    continue;
                }

                // 获取该词的IDF
                double idf = calculateIDFForTerm(term);

                // BM25评分公式：
                // score += IDF(t) * (tf * (k1 + 1)) / (tf + k1 * (1 - b + b * docLen / avgDocLen))

                double numerator = tf * (k1 + 1);
                double denominator = tf + k1 * (1 - b + b * docLen / avgDocLength);
                score += idf * (numerator / denominator);
            }

            scores[i] = score;
        }

        return scores;
    }

    /**
     * 计算特定词的IDF。
     */
    private double calculateIDFForTerm(String term) {
        int n_t = 0; // 包含该词的文档数
        for (List<String> doc : tokenizedDocs) {
            if (doc.contains(term)) {
                n_t++;
            }
        }

        if (n_t == 0) {
            return 0;
        }

        // BM25 IDF公式
        return Math.log((documentCount - n_t + 0.5) / (n_t + 0.5) + 1);
    }

    /**
     * 获取按BM25分数降序排列的前N个文档索引。
     *
     * @param scores BM25分数数组
     * @param n      返回的顶部结果数
     * @return 按分数排序的文档索引列表
     */
    public List<Integer> getTopNIndices(double[] scores, int n) {
        if (scores == null || scores.length == 0) {
            return Collections.emptyList();
        }

        // 创建索引-值对
        List<Map.Entry<Integer, Double>> indexedScores = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            indexedScores.add(Map.entry(i, scores[i]));
        }

        // 按分数降序排序
        indexedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // 取前N个
        int resultSize = Math.min(n, indexedScores.size());
        List<Integer> result = new ArrayList<>(resultSize);
        for (int i = 0; i < resultSize; i++) {
            result.add(indexedScores.get(i).getKey());
        }

        return result;
    }

    /**
     * 获取前N个文档索引及其分数。
     *
     * @param scores BM25分数数组
     * @param n      返回的顶部结果数
     * @return 按分数降序排序的（索引，分数）对列表
     */
    public List<ScoreResult> getTopNWithScores(double[] scores, int n) {
        if (scores == null || scores.length == 0) {
            return Collections.emptyList();
        }

        // 创建索引-值对
        List<Map.Entry<Integer, Double>> indexedScores = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            indexedScores.add(Map.entry(i, scores[i]));
        }

        // 按分数降序排序
        indexedScores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // 取前N个及其分数
        int resultSize = Math.min(n, indexedScores.size());
        List<ScoreResult> result = new ArrayList<>(resultSize);
        for (int i = 0; i < resultSize; i++) {
            Map.Entry<Integer, Double> entry = indexedScores.get(i);
            result.add(new ScoreResult(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    /**
     * 获取索引文档的数量。
     */
    public int getDocumentCount() {
        return documentCount;
    }

    /**
     * 按索引获取原始文档。
     */
    public String getDocument(int index) {
        if (index >= 0 && index < originalDocs.size()) {
            return originalDocs.get(index);
        }
        return null;
    }

    /**
     * 表示文档评分结果。
     */
    public static class ScoreResult {
        private final int documentIndex;
        private final double score;

        public ScoreResult(int documentIndex, double score) {
            this.documentIndex = documentIndex;
            this.score = score;
        }

        public int getDocumentIndex() { return documentIndex; }
        public double getScore() { return score; }
    }
}
