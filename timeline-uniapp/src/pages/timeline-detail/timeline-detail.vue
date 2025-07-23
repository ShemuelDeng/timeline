<template>
  <view class="timeline-detail-page">
    <!-- 固定区域 -->
    <view class="fixed-area">
      <view class="header">
        <text class="back-btn" @click="goBack">←</text>
        <view class="header-info">
          <text class="main-title">{{ timeline.title }}</text>
          <text class="sub-title">{{ timeline.desc }}</text>
        </view>
      </view>
      
      <view class="action-bar">
        <view class="sort-btn" @click="toggleSortOrder">
          <text class="sort-icon">{{ sortAscending ? '↑' : '↓' }}</text>
          <text>{{ sortAscending ? '倒序' : '正序' }}</text>
        </view>
        <view class="add-event-btn" @click="showAddEventModal">
          <text class="add-icon">+</text>
          <text>添加事件</text>
        </view>
      </view>
    </view>
    
    <!-- 可滚动区域 -->
    <scroll-view class="scrollable-area" scroll-y="true">
      <view class="event-list">
        <view class="timeline-line"></view>
        <view v-for="(event, index) in sortedEvents" :key="event.id" class="event-card">
          
          <view class="timeline-dot"></view>
          <view class="event-meta">
            <text class="event-date">{{ event.date }}</text>
            <text class="event-meta-dot">·</text>
            <text class="event-meta-item">{{ event.timeAgo }}</text>
            <text class="event-meta-dot">·</text>
            <view class="location-icon">
              <uni-icons type="location" size="14" color="#338aff"></uni-icons>
            </view>
            <text class="event-meta-item">{{ event.place }}</text>
          </view>
          <view class="event-title">{{ event.title }}</view>
          <view class="event-desc">{{ event.desc }}</view>
          <view class="event-images" v-if="event.images && event.images.length">
            <view class="event-img-row" v-for="row in getImageRows(event.images)" :key="row[0]">
              <image v-for="img in row" :key="img" :src="img" class="event-img" mode="aspectFill" />
            </view>
          </view>
          <view class="event-tags">
            <text class="event-tag" v-for="tag in event.tags" :key="tag">{{ tag }}</text>
          </view>
          
          <!-- 编辑和删除按钮 -->
          <view class="event-actions">
            <view class="event-action-btn edit-btn" @click="editEvent(event)">
              <uni-icons type="compose" size="20"></uni-icons>
            </view>
            <view class="event-action-btn delete-btn" @click="deleteEvent(event)">
              <uni-icons type="trash" size="20"></uni-icons>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
    <!-- 添加事件弹框 -->
    <view class="modal" v-if="showModal">
      <view class="modal-mask" @click="hideAddEventModal"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">添加新事件</text>
          <text class="modal-close" @click="hideAddEventModal">×</text>
        </view>
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">事件标题</text>
            <input class="form-input" type="text" v-model="newEvent.title" placeholder="例如：百天" />
          </view>
          <view class="form-item">
            <text class="form-label">描述</text>
            <textarea class="form-textarea" v-model="newEvent.desc" placeholder="简单描述这个事件的内容" />
          </view>
          <view class="form-item">
            <text class="form-label">日期</text>
            <view class="form-date-picker">
              <picker mode="date" :value="newEvent.date" @change="dateChange">
                <view class="picker-value">{{ newEvent.date }}</view>
              </picker>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">地点</text>
            <input class="form-input" type="text" v-model="newEvent.place" placeholder="例如：上海市人民公园" />
          </view>
          <view class="form-item">
            <text class="form-label">分类标签</text>
            <view class="form-select">
              <picker @change="tagChange" :value="tagIndex" :range="tags">
                <view class="picker-value">{{ tags[tagIndex] }}</view>
              </picker>
            </view>
          </view>
          <view class="form-item">
            <text class="form-label">图片 (可选)</text>
            <view class="upload-area" @click="addImage">
              <view class="upload-icon">+</view>
              <text class="upload-text">点击图片区域上传</text>
            </view>
            <view class="image-url-input" v-if="showImageUrlInput">
              <input class="form-input" type="text" v-model="newEvent.imageUrl" placeholder="https://example.com/image.jpg" />
              <button class="btn-add-url" @click="addImageUrl">添加</button>
            </view>
          </view>
          <view class="form-actions">
            <button class="btn-cancel" @click="hideAddEventModal">取消</button>
            <button class="btn-create" @click="createEvent">添加</button>
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
      showImageUrlInput: false,
      newEvent: {
        title: '',
        desc: '',
        date: this.formatDate(new Date()),
        place: '',
        tag: '重要时刻',
        images: [],
        imageUrl: ''
      },
      tagIndex: 0,
      tags: ['重要时刻', '里程碑', '成长', '学习', '游玩' ,'随笔','灵感', '其他'],
      timeline: {
        title: '宝宝成长记录',
        desc: '记录宝宝成长的每一个重要时刻',
      },
      events: [
        {
          id: 1,
          title: '百天',
          date: '2024年4月24日',
          timeAgo: '1年前',
          place: '儿童摄影工作室',
          desc: '宝宝百天快乐！拍了很多漂亮的照片',
          tags: ['重要时刻'],
          images: [
            'https://images.pexels.com/photos/415829/pexels-photo-415829.jpeg',
            'https://images.pexels.com/photos/325690/pexels-photo-325690.jpeg',
            'https://images.pexels.com/photos/459976/pexels-photo-459976.jpeg',
          ],
        },
        {
          id: 2,
          title: '第一次笑',
          date: '2024年2月28日',
          timeAgo: '1年前',
          place: '客厅沙发上',
          desc: '宝宝第一次对我们笑了，太治愈了',
          tags: ['成长里程碑'],
          images: [],
        },
        {
          id: 3,
          title: '进园',
          date: '2024年3月10日',
          timeAgo: '1年前',
          place: '幼儿园门口',
          desc: '宝宝第一次上幼儿园，勇敢又开心',
          tags: ['成长'],
          images: [
            'https://images.pexels.com/photos/3661350/pexels-photo-3661350.jpeg',
            'https://images.pexels.com/photos/1648376/pexels-photo-1648376.jpeg',
            'https://images.pexels.com/photos/1108099/pexels-photo-1108099.jpeg',
            'https://images.pexels.com/photos/35537/child-children-girl-happy.jpg',
          ],
        },
      ],
      sortAscending: true, // 默认正序排列（从早到晚）
    }
  },
  computed: {
    sortedEvents() {
      // 复制数组以避免修改原始数据
      const sortedArray = [...this.events]
      
      // 根据事件的日期字符串转换为时间戳排序
      if (this.sortAscending) {
        // 正序：从早到晚
        return sortedArray.sort((a, b) => {
          const dateA = new Date(a.date.replace(/年|月/g, '/').replace(/日/, '')).getTime();
          const dateB = new Date(b.date.replace(/年|月/g, '/').replace(/日/, '')).getTime();
          return dateA - dateB;
        })
      } else {
        // 倒序：从晚到早
        return sortedArray.sort((a, b) => {
          const dateA = new Date(a.date.replace(/年|月/g, '/').replace(/日/, '')).getTime();
          const dateB = new Date(b.date.replace(/年|月/g, '/').replace(/日/, '')).getTime();
          return dateB - dateA;
        })
      }
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    getImageRows(images) {
      // 最多9张，3*3排布
      const rows = []
      for (let i = 0; i < Math.min(images.length, 9); i += 3) {
        rows.push(images.slice(i, i + 3))
      }
      return rows
    },
    toggleSortOrder() {
      // 切换排序顺序
      this.sortAscending = !this.sortAscending
    },
    formatDate(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}/${month}/${day}`;
    },
    
    showAddEventModal() {
      this.showModal = true;
    },
    
    hideAddEventModal() {
      this.showModal = false;
      // 重置表单
      this.newEvent = {
        title: '',
        desc: '',
        date: this.formatDate(new Date()),
        place: '',
        tag: '重要时刻',
        images: [],
        imageUrl: ''
      };
      this.tagIndex = 0;
      this.showImageUrlInput = false;
    },
    
    dateChange(e) {
      this.newEvent.date = e.detail.value;
    },
    
    tagChange(e) {
      this.tagIndex = e.detail.value;
      this.newEvent.tag = this.tags[this.tagIndex];
    },
    
    addImage() {
      // 显示输入图片URL的输入框
      this.showImageUrlInput = true;
    },
    
    addImageUrl() {
      if (this.newEvent.imageUrl) {
        // 添加图片URL到图片数组
        this.newEvent.images.push(this.newEvent.imageUrl);
        this.newEvent.imageUrl = '';
        uni.showToast({
          title: '图片添加成功',
          icon: 'success'
        });
      } else {
        uni.showToast({
          title: '请输入图片URL',
          icon: 'none'
        });
      }
    },
    
    createEvent() {
      // 表单验证
      if (!this.newEvent.title) {
        uni.showToast({
          title: '请输入事件标题',
          icon: 'none'
        });
        return;
      }
      
      if (!this.newEvent.date) {
        uni.showToast({
          title: '请选择日期',
          icon: 'none'
        });
        return;
      }
      
      if (!this.newEvent.place) {
        uni.showToast({
          title: '请输入地点',
          icon: 'none'
        });
        return;
      }
      
      // 创建新事件
      const newId = this.events.length > 0 ? Math.max(...this.events.map(e => e.id)) + 1 : 1;
      
      // 计算时间差
      const eventDate = new Date(this.newEvent.date.replace(/\//g, '-'));
      const now = new Date();
      const diffYears = now.getFullYear() - eventDate.getFullYear();
      const diffMonths = now.getMonth() - eventDate.getMonth();
      const diffDays = now.getDate() - eventDate.getDate();
      
      let timeAgo = '';
      if (diffYears > 0) {
        timeAgo = `${diffYears}年前`;
      } else if (diffMonths > 0) {
        timeAgo = `${diffMonths}个月前`;
      } else if (diffDays > 0) {
        timeAgo = `${diffDays}天前`;
      } else {
        timeAgo = '今天';
      }
      
      // 格式化日期为中文格式
      const formattedDate = `${eventDate.getFullYear()}年${eventDate.getMonth() + 1}月${eventDate.getDate()}日`;
      
      const newEvent = {
        id: newId,
        title: this.newEvent.title,
        date: formattedDate,
        timeAgo: timeAgo,
        place: this.newEvent.place,
        desc: this.newEvent.desc || '暂无描述',
        tags: [this.newEvent.tag],
        images: this.newEvent.images
      };
      
      this.events.push(newEvent);
      this.hideAddEventModal();
      
      uni.showToast({
        title: '添加成功',
        icon: 'success'
      });
    },
    
    // 编辑事件
    editEvent(event) {
      uni.showToast({
        title: '编辑事件功能开发中',
        icon: 'none'
      })
      // 这里可以跳转到编辑事件页面
      // uni.navigateTo({
      //   url: `/pages/edit-event/edit-event?timelineId=${this.timeline.id}&eventId=${event.id}`
      // })
    },
    
    // 删除事件
    deleteEvent(event) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这个事件吗？',
        success: (res) => {
          if (res.confirm) {
            // 模拟删除事件
            this.events = this.events.filter(item => item.id !== event.id)
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
          }
        }
      })
    }
  },
  onLoad(options) {
    // 可根据 options.id 拉取 timeline 详情和事件
  },
}
</script>

<style scoped>
.timeline-detail-page {
  min-height: 100vh;
  background: #f7fafd;
  position: relative;
}

/* 固定区域样式 */
.fixed-area {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: #f7fafd;
  z-index: 10;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  padding-top: 44px; /* 增加顶部内边距，避免被状态栏遮挡 */
}

.header {
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx 24rpx 24rpx; /* 减少顶部内边距 */
}
.back-btn {
  font-size: 40rpx;
  color: #338aff;
  margin-right: 24rpx;
  font-weight: bold;
  cursor: pointer;
}
.header-info {
  display: flex;
  flex-direction: column;
}
.main-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #222;
  margin-bottom: 8rpx;
}
.sub-title {
  font-size: 26rpx;
  color: #6b7a8f;
}

/* 操作栏样式 */
.action-bar {
  display: flex;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  padding-bottom: 24rpx;
}
.sort-btn, .add-event-btn {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 40rpx;
  padding: 12rpx 24rpx;
  font-size: 24rpx;
  color: #338aff;
  box-shadow: 0 2rpx 8rpx 0 rgba(51,138,255,0.15);
}
.sort-icon, .add-icon {
  margin-right: 8rpx;
  font-weight: bold;
}
.add-event-btn {
  background: #338aff;
  color: #fff;
}

/* 可滚动区域样式 */
.scrollable-area {
  position: absolute;
  top: calc(180rpx + 44px); /* 根据固定区域高度调整，包括额外的顶部内边距 */
  left: 0;
  right: 0;
  bottom: 0;
  padding-bottom: 40rpx;
}

/* 时间轴样式 */
.event-list {
  padding: 0 24rpx;
  position: relative;
  padding-top: 20rpx;
}
.timeline-line {
  position: absolute;
  left: 40rpx;
  top: 20rpx;
  bottom: 20rpx;
  width: 4rpx;
  background: #338aff;
  opacity: 0.6;
  z-index: 1;
}
.event-card {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 32rpx;
  padding: 32rpx 28rpx 24rpx 28rpx;
  box-shadow: 0 4rpx 24rpx 0 rgba(0,0,0,0.06);
  position: relative;
  margin-left: 60rpx;
}
.timeline-dot {
  position: absolute;
  left: -36rpx;
  top: 40rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #338aff;
  border: 4rpx solid #fff;
  box-shadow: 0 0 0 4rpx rgba(51,138,255,0.3);
  z-index: 2;
}
.event-meta {
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 8rpx;
}
.event-meta-dot {
  margin: 0 8rpx;
}
.event-meta-item {
  color: #8f8f94;
}
.event-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 10rpx;
}
.event-desc {
  font-size: 26rpx;
  color: #6b7a8f;
  margin-bottom: 14rpx;
}
.event-images {
  margin-bottom: 14rpx;
}
.event-img-row {
  display: flex;
  flex-direction: row;
  margin-bottom: 8rpx;
}
.event-img {
  width: 32vw;
  height: 120rpx;
  border-radius: 10rpx;
  object-fit: cover;
  margin-right: 8rpx;
}
.event-img:last-child {
  margin-right: 0;
}
.event-tags {
  margin-top: 8rpx;
}
.event-tag {
  font-size: 20rpx;
  color: #338aff;
  background: #eaf3ff;
  border-radius: 10rpx;
  padding: 4rpx 16rpx;
  margin-right: 12rpx;
}

/* 事件操作按钮样式 */
.event-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16rpx;
  gap: 16rpx;
}
.event-action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.7;
  transition: opacity 0.2s;
}
.event-action-btn:active {
  opacity: 1;
}
.edit-btn {
  color: #338aff;
}
.delete-btn {
  color: #ff3366;
}

/* 定位图标样式 */
.location-icon {
  display: inline-flex;
  align-items: center;
  margin-right: 4rpx;
  vertical-align: middle;
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
  max-height: 80vh;
  background: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 24rpx 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
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
  overflow-y: auto;
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

.form-input, .form-textarea, .form-select, .form-date-picker {
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

.upload-area {
  width: 100%;
  height: 200rpx;
  border: 2rpx dashed #ddd;
  border-radius: 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f9f9f9;
}

.upload-icon {
  font-size: 60rpx;
  color: #ccc;
  margin-bottom: 10rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999;
}

.image-url-input {
  margin-top: 16rpx;
  display: flex;
  align-items: center;
}

.btn-add-url {
  width: 120rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background: #338aff;
  color: #fff;
  font-size: 24rpx;
  border-radius: 8rpx;
  margin-left: 16rpx;
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