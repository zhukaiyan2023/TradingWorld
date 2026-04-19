package com.tradingworld.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 简单的内存缓存管理器。
 * 使用 ConcurrentHashMap 实现线程安全，支持 TTL 过期。
 */
public class CacheManager {

    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Duration defaultTtl;

    /**
     * 创建缓存管理器
     *
     * @param defaultTtl 默认 TTL（存活时间）
     */
    public CacheManager(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    /**
     * 创建缓存管理器，使用默认 5 分钟 TTL
     */
    public CacheManager() {
        this(Duration.ofMinutes(5));
    }

    /**
     * 获取缓存值
     *
     * @param key 缓存键
     * @return 缓存值，如果不存在或已过期返回 null
     */
    public <T> T get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            log.debug("Cache expired for key: {}", key);
            return null;
        }

        log.debug("Cache hit for key: {}", key);
        return (T) entry.value();
    }

    /**
     * 获取缓存值，如果不存在则从 loader 加载
     *
     * @param key   缓存键
     * @param loader 值加载器
     * @param ttl   缓存存活时间
     * @return 缓存值
     */
    public <T> T getOrLoad(String key, java.util.function.Supplier<T> loader, Duration ttl) {
        T value = get(key);
        if (value != null) {
            return value;
        }

        log.debug("Cache miss for key: {}, loading...", key);
        value = loader.get();
        if (value != null) {
            put(key, value, ttl);
        }
        return value;
    }

    /**
     * 获取缓存值，使用默认 TTL
     */
    public <T> T getOrLoad(String key, java.util.function.Supplier<T> loader) {
        return getOrLoad(key, loader, defaultTtl);
    }

    /**
     * 设置缓存值
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param ttl   存活时间
     */
    public void put(String key, Object value, Duration ttl) {
        if (key == null || value == null) {
            return;
        }
        cache.put(key, new CacheEntry(value, Instant.now().plus(ttl)));
        log.debug("Cached value for key: {}, ttl: {}", key, ttl);
    }

    /**
     * 设置缓存值，使用默认 TTL
     */
    public void put(String key, Object value) {
        put(key, value, defaultTtl);
    }

    /**
     * 使缓存失效
     *
     * @param key 缓存键
     */
    public void invalidate(String key) {
        cache.remove(key);
        log.debug("Invalidated cache for key: {}", key);
    }

    /**
     * 清空所有缓存
     */
    public void clear() {
        cache.clear();
        log.info("Cleared all cache entries");
    }

    /**
     * 获取缓存条目数量
     */
    public int size() {
        return cache.size();
    }

    /**
     * 清理过期条目
     */
    public void cleanup() {
        int before = cache.size();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int removed = before - cache.size();
        if (removed > 0) {
            log.info("Cleaned up {} expired cache entries", removed);
        }
    }

    /**
     * 缓存条目
     */
    private record CacheEntry(Object value, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
