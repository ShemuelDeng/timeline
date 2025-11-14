<!-- pages/remind/template-list.vue -->
<template>
  <view class="page">
    <!-- 顶部渐变背景 -->
    <view class="bg-top" />

    <!-- 顶部导航 -->
    <u-navbar
        placeholder
        safeAreaInsetTop
        title="提醒模板"
        :bgColor="'transparent'"
        :titleStyle="{ color: '#ffffff', fontWeight: 600, fontSize: '18px' }"
        leftIcon="arrow-left"
        @leftClick="handleBack"
    >
      <!-- 右侧模式切换装饰（··· ○），先做静态 -->
      <template #right>
        <view class="nav-toggle">
          <view class="nav-toggle-dots">···</view>
          <view class="nav-toggle-circle" />
        </view>
      </template>
    </u-navbar>

    <!-- 内容区域 -->
    <scroll-view scroll-y class="content">
      <!-- 顶部文案 + 圆按钮 -->
      <view class="hero">
        <view class="hero-left">
          <view class="hero-title">提醒模板</view>
          <view class="hero-sub">快速创建常用提醒</view>
        </view>
        <view class="hero-actions">
          <view class="hero-btn" @click="handleSearch">
            <u-icon name="search" size="22" color="#ffffff" />
          </view>
          <view class="hero-btn" @click="handleCreateTemplate">
            <u-icon name="plus" size="22" color="#ffffff" />
          </view>
        </view>
      </view>

      <!-- 分类 tabs（全部 / 工作 / 健康 / 生活） -->
      <scroll-view scroll-x class="cate-scroll" show-scrollbar="false">
        <view class="cate-row">
          <view
              v-for="item in categoryList"
              :key="item.code"
              :class="['cate-item', currentCategory === item.code ? 'cate-item--active' : '']"
              @click="switchCategory(item.code)"
          >
            <view class="cate-icon">
              <text>{{ item.icon }}</text>
            </view>
            <view class="cate-name">{{ item.name }}</view>
          </view>
        </view>
      </scroll-view>

      <!-- 热门推荐 -->
      <view class="section section-hot">
        <view class="section-header">
          <view class="section-title">
            <text class="emoji">🔥</text>
            <text>热门推荐</text>
          </view>
          <view class="section-sub">最常用的提醒模板</view>
        </view>

        <view class="tpl-grid">
          <view
              v-for="tpl in hotTemplates"
              :key="tpl.id"
              class="tpl-card"
              @click="useTemplate(tpl)"
          >
            <view class="tpl-card-main">
              <view class="tpl-icon">{{ tpl.emoji }}</view>
              <view class="tpl-name">{{ tpl.name }}</view>
              <view class="tpl-desc">{{ tpl.desc }}</view>
            </view>
            <view class="tpl-usage">
              {{ tpl.usage }}次使用
            </view>
          </view>
        </view>
      </view>

      <!-- 其它分组示例：工作提醒 -->
      <view class="section">
        <view class="section-header-row">
          <view class="section-header-left">
            <view class="section-title-text">工作提醒</view>
            <view class="section-sub">重要项目交付不忘记</view>
          </view>
          <view class="section-header-right">
            {{ workTemplates.length }} 个模板
          </view>
        </view>

        <view class="work-list">
          <view
              v-for="tpl in workTemplates"
              :key="tpl.id"
              class="work-item"
              @click="useTemplate(tpl)"
          >
            <view class="work-left">
              <view class="work-icon">
                <text>📋</text>
              </view>
              <view class="work-texts">
                <view class="work-title">{{ tpl.name }}</view>
                <view class="work-sub">{{ tpl.desc }}</view>
                <view class="work-tags">
                  <view
                      v-for="tag in tpl.tags"
                      :key="tag"
                      class="work-tag"
                  >
                    {{ tag }}
                  </view>
                </view>
              </view>
            </view>
            <view class="work-play">
              <view class="work-play-btn">
                <u-icon name="play-right-fill" color="#ffffff" size="18" />
              </view>
            </view>
          </view>
        </view>
      </view>

      <view style="height: 40rpx;" />
    </scroll-view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const categoryList = ref([
  { code: 'ALL', name: '全部', icon: '📋' },
  { code: 'WORK', name: '工作', icon: '💼' },
  { code: 'HEALTH', name: '健康', icon: '❤️' },
  { code: 'LIFE', name: '生活', icon: '🏠' }
])

const currentCategory = ref('ALL')

const allTemplates = ref([
  // 一些 mock 数据，后面你可以用接口数据替换
  {
    id: 1,
    category: 'HEALTH',
    name: '喝水提醒',
    desc: '每2小时提醒喝水',
    emoji: '💧',
    usage: 2856
  },
  {
    id: 2,
    category: 'WORK',
    name: '会议提醒',
    desc: '重要会议提前通知',
    emoji: '📅',
    usage: 1892
  },
  {
    id: 3,
    category: 'HEALTH',
    name: '吃药提醒',
    desc: '按时服药不忘记',
    emoji: '💊',
    usage: 1654
  },
  {
    id: 4,
    category: 'LIFE',
    name: '生日提醒',
    desc: '重要人士生日不忘记',
    emoji: '🎂',
    usage: 1432
  },
  {
    id: 5,
    category: 'WORK',
    name: '项目截止日期',
    desc: '重要项目交付提醒',
    emoji: '📌',
    usage: 986,
    tags: ['工作', '项目', '截止']
  }
])

// 热门推荐：这里简单取前4个，也可以按 usage 排序
const hotTemplates = computed(() => {
  let list = allTemplates.value
  if (currentCategory.value !== 'ALL') {
    list = list.filter(item => item.category === currentCategory.value)
  }
  return list.slice(0, 4)
})

// 工作提醒示例：category = WORK
const workTemplates = computed(() =>
    allTemplates.value.filter(item => item.category === 'WORK')
)

const handleBack = () => {
  uni.navigateBack({ delta: 1 })
}

const switchCategory = (code) => {
  currentCategory.value = code
}

// 点击模板，跳到创建提醒页并带上模板 id
const useTemplate = (tpl) => {
  // TODO: 根据你的实际路由调整
  uni.navigateTo({
    url: `/pages/remind/create-remind?templateId=${tpl.id}`
  })
}

const handleSearch = () => {
  // TODO: 跳到模板搜索页
  uni.showToast({
    title: '模板搜索（待接入）',
    icon: 'none'
  })
}

const handleCreateTemplate = () => {
  // TODO: 跳到自定义模板创建页
  uni.showToast({
    title: '创建自定义模板（待接入）',
    icon: 'none'
  })
}

onMounted(() => {
  // TODO: 这里可以改成从后端拉分类 & 模板列表
  // 比如：
  // templateAPI.getTemplateList().then(res => { allTemplates.value = res.data })
})
</script>

<style lang="scss" scoped>
.page {
  min-height: 100vh;
  background: #f5f6fa;
  position: relative;
}

/* 顶部渐变背景 */
.bg-top {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 420rpx;
  background: linear-gradient(135deg, #6e63ff, #4a8dff);
  z-index: 0;
}

/* 内容整体 */
.content {
  position: relative;
  z-index: 1;
  padding: 8rpx 24rpx 24rpx;
}

/* 顶部标题块 */
.hero {
  margin-top: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  color: #ffffff;
}

.hero-left {
  flex: 1;
}

.hero-title {
  font-size: 40rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
}

.hero-sub {
  font-size: 26rpx;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 16rpx;
}

.hero-btn {
  width: 72rpx;
  height: 72rpx;
  border-radius: 72rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.4);
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 顶部右侧 “··· ○” 切换控件 */
.nav-toggle {
  width: 140rpx;
  height: 48rpx;
  padding: 4rpx;
  border-radius: 48rpx;
  background: rgba(0, 0, 0, 0.16);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-toggle-dots {
  flex: 1;
  text-align: center;
  color: #ffffff;
  font-size: 28rpx;
}

.nav-toggle-circle {
  width: 34rpx;
  height: 34rpx;
  border-radius: 34rpx;
  background: #ffffff;
}

/* 分类 tabs */
.cate-scroll {
  margin-top: 32rpx;
  padding-bottom: 16rpx;
}

.cate-row {
  display: flex;
}

.cate-item {
  width: 180rpx;
  height: 140rpx;
  margin-right: 16rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.35);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.cate-item--active {
  background: #ffffff;
  border-color: #ffffff;
}

.cate-item--active .cate-name {
  color: #333333;
}

.cate-icon {
  font-size: 40rpx;
  margin-bottom: 10rpx;
}

.cate-name {
  font-size: 26rpx;
  color: #f5f7ff;
}

/* 白色 section 卡片 */
.section {
  margin-top: 16rpx;
  background: #ffffff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx 24rpx 24rpx;
}

/* 热门推荐的卡片顶部略圆（跟截图一样） */
.section-hot {
  margin-top: 20rpx;
}

/* 标题 */
.section-header {
  margin-bottom: 24rpx;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.section-title .emoji {
  margin-right: 12rpx;
}

.section-sub {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #a0a3af;
}

/* 热门模板网格 */
.tpl-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.tpl-card {
  width: 48%;
  height: 210rpx;
  border-radius: 28rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-sizing: border-box;
  background: linear-gradient(145deg, #6e63ff, #4a8dff);
  color: #ffffff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.tpl-icon {
  font-size: 38rpx;
  margin-bottom: 8rpx;
}

.tpl-name {
  font-size: 30rpx;
  font-weight: 600;
}

.tpl-desc {
  margin-top: 6rpx;
  font-size: 24rpx;
  opacity: 0.9;
}

.tpl-usage {
  align-self: flex-start;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  font-size: 22rpx;
  background: rgba(255, 255, 255, 0.22);
}

/* 工作提醒 section 头部 */
.section-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18rpx;
}

.section-header-left .section-title-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.section-header-left .section-sub {
  margin-top: 6rpx;
  font-size: 24rpx;
  color: #a0a3af;
}

.section-header-right {
  font-size: 24rpx;
  color: #a0a3af;
}

/* 工作提醒列表 */
.work-list {
  margin-top: 10rpx;
}

.work-item {
  padding: 18rpx 12rpx;
  border-radius: 24rpx;
  background: #f8f9ff;
  margin-bottom: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.work-left {
  display: flex;
  align-items: flex-start;
}

.work-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 20rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
  font-size: 32rpx;
}

.work-texts {
  flex: 1;
}

.work-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.work-sub {
  margin-top: 4rpx;
  font-size: 24rpx;
  color: #8f939f;
}

.work-tags {
  margin-top: 12rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
}

.work-tag {
  padding: 4rpx 10rpx;
  border-radius: 20rpx;
  background: #e4e8ff;
  color: #5660ff;
  font-size: 22rpx;
}

.work-play {
  margin-left: 12rpx;
}

.work-play-btn {
  width: 64rpx;
  height: 64rpx;
  border-radius: 32rpx;
  background: #5660ff;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
