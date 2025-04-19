package Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class DeepSeek {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String API_KEY = "";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL_R1 = "deepseek-reasoner";
    private static final String MODEL_V3 = "deepseek-chat";
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    static String requestContent;

    public static String sendRequest(String content) throws Exception {
        // 构建请求体
        ObjectNode requestBody = mapper.createObjectNode();
        requestBody.put("model", MODEL_R1);
        requestBody.put("stream", false);
        ArrayNode messages = mapper.createArrayNode();
        messages.add(mapper.createObjectNode()
                .put("role", "user")
                .put("content", content));
        requestBody.set("messages", messages);
        // 创建HTTP请求
        HttpPost httpPost = new HttpPost(API_URL);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + API_KEY);
        httpPost.setEntity(new StringEntity(mapper.writeValueAsString(requestBody)));

        // 发送请求并处理响应
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity);
                ObjectNode responseJson = (ObjectNode) mapper.readTree(responseBody);

                requestContent = responseJson
                        .path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();

                System.out.println("Response: " + requestContent);
            }
        }
        return requestContent;
    }
}