<template>
  <div class="sql-page">
    <div class="container">
      <h1 class="page-title">数据库实体属性图生成器</h1>
      
      <div class="entity-generator">
        <div class="generator-inputs">
          <div class="input-group">
            <label>数据库建表语句</label>
            <el-input
              v-model="createTableSQL"
              type="textarea"
              :rows="6"
              placeholder="输入 CREATE TABLE 语句..."
              class="sql-input"
            />
            <el-button type="primary" @click="generateEntityDiagram" class="generate-btn">
              生成实体图
            </el-button>
          </div>
          <div class="input-group">
            <label>表名</label>
            <el-input v-model="tableName" placeholder="表名" class="table-name-input" />
            <label>属性修改</label>
            <el-input
              v-model="attributesInput"
              type="textarea"
              :rows="4"
              placeholder="属性列表，每行一个，格式：字段名:类型:描述"
              class="attributes-input"
            />
          </div>
        </div>
        
        <div class="entity-diagram" :class="{ 'fullscreen': isFullscreen }">
          <div class="diagram-header">
            <h3>实体属性图</h3>
            <div class="diagram-actions">
              <el-button @click="toggleFullscreen" class="fullscreen-btn">
                <el-icon><FullScreen /></el-icon>
                {{ isFullscreen ? '退出全屏' : '全屏' }}
              </el-button>
            </div>
          </div>
          <div v-if="!entityData" class="empty-diagram">
            <el-icon class="empty-icon"><DataAnalysis /></el-icon>
            <p>请输入建表语句并点击生成按钮</p>
          </div>
          <div v-else class="diagram-container">
            <div class="table-box">
              <div class="table-name">{{ entityData.tableName }}</div>
            </div>
            <div class="attributes-list">
              <div 
                v-for="(attr, index) in entityData.attributes" 
                :key="index"
                class="attribute-item"
              >
                <div class="attribute-ellipse">
                  <div class="attribute-name">{{ attr.name }}</div>
                  <div class="attribute-type">{{ attr.type }}</div>
                </div>
                <div class="attribute-line"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="entityData" class="entity-details">
          <h3>识别结果</h3>
          <div class="detail-item">
            <span class="detail-label">表名：</span>
            <span class="detail-value">{{ entityData.tableName }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">字段数：</span>
            <span class="detail-value">{{ entityData.attributes.length }} 个</span>
          </div>
          <div class="attributes-table">
            <table>
              <thead>
                <tr>
                  <th>字段名</th>
                  <th>类型</th>
                  <th>描述</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(attr, index) in entityData.attributes" :key="index">
                  <td>{{ attr.name }}</td>
                  <td>{{ attr.type }}</td>
                  <td>{{ attr.description || '-' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <div class="generator-actions">
          <el-button @click="goBack" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回首页
          </el-button>
          <el-button @click="resetGenerator" class="reset-btn">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, DataAnalysis, Refresh, FullScreen } from '@element-plus/icons-vue'

const router = useRouter()

// 输入数据
const createTableSQL = ref('')
const tableName = ref('')
const attributesInput = ref('')
const entityData = ref(null)
const isFullscreen = ref(false)

// 生成实体图
const generateEntityDiagram = () => {
  if (!createTableSQL.value.trim() && !tableName.value.trim()) {
    return
  }
  
  let parsedTableName = tableName.value.trim()
  let parsedAttributes = []
  
  // 从建表语句解析
  if (createTableSQL.value.trim()) {
    const sql = createTableSQL.value.trim()
    
    // 提取表名
    const tableNameMatch = sql.match(/CREATE\s+TABLE\s+(`?)([\w_]+)\1\s*\(/i)
    if (tableNameMatch && !parsedTableName) {
      parsedTableName = tableNameMatch[2]
    }
    
    // 提取属性 - 使用手动括号匹配
    let startIdx = sql.indexOf('(')
    if (startIdx !== -1) {
      let endIdx = startIdx
      let parenthesesCount = 1
      
      for (let i = startIdx + 1; i < sql.length; i++) {
        if (sql[i] === '(') {
          parenthesesCount++
        } else if (sql[i] === ')') {
          parenthesesCount--
          if (parenthesesCount === 0) {
            endIdx = i
            break
          }
        }
      }
      
      if (endIdx > startIdx) {
        const attributesStr = sql.substring(startIdx + 1, endIdx)
        console.log('Attributes string:', attributesStr)
        
        // 按逗号分割，但要处理括号内的逗号
        const attributeLines = []
        let currentLine = ''
        let inParentheses = 0
        
        for (let char of attributesStr) {
          if (char === '(') inParentheses++
          if (char === ')') inParentheses--
          if (char === ',' && inParentheses === 0) {
            attributeLines.push(currentLine.trim())
            currentLine = ''
          } else {
            currentLine += char
          }
        }
        if (currentLine.trim()) {
          attributeLines.push(currentLine.trim())
        }
        console.log('Attribute lines:', attributeLines)
        
        attributeLines.forEach(line => {
          console.log('Processing line:', line)
          if (line && !line.match(/^PRIMARY\s+KEY|^FOREIGN\s+KEY|^UNIQUE\s+KEY|^ENGINE|^CHARACTER|^COLLATE|^ROW_FORMAT/i)) {
            // 提取字段名和类型 - 更宽松的匹配
            const attrMatch = line.match(/^\s*(`?)([\w_]+)\1\s+([^\s]+(?:\([^)]*\))?)(?:\s+.*)?/i)
            console.log('Attr match:', attrMatch)
            if (attrMatch) {
              // 提取注释
              const commentMatch = line.match(/COMMENT\s+['"]([^'"]+)['"]/i)
              parsedAttributes.push({
                name: attrMatch[2],
                type: attrMatch[3].trim(),
                description: commentMatch ? commentMatch[1] : ''
              })
            }
          }
        })
      }
    }
  }
  
  // 从属性输入解析
  if (attributesInput.value.trim()) {
    const lines = attributesInput.value.trim().split('\n')
    parsedAttributes = lines.map(line => {
      const parts = line.split(':')
      return {
        name: parts[0]?.trim() || '',
        type: parts[1]?.trim() || '',
        description: parts[2]?.trim() || ''
      }
    }).filter(attr => attr.name)
  }
  
  // 如果没有表名，使用默认值
  if (!parsedTableName) {
    parsedTableName = 'TableName'
  }
  
  entityData.value = {
    tableName: parsedTableName,
    attributes: parsedAttributes.length > 0 ? parsedAttributes : [
      { name: 'id', type: 'INT', description: '主键' },
      { name: 'name', type: 'VARCHAR(255)', description: '名称' },
      { name: 'created_at', type: 'DATETIME', description: '创建时间' }
    ]
  }
}

// 重置
const resetGenerator = () => {
  createTableSQL.value = ''
  tableName.value = ''
  attributesInput.value = ''
  entityData.value = null
  isFullscreen.value = false
}

// 返回首页
const goBack = () => {
  router.push('/')
}

// 切换全屏
const toggleFullscreen = () => {
  isFullscreen.value = !isFullscreen.value
}
</script>

<style scoped>
.sql-page {
  min-height: 100vh;
  background-color: #F9F6F0;
  padding: 40px 0;
}

.container {
  width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  text-align: center;
  color: #333;
  margin-bottom: 40px;
  font-size: 28px;
  font-weight: 600;
}

.entity-generator {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  padding: 30px;
}

.generator-inputs {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.sql-input,
.attributes-input {
  width: 100%;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
}

.table-name-input {
  width: 100%;
}

.generate-btn {
  margin-top: 10px;
  align-self: flex-start;
}

.entity-diagram {
  margin-bottom: 30px;
  min-height: 400px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 30px;
  background-color: white;
  transition: all 0.3s ease;
}

.entity-diagram.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1000;
  margin: 0;
  border: none;
  border-radius: 0;
}

.diagram-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.diagram-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.empty-diagram {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #D4A76A;
}

.diagram-container {
  position: relative;
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.table-box {
  width: 200px;
  height: 80px;
  background-color: white;
  color: black;
  border: 2px solid black;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 40px;
  box-shadow: none;
  position: relative;
}

.table-name {
  font-weight: 600;
  font-size: 16px;
  color: black;
}

.attributes-list {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  justify-content: center;
  margin-top: 0;
  padding-top: 20px;
}

.attribute-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  margin-top: 40px;
}

.attribute-ellipse {
  width: 120px;
  height: 80px;
  background-color: white;
  border: 2px solid black;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  text-align: center;
  box-shadow: none;
}

.attribute-name {
  font-weight: 600;
  color: black;
  font-size: 14px;
  margin-bottom: 5px;
}

.attribute-type {
  font-size: 12px;
  color: black;
}

.attribute-line {
  width: 2px;
  height: 40px;
  background-color: black;
  position: absolute;
  top: -40px;
}

.entity-details {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #fafafa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.entity-details h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.detail-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.detail-label {
  font-weight: 500;
  color: #666;
  width: 80px;
}

.detail-value {
  color: #333;
  font-weight: 500;
}

.attributes-table {
  margin-top: 20px;
  overflow-x: auto;
}

.attributes-table table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e4e7ed;
  background-color: white;
}

.attributes-table th,
.attributes-table td {
  padding: 12px;
  text-align: left;
  border: 1px solid #e4e7ed;
}

.attributes-table th {
  background-color: #f5f7fa;
  font-weight: 600;
  color: #333;
}

.attributes-table tr:hover {
  background-color: #f5f7fa;
}

.generator-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.back-btn,
.reset-btn {
  min-width: 120px;
}

.fullscreen-btn {
  min-width: 100px;
}

@media (max-width: 1200px) {
  .container {
    width: 90%;
  }
  
  .generator-inputs {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .entity-generator {
    padding: 20px;
  }
  
  .entity-diagram {
    padding: 20px;
  }
  
  .table-box {
    width: 150px;
    height: 60px;
  }
  
  .attribute-ellipse {
    width: 100px;
    height: 70px;
  }
  
  .attributes-list {
    gap: 30px;
  }
  
  .attributes-table table {
    font-size: 14px;
  }
  
  .attributes-table th,
  .attributes-table td {
    padding: 8px;
  }
}
</style>