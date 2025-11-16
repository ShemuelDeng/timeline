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
    </u-navbar>

    <!-- tabs -->
    <view class="tabs-row">
      <!-- 三条杠按钮 -->
      <u-icon
          name="list"
          size="22"
          color="#333"
          class="tabs-menu"
          @click="openSideMenu"
      />

      <u-tabs
          v-model:current="current"
          :list="tabs"
          @change="onTabChange"
          lineColor="#11b668"
          :activeStyle="{color:'#11b668',fontWeight:600}"
          :inactiveStyle="{color:'#666'}"
          itemStyle="height:44px;padding:0 14px;"
      />
    </view>

    <!-- ☆ 中间自适应区域，只在这里滚动 -->
    <view class="main">
      <view class="scroll-area">
        <!-- 列表 / 空状态：v-if / v-else 必须紧挨着 -->
        <view v-if="remindList && remindList.length" class="remind-list">
          <view
              v-for="item in remindList"
              :key="item.id"
              class="remind-card"
              @click="goDetail(item)"
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
              <text class="remind-time-text">
                {{ formatRemindLabel(item.remindTime) }}
              </text>
            </view>
          </view>
        </view>

        <view v-else class="empty-wrap">
          <view class="empty-illus">
            <u-icon name="clock" size="48" color="#cfcfcf" />
          </view>
          <view class="empty-title">{{ emptyTitle }}</view>
          <view class="empty-sub">点击右下角按钮创建第一个提醒吧</view>
        </view>
      </view>
    </view>

    <!-- 悬浮按钮区域（fixed，不参与高度计算） -->
    <view class="fab-area">
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

    <!-- ===== 授权弹窗（仅 H5 使用） ===== -->
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

    <!-- ===== 侧边菜单（左侧抽屉） ===== -->
    <u-popup
        :show="showSideMenu"
        mode="left"
        :round="0"
        :safeAreaInsetTop="false"
        :safeAreaInsetBottom="false"
        :overlay="true"
        :overlayStyle="{ background: 'rgba(0,0,0,0.35)' }"
        @close="closeSideMenu"
    >
      <view class="side-panel">
        <!-- 顶部日期 -->
        <view class="side-date">
          {{ todayLabel }}
        </view>

        <!-- 功能菜单 -->
        <view class="side-menu">
          <view class="side-item" @click="goReminderSearch">
            <view class="side-item-left">
              <u-icon name="search" size="22" color="#10b769" />
              <text class="side-item-text">提醒搜索</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>

          <view class="side-item" @click="goReminderCalendar">
            <view class="side-item-left">
              <u-icon name="calendar" size="22" color="#10b769" />
              <text class="side-item-text">提醒日历</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>

          <view class="side-item" @click="goReminderTemplate">
            <view class="side-item-left">
              <u-icon name="grid" size="22" color="#10b769" />
              <text class="side-item-text">提醒模板</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>

          <view class="side-item" @click="goHelp">
            <view class="side-item-left">
              <u-icon name="question-circle" size="22" color="#10b769" />
              <text class="side-item-text">使用帮助</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>
        </view>

        <!-- 清空分组标题 -->
        <view class="side-section-title">
          ↓ 清空（不可恢复）
        </view>

        <!-- 清空相关菜单 -->
        <view class="side-menu">
          <view class="side-item" @click="clearAll">
            <view class="side-item-left">
              <u-icon name="trash" size="22" color="#10b769" />
              <text class="side-item-text">清空所有</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>

          <view class="side-item" @click="clearPending">
            <view class="side-item-left">
              <u-icon name="trash" size="22" color="#10b769" />
              <text class="side-item-text">清空待提醒</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>

          <view class="side-item" @click="clearExpired">
            <view class="side-item-left">
              <u-icon name="trash" size="22" color="#10b769" />
              <text class="side-item-text">清空已过期</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>

          <view class="side-item" @click="clearCompleted">
            <view class="side-item-left">
              <u-icon name="trash" size="22" color="#10b769" />
              <text class="side-item-text">清空已完成</text>
            </view>
            <u-icon name="arrow-right" size="18" color="#cfcfcf" />
          </view>
        </view>
      </view>
    </u-popup>
    <!-- ===== 侧边菜单 END ===== -->

  </view>
</template>

<script>
import { authAPI, reminderAPI } from '@/utils/api.js'

// uni-app 编译时会注入具体平台：h5 / mp-weixin / app 等
const PLATFORM = process.env.UNI_PLATFORM

export default {
  data() {
    return {
      // 顶部 tabs
      tabs: [
        { name: '待提醒', status: 0 },
        { name: '已过期', status: 1 },
        { name: '已完成', status: 2 }
      ],
      current: 0,

      // 授权相关
      showAuthModal: false,   // 控制你那个自定义弹窗
      isLoggedIn: false,
      userInfo: null,

      // 提醒数据
      remindList: [],
      loading: false,

      // 当前选择的提醒 & 弹窗
      showActionSheet: false,
      selectedReminder: null,
      showSideMenu: false
    }
  },

  computed: {
    // 当前 tab 对应的提醒状态
    currentStatus() {
      const tab = this.tabs[this.current]
      return tab ? tab.status : 0
    },

    todayLabel() {
      const d = new Date()
      const pad = (n) => (n < 10 ? '0' + n : '' + n)
      const weekMap = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      const dateStr = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
      const weekStr = weekMap[d.getDay()]
      return `${dateStr} ${weekStr}`
    },

    emptyTitle() {
      if (this.currentStatus === 0) return '暂无待提醒事项'
      if (this.currentStatus === 1) return '暂无已过期提醒'
      if (this.currentStatus === 2) return '暂无已完成提醒'
      return '暂无数据'
    }
  },

  onLoad() {
    const token = uni.getStorageSync('token')
    const userInfo = uni.getStorageSync('userInfo') || null

    console.log('platform =', PLATFORM, 'token =', token, 'userInfo =', userInfo)

    if (token && userInfo) {
      // 已登录
      this.isLoggedIn = true
      this.userInfo = userInfo
      this.fetchRemindList()
    } else {
      // 未登录：先弹出你自己的授权弹窗（H5 + 小程序都会弹）
      this.showAuthModal = true
    }
  },

  onShow() {
    if (this.isLoggedIn) {
      this.fetchRemindList()
    }
  },

  methods: {
    /* ========== 顶部 tabs / 列表 ========== */

    onTabChange(e) {
      const idx = typeof e === 'number' ? e : e?.index
      if (typeof idx === 'number') {
        this.current = idx
        this.fetchRemindList()
      }
    },

    formatRemindLabel(remindTime) {
      if (!remindTime) return ''
      const d = new Date(remindTime.replace(/-/g, '/'))
      if (Number.isNaN(d.getTime())) return remindTime

      const now = new Date()
      const isToday =
          d.getFullYear() === now.getFullYear() &&
          d.getMonth() === now.getMonth() &&
          d.getDate() === now.getDate()

      const pad = (n) => (n < 10 ? '0' + n : '' + n)
      const hhmm = `${pad(d.getHours())}:${pad(d.getMinutes())}`

      const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const week = weekMap[d.getDay()]

      const dayLabel = isToday
          ? '今天'
          : `${pad(d.getMonth() + 1)}-${pad(d.getDate())}`

      return `${dayLabel} ${hhmm} | ${week}`
    },

    fetchRemindList() {
      this.loading = true
      reminderAPI.getReminderList({ status: this.currentStatus })
          .then(res => {
            if (res.code === 200) {
              const page = res.data || {}
              this.remindList = page.records || []
            } else {
              uni.showToast({
                title: res.message || '获取提醒列表失败',
                icon: 'none'
              })
            }
          })
          .catch(err => {
            console.error('获取提醒列表失败:', err)
            uni.showToast({
              title: '获取提醒列表失败',
              icon: 'none'
            })
          })
          .finally(() => {
            this.loading = false
          })
    },

    /* ========== 新建提醒 ========== */
    handleCreateRemind() {
      if (!this.isLoggedIn) {
        // 未登录时，走统一授权入口
        this.showAuthModal = true
        return
      }
      uni.navigateTo({
        url: '/pages/remind/create-remind'
      })
    },

    /* ========== 授权弹窗 ========== */

    hideAuthPopup() {
      this.showAuthModal = false
    },

    // 点击“确认授权”按钮
    handleAuth() {
      if (PLATFORM === 'mp-weixin') {
        // 小程序：走微信官方头像昵称授权
        this.requestWxAuth()
        return
      }

      // H5：走原来的模拟登录逻辑
      uni.showLoading({ title: '登录中...' })
      authAPI.login()
          .then(res => {
            if (res.code === 200) {
              this.isLoggedIn = true
              this.userInfo = res.data.userInfo || {
                nickname: 'shemuel',
                avatar: '/static/avatar.svg'
              }

              uni.setStorageSync('token', res.data.tokenValue || 'mock-token')
              uni.setStorageSync('userInfo', this.userInfo)

              this.showAuthModal = false
              this.fetchRemindList()

              uni.showToast({
                title: '登录成功',
                icon: 'success'
              })
            } else {
              uni.showToast({
                title: res.message || '登录失败',
                icon: 'none'
              })
            }
          })
          .catch(err => {
            console.error('登录失败:', err)
            uni.showToast({
              title: '登录失败',
              icon: 'none'
            })
          })
          .finally(() => {
            uni.hideLoading()
          })
    },

    // 小程序环境的微信授权（只能在点击事件里调用）
    requestWxAuth() {
      if (PLATFORM !== 'mp-weixin') {
        console.warn('当前平台不是 mp-weixin，requestWxAuth 不执行')
        return
      }

      if (typeof wx === 'undefined' || !wx.getUserProfile) {
        uni.showToast({
          title: '当前环境不支持微信授权',
          icon: 'none'
        })
        return
      }

      wx.getUserProfile({
        // ❗ 这里必须简短，否则就会报 desc length does not meet the requirements
        desc: '用于完善资料',
        success: (res) => {
          console.log('微信登录 返回res', res)
          const wxUser = res.userInfo
          this.userInfo = {
            nickname: wxUser.nickName,
            avatar: wxUser.avatarUrl
          }
          uni.setStorageSync('userInfo', this.userInfo)

          // 拿 code
          uni.login({
            provider: 'weixin',
            success: (loginRes) => {
              const code = loginRes.code
              console.log('微信登录 返回code', code)

              authAPI.loginByWeixin({
                code,
                avatar: wxUser.avatarUrl,
                nickname: wxUser.nickName
              }).then(r => {
                if (r.code === 200) {
                  this.isLoggedIn = true
                  uni.setStorageSync('token', r.data.tokenValue)
                  uni.showToast({ title: '登录成功', icon: 'success' })
                  this.showAuthModal = false
                  this.fetchRemindList()
                } else {
                  uni.showToast({
                    title: r.message || '登录失败',
                    icon: 'none'
                  })
                }
              }).catch(err => {
                console.error('微信登录失败:', err)
                uni.showToast({
                  title: '登录失败',
                  icon: 'none'
                })
              })
            },
            fail: (err) => {
              console.error('wx.login 失败:', err)
              uni.showToast({
                title: '登录失败',
                icon: 'none'
              })
            }
          })
        },
        fail: (err) => {
          console.log('getUserProfile fail', err)
          uni.showToast({
            title: '已取消授权',
            icon: 'none'
          })
          // ❗ 这里不要再递归调用 this.requestWxAuth()，否则死循环体验很差
        }
      })
    },

    /* ========== 详情 + 底部操作 ========== */

    goDetail(item) {
      if (!item || !item.id) return
      uni.navigateTo({
        url: `/pages/remind/detail?id=${item.id}`
      })
    },

    openActionSheet(item) {
      this.selectedReminder = item
      this.showActionSheet = true
    },

    closeActionSheet() {
      this.showActionSheet = false
      this.selectedReminder = null
    },

    onEditReminder() {
      if (!this.selectedReminder) return
      this.showActionSheet = false
      uni.navigateTo({
        url: `/pages/remind/create-remind?id=${this.selectedReminder.id}`
      })
    },

    onCompleteReminder() {
      if (!this.selectedReminder) return
      const id = this.selectedReminder.id
      reminderAPI.updateReminder({
        id,
        status: 2   // 2：已完成
      }).then(res => {
        if (res.code === 200) {
          uni.showToast({
            title: '已完成',
            icon: 'success'
          })
          this.closeActionSheet()
          this.fetchRemindList()
        } else {
          uni.showToast({
            title: res.message || '操作失败',
            icon: 'none'
          })
        }
      }).catch(err => {
        console.error('完成提醒失败:', err)
        uni.showToast({
          title: '操作失败',
          icon: 'none'
        })
      })
    },

    onDeleteReminder() {
      if (!this.selectedReminder) return
      const id = this.selectedReminder.id

      this.showActionSheet = false

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
                      })
                      this.fetchRemindList()
                    } else {
                      uni.showToast({
                        title: r.message || '删除失败',
                        icon: 'none'
                      })
                    }
                  })
                  .catch(err => {
                    console.error('删除提醒失败:', err)
                    uni.showToast({
                      title: '删除失败',
                      icon: 'none'
                    })
                  })
            }
          }
        })
      }, 200)
    },

    /* ========== 侧边菜单 ========== */

    openSideMenu() {
      this.showSideMenu = true
    },
    closeSideMenu() {
      this.showSideMenu = false
    },

    goReminderSearch() {
      this.closeSideMenu()
      uni.showToast({ title: '提醒搜索（待接入）', icon: 'none' })
    },
    goReminderCalendar() {
      this.closeSideMenu()
      uni.showToast({ title: '提醒日历（待接入）', icon: 'none' })
    },
    goReminderTemplate() {
      this.closeSideMenu()
      uni.navigateTo({
        url: '/pages/remind/template-list'
      })
    },
    goHelp() {
      this.closeSideMenu()
      uni.showToast({ title: '使用帮助（待接入）', icon: 'none' })
    },
    clearAll() {
      this.closeSideMenu()
      uni.showToast({ title: '清空所有（待接入）', icon: 'none' })
    },
    clearPending() {
      this.closeSideMenu()
      uni.showToast({ title: '清空待提醒（待接入）', icon: 'none' })
    },
    clearExpired() {
      this.closeSideMenu()
      uni.showToast({ title: '清空已过期（待接入）', icon: 'none' })
    },
    clearCompleted() {
      this.closeSideMenu()
      uni.showToast({ title: '清空已完成（待接入）', icon: 'none' })
    }
  }
}
</script>



<style lang="scss" scoped>
.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
  background: #f7f7f7;
}

.tabs-row {
  display: flex;
  align-items: center;
  padding: 0 16rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
  flex-shrink: 0;
}

.main {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.scroll-area {
  height: 100%;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.remind-list {
  padding: 16rpx 24rpx 160rpx;
}

.empty-wrap {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9aa1a7;
}

.nav-right { display:flex; gap:12rpx; }
.ghost-btn { background:#f3f4f6; color:#333; padding:0 12rpx; box-shadow:0 2rpx 8rpx rgba(0,0,0,.06); }

.group-icon { margin-left:auto; padding:0 12rpx; }

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

/* 授权弹窗样式（H5 用） */
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

.tabs-menu {
  padding-right: 12rpx;
}

.tabs-main {
  flex: 1;
}

.group-icon {
  margin-left: 12rpx;
  padding: 0 4rpx;
}

/* ===== 左侧抽屉菜单 ===== */
.side-panel {
  width: 330rpx;
  height: 100vh;
  background: #ffffff;
  padding: 100rpx 32rpx 40rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.side-date {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 40rpx;
}

.side-menu {
  background: #ffffff;
}

.side-item {
  height: 96rpx;
  padding: 0 8rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid #f3f3f3;
}

.side-item-left {
  display: flex;
  align-items: center;
}

.side-item-text {
  margin-left: 20rpx;
  font-size: 30rpx;
  color: #333;
}

.side-section-title {
  margin: 40rpx 0 8rpx;
  font-size: 26rpx;
  color: #888;
}
</style>
