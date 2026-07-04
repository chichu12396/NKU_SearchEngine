package com.study.repository;

import com.study.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends ElasticsearchRepository<News, String> {

	// 2.3.1 站内查询
	@Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^5\", \"content\"], \"operator\": \"and\"}}")
    Page<News> findByTitleOrContent(String keyword, String keyword2, Pageable pageable);

    // 2.3.2 文档查询 
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"content\", \"anchorTexts\"]}}], \"filter\": [{\"terms\": {\"fileType\": [\"pdf\", \"doc\", \"docx\", \"xls\", \"xlsx\", \"ppt\", \"pptx\"]}}]}}")
    Page<News> searchDocuments(String keyword, Pageable pageable);
    
    // 2.3.3 短语查询 
    @Query("{\"bool\": {\"should\": [{\"match_phrase\": {\"title\": \"?0\"}}, {\"match_phrase\": {\"content\": \"?0\"}}, {\"match_phrase\": {\"anchorTexts\": \"?0\"}}]}}")
    Page<News> searchPhrase(String keyword, Pageable pageable);
   // 2.3.4 通配查询：原生支持 *（多字符）和 ?（单字符）
    @Query("{\"wildcard\": {\"title.keyword\": {\"value\": \"?0\"}}}")
    Page<News> searchWildcard(String keyword, Pageable pageable);
    
    @Query("{\"bool\": {" +
            "  \"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title^5\", \"content\"], \"operator\": \"and\"}}]," +
            "  \"should\": [{\"multi_match\": {\"query\": \"?1\", \"fields\": [\"title^5\", \"content\"], \"boost\": 2.0}}]" +
            "}}")
     Page<News> searchWithPersonalization(String keyword, String favKeyword, Pageable pageable);
    // 2.3.7 个性化推荐：搜索联想 (短语前缀匹配)
    @Query("{\"match_phrase_prefix\": {\"title\": \"?0\"}}")
    Page<News> suggestSearch(String prefix, Pageable pageable);
}