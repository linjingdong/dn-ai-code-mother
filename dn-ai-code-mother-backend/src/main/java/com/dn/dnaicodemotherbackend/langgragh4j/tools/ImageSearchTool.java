package com.dn.dnaicodemotherbackend.langgragh4j.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dn.dnaicodemotherbackend.langgragh4j.model.ImageResource;
import com.dn.dnaicodemotherbackend.langgragh4j.model.enums.ImageCategoryEnum;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ImageSearchTool {

    private static final String PIXABAY_API_URL = "https://pixabay.com/api/";

    @Value("${pixabay.api-key}")
    private String pixabayApiKey;

    private static final int MAX_QUERY_LENGTH = 100;
    private static final int SEARCH_COUNT = 12;

    @Tool("搜索内容相关的图片，用于网站内容展示")
    public List<ImageResource> searchContentImages(@P("搜索关键词") String query) {
        String keyword = normalizeQuery(query);
        List<ImageResource> imageList = new ArrayList<>();
        try (HttpResponse response = HttpRequest.get(PIXABAY_API_URL)
                .form("key", pixabayApiKey)
                .form("q", keyword)
                .form("lang", "zh")
                .form("image_type", "photo")      // 排除矢量图/插画，更适合做网页配图
                .form("orientation", "horizontal") // 横幅/卡片图更合适
                .form("order", "popular")
                .form("per_page", SEARCH_COUNT)
                .form("safesearch", "true")
                .timeout(10_000)                  // 关键：避免无限等待
                .execute()) {
            String body = response.body();
            if (!response.isOk()) {               // 关键：把失败原因打出来
                log.error("Pixabay 请求失败, status={}, body={}", response.getStatus(), body);
                return imageList;
            }
            JSONArray hits = JSONUtil.parseObj(body).getJSONArray("hits");
            if (hits == null || hits.isEmpty()) {
                log.warn("Pixabay 未搜索到图片, query={}", keyword);
                return imageList;
            }
            for (int i = 0; i < hits.size(); i++) {
                JSONObject hit = hits.getJSONObject(i);
                String url = StrUtil.blankToDefault(hit.getStr("largeImageURL"), hit.getStr("webformatURL"));
                if (StrUtil.isBlank(url)) continue;
                String description = StrUtil.blankToDefault(hit.getStr("heading"),
                        StrUtil.blankToDefault(hit.getStr("tags"), keyword)); // alt 不存在，改用 heading/tags
                imageList.add(ImageResource.builder()
                        .category(ImageCategoryEnum.CONTENT)
                        .description(description)
                        .url(url)
                        .build());
            }
            log.info("Pixabay 搜索完成, query={}, 命中 {} 张", keyword, imageList.size());
        } catch (Exception e) {
            log.error("Pixabay API 调用失败: {}", e.getMessage(), e);
        }
        return imageList;
    }

    private String normalizeQuery(String query) {
        if (StrUtil.isBlank(query)) {
            return "background";               // 文档：q 省略会返回全部图，这里给个兜底词
        }
        String kw = query.replaceAll("[\\r\\n\\t]", " ").trim();
        return kw.length() > MAX_QUERY_LENGTH ? kw.substring(0, MAX_QUERY_LENGTH) : kw;
    }
}
