// API配置文件
const config = {
  // 开发环境
  development: {
    baseURL: 'http://localhost:8088'
  },
  // 生产环境
  production: {
    baseURL: 'http://localhost:8088'
  }
}

// 当前环境配置
const currentEnv = process.env.NODE_ENV || 'development'
const API_BASE_URL = config[currentEnv].baseURL

// 通用请求方法
const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    // 获取存储的token
    const token = uni.getStorageSync('token');
    
    uni.request({
      url: `${API_BASE_URL}${url}`,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Accept': '*/*',
        'Authorization': `${token}`,
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res.data)
        } else {
          reject(new Error(`请求失败: ${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

// 用户认证相关API
// utils/api.js 或你现在放 authAPI 的那个文件

// 用户认证相关API
export const authAPI = {
  /**
   * 普通账号密码登录（H5 调用）
   */
  login() {
    return request('/auth/login', {
      method: 'POST',
      data: {
        identifier: 'shemuel',
        password: '849113..'
      }
    })
  },

  /**
   * 微信小程序登录
   * @param {Object} payload
   * @param {string} payload.code    wx.login 返回的 code
   * @param {string} payload.avatar  getUserProfile 返回的 avatarUrl
   * @param {string} payload.nickname getUserProfile 返回的 nickName
   */
  loginByWeixin({ code, avatar, nickname }) {
    return request('/auth/loginByWeixin', {
      method: 'POST',
      data: {
        code,
        avatar,
        nickname
      }
    })
  }
}


// 时间轴相关API
export const timelineAPI = {
  // 1. 查询用户的时间轴列表
  getTimelineList() {
    return request(`/api/timeline/list`)
  },

  // 2. 新增时间轴
  addTimeline(data) {
    return request('/api/timeline/add', {
      method: 'POST',
      data: {
        title: data.title,
        description: data.description,
        tag: data.tag,
        coverUrl: data.coverUrl || '',
        createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
        updateTime: new Date().toISOString().replace('T', ' ').substring(0, 19)
      }
    })
  }
}

// 文件上传相关API
export const fileAPI = {
  // 上传文件
  uploadFile(filePath) {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('token');
      uni.uploadFile({
        url: `${API_BASE_URL}/api/file/upload`,
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': token
        },
        success: (uploadFileRes) => {
          if (uploadFileRes.statusCode === 200) {
            const data = JSON.parse(uploadFileRes.data);
            if (data.code === 200) {
              resolve(data.data);
            } else {
              reject(new Error(data.message || '上传失败'));
            }
          } else {
            reject(new Error(`上传失败: ${uploadFileRes.statusCode}`));
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  }
}


export const eventAPI = {
  // 3. 查询时间轴下的所有事件
  getEventList(timelineId) {
    return request(`/api/event/list?timelineId=${timelineId}`)
  },

  // 5. 删除时间轴下的事件
  deleteEvent(timelineId) {
    return request(`/api/event/delete/${timelineId}`, {
      method: 'DELETE'
    })
  },

  // 4. 给时间轴添加事件
  addEvent(data) {
    return request('/api/event/add', {
      method: 'POST',
      data: {
        timelineId: data.timelineId,
        title: data.title,
        content: data.content,
        isRich: 1,
        tag: data.tag,
        location: data.location,
        images: data.images || [],
        eventTime: data.eventTime
      }
    })
  }
}
// 用户提醒相关API
export const reminderAPI = {
  // 新增提醒
  addReminder(data) {
    return request('/api/t-user-reminder/add', {
      method: 'POST',
      data: {
        // userId 建议由后端通过 token 自动填，这里不传
        templateId: data.templateId || null,
        title: data.title,
        content: data.content || '',
        // 后端是 LocalDateTime，传 'yyyy-MM-dd HH:mm:ss' 字符串即可
        remindTime: data.remindTime,
        // 如 DAILY, WEEKLY, MONTHLY, NONE
        repeatRule: data.repeatRule || 'NONE',
        isActive: data.isActive != null ? data.isActive : 1,
        // createTime / updateTime 通常由后端填，如果你现在没自动填，也可以像 timeline 一样在前端带过去
        // createTime: data.createTime,
        // updateTime: data.updateTime
      }
    })
  },

  // 获取提醒列表
  getReminderList(params = {}) {
    return request('/api/t-user-reminder/list', {
      method: 'GET',
      data: params
    })
  },

  // 获取提醒详情
  getReminderDetail(id) {
    return request(`/api/t-user-reminder/${id}`, {
      method: 'GET'
    })
  },

  // 修改提醒
  updateReminder(data) {
    return request('/api/t-user-reminder/update', {
      method: 'PUT',
      data
    })
  },

  // 删除提醒（支持单个或多个）
  deleteReminder(ids) {
    const idPath = Array.isArray(ids) ? ids.join(',') : ids
    return request(`/api/t-user-reminder/delete/${idPath}`, {
      method: 'DELETE'
    })
  }
}



// 导出配置供其他地方使用
export { API_BASE_URL }
export default { timelineAPI, eventAPI, authAPI, fileAPI, reminderAPI }

