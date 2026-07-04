package com.study.service;

import com.study.entity.News;
import org.springframework.data.domain.Page;

public interface SearchService {
 
    Page<News> basicSearch(String keyword, int page, int size,Integer currentUserId);
 // 2.3.2 文档查询
    Page<News> documentSearch(String keyword, int page, int size);
 // 2.3.3 短语查询
    Page<News> phraseSearch(String keyword, int page, int size);
 // 2.3.4 通配查询
    Page<News> wildcardSearch(String keyword, int page, int size);
 // 个性化推荐：搜索联想
    java.util.List<String> getSearchSuggestions(String prefix);
}