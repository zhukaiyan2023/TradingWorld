package com.tradingworld.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 在应用程序启动时初始化所需目录。
 * 如果不存在，则创建.tradingworld主目录结构。
 */
@Component
public class DirectoryInitializer {

    private static final Logger log = LoggerFactory.getLogger(DirectoryInitializer.class);

    private final AppConfig appConfig;

    public DirectoryInitializer(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @PostConstruct
    public void initializeDirectories() {
        createDirectoryIfNotExists(appConfig.getPaths().getResultsDir(), "results");
        createDirectoryIfNotExists(appConfig.getPaths().getDataCacheDir(), "data cache");
    }

    private void createDirectoryIfNotExists(String pathStr, String description) {
        try {
            Path path = Paths.get(pathStr).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created {} directory: {}", description, path);
            } else {
                log.debug("{} directory already exists: {}", description, path);
            }
        } catch (IOException e) {
            log.error("Failed to create {} directory: {}", description, pathStr, e);
            throw new RuntimeException("Failed to initialize directories", e);
        }
    }
}
