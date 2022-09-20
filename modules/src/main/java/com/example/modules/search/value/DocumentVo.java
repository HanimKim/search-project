package com.example.modules.search.value;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DocumentVo {
    private String blogname;            // 블로그의 이름
    private String contents;            // 블로그 글 요약
    private String datetime;            // 블로그 글 작성시간
    private String thumbnail;           // 검색 시스템에서 추출한 대표 미리보기 이미지 URL
    private String title;               // 블로그 글 제목
    private String url;                 // 블로그 글 URL
}
