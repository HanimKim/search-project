package com.example.modules.search.service;

import com.example.modules.search.value.BlogFindDto;
import com.example.modules.search.value.BlogVo;

import java.util.List;

public interface SearchService {
    List<BlogVo> findBlog(BlogFindDto dto);
}
