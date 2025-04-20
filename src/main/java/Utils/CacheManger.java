package Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class CacheManger {
    private static final Logger logger = LoggerFactory.getLogger(CacheManger.class);
    private static final String CACHE_FILE_DIR = "data";
    private static final String CACHE_FILE_NAME= "question_cache.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<String, String> cache = new HashMap<>();
    private static Path path = Paths.get(CACHE_FILE_DIR, CACHE_FILE_NAME);
    public CacheManger() throws IOException {
        loadCache();
    }

    private void loadCache() throws IOException {
        // 确保目录和文件存在
        if (!Files.exists(path)) {
            System.out.println("外部题库文件不存在，尝试从JAR资源加载...");
            try (InputStream inputStream = CacheManger.class.getClassLoader()
                    .getResourceAsStream(CACHE_FILE_NAME)){
                if (inputStream == null) {
                    System.out.println("在JAR资源中找不到题库，将创建新的题库");
                }
                if (!Files.exists(path.getParent())){
                    Files.createDirectories(path.getParent());
                }
                Files.createFile(path);
                logger.info("创建新缓存文件: {}", CACHE_FILE_NAME);
                // 将题库文件复制到外部目录
                Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);
                // 设置可执行权限（主要针对Linux/Mac）
                File cacheFile = path.toFile();
                if (!cacheFile.setReadable(true)) {
                    logger.warn("无法设置题库文件可读权限");
                }
                if (!cacheFile.setExecutable(true)) {
                    logger.warn("无法设置题库文件可执行权限");
                }
            }
        }
        // 仅当文件有内容时加载
        if (Files.size(path) > 0) {
            try {
                Map<String, String> existingCache = objectMapper.readValue(
                        path.toFile(),
                        objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, String.class)
                );
                cache.putAll(existingCache);
                logger.info("成功加载 {} 条缓存记录", existingCache.size());
            } catch (Exception e) {
                logger.error("加载缓存文件失败，将创建新缓存", e);
                // 如果读取失败，清空缓存以重新开始
                cache.clear();
            }
        }
    }

    public synchronized void saveCache() {
        try {
            objectMapper.writeValue(path.toFile(), cache);
            logger.debug("缓存已保存至 {}，当前缓存大小: {}", CACHE_FILE_NAME, cache.size());
        } catch (Exception e) {
            logger.error("保存缓存文件失败", e);
        }
    }

    public synchronized String getAnswer(String question) {
        return cache.get(question);
    }

    public synchronized void putAnswer(String question, String answer) {
        // 只有当问题不存在或者答案不同时才更新
        if (!cache.containsKey(question) || !cache.get(question).equals(answer)) {
            cache.put(question, answer);
            saveCache(); // 每次更新后保存
        }
    }

    public synchronized int getCacheSize() {
        return cache.size();
    }
}