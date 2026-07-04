package com.study.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.study.entity.SearchLog;
import com.study.mapper.SearchLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PreferenceService {

    @Autowired
    private SearchLogMapper searchLogMapper;


    public String getUserFavoriteKeyword(Integer userId) {
        if (userId == null) {
            return null;
        }
        QueryWrapper<SearchLog> wrapper = new QueryWrapper<>();
        wrapper.select("keyword", "count(*) as cnt")
               .eq("user_id", userId)
               .groupBy("keyword")
               .orderByDesc("cnt")
               .last("limit 1");

        List<Map<String, Object>> list = searchLogMapper.selectMaps(wrapper);
        
        if (list != null && !list.isEmpty()) {

            return (String) list.get(0).get("keyword");
        }
        
        return null; 
    }
}