package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnsweringQuestionParser {
    // 正则表达式匹配以数字序号开头的问题格式（如 "1. 问题内容..."）
    private static final Pattern QUESTION_PATTERN = Pattern.compile("^\\d+\\.\\s*(.*)$");
    public static String parseQuestion(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        Matcher matcher = QUESTION_PATTERN.matcher(input);
        if (matcher.find()) {
            // 返回第一个捕获组的内容（去除首尾空格）
            return matcher.group(1).trim();
        }
        return "";
    }
}
