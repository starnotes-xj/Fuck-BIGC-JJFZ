package Utils;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static Utils.extractElement.getWebElementsList;

public class AutoAnswerQuestion {
    static final String MULTIPLE_QUESTION_ANSWERLISTCSSSELECTOR = "body > div.wrap > div.w1150 > div.exam_cont_left > div.exam_list > div.answer_list_box > ul";
    static final String SINGLE_QUESTION_ANSWER_LISTCSSSELECTOR = "body > div > div.w1150 > div.exam_cont_left > div.exam_list > div.answer_list > ul";
    static final String SINGLE_QUESTION_CSSSELECTOR = "body > div > div.w1150 > div.exam_cont_left > div.exam_list > h2";
    //多选题和判断题的Css选择器一样
    static String MULTIPLE_CHOICE_QUESTIONSCSSSELECTOR = "body > div.wrap > div.w1150 > div.exam_cont_left > div.exam_list > h2";
    static final String EXAMPAGECSSSELECTOR = "body > div.wrap > div.w1150 > div.exam_cont_left > div.exam_list > div.exam_pages";
    static final String SINGLE_QUESTION_UL_CSSSELECTOR = "body > div.wrap > div.w1150 > div.exam_cont_right > div.cont_right_num > div.exam_num_lists > ul:nth-child(2)";
    static final String MULTIPLE_QUESTION_UL_CSSSELECTOR = "body > div.wrap > div.w1150 > div.exam_cont_right > div.cont_right_num > div.exam_num_lists > ul:nth-child(4)";
    static final String JUDGE_QUESTION_UL_CSSSELECTOR = "body > div.wrap > div.w1150 > div.exam_cont_right > div.cont_right_num > div.exam_num_lists > ul:nth-child(6)";
    static Queue<WebElement> questionElementList = new LinkedList<>();
    static List<WebElement> singleQuestionElementList;
    static List<WebElement> multipleQuestionElementList;
    static List<WebElement> judgeQuestionElementList;
    static String answer;
    static WebDriver driver;
    static WebDriverWait wait;
    static CacheManger cacheManger;

    static {
        try {
            cacheManger = new CacheManger();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Map<String, Integer> cacheAnswerMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // 配置驱动路径
        System.setProperty("webdriver.chrome.driver", DriverManger.setupDriver());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("remote-debugging-port=9222");
        options.addArguments("user-data-dir=D:\\selenium_test");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        for (int i = 0; i <= 7; i++) {
            cacheAnswerMap.put(String.valueOf((char) ('A' + i)), i);
        }
        getQuestionList();
        finshQuestions(false);
        getQuestionList();
        finshQuestions(true);
    }

    private static void getQuestionList() {
        //获取右边答题卡的题目列表
        singleQuestionElementList = getWebElementsList(SINGLE_QUESTION_UL_CSSSELECTOR, driver);
        multipleQuestionElementList = getWebElementsList(MULTIPLE_QUESTION_UL_CSSSELECTOR, driver);
        judgeQuestionElementList = getWebElementsList(JUDGE_QUESTION_UL_CSSSELECTOR, driver);
    }

    private static void finshQuestions(boolean isCheck) {
        //将所有题目加入队列
        addQuestions(singleQuestionElementList, questionElementList);
        addQuestions(multipleQuestionElementList, questionElementList);
        addQuestions(judgeQuestionElementList, questionElementList);
        //进行作答
        while (!questionElementList.isEmpty()) {
            WebElement questionElement = questionElementList.poll();
            //如果isCheck为true,说明是进行检查，否则是进行作答.检查漏答的题目需要点击一下才能进行作答
            if (isCheck) questionElement.click();
            AnswerQuestions();
        }
    }

    private static void addQuestions(List<WebElement> QuestionElementList, Queue<WebElement> questionQueue) {
        for (WebElement QuestionElement : QuestionElementList) {
            if (!"done".equals(QuestionElement.getAttribute("class")))
                questionQueue.add(QuestionElement);
        }
    }

    private static void AnswerQuestions() {
        WebElement examPages = driver.findElement(By.cssSelector(EXAMPAGECSSSELECTOR));
        List<WebElement> examButtionList;
        String progress = examPages.findElement(By.tagName("span")).getText();
        QuestionParser.parseQuestionNumbers(progress);
        Integer currentQuestionNumber = QuestionParser.getCurrentQuestionNumber();
        /**给予充足的时间来等待页面加载完成，否则会抛出NoSuchElementException异常
         * */
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.println("无法自动完成该题");
        }
        WebElement singleQuestionElement;
        WebElement multipleQuestionElement;
        List<WebElement> answers;
        String question;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(SINGLE_QUESTION_CSSSELECTOR)));
            singleQuestionElement = driver.findElement(By.cssSelector(SINGLE_QUESTION_CSSSELECTOR));
            question = AnsweringQuestionParser.parseQuestion(singleQuestionElement.getText());
            answers = getWebElementsList(SINGLE_QUESTION_ANSWER_LISTCSSSELECTOR, driver);
            //如果找不到元素或者超时说明是多选题或者判断题
        } catch (NoSuchElementException | TimeoutException e) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(MULTIPLE_CHOICE_QUESTIONSCSSSELECTOR)));
            multipleQuestionElement = driver.findElement(By.cssSelector(MULTIPLE_CHOICE_QUESTIONSCSSSELECTOR));
            question = AnsweringQuestionParser.parseQuestion(multipleQuestionElement.getText());
            answers = getWebElementsList(MULTIPLE_QUESTION_ANSWERLISTCSSSELECTOR, driver);
        }

        String cacheAnswer = cacheManger.getAnswer(question);
        if (cacheAnswer != null) {
            for (int i = 0; i < cacheAnswer.length(); i++) {
                WebElement temp = answers.get(cacheAnswerMap.get(String.valueOf(cacheAnswer.charAt(i))));
                if (!temp.getAttribute("class").contains("result_cut")) temp.click();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("题库中有答案，已完成第" + currentQuestionNumber + "题");
        } else {
            StringBuilder content = new StringBuilder();
            content.append(question).append("\n").append("选项:\n");
            for (WebElement answer : answers) {
                content.append(answer.getText()).append("\n");
            }
            content.append("请自行判断是单选题还是多选题，如果是单选题请直接给出最正确的答案选项的内容，如果是多选题请给出全部的正确答案，不要解释,不论选项有多长都不要使用A,B,C,D来代替选项。");
            System.out.println(content);
            try {
                answer = DeepSeek.sendRequest(content.toString());
                int index = 0;
                StringBuilder res = new StringBuilder();
                for (WebElement answerchoices : answers) {
                    if (answer.contains(answerchoices.getText())) {
                        if (!answerchoices.getAttribute("class").contains("result_cut")) {
                            answerchoices.click();
                            Thread.sleep(1000);
                        }
                        res.append((char) ('A' + index));
                    }
                    index++;
                }
                cacheManger.putAnswer(question, res.toString());
            } catch (Exception e) {
                System.out.println("DeepSeek调用失败");
            }
            System.out.println("已完成第" + currentQuestionNumber + "题，正在点击下一题");
        }

        /**在测试模式浏览器中如果抛出NoSuchElementException异常，需要重启浏览器
         先找到下一页所属的类，再在这个类中去找所有的a标签，最后点击第二个a标签，第二个a标签就是下一页的按钮
         选择正确答案并点击之后需要一定时间反应，所以需要等待1秒，否则会抛出NoSuchElementException异常
         */
        try {
            Thread.sleep(1750);
        } catch (InterruptedException e) {
            System.out.println("等待跳转失败");
        }
        /**每次点击下一页之后，都需要重新获取examPages和examButtionList，否则会抛出元素过时异常
         * */
        examPages = driver.findElement(By.cssSelector(EXAMPAGECSSSELECTOR));
        examButtionList = examPages.findElements(By.tagName("a"));
        examButtionList.get(1).click();
    }
}