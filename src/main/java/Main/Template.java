package Main;

import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static Utils.extractElement.getWebElementsList;

/**
 * 使用Java实现的Selenium自动化脚本
 * 功能：自动化处理教育平台视频学习任务
 */
public class Template {
    private static final String BASE_URL = "http://xscdx.bigc.edu.cn/jjfz/lesson";
    static WebElement pauseEle;
    private static WebDriver driver;
    private static int indexStudyList = 0;
    private final String CLICKSTARTSTUDYADDRESSBOXCSSSELECTOR = "body > div.public_cont.public_cont1 > div.public_btn > a";
    private final String LEARNINGRESTARTORCONTINUEADDRESSBOXCSSSELECTOR = "body > div.public_cont.public_cont1 > div.public_btn > a.public_cancel";
    private final String BEFORELEARNINGADDRESSBOXCSSSELECTOR = "body > div.public_cont.public_cont1 > div.public_btn > a";
    private final String COMPULSORYCSSSELECTOR = "body > div > div.w1150 > div.wrap_right > div.lesson1_cont.q_lesson1_cont > div.lesson1_title > div > a:nth-child(2)";
    private final String SIDEBARCSSSELECTOR = "body > div.wrap_video > div.video_fixed.video_cut > div:nth-child(5) > ul";
    //每隔五分钟，弹窗出现的温馨提示，视频已暂停点击继续观看
    private final String FIVEMINUTEADDRESSBOXCSSSELECTOR = "body > div.public_cont.public_cont1 > div.public_btn > a";
    private final String SOUNDADDRESSBOXCSSSELECTOR = "#wrapper > div > div.plyr__controls > div.plyr__controls__item.plyr__volume > button";
    private final String ENDVIDEOADDRESSBOXCSSSELECTOR = "body > div.public_cont.public_cont1 > div.public_btn > a";

    public Template(WebDriver driver) {
        Template.driver = driver;
    }

    /**
     * 移除所有<a>标签的target="_blank"属性
     */
    private static void removeBlank() {
        String js = "var items = document.getElementsByTagName('a');" +
                "for (var i = 0; i < items.length; i++) {" +
                "   items[i].target = '_self';" +
                "}";
        ((JavascriptExecutor) driver).executeScript(js);
    }

    /**
     * 处理视频暂停问题,蓝色暂停按钮
     */
    private static void addressPause() {
        try {
            /**
             * 检查播放状态
             * Play表示暂停
             * Pause表示播放
             */
            pauseEle = driver.findElement(By.cssSelector("#wrapper > div > button"));
            if ("Play".equals(pauseEle.getAttribute("aria-label"))) {
                try {
                    pauseEle.click();
                    System.out.println("检测到视频暂停，继续播放");
                } catch (Exception e) {
                    System.out.println("蓝色暂停按钮未能点击");
                }

            }
        } catch (NoSuchElementException e) {
        }
    }
    private void clickAddressBox(String cssSelector) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
            // 使用 WebDriverWait 等待最多 1 秒，直到找到具有 cssSelector 选择器的元素
            WebElement iKnow = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));
            // 点击温馨提示弹窗中的按钮
            iKnow.click();
        } catch (TimeoutException ignored) {
            // 无弹窗时不处理
        }
    }

    /**
     * 处理单个必修视频
     *
     * @param index 视频索引
     */
    private void manageVideo(int index, @NotNull List<WebElement> necessaryList) {
        // 进入课程
        necessaryList.get(index).findElement(By.cssSelector("div.l_list_right > h2 > a")).click();
        // 获取侧边栏视频列表
        List<WebElement> sidebars = getWebElementsList(SIDEBARCSSSELECTOR, driver);
        int times = sidebars.size();
        System.out.print("成功加载侧边栏，共" + sidebars.size() + "个视频");
        boolean isNoVoice = false;
        for (int i = 0; i < times; i++) {
            // 每次循环都重新获取当前视频元素
            sidebars = getWebElementsList(SIDEBARCSSSELECTOR, driver);
            WebElement currentVideo = sidebars.get(i);
            System.out.println("正在播放第" + (i + 1) + "个视频");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            String currentVideofinshSymbol = currentVideo.getAttribute("style");
            // 检查是否已播放（通过样式判断）
            if (currentVideofinshSymbol != null && currentVideofinshSymbol.contains("red")) {
                System.out.println("视频已完成，跳过");
            } else {
                /**
                 * 因为第一个课程需要学习，就只需要处理学习前的温馨提醒或者继续学习的按钮
                 * 如果i!=0那么就不是第一个视频，需要点击侧边栏的视频，然后处理学习前的温馨提醒或者继续学习的按钮
                 * 如果i==0那么就是第一个视频，不需要点击侧边栏的视频，直接处理学习前的温馨提醒或者继续学习的按钮
                 * */
                if (i != 0) {
                    clickAddressBox(SIDEBARCSSSELECTOR + " > li:nth-child(" + (i + 1) + ") > a");
                }
                clickAddressBox(BEFORELEARNINGADDRESSBOXCSSSELECTOR);// 处理弹窗
                clickAddressBox(LEARNINGRESTARTORCONTINUEADDRESSBOXCSSSELECTOR);// 处理弹窗
                if (!isNoVoice) {
                    clickAddressBox(SOUNDADDRESSBOXCSSSELECTOR); //静音
                    isNoVoice = !isNoVoice;
                }
                while (true) {
                    //处理五分钟暂停一次的弹窗
                    clickAddressBox(FIVEMINUTEADDRESSBOXCSSSELECTOR);
                    //处理中途点击视频列表导致刷新弹出的继续学习的弹窗
                    clickAddressBox(LEARNINGRESTARTORCONTINUEADDRESSBOXCSSSELECTOR);
                    // 处理可能的暂停状态
                    addressPause();
                    // 检查视频是否播放完成
                    try {
                        WebElement plyr__progress = driver.findElement(By.cssSelector("#wrapper > div > div.plyr__controls > div.plyr__controls__item.plyr__progress__container > div"));
                        if (plyr__progress != null) {
                            clickAddressBox(ENDVIDEOADDRESSBOXCSSSELECTOR);
                            break;
                        }
                    } catch (NoSuchElementException e) {
                        System.out.println("视频正在播放");
                    }
                }
            }
        }
    }

    /**
     * 主执行流程
     */
    public void run() {
        // 获取所有的课程整体
        String allCourseCssSelector = "body > div > div.w1150 > div.lesson_center_ul > ul";
        /**
         *  采用显示等待，使用WebDriverWait确保目标元素加载完成后再进行操作，提升代码稳定性。
         *  定位到课程列表的<ul>元素,再提取<ul>标签下所有的<li>标签元素
         */
        List<WebElement> CoursesList = getWebElementsList(allCourseCssSelector, driver);
        int times = CoursesList.size();
        //循环学习课程
        for (indexStudyList = 0; indexStudyList < times; indexStudyList++) {
            CoursesList = getWebElementsList(allCourseCssSelector, driver);
            String finshSymbol = CoursesList.get(indexStudyList).findElement(By.cssSelector(allCourseCssSelector + " > li:nth-child(" + (indexStudyList + 1) + ")" + "> dl > dd:nth-child(1)")).getText();
            String hasFinshSymbol = CoursesList.get(indexStudyList).findElement(By.cssSelector(allCourseCssSelector + " > li:nth-child(" + (indexStudyList + 1) + ")" + "> dl > dd:nth-child(2)")).getText();
            if (finshSymbol.charAt(5) == hasFinshSymbol.charAt(8)) continue;
            //点击”开始学习“按钮，有个别开始学习按钮是不同的
            try {
                CoursesList.get(indexStudyList).findElement(By.cssSelector("div > a.study")).click();
                clickAddressBox(CLICKSTARTSTUDYADDRESSBOXCSSSELECTOR);
            } catch (Exception ignored1) {
                //如果不是这个按钮，虽然会报错但是直接忽略尝试下一个类型的开始学习按钮
            }
            try {
                CoursesList.get(indexStudyList).findElement(By.cssSelector("div > a")).click();
                clickAddressBox(CLICKSTARTSTUDYADDRESSBOXCSSSELECTOR);
            } catch (Exception ignored2) {
                //如果不是这个按钮，虽然会报错但是直接忽略
            }
            // 切换到必修标签
            clickAddressBox(COMPULSORYCSSSELECTOR);
            /** 获取必修课程列表
             *  采用显示等待，使用WebDriverWait确保目标元素加载完成后再进行操作，提升代码稳定性。
             *  定位到必修课程列表的<ul>元素,再提取<ul>标签下所有的<li>标签元素
             */
            List<WebElement> necessaryList = getWebElementsList(
                    "body > div > div.w1150 > div.wrap_right > div.lesson1_cont.q_lesson1_cont > div.lesson1_lists > ul", driver);
            String currentUrl = driver.getCurrentUrl();// 保存当前URL
            for (int j = 0; j < necessaryList.size(); j++) {
                removeBlank(); // 修改链接属性
                WebElement element = necessaryList.get(j);
                try {
                    String finshSymboltemp = element.findElement(By.cssSelector("li:nth-child(" + (j + 1) + ")" + " > a > div")).getText();
                    if ("完成".equals(finshSymboltemp)) continue;
                } catch (StaleElementReferenceException ignored) {

                } catch (NoSuchElementException ignored2) {
                    // 处理每个必修课
                    manageVideo(j, necessaryList);
                }
                // 返回必修课列表页
                driver.get(currentUrl);
                System.out.println("完成必修课程:第" + indexStudyList + "课" + "的第" + (j + 1) + "必修课");
            }
            driver.get(BASE_URL); // 返回主页
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.urlToBe(BASE_URL));
        }
        System.out.println("所有必修课程完成！");
    }
}