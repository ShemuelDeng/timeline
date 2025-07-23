<template>
  <view class="timeline-page">
    <view class="header">
      <text class="main-title">时光轴</text>
      <text class="sub-title">记录生活中的每一个重要时刻</text>
      <button class="create-btn" @click="showCreateModal">+ 创建新时光轴</button>
    </view>
    <view class="timeline-list">
      <view
        v-for="(item, idx) in timelines"
        :key="item.id"
        class="timeline-card"
        :style="{ borderTop: '8rpx solid ' + item.color, boxShadow: '0 4rpx 24rpx 0 rgba(0,0,0,0.06)' }"
        @click="goDetail(item)"
      >
        <view class="card-header">
          <text class="card-icon" :style="{ background: item.bgColor }">{{ item.icon }}</text>
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
            <input class="form-input" type="text" v-model="newTimeline.title" placeholder="例如：宝宝成长记录" />
          </view>
          <view class="form-item">
            <text class="form-label">描述</text>
            <textarea class="form-textarea" v-model="newTimeline.desc" placeholder="简单描述这个时光轴的内容" />
          </view>
          <view class="form-item">
            <text class="form-label">分类</text>
            <view class="form-select">
              <picker @change="categoryChange" :value="categoryIndex" :range="categories">
                <view class="picker-value">{{ categories[categoryIndex] }}</view>
              </picker>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">背景图片</text>
            <view class="form-select">
              <picker @change="bgImageChange" :value="bgImageIndex" :range="bgImageOptions">
                <view class="picker-value">{{ bgImageOptions[bgImageIndex] }}</view>
              </picker>
            </view>
          </view>
          <view class="form-item" v-if="bgImageIndex === 1">
            <text class="form-label">自定义图片链接</text>
            <input class="form-input" type="text" v-model="newTimeline.bgImageUrl" placeholder="或输入自定义图片URL" />
          </view>
          <view class="form-actions">
            <button class="btn-cancel" @click="hideCreateModal">取消</button>
            <button class="btn-create" @click="createTimeline">创建</button>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      showModal: false,
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
      timelines: [
        {
          id: 1,
          icon: '🏠',
          title: '宝宝成长记录',
          desc: '记录宝宝成长的每一个重要时刻',
          tag: '家庭',
          date: '2024/1/1',
          count: 4,
          color: '#F9C7D1',
          bgColor: '#F9C7D1',
        },
        {
          id: 2,
          icon: '❤️',
          title: '恋爱时光',
          desc: '记录我们美好的恋爱历程',
          tag: '情感',
          date: '2023/6/1',
          count: 4,
          color: '#F9C7D1',
          bgColor: '#F9C7D1',
        },
        {
          id: 3,
          icon: '🎓',
          title: '学习编程之路',
          desc: '记录我的编程学习历程',
          tag: '学习',
          date: '2023/1/1',
          count: 3,
          color: '#C7D6F9',
          bgColor: '#C7D6F9',
        },
      ],
    }
  },
  methods: {
    goDetail(item) {
      uni.navigateTo({
        url: `/pages/timeline-detail/timeline-detail?id=${item.id}`
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
    createTimeline() {
      // 表单验证
      if (!this.newTimeline.title) {
        uni.showToast({
          title: '请输入标题',
          icon: 'none'
        });
        return;
      }
      
      // 创建新时光轴
      const newId = this.timelines.length > 0 ? Math.max(...this.timelines.map(t => t.id)) + 1 : 1;
      const newTimeline = {
        id: newId,
        icon: '📝',
        title: this.newTimeline.title,
        desc: this.newTimeline.desc || '暂无描述',
        tag: this.newTimeline.category,
        date: new Date().toLocaleDateString().replace(/\//g, '/'),
        count: 0,
        color: '#338aff',
        bgColor: '#338aff',
      };
      
      this.timelines.push(newTimeline);
      this.hideCreateModal();
      
      uni.showToast({
        title: '创建成功',
        icon: 'success'
      });
    }
  }
}
</script>

<style scoped>
.timeline-page {
  min-height: 100vh;
  background: #f7fafd;
  padding-bottom: 40rpx;
}
.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 60rpx;
  padding-bottom: 30rpx;
}
.main-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #222;
  margin-bottom: 12rpx;
}
.sub-title {
  font-size: 28rpx;
  color: #6b7a8f;
  margin-bottom: 32rpx;
}
.create-btn {
  background: #338aff;
  color: #fff;
  font-size: 28rpx;
  border-radius: 40rpx;
  padding: 0 48rpx;
  height: 64rpx;
  line-height: 64rpx;
  border: none;
  box-shadow: 0 2rpx 8rpx 0 rgba(51,138,255,0.08);
  margin-bottom: 24rpx;
}
.timeline-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  padding: 0 24rpx;
}
.timeline-card {
  width: 100%;
  max-width: 48%;
  background: #fff;
  border-radius: 24rpx;
  margin-bottom: 32rpx;
  padding: 32rpx 28rpx 24rpx 28rpx;
  box-sizing: border-box;
  transition: box-shadow 0.2s;
  display: inline-block;
}
@media (min-width: 500rpx) {
  .timeline-card {
    max-width: 48%;
  }
}
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 18rpx;
}
.card-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  margin-right: 16rpx;
}
.card-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
}
.card-desc {
  font-size: 26rpx;
  color: #6b7a8f;
  margin-bottom: 18rpx;
}
.card-tags {
  margin-bottom: 18rpx;
}
.card-tag {
  font-size: 22rpx;
  color: #338aff;
  background: #eaf3ff;
  border-radius: 10rpx;
  padding: 4rpx 18rpx;
}
.card-footer {
  display: flex;
  justify-content: space-between;
  font-size: 22rpx;
  color: #b0b8c9;
  margin-top: 8rpx;
}
.card-date {
}
.card-count {
}
/* 弹框样式 */
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

.form-input, .form-textarea, .form-select {
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

.btn-cancel, .btn-create {
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
</style>
