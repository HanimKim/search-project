package com.example.api.controller;

import com.example.modules.search.service.SearchService;
import com.example.modules.search.value.BlogFindDto;
import com.example.modules.search.value.BlogVo;
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
    public List<BlogVo> findBlog(@ModelAttribute BlogFindDto dto) {
        return searchService.findBlog(dto);
    }

}
