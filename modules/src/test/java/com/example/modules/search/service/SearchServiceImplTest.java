package com.example.modules.search.service;

import com.example.modules.search.value.BlogFindDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//import static org.junit.Assert.assertThat;

@SpringBootTest
public class SearchServiceImplTest {

    @Autowired
    private SearchService searchService;

    @Test
    void setParameter() {
        // Given
        BlogFindDto dto = new BlogFindDto();
        dto.setQuery("검색어");

        // When
        String param = searchService.setParameter(dto,"kakao");

        // Then
//        Assert.assertEquals();
        System.out.println("### param : " + param);
    }

}
