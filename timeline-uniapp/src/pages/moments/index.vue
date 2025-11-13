<template>
  <view class="moments-page">
    <!-- Header -->
    <view class="header">
      <u-icon name="list" size="24" color="#333"></u-icon>
      <text class="title">团纸日记</text>
      <u-icon name="chat" size="24" color="#333"></u-icon>
    </view>
    
    <!-- Moments List -->
    <scroll-view scroll-y class="moments-list">
      <view class="timeline-rail"></view>
      <view class="moment-item" v-for="(item, index) in momentsList" :key="index">
        <!-- Content（包含日期头） -->
        <view class="content">
          <view class="date-header">
            <view class="year">{{ item.year }}</view>
            <view class="date">{{ item.date }}</view>
            <view class="time">{{ item.time }}</view>
          </view>
          <text class="moment-text">{{ item.text }}</text>
          <!-- Images Grid -->
          <view class="images-grid" v-if="item.images && item.images.length > 0">
            <view class="image-item" v-for="(img, imgIndex) in item.images" :key="imgIndex" :class="getImageClass(item.images.length, imgIndex)">
              <image :src="img" mode="aspectFill" @click="previewImage(img, item.images)"></image>
            </view>
          </view>
        </view>
        <!-- Actions -->
        <view class="actions" v-if="item.showActions">
          <view class="action-btn">
            <u-icon name="edit-pen" size="16" color="#4a90e2"></u-icon>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- Floating Action Button -->
    <view class="fab" @click="onFab">
      <u-icon name="edit-pen" size="22" color="#fff"></u-icon>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      momentsList: [
        {
          year: '2025',
          date: '07.06',
          time: 'Sun / 22:01:06',
          text: '冒大太阳，终于爬上了金海湖的石崖',
          images: [
            'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop',
            'https://images.unsplash.com/photo-1506197603052-3cc9c3a201bd?w=400&h=300&fit=crop',
            'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop'
          ],
          showActions: false
        },
        {
          year: '2025',
          date: '07.05',
          time: 'Sat / 19:03:00',
          text: '天将黑，花未眠',
          images: [
            'https://images.unsplash.com/photo-1518709268805-4e9042af2176?w=400&h=600&fit=crop'
          ],
          showActions: false
        },
        {
          year: '2025',
          date: '07.04',
          time: 'Fri / 17:04:00',
          text: '黄昏观荷，遇到两朵洞刺的荷花，你不理我，我背对你',
          images: [
            'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=300&fit=crop',
            'https://images.unsplash.com/photo-1558618047-3c8ad309d32d?w=400&h=300&fit=crop',
            'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop'
          ],
          showActions: true
        }
      ]
    }
  },
  methods: {
    // 预览图片
    previewImage(current, urls) {
      uni.previewImage({
        current: current,
        urls: urls
      })
    },
    
    // 获取图片样式类名
    getImageClass(totalCount, index) {
      if (totalCount === 1) {
        return 'single-image'
      } else if (totalCount === 2) {
        return 'double-image'
      } else if (totalCount === 3) {
        return 'triple-image'
      } else {
        return 'multiple-image'
      }
    },

    onFab() {
      uni.showToast({ title: '新建日记', icon: 'none' })
    }
  },
  
  onLoad() {
    // 设置页面标题
    uni.setNavigationBarTitle({
      title: '团纸日记'
    })
  }
}
</script>

<style scoped>
.moments-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #fff;
  border-bottom: 1rpx solid #e5e5e5;
}

.title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.moments-list {
  position: relative;
  flex: 1;
  padding: 0 32rpx 80rpx 32rpx;
  background: #f5f5f5;
}

.timeline-rail {
  position: absolute;
  left: 8rpx; /* 调整更靠左，使右侧容纳日期时间 */
  top: 0;
  bottom: 0;
  width: 0;
  border-left: 2rpx dashed #d9d9d9;
}

.moment-item {
  position: relative;
  display: flex;
  margin-bottom: 40rpx;
  margin-top: 32rpx;
}

.moment-item::before {
  content: '';
  position: absolute;
  left: 0rpx; /* 与左侧48rpx的时间轴居中（48-7） */
  top: 40rpx;
  width: 14rpx;
  height: 14rpx;
  background: #fff; /* 白色填充 */
  border-radius: 50%;
  border: 2rpx solid #cfcfcf; /* 灰色描边，符合UI */
  box-shadow: none; /* 去掉蓝色光晕 */
}

.date-badge {
  width: 140rpx;
  margin-left: 72rpx; /* 放到时间轴右侧 */
  margin-right: 24rpx;
  flex-shrink: 0;
  text-align: left;
}

.year {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 4rpx;
}

.date {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 4rpx;
}

.time {
  font-size: 20rpx;
  color: #999;
}

.content {
  flex: 1;
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  position: relative;
  margin-left: 72rpx; /* 让整张卡片在时间轴右侧 */
 }
 
 .content::before {
   content: '';
   position: absolute;
   left: -12rpx;
   top: 24rpx;
   width: 0;
   height: 0;
   border-top: 12rpx solid transparent;
   border-bottom: 12rpx solid transparent;
   border-right: 12rpx solid #fff;
 }
 
 .date-header {
   margin-bottom: 16rpx;
 }
 
 .moment-text {
   font-size: 28rpx;
   line-height: 1.6;
   color: #333;
   display: block;
   margin-bottom: 20rpx;
 }

.images-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
}

.image-item {
  border-radius: 8rpx;
  overflow: hidden;
}

.image-item image {
  width: 100%;
  height: 100%;
  display: block;
}

/* 单张图片 */
.single-image {
  width: 400rpx;
  height: 500rpx;
}

/* 两张图片 */
.double-image {
  width: calc(50% - 4rpx);
  height: 200rpx;
}

/* 三张图片 */
.triple-image {
  width: calc(33.33% - 6rpx);
  height: 150rpx;
}

/* 多张图片 */
.multiple-image {
  width: calc(33.33% - 6rpx);
  height: 150rpx;
}

.actions {
  width: 60rpx;
  margin-left: 16rpx;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 24rpx;
}

.fab {
  position: fixed;
  left: 90%;
  transform: translateX(-50%);
  bottom: 40rpx;
  width: 90rpx;
  height: 90rpx;
  background: #00455b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 20rpx rgba(0,0,0,0.15);
}



/* 小红点样式 */
.moment-item:last-child .actions::after {
  content: '';
  position: absolute;
  right: 20rpx;
  top: 10rpx;
  width: 12rpx;
  height: 12rpx;
  background: #ff4d4f;
  border-radius: 50%;
  border: 2rpx solid #fff;
}
</style>