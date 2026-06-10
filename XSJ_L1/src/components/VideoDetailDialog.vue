<template>
  <el-dialog
    v-model="visible"
    :title="video?.title || t('video.title')"
    width="800px"
    :before-close="handleClose"
    @close="handleClose"
    class="video-detail-dialog"
  >
    <div class="video-detail-content">
      <div class="video-player">
        <div class="video-container">
          <img :src="video?.thumbnail" :alt="video?.title" class="video-poster" />
          <div class="play-overlay">
            <el-icon class="play-icon-large"><el-icon-video-play /></el-icon>
          </div>
        </div>
      </div>
      
      <div class="video-info-detail">
        <h3 class="detail-title">{{ video?.title }}</h3>
        <p class="detail-description">{{ video?.description || t('video.noDescription') }}</p>
        
        <div class="detail-meta">
          <div class="meta-row">
            <el-icon class="meta-icon"><el-icon-user /></el-icon>
            <span class="meta-label">{{ t('home.author') }}:</span>
            <span class="meta-value">{{ video?.author }}</span>
          </div>
          <div class="meta-row">
            <el-icon class="meta-icon"><el-icon-view /></el-icon>
            <span class="meta-label">{{ t('home.views') }}:</span>
            <span class="meta-value">{{ formatNumber(video?.views || 0) }}</span>
          </div>
          <div class="meta-row">
            <el-icon class="meta-icon"><el-icon-chat-dot-round /></el-icon>
            <span class="meta-label">{{ t('home.comments') }}:</span>
            <span class="meta-value">{{ formatNumber(video?.comments || 0) }}</span>
          </div>
          <div class="meta-row">
            <el-icon class="meta-icon"><el-icon-clock /></el-icon>
            <span class="meta-label">{{ t('home.duration') }}:</span>
            <span class="meta-value">{{ video?.duration }}</span>
          </div>
          <div class="meta-row">
            <el-icon class="meta-icon"><el-icon-calendar /></el-icon>
            <span class="meta-label">{{ t('video.publishTime') }}:</span>
            <span class="meta-value">{{ video?.publishTime || '2024-01-01' }}</span>
          </div>
        </div>
        
        <div class="detail-tags" v-if="video?.tags && video.tags.length">
          <span class="detail-tag" v-for="tag in video.tags" :key="tag">{{ tag }}</span>
        </div>
      </div>
      
      <div class="detail-actions">
        <el-button type="primary" size="large" @click="handlePlay">
          <el-icon><el-icon-video-play /></el-icon>
          {{ t('video.playNow') }}
        </el-button>
        <el-button size="large" @click="handleClose">
          {{ t('video.close') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  video: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'play'])

const handleClose = () => {
  emit('close')
}

const handlePlay = () => {
  emit('play', props.video)
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}
</script>

<style scoped>
.video-detail-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.video-detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.video-player {
  width: 100%;
  max-height: 400px;
}

.video-container {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 12px;
  overflow: hidden;
  background: #000;
}

.video-poster {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.video-container:hover .play-overlay {
  opacity: 1;
}

.play-icon-large {
  font-size: 64px;
  color: white;
}

.video-info-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
  line-height: 1.4;
}

.detail-description {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin: 0;
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.meta-value {
  font-size: 15px;
  color: #333;
  font-weight: 600;
}

.detail-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.detail-tag {
  padding: 6px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  color: #667eea;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.detail-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}
</style>
