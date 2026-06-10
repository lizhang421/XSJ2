<template>
  <div class="system-settings">
    <div class="page-header">
      <h1 class="page-title">系统设置</h1>
    </div>
    
    <div class="settings-container">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="评分配置" name="score">
          <div class="settings-section" v-loading="loading">
            <div class="section-header">
              <h3 class="section-title">视频评分系统配置</h3>
              <el-button type="primary" @click="handleEditScore">
                修改配置
              </el-button>
            </div>
            <div v-if="scoreConfig" class="score-config-card">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="配置ID">{{ scoreConfig.id }}</el-descriptions-item>
                <el-descriptions-item label="版本">{{ scoreConfig.version }}</el-descriptions-item>
                <el-descriptions-item label="启用状态">
                  <el-tag :type="scoreConfig.enable === 1 || scoreConfig.enable === true ? 'success' : 'danger'">
                    {{ scoreConfig.enable === 1 || scoreConfig.enable === true ? '已启用' : '已禁用' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="评分窗口">
                  {{ scoreConfig.scoreWindowHours }} 小时
                </el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDate(scoreConfig.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="更新时间">{{ formatDate(scoreConfig.updateTime) }}</el-descriptions-item>
                <el-descriptions-item label="创建人">{{ scoreConfig.createBy }}</el-descriptions-item>
                <el-descriptions-item label="更新人">{{ scoreConfig.updateBy }}</el-descriptions-item>
                <el-descriptions-item label="备注" :span="2">{{ scoreConfig.remark }}</el-descriptions-item>
              </el-descriptions>
              
              <el-divider content-position="left">权重配置</el-divider>
              
              <div class="weight-config">
                <el-row :gutter="20">
                  <el-col :span="8">
                    <el-tooltip 
                      content="完播率权重：用户完整观看视频的比例权重" 
                      placement="top"
                    >
                      <div class="weight-item">
                        <div class="weight-label">
                          <el-icon><InfoFilled /></el-icon>
                          完播率权重
                        </div>
                        <div class="weight-value">{{ scoreConfig.finishRateWeight }}%</div>
                      </div>
                    </el-tooltip>
                  </el-col>
                  
                  <el-col :span="8">
                    <el-tooltip 
                      content="点赞率权重：用户点赞视频的比例权重" 
                      placement="top"
                    >
                      <div class="weight-item">
                        <div class="weight-label">
                          <el-icon><InfoFilled /></el-icon>
                          点赞率权重
                        </div>
                        <div class="weight-value">{{ scoreConfig.likeRateWeight }}%</div>
                      </div>
                    </el-tooltip>
                  </el-col>
                  
                  <el-col :span="8">
                    <el-tooltip 
                      content="评论率权重：用户评论视频的比例权重" 
                      placement="top"
                    >
                      <div class="weight-item">
                        <div class="weight-label">
                          <el-icon><InfoFilled /></el-icon>
                          评论率权重
                        </div>
                        <div class="weight-value">{{ scoreConfig.commentRateWeight }}%</div>
                      </div>
                    </el-tooltip>
                  </el-col>
                </el-row>
                
                <el-row :gutter="20" style="margin-top: 20px;">
                  <el-col :span="8">
                    <el-tooltip 
                      content="收藏率权重：用户收藏视频的比例权重" 
                      placement="top"
                    >
                      <div class="weight-item">
                        <div class="weight-label">
                          <el-icon><InfoFilled /></el-icon>
                          收藏率权重
                        </div>
                        <div class="weight-value">{{ scoreConfig.collectRateWeight }}%</div>
                      </div>
                    </el-tooltip>
                  </el-col>
                  
                  <el-col :span="8">
                    <el-tooltip 
                      content="分享率权重：用户分享视频的比例权重" 
                      placement="top"
                    >
                      <div class="weight-item">
                        <div class="weight-label">
                          <el-icon><InfoFilled /></el-icon>
                          分享率权重
                        </div>
                        <div class="weight-value">{{ scoreConfig.shareRateWeight }}%</div>
                      </div>
                    </el-tooltip>
                  </el-col>
                  
                  <el-col :span="8">
                    <el-tooltip 
                      content="时间得分权重：视频发布时间对评分的影响权重" 
                      placement="top"
                    >
                      <div class="weight-item">
                        <div class="weight-label">
                          <el-icon><InfoFilled /></el-icon>
                          时间得分权重
                        </div>
                        <div class="weight-value">{{ scoreConfig.timeScoreWeight }}%</div>
                      </div>
                    </el-tooltip>
                  </el-col>
                </el-row>
                
                <div class="weight-total">
                  <span class="total-label">权重总和：</span>
                  <el-tag 
                    :type="getTotalWeight() === 100 ? 'success' : 'danger'" 
                    size="large"
                  >
                    {{ getTotalWeight() }}%
                    <el-icon v-if="getTotalWeight() !== 100" style="margin-left: 4px;">
                      <WarningFilled />
                    </el-icon>
                  </el-tag>
                  <span v-if="getTotalWeight() !== 100" class="warning-text">
                    权重总和必须等于100%
                  </span>
                </div>
              </div>
              
              <el-divider content-position="left">评分计算公式</el-divider>
              
              <div class="formula-section">
                <div class="formula-group">
                  <h4 class="formula-title">互动得分计算</h4>
                  <div class="formula-item">
                    <span class="formula-label">点赞分 =</span>
                    <span class="formula-content">(点赞数 / 播放数) × 点赞权重</span>
                  </div>
                  <div class="formula-item">
                    <span class="formula-label">评论分 =</span>
                    <span class="formula-content">(评论数 / 播放数) × 评论权重</span>
                  </div>
                  <div class="formula-item">
                    <span class="formula-label">收藏分 =</span>
                    <span class="formula-content">(收藏数 / 播放数) × 收藏权重</span>
                  </div>
                  <div class="formula-item">
                    <span class="formula-label">分享分 =</span>
                    <span class="formula-content">(分享数 / 播放数) × 分享权重</span>
                  </div>
                  <div class="formula-item highlight">
                    <span class="formula-label">互动总分 =</span>
                    <span class="formula-content">点赞分 + 评论分 + 收藏分 + 分享分</span>
                  </div>
                </div>
                
                <div class="formula-group">
                  <h4 class="formula-title">时间得分计算</h4>
                  <div class="formula-item">
                    <span class="formula-label">已发布小时数 =</span>
                    <span class="formula-content">当前时间 - 发布时间</span>
                  </div>
                  <div class="formula-item">
                    <span class="formula-label">时间系数 =</span>
                    <span class="formula-content">(168 - 已发布小时数) / 168</span>
                  </div>
                  <div class="formula-item">
                    <span class="formula-label">说明：</span>
                    <span class="formula-content">超过168小时 → 时间系数 = 0</span>
                  </div>
                  <div class="formula-item highlight">
                    <span class="formula-label">时间分 =</span>
                    <span class="formula-content">时间系数 × 时间权重</span>
                  </div>
                </div>
                
                <div class="formula-group">
                  <h4 class="formula-title">最终得分与池分类</h4>
                  <div class="formula-item highlight">
                    <span class="formula-label">最终得分 =</span>
                    <span class="formula-content">互动总分 + 时间分（限制 0~100）</span>
                  </div>
                  <div class="pool-rules">
                    <h5 class="rules-title">池分类规则：</h5>
                    <div class="rule-item">
                      <el-tag type="danger" size="large">≥71分</el-tag>
                      <span class="rule-arrow">→</span>
                      <span class="rule-result">爆款池</span>
                    </div>
                    <div class="rule-item">
                      <el-tag type="warning" size="large">41~70分</el-tag>
                      <span class="rule-arrow">→</span>
                      <span class="rule-result">长尾池</span>
                    </div>
                    <div class="rule-item">
                      <el-tag type="info" size="large">21~40分</el-tag>
                      <span class="rule-arrow">→</span>
                      <span class="rule-result">冷池</span>
                    </div>
                    <div class="rule-item">
                      <el-tag type="primary" size="large">&lt;21分</el-tag>
                      <span class="rule-arrow">→</span>
                      <span class="rule-result">冷启动池</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="池配置" name="pool">
          <div class="settings-section" v-loading="loading">
            <div class="section-header">
              <h3 class="section-title">视频池配置管理</h3>
              <el-button type="primary" @click="handleAddPool">
                新增池配置
              </el-button>
            </div>
            <el-table :data="poolConfigs" style="width: 100%" border>
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="poolType" label="池类型" width="120">
                <template #default="{ row }">
                  <el-tag :type="getPoolTypeTag(row.poolType)">
                    {{ row.poolType }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="分层比例" width="200">
                <template #default="{ row }">
                  <div class="ratio-info">
                    <span>上: {{ (row.topWeightLayerRatio * 100).toFixed(0) }}%</span>
                    <span>中: {{ (row.midWeightLayerRatio * 100).toFixed(0) }}%</span>
                    <span>下: {{ (row.bottomWeightLayerRatio * 100).toFixed(0) }}%</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="抽取比例" width="200">
                <template #default="{ row }">
                  <div class="ratio-info">
                    <span>上: {{ (row.topWeightExtractRatio * 100).toFixed(0) }}%</span>
                    <span>中: {{ (row.midWeightExtractRatio * 100).toFixed(0) }}%</span>
                    <span>下: {{ (row.bottomWeightExtractRatio * 100).toFixed(0) }}%</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="权重阈值" width="180">
                <template #default="{ row }">
                  <div class="threshold-info">
                    <span>上: {{ row.topWeightMinThreshold }}</span>
                    <span>中: {{ row.midWeightMinThreshold }}</span>
                    <span>下: {{ row.bottomWeightMinThreshold }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="状态" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.enable ? 'success' : 'danger'" size="small">
                    {{ row.enable ? '启用' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="handleEditPool(row)">编辑</el-button>
                  <el-button 
                    v-if="row.id > 4" 
                    type="danger" 
                    link 
                    @click="handleDeletePool(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <el-dialog 
      v-model="editDialogVisible" 
      :title="isAddMode ? '新增池配置' : '编辑池配置'" 
      width="800px"
      :close-on-click-modal="false"
    >
      <el-alert
        title="警告"
        type="warning"
        description="您当前的修改可能会影响系统视频算法，会对视频规则造成不可逆影响是否执行当前修改？？"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      />
      
      <el-form :model="editForm" label-width="140px" v-if="editForm">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="池类型" prop="poolType">
              <el-input 
                v-model="editForm.poolType" 
                :disabled="!isAddMode"
                placeholder="请输入池类型"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="视频分类" prop="category">
              <el-input 
                v-model="editForm.category" 
                placeholder="* 代表全部"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-divider content-position="left">分层比例配置（总和必须等于1）</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="上层比例">
              <el-tooltip 
                content="高质量视频在流量池中划分的比例" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.topWeightLayerRatio" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="4"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中层比例">
              <el-tooltip 
                content="中等质量视频划分比例" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.midWeightLayerRatio" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="4"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下层比例">
              <el-tooltip 
                content="低权重/新视频划分比例" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.bottomWeightLayerRatio" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="4"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="validation-info">
          <span class="validation-label">分层占比总和：</span>
          <el-tag :type="getLayerRatioSum() === 1 ? 'success' : 'danger'">
            {{ getLayerRatioSum().toFixed(4) }}
          </el-tag>
          <span v-if="getLayerRatioSum() !== 1" class="warning-text">（必须等于1）</span>
        </div>
        
        <el-divider content-position="left">抽取比例配置（总和必须等于1）</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="上层抽取">
              <el-tooltip 
                content="推荐时优先从上层抽取的视频比例" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.topWeightExtractRatio" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="4"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中层抽取">
              <el-tooltip 
                content="从中层抽取的比例" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.midWeightExtractRatio" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="4"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下层抽取">
              <el-tooltip 
                content="从下层抽取的比例" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.bottomWeightExtractRatio" 
                  :min="0" 
                  :max="1" 
                  :step="0.1" 
                  :precision="4"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="validation-info">
          <span class="validation-label">抽取占比总和：</span>
          <el-tag :type="getExtractRatioSum() === 1 ? 'success' : 'danger'">
            {{ getExtractRatioSum().toFixed(4) }}
          </el-tag>
          <span v-if="getExtractRatioSum() !== 1" class="warning-text">（必须等于1）</span>
        </div>
        
        <el-divider content-position="left">权重阈值配置（上层 ≥ 中层 ≥ 下层）</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="上层阈值">
              <el-tooltip 
                content="进入上层池的最低分数（0~100）" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.topWeightMinThreshold" 
                  :min="0"
                  :max="100"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中层阈值">
              <el-tooltip 
                content="进入中层池的最低分数（0~100）" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.midWeightMinThreshold" 
                  :min="0"
                  :max="100"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="下层阈值">
              <el-tooltip 
                content="进入下层池的最低分数（0~100）" 
                placement="top"
              >
                <el-input-number 
                  v-model="editForm.bottomWeightMinThreshold" 
                  :min="0"
                  :max="100"
                  style="width: 100%"
                />
              </el-tooltip>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="validation-info">
          <span class="validation-label">阈值验证：</span>
          <el-tag :type="validateThresholds() ? 'success' : 'danger'">
            {{ validateThresholds() ? '通过' : '不通过' }}
          </el-tag>
          <span v-if="!validateThresholds()" class="warning-text">（上层 ≥ 中层 ≥ 下层）</span>
        </div>
        
        <el-form-item label="启用状态">
          <el-switch v-model="editForm.enable" />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input 
            v-model="editForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入配置说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSavePool" 
          :loading="saving"
          :disabled="poolCountdown > 0"
        >
          {{ poolCountdown > 0 ? `确定 (${poolCountdown}s)` : '确定' }}
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog 
      v-model="scoreEditDialogVisible" 
      title="修改评分配置" 
      width="700px"
      :close-on-click-modal="false"
    >
      <el-alert
        title="警告"
        type="warning"
        description="您当前的修改可能会影响系统视频算法，会对视频规则造成不可逆影响是否执行当前修改？？"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      />
      
      <el-form :model="scoreEditForm" :rules="scoreEditRules" ref="scoreEditFormRef" label-width="140px">
        <el-divider content-position="left">权重配置（总和必须等于100%）</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="完播率权重" prop="finishRateWeight">
              <el-input-number 
                v-model="scoreEditForm.finishRateWeight" 
                :min="0" 
                :max="100" 
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="点赞率权重" prop="likeRateWeight">
              <el-input-number 
                v-model="scoreEditForm.likeRateWeight" 
                :min="0" 
                :max="100" 
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评论率权重" prop="commentRateWeight">
              <el-input-number 
                v-model="scoreEditForm.commentRateWeight" 
                :min="0" 
                :max="100" 
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收藏率权重" prop="collectRateWeight">
              <el-input-number 
                v-model="scoreEditForm.collectRateWeight" 
                :min="0" 
                :max="100" 
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分享率权重" prop="shareRateWeight">
              <el-input-number 
                v-model="scoreEditForm.shareRateWeight" 
                :min="0" 
                :max="100" 
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时间得分权重" prop="timeScoreWeight">
              <el-input-number 
                v-model="scoreEditForm.timeScoreWeight" 
                :min="0" 
                :max="100" 
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <div class="weight-total-edit">
          <span class="total-label">权重总和：</span>
          <el-tag 
            :type="getEditTotalWeight() === 100 ? 'success' : 'danger'" 
            size="large"
          >
            {{ getEditTotalWeight() }}%
          </el-tag>
          <span v-if="getEditTotalWeight() !== 100" class="warning-text">
            权重总和必须等于100%
          </span>
        </div>
        
        <el-divider content-position="left">其他配置</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评分窗口" prop="scoreWindowHours">
              <el-input-number 
                v-model="scoreEditForm.scoreWindowHours" 
                :min="1"
                style="width: 100%"
              />
              <span class="form-hint">小时</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启用状态" prop="enable">
              <el-switch v-model="scoreEditForm.enable" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="scoreEditForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入配置说明"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="scoreEditDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSaveScore" 
          :loading="savingScore"
          :disabled="countdown > 0"
        >
          {{ countdown > 0 ? `确定 (${countdown}s)` : '确定' }}
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog 
      v-model="deleteDialogVisible" 
      title="删除池配置" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-alert
        title="危险操作警告"
        type="error"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;"
      >
        <template #default>
          <div style="line-height: 1.6;">
            <p style="margin: 0 0 8px 0; font-weight: 600;">此操作不可逆！</p>
            <p style="margin: 0;">您即将删除池配置：<strong>{{ deleteTarget?.poolType }}</strong></p>
            <p style="margin: 8px 0 0 0; color: #f56c6c;">删除后将无法恢复，请谨慎操作！</p>
          </div>
        </template>
      </el-alert>
      
      <div style="text-align: center; padding: 20px 0;">
        <p style="font-size: 14px; color: #666; margin-bottom: 16px;">
          确定要删除此池配置吗？
        </p>
        <el-button 
          type="danger" 
          size="large"
          @click="confirmDelete"
          :loading="deleting"
          :disabled="deleteCountdown > 0"
        >
          {{ deleteCountdown > 0 ? `确认删除 (${deleteCountdown}s)` : '确认删除' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { InfoFilled, WarningFilled } from '@element-plus/icons-vue'
import { GetSysScoreConfig, UpdateVideoSortWeight, UpdatePoolConfig, DeletePool, AddPool } from '@/admin/api/admin'

const activeTab = ref('score')
const loading = ref(false)
const saving = ref(false)
const scoreConfig = ref(null)
const poolConfigs = ref([])
const editDialogVisible = ref(false)
const editForm = ref(null)
const isAddMode = ref(false)
const scoreEditDialogVisible = ref(false)
const scoreEditFormRef = ref(null)
const savingScore = ref(false)
const countdown = ref(0)
const poolCountdown = ref(0)
const deleteDialogVisible = ref(false)
const deleteTarget = ref(null)
const deleting = ref(false)
const deleteCountdown = ref(0)
let countdownTimer = null
let poolCountdownTimer = null
let deleteCountdownTimer = null

const scoreEditForm = ref({
  id: null,
  finishRateWeight: 0,
  likeRateWeight: 0,
  commentRateWeight: 0,
  collectRateWeight: 0,
  shareRateWeight: 0,
  timeScoreWeight: 0,
  scoreWindowHours: 168,
  enable: 1,
  remark: ''
})

const scoreEditRules = {
  finishRateWeight: [
    { required: true, message: '请输入完播率权重', trigger: 'blur' }
  ],
  likeRateWeight: [
    { required: true, message: '请输入点赞率权重', trigger: 'blur' }
  ],
  commentRateWeight: [
    { required: true, message: '请输入评论率权重', trigger: 'blur' }
  ],
  collectRateWeight: [
    { required: true, message: '请输入收藏率权重', trigger: 'blur' }
  ],
  shareRateWeight: [
    { required: true, message: '请输入分享率权重', trigger: 'blur' }
  ],
  timeScoreWeight: [
    { required: true, message: '请输入时间得分权重', trigger: 'blur' }
  ],
  scoreWindowHours: [
    { required: true, message: '请输入评分窗口', trigger: 'blur' }
  ]
}

const fetchConfig = async () => {
  loading.value = true
  try {
    const res = await GetSysScoreConfig()
    if (res.code === 0 && res.data) {
      const configData = res.data.data || res.data
      scoreConfig.value = configData.sysScoreConfig || configData
      poolConfigs.value = configData.sysConfig || []
    }
  } catch (error) {
    console.error('获取系统配置失败:', error)
    ElMessage.error('获取系统配置失败')
  } finally {
    loading.value = false
  }
}

const getPoolTypeTag = (type) => {
  const tagMap = {
    '爆款池': 'danger',
    '长尾池': 'warning',
    '冷启动池': 'primary',
    '冷池': 'info'
  }
  return tagMap[type] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getTotalWeight = () => {
  if (!scoreConfig.value) return 0
  const total = 
    (scoreConfig.value.finishRateWeight || 0) +
    (scoreConfig.value.likeRateWeight || 0) +
    (scoreConfig.value.commentRateWeight || 0) +
    (scoreConfig.value.collectRateWeight || 0) +
    (scoreConfig.value.shareRateWeight || 0) +
    (scoreConfig.value.timeScoreWeight || 0)
  return total.toFixed(2)
}

const getEditTotalWeight = () => {
  const total = 
    (scoreEditForm.value.finishRateWeight || 0) +
    (scoreEditForm.value.likeRateWeight || 0) +
    (scoreEditForm.value.commentRateWeight || 0) +
    (scoreEditForm.value.collectRateWeight || 0) +
    (scoreEditForm.value.shareRateWeight || 0) +
    (scoreEditForm.value.timeScoreWeight || 0)
  return total.toFixed(2)
}

const handleEditScore = () => {
  if (!scoreConfig.value) return
  
  scoreEditForm.value = {
    id: scoreConfig.value.id,
    finishRateWeight: scoreConfig.value.finishRateWeight || 0,
    likeRateWeight: scoreConfig.value.likeRateWeight || 0,
    commentRateWeight: scoreConfig.value.commentRateWeight || 0,
    collectRateWeight: scoreConfig.value.collectRateWeight || 0,
    shareRateWeight: scoreConfig.value.shareRateWeight || 0,
    timeScoreWeight: scoreConfig.value.timeScoreWeight || 0,
    scoreWindowHours: scoreConfig.value.scoreWindowHours || 168,
    enable: scoreConfig.value.enable || 1,
    remark: scoreConfig.value.remark || ''
  }
  
  countdown.value = 3
  scoreEditDialogVisible.value = true
  
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
  }, 1000)
}

const handleSaveScore = async () => {
  try {
    await scoreEditFormRef.value.validate()
    
    const total = parseFloat(getEditTotalWeight())
    if (total !== 100) {
      ElMessage.error('权重总和必须等于100%')
      return
    }
    
    savingScore.value = true
    
    const res = await UpdateVideoSortWeight(scoreEditForm.value)
    if (res.code === 0) {
      ElMessage.success('修改成功')
      scoreEditDialogVisible.value = false
      fetchConfig()
    }
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    savingScore.value = false
  }
}

const handleEditPool = (row) => {
  isAddMode.value = false
  editForm.value = { ...row }
  poolCountdown.value = 3
  editDialogVisible.value = true
  
  poolCountdownTimer = setInterval(() => {
    poolCountdown.value--
    if (poolCountdown.value <= 0) {
      clearInterval(poolCountdownTimer)
      poolCountdownTimer = null
    }
  }, 1000)
}

const handleAddPool = () => {
  isAddMode.value = true
  editForm.value = {
    poolType: '',
    category: '*',
    topWeightLayerRatio: 0.3,
    midWeightLayerRatio: 0.5,
    bottomWeightLayerRatio: 0.2,
    topWeightExtractRatio: 0.5,
    midWeightExtractRatio: 0.3,
    bottomWeightExtractRatio: 0.2,
    topWeightMinThreshold: 80,
    midWeightMinThreshold: 60,
    bottomWeightMinThreshold: 30,
    enable: true,
    remark: ''
  }
  poolCountdown.value = 3
  editDialogVisible.value = true
  
  poolCountdownTimer = setInterval(() => {
    poolCountdown.value--
    if (poolCountdown.value <= 0) {
      clearInterval(poolCountdownTimer)
      poolCountdownTimer = null
    }
  }, 1000)
}

const getLayerRatioSum = () => {
  if (!editForm.value) return 0
  return (
    (editForm.value.topWeightLayerRatio || 0) +
    (editForm.value.midWeightLayerRatio || 0) +
    (editForm.value.bottomWeightLayerRatio || 0)
  )
}

const getExtractRatioSum = () => {
  if (!editForm.value) return 0
  return (
    (editForm.value.topWeightExtractRatio || 0) +
    (editForm.value.midWeightExtractRatio || 0) +
    (editForm.value.bottomWeightExtractRatio || 0)
  )
}

const validateThresholds = () => {
  if (!editForm.value) return false
  const top = editForm.value.topWeightMinThreshold || 0
  const mid = editForm.value.midWeightMinThreshold || 0
  const bottom = editForm.value.bottomWeightMinThreshold || 0
  return top >= mid && mid >= bottom
}

const handleSavePool = async () => {
  try {
    const layerSum = getLayerRatioSum()
    if (Math.abs(layerSum - 1) > 0.0001) {
      ElMessage.error('分层占比总和必须等于1')
      return
    }
    
    const extractSum = getExtractRatioSum()
    if (Math.abs(extractSum - 1) > 0.0001) {
      ElMessage.error('抽取占比总和必须等于1')
      return
    }
    
    if (!validateThresholds()) {
      ElMessage.error('权重阈值必须满足：上层 ≥ 中层 ≥ 下层')
      return
    }
    
    saving.value = true
    
    let res
    if (isAddMode.value) {
      res = await AddPool(editForm.value)
    } else {
      res = await UpdatePoolConfig(editForm.value)
    }
    
    if (res.code === 0) {
      ElMessage.success(isAddMode.value ? '新增成功' : '修改成功')
      editDialogVisible.value = false
      fetchConfig()
    }
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

const handleDeletePool = (row) => {
  deleteTarget.value = row
  deleteCountdown.value = 3
  deleteDialogVisible.value = true
  
  deleteCountdownTimer = setInterval(() => {
    deleteCountdown.value--
    if (deleteCountdown.value <= 0) {
      clearInterval(deleteCountdownTimer)
      deleteCountdownTimer = null
    }
  }, 1000)
}

const confirmDelete = async () => {
  try {
    deleting.value = true
    
    const res = await DeletePool(deleteTarget.value.id)
    if (res.code === 0) {
      ElMessage.success('删除成功')
      deleteDialogVisible.value = false
      fetchConfig()
    }
  } catch (error) {
    console.error('删除失败:', error)
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  fetchConfig()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  if (poolCountdownTimer) {
    clearInterval(poolCountdownTimer)
    poolCountdownTimer = null
  }
  if (deleteCountdownTimer) {
    clearInterval(deleteCountdownTimer)
    deleteCountdownTimer = null
  }
})
</script>

<style scoped>
.system-settings {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e1e2d;
  margin: 0;
}

.settings-container {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.settings-section {
  padding: 20px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e1e2d;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.section-header .section-title {
  margin: 0;
  padding: 0;
  border: none;
}

.weight-total-edit {
  margin: 20px 0;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-hint {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}

.validation-info {
  margin: 16px 0;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.validation-label {
  font-size: 14px;
  font-weight: 500;
  color: #1e1e2d;
}

.score-config-card {
  margin-top: 20px;
}

.ratio-info,
.threshold-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
}

.ratio-info span,
.threshold-info span {
  color: #666;
}

.el-divider__text {
  font-weight: 500;
  color: #1e1e2d;
}

.weight-config {
  margin-top: 20px;
}

.weight-item {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: help;
  transition: all 0.3s;
}

.weight-item:hover {
  background: #e6f7ff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.weight-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.weight-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e1e2d;
}

.weight-total {
  margin-top: 30px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.total-label {
  font-size: 16px;
  font-weight: 600;
  color: #1e1e2d;
}

.warning-text {
  color: #f56c6c;
  font-size: 14px;
  margin-left: 12px;
}

.formula-section {
  margin-top: 20px;
}

.formula-group {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.formula-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e1e2d;
  margin: 0 0 16px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #3699ff;
}

.formula-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #fff;
  border-radius: 6px;
  transition: all 0.3s;
}

.formula-item:hover {
  background: #e6f7ff;
  transform: translateX(4px);
}

.formula-item.highlight {
  background: #fff7e6;
  border-left: 4px solid #faad14;
  font-weight: 600;
}

.formula-label {
  min-width: 140px;
  color: #666;
  font-size: 14px;
}

.formula-content {
  color: #1e1e2d;
  font-size: 14px;
  font-family: 'Courier New', monospace;
}

.pool-rules {
  margin-top: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
}

.rules-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e1e2d;
  margin: 0 0 12px 0;
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 6px;
  transition: all 0.3s;
}

.rule-item:hover {
  background: #e6f7ff;
}

.rule-item:last-child {
  margin-bottom: 0;
}

.rule-arrow {
  font-size: 18px;
  color: #999;
  font-weight: bold;
}

.rule-result {
  font-size: 14px;
  font-weight: 600;
  color: #1e1e2d;
}
</style>
