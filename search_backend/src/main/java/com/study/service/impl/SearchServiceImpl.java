package com.study.service.impl;

import com.study.entity.News;
import com.study.repository.NewsRepository;
import com.study.service.PreferenceService;
import com.study.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private PreferenceService preferenceService;

    @Override
    public Page<News> basicSearch(String keyword, int page, int size,Integer currentUserId) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        
        String favKeyword = preferenceService.getUserFavoriteKeyword(currentUserId);

        if (favKeyword != null && !favKeyword.isEmpty()) {
            System.out.println("触发个性化搜索！该用户偏好词为：" + favKeyword);
            return newsRepository.searchWithPersonalization(keyword, favKeyword, pageRequest);
        } else {
            System.out.println("普通搜索通道");
            return newsRepository.findByTitleOrContent(keyword, keyword, pageRequest);
        }
    }
    @Override
    public Page<News> documentSearch(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return newsRepository.searchDocuments(keyword, pageRequest);
    }
    @Override
    public Page<News> phraseSearch(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return newsRepository.searchPhrase(keyword, pageRequest);
    }
    @Override
    public Page<News> wildcardSearch(String keyword, int page, int size) {
        String finalKeyword = "*" + keyword + "*";
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return newsRepository.searchWildcard(finalKeyword, pageRequest);
    }
    @Override
    public java.util.List<String> getSearchSuggestions(String prefix) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<News> newsPage = newsRepository.suggestSearch(prefix, pageRequest);

        return newsPage.getContent().stream()
                .map(News::getTitle)
                .distinct() 
                .limit(5)  
                .collect(java.util.stream.Collectors.toList());
    }
}