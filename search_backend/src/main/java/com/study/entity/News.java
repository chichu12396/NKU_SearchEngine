package com.study.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.InnerField;
import lombok.Data;

@Data
@Document(indexName = "nankai_news")
@JsonIgnoreProperties(ignoreUnknown = true) 
public class News {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @MultiField(
        mainField = @Field(type = FieldType.Text),
        otherFields = {
            @InnerField(suffix = "keyword", type = FieldType.Keyword)
        }
    )
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String date;

    @Field(type = FieldType.Keyword)
    private String source;

    @Field(type = FieldType.Keyword)
    @JsonProperty("file_type") 
    private String fileType; 

    @Field(type = FieldType.Text)
    @JsonProperty("anchor_texts") 
    private List<String> anchorTexts;
}