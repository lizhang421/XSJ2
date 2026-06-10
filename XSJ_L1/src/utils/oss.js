import OSS from 'ali-oss'

const OSS_CONFIG = {
  region: 'oss-cn-beijing',
  accessKeyId: 'LTAI5tBhdXBTnmsjjDFAXY1E',
  accessKeySecret: 'HZTODzl1ChjzUTYoAZfFRj60FFmepj',
  bucket: 'lizhanghao',
  secure: true,
  timeout: 600000
}

let ossClient = null

const getOSSClient = () => {
  if (!ossClient) {
    ossClient = new OSS(OSS_CONFIG)
  }
  return ossClient
}

const generateFileName = (file, type) => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  const timestamp = `${year}${month}${day}${hours}${minutes}${seconds}`
  
  const randomStr = Math.random().toString(36).substring(2, 8)
  const fileExt = file.name.substring(file.name.lastIndexOf('.'))
  
  const baseDir = type === 'video' ? 'video/' : 'images/'
  const datePath = `${year}/${month}/${day}/`
  
  return `${baseDir}${datePath}${timestamp}_${randomStr}${fileExt}`
}

export const uploadToOSS = async (file, type = 'image', onProgress) => {
  const maxRetries = 3
  let lastError = null
  
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      const client = getOSSClient()
      const fileName = generateFileName(file, type)
      
      const result = await client.put(fileName, file, {
        progress: (p) => {
          if (onProgress) {
            onProgress(Math.round(p * 100))
          }
        },
        timeout: 600000
      })
      
      return {
        success: true,
        url: result.url,
        name: fileName
      }
    } catch (error) {
      console.error(`OSS上传失败 (尝试 ${attempt}/${maxRetries}):`, error)
      lastError = error
      
      if (attempt < maxRetries) {
        await new Promise(resolve => setTimeout(resolve, 2000 * attempt))
      }
    }
  }
  
  return {
    success: false,
    error: lastError?.message || '上传失败，请检查网络连接'
  }
}

export const uploadMultipleToOSS = async (files, type = 'image', onProgress) => {
  const results = []
  let totalProgress = 0
  
  for (let i = 0; i < files.length; i++) {
    const file = files[i]
    const result = await uploadToOSS(file, type, (progress) => {
      const currentFileProgress = (progress / files.length)
      const completedFilesProgress = (i / files.length) * 100
      totalProgress = completedFilesProgress + currentFileProgress
      if (onProgress) {
        onProgress(Math.round(totalProgress))
      }
    })
    results.push(result)
  }
  
  return results
}

export const deleteFromOSS = async (fileName) => {
  try {
    const client = getOSSClient()
    await client.delete(fileName)
    return {
      success: true
    }
  } catch (error) {
    console.error('OSS删除失败:', error)
    return {
      success: false,
      error: error.message || '删除失败'
    }
  }
}

export const getOSSFileURL = (fileName) => {
  const client = getOSSClient()
  return client.signatureUrl(fileName, {
    expires: 3600
  })
}
