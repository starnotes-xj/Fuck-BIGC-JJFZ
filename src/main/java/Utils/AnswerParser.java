package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnswerParser {
    // 修改后的正则表达式支持A-E
    private static final Pattern PATTERN = Pattern.compile(
            "(?:正确答案|正确选项)[：:]\\s*([A-Ea-e]+)",  // 修改A-D为A-E
            Pattern.UNICODE_CHARACTER_CLASS | Pattern.CASE_INSENSITIVE
    );

    public static String extractAnswer(String input) {
        Matcher matcher = PATTERN.matcher(input);
        if (matcher.find()) {
            String answer = matcher.group(1)
                    .trim()
                    .toUpperCase();

            // 更新验证范围为A-E
            if (answer.matches("^[A-E]+$")) {  // 修改A-D为A-E
                return answer;
            }
        }
        return null;
    }
}