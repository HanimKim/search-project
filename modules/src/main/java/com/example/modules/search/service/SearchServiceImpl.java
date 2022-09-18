package com.example.modules.search.service;

import com.example.modules.search.value.BlogFindDto;
import com.example.modules.search.value.BlogVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{

    public List<BlogVo> findBlog(BlogFindDto dto){
        return null;
    }
}
