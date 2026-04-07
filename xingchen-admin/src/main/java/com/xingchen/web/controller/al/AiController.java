package com.xingchen.web.controller.al;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xingchen.common.core.domain.AjaxResult;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 助手控制器
 * 
 * @author xingchen
 */
@RestController
@RequestMapping("/ai/chat")
public class AiController {
    
    // Kimi (Moonshot) API Key
    private static final String API_KEY = "sk-Eb2xQ0OYZzKsUI6eK4pjeoM1tRkZkfyihqg8yZ7mh1scNrcK";
    private static final String API_URL = "https://api.moonshot.cn/v1/chat/completions";

    /**
     * 发送消息到 AI
     * 
     * @param request 请求参数，包含 message(文本) 和 image(Base64图片，可选)
     * @return AI 回复内容
     */
    @PostMapping("/send")
    public AjaxResult sendMessage(@RequestBody Map<String, Object> request) {
        try {
            String userMessage = (String) request.get("message");
            String imageBase64 = (String) request.get("image");
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            
            // 构建消息数组
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            
            // 如果有图片，构建多模态内容并使用支持视觉的模型
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                requestBody.put("model", "moonshot-v1-8k-vision-preview"); // 使用支持视觉的模型
                Map<String, Object>[] contentArray = new Map[2];
                
                // 文本部分
                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", userMessage != null && !userMessage.isEmpty() ? userMessage : "请描述这张图片");
                contentArray[0] = textContent;
                
                // 图片部分
                Map<String, Object> imageUrl = new HashMap<>();
                imageUrl.put("type", "image_url");
                Map<String, String> imageUrlDetail = new HashMap<>();
                imageUrlDetail.put("url", imageBase64);
                imageUrl.put("image_url", imageUrlDetail);
                contentArray[1] = imageUrl;
                
                message.put("content", contentArray);
            } else {
                // 纯文本，使用标准模型
                requestBody.put("model", "moonshot-v1-8k");
                message.put("content", userMessage != null && !userMessage.isEmpty() ? userMessage : "你好");
            }
            
            Map<String, Object>[] messages = new Map[]{message};
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            
            // 发送 HTTP 请求
            String response = sendHttpRequest(requestBody);
            
            // 解析响应
            String aiReply = parseResponse(response);
            
            return AjaxResult.success(aiReply);
            
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("AI 服务调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送 HTTP 请求到 DeepSeek API
     */
    private String sendHttpRequest(Map<String, Object> requestBody) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setDoOutput(true);
        
        // 使用 Fastjson 构建 JSON 字符串
        String jsonInputString = JSON.toJSONString(requestBody);
        
        // 发送请求体
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // 读取响应
        int responseCode = conn.getResponseCode();
        BufferedReader in;
        
        if (responseCode == 200) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }
        
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
    
    /**
     * 解析 AI 响应
     */
    private String parseResponse(String response) {
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            
            // 如果返回了错误信息
            if (jsonObject.containsKey("error")) {
                JSONObject errorObj = jsonObject.getJSONObject("error");
                String errorMsg = errorObj.getString("message");
                return "抱歉，AI 服务返回异常: " + errorMsg;
            }
            
            JSONArray choices = jsonObject.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject messageObj = choice.getJSONObject("message");
                if (messageObj != null && messageObj.containsKey("content")) {
                    return messageObj.getString("content");
                }
            }
            return "抱歉，未能获取 AI 回复内容";
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，解析 AI 回复失败";
        }
    }
}
