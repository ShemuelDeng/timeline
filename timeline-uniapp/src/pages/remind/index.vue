<!-- pages/remind/index.vue -->
<template>
  <view class="page">
    <u-navbar
        placeholder
        safeAreaInsetTop
        title="我的提醒"
        :bgColor="'#ffffff'"
        :titleStyle="{fontWeight:600,fontSize:'18px'}"
    >
      <template #left><u-icon name="list" size="22" /></template>
      <template #right>
        <view class="nav-right">
          <u-button shape="circle" size="small" :hairline="false" text="···" class="ghost-btn" />
          <u-button shape="circle" size="small" :hairline="false" icon="scan" class="ghost-btn" />
        </view>
      </template>
    </u-navbar>

    <!-- tabs -->
    <view class="tabs-row">
      <u-tabs
          v-model:current="current"
          :list="tabs"
          @change="onTabChange"
          lineColor="#11b668"
          :activeStyle="{color:'#11b668',fontWeight:600}"
          :inactiveStyle="{color:'#666'}"
          itemStyle="height:44px;padding:0 14px;"
      />
      <u-icon name="account" color="#11b668" size="20" class="group-icon" />
    </view>

    <!-- 列表 / 空状态 -->
    <view v-if="remindList && remindList.length" class="remind-list">
      <view
          v-for="item in remindList"
          :key="item.id"
          class="remind-card"
      >
        <view class="remind-card-top">
          <text class="remind-title">{{ item.title }}</text>
          <text
              class="more-btn"
              @click.stop="openActionSheet(item)"
          >
            ...
          </text>
        </view>

        <view class="remind-card-bottom">
          <u-icon name="clock" size="18" color="#11b668" />
          <text class="remind-time-text">{{ formatRemindLabel(item.remindTime) }}</text>
        </view>
      </view>
    </view>

    <view v-else class="empty-wrap">
      <view class="empty-illus">
        <u-icon name="clock" size="48" color="#cfcfcf" />
      </view>
      <view class="empty-title">暂无待提醒事项</view>
      <view class="empty-sub">点击右下角按钮创建第一个提醒吧</view>
    </view>

    <!-- 悬浮按钮区域 -->
    <view class="fab-area">
      <!-- 创建提醒：加号 -->
      <u-button
          type="success"
          shape="circle"
          :customStyle="{
          width:'64rpx',
          height:'64rpx',
          boxShadow:'0 6rpx 18rpx rgba(17,182,104,.35)'
        }"
          @click="handleCreateRemind"
      >
        <u-icon name="plus" color="#ffffff" size="28" />
      </u-button>

      <!-- 语音按钮 -->
      <u-button
          type="success"
          shape="circle"
          :customStyle="{
          width:'64rpx',
          height:'64rpx',
          marginTop:'28rpx',
          boxShadow:'0 6rpx 18rpx rgba(17,182,104,.35)'
        }"
      >
        <u-icon name="mic" color="#ffffff" size="26" />
      </u-button>
    </view>

    <!-- ===== 授权弹窗 ===== -->
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
    <!-- ===== 授权弹窗 END ===== -->

    <!-- ===== 底部操作弹窗（修改/完成/删除/取消） ===== -->
    <u-popup
        :show="showActionSheet"
        mode="bottom"
        round="20"
        @close="closeActionSheet"
        :safeAreaInsetBottom="true"
    >
      <view class="sheet-wrap">
        <view class="sheet-title">
          {{ selectedReminder ? selectedReminder.title : '' }}
        </view>

        <view class="sheet-item" @click="onEditReminder">
          <text class="sheet-emoji">📝</text>
          <text class="sheet-text">修改</text>
        </view>

        <view class="sheet-item" @click="onCompleteReminder">
          <text class="sheet-emoji">✅</text>
          <text class="sheet-text">完成</text>
        </view>

        <view class="sheet-item sheet-item-danger" @click="onDeleteReminder">
          <text class="sheet-emoji">🗑</text>
          <text class="sheet-text">删除</text>
        </view>

        <view class="sheet-cancel" @click="closeActionSheet">
          取消
        </view>
      </view>
    </u-popup>
    <!-- ===== 底部操作弹窗 END ===== -->
  </view>
</template>

<script>
import { authAPI, reminderAPI } from '@/utils/api.js';

export default {
  data() {
    return {
      // 顶部 tabs
      tabs: [
        { name: '待提醒' },
        { name: '已过期' },
        { name: '已完成' }
      ],
      current: 0,

      // 授权相关状态
      showAuthModal: false,
      isLoggedIn: false,
      userInfo: null,

      // 提醒数据
      remindList: [],
      loading: false,

      // 当前选择的提醒 & 操作弹窗
      showActionSheet: false,
      selectedReminder: null
    }
  },

  onLoad() {
    const token = uni.getStorageSync('token');
    console.log('remind token', token);
    if (token) {
      this.isLoggedIn = true;
      this.userInfo = uni.getStorageSync('userInfo') || null;
      this.fetchRemindList && this.fetchRemindList();
    } else {
      setTimeout(() => {
        this.showAuthModal = true;
      }, 500);
    }
  },

  onShow() {
    // 如果已登录，刷新提醒列表
    if (this.isLoggedIn) {
      this.fetchRemindList();
    }
  },

  methods: {
    // tab 切换（目前只是切 tab，后续可以根据 current 过滤/请求）
    onTabChange(e) {
      const idx = typeof e === 'number' ? e : e?.index;
      if (typeof idx === 'number') this.current = idx;
      // TODO: 根据 current 加载不同状态列表（待提醒 / 已过期 / 已完成）
    },

    // 格式化提醒时间 => “今天 11:55 | 周四”
    formatRemindLabel(remindTime) {
      if (!remindTime) return '';
      const d = new Date(remindTime.replace(/-/g, '/'));
      if (Number.isNaN(d.getTime())) return remindTime;

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

    // 显示授权弹窗
    showAuthPopup() {
      this.showAuthModal = true;
    },

    // 隐藏授权弹窗
    hideAuthPopup() {
      this.showAuthModal = false;
    },

    // 授权登录
    handleAuth() {
      uni.showLoading({
        title: '登录中...'
      });

      authAPI.login()
          .then(res => {
            if (res.code === 200) {
              this.isLoggedIn = true;
              this.userInfo = res.data.userInfo || {
                nickname: 'shemuel',
                avatar: '/static/avatar.svg'
              };

              uni.setStorageSync('token', res.data.tokenValue || 'mock-token');
              uni.setStorageSync('userInfo', this.userInfo);

              this.hideAuthPopup();
              this.fetchRemindList && this.fetchRemindList();

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

    // 获取提醒列表
    fetchRemindList() {
      this.loading = true;
      reminderAPI.getReminderList()
          .then(res => {
            if (res.code === 200) {
              const page = res.data || {};
              this.remindList = page.records || [];
              console.log('remindList', this.remindList);
            } else {
              uni.showToast({
                title: res.message || '获取提醒列表失败',
                icon: 'none'
              });
            }
          })
          .catch(err => {
            console.error('获取提醒列表失败:', err);
            uni.showToast({
              title: '获取提醒列表失败',
              icon: 'none'
            });
          })
          .finally(() => {
            this.loading = false;
          });
    },

    // 新建提醒
    handleCreateRemind() {
      if (!this.isLoggedIn) {
        this.showAuthPopup();
        return;
      }
      uni.navigateTo({
        url: '/pages/remind/create-remind'
      });
    },

    // 打开底部操作弹窗
    openActionSheet(item) {
      this.selectedReminder = item;
      this.showActionSheet = true;
    },

    // 关闭底部操作弹窗
    closeActionSheet() {
      this.showActionSheet = false;
      this.selectedReminder = null;
    },

    // 修改（先跳到创建页，带上 id；你后续在创建页里做编辑逻辑）
    onEditReminder() {
      if (!this.selectedReminder) return;
      this.showActionSheet = false;
      uni.navigateTo({
        url: `/pages/remind/create-remind?id=${this.selectedReminder.id}`
      });
    },

    // 完成（这里假设 isActive = 0 表示已完成，具体你可以再调整）
    onCompleteReminder() {
      if (!this.selectedReminder) return;
      const id = this.selectedReminder.id;
      reminderAPI.updateReminder({
        id,
        isActive: 0
      }).then(res => {
        if (res.code === 200) {
          uni.showToast({
            title: '已完成',
            icon: 'success'
          });
          this.closeActionSheet();
          this.fetchRemindList();
        } else {
          uni.showToast({
            title: res.message || '操作失败',
            icon: 'none'
          });
        }
      }).catch(err => {
        console.error('完成提醒失败:', err);
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        });
      });
    },

    // 删除
    onDeleteReminder() {
      if (!this.selectedReminder) return;
      const id = this.selectedReminder.id;

      // 1. 先关闭底部操作弹窗，避免遮挡
      this.showActionSheet = false;

      // 2. 等弹窗关闭动画结束后再弹出确认框
      setTimeout(() => {
        uni.showModal({
          title: '删除提醒',
          content: '确定删除该提醒吗？',
          success: (res) => {
            if (res.confirm) {
              reminderAPI.deleteReminder(id)
                  .then(r => {
                    if (r.code === 200) {
                      uni.showToast({
                        title: '已删除',
                        icon: 'success'
                      });
                      this.fetchRemindList();
                    } else {
                      uni.showToast({
                        title: r.message || '删除失败',
                        icon: 'none'
                      });
                    }
                  })
                  .catch(err => {
                    console.error('删除提醒失败:', err);
                    uni.showToast({
                      title: '删除失败',
                      icon: 'none'
                    });
                  });
            }
          }
        });
      }, 200);
    }
  }
}
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; position: relative; background:#f7f7f7; }

.nav-right { display:flex; gap:12rpx; }
.ghost-btn { background:#f3f4f6; color:#333; padding:0 12rpx; box-shadow:0 2rpx 8rpx rgba(0,0,0,.06); }
.tabs-row { display:flex; align-items:center; padding:0 16rpx; background:#fff; border-bottom:1rpx solid #f0f0f0; }
.group-icon { margin-left:auto; padding:0 12rpx; }

/* 列表样式 */
.remind-list {
  padding: 16rpx 24rpx 160rpx;
}

.remind-card {
  background:#ffffff;
  border-radius:24rpx;
  padding:20rpx 24rpx;
  margin-bottom:16rpx;
  box-shadow:0 6rpx 20rpx rgba(0,0,0,0.06);
}

.remind-card-top {
  display:flex;
  align-items:center;
  justify-content:space-between;
  margin-bottom:12rpx;
}

.remind-title {
  font-size:30rpx;
  font-weight:500;
  color:#333;
}

.remind-card-bottom {
  display:flex;
  align-items:center;
  margin-top:4rpx;
}

.remind-time-text {
  margin-left:8rpx;
  font-size:24rpx;
  color:#666;
}

/* 空状态 */
.empty-wrap { height:60vh; display:flex; flex-direction:column; align-items:center; justify-content:center; color:#9aa1a7; }
.empty-illus { width:360rpx; height:360rpx; border-radius:360rpx; background:radial-gradient(closest-side,#f4f7f5,#ebf4ef); display:flex; align-items:center; justify-content:center; margin-bottom:28rpx; }
.empty-title { color:#555; font-size:32rpx; margin-bottom:10rpx; }
.empty-sub { color:#b6bec6; font-size:26rpx; }

/* 悬浮按钮 */
.fab-area {
  position:fixed;
  right:32rpx;
  bottom:140rpx;
  display:flex;
  flex-direction:column;
  z-index:999;
}

/* 授权弹窗样式（保持原样） */
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
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.auth-header { padding: 30rpx; border-bottom: 1rpx solid #eee; }

.auth-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  text-align: center;
  display: block;
}

.auth-body { padding: 40rpx 30rpx; }

.auth-avatar {
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 30rpx;
  border-radius: 50%;
  overflow: hidden;
  border: 1rpx solid #eee;
}

.auth-avatar image { width: 100%; height: 100%; }

.auth-info { text-align: center; margin-bottom: 40rpx; }

.auth-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #222;
  margin-bottom: 12rpx;
}

.auth-desc { font-size: 26rpx; color: #6b7a8f; }

.auth-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 40rpx;
}

.auth-cancel, .auth-confirm {
  width: 45%;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.auth-cancel { background: #f5f5f5; color: #666; }
.auth-confirm { background: #338aff; color: #fff; }

/* 底部操作弹窗 */
.sheet-wrap {
  background:#f5f5f5;
  padding:16rpx 0 24rpx;
}

.sheet-title {
  background:#ffffff;
  margin:0 24rpx;
  padding:24rpx 0;
  text-align:center;
  font-size:28rpx;
  color:#333;
  border-bottom:1rpx solid #f0f0f0;
}

.sheet-item {
  background:#ffffff;
  margin:0 24rpx;
  padding:24rpx 32rpx;
  display:flex;
  align-items:center;
  border-top:1rpx solid #f5f5f5;
}

.sheet-emoji {
  font-size:32rpx;
  margin-right:16rpx;
}

.sheet-text {
  font-size:28rpx;
  color:#333;
}

.sheet-item-danger .sheet-text {
  color:#e34d3d;
}

.sheet-cancel {
  margin:16rpx 24rpx 0;
  background:#ffffff;
  text-align:center;
  padding:24rpx 0;
  border-radius:16rpx;
  font-size:28rpx;
  color:#333;
}
.more-btn {
  font-size: 36rpx;
  color: #999;
  padding: 0 10rpx;
}

</style>
