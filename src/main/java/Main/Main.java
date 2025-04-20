package Main;// SPDX-FileCopyrightText: 2025 starnotes <starnotes@qq.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

import CAPTCHAUtils.BaiDu;
import CAPTCHAUtils.IdentificationCAPTCHA;
import Utils.DriverManger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 主类，包含程序的入口点和主要操作方法。
 */
public class Main {
    // 定义用户名常量，用于登录操作
    private static String userName = "";
    // 定义密码常量，用于登录操作
    private static String password = "";
    // 定义网站 URL 常量，用于登录操作
    private static final String URL = "http://xscdx.bigc.edu.cn/";
    // 定义 WebDriver 对象，用于控制浏览器
    private static WebDriver driver;
    public static Path path = Paths.get("config/APIKeys.json");
    public static ObjectMapper API_KEY_Mapper = new ObjectMapper();
    /**
     * 程序的入口点，负责初始化 WebDriver，执行登录操作，并进入学习中心。
     *
     * @param args 命令行参数，本方法未使用。
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入学号:");
        userName = scanner.nextLine();
        System.out.println("请输入密码:");
        password = scanner.nextLine();
        String appId,apiKey,secretKey,deepSeekApiKey;
        Map<String, String> APIKeys = new HashMap<>();
        if (!Files.exists(path)) {
            System.out.println("请输入百度APPID");
            appId = scanner.nextLine();
            System.out.println("请输入百度APIKey");
            apiKey = scanner.nextLine();
            System.out.println("请输入百度SecretKey");
            secretKey = scanner.nextLine();
            System.out.println("请输入DeepSeek APIKey");
            deepSeekApiKey = scanner.nextLine();
            while(true) {
                System.out.println("是否保存APIKey等数据(yes/no):");
                String input = scanner.nextLine();
                if (!"yes".equals(input) && !"no".equals(input)) {
                    System.out.println("输入错误,请重新输入");
                    continue;
                }
                if ("no".equals(input)) break;
                APIKeys.put("appid", appId);
                APIKeys.put("apikey", apiKey);
                APIKeys.put("secretkey", secretKey);
                APIKeys.put("deepseekapikey", deepSeekApiKey);
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
                API_KEY_Mapper.writeValue(path.toFile(), APIKeys);
                break;
            }
        }else {
            APIKeys = API_KEY_Mapper.readValue(path.toFile(),API_KEY_Mapper.getTypeFactory()
                    .constructMapType(HashMap.class,String.class,String.class));
        }
        appId = APIKeys.get("appid");
        apiKey = APIKeys.get("apikey");
        secretKey = APIKeys.get("secretkey");
        IdentificationCAPTCHA captcha = new BaiDu(appId, apiKey, secretKey);

        // 获取 WebDriver 实例
        driver = getWebDriver();
        // 执行登录操作
        LoginUp(captcha);
        // 进入学习中心的方法
        toLearningCenter();
        //开始自动学习课程
        Template template = new Template(driver);
        template.run();
    }

    /**
     * 进入学习中心的方法，通过点击页面元素进入学习中心
     */
    private static void toLearningCenter() {
        // 创建 WebDriverWait 对象，设置最大等待时间为 3 秒
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        // 等待学习任务元素可见
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body > div.wrap > div.w1150 > div.wrap_left > div.wrap_left_list > ul > li:nth-child(2) > div > a > span")));
        // 点击学习任务
        driver.findElement(By.cssSelector("body > div > div.w1150 > div.wrap_left > div.wrap_left_list > ul > li:nth-child(2) > div > a > span")).click();
        // 点击去完成按钮进入学习中心
        driver.findElement(By.cssSelector("body > div > div.w1150 > div.wrap_right > div.study_cont > div:nth-child(1) > div > h5 > a")).click();
    }
    /**
     * 获取 WebDriver 实例的方法，设置 ChromeDriver 的路径，初始化 ChromeDriver，并打开指定 URL，最大化窗口。
     *
     * @return 初始化后的 WebDriver 实例。
     */
    private static WebDriver getWebDriver() throws IOException {
        /**
         * WebDriverManager.chromedriver().setup();
         * 因为 WebDriverManger 只支持 Chrome 的 114 版本，目前最新的 Chrome 版本是 134,所以这里手动指定
         * 第一个参数是固定写法,第二个参数是驱动所在路径
         * System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
         * 此处为了能够在 JAR 包中运行,所以需要将驱动文件复制到临时目录中,然后设置驱动路径
         */
        // 配置驱动路径
        System.setProperty("webdriver.chrome.driver", DriverManger.setupDriver());
        WebDriver driver = new ChromeDriver();
        // 打开指定 URL
        driver.get(URL);
        // 最大化窗口
        driver.manage().window().maximize();
        return driver;
    }

    /**
     * 登录方法，通过定位用户名和密码输入框，输入用户名和密码，获取验证码并输入，最后点击登录按钮。
     */
    private static void LoginUp(IdentificationCAPTCHA captcha) {
        // 创建 WebDriverWait 对象，设置最大等待时间为 3 秒
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        ExpectedConditions.alertIsPresent();
        // 等待用户名输入框可见
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_user.login_item > input[type=text]")));
        // 定位用户名输入框
        WebElement usernameInput = driver.findElement(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_user.login_item > input[type=text]"));
        // 输入用户名
        usernameInput.sendKeys(userName);
        // 等待密码输入框可见
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_pass.login_item > input[type=password]")));
        // 定位密码输入框
        WebElement passwordInput = driver.findElement(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_pass.login_item > input[type=password]"));
        // 输入密码
        passwordInput.sendKeys(password);
        // 获取验证码结果
        String captcha_result = getCaptchaImageResult(captcha);
        // 等待验证码输入框可见
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_item.login_item_piccheck > input")));
        // 定位验证码输入框
        WebElement captchaInput = driver.findElement(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_item.login_item_piccheck > input"));
        // 输入验证码
        captchaInput.sendKeys(captcha_result);
        // 定位登录按钮
        WebElement login_button = driver.findElement(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div:nth-child(5) > button"));
        // 点击登录按钮
        login_button.click();
    }

    /**
     * 获取验证码图片并识别结果的方法。
     *
     * @return 识别出的验证码结果，如果出现异常则返回 null。
     */
    private static String getCaptchaImageResult(IdentificationCAPTCHA implementationClass) {
        // 初始化验证码结果为 null
        String captcha_result = null;
        try {
            // 1. 定位验证码元素
            WebElement captcha_image = driver.findElement(By.cssSelector("#app > div > div.login_con_box > div.login_plat > div > div.login_item.login_item_piccheck > img"));
            // 2. 获取元素位置和尺寸
            Point location = captcha_image.getLocation();
            Dimension size = captcha_image.getSize();
            int x = location.getX();
            int y = location.getY();
            int width = size.getWidth();
            int height = size.getHeight();
            // 3. 处理设备像素比（DPR）
            JavascriptExecutor js = (JavascriptExecutor) driver;
            double DPR = (double) js.executeScript("return window.devicePixelRatio;");
            x = (int) (x * DPR);
            y = (int) (y * DPR);
            width = (int) (width * DPR);
            // 调用自定义的 BaiDu 类的方法识别验证码
            height = (int) (height * DPR);
            // 4. 截取全屏
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImage = ImageIO.read(screenshot);
            // 5. 裁剪并保存（处理边界）
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + width > fullImage.getWidth()) width = fullImage.getWidth() - x;
            if (y + height > fullImage.getHeight()) height = fullImage.getHeight() - y;
            BufferedImage captchaImage = fullImage.getSubimage(x, y, width, height);
            // 定义外部保存目录（示例：保存到项目运行目录的 "output/images" 下）
            String outputDir = "images";
            Path dirPath = Paths.get(outputDir);
            // 如果目录不存在，则创建
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            // 定义保存路径（示例：images/captcha.png）
            String savePath = outputDir + File.separator + "captcha.png";
            File outputFile = new File(savePath);
            // 保存图片到外部目录
            ImageIO.write(captchaImage, "png", outputFile);
            System.out.println("验证码截图已保存");
            captcha_result = implementationClass.getCaptcha(outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("读取全屏截屏图片失败");
        }
        return captcha_result;
    }
}