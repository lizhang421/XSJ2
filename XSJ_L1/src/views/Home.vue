<template>
  <div class="home-container">
    <!-- ========== 导航栏（原生锚点跳转） ========== -->
    <header class="navbar">
      <div class="container">
        <div class="navbar-brand">
          <h1 class="brand-logo">XSJ</h1>
          <div class="brand-name">
            <h2>{{ t('brand.name') }}</h2>
            <p>{{ t('brand.englishName') }}</p>
          </div>
        </div>
        <nav class="navbar-menu">
          <!-- 原生锚点：href="#锚点ID" -->
          <a href="#home" class="menu-item">{{ t('nav.home') }}</a>
          <a href="#features" class="menu-item">{{ t('nav.features') }}</a>
          <a href="#advantages" class="menu-item">{{ t('nav.advantages') }}</a>
          <a href="#cases" class="menu-item">{{ t('nav.cases') }}</a>
          <a href="#faq" class="menu-item">{{ t('nav.faq') }}</a>
          <a href="#contact" class="menu-item">{{ t('nav.contact') }}</a>
        </nav>
        <div class="navbar-actions">
          <div class="lang-switch">
            <button class="lang-btn" @click="locale = 'zh'" :class="{ active: locale === 'zh' }">中文</button>
            <button class="lang-btn" @click="locale = 'en'" :class="{ active: locale === 'en' }">EN</button>
            <button class="lang-btn" @click="locale = 'ja'" :class="{ active: locale === 'ja' }">日本語</button>
          </div>
          <button class="join-btn" @click="TurnLogin()">
            <i class="el-icon-user-add" style="margin-right: 8px;"></i>
            {{ t('nav.joinUs') }}
          </button>
        </div>
      </div>
    </header>

<!-- ========== 轮播图（仅图片） ========== -->
<section id="home" class="carousel-section" @mouseenter="stopCarousel" @mouseleave="startCarousel">
  <div class="carousel-container" :style="{ height: '80vh' }">
    <div 
      class="carousel-slide" 
      v-for="(item, index) in imageResources.carousel" 
      :key="index" 
      :class="{ active: carouselActiveIndex === index }"
    >
      <!-- 只保留图片，永远不出现视频 -->
      <div 
        class="carousel-bg"
        :style="{ backgroundImage: `url(${item.src})` }"
      ></div>

      <div class="carousel-mask"></div>
      <div class="container carousel-content">
        <h1 class="carousel-title">{{ t(`carousel.item${index+1}.title`) }}</h1>
        <p class="carousel-desc">{{ t(`carousel.item${index+1}.desc`) }}</p>
        <div class="carousel-buttons">
          <button class="carousel-btn" onclick="location.href='#features'">
            {{ t(`carousel.item${index+1}.btnText`) }}
            <i class="el-icon-arrow-right" style="margin-left: 8px;"></i>
          </button>
          <button class="carousel-join-btn" onclick="location.href='#contact'">
            <i class="el-icon-user-add" style="margin-right: 8px;"></i>
            {{ t('carousel.joinBtn') }}
          </button>
        </div>
      </div>
    </div>

    <div class="carousel-indicators">
      <button 
        class="indicator-btn" 
        v-for="(item, index) in imageResources.carousel" 
        :key="index" 
        :class="{ active: carouselActiveIndex === index }"
        @click="goToSlide(index)"
      ></button>
    </div>
  </div>
</section>
    <!-- ========== 核心功能 ========== -->
    <section id="features" class="features">
      <div class="container">
        <div class="section-title">
          <h2>{{ t('features.title') }}</h2>
          <p>{{ t('features.subtitle') }}</p>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-lightbulb icon-animate" style="font-size: 32px;"></i>
            </div>
            <h3>{{ t('features.item1.title') }}</h3>
            <p>{{ t('features.item1.desc') }}</p>
            <button class="feature-more-btn" onclick="location.href='#features'">
              {{ t('features.item1.more') }} <i class="el-icon-arrow-right"></i>
            </button>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-video-play icon-animate" style="font-size: 32px;"></i>
            </div>
            <h3>{{ t('features.item2.title') }}</h3>
            <p>{{ t('features.item2.desc') }}</p>
            <button class="feature-more-btn" onclick="location.href='#features'">
              {{ t('features.item2.more') }} <i class="el-icon-arrow-right"></i>
            </button>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-user-filled icon-animate" style="font-size: 32px;"></i>
            </div>
            <h3>{{ t('features.item3.title') }}</h3>
            <p>{{ t('features.item3.desc') }}</p>
            <button class="feature-more-btn" onclick="location.href='#features'">
              {{ t('features.item3.more') }} <i class="el-icon-arrow-right"></i>
            </button>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="el-icon-data-analysis icon-animate" style="font-size: 32px;"></i>
            </div>
            <h3>{{ t('features.item4.title') }}</h3>
            <p>{{ t('features.item4.desc') }}</p>
            <button class="feature-more-btn" onclick="location.href='#features'">
              {{ t('features.item4.more') }} <i class="el-icon-arrow-right"></i>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 平台优势 ========== -->
    <section id="advantages" class="advantages">
      <div class="container">
        <div class="section-title">
          <h2>{{ t('advantages.title') }}</h2>
          <p>{{ t('advantages.subtitle') }}</p>
        </div>
        <div class="advantages-grid">
          <div class="advantage-card" v-for="(img, index) in imageResources.advantages" :key="index">
            <div class="advantage-image-wrap">
              <img :src="img" class="advantage-image" alt="平台优势" />
              <div class="advantage-icon">
                <i v-if="index===0" class="el-icon-trend-charts" style="font-size: 40px;"></i>
                <i v-if="index===1" class="el-icon-message" style="font-size: 40px;"></i>
                <i v-if="index===2" class="el-icon-line-chart" style="font-size: 40px;"></i>
              </div>
              <div class="advantage-hover-effect"></div>
            </div>
            <div class="advantage-content">
              <h3>{{ t(`advantages.item${index+1}.title`) }}</h3>
              <p>{{ t(`advantages.item${index+1}.desc`) }}</p>
              <button class="advantage-more-btn" onclick="location.href='#advantages'">
                {{ t(`advantages.item${index+1}.more`) }}
                <i class="el-icon-arrow-right"></i>
              </button>
            </div>
          </div>
        </div>
        <div class="advantages-cta">
          <button class="advantages-join-btn" onclick="location.href='#contact'">
            <i class="el-icon-user-add" style="margin-right: 8px;"></i>
            {{ t('advantages.joinBtn') }}
          </button>
        </div>
      </div>
    </section>

    <!-- ========== AI能力展示 ========== -->
    <section id="cases" class="ai-showcase">
      <div class="container ai-content">
        <div class="ai-image">
          <img :src="imageResources.aiShowcase" alt="XSJ AI教学能力展示" class="ai-img" />
        </div>
        <div class="ai-text">
          <h2>{{ t('aiShowcase.title') }}</h2>
          <ul class="ai-features-list">
            <li class="ai-feature-item" v-for="i in 5" :key="i">
              <i class="el-icon-check check-icon"></i>
              <span>{{ t(`aiShowcase.item${i}`) }}</span>
            </li>
          </ul>
          <button class="ai-btn" onclick="location.href='#cases'">
            {{ t('aiShowcase.btnText') }}
            <i class="el-icon-arrow-right" style="margin-left: 8px;"></i>
          </button>
        </div>
      </div>
    </section>

    <!-- ========== 用户评价 ========== -->
    <section class="testimonials">
      <div class="container">
        <div class="section-title">
          <h2>{{ t('testimonials.title') }}</h2>
          <p>{{ t('testimonials.subtitle') }}</p>
        </div>
        <div class="testimonial-carousel">
          <div class="testimonial-slide" v-for="i in 3" :key="i">
            <div class="testimonial-card">
              <div class="rating">
                <div class="stars">
                  <i class="el-icon-star star filled" v-for="star in (i===3 ? 4 : 5)" :key="star"></i>
                  <i class="el-icon-star-off star empty" v-if="i===3"></i>
                </div>
              </div>
              <p class="testimonial-content">"{{ t(`testimonials.item${i}.content`) }}"</p>
              <div class="testimonial-author">
                <img :src="imageResources.avatars[i-1]" alt="用户头像" class="avatar" />
                <div class="author-info">
                  <h4>{{ t(`testimonials.item${i}.name`) }}</h4>
                  <p>{{ t(`testimonials.item${i}.role`) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== 合作伙伴 ========== -->
    <section class="partners">
      <div class="container">
        <div class="section-title">
          <h2>{{ t('partners.title') }}</h2>
          <p>{{ t('partners.subtitle') }}</p>
        </div>
        <div class="partners-grid">
          <div class="partner-item" v-for="(logo, index) in imageResources.partners" :key="index">
            <img :src="logo" alt="合作伙伴logo" class="partner-logo" />
          </div>
        </div>
        <div class="partners-cta">
          <button class="partner-more-btn" onclick="location.href='#cases'">
            {{ t('partners.more') }} <i class="el-icon-arrow-right"></i>
          </button>
        </div>
      </div>
    </section>

    <!-- ========== FAQ ========== -->
    <section id="faq" class="faq">
      <div class="container">
        <div class="section-title">
          <h2>{{ t('faq.title') }}</h2>
          <p>{{ t('faq.subtitle') }}</p>
        </div>
        <div class="faq-list">
          <div class="faq-item" v-for="i in 5" :key="i">
            <div class="faq-question" @click="toggleFaq(i)">
              {{ t(`faq.item${i}.question`) }}
              <i class="el-icon-arrow-right faq-arrow" :class="{ rotated: faqOpen === i }"></i>
            </div>
            <div class="faq-answer" v-show="faqOpen === i">
              {{ t(`faq.item${i}.answer`) }}
            </div>
          </div>
        </div>
        <div class="faq-more">
          <button class="faq-more-btn" onclick="location.href='#faq'">
            {{ t('faq.moreBtn') }}
            <i class="el-icon-arrow-right" style="margin-left: 8px;"></i>
          </button>
        </div>
      </div>
    </section>

    <!-- ========== 联系我们 ========== -->
    <section id="contact" class="contact">
      <div class="container">
        <div class="section-title">
          <h2>{{ t('contact.title') }}</h2>
          <p>{{ t('contact.subtitle') }}</p>
        </div>
        <div class="contact-content">
          <div class="contact-image">
            <img :src="imageResources.contact" alt="联系我们" class="contact-img" />
          </div>
          <div class="contact-info">
            <div class="contact-item">
              <i class="el-icon-phone contact-icon" style="font-size: 24px;"></i>
              <div class="contact-text">
                <h4>{{ t('contact.phone') }}</h4>
                <p>{{ currentContactInfo.phone }}</p>
              </div>
            </div>
            <div class="contact-item">
              <i class="el-icon-message contact-icon" style="font-size: 24px;"></i>
              <div class="contact-text">
                <h4>{{ t('contact.email') }}</h4>
                <p>{{ currentContactInfo.email }}</p>
              </div>
            </div>
            <div class="contact-item">
              <i class="el-icon-location contact-icon" style="font-size: 24px;"></i>
              <div class="contact-text">
                <h4>{{ t('contact.address') }}</h4>
                <p>{{ currentContactInfo.address }}</p>
              </div>
            </div>
            <div class="contact-item">
              <i class="el-icon-clock contact-icon" style="font-size: 24px;"></i>
              <div class="contact-text">
                <h4>{{ t('contact.workingHours') }}</h4>
                <p>{{ currentContactInfo.workingHours }}</p>
              </div>
            </div>
            <button class="contact-btn" onclick="location.href='#contact'">
              {{ t('contact.btnText') }}
              <i class="el-icon-chat-dot-round" style="margin-left: 8px;"></i>
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- ========== CTA ========== -->
    <section class="cta">
      <div class="container cta-content">
        <h2>{{ t('cta.title') }}</h2>
        <p>{{ t('cta.subtitle') }}</p>
        <button class="cta-btn" onclick="location.href='#contact'">
          <i class="el-icon-user-add" style="margin-right: 8px;"></i>
          {{ t('cta.btnText') }}
        </button>
      </div>
    </section>

    <!-- ========== 页脚 ========== -->
    <footer class="footer">
      <div class="container">
        <div class="footer-top">
          <div class="footer-brand">
            <h3 class="footer-logo">XSJ</h3>
            <h4>{{ t('brand.name') }}</h4>
            <p>{{ t('brand.englishName') }}</p>
            <div class="footer-social">
              <i class="el-icon-wechat social-icon"></i>
              <i class="el-icon-weibo social-icon"></i>
              <i class="el-icon-share social-icon"></i>
              <i class="el-icon-mail social-icon"></i>
            </div>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>{{ t('footer.platformAdvantages') }}</h4>
              <ul>
                <li><a href="#features" onclick="location.href='#features'">{{ t('features.item1.title') }}</a></li>
                <li><a href="#features" onclick="location.href='#features'">{{ t('features.item2.title') }}</a></li>
                <li><a href="#features" onclick="location.href='#features'">{{ t('features.item3.title') }}</a></li>
                <li><a href="#features" onclick="location.href='#features'">{{ t('features.item4.title') }}</a></li>
              </ul>
            </div>
            <div class="link-group">
              <h4>{{ t('footer.aboutUs') }}</h4>
              <ul>
                <li><a href="#home" onclick="location.href='#home'">{{ t('nav.home') }}</a></li>
                <li><a href="#home" onclick="location.href='#home'">{{ t('footer.aboutUs') }}</a></li>
                <li><a href="#contact" onclick="location.href='#contact'">{{ t('nav.contact') }}</a></li>
                <li><a href="#contact" onclick="location.href='#contact'">{{ t('nav.joinUs') }}</a></li>
              </ul>
            </div>
            <div class="link-group">
              <h4>{{ t('footer.helpCenter') }}</h4>
              <ul>
                <li><a href="#faq" onclick="location.href='#faq'">{{ t('subscribe.title') }}</a></li>
                <li><a href="#faq" onclick="location.href='#faq'">{{ t('nav.faq') }}</a></li>
                <li><a href="#contact" onclick="location.href='#contact'">{{ t('subscribe.desc') }}</a></li>
                <li><a href="#contact" onclick="location.href='#contact'">{{ t('footer.privacy') }}</a></li>
              </ul>
            </div>
            <div class="link-group">
              <h4>{{ t('footer.businessCooperation') }}</h4>
              <ul>
                <li><a href="#cases" onclick="location.href='#cases'">{{ t('partners.more') }}</a></li>
                <li><a href="#cases" onclick="location.href='#cases'">{{ t('nav.cases') }}</a></li>
                <li><a href="#contact" onclick="location.href='#contact'">{{ t('cta.btnText') }}</a></li>
                <li><a href="#cases" onclick="location.href='#cases'">{{ t('aiShowcase.btnText') }}</a></li>
              </ul>
            </div>
          </div>
        </div>
        <div class="footer-divider"></div>
        <div class="footer-bottom">
          <p>{{ t('footer.copyright') }}</p>
          <div class="footer-links-small">
            <a href="#contact" onclick="location.href='#contact'">{{ t('footer.terms') }}</a>
            <a href="#contact" onclick="location.href='#contact'">{{ t('footer.privacy') }}</a>
            <a href="#contact" onclick="location.href='#contact'">{{ t('footer.cookie') }}</a>
            <a href="#home" onclick="location.href='#home'">{{ t('footer.sitemap') }}</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, watch, onMounted,onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { HomeresoultService } from '@/api/Home.js';
import router from '@/router';
// 多语言
const { t, locale } = useI18n()

// 图片/视频资源配置
const imageResources = ref({
  carousel: [
    { type: 'image', src: 'https://picsum.photos/1920/800?random=1' },
    { type: 'image', src: 'https://www.w3school.com.cn/i/movie.mp4' },
    { type: 'image', src: 'https://picsum.photos/1920/800?random=3' }
  ],
  advantages: [
    'https://picsum.photos/500/350?random=10',
    'https://picsum.photos/500/350?random=11',
    'https://picsum.photos/500/350?random=12'
  ],
  aiShowcase: 'https://picsum.photos/500/400?random=20',
  avatars: [
    'https://picsum.photos/60/60?random=1',
    'https://picsum.photos/60/60?random=2',
    'https://picsum.photos/60/60?random=3'
  ],
  partners: [
    'https://picsum.photos/150/80?random=30',
    'https://picsum.photos/150/80?random=31',
    'https://picsum.photos/150/80?random=32',
    'https://picsum.photos/150/80?random=33',
    'https://picsum.photos/150/80?random=34'
  ],
  contact: 'https://picsum.photos/600/400?random=40'
})

// 联系信息多语言配置
const contactInfo = ref({
  zh: {
    phone: '400-888-9999',
    email: 'contact@xsj-ai.com',
    address: '北京市海淀区中关村人工智能产业园A座15层',
    workingHours: '周一至周日 9:00-22:00'
  },
  en: {
    phone: '+86 400-888-9999',
    email: 'contact@xsj-ai.com',
    address: '15/F, Building A, Zhongguancun AI Industrial Park, Haidian District, Beijing',
    workingHours: 'Monday to Sunday 9:00-22:00 (GMT+8)'
  },
  ja: {
    phone: '+86 400-888-9999',
    email: 'contact@xsj-ai.com',
    address: '北京市海淀区中关村人工知能産業園A棟15階',
    workingHours: '月曜日～日曜日 9:00～22:00（中国標準時）'
  }
})

const currentContactInfo = ref(contactInfo.value.zh)
watch(locale, (newLang) => {
  currentContactInfo.value = contactInfo.value[newLang] || contactInfo.value.zh
}, { immediate: true })

// 轮播图逻辑（图片+视频）
const carouselInterval = ref(5000) 
const carouselActiveIndex = ref(0)
let carouselTimer = null
const videoRefs = ref([])

// 暂停所有视频
const pauseAllVideos = () => {
  videoRefs.value.forEach(video => {
    if (video) video.pause()
  })
}

// 播放当前视频
const playCurrentVideo = () => {
  const currentItem = imageResources.value.carousel[carouselActiveIndex.value]
  const currentVideo = videoRefs.value[carouselActiveIndex.value]
  if (currentItem?.type === 'video' && currentVideo) {
    currentVideo.play().catch(err => {
      console.log('视频自动播放失败（需用户交互）:', err)
    })
  }
}

// 切换轮播页
const goToSlide = (index) => {
  pauseAllVideos()
  carouselActiveIndex.value = index
  setTimeout(() => playCurrentVideo(), 300)
  restartCarousel()
}

// 自动切换下一页
const nextSlide = () => {
  pauseAllVideos()
  const total = imageResources.value.carousel.length
  carouselActiveIndex.value = (carouselActiveIndex.value + 1) % total
  setTimeout(() => playCurrentVideo(), 300)
}

// 启动轮播
const startCarousel = () => {
  if (carouselTimer) clearInterval(carouselTimer)
  carouselTimer = setInterval(() => {
    const total = imageResources.value.carousel.length
    if (total > 1) nextSlide()
  }, carouselInterval.value)
}

// 停止轮播
const stopCarousel = () => {
  if (carouselTimer) {
    clearInterval(carouselTimer)
    carouselTimer = null
  }
}

// 重启轮播
const restartCarousel = () => {
  stopCarousel()
  startCarousel()
}

//调用后端接口实现首页信息的渲染
const HomeResoult = async()=>{
  try {
	  console.log('@ 指向:', import.meta.env.BASE_URL);
	  
    const result = await HomeresoultService();
	
	console.log(result);
    // 假设后端返回格式：{ code: 200, data: {...}, msg: '成功' }（和后端统一）
    if(result.code === 0){ 
      // 赋值用 =，且字段名是 data（不是 date）
      imageResources.value = result.data; 
      console.log('首页数据加载成功', imageResources.value);
    } else {
      console.error('接口返回失败', result.msg);
    }
  } catch (err) {
    console.error('接口调用失败', err);
  }
}

onMounted(() => {
	HomeResoult()
  setTimeout(() => {
    playCurrentVideo()
    startCarousel()
  }, 200)
})

onUnmounted(() => {
  stopCarousel()
  pauseAllVideos()
})

// FAQ展开/收起逻辑
const faqOpen = ref(null)
const toggleFaq = (index) => {
  faqOpen.value = faqOpen.value === index ? null : index
}


//跳转到登录注册界面
const TurnLogin =()=>{
	router.push('/login');
}




</script>

<style scoped>
/* 全局样式 */
.home-container {
  background-color: #F9F6F0;
  min-height: 100vh;
  background-image: 
    radial-gradient(#D4A76A 0.5px, transparent 0.5px),
    radial-gradient(#D4A76A 0.5px, #F9F6F0 0.5px);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
  opacity: 1;
}

.container {
  width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 锚点偏移：防止导航栏遮挡内容 */
section {
  scroll-margin-top: 100px;
  scroll-behavior: smooth;
}

/* 导航栏样式 */
.navbar {
  background-color: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}
.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100px;
  flex-wrap: nowrap;
}
.navbar-brand {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-shrink: 0;
}

.brand-logo {
  font-size: 32px;
  font-weight: 800;
  color: #D4A76A;
  margin: 0;
}

.brand-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: max-content;
}

.brand-name h2 {
  font-size: 16px;
  color: #333;
  margin: 0;
  font-weight: 600;
  white-space: nowrap;
}

.brand-name p {
  font-size: 11px;
  color: #999;
  margin: 0;
  white-space: nowrap;
}
.navbar-menu {
  display: flex;
  gap: 40px;
  flex-wrap: nowrap;
  flex-shrink: 0;
}

.menu-item {
  text-decoration: none;
  color: #333;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  padding: 5px 12px;
  white-space: nowrap;
  min-width: max-content;
}
.menu-item:hover {
  color: #D4A76A;
}
.menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #D4A76A;
  transition: width 0.3s ease;
}
.menu-item:hover::after {
  width: 100%;
}
.navbar-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}
.lang-switch {
  display: flex;
  gap: 5px;
}
.lang-btn {
  padding: 5px 10px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-radius: 4px;
  transition: all 0.3s ease;
}
.lang-btn.active {
  color: #D4A76A;
  font-weight: 600;
  background-color: #F5F2EB;
}
.lang-btn:hover {
  color: #D4A76A;
}
.join-btn {
  background-color: #D4A76A;
  border: none;
  color: white;
  padding: 8px 20px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}
.join-btn:hover {
  background-color: #C29458;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(212, 167, 106, 0.2);
}

/* 轮播图样式 */
.carousel-section {
  width: 100%;
  margin-bottom: 60px;
  overflow: hidden;
}
.carousel-container {
  position: relative;
  height: 80vh;
}
.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 0.8s ease;
  z-index: 1;
}
.carousel-slide.active {
  opacity: 1;
  z-index: 2;
}
.carousel-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}
.carousel-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.carousel-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.4);
  z-index: 1;
}
.carousel-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  text-align: center;
  z-index: 2;
}
.carousel-title {
  font-size: 48px;
  margin-bottom: 10px;
  font-weight: 700;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
  animation: fadeUp 1s ease;
}
.carousel-desc {
  font-size: 20px;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
  animation: fadeUp 1s ease 0.3s;
  animation-fill-mode: both;
  max-width: 800px;
}
.carousel-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  animation: fadeUp 1s ease 0.6s;
  animation-fill-mode: both;
}
.carousel-btn {
  background-color: #D4A76A;
  border: none;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}
.carousel-btn:hover {
  background-color: #C29458;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(212, 167, 106, 0.3);
}
.carousel-join-btn {
  background-color: transparent;
  border: 2px solid white;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}
.carousel-join-btn:hover {
  background-color: rgba(255,255,255,0.2);
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.2);
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 3;
}

.indicator-btn {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
}
.indicator-btn.active {
  background-color: #D4A76A;
  border-color: #D4A76A;
  transform: scale(1.2);
}

/* 通用标题样式 */
.section-title {
  text-align: center;
  margin-bottom: 40px;
}
.section-title h2 {
  font-size: 32px;
  margin-bottom: 10px;
  color: #333;
  position: relative;
  display: inline-block;
}
.section-title h2::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background-color: #D4A76A;
}
.section-title p {
  color: #666;
  font-size: 16px;
  margin-top: 20px;
}

/* 核心功能样式 */
.features {
  padding: 60px 0;
  position: relative;
  z-index: 1;
}
.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.feature-card {
  padding: 25px;
  text-align: center;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.05);
  transition: all 0.5s ease;
  background-color: white;
  position: relative;
  overflow: hidden;
}
.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, transparent, #D4A76A, transparent);
  transition: left 1s ease;
}
.feature-card:hover::before {
  left: 100%;
}
.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
}
.feature-icon {
  color: #D4A76A;
  margin-bottom: 15px;
  position: relative;
}
.icon-animate {
  transition: all 0.5s ease;
}
.feature-card:hover .icon-animate {
  transform: rotate(10deg) scale(1.2);
  color: #C29458;
  filter: drop-shadow(0 0 8px rgba(212, 167, 106, 0.5));
}
.feature-card h3 {
  margin-bottom: 12px;
  font-size: 20px;
  color: #333;
}
.feature-card p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  font-size: 14px;
}
.feature-more-btn {
  color: #D4A76A;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 14px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}
.feature-more-btn:hover {
  color: #C29458;
}

/* 平台优势样式 */
.advantages {
  padding: 60px 0;
  background-color: #F5F2EB;
  position: relative;
  z-index: 1;
}
.advantages-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-bottom: 40px;
}
.advantage-card {
  background-color: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transition: all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}
.advantage-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 15px 35px rgba(0,0,0,0.15);
}
.advantage-image-wrap {
  position: relative;
  height: 220px;
  overflow: hidden;
}
.advantage-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.8s ease;
}
.advantage-card:hover .advantage-image {
  transform: scale(1.1);
  filter: brightness(1.1);
}
.advantage-icon {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 50px;
  height: 50px;
  background-color: rgba(255,255,255,0.9);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #D4A76A;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: all 0.5s ease;
  z-index: 2;
}
.advantage-card:hover .advantage-icon {
  transform: scale(1.1) rotate(15deg);
  background-color: #D4A76A;
  color: white;
}
.advantage-hover-effect {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    to bottom,
    transparent 0%,
    transparent 70%,
    rgba(0,0,0,0.4) 100%
  );
  opacity: 0;
  transition: opacity 0.5s ease;
  z-index: 1;
}
.advantage-card:hover .advantage-hover-effect {
  opacity: 1;
}
.advantage-content {
  padding: 25px;
}
.advantage-content h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 600;
}
.advantage-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  font-size: 14px;
}
.advantage-more-btn {
  color: #D4A76A;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 14px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}
.advantage-more-btn:hover {
  color: #C29458;
}
.advantages-cta {
  text-align: center;
  margin-top: 20px;
}
.advantages-join-btn {
  background-color: #D4A76A;
  border: none;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.advantages-join-btn:hover {
  background-color: #C29458;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(212, 167, 106, 0.3);
}

/* AI能力展示样式 */
.ai-showcase {
  padding: 60px 0;
  background-color: white;
}
.ai-content {
  display: flex;
  align-items: center;
  gap: 60px;
}
.ai-image {
  flex: 1;
}
.ai-img {
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.ai-text {
  flex: 1;
}
.ai-text h2 {
  font-size: 32px;
  color: #333;
  margin-bottom: 20px;
}
.ai-features-list {
  list-style: none;
  padding: 0;
  margin-bottom: 30px;
}
.ai-feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  font-size: 16px;
  color: #666;
}
.check-icon {
  color: #D4A76A;
  margin-right: 10px;
  flex-shrink: 0;
}
.ai-btn {
  background-color: #D4A76A;
  border: none;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}
.ai-btn:hover {
  background-color: #C29458;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(212, 167, 106, 0.3);
}

/* 用户评价样式 */
.testimonials {
  padding: 60px 0;
  background-color: #F5F2EB;
}
.testimonial-carousel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
}
.testimonial-card {
  background-color: white;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transition: all 0.5s ease;
}
.testimonial-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
}
.rating {
  margin-bottom: 15px;
}
.stars {
  display: flex;
  gap: 5px;
}
.star {
  color: #ddd;
}
.star.filled {
  color: #D4A76A;
}
.testimonial-content {
  font-size: 16px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 20px;
  font-style: italic;
}
.testimonial-author {
  display: flex;
  align-items: center;
  gap: 15px;
}
.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}
.author-info h4 {
  margin: 0;
  color: #333;
  font-size: 16px;
}
.author-info p {
  margin: 5px 0 0 0;
  color: #999;
  font-size: 14px;
}

/* 合作伙伴样式 */
.partners {
  padding: 60px 0;
  background-color: white;
}
.partners-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 30px;
  margin-bottom: 40px;
}
.partner-item {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80px;
  background-color: #F9F6F0;
  border-radius: 8px;
  transition: all 0.3s ease;
}
.partner-item:hover {
  background-color: #F5F2EB;
  transform: scale(1.05);
}
.partner-logo {
  max-width: 120px;
  max-height: 60px;
  object-fit: contain;
}
.partners-cta {
  text-align: center;
}
.partner-more-btn {
  color: #D4A76A;
  font-weight: 500;
  transition: all 0.3s ease;
  font-size: 16px;
  border: 1px solid #D4A76A;
  background: transparent;
  cursor: pointer;
  padding: 10px 25px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
}
.partner-more-btn:hover {
  background-color: #D4A76A;
  color: white;
}

/* FAQ样式 */
.faq {
  padding: 60px 0;
  background-color: #F9F6F0;
}
.faq-list {
  max-width: 800px;
  margin: 0 auto 40px;
}
.faq-item {
  margin-bottom: 10px;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.faq-question {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  font-weight: 500;
  color: #333;
  transition: all 0.3s ease;
}
.faq-question:hover {
  background-color: #F5F2EB;
}
.faq-arrow {
  transition: transform 0.3s ease;
}
.faq-arrow.rotated {
  transform: rotate(90deg);
  color: #D4A76A;
}
.faq-answer {
  padding: 0 20px 15px;
  color: #666;
  line-height: 1.8;
  animation: fadeIn 0.3s ease;
}
.faq-more {
  text-align: center;
}
.faq-more-btn {
  background-color: #D4A76A;
  border: none;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.faq-more-btn:hover {
  background-color: #C29458;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(212, 167, 106, 0.3);
}

/* 联系我们样式 */
.contact {
  padding: 60px 0;
  background-color: white;
}
.contact-content {
  display: flex;
  gap: 60px;
  align-items: center;
}
.contact-image {
  flex: 1;
}
.contact-img {
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}
.contact-info {
  flex: 1;
}
.contact-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 20px;
}
.contact-icon {
  color: #D4A76A;
  margin-top: 2px;
  flex-shrink: 0;
}
.contact-text h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 16px;
}
.contact-text p {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}
.contact-btn {
  margin-top: 20px;
  background-color: #D4A76A;
  border: none;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
}
.contact-btn:hover {
  background-color: #C29458;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(212, 167, 106, 0.3);
}

/* CTA样式 */
.cta {
  padding: 80px 0;
  background-color: #D4A76A;
  color: white;
  text-align: center;
}
.cta-content h2 {
  font-size: 32px;
  margin-bottom: 15px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
.cta-content p {
  font-size: 18px;
  margin-bottom: 30px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  opacity: 0.9;
}
.cta-btn {
  background-color: white;
  border: none;
  color: #D4A76A;
  padding: 15px 40px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}
.cta-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

/* 页脚样式 */
.footer {
  background-color: #333;
  color: white;
  padding: 60px 0 30px;
}
.footer-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
}
.footer-brand {
  flex: 1;
}
.footer-logo {
  font-size: 32px;
  font-weight: 800;
  color: #D4A76A;
  margin: 0 0 10px 0;
}
.footer-brand h4 {
  font-size: 18px;
  margin: 0 0 5px 0;
}
.footer-brand p {
  color: #999;
  font-size: 14px;
  margin: 0 0 20px 0;
}
.footer-social {
  display: flex;
  gap: 15px;
}
.social-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #444;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}
.social-icon:hover {
  background-color: #D4A76A;
  transform: translateY(-3px);
}
.footer-links {
  flex: 3;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.link-group h4 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #fff;
}
.link-group ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.link-group li {
  margin-bottom: 10px;
}
.link-group a {
  color: #999;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 14px;
}
.link-group a:hover {
  color: #D4A76A;
  padding-left: 5px;
}
.footer-divider {
  height: 1px;
  background-color: #444;
  margin-bottom: 20px;
}
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.footer-bottom p {
  color: #999;
  font-size: 14px;
  margin: 0;
}
.footer-links-small {
  display: flex;
  gap: 20px;
}
.footer-links-small a {
  color: #999;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}
.footer-links-small a:hover {
  color: #D4A76A;
}

/* 动画样式 */
@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    height: 0;
  }
  to {
    opacity: 1;
    height: auto;
  }
}

/* 响应式样式 */
@media (max-width: 1200px) {
  .container {
    width: 90%;
  }
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .advantages-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .testimonial-carousel {
    grid-template-columns: repeat(2, 1fr);
  }
  .partners-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .ai-content {
    flex-direction: column;
    gap: 30px;
  }
  .contact-content {
    flex-direction: column;
    gap: 30px;
  }
}

@media (max-width: 768px) {
  .navbar-menu {
    display: none;
  }
  .carousel-title {
    font-size: 32px;
  }
  .carousel-desc {
    font-size: 16px;
  }
  .features-grid {
    grid-template-columns: 1fr;
  }
  .advantages-grid {
    grid-template-columns: 1fr;
  }
  .testimonial-carousel {
    grid-template-columns: 1fr;
  }
  .partners-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .footer-top {
    flex-direction: column;
    gap: 40px;
  }
  .footer-links {
    grid-template-columns: repeat(2, 1fr);
  }
  .footer-bottom {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
}
</style>