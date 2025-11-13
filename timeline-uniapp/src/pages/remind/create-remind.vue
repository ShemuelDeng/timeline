<template>
  <view class="create-page">
    <!-- 顶部导航 -->
    <u-navbar
        title="创建提醒"
        :autoBack="true"
        :bgColor="'#ffffff'"
        :titleStyle="{fontWeight:600,fontSize:'18px'}"
    />

    <!-- 表单内容 -->
    <view class="form-wrap">
      <!-- 提醒事项 -->
      <view class="form-item">
        <text class="label">提醒事项</text>
        <input
            class="input"
            v-model="form.title"
            placeholder="请输入提醒内容"
            placeholder-style="color:#ccc;"
        />
      </view>

      <view class="line"></view>

      <!-- 提醒时间 -->
      <view class="form-item">
        <text class="label">提醒时间</text>
        <view class="time-row">
          <picker mode="date" :value="form.date" @change="onDateChange">
            <view class="date-text">{{ dateLabel }}</view>
          </picker>
          <text class="split-text">|</text>
          <picker mode="time" :value="form.time" @change="onTimeChange">
            <view class="time-text">{{ form.time }}</view>
          </picker>
          <text class="plus-icon">＋</text>
        </view>
      </view>

      <view class="line"></view>

      <!-- 重复 -->
      <view class="form-item arrow-item">
        <text class="label">重复(vip)</text>
        <picker :value="repeatIndex" :range="repeatOptions" @change="onRepeatChange">
          <view class="picker-value">
            {{ repeatOptions[repeatIndex] }}
          </view>
        </picker>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="btn-area">
      <button class="submit-btn" @click="handleSubmit">提交</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { reminderAPI } from '@/utils/api.js'

const today = new Date()
const pad = (n) => (n < 10 ? '0' + n : '' + n)

const form = ref({
  title: '',
  date: `${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(today.getDate())}`,
  time: `${pad(today.getHours())}:${pad(today.getMinutes())}`,
  repeat: '不重复'
})

const repeatOptions = ['不重复', '每天', '每周', '工作日','每月', '每年']
const repeatIndex = ref(0)

const getWeekText = (dateStr) => {
  const d = new Date(dateStr.replace(/-/g, '/'))
  const day = d.getDay()
  const map = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return map[day]
}

const isToday = computed(() => {
  const t = today
  const d = new Date(form.value.date.replace(/-/g, '/'))
  return (
      t.getFullYear() === d.getFullYear() &&
      t.getMonth() === d.getMonth() &&
      t.getDate() === d.getDate()
  )
})

const dateLabel = computed(() => {
  const week = getWeekText(form.value.date)
  if (isToday.value) {
    return `今天（${week}）`
  }
  return `${form.value.date}（${week}）`
})

const onDateChange = (e) => {
  form.value.date = e.detail.value
}

const onTimeChange = (e) => {
  form.value.time = e.detail.value
}

const onRepeatChange = (e) => {
  repeatIndex.value = Number(e.detail.value)
  form.value.repeat = repeatOptions[repeatIndex.value]
}

const handleSubmit = async () => {
  if (!form.value.title.trim()) {
    uni.showToast({
      title: '请输入提醒事项',
      icon: 'none'
    })
    return
  }

  // 组合 LocalDateTime 字符串：yyyy-MM-dd HH:mm:ss
  const remindTime = `${form.value.date} ${form.value.time}:00`

  // 把选择的中文重复选项映射成后端描述中的英文规则
  // const repeatOptions = ['不重复', '每天', '每周', '工作日','每月', '每年']
  const repeatCodeMap = ['NONE', 'DAILY', 'WEEKLY','WORKDAY', 'MONTHLY', 'YEARLY']
  const repeatRule = repeatCodeMap[repeatIndex.value] || 'NONE'

  uni.showLoading({ title: '提交中...' })

  try {
    const res = await reminderAPI.addReminder({
      title: form.value.title,
      content: '',             // 目前你页面上只有标题，有需要可以加一个内容输入框
      remindTime,              // 后端 LocalDateTime
      repeatRule,              // 如 DAILY / WEEKLY / MONTHLY / NONE
      isActive: 1              // 默认启用
      // templateId: 可后续支持模板再传
    })

    if (res.code === 200) {
      uni.showToast({
        title: '创建成功',
        icon: 'success'
      })
      // 返回提醒首页
      setTimeout(() => {
        uni.navigateBack()
      }, 500)
    } else {
      uni.showToast({
        title: res.message || '创建失败',
        icon: 'none'
      })
    }
  } catch (e) {
    console.error('创建提醒失败:', e)
    uni.showToast({
      title: '创建失败',
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

</script>

<style lang="scss" scoped>
.create-page {
  min-height: 100vh;
  background-color: #f7f7f7;
  box-sizing: border-box;
  padding-bottom: 80rpx;
}

/* 表单 */
.form-wrap {
  margin-top: 16rpx;
  background-color: #ffffff;
}

.form-item {
  padding: 24rpx 32rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.label {
  font-size: 28rpx;
  color: #666;
  width: 180rpx;
}

.input {
  flex: 1;
  font-size: 28rpx;
}

.line {
  height: 1rpx;
  background-color: #f1f1f1;
  margin-left: 32rpx;
}

/* 时间行 */
.time-row {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.date-text,
.time-text {
  font-size: 28rpx;
  color: #333;
}

.split-text {
  margin: 0 12rpx;
  color: #ccc;
}

.plus-icon {
  margin-left: 12rpx;
  font-size: 32rpx;
  color: #11b668;
}

/* 重复 */
.arrow-item .picker-value {
  font-size: 28rpx;
  color: #333;
}

/* 提交按钮 */
.btn-area {
  margin-top: 80rpx;
  padding: 0 80rpx;
}

.submit-btn {
  background-color: #07c160;
  color: #ffffff;
  border-radius: 999rpx;
  font-size: 30rpx;
}
</style>
