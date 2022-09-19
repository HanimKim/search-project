package com.example.modules.search.service;

import com.example.modules.search.util.HttpConnectionUtil;
import com.example.modules.search.value.BlogFindDto;
import com.example.modules.search.value.BlogVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    @Value("${spring.kakao-api.host}") private String KAKAO_HOST;
    @Value("${spring.kakao-api.blog-path}") private String KAKAO_BLOG_PATH;
    @Value("${spring.kakao-api.rest-api-key}") private String KAKAO_REST_API_KEY;

    @Override
    public String findBlog(BlogFindDto dto){
        return SearchBlogByKakaoApi(dto);
    }

    // kakao blog search api 호출
    public String SearchBlogByKakaoApi(BlogFindDto dto){
        String url = KAKAO_HOST + KAKAO_BLOG_PATH;
        String param = setParameter(dto, "kakao");

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);
        return HttpConnectionUtil.get(url+param, requestHeaders);
    }

    // get parameter 세팅
    public String setParameter(BlogFindDto dto, String type) {
        StringBuilder stringBuilder = new StringBuilder();

        if (type.equals("kakao")){
            stringBuilder.append("?query=").append(dto.getQuery());

            if (dto.getSort() != null && !dto.getSort().equals("")){
                stringBuilder.append("&sort=").append(dto.getSort());
            }
            if (dto.getPage() > 0){
                stringBuilder.append("&page=").append(dto.getSort());
            }
            if (dto.getSize() > 0){
                stringBuilder.append("&size=").append(dto.getSize());
            }

        }else{
            //TODO. naver parm 값 넣기
        }

        return stringBuilder.toString();
    }
}
