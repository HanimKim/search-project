package com.example.modules.search.value;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BlogVo {
    private List<DocumentVo> documents;
    private PageVo page;

}
