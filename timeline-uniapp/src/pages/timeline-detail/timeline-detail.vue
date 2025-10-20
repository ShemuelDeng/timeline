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
              <image v-for="(img, imgIndex) in row" :key="img" :src="img" class="event-img" mode="aspectFill" @click="previewImage(event.images, imgIndex)" />
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
    <u-popup :show="showModal" mode="center" round="16" :closeable="true" @close="hideAddEventModal" :z-index="10">
      <view class="popup-content">
        <view class="popup-title">添加新事件</view>
        <view class="form-container">
          <u-form :model="newEvent" ref="uForm" label-position="top" label-width="auto">
            <u-form-item label="事件标题" prop="title">
              <u-input v-model="newEvent.title" placeholder="例如：百天" border="bottom" />
            </u-form-item>
            <u-form-item label="描述" prop="desc">
              <u-textarea v-model="newEvent.desc" placeholder="简单描述这个事件的内容" height="160" />
            </u-form-item>
            <u-form-item label="日期" prop="date">
              <u-datetime-picker
                v-model="newEvent.date"
                mode="date"
                :show-toolbar="true"
                @confirm="dateConfirm"
              >
                <u-input
                  slot="trigger"
                  v-model="newEvent.date"
                  disabled
                  placeholder="请选择日期"
                  suffixIcon="calendar"
                  border="bottom"
                />
              </u-datetime-picker>
            </u-form-item>
            <u-form-item label="地点" prop="place">
              <u-input v-model="newEvent.place" placeholder="例如：上海市人民公园" border="bottom" />
            </u-form-item>
            <u-form-item label="分类标签" prop="tag">
              <u-picker
                :show="showTagPicker"
                :columns="[tags]"
                @confirm="tagConfirm"
                @cancel="showTagPicker = false"
                keyName="text"
              ></u-picker>
              <u-input
                v-model="newEvent.tag"
                disabled
                placeholder="请选择标签"
                suffixIcon="arrow-right"
                border="bottom"
                @click="showTagPicker = true"
              />
            </u-form-item>
            <u-form-item label="图片 (可选)">
              <view class="upload-area" @click="addImage">
                <u-icon name="plus" size="40" color="#ccc"></u-icon>
                <text class="upload-text">点击图片区域上传</text>
              </view>
              <view class="image-preview" v-if="newEvent.images.length > 0">
                <view class="image-item" v-for="(img, index) in newEvent.images" :key="index">
                  <image :src="img" mode="aspectFill" class="preview-img" />
                  <view class="delete-icon" @click.stop="removeImage(index)">
                    <u-icon name="close" size="20" color="#fff"></u-icon>
                  </view>
                </view>
              </view>
            </u-form-item>
          </u-form>
        </view>
        <view class="form-actions">
          <u-button type="info" text="取消" plain shape="circle" @click="hideAddEventModal"></u-button>
          <u-button type="primary" text="添加" shape="circle" @click="createEvent"></u-button>
        </view>
      </view>
    </u-popup>
    
    <!-- 图片预览组件 -->
    <u-popup :show="showImagePreview" mode="center" :round="0" @close="closeImagePreview" :closeable="true" :z-index="100" :custom-style="{width: '100vw', height: '100vh', overflow: 'hidden', background: '#000', borderRadius: 0}">
      <view class="image-preview-container">
        <swiper class="preview-swiper" :current="currentImageIndex" @change="handleSwiperChange">
          <swiper-item v-for="(img, index) in previewImages" :key="index">
            <movable-area class="movable-area">
              <movable-view class="movable-view" 
                direction="all" 
                :scale="true" 
                :scale-min="1" 
                :scale-max="4" 
                :scale-value="imageScale" 
                @scale="onImageScale"
                @change="onImageMove">
                <image :src="img" mode="aspectFit" class="preview-swiper-img" @doubletap="resetImageScale" />
              </movable-view>
            </movable-area>
          </swiper-item>
        </swiper>
        <view class="preview-indicator">{{ currentImageIndex + 1 }}/{{ previewImages.length }}</view>
        <view class="preview-close" @click="closeImagePreview">
          <text class="close-icon">×</text>
        </view>
      </view>
    </u-popup>

  </view>
</template>

<script>
import { eventAPI, fileAPI, API_BASE_URL } from '../../utils/api.js';

export default {
  data() {
    return {
      timelineId: null,
      loading: false,
      showModal: false,
      showTagPicker: false,
      newEvent: {
        title: '',
        desc: '',
        date: this.formatDate(new Date()),
        place: '',
        tag: '重要时刻',
        images: [],
        imagesFileKeys: [],
        imageUrl: ''
      },
      tagIndex: 0,
      tags: [
        { text: '重要时刻', value: '重要时刻' },
        { text: '里程碑', value: '里程碑' },
        { text: '成长', value: '成长' },
        { text: '学习', value: '学习' },
        { text: '游玩', value: '游玩' },
        { text: '随笔', value: '随笔' },
        { text: '灵感', value: '灵感' },
        { text: '其他', value: '其他' }
      ],
      timeline: {
        id: null,
        title: '',
        desc: ''
      },
      events: [],
      sortAscending: true, // 默认正序排列（从早到晚）
      // 图片预览相关
      showImagePreview: false,
      previewImages: [],
      currentImageIndex: 0,
      imageScale: 1
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
    // 获取时间轴下的事件列表
    fetchEventList(timelineId) {
      this.loading = true;
      uni.showLoading({
        title: '加载中...'
      });
      
      eventAPI.getEventList(timelineId)
        .then(res => {
          if (res.code === 200) {
            // 设置时间轴信息
            if (res.data && res.data.timeline) {
              this.timeline = {
                id: res.data.timeline.id,
                title: res.data.timeline.title,
                desc: res.data.timeline.description || ''
              };
            }
            
            // 处理事件列表数据
            if (res.data && res.data.page.records) {
              this.events = res.data.page.records.map(item => {
                // 计算时间差
                const eventDate = new Date(item.eventTime);
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
                
                return {
                  id: item.id,
                  title: item.title,
                  date: formattedDate,
                  timeAgo: timeAgo,
                  place: item.location || '未知地点',
                  desc: item.content || '暂无描述',
                  tags: item.tag ? [item.tag] : ['其他'],
                  images: item.images || []
                };
              });
            }
          } else {
            uni.showToast({
              title: res.message || '获取事件列表失败',
              icon: 'none'
            });
          }
        })
        .catch(err => {
          console.error('获取事件列表失败:', err);
          uni.showToast({
            title: '获取事件列表失败',
            icon: 'none'
          });
        })
        .finally(() => {
          this.loading = false;
          uni.hideLoading();
        });
    },
    
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
      this.showTagPicker = false;
      
      // 如果有表单校验，重置表单校验状态
      if (this.$refs.uForm) {
        this.$refs.uForm.resetFields();
      }
    },
    
    dateConfirm(e) {
      // u-datetime-picker的确认事件
      this.newEvent.date = this.formatDate(new Date(e));
    },
    
    tagConfirm(e) {
      // u-picker的确认事件
      this.showTagPicker = false;
      this.newEvent.tag = e[0].value;
    },
    
    addImage() {
      // 显示操作菜单，选择图片来源
      uni.showActionSheet({
        itemList: ['从相册选择', '拍照'],
        popover: {
          // 设置更高的z-index，确保在弹窗上方显示
          style: { zIndex: 999 }
        },
        success: (res) => {
          // 从相册选择或拍照
          uni.chooseImage({
            count: 9 - this.newEvent.images.length, // 最多可选择的图片数量
            sizeType: ['compressed'], // 压缩图
            sourceType: res.tapIndex === 0 ? ['album'] : ['camera'],
            success: (res) => {
              // 显示上传中提示
              uni.showLoading({
                title: '上传中...'
              });
              
              // 循环上传图片到后端
              const tempFilePaths = res.tempFilePaths;
              const uploadPromises = tempFilePaths.map(filePath => {
                return fileAPI.uploadFile(filePath)
                  .then(fileUrl => {
                    // 上传成功，返回文件URL
                    return fileUrl;
                  })
                  .catch(err => {
                    throw new Error(err.message || '上传失败');
                  });
              });
              
              // 处理所有上传结果
              Promise.all(uploadPromises)
                .then(filePaths => {
                  // 将上传成功的文件路径添加到图片数组
                  filePaths.forEach(tempFileData => {
                    this.newEvent.images.push(tempFileData.fileUrl)
                    this.newEvent.imagesFileKeys.push(tempFileData.filePath)
                  })
                  uni.hideLoading();
                  uni.showToast({
                    title: '图片上传成功',
                    icon: 'success'
                  });
                })
                .catch(err => {
                  console.error('图片上传失败:', err);
                  uni.hideLoading();
                  uni.showToast({
                    title: '图片上传失败',
                    icon: 'none'
                  });
                });
            }
          });
        }
      });
    },
    

    
    // 删除图片
    removeImage(index) {
      this.newEvent.images.splice(index, 1);
      this.newEvent.imagesFileKeys.splice(index, 1);
      uni.showToast({
        title: '已删除',
        icon: 'none'
      });
    },
    
    // 预览图片
    previewImage(images, index) {
      this.previewImages = images;
      this.currentImageIndex = index;
      this.showImagePreview = true;
    },
    
    // 关闭图片预览
    closeImagePreview() {
      this.showImagePreview = false;
    },
    
    // 处理滑动切换
    handleSwiperChange(e) {
      this.currentImageIndex = e.detail.current;
      // 重置缩放比例
      this.imageScale = 1;
    },
    
    // 处理图片缩放
    onImageScale(e) {
      this.imageScale = e.detail.scale;
    },
    
    // 处理图片移动
    onImageMove(e) {
      // 可以在这里处理图片移动的逻辑
    },
    
    // 双击重置缩放
    resetImageScale() {
      this.imageScale = 1;
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
      
      
      // 显示加载中
      uni.showLoading({
        title: '创建中...'
      });
      
      // 准备请求数据
      const data = {
        timelineId: this.timelineId,
        title: this.newEvent.title,
        content: this.newEvent.desc || '',
        isRich: 1,
        tag: this.newEvent.tag,
        location: this.newEvent.place,
        images: this.newEvent.imagesFileKeys,
        eventTime: this.newEvent.date.replace(/\//g, '-') + ' 00:00:00'
      };
      
      // 调用API创建事件
      eventAPI.addEvent(data)
        .then(res => {
          if (res.code === 200) {
            // 创建成功，刷新列表
            this.hideAddEventModal();
            this.fetchEventList(this.timelineId);
            
            uni.showToast({
              title: '添加成功',
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
          console.error('创建事件失败:', err);
          uni.showToast({
            title: '创建失败',
            icon: 'none'
          });
        })
        .finally(() => {
          uni.hideLoading();
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
    if (options && options.id) {
      this.timelineId = options.id;
      this.fetchEventList(this.timelineId);
    } else {
      uni.showToast({
        title: '缺少时间轴ID',
        icon: 'none'
      });
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
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
  left: 44rpx; /* 调整位置以对齐节点圆圈 */
  top: 20rpx;
  bottom: 20rpx;
  width: 4rpx;
  background: #338aff;
  opacity: 0.6;
  z-index: 1;
}
.timeline-dot {
  position: absolute;
  left: 40rpx; /* 调整位置以确保时间轴线贯穿 */
  top: 40rpx;
  width: 16rpx;
  height: 16rpx;
  border-radius: 50%;
  background: #338aff;
  border: 4rpx solid #fff;
  box-shadow: 0 0 0 4rpx rgba(51,138,255,0.3);
  z-index: 2;
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
/* 图片预览样式 */
.image-preview-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  background: #000;
  overflow: hidden;
}
.preview-swiper {
  width: 100%;
  height: 100%;
}
.preview-swiper-img {
  width: 100%;
  height: 100%;
}
.movable-area {
  width: 100%;
  height: 100%;
  position: relative;
}
.movable-view {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.preview-indicator {
  position: absolute;
  bottom: 20rpx;
  left: 0;
  right: 0;
  text-align: center;
  color: #fff;
  font-size: 24rpx;
  background: rgba(0, 0, 0, 0.3);
  padding: 6rpx 0;
}
.preview-close {
  position: absolute;
  top: 40rpx;
  right: 40rpx;
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
  z-index: 101;
}
.close-icon {
  color: #fff;
  font-size: 40rpx;
  font-weight: bold;
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
.popup-content {
  width: 650rpx;
  padding: 30rpx;
  box-sizing: border-box;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.form-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  text-align: center;
  margin-bottom: 30rpx;
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
  margin-bottom: 20rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999;
  margin-top: 10rpx;
}

.image-preview {
  display: flex;
  flex-wrap: wrap;
  margin-top: 20rpx;
}

.image-item {
  width: 180rpx;
  height: 180rpx;
  margin-right: 15rpx;
  margin-bottom: 15rpx;
  position: relative;
  border-radius: 8rpx;
  overflow: hidden;
}

.image-item:nth-child(3n) {
  margin-right: 0;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-icon {
  position: absolute;
  top: 5rpx;
  right: 5rpx;
  width: 40rpx;
  height: 40rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30rpx;
  width: 100%;
  padding-bottom: 10rpx;
}

/* 图片URL输入弹窗样式 */
.url-popup-content {
  width: 600rpx;
  padding: 30rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.url-popup-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30rpx;
  width: 100%;
}
</style>