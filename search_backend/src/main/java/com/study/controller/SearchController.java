package com.study.controller;

import javax.servlet.http.HttpServletRequest;
import com.study.utils.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.study.entity.News;
import com.study.entity.Result;
import com.study.entity.SearchLog;
import com.study.mapper.SearchLogMapper;
import com.study.repository.NewsRepository;
import com.study.service.SearchService;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@CrossOrigin 
public class SearchController {

    @Autowired
    private SearchService searchService;
    
    @Autowired
    private SearchLogMapper searchLogMapper;
    
    @Autowired
    private NewsRepository newsRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/basic")
    public Result basicSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) { 

        String token = request.getHeader("token");
        Integer currentUserId = null;

        if (token != null && jwtUtil.validate(token)) {
            currentUserId = jwtUtil.getUserId(token);
        }

        if (currentUserId != null) {
            SearchLog log = new SearchLog();
            log.setKeyword(keyword);
            log.setSearchTime(new Date());
            log.setUserId(currentUserId); 
            searchLogMapper.insert(log);
        }

        Page<News> newsPage = searchService.basicSearch(keyword, page, size, currentUserId);
        
        return Result.success(newsPage);
    }

    @GetMapping("/document")
    public Result documentSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<News> documentPage = searchService.documentSearch(keyword, page, size);
        return Result.success(documentPage);
    }

    @GetMapping("/phrase")
    public Result phraseSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<News> phrasePage = searchService.phraseSearch(keyword, page, size);
        return Result.success(phrasePage);
    }

    @GetMapping("/wildcard")
    public Result wildcardSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<News> wildcardPage = searchService.wildcardSearch(keyword, page, size);
        return Result.success(wildcardPage);
    }

    @GetMapping("/history")
    public Result getSearchHistory(HttpServletRequest request) { 
        
        String token = request.getHeader("token");

        if (token == null || !jwtUtil.validate(token)) {
            return Result.error("未登录或 Token 失效，无法获取搜索历史");
        }
  
        Integer currentUserId = jwtUtil.getUserId(token); 

        QueryWrapper<SearchLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId)
                    .orderByDesc("search_time")
                    .last("limit 10");
                    
        List<SearchLog> historyList = searchLogMapper.selectList(queryWrapper);
        return Result.success(historyList);
    }

    // 2.3.6 网页快照：根据 ES 的唯一 ID 直接读取缓存的内容
    @GetMapping("/snapshot")
    public Result getSnapshot(@RequestParam String id) {
        // findById 是 Spring Data ES 自带的方法
        java.util.Optional<News> newsOptional = newsRepository.findById(id);
        
        if (newsOptional.isPresent()) {
            return Result.success(newsOptional.get());
        } else {
            return Result.error("快照不存在或已丢失");
        }
    }

    // 个性化推荐：搜索联想接口
    @GetMapping("/suggest")
    public Result getSuggestions(@RequestParam String prefix) {
        // 如果用户还没输入内容，直接返回空列表
        if (prefix == null || prefix.trim().isEmpty()) {
            return Result.success(java.util.Collections.emptyList());
        }
        
        java.util.List<String> suggestions = searchService.getSearchSuggestions(prefix);
        return Result.success(suggestions);
    }
}