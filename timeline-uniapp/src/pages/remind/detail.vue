<!-- pages/remind/detail.vue -->
<template>
  <view class="detail-page">
    <!-- 顶部导航 -->
    <u-navbar
        placeholder
        safeAreaInsetTop
        title="提醒详情"
        :bgColor="'#ffffff'"
        :titleStyle="{fontWeight:600,fontSize:'18px'}"
    >
      <template #left>
        <u-icon name="arrow-left" size="22" @click="goBack" />
      </template>
      <template #right>
        <view class="nav-right">
          <u-icon name="more-dot-fill" size="22" />
          <u-icon name="clock" size="22" style="margin-left: 16rpx;" />
        </view>
      </template>
    </u-navbar>

    <!-- 内容卡片 -->
    <view class="detail-card">
      <!-- 标题 + 一键复制 -->
      <view class="detail-header">
        <view class="detail-title">
          {{ reminder.title || '未设置标题' }}
        </view>
        <view class="copy-btn" @click="handleCopy">
          <u-icon name="file-text" size="18" color="#11b668" />
          <text class="copy-text">一键复制</text>
        </view>
      </view>

      <!-- 分割线 -->
      <view class="divider"></view>

      <!-- 行：提醒时间 -->
      <view class="detail-row">
        <text class="row-label">提醒时间</text>
        <text class="row-value">{{ remindTimeLabel }}</text>
      </view>

      <!-- 行：提醒状态（当前时间 > 提醒时间：已提醒，否则：待提醒） -->
      <view class="detail-row">
        <text class="row-label">提醒状态</text>
        <text
            class="row-value status-text"
            :class="isReminded ? 'status-done' : 'status-pending'"
        >
          {{ remindStatusText }}
        </text>
      </view>

      <!-- 行：创建时间 -->
      <view class="detail-row">
        <text class="row-label">创建时间</text>
        <text class="row-value">{{ reminder.createTime || '-' }}</text>
      </view>
    </view>

    <!-- 设为完成 -->
    <view class="set-complete-card">
      <view class="set-complete-row">
        <text class="set-complete-label">设为完成</text>
        <u-switch
            v-model="isCompleted"
            activeColor="#11b668"
            size="26"
            @change="onToggleComplete"
        />
      </view>
    </view>

    <!-- 操作按钮：紧挨着设为完成 -->
    <view class="action-row">
      <view class="action-btn" @click="goHome">
        <u-icon name="home" size="20" />
        <text class="action-text">首页</text>
      </view>

      <view class="action-btn action-btn-danger" @click="onDelete">
        <u-icon name="trash" size="20" />
        <text class="action-text-delete">删除</text>
      </view>

      <view class="action-btn" @click="onReRemind">
        <u-icon name="bell" size="20" />
        <text class="action-text">再提醒</text>
      </view>
    </view>

  </view>
</template>

<script>
import { reminderAPI } from '@/utils/api.js';

export default {
  data() {
    return {
      reminderId: null,
      reminder: {
        id: null,
        title: '',
        content: '',
        remindTime: '',
        createTime: ''
      },
      isCompleted: false
    };
  },

  computed: {
    // 当前时间是否已经超过提醒时间
    isReminded() {
      if (!this.reminder.remindTime) return false;
      const t = this.parseDate(this.reminder.remindTime);
      if (!t) return false;
      return Date.now() > t.getTime();
    },

    // 提醒状态文本
    remindStatusText() {
      return this.isReminded ? '已提醒' : '待提醒';
    },

    // 提醒时间展示：今天 12:14 | 周四
    remindTimeLabel() {
      if (!this.reminder.remindTime) return '-';
      return this.formatRemindLabel(this.reminder.remindTime);
    }
  },

  onLoad(options) {
    if (options && options.id) {
      this.reminderId = options.id;
      this.fetchDetail();
    }
  },

  methods: {
    goBack() {
      uni.navigateBack({
        delta: 1
      });
    },

    // 解析时间字符串（兼容 iOS）
    parseDate(str) {
      if (!str) return null;
      const d = new Date(str.replace(/-/g, '/'));
      if (Number.isNaN(d.getTime())) return null;
      return d;
    },

    // 与列表页保持一致的展示格式
    formatRemindLabel(remindTime) {
      const d = this.parseDate(remindTime);
      if (!d) return remindTime || '';

      const now = new Date();
      const isToday =
          d.getFullYear() === now.getFullYear() &&
          d.getMonth() === now.getMonth() &&
          d.getDate() === now.getDate();

      const pad = (n) => (n < 10 ? '0' + n : '' + n);
      const hhmm = `${pad(d.getHours())}:${pad(d.getMinutes())}`;

      const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const week = weekMap[d.getDay()];

      const dayLabel = isToday
          ? '今天'
          : `${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;

      return `${dayLabel} ${hhmm} | ${week}`;
    },

    // 获取详情
    fetchDetail() {
      reminderAPI
          .getReminderDetail(this.reminderId)
          .then((res) => {
            if (res.code === 200) {
              this.reminder = res.data || {};
              // 假设后端 status = 2 表示已完成
              this.isCompleted = this.reminder.status === 2;
            } else {
              uni.showToast({
                title: res.message || '获取提醒详情失败',
                icon: 'none'
              });
            }
          })
          .catch((err) => {
            console.error('获取提醒详情失败:', err);
            uni.showToast({
              title: '获取提醒详情失败',
              icon: 'none'
            });
          });
    },

    // 一键复制：标题 + 内容
    handleCopy() {
      const text =
          (this.reminder.title || '') +
          (this.reminder.content ? '\n' + this.reminder.content : '');
      if (!text) {
        uni.showToast({
          title: '没有可复制的内容',
          icon: 'none'
        });
        return;
      }
      uni.setClipboardData({
        data: text,
        success: () => {
          uni.showToast({
            title: '复制成功',
            icon: 'success'
          });
        }
      });
    },

    // 设为完成 / 取消完成
    onToggleComplete(val) {
      if (!this.reminder.id) return;
      reminderAPI
          .updateReminder({
            id: this.reminder.id,
            status: val ? 2 : 0 // 2：已完成，0：待提醒
          })
          .then((res) => {
            if (res.code === 200) {
              uni.showToast({
                title: val ? '已设为完成' : '已取消完成',
                icon: 'success'
              });
              this.reminder.status = val ? 2 : 0;
            } else {
              this.isCompleted = !val; // 回滚
              uni.showToast({
                title: res.message || '操作失败',
                icon: 'none'
              });
            }
          })
          .catch((err) => {
            console.error('更新提醒状态失败:', err);
            this.isCompleted = !val; // 回滚
            uni.showToast({
              title: '操作失败',
              icon: 'none'
            });
          });
    },

    // 底部：首页
    goHome() {
      uni.switchTab({
        url: '/pages/remind/index'
      });
    },

    // 底部：删除
    onDelete() {
      if (!this.reminder.id) return;
      uni.showModal({
        title: '删除提醒',
        content: '确定删除该提醒吗？',
        success: (res) => {
          if (res.confirm) {
            reminderAPI
                .deleteReminder(this.reminder.id)
                .then((r) => {
                  if (r.code === 200) {
                    uni.showToast({
                      title: '已删除',
                      icon: 'success'
                    });
                    setTimeout(() => {
                      this.goHome();
                    }, 500);
                  } else {
                    uni.showToast({
                      title: r.message || '删除失败',
                      icon: 'none'
                    });
                  }
                })
                .catch((err) => {
                  console.error('删除提醒失败:', err);
                  uni.showToast({
                    title: '删除失败',
                    icon: 'none'
                  });
                });
          }
        }
      });
    },

    // 底部：再提醒（这里你可以打开编辑页重新设置时间）
    // 底部：再提醒（跳转到新增提醒页，并带上当前标题）
    onReRemind() {
      if (!this.reminder || !this.reminder.title) {
        uni.showToast({
          title: '当前提醒没有标题',
          icon: 'none'
        })
        return
      }
      const title = encodeURIComponent(this.reminder.title)
      uni.navigateTo({
        // 不带 id，走“新增”逻辑，只把 title 预填
        url: `/pages/remind/create-remind?title=${title}`
      })
    }
  }
};
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background-color: #f6f7fb;
  padding-bottom: 120rpx;
}

.nav-right {
  display: flex;
  align-items: center;
  padding-right: 16rpx;
}

/* 主卡片 */
.detail-card {
  margin: 24rpx;
  padding: 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.03);
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.detail-title {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: #111111;
  padding-right: 24rpx;
  word-break: break-all;
}

.copy-btn {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  font-size: 24rpx;
  color: #11b668;
}

.copy-text {
  margin-left: 8rpx;
}

.divider {
  margin: 24rpx 0 8rpx;
  height: 1rpx;
  background-color: #f1f1f1;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 0;
  font-size: 26rpx;
}

.row-label {
  color: #999999;
}

.row-value {
  color: #333333;
}

/* 状态颜色 */
.status-text {
  font-weight: 500;
}

.status-done {
  color: #11b668;
}

.status-pending {
  color: #ffaa00;
}

/* 设为完成 */
.set-complete-card {
  margin: 0 24rpx;
  margin-top: 16rpx;
  padding: 24rpx 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
}

.set-complete-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.set-complete-label {
  font-size: 28rpx;
  color: #333333;
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding-bottom: env(safe-area-inset-bottom);
  height: 120rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #f1f1f1;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.bottom-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 22rpx;
}

.bottom-text {
  margin-top: 6rpx;
  color: #666666;
}

.bottom-delete .bottom-text {
  color: #ff4d4f;
}

/* 操作按钮行：紧挨着设为完成 */
.action-row {
  margin: 16rpx 24rpx 24rpx;
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  border-radius: 999rpx;
  background-color: #ffffff;
  border: 1rpx solid #e5e5e5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  color: #333333;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
}

.action-btn-danger {
  color: #ff4d4f;
  border-color: #ffebe8;
  background-color: #fff6f5;
}

.action-text {
  margin-left: 8rpx;
  color: #11b668;
}

.action-text-delete {
  margin-left: 8rpx;
  color: #ec092c;
}
</style>

