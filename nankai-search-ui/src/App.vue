<template>
  <div class="search-container">
    <div class="header">
      <h1 class="logo">南开搜索</h1>
      
      <div class="search-box-wrapper">
        <input 
          type="text" 
          class="capsule-input" 
          v-model="keyword" 
          @input="handleInput"
          @keyup.enter="doSearch"
          placeholder="探索校园万象..." 
        />
        <button class="capsule-btn" @click="doSearch">搜索</button>

        <div class="suggest-glass-panel" v-if="suggestions.length > 0">
          <div 
            class="suggest-item" 
            v-for="(item, index) in suggestions" 
            :key="index"
            @click="selectSuggest(item)"
          >
            {{ item }}
          </div>
        </div>
      </div>

      <div class="search-modes">
        <label class="mode-label" :class="{ active: searchMode === 'basic' }">
          <input type="radio" value="basic" v-model="searchMode" @change="doSearch" /> 站内查询
        </label>
        <label class="mode-label" :class="{ active: searchMode === 'document' }">
          <input type="radio" value="document" v-model="searchMode" @change="doSearch" /> 文档查询
        </label>
        <label class="mode-label" :class="{ active: searchMode === 'phrase' }">
          <input type="radio" value="phrase" v-model="searchMode" @change="doSearch" /> 短语查询
        </label>
        <label class="mode-label" :class="{ active: searchMode === 'wildcard' }">
          <input type="radio" value="wildcard" v-model="searchMode" @change="doSearch" /> 通配查询
        </label>
      </div>
    </div>

    <div class="history-section" v-if="historyList.length > 0 && results.length === 0">
      <h3>历史搜索</h3>
      <div class="history-tags">
        <span class="capsule-tag" v-for="tag in historyList" :key="tag.id" @click="selectSuggest(tag.keyword)">
          {{ tag.keyword }}
        </span>
      </div>
    </div>

    <div class="results-section">
      <div class="result-card" v-for="item in results" :key="item.id">
        <a :href="item.url" target="_blank" class="result-title" v-html="highlight(item.title)"></a>
        <p class="result-desc" v-html="highlight(item.content)"></p>
        <div class="result-meta">
          <span class="meta-tag" @click="openSnapshot(item.id)">网页文本缓存</span>
          <span class="meta-date">{{ item.date || '近期发布' }}</span>
        </div>
      </div>
    </div>

    <div class="snapshot-modal" v-if="isSnapshotVisible" @click.self="closeSnapshot">
      <div class="snapshot-dialog">
        <div class="snapshot-header">
          <h3>网页文本缓存 (离线容灾备份)</h3>
          <button class="close-btn" @click="closeSnapshot">关闭</button>
        </div>
        <div class="snapshot-body">
          <pre>{{ snapshotContent }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:8080'

axios.interceptors.request.use(config => {
  const token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzgzMTMxNzE4LCJleHAiOjE3ODMyMTgxMTh9.eSeaF87licd7boFJglLhx4lWXnJ17GUtv3L63uWWnTo" 
  if (token) {
    config.headers['token'] = token
  }
  return config
})

const keyword = ref('')
const suggestions = ref([])
const historyList = ref([])
const results = ref([])
const searchMode = ref('basic')

onMounted(async () => {
  try {
    const res = await axios.get('/api/search/history')
    if (res.data.code === 200) {
      historyList.value = res.data.data || []
    }
  } catch (error) {
    console.error("获取历史记录失败", error)
  }
})

let timeout = null
const handleInput = () => {
  clearTimeout(timeout)
  timeout = setTimeout(async () => {
    if (!keyword.value) {
      suggestions.value = []
      return
    }
    try {
      const res = await axios.get(`/api/search/suggest?prefix=${keyword.value}`)
      if (res.data.code === 200) {
        suggestions.value = res.data.data || []
      }
    } catch (error) {
      console.error("获取联想词失败", error)
    }
  }, 300)
}

const doSearch = async () => {
  suggestions.value = [] 
  if (!keyword.value) return
  
  try {
    const endpoint = `/api/search/${searchMode.value}?keyword=${keyword.value}`
    const res = await axios.get(endpoint)
    
    if (res.data.code === 200) {
      const rawData = res.data.data.content || []
      
      const uniqueMap = new Map()
      rawData.forEach(item => {
        if (item.title && !item.title.includes("moz-filter")) {
            if (!uniqueMap.has(item.title)) {
              uniqueMap.set(item.title, item)
            }
        }
      })
      results.value = Array.from(uniqueMap.values())
      
      const historyRes = await axios.get('/api/search/history')
      if (historyRes.data.code === 200) {
        historyList.value = historyRes.data.data || []
      }
    }
  } catch (error) {
    console.error("搜索请求失败", error)
  }
}

const selectSuggest = (text) => {
  keyword.value = text
  doSearch()
}

const highlight = (text) => {
  if (!text) return ''
  const regex = new RegExp(keyword.value, 'gi')
  return text.replace(regex, `<span style="color: #165DFF; font-weight: bold;">$&</span>`)
}

const isSnapshotVisible = ref(false)
const snapshotContent = ref('')

const openSnapshot = async (id) => {
  try {
    const res = await axios.get(`/api/search/snapshot?id=${id}`)
    if (res.data.code === 200) {
      snapshotContent.value = res.data.data.content || '暂无快照数据'
      isSnapshotVisible.value = true
    } else {
      alert("快照加载失败: " + res.data.msg)
    }
  } catch (error) {
    console.error("获取快照请求异常", error)
  }
}

const closeSnapshot = () => {
  isSnapshotVisible.value = false
  snapshotContent.value = ''
}
</script>

<style scoped>
.search-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  color: #333;
}

.snapshot-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.snapshot-dialog {
  width: 80%;
  max-width: 900px;
  height: 80vh;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
  overflow: hidden;
}

.snapshot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f2f3f5;
  border-bottom: 1px solid #e5e6eb;
}
.snapshot-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #1d2129;
}
.close-btn {
  background: #ff4d4f;
  color: white;
  border: none;
  padding: 6px 15px;
  border-radius: 999px;
  cursor: pointer;
}
.close-btn:hover {
  background: #d9363e;
}

.snapshot-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
  background: #1e1e1e;
  color: #d4d4d4;
}
.snapshot-body pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: Consolas, Monaco, monospace;
  font-size: 0.9rem;
  line-height: 1.5;
}

.logo {
  text-align: center;
  font-size: 2.5rem;
  color: #165DFF;
  margin-bottom: 30px;
  letter-spacing: 2px;
}

.search-box-wrapper {
  display: flex;
  justify-content: center;
  gap: 10px;
  position: relative;
  margin-bottom: 20px; /* 调整间距容纳单选按钮 */
}

.capsule-input {
  width: 60%;
  padding: 15px 25px;
  font-size: 1.1rem;
  border: 2px solid #e5e6eb;
  border-radius: 999px;
  outline: none;
  transition: all 0.3s ease;
}
.capsule-input:focus {
  border-color: #165DFF;
  box-shadow: 0 0 0 4px rgba(22, 93, 255, 0.1);
}

.capsule-btn {
  padding: 0 30px;
  font-size: 1.1rem;
  color: #fff;
  background-color: #165DFF;
  border: none;
  border-radius: 999px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.capsule-btn:hover {
  background-color: #1453e0;
}

.search-modes {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 40px;
}
.mode-label {
  padding: 6px 16px;
  font-size: 0.9rem;
  color: #4e5969;
  background-color: #f2f3f5;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.3s;
}
.mode-label input {
  display: none; /* 隐藏原生单选框 */
}
.mode-label:hover {
  background-color: rgba(22, 93, 255, 0.1);
  color: #165DFF;
}
.mode-label.active {
  background-color: #165DFF;
  color: #fff;
}

.suggest-glass-panel {
  position: absolute;
  top: 60px;
  width: 60%;
  left: 50%;
  transform: translateX(-54%);
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  z-index: 100;
}

.suggest-item {
  padding: 12px 25px;
  cursor: pointer;
  transition: background 0.2s;
}
.suggest-item:hover {
  background: rgba(22, 93, 255, 0.1);
  color: #165DFF;
}

.history-section {
  margin-bottom: 40px;
}
.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}
.capsule-tag {
  padding: 6px 16px;
  background: #f2f3f5;
  color: #4e5969;
  border-radius: 999px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}
.capsule-tag:hover {
  background: rgba(22, 93, 255, 0.1);
  color: #165DFF;
}

.result-card {
  padding: 20px 0;
  border-bottom: 1px solid #e5e6eb;
}
.result-title {
  font-size: 1.25rem;
  color: #165DFF;
  text-decoration: none;
  font-weight: 500;
  margin-bottom: 8px;
  display: block;
}
.result-title:hover {
  text-decoration: underline;
}
.result-desc {
  font-size: 0.95rem;
  color: #4e5969;
  line-height: 1.6;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.result-meta {
  display: flex;
  gap: 15px;
  font-size: 0.85rem;
}
.meta-tag {
  color: #165DFF;
  background: rgba(22, 93, 255, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  cursor: pointer;
}
.meta-date {
  color: #86909c;
}
</style>