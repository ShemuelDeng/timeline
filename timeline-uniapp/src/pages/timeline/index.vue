<template>
  <view class="timeline-page">
    <!-- 顶部导航 -->
    <u-navbar
        placeholder
        safeAreaInsetTop
        title="时光轴"
        :bgColor="'transparent'"
        :titleStyle="{ fontWeight: 600, fontSize: '18px', color: '#ffffff' }"
        :autoBack="true"
        leftIcon="arrow-left"
        leftText="返回"
        leftIconColor="#ffffff"
        leftTextStyle="{ color: '#ffffff' }"
    />


    <!-- 顶部介绍 + 按钮区 -->
    <view class="timeline-header">
      <text class="header-title">效率时间</text>
      <text class="header-subtitle">专注记录与分析每一个生活瞬间</text>

      <view class="header-actions">
        <button class="header-btn primary" @click="showCreateModal">
          <text class="header-btn-plus">＋</text>
          <text>创建时光轴</text>
        </button>
        <button class="header-btn ghost" @click="goMoments">
          时间轴组件
        </button>
      </view>
    </view>

    <!-- 时光轴卡片列表（保持卡片形式不变） -->
    <view class="timeline-list">
      <view
          v-for="(item, idx) in timelines"
          :key="item.id"
          class="timeline-card"
          @click="goDetail(item)"
      >
        <view class="card-header">
          <text class="card-icon" :style="{ background: item.bgColor }">
            {{ item.icon }}
          </text>
          <text class="card-title">{{ item.title }}</text>
        </view>
        <view class="card-desc">{{ item.desc }}</view>
        <view class="card-tags">
          <text class="card-tag">{{ item.tag }}</text>
        </view>
        <view class="card-footer">
          <text class="card-date">创建于 {{ item.date }}</text>
          <text class="card-count">{{ item.count }} 个事件</text>
        </view>
      </view>
    </view>

    <!-- 创建新时光轴弹框 -->
    <view class="modal" v-if="showModal">
      <view class="modal-mask" @click="hideCreateModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">创建新的时光轴</text>
          <text class="modal-close" @click="hideCreateModal">×</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">标题</text>
            <input
                class="form-input"
                type="text"
                v-model="newTimeline.title"
                placeholder="例如：宝宝成长记录"
            />
          </view>
          <view class="form-item">
            <text class="form-label">描述</text>
            <textarea
                class="form-textarea"
                v-model="newTimeline.desc"
                placeholder="简单描述这个时光轴的内容"
            />
          </view>
          <view class="form-item">
            <text class="form-label">分类</text>
            <view class="form-select">
              <picker
                  @change="categoryChange"
                  :value="categoryIndex"
                  :range="categories"
              >
                <view class="picker-value">{{ categories[categoryIndex] }}</view>
              </picker>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">背景图片</text>
            <view class="form-select">
              <picker
                  @change="bgImageChange"
                  :value="bgImageIndex"
                  :range="bgImageOptions"
              >
                <view class="picker-value">
                  {{ bgImageOptions[bgImageIndex] }}
                </view>
              </picker>
            </view>
          </view>
          <view class="form-item" v-if="bgImageIndex === 1">
            <text class="form-label">自定义图片链接</text>
            <input
                class="form-input"
                type="text"
                v-model="newTimeline.bgImageUrl"
                placeholder="或输入自定义图片URL"
            />
          </view>
          <view class="form-actions">
            <button class="btn-cancel" @click="hideCreateModal">取消</button>
            <button class="btn-create" @click="createTimeline">创建</button>
          </view>
        </view>
      </view>
    </view>

    <!-- 授权弹窗 -->
    <view class="auth-modal" v-if="showAuthModal">
      <view class="auth-mask" @click="hideAuthPopup"></view>
      <view class="auth-content">
        <view class="auth-header">
          <text class="auth-title">授权登录</text>
        </view>
        <view class="auth-body">
          <view class="auth-avatar">
            <image src="/static/avatar.svg" mode="aspectFill"></image>
          </view>
          <view class="auth-info">
            <view class="auth-name">时光轴</view>
            <view class="auth-desc">申请获取您的公开信息（昵称、头像等）</view>
          </view>
          <view class="auth-actions">
            <button class="auth-cancel" @click="hideAuthPopup">取消</button>
            <button class="auth-confirm" @click="handleAuth">确认授权</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>



<script>
import { timelineAPI, authAPI } from '@/utils/api.js';

export default {
  data() {
    return {
      showModal: false,
      showAuthModal: false, // 是否显示授权弹窗
      isLoggedIn: false, // 是否已登录
      userInfo: null, // 用户信息
      newTimeline: {
        title: '',
        desc: '',
        category: '其他',
        bgImage: '无背景图片',
        bgImageUrl: ''
      },
      categoryIndex: 0,
      categories: ['其他', '家庭', '工作', '学习', '旅行', '情感'],
      bgImageIndex: 0,
      bgImageOptions: ['无背景图片', '自定义图片链接'],
      timelines: [],
      loading: false,
    }
  },
  onLoad() {
    // 检查登录状态
    const token = uni.getStorageSync('token');
    console.log("token", token)
    if (token) {
      this.isLoggedIn = true;
      this.userInfo = uni.getStorageSync('userInfo') || null;
      this.fetchTimelineList();
    } else {
      // 显示授权弹窗
      setTimeout(() => {
        this.showAuthModal = true;
      }, 500);
    }
  },
  methods: {
    // 获取时间轴列表
    fetchTimelineList() {
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      timelineAPI.getTimelineList()
        .then(res => {
          if (res.code === 200) {
            // 处理返回的数据
            this.timelines = res.data.records.map(item => ({
              id: item.id,
              icon: this.getIconByTag(item.tag),
              title: item.title,
              desc: item.description,
              tag: item.tag,
              date: this.formatDate(item.createTime),
              count: item.eventCount || 0,
              color: this.getColorByTag(item.tag),
              bgColor: this.getColorByTag(item.tag),
            }));
          } else {
            uni.showToast({
              title: res.message || '获取时间轴列表失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取时间轴列表失败:', err);
          uni.showToast({
            title: '获取时间轴列表失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
          uni.hideLoading();
        });
    },
    
    // 根据标签获取图标
    getIconByTag(tag) {
      const iconMap = {
        '家庭': '🏠',
        '工作': '💼',
        '学习': '🎓',
        '旅行': '✈️',
        '情感': '❤️',
        '其他': '📝'
      };
      return iconMap[tag] || '📝';
    },
    
    // 根据标签获取颜色
    getColorByTag(tag) {
      const colorMap = {
        '家庭': '#F9C7D1',
        '工作': '#A1C4FD',
        '学习': '#C7D6F9',
        '旅行': '#C2E9FB',
        '情感': '#FFCAC9',
        '其他': '#338aff'
      };
      return colorMap[tag] || '#338aff';
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getDate().toString().padStart(2, '0')}`;
    },
    
    goDetail(item) {
      uni.navigateTo({
        url: `/pages/timeline/timeline-detail?id=${item.id}`
      })
    },
    
    goMoments() {
      uni.navigateTo({
        url: '/pages/moments/index'
      })
    },
    
    showCreateModal() {
      this.showModal = true;
    },
    
    hideCreateModal() {
      this.showModal = false;
      // 重置表单
      this.newTimeline = {
        title: '',
        desc: '',
        category: '其他',
        bgImage: '无背景图片',
        bgImageUrl: ''
      };
      this.categoryIndex = 0;
      this.bgImageIndex = 0;
    },
    
    categoryChange(e) {
      this.categoryIndex = e.detail.value;
      this.newTimeline.category = this.categories[this.categoryIndex];
    },
    
    bgImageChange(e) {
      this.bgImageIndex = e.detail.value;
      this.newTimeline.bgImage = this.bgImageOptions[this.bgImageIndex];
    },
    
    // 显示授权弹窗
    showAuthPopup() {
      this.showAuthModal = true;
    },
    
    // 隐藏授权弹窗
    hideAuthPopup() {
      this.showAuthModal = false;
    },
    
    // 处理授权
    handleAuth() {
      uni.showLoading({
        title: '登录中...'
      });
      
      // 调用登录接口
      authAPI.login()
        .then(res => {
          if (res.code === 200) {
            // 登录成功
            this.isLoggedIn = true;
            this.userInfo = res.data.userInfo || {
               nickname: 'shemuel',
               avatar: '/static/avatar.svg'
             };
            
            // 保存登录状态和用户信息
             uni.setStorageSync('token', res.data.tokenValue || 'mock-token');
             uni.setStorageSync('userInfo', this.userInfo);
            
            // 隐藏授权弹窗
            this.hideAuthPopup();
            
            // 获取时间轴列表
            this.fetchTimelineList();
            
            uni.showToast({
              title: '登录成功',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: res.message || '登录失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('登录失败:', err);
          uni.showToast({
            title: '登录失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    },
    
    createTimeline() {
      // 检查登录状态
      if (!this.isLoggedIn) {
        this.showAuthPopup();
        return;
      }
      
      // 表单验证
      // if (!this.newTimeline.title) {
      //   uni.showToast({
      //     title: '请输入标题',
      //     icon: 'none'
      //   });
      //   return;
      // }
      
      // 显示加载中
      uni.showLoading({
        title: '创建中...'
      });
      
      // 准备请求数据
      const data = {
        title: this.newTimeline.title,
        description: this.newTimeline.desc || '',
        tag: this.newTimeline.category,
        coverUrl: this.bgImageIndex === 1 ? this.newTimeline.bgImageUrl : ''
      };
      
      // 调用API创建时间轴
      timelineAPI.addTimeline(data)
        .then(res => {
          if (res.code === 200) {
            // 创建成功，刷新列表
            this.hideCreateModal();
            this.fetchTimelineList();
            
            uni.showToast({
              title: '创建成功',
              icon: 'success'
            });
          } else {
            uni.showToast({
              title: res.message || '创建失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('创建时间轴失败:', err);
          uni.showToast({
            title: '创建失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
        });
    }
  }
}
</script>

<style scoped>
/* 整体背景：按参考图提取的蓝紫渐变 */
.timeline-page {
  min-height: 100vh;
  box-sizing: border-box;
  padding-bottom: 40rpx;
  background: linear-gradient(180deg, #6a6dd0 0%, #8184da 45%, #b0b4e1 100%);
}

/* 顶部说明区域 */
.timeline-header {
  padding: 32rpx 28rpx 12rpx;
  color: #ffffff;
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  margin-bottom: 10rpx;
}

.header-subtitle {
  font-size: 26rpx;
  opacity: 0.9;
}

/* 上方两个按钮：主按钮 + 次按钮 放在一行 */
.header-actions {
  margin-top: 26rpx;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.header-btn {
  height: 64rpx;
  padding: 0 30rpx;
  border-radius: 999rpx;
  border: none;
  font-size: 26rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 主按钮：偏暖一点，突出但不炸眼 */
.header-btn.primary {
  background: linear-gradient(135deg, #ffd58e 0%, #ff9ca9 100%);
  color: #ffffff;
  box-shadow: 0 6rpx 14rpx rgba(255, 156, 169, 0.45);
}

/* 次按钮：轻一点，类似参考图右上角的小胶囊 */
.header-btn.ghost {
  margin-left: 16rpx;
  background: rgba(255, 255, 255, 0.22);
  color: #ffffff;
  border: 1rpx solid rgba(255, 255, 255, 0.55);
}

.header-btn-plus {
  font-size: 32rpx;
  margin-right: 6rpx;
}

/* 卡片列表：两列布局，保持“卡片形式” */
.timeline-list {
  padding: 16rpx 24rpx 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

/* 单个时光轴卡片：参考图那种浅浅的玻璃卡片效果 */
.timeline-card {
  width: 48%;
  margin-bottom: 24rpx;
  padding: 24rpx 20rpx 20rpx;
  box-sizing: border-box;
  border-radius: 20rpx;
  background: linear-gradient(
      145deg,
      rgba(255, 255, 255, 0.22),
      rgba(255, 255, 255, 0.10)
  );
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 4rpx 12rpx rgba(26, 45, 110, 0.20);
  color: #ffffff;
}

/* 卡片头部：icon + 标题 */
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 14rpx;
}

.card-icon {
  width: 52rpx;
  height: 52rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 34rpx;
  margin-right: 14rpx;
  /* 背景还是用 item.bgColor（外面 :style 已经传进来） */
}

.card-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #ffffff;
}

/* 描述：稍微淡一点 */
.card-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.92);
  margin-bottom: 10rpx;
}

/* 标签 */
.card-tags {
  margin-bottom: 12rpx;
}

.card-tag {
  font-size: 22rpx;
  padding: 4rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  color: #ffffff;
}

/* 底部信息：更弱一点 */
.card-footer {
  display: flex;
  justify-content: space-between;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.82);
}

.card-date,
.card-count {
  white-space: nowrap;
}

/* ===== 弹框和授权弹窗，保持原来的白色风格即可 ===== */

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 24rpx 0 rgba(0, 0, 0, 0.1);
}

.modal-header {
  position: relative;
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  text-align: center;
  display: block;
}

.modal-close {
  position: absolute;
  right: 30rpx;
  top: 30rpx;
  font-size: 40rpx;
  color: #999;
  line-height: 1;
}

.modal-body {
  padding: 30rpx;
}

.form-item {
  margin-bottom: 24rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 12rpx;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  height: 80rpx;
  border: 1rpx solid #ddd;
  border-radius: 8rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
  background: #f9f9f9;
}

.form-textarea {
  height: 160rpx;
  padding: 20rpx;
  line-height: 1.5;
}

.picker-value {
  height: 80rpx;
  line-height: 80rpx;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 40rpx;
}

.btn-cancel,
.btn-create {
  width: 45%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-create {
  background: #338aff;
  color: #fff;
}

/* 授权弹窗 */
.auth-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.auth-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.auth-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
  box-shadow: 0 -4rpx 24rpx 0 rgba(0, 0, 0, 0.1);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.auth-header {
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
}

.auth-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  text-align: center;
  display: block;
}

.auth-body {
  padding: 40rpx 30rpx;
}

.auth-avatar {
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 30rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 1rpx solid #eee;
}

.auth-avatar image {
  width: 100%;
  height: 100%;
}

.auth-info {
  text-align: center;
  margin-bottom: 40rpx;
}

.auth-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 12rpx;
}

.auth-desc {
  font-size: 26rpx;
  color: #6b7a8f;
}

.auth-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 40rpx;
}

.auth-cancel,
.auth-confirm {
  width: 45%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.auth-cancel {
  background: #f5f5f5;
  color: #666;
}

.auth-confirm {
  background: #338aff;
  color: #fff;
}
</style>




