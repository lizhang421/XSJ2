import request from '@/utils/request'

export const GetImageCode=()=>{
	return request.get('/user/captcha',
	{responseType:'blob'})
}


export const PhoneRegister=(phone, password, rewpassword, captchaCode)=>{
	return request.post('/user/register', null, {
		params: {
			phone,
			password,
			rewpassword,
			captchaCode
		}
	})
}

export const PhoneLogin=(phone, password, captchaCode)=>{
	return request.post('/user/login', null, {
		params: {
			phone,
			password,
			captchaCode
		}
	})
}

export const SendEmailCode=(toEmail, type)=>{
	return request.post('/email/send', null, {
		params: {
			toEmail,
			type
		}
	})
}

export const EmailRegister=(toEmail, code, type)=>{
	return request.post('/email/register', null, {
		params: {
			toEmail,
			code,
			type
		}
	})
}

export const EmailLogin=(email, code, type)=>{
	return request.post('/email/login', null, {
		params: {
			email,
			code,
			type
		}
	})
}

export const GetHomeTop=()=>{
	return request.get('/home/getHomeTop')
}

export const GetVideos=(mount)=>{
 
  return request.get('/Video/Videos', {
    params: {
      mount: mount 
    }
  })
}

export const GetVideoResource=(encode)=>{
  return request.get('/home/VideRecourse', {
    params: {
      Encode: encode
    }
  })
}

export const SendVideoComment=(enCode, comment, parentId)=>{
  return request.post('/Video/video-comment', {
    enCode,
    comment,
    parentId
  })
}

export const VideoOperate=(enCode, operateType, status)=>{
  return request.post('/Video/video-operate', {
    EnCode: enCode,
    operateType,
    status
  })
}

export const GetVideoOperateMessage=(enCode)=>{
  return request.get('/Video/video-opmessage', {
    params: {
      enCode
    }
  })
}

export const GetUserDate=()=>{
  return request.get('/user/date')
}

export const UpdateLanguage=(language)=>{
  return request.post('/user/updateLanguage', null, {
    params: {
      language
    }
  })
}

export const GetUserInfoMessage=()=>{
  return request.get('/user/user-info-message')
}

export const CancelFollow=(enCode, status)=>{
  return request.post('/user/cancelFollow', null, {
    params: {
      enCode,
      status
    }
  })
}

export const InsertVideoHistory=(data)=>{
  return request.post('/Video/video-history', data)
}

export const GetVideoHistory=()=>{
  return request.get('/Video/look-history')
}

export const SearchVideo=(key)=>{
  return request.get('/Video/search', { params: { key } })
}

export const GetUserProfile=(encode)=>{
  return request.get('/user/profile', { params: { encode } })
}

export const GetUserVideos=(encode, page=1, size=20)=>{
  return request.get('/user/videos', { 
    params: { 
      encode, 
      page, 
      size 
    } 
  })
}

export const GetUserCollections=(encode, page=1, size=20)=>{
  return request.get('/user/collections', { 
    params: { 
      encode, 
      page, 
      size 
    } 
  })
}

export const GetFriendList=()=>{
  return request.get('/userChat/frindlist')
}

export const GetOtherUserData=(encode)=>{
  return request.get('/user/other-data', { 
    params: { encode, _t: Date.now() },
    headers: { 'Cache-Control': 'no-cache' }
  })
}

export const GetOtherUserHome=(encode)=>{
  return request.get('/user/other-home', { params: { encode } })
}

export const AddFriend=(toUserEncode)=>{
  return request.post('/userChat/addFriend', null, { params: { toUserEncode } })
}

export const HandleFriendRequest=(toUserEncode, status)=>{
  return request.post('/userChat/agree', null, { params: { toUserEncode, status } })
}

export const DeleteFriend=(encode)=>{
  return request.post('/userChat/agree', null, { params: { toUserEncode: encode, status: 2 } })
}

export const GetFriendApplyList=()=>{
  return request.get('/userChat/applylist')
}

export const GetChatHistory=(toUserEncode)=>{
  return request.get('/userChat/chatHistory', { params: { toUserEncode } })
}

export const MarkMessagesRead=(toUserEncode, msgIdList)=>{
  return request.post('/userChat/chat/read', msgIdList, {
    params: { toUserEncode },
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const DeleteMessages=(toUserEncode, msgIdList)=>{
  return request.post('/userChat/chat/deleteMessages', msgIdList, { 
    params: { toUserEncode },
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const DeleteSession=(toUserEncode)=>{
  return request.delete('/userChat/chat/deleteSession', { 
    params: { toUserEncode }
  })
}

export const GetReplyMessages=()=>{
  return request.get('/Video/get-user-commentmessage')
}

export const MarkReplyRead=(commentIds)=>{
  return request.post('/Video/user-commentmessage', commentIds, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const GetMyInteract=()=>{
  return request.get('/Video/get-user-intercate')
}

export const MarkInteractRead=(interactIds)=>{
  return request.post('/Video/update-interact-status', interactIds, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const GetUserAuditInfo=()=>{
  return request.get('/home/getUserAuditInfo')
}

export const ReadAuditMessage=(data)=>{
  return request.post('/home/readMessage', data, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const UpdateUserInfo=(userData)=>{
  return request.post('/user/user-updateUserMessage', userData)
}

export const ReportContent=(targetUserEncode, reportType, targetEncode, reason, remark)=>{
  return request.post('/user/report', null, {
    params: {
      targetUserEncode,
      ReportType: reportType,
      TargetEncode: targetEncode,
      Reason: reason,
      Remark: remark
    }
  })
}

export const GetReport=()=>{
  return request.get('/user/getreport')
}

export const ReadReport=(reportIds)=>{
  return request.post('/user/readreport', reportIds, {
    headers: {
      'Content-Type': 'application/json'
    }
  })
}

export const UpdatePassword=(oldPassword, newPassword)=>{
	return request.post('/user/updatePassword', null, {
		params: {
			oldPassword,
			newPassword
		}
	})
}

export const UpdatePasswordByEmail=(toEmail, code, type, newPassword)=>{
	return request.post('/email/updatePasswordbyem', null, {
		params: {
			toEmail,
			code,
			type,
			newPassword
		}
	})
}

export const UploadVideoResource=(fileurl, videoEncode, name, type, format, size, recoursedese)=>{
	return request.post('/Video/video/resource', null, {
		params: {
			fileurl,
			videoEncode,
			name,
			type,
			format,
			size,
			recoursedese
		}
	})
}

export const GetVideoResources=(enCode)=>{
	return request.get('/Video/video-file', {
		params: {
			enCode
		}
	})
}

export const DeleteVideo=(enCode)=>{
	return request.post('/Video/delete-video', null, {
		params: {
			enCode
		}
	})
}

export const DeleteVideoResource=(id)=>{
	return request.post('/Video/delete-video-resource', null, {
		params: {
			id
		}
	})
}
