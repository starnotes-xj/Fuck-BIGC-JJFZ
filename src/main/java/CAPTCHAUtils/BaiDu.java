// SPDX-FileCopyrightText: 2025 starnotes <starnotes@qq.com>
//
// SPDX-License-Identifier: GPL-3.0-or-later

package CAPTCHAUtils;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaiDu implements IdentificationCAPTCHA {
    private static String APP_ID = "";
    private static String API_KEY = "";
    private static String SECRET_KEY = "";
    private static AipOcr client;
    String captcha_result = "";

    public BaiDu(String appId, String apiKey, String secretKey) {
        APP_ID = appId;
        API_KEY = apiKey;
        SECRET_KEY = secretKey;
        client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
    }

    @Override
    public String getCaptcha(String imagePath) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("probability", "false");
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用接口
        JSONObject res = client.basicAccurateGeneral(imagePath, options);
        try {
            captcha_result = getWords(res);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return captcha_result;
    }
    private String getWords(JSONObject object) throws Exception {
        // 2. 获取words_result数组
        JSONArray wordsResultArray = object.getJSONArray("words_result");
        // 3. 遍历数组，提取每个元素的words值
        List<String> wordsList = new ArrayList<>();
        for (Object element : wordsResultArray) {
            JSONObject obj = (JSONObject) element;
            String word = obj.get("words").toString(); // 提取"words"字段
            wordsList.add(word);
        }
        return wordsList.getFirst();
    }
}