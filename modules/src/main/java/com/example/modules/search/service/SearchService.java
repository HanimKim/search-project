package com.example.modules.search.service;

import com.example.modules.search.value.BlogFindDto;
import com.example.modules.search.value.BlogVo;
import com.example.modules.search.value.SearchSentenceVo;

import java.util.List;

public interface SearchService {
    BlogVo findBlog(BlogFindDto dto) throws Exception;
    List<SearchSentenceVo> findSearchSentences();
    String SearchBlogByKakaoApi(BlogFindDto dto);
    String setBlogParameter(BlogFindDto dto, String type);
    BlogVo parseBlogSearchResult(String resultStr, BlogFindDto dto, String type);
}
