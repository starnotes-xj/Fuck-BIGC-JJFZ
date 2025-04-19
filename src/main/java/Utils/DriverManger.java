package Utils;

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

public class DriverManger {
    // 默认外部配置目录
    private static final String CONFIG_DIR = "config";
    private static final String DRIVER_NAME = "chromedriver.exe";
    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);

    public static String setupDriver() throws IOException {
        // 1. 首先检查外部config目录下是否存在驱动文件
        Path driverPath = Paths.get(CONFIG_DIR, DRIVER_NAME);
        if (Files.exists(driverPath)) {
            logger.info("使用外部驱动文件: {}", driverPath.toAbsolutePath());
            return driverPath.toAbsolutePath().toString();
        }
        // 2. 如果外部不存在，尝试从JAR资源中提取
        logger.info("外部驱动文件不存在，尝试从JAR资源加载...");
        try (InputStream inputStream = DriverManger.class.getClassLoader().getResourceAsStream("chromedriver.exe")) {
            if (inputStream == null) {
                System.out.println("在JAR资源中找不到 " + DRIVER_NAME);
            }
            // 创建config目录（如果不存在）
            if (!Files.exists(driverPath)) {
                Files.createDirectories(Paths.get(CONFIG_DIR));
            }
            // 将驱动文件复制到外部目录
            Files.copy(inputStream, driverPath, StandardCopyOption.REPLACE_EXISTING);
            // 设置可执行权限（主要针对Linux/Mac）
            File driverFile = driverPath.toFile();
            if (!driverFile.setExecutable(true)) {
                logger.warn("无法设置驱动文件可执行权限");
            }
            logger.info("已将驱动文件提取到: {}", driverPath.toAbsolutePath());
            return driverPath.toAbsolutePath().toString();
        }
    }
}
