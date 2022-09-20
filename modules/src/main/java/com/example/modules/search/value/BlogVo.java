package com.example.modules.search.value;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BlogVo {
    private List<DocumentVo> documents;
    private PageVo page;
}
