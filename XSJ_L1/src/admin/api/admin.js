import request from '@/admin/utils/adminRequest'

export const AdminLogin = (phone, password) => {
  return request.post('/admin/login', null, {
    params: {
      phone,
      password
    }
  })
}

export const GetAdminInfo = () => {
  return request.get('/admin/info')
}

export const GetDashboardData = () => {
  return request.get('/admin/dashboard')
}

export const BanUser = (userId, type, reason, duration) => {
  return request.post(`/admin/users/${userId}/ban`, { type, reason, duration })
}

export const UnbanUser = (userId) => {
  return request.post(`/admin/users/${userId}/unban`)
}

export const GetVideoList = (params) => {
  return request.get('/admin/videos', { params })
}

export const DeleteComment = (commentId, status) => {
  return request.post('/admin/comment/deleteComment', null, {
    params: {
      commentId: commentId,
      status: status
    }
  })
}

export const GetReportList = (params) => {
  return request.get('/admin/reports', { params })
}

export const HandleReport = (reportId, action, reason) => {
  return request.put(`/admin/reports/${reportId}`, { action, reason })
}

// 获取待审核视频列表
export const GetAuditVideoList = (params) => {
  return request.get('/admin/getAuditVideo', { params })
}

// 获取审核标签列表
export const GetAuditVideoResult = () => {
  return request.get('/admin/GetauditVideoResult')
}

// 审核视频
export const AuditVideo = (videoPendingId, reviewStatus, videoAuditTagId) => {
  console.log('=== AuditVideo API调用 ===')
  console.log('参数 videoPendingId:', videoPendingId)
  console.log('参数 reviewStatus:', reviewStatus)
  console.log('参数 videoAuditTagId:', videoAuditTagId)
  
  const requestData = {
    videoPendingId: videoPendingId,
    reviewStatus: reviewStatus,
    videoAuditTagId: videoAuditTagId || []
  }
  
  console.log('请求数据JSON:', JSON.stringify(requestData))
  
  return request.post('/admin/auditVideo', requestData, {
    timeout: 300000
  })
}

// 获取用户审核图片列表
export const GetShenHeImageList = (statusList) => {
  return request.post('/admin/getShenHeImage', statusList)
}

// 审核用户图片
export const AuditUserImage = (userviewid, result, VideoAuditTagId) => {
  console.log('=== AuditUserImage API调用 ===')
  console.log('参数 userviewid:', userviewid)
  console.log('参数 result:', result)
  console.log('参数 VideoAuditTagId:', VideoAuditTagId)
  
  const params = new URLSearchParams()
  params.append('userviewid', userviewid)
  params.append('result', result)
  params.append('VideoAuditTagId', VideoAuditTagId || '')
  
  console.log('请求参数:', params.toString())
  
  return request.post('/admin/uploadShenHeImageResult', params, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  })
}

// 获取AI模型列表（支持查询和分页）
export const GetAIModelList = (params) => {
  return request.post('/admin/Ai/getAi', params)
}

// 添加AI模型
export const AddAIModel = (modelData) => {
  return request.post('/admin/Ai/addAi', modelData)
}

// 更新AI模型
export const UpdateAIModel = (modelData) => {
  return request.post('/admin/Ai/updateAi', modelData)
}

// 删除AI模型
export const DeleteAIModel = (aiConfigId, status) => {
  return request.post('/admin/Ai/deleteAi', null, {
    params: {
      AiID: aiConfigId,
      status: status
    }
  })
}

// 管理员获取视频列表
export const GetAdminVideoList = (pageNum, pageSize, status) => {
  return request.get('/admin/Video/getVideo', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize,
        status: status
    }
  })
}

// 获取视频详细信息
export const GetVideoDetail = (encode, where) => {
  return request.get('/admin/Video/getVideoDetail', {
    params: {
      encode: encode,
      where: where
    }
  })
}

// 获取用户列表
export const GetUserList = (pageNum, pageSize, status, key, roleBase) => {
  return request.get('/admin/getUserList', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize,
      status: status,
      key: key,
      roleBase: roleBase
    }
  })
}

// 更新用户状态
export const UpdateUserStatus = (encode, status) => {
  return request.post('/admin/updateUserStatus', null, {
    params: {
      encode: encode,
      status: status
    }
  })
}

// 获取管理员信息
export const GetManagerMessage = () => {
  return request.get('/admin/getManagerMessage')
}

// 获取评论列表
export const GetCommentList = (pageNum, pageSize, status, key, startTime, endTime) => {
  return request.get('/admin/comment/getCommentList', {
    params: {
      pageNum: pageNum,
      pageSize: pageSize,
      status: status,
      key: key,
      startTime: startTime,
      endTime: endTime
    }
  })
}

// 紧急封禁视频
export const DeleteVideo = (encode, videoAuditTagId, status) => {
  return request.post('/admin/Video/deleteVideo', {
    encode: encode,
    videoAuditTagId: videoAuditTagId,
    status: status
  })
}

// 获取系统配置
export const GetSysScoreConfig = () => {
  return request.get('/admin/getSysScoreConfig')
}

// 更新评分配置
export const UpdateVideoSortWeight = (data) => {
  return request.post('/admin/updateVideoSortWeight', data)
}

// 更新池配置
export const UpdatePoolConfig = (data) => {
  return request.post('/admin/updatePoolConfig', data)
}

// 删除池配置
export const DeletePool = (poolId) => {
  return request.post('/admin/deletePool', null, {
    params: { poolId }
  })
}

// 新增池配置
export const AddPool = (data) => {
  return request.post('/admin/addPool', data)
}

// 获取举报审核列表
export const GetJubaoList = (params) => {
  return request.get('/admin/getjubao', { params })
}

// 审核举报
export const AuditJubao = (jubaoId, jieguo, solvechufa) => {
  return request.post('/admin/solvejubao', null, {
    params: {
      jubaoId,
      jieguo,
      solvechufa
    }
  })
}

export const AddAdmin = (data) => {
  return request.post('/admin/addadmin', data)
}

export const InsertManager = (phone, password, nickname, realname) => {
  return request.post('/admin/insertmanager', null, {
    params: {
      phone,
      password,
      nickname,
      realname
    }
  })
}

export const UpdateAdminProfile = (data) => {
  return request.post('/admin/updateUserMessage', data, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const GetSysResourceConfig = () => {
  return request.get('/admin/getSysResourceConfig')
}

export const UpdateSysResource = (homeResourcesId, url) => {
  return request.post('/admin/updateSysResourceConfig', null, {
    params: {
      Homeresourse: homeResourcesId,
      url: url
    }
  })
}

export const GetDataEase = () => {
  return request.get('/admin/getdataease')
}
