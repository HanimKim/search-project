package com.example.modules.search.value;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageVo {
    private int total_count;                // 검색된 문서 수
    private int pageable_count;             // total_count 중 노출 가능 문서 수
    private boolean is_end;                 // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
    private int total_page;                 // 총 페이지 개수
    private int current_page;               // 현재 페이지
    private int size;                       // 한 페이지에 보여질 문서 수

    // Lombok boolean 형 변수 사용 시, 변수명이 is** 형태면 is가 사라지는 현상 발생.
    // 해결 방법으로 get 메소드를 명시해주면 된다.
    public boolean getIs_end() {
        return this.is_end;
    }
}
