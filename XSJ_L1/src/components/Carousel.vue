<template>
  <div class="carousel-container">
    <el-carousel :interval="5000" arrow="always" indicator-position="outside">
      <el-carousel-item v-for="(item, index) in items" :key="index">
        <div class="carousel-item" @click="handleClick(item)">
          <img :src="item.image" :alt="item.title || item.fileName || '轮播图'" />
          <div class="carousel-content" v-if="item.title || item.description">
            <h3 class="carousel-title" v-if="item.title">{{ item.title }}</h3>
            <p class="carousel-desc" v-if="item.description">{{ item.description }}</p>
            <div class="carousel-meta" v-if="item.views || item.author">
              <span class="meta-item" v-if="item.views">
                <el-icon><View /></el-icon>
                {{ formatNumber(item.views) }}{{ t('home.views') }}
              </span>
              <span class="meta-item" v-if="item.author">
                <el-icon><User /></el-icon>
                {{ item.author }}
              </span>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { View, User } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const props = defineProps({
  items: {
    type: Array,
    required: true,
    default: () => []
  }
})

const emit = defineEmits(['click'])

const handleClick = (item) => {
  emit('click', item)
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}
</script>

<style scoped>
.carousel-container {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.carousel-item {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
  overflow: hidden;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.carousel-item:hover img {
  transform: scale(1.05);
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.4) 60%, transparent 100%);
  padding: 40px 30px 30px;
  color: white;
  transition: all 0.3s ease;
}

.carousel-item:hover .carousel-content {
  background: linear-gradient(to top, rgba(0, 0, 0, 0.9) 0%, rgba(0, 0, 0, 0.6) 70%, rgba(0, 0, 0, 0.3) 100%);
}

.carousel-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 12px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.carousel-desc {
  font-size: 16px;
  line-height: 1.6;
  margin: 0 0 16px;
  opacity: 0.95;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.carousel-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

:deep(.el-carousel__indicators--outside) {
  bottom: 20px;
}

:deep(.el-carousel__indicator--outside .el-carousel__indicator) {
  background: rgba(255, 255, 255, 0.5);
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

:deep(.el-carousel__indicator--outside .el-carousel__indicator.is-active) {
  background: #667eea;
  width: 12px;
  height: 12px;
}

:deep(.el-carousel__arrow) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  width: 48px;
  height: 48px;
  border-radius: 50%;
}

:deep(.el-carousel__arrow:hover) {
  background: rgba(255, 255, 255, 0.95);
}
</style>
