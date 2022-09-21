package com.example.api.controller;

import com.example.modules.search.service.SearchService;
import com.example.modules.search.value.BlogVo;
import com.example.modules.search.value.DocumentVo;
import com.example.modules.search.value.PageVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


// https://memostack.tistory.com/197 참고
@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiSearchController.class)
@DisplayName("ApiSearchController 테스트")
public class ApiSearchControllerTest {

    private MockMvc mvc;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        mvc =
                MockMvcBuilders.standaloneSetup(new ApiSearchController(searchService))
                        .addFilters(new CharacterEncodingFilter("UTF-8", true)) // utf-8 필터 추가
                        .build();
    }

    @Test
    @DisplayName("Blog 검색 테스트")
    void findBlog() throws Exception{

        // given
        List<DocumentVo> documents = new ArrayList<>();
        final DocumentVo documentVo = DocumentVo.builder()
                .blogname("꿀복이네")
                .contents("꿀복이네 생활보감 # 바나나 바나나가 무슨 말인지 알고 계셨나요? 원주민 말로 &#39;밥&#39;이라는 의미를 지닌 말이었답니다. 바나나는 대표적인 후숙(즉 익혀서 먹는) 과일에 속합니다. 바나나를 떠올렸을 때 설명할 수 있는 단어로는 &#39;부드러움&#39;, &#39;촉촉함&#39;, &#39;달콤함&#39;, &#39;푹신푹신&#39;이 아닐까요? 바나나를 상온 약 13도 이상의...")
                .datetime("2022-08-29T08:26:04.000+09:00")
                .thumbnail("https://search3.kakaocdn.net/argon/130x130_85_c/5rO5XTtgfmB")
                .title("바나나 영양소 성분 효능 주의사항(<b>Banana</b>)")
                .url("http://blog.pig2cow.com/224")
                .build();
        documents.add(documentVo);

        final PageVo pageVo = PageVo.builder()
                .total_count(112537)
                .pageable_count(800)
                .is_end(false)
                .total_page(800)
                .current_page(1)
                .size(1)
                .build();

        given(searchService.findBlog(any()))
                .willReturn(
                        BlogVo.builder()
                                .documents(documents)
                                .page(pageVo)
                                .build());

        // when
        final ResultActions actions =
                mvc.perform(
                        get("/v1/search/blog?query=banana&page=1&size=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8"));

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("blogname").value("꿀복이네"))
                .andExpect(jsonPath("contents").value("꿀복이네 생활보감 # 바나나 바나나가 무슨 말인지 알고 계셨나요? 원주민 말로 &#39;밥&#39;이라는 의미를 지닌 말이었답니다. 바나나는 대표적인 후숙(즉 익혀서 먹는) 과일에 속합니다. 바나나를 떠올렸을 때 설명할 수 있는 단어로는 &#39;부드러움&#39;, &#39;촉촉함&#39;, &#39;달콤함&#39;, &#39;푹신푹신&#39;이 아닐까요? 바나나를 상온 약 13도 이상의..."))
                .andExpect(jsonPath("datetime").value("2022-08-29T08:26:04.000+09:00"))
                .andExpect(jsonPath("thumbnail").value("https://search3.kakaocdn.net/argon/130x130_85_c/5rO5XTtgfmB"))
                .andExpect(jsonPath("title").value("바나나 영양소 성분 효능 주의사항(<b>Banana</b>)"))
                .andExpect(jsonPath("url").value("http://blog.pig2cow.com/224"));
    }

}
