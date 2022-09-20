package com.example.modules.search.repository;

import com.example.modules.search.value.SearchSentenceVo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.modules.search.domain.QSearchSentence.searchSentence;

@RequiredArgsConstructor
@Repository
public class SearchSentenceRepositorySupport {
    private final JPAQueryFactory queryFactory;

    public List<SearchSentenceVo> findSearchSentences() {
        return queryFactory.select(Projections.constructor(SearchSentenceVo.class,
                        searchSentence.sentence, searchSentence.count))
                .from(searchSentence)
                .orderBy(searchSentence.count.desc())
                .limit(10)
                .fetch();
    }
}
