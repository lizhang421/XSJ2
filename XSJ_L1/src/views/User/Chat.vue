<template>
  <div class="chat-page">
    <header class="chat-header" :style="{ backgroundImage: `url(${headerBgImage})` }">
      <div class="header-inner">
        <div class="header-left">
          <div class="navbar-brand" @click="goHome">
            <h1 class="brand-logo">XSJ</h1>
            <div class="brand-name">
              <h2>{{ locale === 'en' ? 'New World AI Education' : t('brand.name') }}</h2>
              <p>{{ locale === 'en' ? 'AI-driven Education' : t('brand.englishName') }}</p>
            </div>
          </div>
        </div>
        
        <div class="header-center">
          <nav class="nav-menu">
            <a class="nav-item" :class="{ active: currentNav === 'home' }" @click="goToFirst">
              <span class="nav-icon">🏠</span>
              <span class="nav-text">{{ t('nav.home') }}</span>
            </a>
            <a class="nav-item" :class="{ active: currentNav === 'ai' }" @click="goToAI">
              <span class="nav-icon">🤖</span>
              <span class="nav-text">AI</span>
            </a>
          </nav>
        </div>
        
        <div class="header-right">
          <div class="lang-switch">
            <el-dropdown trigger="click" @command="changeLocale" placement="bottom-end">
              <div class="lang-btn">
                <el-icon><el-icon-globe /></el-icon>
                <span class="lang-text">{{ currentLangText }}</span>
                <el-icon class="dropdown-arrow"><el-icon-arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="lang-dropdown-menu">
                  <el-dropdown-item command="zh" :class="{ active: locale === 'zh' }">
                    <el-icon v-if="locale === 'zh'"><el-icon-check /></el-icon>
                    <span>🇨🇳 中文</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="en" :class="{ active: locale === 'en' }">
                    <el-icon v-if="locale === 'en'"><el-icon-check /></el-icon>
                    <span>🇺🇸 English</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="ja" :class="{ active: locale === 'ja' }">
                    <el-icon v-if="locale === 'ja'"><el-icon-check /></el-icon>
                    <span>🇯🇵 日本語</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          
          <div class="user-avatar-wrapper active" @click="goToCenter">
            <img :src="userInfoStore.info.avatar || 'https://picsum.photos/100/100?random=1'" :alt="userInfoStore.info.nickName || '用户头像'" class="user-avatar" />
          </div>
        </div>
      </div>
    </header>

    <div class="chat-container">
      <div class="chat-content">
        <div class="left-menu">
          <div class="menu-header">
            <h3>{{ t('chat.messageCenter') }}</h3>
          </div>
          <div class="menu-list">
            <div 
              class="menu-item" 
              :class="{ active: currentMenu === 'system' }"
              @click="selectMenu('system')"
            >
              <div class="menu-icon">
                <el-icon><el-icon-bell /></el-icon>
              </div>
              <span class="menu-text">{{ t('chat.systemMessage') }}</span>
              <el-badge :value="systemUnread" :hidden="systemUnread === 0" class="menu-badge" />
            </div>
            
            <div 
              class="menu-item" 
              :class="{ active: currentMenu === 'reply' }"
              @click="selectMenu('reply')"
            >
              <div class="menu-icon">
                <el-icon><el-icon-chat-dot-round /></el-icon>
              </div>
              <span class="menu-text">{{ t('chat.replyMe') }}</span>
              <el-badge :value="replyUnread" :hidden="replyUnread === 0" class="menu-badge" />
            </div>
            
            <div 
              class="menu-item" 
              :class="{ active: currentMenu === 'like' }"
              @click="selectMenu('like')"
            >
              <div class="menu-icon">
                <el-icon><el-icon-aim /></el-icon>
              </div>
              <span class="menu-text">我的互动</span>
            </div>
            
            <div 
              class="menu-item" 
              :class="{ active: currentMenu === 'friendApply' }"
              @click="selectMenu('friendApply')"
            >
              <div class="menu-icon">
                <el-icon><el-icon-user-filled /></el-icon>
              </div>
              <span class="menu-text">{{ t('chat.friendApply') }}</span>
              <el-badge :value="friendApplyUnread" :hidden="friendApplyUnread === 0" class="menu-badge" />
            </div>
            
            <div 
              class="menu-item" 
              :class="{ active: currentMenu === 'message' }"
              @click="selectMenu('message')"
            >
              <div class="menu-icon">
                <el-icon><el-icon-message /></el-icon>
              </div>
              <span class="menu-text">{{ t('chat.myMessage') }}</span>
              <el-badge :value="messageUnread" :hidden="messageUnread === 0" class="menu-badge" />
            </div>
          </div>
        </div>

        <div class="contacts-sidebar" v-if="currentMenu === 'message'">
          <div class="sidebar-header">
            <div class="header-top">
              <h3>{{ t('chat.friendList') }}</h3>
              <el-button text size="small" class="refresh-btn" @click="refreshContacts">
                <el-icon><el-icon-refresh /></el-icon>
              </el-button>
            </div>
            <div class="search-box">
              <el-input
                v-model="searchQuery"
                :placeholder="t('chat.searchPlaceholder')"
                clearable
                size="default"
                class="search-input"
              >
                <template #prefix>
                  <el-icon class="search-icon"><el-icon-search /></el-icon>
                </template>
              </el-input>
            </div>
          </div>
          
          <div class="contacts-list">
            <div 
              class="contact-item" 
              v-for="contact in filteredContacts" 
              :key="contact.encode"
              :class="{ active: selectedContact?.encode === contact.encode }"
              @click="selectContact(contact)"
              @contextmenu.prevent="showContactContextMenu($event, contact)"
            >
              <div class="contact-avatar">
                <img :src="getAvatarUrl(contact.avatar, contact.nickName)" :alt="contact.nickName" />
                <span class="online-status" :class="{ online: contact.isOnline }"></span>
              </div>
              <div class="contact-info">
                <div class="contact-name">
                  <span class="name">{{ contact.nickName }}</span>
                  <span class="time">{{ contact.lastMessageTime }}</span>
                </div>
                <div class="contact-preview">
                  <span class="preview">{{ contact.lastMessage }}</span>
                  <span class="unread" v-if="contact.unreadCount > 0">{{ contact.unreadCount }}</span>
                </div>
              </div>
            </div>
            
            <div class="empty-contacts" v-if="filteredContacts.length === 0">
              <div class="empty-icon-wrapper">
                <el-icon class="empty-icon"><el-icon-user /></el-icon>
              </div>
              <p>{{ t('chat.noContacts') }}</p>
            </div>
          </div>
        </div>

        <div class="chat-main" v-if="currentMenu === 'message'">
          <div class="chat-header-bar" v-if="selectedContact">
            <div class="chat-user-info">
              <div class="avatar-wrapper" @click="showAvatarDialog">
                <img :src="getAvatarUrl(selectedContact.avatar, selectedContact.nickName)" :alt="selectedContact.nickName" class="chat-avatar" />
                <span class="status-dot" :class="{ online: selectedContact.isOnline }"></span>
              </div>
              <div class="chat-user-detail">
                <h4>{{ selectedContact.nickName }}</h4>
                <span class="user-status" :class="{ online: selectedContact.isOnline }">
                  {{ selectedContact.isOnline ? t('chat.online') : t('chat.offline') }}
                </span>
              </div>
            </div>
            <div class="chat-actions">
              <template v-if="!isMultiSelectMode">
                <el-tooltip :content="t('chat.videoCall')" placement="bottom">
                  <el-button text class="action-btn" @click="startVideoCall">
                    <el-icon><el-icon-video-camera /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="t('chat.voiceCall')" placement="bottom">
                  <el-button text class="action-btn" @click="startVoiceCall">
                    <el-icon><el-icon-phone /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="t('chat.viewProfile')" placement="bottom">
                  <el-button text class="action-btn" @click="viewProfile">
                    <el-icon><el-icon-user /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="多选删除" placement="bottom">
                  <el-button text class="action-btn" @click="enterMultiSelectMode">
                    <el-icon><el-icon-finished /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip :content="t('chat.clearHistory')" placement="bottom">
                  <el-button text class="action-btn" @click="confirmDeleteAll">
                    <el-icon><el-icon-delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </template>
              <template v-else>
                <el-button type="primary" size="small" @click="deleteSelectedMessages" :disabled="selectedMessageIds.length === 0">
                  删除选中 ({{ selectedMessageIds.length }})
                </el-button>
                <el-button size="small" @click="exitMultiSelectMode">取消</el-button>
              </template>
            </div>
          </div>

          <div class="chat-messages" ref="messagesContainer">
            <div class="messages-wrapper">
              <div class="message-date-divider" v-if="messages.length > 0">
                <span>{{ t('chat.today') }}</span>
              </div>
              
              <div 
                class="message-item" 
                v-for="(message, index) in messages" 
                :key="message.msgId || message.id || index"
                :class="{ 
                  sent: message.isSent, 
                  received: !message.isSent,
                  selected: isMultiSelectMode && selectedMessageIds.includes(message.msgId || message.id)
                }"
                @click="isMultiSelectMode && toggleMessageSelection(message)"
              >
                <div class="message-checkbox" v-if="isMultiSelectMode">
                  <el-checkbox :model-value="selectedMessageIds.includes(message.msgId || message.id)" />
                </div>
                <div class="message-avatar" v-if="!message.isSent">
                  <img :src="getAvatarUrl(selectedContact?.avatar, selectedContact?.nickName)" />
                </div>
                <div class="message-content">
                  <div class="message-bubble" :class="{ 
                    'with-shadow': true, 
                    'image-message': message.type === 'image' || Number(message.msgType) === 1,
                    'file-message': message.type === 'file' || Number(message.msgType) === 2
                  }" @contextmenu.prevent="!isMultiSelectMode && showMessageMenu($event, message)">
                    <template v-if="message.type === 'image' || Number(message.msgType) === 1">
                      <img :src="message.content" class="message-image" @click="previewImage(message.content)" />
                    </template>
                    <template v-else-if="message.type === 'file' || Number(message.msgType) === 2">
                      <div class="file-preview" @click="downloadFile(message.content, message.fileName)">
                        <el-icon class="file-icon"><el-icon-document /></el-icon>
                        <div class="file-info">
                          <span class="file-name">{{ message.fileName || '未知文件' }}</span>
                          <span class="file-size">{{ formatFileSize(message.content) }}</span>
                        </div>
                      </div>
                    </template>
                    <template v-else>
                      <p>{{ message.content }}</p>
                    </template>
                  </div>
                  <div class="message-meta">
                    <span class="message-time">{{ message.time }}</span>
                    <span class="message-status" v-if="message.isSent">
                      <span v-if="message.status === 'read'" class="read-text">{{ t('chat.read') }}</span>
                      <span v-else class="sent-text">{{ t('chat.sent') }}</span>
                    </span>
                  </div>
                </div>
                <div class="message-avatar" v-if="message.isSent">
                  <img :src="userInfoStore.info.avatar || 'https://picsum.photos/100/100?random=1'" />
                </div>
              </div>
              
              <div class="empty-messages" v-if="messages.length === 0 && selectedContact">
                <div class="empty-animation">
                  <div class="chat-bubble-animation">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
                <p>{{ t('chat.startConversation') }}</p>
              </div>
              
              <div class="no-chat-selected" v-if="!selectedContact">
                <div class="no-chat-icon">
                  <el-icon class="select-icon"><el-icon-chat-line-round /></el-icon>
                </div>
                <h3>{{ t('chat.selectFriendChat') }}</h3>
                <p>{{ t('chat.selectFriendDesc') }}</p>
              </div>
            </div>
          </div>

          <div class="chat-input-area" v-if="selectedContact">
            <div class="input-tools">
              <el-tooltip :content="t('chat.sendImage')" placement="top">
                <div class="tool-btn image-btn" @click="handleImageUpload">
                  <el-icon><el-icon-picture /></el-icon>
                </div>
              </el-tooltip>
              <el-tooltip :content="t('chat.sendFile')" placement="top">
                <div class="tool-btn file-btn" @click="handleFileUpload">
                  <el-icon><el-icon-document /></el-icon>
                </div>
              </el-tooltip>
            </div>
            <div class="input-wrapper">
              <el-input
                v-model="messageInput"
                type="textarea"
                :autosize="{ minRows: 1, maxRows: 4 }"
                :placeholder="t('chat.inputPlaceholder')"
                @keydown.enter.exact.prevent="sendMessage"
                class="message-input"
              />
            </div>
            <el-button 
              type="primary" 
              class="send-btn" 
              @click="sendMessage" 
              :disabled="!messageInput.trim()"
              :class="{ 'has-content': messageInput.trim() }"
            >
              <el-icon><el-icon-s-promotion /></el-icon>
            </el-button>
          </div>
        </div>

        <div class="content-panel" v-if="currentMenu !== 'message'">
          <div class="panel-header">
            <h3>{{ getMenuTitle() }}</h3>
          </div>
          <div class="panel-content">
            <template v-if="currentMenu === 'friendApply'">
              <div class="friend-apply-section">
                <div class="apply-tabs">
                  <el-radio-group v-model="applyTab" size="large">
                    <el-radio-button value="received">
                      {{ t('chat.receivedApply') }}
                      <el-badge :value="friendApplyUnread" :hidden="friendApplyUnread === 0" class="tab-badge" />
                    </el-radio-button>
                    <el-radio-button value="sent">
                      {{ t('chat.sentApply') }}
                    </el-radio-button>
                  </el-radio-group>
                </div>
                
                <div class="apply-table-container" v-if="applyTab === 'received'">
                  <div class="apply-table-header">
                    <div class="table-col col-avatar">{{ t('chat.avatar') || '头像' }}</div>
                    <div class="table-col col-name">{{ t('chat.nickname') || '昵称' }}</div>
                    <div class="table-col col-desc">{{ t('chat.description') || '申请说明' }}</div>
                    <div class="table-col col-status">{{ t('chat.status') || '状态' }}</div>
                    <div class="table-col col-action">{{ t('chat.operation') || '操作' }}</div>
                  </div>
                  <div class="apply-table-body">
                    <div class="apply-table-row" v-for="item in receivedApplyList" :key="item.enCode">
                      <div class="table-col col-avatar">
                        <img :src="getAvatarUrl(item.avatar, item.nickName)" class="apply-avatar" @click="viewUserProfile(item.enCode)" />
                      </div>
                      <div class="table-col col-name">
                        <span class="user-name" @click="viewUserProfile(item.enCode)">{{ item.nickName }}</span>
                      </div>
                      <div class="table-col col-desc">
                        <span class="apply-message">{{ item.nickName }} {{ t('chat.requestAddFriend') }}</span>
                      </div>
                      <div class="table-col col-status">
                        <span class="status-tag" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</span>
                      </div>
                      <div class="table-col col-action">
                        <template v-if="item.status === 3">
                          <el-button type="primary" size="small" @click="handleApply(item.enCode, 1)">{{ t('chat.agree') }}</el-button>
                          <el-button size="small" @click="handleApply(item.enCode, 4)">{{ t('chat.reject') }}</el-button>
                        </template>
                        <span v-else class="action-done">-</span>
                      </div>
                    </div>
                    <div class="apply-empty" v-if="receivedApplyList.length === 0">
                      <el-icon class="empty-icon"><el-icon-user-filled /></el-icon>
                      <span>{{ t('chat.noReceivedApply') }}</span>
                    </div>
                  </div>
                </div>
                
                <div class="apply-table-container" v-if="applyTab === 'sent'">
                  <div class="apply-table-header">
                    <div class="table-col col-avatar">{{ t('chat.avatar') || '头像' }}</div>
                    <div class="table-col col-name">{{ t('chat.nickname') || '昵称' }}</div>
                    <div class="table-col col-desc">{{ t('chat.description') || '申请说明' }}</div>
                    <div class="table-col col-status">{{ t('chat.status') || '状态' }}</div>
                    <div class="table-col col-action">{{ t('chat.operation') || '操作' }}</div>
                  </div>
                  <div class="apply-table-body">
                    <div class="apply-table-row" v-for="item in sentApplyList" :key="item.enCode">
                      <div class="table-col col-avatar">
                        <img :src="getAvatarUrl(item.avatar, item.nickName)" class="apply-avatar" @click="viewUserProfile(item.enCode)" />
                      </div>
                      <div class="table-col col-name">
                        <span class="user-name" @click="viewUserProfile(item.enCode)">{{ item.nickName }}</span>
                      </div>
                      <div class="table-col col-desc">
                        <span class="apply-message">{{ t('chat.youRequestAddFriend') }} {{ item.nickName }}</span>
                      </div>
                      <div class="table-col col-status">
                        <span class="status-tag" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</span>
                      </div>
                      <div class="table-col col-action">
                        <span class="action-done">-</span>
                      </div>
                    </div>
                    <div class="apply-empty" v-if="sentApplyList.length === 0">
                      <el-icon class="empty-icon"><el-icon-user-filled /></el-icon>
                      <span>{{ t('chat.noSentApply') }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="reply-section" v-if="currentMenu === 'reply'">
                <div class="reply-tabs">
                  <div 
                    class="tab-item" 
                    :class="{ active: replyTab === 'received' }"
                    @click="replyTab = 'received'"
                  >
                    <span>回复我的</span>
                    <el-badge :value="replyUnread" :hidden="replyUnread === 0" class="tab-badge" />
                  </div>
                  <div 
                    class="tab-item" 
                    :class="{ active: replyTab === 'sent' }"
                    @click="replyTab = 'sent'"
                  >
                    <span>我发出的</span>
                  </div>
                </div>
                
                <div class="reply-list" v-if="replyTab === 'received' && replyMessagesList.length > 0">
                  <div 
                    class="reply-item" 
                    v-for="reply in replyMessagesList" 
                    :key="reply.videoCommentId"
                    :class="{ unread: reply.isRead === 0 }"
                    @click="handleReplyClick(reply)"
                  >
                    <img :src="reply.replyAvatar || 'https://picsum.photos/48/48?random=1'" class="reply-avatar" />
                    <div class="reply-content">
                      <div class="reply-main">
                        <span class="reply-nickname">{{ reply.replyNickName }}</span>
                        <span class="reply-action">回复了你的评论</span>
                        <span class="read-status-tag" :class="{ read: reply.isRead === 1, unread: reply.isRead === 0 }">
                          {{ reply.isRead === 1 ? '已读' : '未读' }}
                        </span>
                      </div>
                      <div class="reply-text">{{ reply.content }}</div>
                      <div class="original-comment">
                        <span class="original-label">你的评论：</span>
                        <span class="original-text">{{ reply.originalContent }}</span>
                      </div>
                      <div class="reply-footer">
                        <div class="reply-video-info">
                          <el-icon><el-icon-video-camera /></el-icon>
                          <span>{{ reply.videoName }}</span>
                        </div>
                        <span class="reply-time">{{ formatReplyTime(reply.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="reply-list" v-else-if="replyTab === 'sent' && sentCommentsList.length > 0">
                  <div 
                    class="reply-item sent-item" 
                    v-for="comment in sentCommentsList" 
                    :key="comment.videoCommentId"
                    @click="handleSentCommentClick(comment)"
                  >
                    <img :src="comment.replyAvatar || 'https://picsum.photos/48/48?random=1'" class="reply-avatar" />
                    <div class="reply-content">
                      <div class="reply-main">
                        <span class="reply-nickname">你</span>
                        <span class="reply-action">回复了 {{ comment.replyNickName }}</span>
                      </div>
                      <div class="reply-text">{{ comment.content }}</div>
                      <div class="original-comment" v-if="comment.originalContent">
                        <span class="original-label">原评论：</span>
                        <span class="original-text">{{ comment.originalContent }}</span>
                      </div>
                      <div class="reply-footer">
                        <div class="reply-video-info">
                          <el-icon><el-icon-video-camera /></el-icon>
                          <span>{{ comment.videoName }}</span>
                        </div>
                        <span class="comment-time">{{ formatReplyTime(comment.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="empty-reply" v-else>
                  <div class="empty-icon-wrapper">
                    <el-icon class="empty-icon"><el-icon-chat-dot-round /></el-icon>
                  </div>
                  <h4>{{ replyTab === 'received' ? t('chat.noReplyMessage') : '暂无发出的评论' }}</h4>
                  <p>{{ replyTab === 'received' ? t('chat.noReplyMessageDesc') : '您发出的评论会在这里显示' }}</p>
                </div>
              </div>
              
              <div class="interact-section" v-else-if="currentMenu === 'like'">
                <div class="interact-tabs">
                  <div 
                    class="tab-item" 
                    :class="{ active: interactTab === 'sent' }"
                    @click="interactTab = 'sent'"
                  >
                    <span>我发出的</span>
                  </div>
                  <div 
                    class="tab-item" 
                    :class="{ active: interactTab === 'received' }"
                    @click="interactTab = 'received'"
                  >
                    <span>我收到的</span>
                    <el-badge :value="receivedInteractUnread" :hidden="receivedInteractUnread === 0" class="tab-badge" />
                  </div>
                </div>
                <div class="interact-list" v-if="(interactTab === 'sent' ? sentInteractList : receivedInteractList).length > 0">
                  <div 
                    class="interact-item" 
                    :class="{ unread: interactTab === 'received' && item.isRead === 0 }"
                    v-for="item in (interactTab === 'sent' ? sentInteractList : receivedInteractList)" 
                    :key="item.id"
                    @click="handleInteractClick(item)"
                  >
                    <div class="interact-video-cover">
                      <img :src="item.videoImageUrl || 'https://picsum.photos/120/68?random=1'" :alt="item.videoName" />
                    </div>
                    <div class="interact-content">
                      <div class="interact-header">
                        <div class="interact-video-name">{{ item.videoName }}</div>
                        <span class="interact-read-status" :class="{ read: item.isRead === 1, unread: item.isRead === 0 }" v-if="interactTab === 'received'">
                          {{ item.isRead === 1 ? '已读' : '未读' }}
                        </span>
                      </div>
                      <div class="interact-tags">
                        <span class="interact-tag like" v-if="item.isLike === 1">
                          <el-icon><el-icon-star /></el-icon> 点赞
                        </span>
                        <span class="interact-tag collect" v-if="item.isCollect === 1">
                          <el-icon><el-icon-folder-add /></el-icon> 收藏
                        </span>
                        <span class="interact-tag comment" v-if="item.isComment === 1">
                          <el-icon><el-icon-chat-dot-round /></el-icon> 评论
                        </span>
                        <span class="interact-tag share" v-if="item.isShare === 1">
                          <el-icon><el-icon-share /></el-icon> 分享
                        </span>
                      </div>
                      <div class="interact-footer">
                        <div class="interact-user" v-if="interactTab === 'received' && item.userNickName">
                          <el-icon><el-icon-user /></el-icon>
                          <span>{{ item.userNickName }}</span>
                        </div>
                        <div class="interact-user" v-else-if="interactTab === 'sent'">
                          <el-icon><el-icon-user /></el-icon>
                          <span>我</span>
                        </div>
                        <span class="interact-time">{{ formatReplyTime(item.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="empty-interact" v-else>
                  <div class="empty-icon-wrapper">
                    <el-icon class="empty-icon"><el-icon-aim /></el-icon>
                  </div>
                  <h4>{{ interactTab === 'sent' ? '暂无发出的互动' : '暂无收到的互动' }}</h4>
                  <p>您的点赞、收藏、评论、分享会在这里显示</p>
                </div>
              </div>
              
              <div class="system-section" v-else-if="currentMenu === 'system'">
                <div class="system-tabs">
                  <div 
                    class="tab-item" 
                    :class="{ active: systemTab === 'review' }"
                    @click="systemTab = 'review'"
                  >
                    <span>审核记录</span>
                    <el-badge :value="reviewUnread" :hidden="reviewUnread === 0" class="tab-badge" />
                  </div>
                  <div 
                    class="tab-item" 
                    :class="{ active: systemTab === 'ban' }"
                    @click="systemTab = 'ban'"
                  >
                    <span>封禁记录</span>
                    <el-badge :value="banUnread" :hidden="banUnread === 0" class="tab-badge" />
                  </div>
                  <div 
                    class="tab-item" 
                    :class="{ active: systemTab === 'report' }"
                    @click="systemTab = 'report'"
                  >
                    <span>举报信息</span>
                    <el-badge :value="reportUnread" :hidden="reportUnread === 0" class="tab-badge" />
                  </div>
                </div>
                
                <div class="system-list" v-if="systemTab === 'review' && userReviewList.length > 0">
                  <div 
                    class="system-item" 
                    :class="{ unread: item.isRead === 0 }"
                    v-for="item in userReviewList" 
                    :key="item.id"
                  >
                    <div class="system-avatar">
                      <img :src="userInfoStore.info.avatar || 'https://picsum.photos/48/48?random=1'" alt="用户头像" />
                    </div>
                    <div class="system-content">
                      <div class="system-header">
                        <span class="system-title">{{ getReviewTitle(item) }}</span>
                        <span class="read-status-tag" :class="{ read: item.isRead === 1, unread: item.isRead === 0 }">
                          {{ item.isRead === 1 ? '已读' : '未读' }}
                        </span>
                      </div>
                      <div class="system-detail">
                        <span class="detail-label">审核类型：</span>
                        <span class="detail-value">{{ getTargetTypeName(item.targetType) }}</span>
                      </div>
                      <div class="system-detail">
                        <span class="detail-label">审核状态：</span>
                        <span class="detail-value" :class="getReviewStatusClass(item.status)">{{ getReviewStatusName(item.status) }}</span>
                      </div>
                      <div class="system-footer">
                        <span class="system-time">{{ formatReplyTime(item.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="system-list" v-else-if="systemTab === 'ban' && accountStatusList.length > 0">
                  <div 
                    class="system-item" 
                    :class="{ unread: item.isRead === 0 }"
                    v-for="item in accountStatusList" 
                    :key="item.accountStatusId"
                  >
                    <div class="system-avatar">
                      <img :src="userInfoStore.info.avatar || 'https://picsum.photos/48/48?random=1'" alt="用户头像" />
                    </div>
                    <div class="system-content">
                      <div class="system-header">
                        <span class="system-title">账号封禁通知</span>
                        <span class="read-status-tag" :class="{ read: item.isRead === 1, unread: item.isRead === 0 }">
                          {{ item.isRead === 1 ? '已读' : '未读' }}
                        </span>
                      </div>
                      <div class="system-detail">
                        <span class="detail-label">封禁原因：</span>
                        <span class="detail-value ban-reason">{{ item.reason }}</span>
                      </div>
                      <div class="system-detail">
                        <span class="detail-label">封禁时间：</span>
                        <span class="detail-value">{{ formatBanTime(item.startTime, item.endTime) }}</span>
                      </div>
                      <div class="system-footer">
                        <span class="system-time">{{ formatReplyTime(item.createTime) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="report-section" v-else-if="systemTab === 'report'">
                  <div class="report-sub-tabs">
                    <div 
                      class="sub-tab-item" 
                      :class="{ active: reportSubTab === 'received' }"
                      @click="reportSubTab = 'received'"
                    >
                      <span>我收到的举报</span>
                      <el-badge :value="receivedReports.filter(r => r.targetUserIsRead === 0).length" :hidden="receivedReports.filter(r => r.targetUserIsRead === 0).length === 0" class="tab-badge" />
                    </div>
                    <div 
                      class="sub-tab-item" 
                      :class="{ active: reportSubTab === 'sent' }"
                      @click="reportSubTab = 'sent'"
                    >
                      <span>我发出的举报</span>
                      <el-badge :value="sentReports.filter(r => r.userIsRead === 0).length" :hidden="sentReports.filter(r => r.userIsRead === 0).length === 0" class="tab-badge" />
                    </div>
                  </div>
                  
                  <div class="report-list" v-if="(reportSubTab === 'received' ? receivedReports : sentReports).length > 0">
                    <div 
                      class="report-item" 
                      :class="{ unread: reportSubTab === 'received' ? item.targetUserIsRead === 0 : item.userIsRead === 0 }"
                      v-for="item in (reportSubTab === 'received' ? receivedReports : sentReports)" 
                      :key="item.id"
                    >
                      <div class="report-type-badge" :class="item.reportType === 1 ? 'video' : 'comment'">
                        {{ item.reportType === 1 ? '视频' : '评论' }}
                      </div>
                      <div class="report-content">
                        <div class="report-header">
                          <span class="report-time">{{ formatReportTime(item.createTime) }}</span>
                          <div class="report-tags">
                            <el-tag :type="(reportSubTab === 'received' ? item.targetUserIsRead : item.userIsRead) === 0 ? 'danger' : 'success'" size="small">
                              {{ (reportSubTab === 'received' ? item.targetUserIsRead : item.userIsRead) === 0 ? '未读' : '已读' }}
                            </el-tag>
                            <el-tag :type="getReportStatusType(item.status)" size="small">
                              {{ getReportStatusText(item.status) }}
                            </el-tag>
                          </div>
                        </div>
                        <div class="report-body">
                          <div class="report-target" v-if="item.reportType === 1">
                            <img :src="item.videoImageUrl" class="target-thumbnail" v-if="item.videoImageUrl" />
                            <div class="target-info">
                              <h4>{{ item.videoName }}</h4>
                              <p>举报原因：{{ item.reason }}</p>
                              <p v-if="item.remark">备注：{{ item.remark }}</p>
                            </div>
                          </div>
                          <div class="report-target" v-else>
                            <div class="target-info">
                              <h4>评论内容</h4>
                              <p class="comment-text">{{ item.commentContent }}</p>
                              <p>举报原因：{{ item.reason }}</p>
                              <p v-if="item.remark">备注：{{ item.remark }}</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <el-empty v-else :description="reportSubTab === 'received' ? '暂无收到的举报' : '暂无发出的举报'" />
                </div>
                
                <div class="empty-system" v-else-if="systemTab !== 'report'">
                  <div class="empty-icon-wrapper">
                    <el-icon class="empty-icon"><el-icon-bell /></el-icon>
                  </div>
                  <h4>{{ systemTab === 'review' ? '暂无审核记录' : '暂无封禁记录' }}</h4>
                  <p>系统通知会在这里显示</p>
                </div>
              </div>
              
              <div class="empty-panel" v-else>
                <div class="empty-icon-wrapper large">
                  <el-icon class="empty-icon">
                    <el-icon-bell />
                  </el-icon>
                </div>
                <h4>{{ getEmptyTitle() }}</h4>
                <p>{{ getEmptyDesc() }}</p>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="avatarDialogVisible"
      :title="selectedContact?.nickName"
      width="400px"
      class="avatar-dialog"
      :show-close="true"
    >
      <div class="avatar-dialog-content">
        <img :src="getAvatarUrl(selectedContact?.avatar, selectedContact?.nickName)" :alt="selectedContact?.nickName" class="avatar-large" />
        <div class="avatar-actions">
          <el-button type="primary" @click="goToUserProfile">
            <el-icon><el-icon-user /></el-icon>
            {{ t('chat.viewProfile') }}
          </el-button>
        </div>
      </div>
    </el-dialog>

    <el-dialog
      v-model="deleteAllDialogVisible"
      :title="t('chat.clearHistory')"
      width="400px"
      class="delete-dialog"
    >
      <p>{{ t('chat.clearHistoryConfirm') }}</p>
      <template #footer>
        <el-button @click="deleteAllDialogVisible = false">{{ t('chat.cancel') }}</el-button>
        <el-button type="danger" @click="handleDeleteAll">{{ t('chat.confirm') }}</el-button>
      </template>
    </el-dialog>

    <div 
      v-if="messageMenuVisible" 
      class="message-context-menu" 
      :style="{ top: messageMenuPosition.y + 'px', left: messageMenuPosition.x + 'px' }"
    >
      <div class="menu-item" @click="handleDeleteMessage">
        <el-icon><el-icon-delete /></el-icon>
        <span>{{ t('chat.deleteMessage') }}</span>
      </div>
      <div class="menu-item" @click="handleMultiSelectFromMenu">
        <el-icon><el-icon-finished /></el-icon>
        <span>多选删除</span>
      </div>
    </div>
    
    <div 
      v-if="contactMenuVisible" 
      class="message-context-menu" 
      :style="{ top: contactMenuPosition.y + 'px', left: contactMenuPosition.x + 'px' }"
    >
      <div class="menu-item" @click="handleDeleteContact">
        <el-icon><el-icon-delete /></el-icon>
        <span>删除会话</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserInfoStore } from '@/stores/userinfo'
import { useOnlineStatusStore } from '@/stores/onlineStatus'
import { GetFriendList, GetFriendApplyList, HandleFriendRequest, GetChatHistory, MarkMessagesRead, DeleteMessages, DeleteSession, GetReplyMessages, MarkReplyRead, GetMyInteract, MarkInteractRead, GetUserAuditInfo, ReadAuditMessage, GetReport, ReadReport } from '@/api/Users'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const userInfoStore = useUserInfoStore()
const onlineStatusStore = useOnlineStatusStore()

const currentNav = ref('home')
const currentMenu = ref('message')
const searchQuery = ref('')
const selectedContact = ref(null)
const messageInput = ref('')
const messagesContainer = ref(null)
const loadingContacts = ref(false)

const systemTab = ref('review')
const reportSubTab = ref('received')
const userReviewList = ref([])
const accountStatusList = ref([])
const receivedReports = ref([])
const sentReports = ref([])

const reviewUnread = computed(() => {
  return userReviewList.value.filter(item => item.isRead === 0).length
})

const banUnread = computed(() => {
  return accountStatusList.value.filter(item => item.isRead === 0).length
})

const reportUnread = computed(() => {
  const receivedUnread = receivedReports.value.filter(item => item.targetUserIsRead === 0).length
  const sentUnread = sentReports.value.filter(item => item.userIsRead === 0).length
  return receivedUnread + sentUnread
})

const systemUnread = computed(() => {
  return reviewUnread.value + banUnread.value + reportUnread.value
})

const isMultiSelectMode = ref(false)
const selectedMessageIds = ref([])
const replyUnread = ref(0)
const friendApplyUnread = ref(0)
const replyMessagesList = ref([])
const replyTab = ref('received')
const sentCommentsList = ref([])
const interactList = ref([])
const interactTab = ref('sent')
const sentInteractList = ref([])
const receivedInteractList = ref([])

const receivedInteractUnread = computed(() => {
  return receivedInteractList.value.filter(item => item.isRead === 0).length
})

const messageUnread = computed(() => {
  return contacts.value.reduce((sum, contact) => sum + (contact.unreadCount || 0), 0)
})

const applyTab = ref('received')
const receivedApplyList = ref([])
const sentApplyList = ref([])

const avatarDialogVisible = ref(false)
const deleteAllDialogVisible = ref(false)
const messageMenuVisible = ref(false)
const messageMenuPosition = ref({ x: 0, y: 0 })
const selectedMessage = ref(null)

const contactMenuVisible = ref(false)
const contactMenuPosition = ref({ x: 0, y: 0 })
const selectedContactForMenu = ref(null)

const closeContactMenu = () => {
  contactMenuVisible.value = false
}

const headerBgImage = ref('https://lizhanghao.oss-cn-beijing.aliyuncs.com/%E8%8F%9C%E5%8D%95%E6%A0%8F/487-1800x70.jpg')

const contacts = ref([])

const messages = ref([])

let websocket = null
const wsConnected = ref(false)

const currentLangText = computed(() => {
  const langMap = { zh: '中文', en: 'English', ja: '日本語' }
  return langMap[locale.value] || '中文'
})

const filteredContacts = computed(() => {
  if (!searchQuery.value) return contacts.value
  return contacts.value.filter(contact => 
    contact.nickName.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const getAvatarUrl = (avatar, name) => {
  if (avatar) {
    return avatar.replace(/[\r\n`]/g, '').trim()
  }
  const colors = ['667eea', '764ba2', 'f093fb', 'f5576c', '4facfe', '00f2fe']
  const colorIndex = (name || '').length % colors.length
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}&background=${colors[colorIndex]}&color=fff&size=200`
}

const selectMenu = (menu) => {
  currentMenu.value = menu
  if (menu !== 'message') {
    selectedContact.value = null
    messages.value = []
  }
  if (menu === 'like') {
    markInteractAsRead()
  }
  if (menu === 'reply') {
    markReplyAsRead()
  }
  if (menu === 'system') {
    markSystemAsRead()
  }
}

const markSystemAsRead = async () => {
  const unreadReviews = userReviewList.value.filter(item => item.isRead === 0)
  const unreadBans = accountStatusList.value.filter(item => item.isRead === 0)
  
  if (unreadReviews.length > 0 || unreadBans.length > 0) {
    const data = {
      accountStatusIds: unreadBans.map(item => item.accountStatusId),
      userReviewIds: unreadReviews.map(item => item.id)
    }
    try {
      await ReadAuditMessage(data)
      unreadReviews.forEach(item => {
        item.isRead = 1
      })
      unreadBans.forEach(item => {
        item.isRead = 1
      })
    } catch (error) {
      console.error('标记系统通知已读失败:', error)
    }
  }
}

const markReplyAsRead = async () => {
  const unreadItems = replyMessagesList.value.filter(item => item.isRead === 0)
  if (unreadItems.length > 0) {
    const commentIds = unreadItems.map(item => item.videoCommentId)
    try {
      await MarkReplyRead(commentIds)
      unreadItems.forEach(item => {
        item.isRead = 1
      })
      replyUnread.value = 0
    } catch (error) {
      console.error('标记回复已读失败:', error)
    }
  }
}

const markInteractAsRead = async () => {
  const unreadItems = receivedInteractList.value.filter(item => item.isRead === 0)
  if (unreadItems.length > 0) {
    const interactIds = unreadItems.map(item => item.id)
    try {
      await MarkInteractRead(interactIds)
      unreadItems.forEach(item => {
        item.isRead = 1
      })
    } catch (error) {
      console.error('标记互动已读失败:', error)
    }
  }
}

const fetchFriendList = async () => {
  loadingContacts.value = true
  try {
    const res = await GetFriendList()
    if (res.code === 0 && res.data) {
      contacts.value = res.data.map(friend => ({
        encode: friend.enCode,
        nickName: friend.remark || friend.nickName,
        avatar: friend.avatar ? friend.avatar.replace(/[\r\n`]/g, '').trim() : '',
        isOnline: friend.isOnline !== undefined ? friend.isOnline : false,
        lastMessage: '',
        lastMessageTime: '',
        unreadCount: 0
      }))
    }
  } catch (error) {
    console.error('获取好友列表失败:', error)
  } finally {
    loadingContacts.value = false
  }
}

const fetchFriendApplyList = async () => {
  try {
    const res = await GetFriendApplyList()
    if (res.code === 0 && res.data) {
      const cleanAvatar = (avatar) => avatar ? avatar.replace(/[\r\n`]/g, '').trim() : ''
      
      receivedApplyList.value = (res.data.friendapplyUserVOList || []).map(item => ({
        enCode: item.enCode,
        nickName: item.nickName,
        avatar: cleanAvatar(item.avatar),
        remark: item.remark,
        status: Number(item.status)
      }))
      
      sentApplyList.value = (res.data.userapplyfriedVOList || []).map(item => ({
        enCode: item.enCode,
        nickName: item.nickName,
        avatar: cleanAvatar(item.avatar),
        remark: item.remark,
        status: Number(item.status)
      }))
      
      const pendingCount = receivedApplyList.value.filter(item => item.status === 3).length
      friendApplyUnread.value = pendingCount
    }
  } catch (error) {
    console.error('获取好友申请列表失败:', error)
  }
}

const fetchSystemNotifications = async () => {
  try {
    const res = await GetUserAuditInfo()
    if (res.code === 0 && res.data) {
      userReviewList.value = res.data.UserReview || []
      accountStatusList.value = res.data.accountStatusList || []
    }
  } catch (error) {
    console.error('获取系统通知失败:', error)
  }
}

const fetchReportInfo = async () => {
  try {
    const res = await GetReport()
    if (res.code === 0 && res.data) {
      receivedReports.value = res.data.mygetreport || []
      sentReports.value = res.data.mysendreport || []
    }
  } catch (error) {
    console.error('获取举报信息失败:', error)
  }
}

const formatReportTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

const getReportStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

const getReportStatusText = (status) => {
  const textMap = {
    0: '待处理',
    1: '已通过',
    2: '已驳回'
  }
  return textMap[status] || '未知'
}

const markReportsAsRead = async (type) => {
  let unreadReports = []
  if (type === 'received') {
    unreadReports = receivedReports.value.filter(r => r.targetUserIsRead === 0)
  } else {
    unreadReports = sentReports.value.filter(r => r.userIsRead === 0)
  }
  
  if (unreadReports.length === 0) return
  
  const reportIds = unreadReports.map(r => r.id)
  try {
    await ReadReport(reportIds)
    if (type === 'received') {
      receivedReports.value.forEach(r => {
        if (reportIds.includes(r.id)) r.targetUserIsRead = 1
      })
    } else {
      sentReports.value.forEach(r => {
        if (reportIds.includes(r.id)) r.userIsRead = 1
      })
    }
  } catch (error) {
    console.error('标记举报已读失败:', error)
  }
}

const getTargetTypeName = (type) => {
  const typeMap = {
    'AVATAR': '头像',
    'NICKNAME': '昵称',
    'VIDEO': '视频',
    'COMMENT': '评论'
  }
  return typeMap[type] || type
}

const getReviewStatusName = (status) => {
  const statusMap = {
    0: '待审核',
    1: '审核通过',
    2: '审核拒绝'
  }
  return statusMap[status] || '未知'
}

const getReviewStatusClass = (status) => {
  const classMap = {
    0: 'status-pending',
    1: 'status-pass',
    2: 'status-reject'
  }
  return classMap[status] || ''
}

const getReviewTitle = (item) => {
  const typeName = getTargetTypeName(item.targetType)
  const statusName = getReviewStatusName(item.status)
  return `${typeName}审核${statusName}`
}

const formatBanTime = (startTime, endTime) => {
  const formatDate = (dateStr) => {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  }
  return `${formatDate(startTime)} 至 ${formatDate(endTime)}`
}

const handleApply = async (toUserEncode, status) => {
  try {
    const res = await HandleFriendRequest(toUserEncode, status)
    if (res.code === 0) {
      ElMessage.success(status === 1 ? t('chat.agreeSuccess') : t('chat.rejectSuccess'))
      await fetchFriendApplyList()
      if (status === 1) {
        await fetchFriendList()
      }
    } else {
      ElMessage.error(res.message || t('chat.operateFailed'))
    }
  } catch (error) {
    console.error('处理好友申请失败:', error)
    ElMessage.error(t('chat.operateFailed'))
  }
}

const fetchReplyMessages = async () => {
  try {
    const res = await GetReplyMessages()
    if (res.code === 0 && res.data) {
      const cleanUrl = (url) => {
        if (!url) return ''
        return url.replace(/[\r\n`]/g, '').trim()
      }
      
      const mapItem = (item) => ({
        videoCommentId: item.videoCommentId,
        videoId: item.videoId,
        content: item.content,
        likeCount: item.likeCount,
        createTime: item.createTime,
        isRead: item.isRead,
        replyNickName: item.replyNickName,
        replyAvatar: cleanUrl(item.replyAvatar),
        replyEncode: item.replyEncode,
        originalContent: item.originalContent,
        videoName: item.videoName,
        videoImageUrl: cleanUrl(item.videoImageUrl),
        videoEncode: item.videoEncode
      })
      
      replyMessagesList.value = (res.data.replyMe || []).map(mapItem)
      sentCommentsList.value = (res.data.sent || []).map(mapItem)
      
      replyUnread.value = replyMessagesList.value.filter(item => item.isRead === 0).length
    }
  } catch (error) {
    console.error('获取回复消息失败:', error)
  }
}

const formatReplyTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return timeStr.split('T')[0]
}

const handleReplyClick = async (reply) => {
  if (reply.isRead === 0) {
    try {
      await MarkReplyRead([reply.videoCommentId])
      reply.isRead = 1
      replyUnread.value = Math.max(0, replyUnread.value - 1)
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  
  router.push({
    path: '/Video',
    query: { encode: reply.videoEncode }
  })
}

const fetchInteractList = async () => {
  try {
    const res = await GetMyInteract()
    if (res.code === 0 && res.data) {
      const cleanUrl = (url) => {
        if (!url) return ''
        return url.replace(/[\r\n`]/g, '').trim()
      }
      
      const mapItem = (item) => ({
        id: item.id,
        userEncode: item.userEncode,
        userNickName: item.userNickName || item.nickName || '',
        videoEncode: item.videoEncode,
        videoName: item.videoName,
        videoImageUrl: cleanUrl(item.videoImageUrl),
        isLike: item.isLike,
        isCollect: item.isCollect,
        isComment: item.isComment,
        isShare: item.isShare,
        createTime: item.createTime,
        isRead: item.isRead
      })
      
      sentInteractList.value = (res.data.myInteractDetailVO || []).map(mapItem)
      receivedInteractList.value = (res.data.otherInteractDetailVO || []).map(mapItem)
    }
  } catch (error) {
    console.error('获取互动列表失败:', error)
  }
}

const getInteractTags = (item) => {
  const tags = []
  if (item.isLike === 1) tags.push('点赞')
  if (item.isCollect === 1) tags.push('收藏')
  if (item.isComment === 1) tags.push('评论')
  if (item.isShare === 1) tags.push('分享')
  return tags
}

const handleInteractClick = async (item) => {
  if (interactTab.value === 'received' && item.isRead === 0) {
    try {
      await MarkInteractRead([item.id])
      item.isRead = 1
    } catch (error) {
      console.error('标记互动已读失败:', error)
    }
  }
  router.push({
    path: '/Video',
    query: { encode: item.videoEncode }
  })
}

const handleSentCommentClick = (comment) => {
  router.push({
    path: '/Video',
    query: { encode: comment.videoEncode }
  })
}

const viewUserProfile = (encode) => {
  if (encode) {
    router.push('/user/' + encode)
  }
}

const getStatusClass = (status) => {
  const classMap = {
    1: 'status-agreed',
    2: 'status-deleted',
    3: 'status-pending',
    4: 'status-rejected'
  }
  return classMap[status] || ''
}

const getStatusText = (status) => {
  const textMap = {
    1: t('chat.agreed'),
    2: t('chat.deleted'),
    3: t('chat.pending'),
    4: t('chat.rejected')
  }
  return textMap[status] || ''
}

const connectWebSocket = () => {
  const userEncode = userInfoStore.info?.enCode
  if (!userEncode) {
    console.error('无法获取用户encode')
    return
  }
  
  const wsUrl = `ws://localhost:8080/websocket/${userEncode}`
  
  if (websocket) {
    websocket.close()
  }
  
  websocket = new WebSocket(wsUrl)
  
  websocket.onopen = () => {
    console.log('WebSocket连接成功')
    wsConnected.value = true
  }
  
  websocket.onmessage = (event) => {
    console.log('=== WebSocket收到消息 ===')
    console.log('原始数据长度:', event.data.length)
    console.log('原始数据前200字符:', event.data.substring(0, Math.min(200, event.data.length)))
    
    try {
      const data = JSON.parse(event.data)
      console.log('解析后的数据:', {
        type: data.type,
        hasContent: !!data.content,
        msgType: data.msgType,
        fromEncode: data.fromEncode,
        toEncode: data.toEncode
      })
      
      if (data.type === 'onlineStatus') {
        if (Array.isArray(data.users)) {
          data.users.forEach(user => {
            updateContactOnlineStatus(user.userEncode, user.isOnline)
          })
        } else {
          updateContactOnlineStatus(data.userEncode, data.isOnline)
        }
      } else if (data.type === 'allOnlineStatus') {
        if (Array.isArray(data.onlineList)) {
          contacts.value.forEach(contact => {
            contact.isOnline = false
          })
          data.onlineList.forEach(user => {
            updateContactOnlineStatus(user.userEncode, user.isOnline)
          })
        }
      } else if (data.type === 'readStatus') {
        updateMessagesReadStatus(data.msgIds)
      } else if (data.content) {
        console.log('调用 handleReceivedMessage 处理消息')
        handleReceivedMessage(data)
      } else {
        console.log('消息没有content字段，跳过处理')
      }
    } catch (error) {
      console.error('解析消息失败:', error)
    }
  }
  
  websocket.onerror = (error) => {
    console.error('WebSocket连接错误:', error)
    wsConnected.value = false
  }
  
  websocket.onclose = () => {
    console.log('WebSocket连接关闭')
    wsConnected.value = false
    setTimeout(() => {
      if (!wsConnected.value) {
        console.log('尝试重新连接WebSocket...')
        connectWebSocket()
      }
    }, 3000)
  }
}

const handleReceivedMessage = (data) => {
  if (!data.content) return
  
  const now = new Date()
  const time = data.sendTime ? data.sendTime.split(' ')[1].substring(0, 5) : `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
  
  const currentUserId = userInfoStore.info?.enCode
  const isSentByMe = data.fromEncode && currentUserId && data.fromEncode === currentUserId
  
  const getMessageType = (msgType) => {
    const type = Number(msgType)
    if (type === 1) return 'image'
    if (type === 2) return 'file'
    return 'text'
  }
  
  const getLastMessageText = (data) => {
    const type = Number(data.msgType)
    if (type === 1) return '[图片]'
    if (type === 2) return '[文件]'
    return data.content
  }
  
  const getFileName = (content, msgType) => {
    if (msgType !== 2) return ''
    if (content && content.includes('data:')) {
      const match = content.match(/data:([^;]+);/)
      if (match) {
        const mime = match[1]
        const ext = mime.split('/')[1] || 'bin'
        return `文件.${ext}`
      }
    }
    return '未知文件'
  }
  
  if (isSentByMe) {
    if (selectedContact.value && selectedContact.value.encode === data.toEncode) {
      const existingMessage = messages.value.find(m => 
        m.content === data.content && m.isSent === true && m.tempId
      )
      const msgTypeNum = Number(data.msgType)
      if (existingMessage) {
        existingMessage.msgId = data.msgId
        existingMessage.tempId = null
        existingMessage.status = data.isRead === 1 ? 'read' : 'sent'
        existingMessage.msgType = msgTypeNum
        existingMessage.type = getMessageType(data.msgType)
        existingMessage.fileName = data.fileName || getFileName(data.content, data.msgType)
      } else {
        messages.value.push({
          msgId: data.msgId,
          content: data.content,
          msgType: msgTypeNum,
          type: getMessageType(data.msgType),
          fileName: data.fileName || getFileName(data.content, data.msgType),
          isSent: true,
          time: time,
          status: data.isRead === 1 ? 'read' : 'sent'
        })
      }
      selectedContact.value.lastMessage = getLastMessageText(data)
      selectedContact.value.lastMessageTime = time
      scrollToBottom()
    }
  } else {
    if (selectedContact.value && selectedContact.value.encode === data.fromEncode) {
      const msgTypeNum = Number(data.msgType)
      messages.value.push({
        msgId: data.msgId,
        content: data.content,
        msgType: msgTypeNum,
        type: getMessageType(data.msgType),
        fileName: data.fileName || getFileName(data.content, data.msgType),
        isSent: false,
        time: time,
        status: data.isRead === 1 ? 'read' : 'sent'
      })
      selectedContact.value.lastMessage = getLastMessageText(data)
      selectedContact.value.lastMessageTime = time
      if (data.isRead === 1) {
        scrollToBottom()
      }
    } else {
      const contact = contacts.value.find(c => c.encode === data.fromEncode)
      if (contact) {
        contact.lastMessage = getLastMessageText(data)
        contact.lastMessageTime = time
        contact.unreadCount = (contact.unreadCount || 0) + 1
      }
    }
  }
}

const getMenuTitle = () => {
  const titles = {
    system: t('chat.systemMessage'),
    reply: t('chat.replyMe'),
    like: '我的互动',
    friendApply: t('chat.friendApply')
  }
  return titles[currentMenu.value] || ''
}

const getEmptyTitle = () => {
  const titles = {
    system: t('chat.noSystemMessage')
  }
  return titles[currentMenu.value] || ''
}

const getEmptyDesc = () => {
  const descs = {
    system: t('chat.noSystemMessageDesc')
  }
  return descs[currentMenu.value] || ''
}

const selectContact = async (contact) => {
  selectedContact.value = contact
  contact.unreadCount = 0
  messages.value = []
  await loadChatHistory(contact.encode)
  scrollToBottom()
}

const loadChatHistory = async (toUserEncode) => {
  try {
    const res = await GetChatHistory(toUserEncode)
    if (res.code === 0 && res.data) {
      console.log('=== 加载聊天历史 ===')
      console.log('历史消息数量:', res.data.length)
      console.log('第一条消息:', res.data[0])
      
      const currentUserId = userInfoStore.info?.enCode
      const unreadMsgIds = []
      
      const getMessageType = (msgType) => {
        const type = Number(msgType)
        if (type === 1) return 'image'
        if (type === 2) return 'file'
        return 'text'
      }
      
      const getFileName = (content, msgType) => {
        const type = Number(msgType)
        if (type !== 2) return ''
        if (content && content.includes('data:')) {
          const match = content.match(/data:([^;]+);/)
          if (match) {
            const mime = match[1]
            const ext = mime.split('/')[1] || 'bin'
            return `文件.${ext}`
          }
        }
        return '未知文件'
      }
      
      messages.value = res.data.map(msg => {
        if (msg.toEncode === currentUserId && msg.isRead === 0) {
          unreadMsgIds.push(msg.msgId)
        }
        const msgTypeNum = Number(msg.msgType)
        return {
          msgId: msg.msgId,
          content: msg.content,
          msgType: msgTypeNum,
          type: getMessageType(msg.msgType),
          fileName: msg.fileName || getFileName(msg.content, msg.msgType),
          isSent: msg.fromEncode === currentUserId,
          time: msg.sendTime ? msg.sendTime.split(' ')[1].substring(0, 5) : '',
          status: msg.isRead === 1 ? 'read' : 'sent'
        }
      })
      
      if (unreadMsgIds.length > 0) {
        await markMessagesAsRead(toUserEncode, unreadMsgIds)
      }
    }
  } catch (error) {
    console.error('加载聊天历史失败:', error)
  }
}

const markMessagesAsRead = async (toUserEncode, messageIds) => {
  try {
    await MarkMessagesRead(toUserEncode, messageIds)
  } catch (error) {
    console.error('标记消息已读失败:', error)
  }
}

const deleteMessage = async (msgId) => {
  if (!selectedContact.value || !msgId) return
  try {
    const res = await DeleteMessages(selectedContact.value.encode, [msgId])
    if (res.code === 0) {
      messages.value = messages.value.filter(m => m.msgId !== msgId)
      ElMessage.success(t('chat.deleteSuccess') || '消息已删除')
    }
  } catch (error) {
    console.error('删除消息失败:', error)
    ElMessage.error(t('chat.deleteFailed') || '删除失败')
  }
}

const deleteAllMessages = async () => {
  if (!selectedContact.value) return
  try {
    const lastMessageId = messages.value.length > 0 ? [messages.value[messages.value.length - 1].id] : []
    const res = await DeleteMessages(selectedContact.value.encode, lastMessageId)
    if (res.code === 0) {
      messages.value = []
      selectedContact.value.lastMessage = ''
      ElMessage.success(t('chat.deleteAllSuccess') || '聊天记录已清空')
    }
  } catch (error) {
    console.error('清空聊天记录失败:', error)
    ElMessage.error(t('chat.deleteFailed') || '删除失败')
  }
}

const showMessageMenu = (event, message) => {
  selectedMessage.value = message
  messageMenuPosition.value = { x: event.clientX, y: event.clientY }
  messageMenuVisible.value = true
}

const showContactContextMenu = (event, contact) => {
  selectedContactForMenu.value = contact
  contactMenuPosition.value = { x: event.clientX, y: event.clientY }
  contactMenuVisible.value = true
}

const handleDeleteContact = () => {
  contactMenuVisible.value = false
  if (selectedContactForMenu.value) {
    deleteContactChat(selectedContactForMenu.value)
    selectedContactForMenu.value = null
  }
}

const handleDeleteMessage = async () => {
  messageMenuVisible.value = false
  if (selectedMessage.value) {
    await deleteMessage(selectedMessage.value.msgId)
    selectedMessage.value = null
  }
}

const handleMultiSelectFromMenu = () => {
  messageMenuVisible.value = false
  if (selectedMessage.value) {
    enterMultiSelectMode()
    const msgId = selectedMessage.value.msgId || selectedMessage.value.id
    if (msgId) {
      selectedMessageIds.value.push(msgId)
    }
    selectedMessage.value = null
  }
}

const enterMultiSelectMode = () => {
  isMultiSelectMode.value = true
  selectedMessageIds.value = []
}

const exitMultiSelectMode = () => {
  isMultiSelectMode.value = false
  selectedMessageIds.value = []
}

const toggleMessageSelection = (message) => {
  const msgId = message.msgId || message.id
  const index = selectedMessageIds.value.indexOf(msgId)
  if (index > -1) {
    selectedMessageIds.value.splice(index, 1)
  } else {
    selectedMessageIds.value.push(msgId)
  }
}

const deleteSelectedMessages = async () => {
  if (selectedMessageIds.value.length === 0 || !selectedContact.value) return
  
  try {
    await DeleteMessages(selectedContact.value.encode, selectedMessageIds.value)
    messages.value = messages.value.filter(m => !selectedMessageIds.value.includes(m.id))
    exitMultiSelectMode()
    ElMessage.success('删除成功')
  } catch (error) {
    console.error('删除消息失败:', error)
    ElMessage.error('删除失败')
  }
}

const deleteContactChat = async (contact) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除与 ${contact.nickName} 的聊天记录吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await DeleteSession(contact.encode)
    
    contacts.value = contacts.value.filter(c => c.encode !== contact.encode)
    
    if (selectedContact.value?.encode === contact.encode) {
      selectedContact.value = null
      messages.value = []
    }
    
    await fetchFriendList()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除对话失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const confirmDeleteAll = () => {
  deleteAllDialogVisible.value = true
}

const handleDeleteAll = async () => {
  deleteAllDialogVisible.value = false
  await deleteAllMessages()
}

const updateContactOnlineStatus = (userEncode, isOnline) => {
  onlineStatusStore.updateStatus(userEncode, isOnline)
  const contact = contacts.value.find(c => c.encode === userEncode)
  if (contact) {
    contact.isOnline = isOnline
  }
}

const updateMessagesReadStatus = (msgIds) => {
  if (!msgIds || !Array.isArray(msgIds)) return
  msgIds.forEach(msgId => {
    const message = messages.value.find(m => m.msgId === msgId)
    if (message) {
      message.status = 'read'
    }
  })
}

const sendMessage = () => {
  if (!messageInput.value.trim() || !selectedContact.value) return
  
  const messageContent = messageInput.value.trim()
  
  if (wsConnected.value && websocket) {
    const currentUserId = userInfoStore.info?.enCode
    const messageData = {
      fromEncode: currentUserId,
      toEncode: selectedContact.value.encode,
      content: messageContent,
      msgType: 0
    }
    websocket.send(JSON.stringify(messageData))
    
    const now = new Date()
    const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    
    messages.value.push({
      tempId: `temp_${Date.now()}`,
      content: messageContent,
      msgType: 0,
      isSent: true,
      time: time,
      status: 'sent'
    })
    
    selectedContact.value.lastMessage = messageContent
    selectedContact.value.lastMessageTime = time
    
    messageInput.value = ''
    scrollToBottom()
  } else {
    ElMessage.warning(t('chat.connectionLost') || '连接已断开，正在重新连接...')
    connectWebSocket()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const refreshContacts = () => {
  ElMessage.success(t('chat.refreshSuccess'))
}

const goToUserProfile = () => {
  if (selectedContact.value?.encode) {
    avatarDialogVisible.value = false
    router.push('/user/' + selectedContact.value.encode)
  }
}

const showAvatarDialog = () => {
  avatarDialogVisible.value = true
}

const viewProfile = () => {
  if (selectedContact.value?.encode) {
    router.push('/user/' + selectedContact.value.encode)
  }
}

const startVideoCall = () => {
  ElMessage.info(t('chat.videoCallHint'))
}

const startVoiceCall = () => {
  ElMessage.info(t('chat.voiceCallHint'))
}

const handleImageUpload = () => {
  console.log('=== 点击发送图片按钮 ===')
  console.log('当前消息列表状态:', JSON.stringify(messages.value.map(m => ({ msgId: m.msgId, msgType: m.msgType, type: m.type }))))
  
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    console.log('=== 文件选择对话框关闭 ===')
    console.log('当前消息列表状态(关闭后):', JSON.stringify(messages.value.map(m => ({ msgId: m.msgId, msgType: m.msgType, type: m.type }))))
    
    const files = e.target.files
    console.log('选择的文件:', files)
    
    if (!files || files.length === 0) {
      console.log('用户取消了文件选择')
      return
    }
    
    const file = files[0]
    if (!file) {
      console.log('没有选择文件')
      return
    }
    
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过5MB')
      return
    }
    
    try {
      const compressedImage = await compressImage(file, 800, 800, 0.7)
      if (compressedImage) {
        sendImageMessage(compressedImage)
      }
    } catch (error) {
      console.error('图片处理失败:', error)
      ElMessage.error('图片处理失败，请重试')
    }
  }
  input.click()
}

const compressImage = (file, maxWidth, maxHeight, quality) => {
  return new Promise((resolve) => {
    const reader = new FileReader()
    reader.onload = (event) => {
      const img = new Image()
      img.onload = () => {
        const originalData = event.target.result
        const targetSize = 50 * 1024 // 目标大小：50KB的Base64数据
        
        // 如果图片本身就很小，不需要压缩
        if (originalData.length < targetSize) {
          console.log('图片较小，无需压缩')
          resolve(originalData)
          return
        }
        
        const canvas = document.createElement('canvas')
        let width = img.width
        let height = img.height
        
        // 计算缩放比例，限制最大尺寸
        const maxDimension = 600
        if (width > maxDimension || height > maxDimension) {
          const ratio = Math.min(maxDimension / width, maxDimension / height)
          width = Math.floor(width * ratio)
          height = Math.floor(height * ratio)
        }
        
        canvas.width = width
        canvas.height = height
        
        const ctx = canvas.getContext('2d')
        ctx.drawImage(img, 0, 0, width, height)
        
        // 逐步降低质量直到满足大小要求
        let compressedData = canvas.toDataURL('image/jpeg', 0.7)
        let currentQuality = 0.7
        
        while (compressedData.length > targetSize && currentQuality > 0.1) {
          currentQuality -= 0.1
          compressedData = canvas.toDataURL('image/jpeg', currentQuality)
        }
        
        // 如果压缩后反而更大，使用原始图片
        if (compressedData.length > originalData.length) {
          console.log('压缩后更大，使用原始图片')
          resolve(originalData)
          return
        }
        
        console.log('图片压缩完成:', {
          原始大小: (originalData.length / 1024).toFixed(2) + 'KB',
          压缩后大小: (compressedData.length / 1024).toFixed(2) + 'KB',
          压缩质量: currentQuality.toFixed(1),
          尺寸: `${width}x${height}`
        })
        resolve(compressedData)
      }
      img.src = event.target.result
    }
    reader.readAsDataURL(file)
  })
}

const sendImageMessage = (imageData) => {
  if (!selectedContact.value) {
    ElMessage.warning('请先选择聊天对象')
    return
  }
  
  if (wsConnected.value && websocket) {
    const currentUserId = userInfoStore.info?.enCode
    const messageData = {
      fromEncode: currentUserId,
      toEncode: selectedContact.value.encode,
      content: imageData,
      msgType: 1
    }
    
    const jsonString = JSON.stringify(messageData)
    console.log('发送图片消息 - 完整JSON结构验证:')
    console.log('  toEncode:', messageData.toEncode)
    console.log('  msgType:', messageData.msgType, '(类型:', typeof messageData.msgType, ')')
    console.log('  content长度:', messageData.content.length)
    console.log('  content前50字符:', messageData.content.substring(0, 50) + '...')
    
    try {
      websocket.send(jsonString)
      console.log('✓ WebSocket发送成功')
    } catch (error) {
      console.error('✗ WebSocket发送失败:', error)
      ElMessage.error('发送失败，请重试')
      return
    }
    
    const now = new Date()
    const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    
    messages.value.push({
      tempId: `temp_${Date.now()}`,
      content: imageData,
      msgType: 1,
      type: 'image',
      isSent: true,
      time: time,
      status: 'sent'
    })
    
    console.log('=== 发送图片后添加的消息 ===')
    console.log('消息列表最后一条:', messages.value[messages.value.length - 1])
    
    selectedContact.value.lastMessage = '[图片]'
    selectedContact.value.lastMessageTime = time
    
    scrollToBottom()
  } else {
    console.log('WebSocket状态:', { wsConnected: wsConnected.value, websocket: !!websocket })
    ElMessage.warning(t('chat.connectionLost') || '连接已断开，正在重新连接...')
    connectWebSocket()
  }
}

const previewImage = (imageUrl) => {
  window.open(imageUrl, '_blank')
}

const handleFileUpload = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.pdf,.doc,.docx,.xls,.xlsx,.txt,.zip,.rar'
  input.onchange = async (e) => {
    const files = e.target.files
    if (!files || files.length === 0) {
      console.log('用户取消了文件选择')
      return
    }
    
    const file = files[0]
    if (!file) {
      console.log('没有选择文件')
      return
    }
    
    if (file.size > 5 * 1024 * 1024) {
      ElMessage.warning('文件大小不能超过5MB')
      return
    }
    
    try {
      const reader = new FileReader()
      reader.onload = (event) => {
        const fileData = event.target.result
        sendFileMessage(fileData, file.name)
      }
      reader.onerror = () => {
        ElMessage.error('文件读取失败')
      }
      reader.readAsDataURL(file)
    } catch (error) {
      console.error('文件处理失败:', error)
      ElMessage.error('文件处理失败，请重试')
    }
  }
  input.click()
}

const sendFileMessage = (fileData, fileName) => {
  if (!selectedContact.value) return
  
  if (wsConnected.value && websocket) {
    const currentUserId = userInfoStore.info?.enCode
    const messageData = {
      fromEncode: currentUserId,
      toEncode: selectedContact.value.encode,
      content: fileData,
      msgType: 2
    }
    websocket.send(JSON.stringify(messageData))
    
    const now = new Date()
    const time = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
    
    messages.value.push({
      tempId: `temp_${Date.now()}`,
      content: fileData,
      fileName: fileName,
      msgType: 2,
      type: 'file',
      isSent: true,
      time: time,
      status: 'sent'
    })
    
    selectedContact.value.lastMessage = `[文件] ${fileName}`
    selectedContact.value.lastMessageTime = time
    
    scrollToBottom()
  } else {
    ElMessage.warning(t('chat.connectionLost') || '连接已断开，正在重新连接...')
    connectWebSocket()
  }
}

const downloadFile = (fileData, fileName) => {
  const link = document.createElement('a')
  link.href = fileData
  link.download = fileName || 'download'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

const formatFileSize = (base64String) => {
  if (!base64String) return '0 B'
  const padding = base64String.endsWith('==') ? 2 : base64String.endsWith('=') ? 1 : 0
  const sizeInBytes = (base64String.length * 3 / 4) - padding
  
  if (sizeInBytes < 1024) {
    return `${sizeInBytes.toFixed(0)} B`
  } else if (sizeInBytes < 1024 * 1024) {
    return `${(sizeInBytes / 1024).toFixed(2)} KB`
  } else {
    return `${(sizeInBytes / (1024 * 1024)).toFixed(2)} MB`
  }
}

const changeLocale = async (lang) => {
  locale.value = lang
  localStorage.setItem('locale', lang)
  
  const langMap = {
    'zh': '中文',
    'en': '英文',
    'ja': '日文'
  }
  const result = await userInfoStore.updateLanguage(langMap[lang])
  if (result) {
    ElMessage.success(t('loginRegister.languageUpdated'))
  }
}

const goHome = () => router.push('/First')
const goToFirst = () => router.push('/First')
const goToAI = () => router.push('/ai/home')
const goToCenter = () => router.push('/center')

watch(() => route.query.user, (userEncode) => {
  if (userEncode) {
    currentMenu.value = 'message'
    const contact = contacts.value.find(c => c.encode === userEncode)
    if (contact) {
      selectContact(contact)
    } else {
      contacts.value.unshift({
        encode: userEncode,
        nickName: '新联系人',
        avatar: '',
        isOnline: true,
        lastMessage: '',
        lastMessageTime: '刚刚',
        unreadCount: 0
      })
      selectContact(contacts.value[0])
    }
  }
}, { immediate: true })

watch(interactTab, (newVal) => {
  if (newVal === 'received' && currentMenu.value === 'like') {
    markInteractAsRead()
  }
})

watch(systemTab, (newVal) => {
  if (newVal === 'ban') {
    ElMessage.info({
      message: '保持良好的信誉有助于学习',
      duration: 3000
    })
  }
  if (newVal === 'report') {
    markReportsAsRead(reportSubTab.value)
  }
})

watch(reportSubTab, (newVal) => {
  if (systemTab.value === 'report') {
    markReportsAsRead(newVal)
  }
})

onMounted(() => {
  fetchFriendList()
  fetchFriendApplyList()
  fetchReplyMessages()
  fetchInteractList()
  fetchSystemNotifications()
  fetchReportInfo()
  connectWebSocket()
  if (route.query.user) {
    currentMenu.value = 'message'
    const userEncode = route.query.user
    const contact = contacts.value.find(c => c.encode === userEncode)
    if (contact) {
      selectContact(contact)
    }
  }
  document.addEventListener('click', closeMessageMenu)
  document.addEventListener('visibilitychange', handleVisibilityChange)
  window.addEventListener('focus', handleWindowFocus)
})

onUnmounted(() => {
  if (websocket) {
    websocket.close()
    websocket = null
  }
  document.removeEventListener('click', closeMessageMenu)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('focus', handleWindowFocus)
})

const handleWindowFocus = async () => {
  if (selectedContact.value) {
    await refreshMessagesAndMarkRead()
  }
}

const refreshMessagesAndMarkRead = async () => {
  if (!selectedContact.value) return
  
  const res = await GetChatHistory(selectedContact.value.encode)
  if (res.code === 0 && res.data) {
    const currentUserId = userInfoStore.info?.enCode
    const unreadMsgIds = []
    
    const getMessageType = (msgType) => {
      const type = Number(msgType)
      if (type === 1) return 'image'
      if (type === 2) return 'file'
      return 'text'
    }
    
    const getFileName = (content, msgType) => {
      const type = Number(msgType)
      if (type !== 2) return ''
      if (content && content.includes('data:')) {
        const match = content.match(/data:([^;]+);/)
        if (match) {
          const mime = match[1]
          const ext = mime.split('/')[1] || 'bin'
          return `文件.${ext}`
        }
      }
      return '未知文件'
    }
    
    messages.value = res.data.map(msg => {
      if (msg.toEncode === currentUserId && msg.isRead === 0) {
        unreadMsgIds.push(msg.msgId)
      }
      const msgTypeNum = Number(msg.msgType)
      return {
        msgId: msg.msgId,
        content: msg.content,
        msgType: msgTypeNum,
        type: getMessageType(msg.msgType),
        fileName: msg.fileName || getFileName(msg.content, msg.msgType),
        isSent: msg.fromEncode === currentUserId,
        time: msg.sendTime ? msg.sendTime.split(' ')[1].substring(0, 5) : '',
        status: msg.isRead === 1 ? 'read' : 'sent'
      }
    })
    
    if (unreadMsgIds.length > 0) {
      await markMessagesAsRead(selectedContact.value.encode, unreadMsgIds)
    }
    
    scrollToBottom()
  }
}

const handleVisibilityChange = async () => {
  if (document.visibilityState === 'visible' && selectedContact.value) {
    await refreshMessagesAndMarkRead()
  }
}

const closeMessageMenu = () => {
  messageMenuVisible.value = false
  contactMenuVisible.value = false
}
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  overflow: hidden;
}

.chat-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 80px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
  flex-shrink: 0;
}

.header-inner {
  max-width: 1920px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 40px;
}

.header-left {
  display: flex;
  align-items: center;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px 16px;
  border-radius: 12px;
}

.navbar-brand:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: scale(1.02);
}

.brand-logo {
  font-size: 32px;
  font-weight: 800;
  color: #00d4aa;
  margin: 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name h2 {
  font-size: 16px;
  color: white;
  margin: 0;
  font-weight: 600;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.brand-name p {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
  white-space: nowrap;
}

.header-center {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: center;
  max-width: 600px;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 22px;
  font-size: 15px;
  color: white;
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  text-decoration: none;
  position: relative;
  overflow: hidden;
  font-weight: 500;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5), 0 2px 4px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
  flex-shrink: 0;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ff6b6b, #feca57);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 20px;
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item:hover {
  color: white;
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.nav-icon {
  font-size: 18px;
  position: relative;
  z-index: 1;
}

.nav-text {
  position: relative;
  z-index: 1;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-switch {
  margin-left: 8px;
}

.lang-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #f1f2f3;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #333;
}

.lang-btn:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  transform: translateY(-2px);
}

.lang-text {
  font-weight: 500;
}

.dropdown-arrow {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.lang-btn:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.user-avatar-wrapper {
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 2px;
  border-radius: 50%;
  background: linear-gradient(135deg, #00a1d6, #00b5e2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-avatar-wrapper:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(0, 161, 214, 0.4);
}

.user-avatar-wrapper.active {
  background: linear-gradient(135deg, #ff6b6b, #feca57);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid white;
}

.chat-container {
  flex: 1;
  margin-top: 80px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 20px;
  min-height: 0;
}

.chat-content {
  flex: 1;
  display: flex;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 0;
}

.left-menu {
  width: 200px;
  background: linear-gradient(180deg, #f0f2f5 0%, #e8ecf4 100%);
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.menu-header {
  padding: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.menu-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.menu-list {
  flex: 1;
  padding: 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  margin-bottom: 8px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.menu-item:hover {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.menu-item:hover .menu-icon {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.menu-item.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
}

.menu-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  color: #667eea;
  transition: all 0.3s ease;
}

.menu-item.active .menu-icon {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.menu-text {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.menu-badge {
  margin-left: auto;
}

.contacts-sidebar {
  width: 300px;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  background: #fafbfc;
  flex-shrink: 0;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
  background: white;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 17px;
  color: #1a1a1a;
  font-weight: 600;
  letter-spacing: -0.3px;
}

.refresh-btn {
  padding: 6px;
  border-radius: 8px;
}

.refresh-btn:hover {
  background: #f5f5f5;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  background: #f5f6f7;
  box-shadow: none;
  border: 1px solid transparent;
  padding: 4px 12px;
}

.search-input :deep(.el-input__wrapper):hover {
  border-color: #e0e0e0;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  background: white;
  border-color: #667eea;
}

.search-icon {
  color: #999;
}

.contacts-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  cursor: pointer;
  transition: all 0.25s ease;
  border-radius: 14px;
  margin-bottom: 6px;
  position: relative;
}

.contact-main {
  display: flex;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.contact-actions {
  opacity: 0;
  transition: opacity 0.2s ease;
}

.contact-item:hover .contact-actions {
  opacity: 1;
}

.delete-contact-btn {
  padding: 8px;
  color: #f56c6c;
}

.delete-contact-btn:hover {
  background: rgba(245, 108, 108, 0.1);
}

.contact-item:hover {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.contact-item.active {
  background: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.12);
}

.contact-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 0 4px 4px 0;
}

.contact-avatar {
  position: relative;
  flex-shrink: 0;
}

.contact-avatar img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: none;
}

.online-status {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #d1d5db;
  border: 2px solid #fafbfc;
  transition: background 0.3s ease;
}

.online-status.online {
  background: #22c55e;
  box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.2);
}

.contact-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.contact-name {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.contact-name .name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: -0.2px;
}

.contact-name .time {
  font-size: 11px;
  color: #9ca3af;
  font-weight: 500;
}

.contact-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contact-preview .preview {
  font-size: 13px;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.contact-preview .unread {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 12px;
  margin-left: 8px;
  font-weight: 600;
  min-width: 20px;
  text-align: center;
}

.empty-contacts {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}

.empty-contacts .empty-icon {
  font-size: 36px;
  color: #d1d5db;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fafbfc;
  min-width: 0;
  overflow: hidden;
}

.chat-header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  background: white;
  flex-shrink: 0;
}

.chat-user-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.chat-avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  object-fit: cover;
  border: none;
  transition: transform 0.2s ease;
}

.avatar-wrapper:hover .chat-avatar {
  transform: scale(1.05);
}

.status-dot {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #d1d5db;
  border: 2px solid white;
}

.status-dot.online {
  background: #22c55e;
  box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.2);
}

.chat-user-detail h4 {
  margin: 0;
  font-size: 16px;
  color: #1a1a1a;
  font-weight: 600;
  letter-spacing: -0.2px;
}

.user-status {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.user-status.online {
  color: #22c55e;
}

.chat-actions {
  display: flex;
  gap: 6px;
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  font-size: 18px;
  color: #6b7280;
}

.action-btn:hover {
  background: #f3f4f6;
  color: #667eea;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  background: linear-gradient(180deg, #f8f9fb 0%, #ffffff 100%);
  min-height: 0;
}

.messages-wrapper {
  padding: 24px;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.message-date-divider {
  text-align: center;
  margin: 16px 0;
}

.message-date-divider span {
  background: rgba(0, 0, 0, 0.04);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  animation: messageIn 0.3s ease;
  cursor: default;
}

.message-item.selected {
  background: rgba(102, 126, 234, 0.1);
  border-radius: 8px;
  margin: 0 -8px 16px;
  padding: 8px;
}

.message-checkbox {
  display: flex;
  align-items: center;
  padding-right: 8px;
}

@keyframes messageIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.sent {
  justify-content: flex-end;
}

.message-item.received {
  justify-content: flex-start;
}

.message-avatar img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
  border: none;
}

.message-content {
  max-width: 65%;
  display: flex;
  flex-direction: column;
}

.message-bubble {
  padding: 14px 18px;
  border-radius: 20px;
  position: relative;
  word-break: break-word;
}

.message-item.sent .message-bubble {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-bottom-right-radius: 6px;
}

.message-item.received .message-bubble {
  background: white;
  color: #1a1a1a;
  border-bottom-left-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.message-bubble p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}

.message-bubble.image-message {
  padding: 4px;
  background: transparent !important;
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.message-image:hover {
  transform: scale(1.02);
}

.message-bubble.file-message {
  padding: 12px;
  background: white !important;
  cursor: pointer;
}

.file-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 200px;
  max-width: 280px;
}

.file-icon {
  font-size: 36px;
  color: #409eff;
  flex-shrink: 0;
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.file-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 12px;
  color: #999;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  padding: 0 4px;
}

.message-time {
  font-size: 11px;
  color: #9ca3af;
  font-weight: 500;
}

.message-item.sent .message-meta {
  justify-content: flex-end;
}

.message-status {
  display: flex;
  align-items: center;
  margin-left: 6px;
  visibility: visible !important;
  opacity: 1 !important;
}

.message-status .read-text {
  color: #22c55e;
  font-size: 12px;
  font-weight: 500;
}

.message-status .sent-text {
  color: #9ca3af;
  font-size: 12px;
  font-weight: 500;
}

/* 确保已读状态图标显示 */
.message-item.sent .message-status {
  visibility: visible !important;
  opacity: 1 !important;
}

.empty-messages {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #aaa;
}

.empty-animation {
  margin-bottom: 16px;
}

.chat-bubble-animation {
  display: flex;
  gap: 6px;
}

.chat-bubble-animation span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  animation: bounce 1.4s ease-in-out infinite both;
}

.chat-bubble-animation span:nth-child(1) {
  animation-delay: -0.32s;
}

.chat-bubble-animation span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.no-chat-selected {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #aaa;
  padding: 40px;
}

.no-chat-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.no-chat-selected .select-icon {
  font-size: 50px;
  color: #ccc;
}

.no-chat-selected h3 {
  margin: 0 0 8px;
  color: #666;
  font-size: 16px;
}

.no-chat-selected p {
  margin: 0;
  font-size: 13px;
  text-align: center;
  max-width: 280px;
}

.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #e8e8e8;
  background: white;
  display: flex;
  gap: 12px;
  align-items: flex-end;
  flex-shrink: 0;
}

.input-tools {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.tool-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  font-size: 20px;
  cursor: pointer;
}

.tool-btn.image-btn {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.tool-btn.image-btn:hover {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.tool-btn.file-btn {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
}

.tool-btn.file-btn:hover {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  color: white;
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.input-wrapper {
  flex: 1;
  min-width: 0;
}

.message-input :deep(.el-textarea__inner) {
  border-radius: 16px;
  resize: none;
  border: 2px solid #e8e8e8;
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.5;
  background: #fafbfc;
  transition: all 0.25s ease;
}

.message-input :deep(.el-textarea__inner):hover {
  border-color: #d1d5db;
}

.message-input :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.send-btn {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  transition: all 0.25s ease;
  flex-shrink: 0;
}

.send-btn:not(.has-content) {
  background: #f3f4f6;
  border-color: #f3f4f6;
  color: #d1d5db;
}

.send-btn.has-content {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.35);
}

.send-btn.has-content:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(102, 126, 234, 0.45);
}

.message-context-menu {
  position: fixed;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  min-width: 150px;
  z-index: 9999;
}

.message-context-menu .menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #333;
  font-size: 14px;
}

.message-context-menu .menu-item:hover {
  background: #f5f5f5;
  color: #f56c6c;
}

.message-context-menu .menu-item .el-icon {
  font-size: 16px;
}

.delete-dialog p {
  text-align: center;
  color: #666;
  font-size: 15px;
  margin: 20px 0;
}

.content-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  min-width: 0;
  min-height: 0;
  overflow: hidden;
}

.panel-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.panel-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
  min-height: 0;
}

.panel-content > * {
  width: 100%;
  min-height: 0;
}

.empty-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.empty-icon-wrapper.large {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-icon-wrapper.large .empty-icon {
  font-size: 48px;
  color: #ccc;
}

.empty-panel h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #666;
}

.empty-panel p {
  margin: 0;
  font-size: 13px;
  color: #aaa;
  max-width: 300px;
}

.friend-apply-section {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.apply-tabs {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.apply-tabs .el-radio-group {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 4px;
}

.apply-tabs :deep(.el-radio-button__inner) {
  border-radius: 8px;
  border: none;
  padding: 10px 24px;
  font-weight: 500;
}

.apply-tabs :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tab-badge {
  margin-left: 6px;
}

.apply-table-container {
  flex: 1;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.apply-table-header {
  display: flex;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 16px 20px;
  font-weight: 600;
  font-size: 14px;
}

.apply-table-body {
  max-height: calc(100% - 56px);
  overflow-y: auto;
}

.apply-table-row {
  display: flex;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
  align-items: center;
}

.apply-table-row:hover {
  background: rgba(102, 126, 234, 0.05);
}

.apply-table-row:last-child {
  border-bottom: none;
}

.table-col {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.col-avatar {
  width: 80px;
  min-width: 80px;
}

.col-name {
  width: 180px;
  min-width: 180px;
  justify-content: flex-start;
  padding-right: 12px;
}

.col-desc {
  flex: 1;
  min-width: 200px;
  justify-content: flex-start;
  padding: 0 16px;
}

.col-status {
  width: 100px;
  min-width: 100px;
}

.col-action {
  width: 180px;
  min-width: 180px;
  gap: 8px;
}

.apply-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
  border: 2px solid #667eea;
  transition: all 0.3s;
}

.apply-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-name {
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: color 0.3s;
  max-width: 160px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

.user-name:hover {
  color: #667eea;
}

.apply-message {
  color: #666;
  font-size: 13px;
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.status-agreed {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.status-tag.status-rejected {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.status-tag.status-pending {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.status-tag.status-deleted {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.action-done {
  color: #ccc;
}

.apply-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.apply-empty .empty-icon {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 16px;
}

.apply-empty span {
  font-size: 14px;
}

.reply-section {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  width: 100%;
  flex: 1;
  overflow: hidden;
}

.reply-tabs {
  display: flex;
  gap: 0;
  padding: 16px 20px;
  background: #fafbfc;
  border-bottom: 1px solid #eee;
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  background: transparent;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  border: none;
  border-bottom: 3px solid transparent;
  position: relative;
}

.tab-item:hover {
  background: rgba(102, 126, 234, 0.05);
  color: #667eea;
}

.tab-item.active {
  background: transparent;
  color: #667eea;
  border-bottom: 3px solid #667eea;
}

.tab-badge {
  margin-left: 4px;
}

.reply-list {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  width: 100%;
  min-height: 0;
}

.reply-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 24px;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border-bottom: 1px solid #e8e8e8;
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.reply-item:hover {
  background: #f9fafb;
}

.reply-item.unread {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02), rgba(118, 75, 162, 0.02));
  border-left: 4px solid #667eea;
}

.reply-item.sent-item {
  background: #fafbfc;
}

.reply-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid rgba(102, 126, 234, 0.15);
}

.reply-video-cover {
  width: 80px;
  height: 45px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  position: relative;
}

.reply-video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.reply-item:hover .video-cover-overlay {
  opacity: 1;
}

.video-cover-overlay .el-icon {
  font-size: 24px;
  color: white;
}

.reply-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;
}

.reply-main {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.reply-nickname {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.reply-action {
  font-size: 13px;
  color: #999;
}

.unread-tag {
  font-size: 11px;
  color: white;
  background: #f56c6c;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.reply-text {
  font-size: 14px;
  color: #333;
  line-height: 1.7;
  word-break: break-word;
}

.original-comment {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 14px;
  background: #f5f7fa;
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.original-label {
  font-size: 12px;
  color: #667eea;
  font-weight: 500;
  flex-shrink: 0;
}

.original-text {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  word-break: break-word;
}

.reply-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 4px;
}

.reply-video-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
}

.reply-video-info .el-icon {
  font-size: 14px;
  color: #667eea;
}

.reply-time, .comment-time {
  font-size: 12px;
  color: #bbb;
}

.empty-reply {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #999;
  flex: 1;
  width: 100%;
}

.empty-reply .empty-icon-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-reply .empty-icon {
  font-size: 48px;
  color: #ccc;
}

.empty-reply h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #666;
}

.empty-reply p {
  margin: 0;
  font-size: 13px;
  color: #aaa;
}

.interact-section {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  width: 100%;
  flex: 1;
  overflow: hidden;
}

.interact-tabs {
  display: flex;
  gap: 0;
  padding: 16px 20px;
  background: #fafbfc;
  border-bottom: 1px solid #eee;
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.interact-list {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  width: 100%;
  min-height: 0;
}

.interact-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px 24px;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  border-bottom: 1px solid #e8e8e8;
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.interact-item:hover {
  background: #f9fafb;
}

.interact-item.unread {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02), rgba(118, 75, 162, 0.02));
  border-left: 4px solid #667eea;
}

.interact-video-cover {
  width: 120px;
  height: 68px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.interact-video-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.interact-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.interact-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.interact-video-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.interact-read-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  flex-shrink: 0;
}

.interact-read-status.read {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.interact-read-status.unread {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.read-status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  flex-shrink: 0;
}

.read-status-tag.read {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.read-status-tag.unread {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.interact-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.interact-user {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.interact-user .el-icon {
  font-size: 14px;
}

.interact-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.interact-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.interact-tag.like {
  background: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.interact-tag.collect {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.interact-tag.comment {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.interact-tag.share {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.interact-time {
  font-size: 12px;
  color: #bbb;
}

.empty-interact {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #999;
  flex: 1;
  width: 100%;
}

.empty-interact .empty-icon-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-interact .empty-icon {
  font-size: 48px;
  color: #ccc;
}

.empty-interact h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #666;
}

.empty-interact p {
  margin: 0;
  font-size: 13px;
  color: #aaa;
}

.system-section {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.system-tabs {
  display: flex;
  gap: 0;
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: white;
  flex-shrink: 0;
}

.system-list {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.system-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px 24px;
  background: white;
  border-bottom: 1px solid #e8e8e8;
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.system-item:hover {
  background: #f9fafb;
}

.system-item.unread {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02), rgba(118, 75, 162, 0.02));
  border-left: 4px solid #667eea;
}

.system-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.system-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.system-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.system-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.system-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.system-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.detail-label {
  color: #999;
  flex-shrink: 0;
}

.detail-value {
  color: #333;
}

.detail-value.status-pending {
  color: #e6a23c;
}

.detail-value.status-pass {
  color: #67c23a;
}

.detail-value.status-reject {
  color: #f56c6c;
}

.detail-value.ban-reason {
  color: #f56c6c;
  font-weight: 500;
}

.system-footer {
  display: flex;
  justify-content: flex-end;
}

.system-time {
  font-size: 12px;
  color: #999;
}

.empty-system {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-system .empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f0f0, #e8e8e8);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.empty-system .empty-icon {
  font-size: 40px;
  color: #ccc;
}

.empty-system h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #666;
}

.empty-system p {
  margin: 0;
  font-size: 13px;
  color: #aaa;
}

.report-section {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.report-sub-tabs {
  display: flex;
  gap: 0;
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: white;
  flex-shrink: 0;
}

.sub-tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  background: transparent;
  border-radius: 0;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  border: none;
  border-bottom: 3px solid transparent;
  position: relative;
}

.sub-tab-item:hover {
  background: rgba(102, 126, 234, 0.05);
  color: #667eea;
}

.sub-tab-item.active {
  background: transparent;
  color: #667eea;
  border-bottom: 3px solid #667eea;
}

.report-list {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.report-item {
  position: relative;
  display: flex;
  gap: 16px;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #e8e8e8;
  transition: all 0.3s;
}

.report-item.unread {
  background: #fff9e6;
  border-left: 4px solid #faad14;
}

.report-item:hover {
  background: #f9fafb;
}

.report-type-badge {
  flex-shrink: 0;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.report-type-badge.video {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.report-type-badge.comment {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.report-content {
  flex: 1;
  min-width: 0;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.report-time {
  font-size: 12px;
  color: #999;
}

.report-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.report-body {
  margin-top: 8px;
}

.report-target {
  display: flex;
  gap: 12px;
}

.target-thumbnail {
  width: 120px;
  height: 68px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.target-info {
  flex: 1;
  min-width: 0;
}

.target-info h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.target-info p {
  margin: 4px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.target-info .comment-text {
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
  margin-bottom: 8px;
}

@media (max-width: 900px) {
  .header-inner {
    padding: 0 15px;
  }
  
  .header-center {
    display: none;
  }
  
  .chat-container {
    padding: 10px;
  }
  
  .left-menu {
    width: 60px;
  }
  
  .menu-header {
    display: none;
  }
  
  .menu-item {
    flex-direction: column;
    padding: 12px 8px;
    gap: 4px;
  }
  
  .menu-text {
    font-size: 10px;
  }
  
  .menu-badge {
    display: none;
  }
  
  .contacts-sidebar {
    width: 200px;
  }
}

@media (max-width: 600px) {
  .left-menu {
    display: none;
  }
  
  .contacts-sidebar {
    display: none;
  }
  
  .chat-main {
    flex: 1;
  }
  
  .message-content {
    max-width: 80%;
  }
}

.avatar-dialog {
  border-radius: 16px;
}

.avatar-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  padding: 20px 0;
}

.avatar-large {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #667eea;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.avatar-actions {
  display: flex;
  gap: 12px;
}
</style>
