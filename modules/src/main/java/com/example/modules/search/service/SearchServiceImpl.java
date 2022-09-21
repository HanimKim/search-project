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

    @Value("${spring.naver-api.host}") private String NAVER_HOST;
    @Value("${spring.naver-api.blog-path}") private String NAVER_BLOG_PATH;
    @Value("${spring.naver-api.client-id}") private String NAVER_CLIENT_ID;
    @Value("${spring.naver-api.client-secret}") private String NAVER_CLIENT_SECRET;

    private final String KAKAO = "kakao";
    private final String NAVER = "naver";

    private final String ACCURACY = "accuracy";
    private final String RECENCY = "recency";

    private final ModelMapper mapper;

    private final SearchSentenceRepository searchSentenceRepository;

    private final SearchSentenceRepositorySupport searchSentenceRepositorySupport;

    @Override
    public BlogVo findBlog(BlogFindDto dto) {
        BlogVo blogVo = new BlogVo();

        String resultStr = SearchBlogByKakaoApi(dto);   // kakao api로 검색

        if (resultStr != null) {
            blogVo = parseBlogSearchResult(resultStr, dto, KAKAO);    // 결과값 parsing
        }else {     // kakao api로 검색이 안됬을 시, navaer api로 재검색
            resultStr = SearchBlogByNaverApi(dto);
            if (resultStr != null) {
                blogVo = parseBlogSearchResult(resultStr, dto, NAVER);
            }
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
            String param = setBlogParameter(dto, KAKAO);

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", "KakaoAK " + KAKAO_REST_API_KEY);

            result = HttpConnectionUtil.get(url+param, requestHeaders);
        }catch (Exception e){
            result = null;
        }

        return result;
    }

    // naver blog search api 호출
    @Override
    public String SearchBlogByNaverApi(BlogFindDto dto){
        String result = "";
        try {
            String url = NAVER_HOST + NAVER_BLOG_PATH;
            String param = setBlogParameter(dto, NAVER);

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", NAVER_CLIENT_ID);
            requestHeaders.put("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);

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
            String text = null;
            try {
                text = URLEncoder.encode(dto.getQuery(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("검색어 인코딩 실패",e);
            }

            stringBuilder.append("?query=").append(text);

            if (type.equals(KAKAO)){  // kakao param setting
                if (dto.getSort() != null && !dto.getSort().equals("")){
                    String sort = dto.getSort();
                    if (sort.equals(ACCURACY) || sort.equals(RECENCY)){
                        stringBuilder.append("&sort=").append(sort);
                    }
                }
                if (dto.getPage() > 0){
                    if (dto.getPage() > 50) dto.setPage(1);
                    stringBuilder.append("&page=").append(dto.getPage());
                }
                if (dto.getSize() > 0){
                    if (dto.getSize() > 50) dto.setSize(10);
                    stringBuilder.append("&size=").append(dto.getSize());
                }
            }else{  // naver param setting
                if (dto.getSort() != null && !dto.getSort().equals("")){
                    String sort = dto.getSort();
                    if (sort.equals(ACCURACY)){  //정확도순
                        sort = "sim";
                        stringBuilder.append("&sort=").append(sort);
                    }else if (sort.equals(RECENCY)){  //최신순
                        sort = "date";
                        stringBuilder.append("&sort=").append(sort);
                    }
                }
                if (dto.getPage() > 0){
                    if (dto.getPage() > 50) dto.setPage(1);
                    stringBuilder.append("&start=").append(dto.getPage());
                }
                if (dto.getSize() > 0){
                    if (dto.getSize() > 50) dto.setSize(10);
                    stringBuilder.append("&display=").append(dto.getSize());
                }
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

        if (type.equals(KAKAO)) {     // kakao data parsing
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

        } else {        // naver data parsing
            try {
                JSONObject jsonObject = new JSONObject(resultStr);

                JSONArray docJsonArray = jsonObject.getJSONArray("items");
                ArrayList<DocumentVo> docList = new ArrayList<>();
                for (int i = 0; i < docJsonArray.length(); i++) {
                    JSONObject obj = docJsonArray.getJSONObject(i);

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(String.valueOf(obj.get("postdate")));
                    stringBuilder.append("T00:00:00.000+09:00");
                    stringBuilder.insert(4, "-");
                    stringBuilder.insert(7, "-");

                    DocumentVo documentVo = DocumentVo.builder()
                            .blogname(String.valueOf(obj.get("bloggername")))
                            .contents(String.valueOf(obj.get("description")))
                            .datetime(stringBuilder.toString())
                            .thumbnail("")
                            .title(String.valueOf(obj.get("title")))
                            .url(String.valueOf(obj.get("link")))
                            .build();
                    docList.add(documentVo);
                }

                String countStr = String.valueOf(jsonObject.get("total"));  // 총 검색 갯수
                int total_count = Integer.parseInt(countStr);
                String startStr = String.valueOf(jsonObject.get("start"));     // 현재 페이지
                int start = Integer.parseInt(startStr);
                String displayStr = String.valueOf(jsonObject.get("display"));  //한 페이지에 표시할 검색 결과 개수
                int display = Integer.parseInt(displayStr);

                int pageable_count = Math.min(total_count, 2500);
                int total_page = (int) Math.ceil(pageable_count * 1.0 / display);
                boolean is_end = (start < total_page);

                PageVo pageVo = PageVo.builder()
                        .total_count(total_count)
                        .pageable_count(pageable_count)
                        .is_end(is_end)
                        .total_page(total_page)
                        .current_page(start)
                        .size(display)
                        .build();

                blogVo.setDocuments(docList);
                blogVo.setPage(pageVo);

            } catch (Exception e) {
                throw new JSONException("naver api 검색 결과를 변환하는 데 실패했습니다.", e);
            }
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
