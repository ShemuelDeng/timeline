<template>
  <view class="page">
    <!-- 顶部栏：标题 + 右侧两个圆按钮 -->
    <u-navbar placeholder title="工具" safeAreaInsetTop :bgColor="'#ffffff'"
              :titleStyle="{fontWeight:600,fontSize:'18px'}">
    </u-navbar>

    <u-gap height="8" bgColor="#f7f8f9" />

    <!-- 分组 1：时间进度 -->
    <view class="section">
      <u-title title="时间进度" :right="false" lineColor="#2b7cff" />
      <u-grid :border="false" col="2" class="grid">
        <u-grid-item v-for="(it, idx) in timeTools" :key="idx" @click="open(it)">
          <view class="card">
            <view class="card-icon">
              <u-icon :name="it.icon" size="26" :color="it.color" />
            </view>
            <view class="card-title">{{ it.title }}</view>
            <view class="card-sub">{{ it.sub }}</view>
          </view>
        </u-grid-item>
      </u-grid>
    </view>

    <!-- 分组 2：生活服务 -->
    <view class="section">
      <u-title title="生活服务" :right="false" lineColor="#2b7cff" />
      <u-grid :border="false" col="2" class="grid">
        <u-grid-item v-for="(it, idx) in lifeTools" :key="idx" @click="open(it)">
          <view class="card">
            <view class="card-icon">
              <u-icon :name="it.icon" size="26" :color="it.color" />
            </view>
            <view class="card-title">{{ it.title }}</view>
            <view class="card-sub">{{ it.sub }}</view>
          </view>
        </u-grid-item>
      </u-grid>
    </view>

    <!-- 分组 3：实用工具 -->
    <view class="section">
      <u-title title="实用工具" :right="false" lineColor="#2b7cff" />
      <u-grid :border="false" col="2" class="grid">
        <u-grid-item v-for="(it, idx) in utilTools" :key="idx" @click="open(it)">
          <view class="card">
            <view class="card-icon">
              <u-icon :name="it.icon" size="26" :color="it.color" />
            </view>
            <view class="card-title">{{ it.title }}</view>
            <view class="card-sub">{{ it.sub }}</view>
          </view>
        </u-grid-item>
      </u-grid>
    </view>

    <u-gap height="24" />
  </view>
</template>

<script setup>
const timeTools = [
  { title: '人生进度', sub: '看看你的人生余额', icon: 'grid', color: '#11b668', path: '/pages/placeholder/progress' },
  { title: '时间进度', sub: '时间在流逝', icon: 'hourglass', color: '#41c1ff' },
  { title: '日期倒计时', sub: '重要日子', icon: 'clock', color: '#2b7cff' },
  { title: '翻页时钟', sub: '专注计时', icon: 'calendar', color: '#6f63ff' },
  { title: '日程时间', sub: '今日进度', icon: 'calendar-fill', color: '#11b668' },
  { title: '番茄时钟', sub: '番茄工作法', icon: 'coupon', color: '#ff6a32' },
  { title: '人生格子', sub: '人生只有900格', icon: 'grid', color: '#6f63ff' },
  { title: '倒数日', sub: '等一个日子', icon: 'calendar', color: '#ff9f2f' },
  { title: '节假日', sub: '还有多久过节？', icon: 'gift', color: '#ff6aa0' }
]

const lifeTools = [
  { title: '美团外卖红包', sub: '天天领天天省', icon: 'red-packet', color: '#ff9f2f' },
  { title: '饿了么红包',   sub: '天天领天天省', icon: 'red-packet', color: '#2b7cff' }
]

const utilTools = [
  { title: '记账本（新）', sub: '简单高效', icon: 'rmb-circle', color: '#11b668' },
  { title: '备忘录',       sub: '轻松记录', icon: 'file-text',  color: '#ff9f2f' }
]

const open = (it) => {
  // 这里只做示例跳转，你可替换成真实页面
  uni.showToast({ title: it.title, icon: 'none' })
}
</script>

<style lang="scss" scoped>
.page { min-height: 100vh; }
.nav-right { display: flex; gap: 12rpx; }
.ghost-btn { background: #f3f4f6; color:#333; padding: 0 12rpx; box-shadow: 0 2rpx 8rpx rgba(0,0,0,.06); }

.section { margin: 20rpx; padding: 20rpx; background: #fff; border-radius: 18rpx; box-shadow: 0 6rpx 18rpx rgba(0,0,0,.03); }
.grid { margin-top: 12rpx; }

.card {
  width: 100%; height: 160rpx; border-radius: 16rpx; background: #fff;
  border: 1rpx solid #f0f0f0; padding: 20rpx; display: flex; flex-direction: column;
  justify-content: center; box-shadow: 0 4rpx 14rpx rgba(0,0,0,.02);
}
.card-icon { width: 42rpx; height: 42rpx; border-radius: 8rpx; background: #f7f8f9; display:flex; align-items:center; justify-content:center; margin-bottom: 10rpx; }
.card-title { font-size: 30rpx; color: #333; font-weight: 600; }
.card-sub { font-size: 24rpx; color: #999; }
</style>
