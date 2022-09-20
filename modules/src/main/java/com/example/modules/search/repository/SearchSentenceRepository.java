package com.example.modules.search.repository;

import com.example.modules.search.domain.SearchSentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchSentenceRepository extends JpaRepository<SearchSentence, String> {
}
