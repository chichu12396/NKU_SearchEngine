package com.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.entity.News;
import com.study.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DataImportTest {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    public void importDataToES() {
        // 你的数据文件绝对路径
        String filePath = "D:\\信息检索系统原理\\hw4\\nankai_spider\\nankai_data.jsonl";
        
        // Spring Boot 自带的 Jackson JSON 解析神器
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 为什么要用 List？因为一条一条存 ES 太慢了，我们采用“攒够 1000 条，一次性打包入库”的策略
        List<News> batchList = new ArrayList<>();
        int batchSize = 1000;
        int totalCount = 0;

        System.out.println(" 开始读取数据并导入 ElasticSearch...");
        long startTime = System.currentTimeMillis();

        // 采用 BufferedReader 逐行读取，防止 100MB 的文件一次性撑爆内存
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    // 将每一行 JSON 字符串直接反序列化为你的 News 实体类
                    News news = objectMapper.readValue(line, News.class);
                    batchList.add(news);
                    totalCount++;

                    // 攒够 1000 条，执行一次批量保存
                    if (batchList.size() >= batchSize) {
                        newsRepository.saveAll(batchList);
                        System.out.println("已成功导入: " + totalCount + " 条...");
                        batchList.clear(); // 清空集合，准备装下一批
                    }
                } catch (Exception e) {
                    System.err.println("某行 JSON 解析跳过 (可能是脏数据): " + e.getMessage());
                }
            }

            // 循环结束后，把最后剩下不够 1000 条的尾巴也存进去
            if (!batchList.isEmpty()) {
                newsRepository.saveAll(batchList);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("导入大功告成！总计导入: " + totalCount + " 条，耗时: " + (endTime - startTime) / 1000 + " 秒");

        } catch (Exception e) {
            System.err.println("读取文件发生严重错误: " + e.getMessage());
        }
    }
}