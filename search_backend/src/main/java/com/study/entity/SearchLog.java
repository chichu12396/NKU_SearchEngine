package com.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("search_log") 
public class SearchLog {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId; 
    
    private String keyword; 
    
    private Date searchTime; 
}