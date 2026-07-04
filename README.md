# NKU Web Search Engine（南开大学校内信息检索系统）

基于 **Elasticsearch + MySQL + Spring Boot + Vue3** 构建的南开大学校内全文检索系统。项目采用前后端分离架构，通过自研爬虫采集校内十万余条网页数据，结合倒排索引与个性化排序算法，实现了站内查询、文档查询、短语查询、通配查询、查询日志、网页快照等多种检索能力。

---

## 目录结构

```
.
├── nankai_spider/        # Python 爬虫，负责采集南开大学各院系/部门网站数据
├── search_backend/       # Spring Boot 后端服务
├── nankai-search-ui/     # Vue3 前端（南开搜索页面）
└── README.md
```

## 技术栈

| 模块 | 技术选型 | 核心职责 |
|---|---|---|
| 前端展示 | Vue 3 | 渲染 DOM，输入防抖控制减少无效请求 |
| 网络通信 | Axios | 发送 HTTP 请求，拦截请求并注入鉴权 Token |
| 业务处理 | Spring Boot | 暴露 RESTful 接口，解析请求并调用数据服务 |
| 安全鉴权 | JWT | 生成无状态身份令牌，解析请求确定用户 ID |
| 数据持久化 | MyBatis-Plus | 封装 SQL 操作，读写 MySQL 用户表与日志表 |
| 全文检索 | Elasticsearch | 构建倒排索引，执行短语/通配匹配 |
| 关系型数据库 | MySQL | 存储核心元数据，支撑偏好计算 |

## 系统架构

- **展示层**：Vue3 渲染界面，搜索框/按钮采用胶囊形状组件，下拉列表与弹窗采用毛玻璃半透明效果，主色调 `#165DFF`。
- **业务逻辑层**：后端处理接口请求，记录用户搜索行为日志，并据此统计历史偏好、调整文档排序。
- **数据层**：MySQL 存储结构化用户信息与高频检索日志；Elasticsearch 对爬虫获取的海量非结构化 HTML 原文建立倒排索引。

## 核心功能

1. **网页抓取**：Python 爬虫深度遍历南开大学校内各站点，最终采集约 10 万条数据，保存为 `nankai_data.jsonl`。
2. **文本索引**：基于 Elasticsearch 构建多字段倒排索引（标题、正文、URL、锚文本等），中文核心字段使用 `ik_max_word` 分词器。
3. **查询服务**（共 6 种高级搜索功能）：
   - 站内查询：标题/正文关键词精确匹配
   - 文档查询：支持 PDF/DOC 等文件检索与直接下载
   - 短语查询：基于 `match_phrase` 保证词序与相邻关系
   - 通配查询：支持 `*`（多字符）与 `?`（单字符）通配符
   - 查询日志：通过 JWT 解析用户身份，记录并展示最近 10 条搜索历史
   - 网页快照：存储爬虫抓取时的页面纯文本，防止链接失效或内容变更导致信息丢失
4. **个性化查询**：基于用户历史检索词计算兴趣偏好，采用 `bool` 查询将偏好词放入 `should` 子句并通过 `boost` 加权，实现个性化排序。
5. **搜索联想**：前端 300ms 防抖后请求后端，基于 `match_phrase_prefix` 实现搜索词自动补全。

---

## 环境要求

| 依赖 | 版本建议 |
|---|---|
| JDK | 1.8 |
| Maven | 3.6+ |
| Node.js | 16+ |
| MySQL | 5.7 / 8.0 |
| Elasticsearch | 7.15.1（需与后端 `elasticsearch-rest-high-level-client` 版本匹配） |
| IK 分词器插件 | 与 Elasticsearch 版本一致 |

---

## 启动说明

### 1. 启动 Elasticsearch

进入本地解压好的 Elasticsearch 目录：

```bash
cd elasticsearch-7.15.1/bin
elasticsearch.bat        # Windows
# 或
./elasticsearch          # Linux/Mac
```

保持该窗口持续运行，然后访问 [http://localhost:9200](http://localhost:9200) 验证，能看到集群信息 JSON 即为启动成功。

> 若启动缓慢或占用内存较高属正常现象（首次启动会下载/更新 GeoIP 数据库）。如需调整内存占用，可修改 `config/jvm.options` 中的 `-Xms` / `-Xmx`。

### 2. 启动 MySQL

确保本地 MySQL 服务已启动，并创建好项目所需数据库（用户表、`search_log` 表等），具体建表语句见 `search_backend` 项目下的 SQL 脚本（如有）。

### 3. 配置后端

打开 `search_backend/src/main/resources/application.yml`（或 `.properties`），核对以下配置与本地环境一致：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/你的数据库名
    username: 你的用户名
    password: 你的密码
  elasticsearch:
    uris: http://localhost:9200
```

### 4. 启动后端（Spring Boot）

```bash
cd search_backend
mvn clean install
mvn spring-boot:run
```

或直接在 IDE（IDEA）中运行启动类 `SpringbootTeachDemoApplication`。

后端默认启动后可通过 Swagger UI 查看接口文档（地址一般为 `http://localhost:8080/swagger-ui.html` 或项目内配置的路径）。

### 5. 启动前端

```bash
cd nankai-search-ui
npm install
npm run dev
```

启动成功后访问终端提示的地址（通常为 `http://localhost:5173`）。

### 6.（可选）运行爬虫获取数据

```bash
cd nankai_spider
pip install -r requirements.txt
python 爬虫入口脚本.py
```

爬取完成后会在 `nankai_spider` 目录下生成 `nankai_data.jsonl`，可通过如下命令查看数据条数：

```bash
(Get-Content nankai_data.jsonl | Measure-Object -Line).Lines
```

将数据导入 Elasticsearch 的具体方式请参考后端项目内的数据导入脚本/接口。

---

## 常见问题排查

- **后端启动报错 `Timeout connecting to [localhost/127.0.0.1:9200]`**：说明 Elasticsearch 未启动或未监听 9200 端口，请先按上文步骤启动 ES 并验证。
- **中文分词效果不佳**：确认 Elasticsearch 已正确安装与版本匹配的 IK 分词器插件。
- **前端请求跨域或 401**：检查 JWT Token 是否正确携带在请求 Header 中，以及后端跨域配置。

---

## 数据与隐私说明

- `nankai_data.jsonl` 等大体积爬虫数据文件、`elasticsearch-7.15.1` 安装包等不纳入 Git 版本管理（详见 `.gitignore`），如需数据集请自行运行爬虫生成或通过其他渠道获取。
- 请勿将账号密码等敏感信息提交到仓库中。
