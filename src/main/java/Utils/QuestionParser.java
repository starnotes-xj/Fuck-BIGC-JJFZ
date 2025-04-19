package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionParser {
    static Integer currentQuestionNumber;


    static Integer totalQuestionNumber;
    // 修改后的正则表达式：允许序号前有空格
    private static final Pattern QUESTION_PATTERN =
            Pattern.compile("^\\s*\\d+、\\【.*?】\\s*(.*)"); // 新增 \\s* 匹配前导空格

    public static String extractQuestionContent(String question) {
        Matcher matcher = QUESTION_PATTERN.matcher(question);
        return matcher.find() ? matcher.group(1).trim() : question;
    }

    public static void parseQuestionNumbers(String progress) {
        Pattern pattern = Pattern.compile("(\\d+)/(\\d+)");
        Matcher matcher = pattern.matcher(progress);
        if (matcher.matches()) {
            currentQuestionNumber = Integer.parseInt(matcher.group(1));
            totalQuestionNumber = Integer.parseInt(matcher.group(2));
        }
    }

    public static Integer getTotalQuestionNumber() {
        return totalQuestionNumber;
    }

    public static Integer getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }
}