// API配置文件
const config = {
  // 开发环境
  development: {
    baseURL: 'http://localhost:8088'
  },
  // 生产环境
  production: {
    baseURL: 'https://your-production-domain.com'
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
export const authAPI = {
  // 用户登录
  login() {
    return request('/auth/login', {
      method: 'POST',
      data: {
        identifier: 'shemuel',
        password: '849113..'
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



// 导出配置供其他地方使用
export { API_BASE_URL }
export default { timelineAPI, eventAPI, authAPI }