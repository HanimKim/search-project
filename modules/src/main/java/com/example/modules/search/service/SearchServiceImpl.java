package com.example.modules.search.service;

import com.example.modules.search.domain.SearchSentence;
import com.example.modules.search.repository.SearchSentenceRepository;
import com.example.modules.search.repository.SearchSentenceRepositorySupport;
import com.example.modules.search.util.HttpConnectionUtil;
import com.example.modules.search.value.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService{

    @Value("${spring.kakao-api.host}") private String KAKAO_HOST;
    @Value("${spring.kakao-api.blog-path}") private String KAKAO_BLOG_PATH;
    @Value("${spring.kakao-api.rest-api-key}") private String KAKAO_REST_API_KEY;

    private final ModelMapper mapper;

    private final SearchSentenceRepository searchSentenceRepository;

    private final SearchSentenceRepositorySupport searchSentenceRepositorySupport;

    @Override
    public BlogVo findBlog(BlogFindDto dto) {
        BlogVo blogVo = null;

        String resultStr = SearchBlogByKakaoApi(dto);

        if (resultStr != null) {
            blogVo = parseBlogSearchResult(resultStr, dto, "kakao");
        }else {
            //TODO. 네이버 검색 추가
        }

        saveSearchSentence(dto.getQuery()); // 검색어 저장

        return blogVo;
    }

    @Override
    public List<SearchSentenceVo> findSearchSentences(){
        return searchSentenceRepositorySupport.findSearchSentences();
    }

    // kakao blog search api 호출
    @Override
    public String SearchBlogByKakaoApi(BlogFindDto dto){
        String result = "";
        try {
            String url = KAKAO_HOST + KAKAO_BLOG_PATH;
            String param = setBlogParameter(dto, "kakao");

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

            result = HttpConnectionUtil.get(url+param, requestHeaders);
        }catch (Exception e){
            result = null;
        }

        return result;
    }

    // get parameter 세팅
    @Override
    public String setBlogParameter(BlogFindDto dto, String type) {
        StringBuilder stringBuilder = new StringBuilder();

        try {

            if (type.equals("kakao")){
                String text = null;
                try {
                    text = URLEncoder.encode(dto.getQuery(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("검색어 인코딩 실패",e);
                }
                stringBuilder.append("?query=").append(text);

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
                //TODO. naver param 값 넣기
            }

        }catch (Exception e){
            return "";
        }

        return stringBuilder.toString();
    }

    // 블로그 검색 결과 파싱
    @Override
    public BlogVo parseBlogSearchResult(String resultStr, BlogFindDto dto, String type){
        BlogVo blogVo = new BlogVo();

        if (type.equals("kakao")) {
            try {
                JSONObject jsonObject = new JSONObject(resultStr);

                JSONArray docJsonArray = jsonObject.getJSONArray("documents");
                ArrayList<DocumentVo> docList = new ArrayList<>();
                for (int i = 0; i < docJsonArray.length(); i++) {
//                    docList.add( mapper.map(docJsonArray.getJSONObject(i), DocumentVo.class) );
                    JSONObject obj = docJsonArray.getJSONObject(i);
                    DocumentVo documentVo = DocumentVo.builder()
                            .blogname(String.valueOf(obj.get("blogname")))
                            .contents(String.valueOf(obj.get("contents")))
                            .datetime(String.valueOf(obj.get("datetime")))
                            .thumbnail(String.valueOf(obj.get("thumbnail")))
                            .title(String.valueOf(obj.get("title")))
                            .url(String.valueOf(obj.get("url")))
                            .build();
                    docList.add(documentVo);
                }

                JSONObject metaJsonObject = jsonObject.getJSONObject("meta");
                String countStr = String.valueOf(metaJsonObject.get("total_count"));
                int total_count = Integer.parseInt(countStr);
                String pageableStr = String.valueOf(metaJsonObject.get("pageable_count"));
                int pageable_count = Integer.parseInt(pageableStr);
                String endStr = String.valueOf(metaJsonObject.get("is_end"));
                boolean is_end = Boolean.parseBoolean(endStr);

                int size = ( dto.getSize() > 0 ) ? dto.getSize() : 10;
                int page = ( dto.getPage() > 0 ) ? dto.getPage() : 1;
                int total_page = (int) Math.ceil(pageable_count * 1.0 / size);
                int current_page = Math.min(page, total_page);

                PageVo pageVo = PageVo.builder()
                        .total_count(total_count)
                        .pageable_count(pageable_count)
                        .is_end(is_end)
                        .total_page(total_page)
                        .current_page(current_page)
                        .size(size)
                        .build();

                blogVo.setDocuments(docList);
                blogVo.setPage(pageVo);

            } catch (Exception e) {
                throw new JSONException("kakao api 검색 결과를 변환하는 데 실패했습니다.", e);
            }

        } else {
            //TODO. 네이버 파싱 구현
        }

        return blogVo;
    }

    /**
     * 검색어 저장
     *  이미 저장된 검색어가 있으면 count++, 없으면 저장
     */
    @Transactional(rollbackFor = {Exception.class, Error.class})
    public void saveSearchSentence(String query) {
        Date today = new Date();
        SearchSentence searchSentence;

        Optional<SearchSentence> selectQuery = searchSentenceRepository.findById(query); // 검색어 검색
        if (selectQuery.isPresent()) {   // 검색한 적이 있으면,
            searchSentence = selectQuery.get();
            searchSentence.setCount( (searchSentence.getCount()+1) );
            searchSentence.setModificationDate(today);
        }else {
            searchSentence = new SearchSentence();
            searchSentence.setSentence(query);
            searchSentence.setCount(1);
            searchSentence.setRegistrationDate(today);
            searchSentence.setModificationDate(today);
        }
        searchSentenceRepository.saveAndFlush(searchSentence);

    }

}
