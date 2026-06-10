import request from '@/utils/request'

export const uploadVideoInfo = (videoData) => {
  return request.post('/Video/upload-video', videoData)
}

export const getVideoTypeList = () => {
  return request.get('/user/gettypelist')
}

export const getVideoList = (params) => {
  return request.get('/Video/list', { params })
}

export const getVideoDetail = (id) => {
  return request.get(`/Video/${id}`)
}

export const updateVideo = (id, videoData) => {
  return request.put(`/Video/${id}`, videoData)
}

export const deleteVideo = (id) => {
  return request.delete(`/Video/${id}`)
}

export const getVideoResolution = (enCode) => {
  return request.get('/Video/videoresoultion', {
    params: {
      enCode: enCode
    }
  })
}
