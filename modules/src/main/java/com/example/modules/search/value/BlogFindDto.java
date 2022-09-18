package com.example.modules.search.value;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogFindDto {

    @ApiModelProperty(value = "검색을 원하는 질의어", dataType = "string", required = true)
    private String query;

    @ApiModelProperty(value = "결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순)", dataType = "string")
    private String sort;

    @ApiModelProperty(value = "결과 페이지 번호", dataType = "integer")
    private int page;

    @ApiModelProperty(value = "한 페이지에 보여질 문서 수", dataType = "integer")
    private int size;
}
