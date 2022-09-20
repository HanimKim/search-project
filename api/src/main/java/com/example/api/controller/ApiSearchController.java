package com.example.api.controller;

import com.example.modules.search.service.SearchService;
import com.example.modules.search.value.BlogFindDto;
import com.example.modules.search.value.BlogVo;
import com.example.modules.search.value.SearchSentenceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("Search APIs")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/search")
public class ApiSearchController {

    private final SearchService searchService;

    @ApiOperation(value = "블로그 조회"
            , notes = "블로그를 조회하는 api </br>" +
            " </br>")
    @GetMapping("/blog")
    public BlogVo findBlog(@ModelAttribute BlogFindDto dto) throws Exception {
        return searchService.findBlog(dto);
    }

    @ApiOperation(value = "인기 검색어 조회"
            , notes = "인기 검색어를 조회하는 api </br>" +
            "검색어와 검색 횟수를 제공하며, 검색 횟수가 높은 순으로 정렬되어 제공 된다. </br>" +
            "최대 10개의 검색어까지 노출 된다. </br>")
    @GetMapping("/popularity/sentences")
    public List<SearchSentenceVo> findSearchSentences(){
        return searchService.findSearchSentences();
    }

}
