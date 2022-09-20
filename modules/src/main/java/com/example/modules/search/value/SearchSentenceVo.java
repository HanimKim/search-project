package com.example.modules.search.value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor     // 모든 변수가 들어간 생성자 만들어 줌.
public class SearchSentenceVo {
    private String sentence;
    private int count;
}
