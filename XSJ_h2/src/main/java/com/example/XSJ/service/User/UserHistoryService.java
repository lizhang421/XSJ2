package com.example.XSJ.service.User;

import com.example.XSJ.Mapper.UserMapper.UserHistoryServiceMapper;
import com.example.XSJ.Pojo.User.UserVideoInteractDTO;
import com.example.XSJ.ServiceImpl.UserServiceImpl.UserHistoryServiceimpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserHistoryService implements UserHistoryServiceimpl {


    @Autowired
    private UserHistoryServiceMapper userHistoryServiceMapper;

    @Override
    public List<String> queryRecentThreeDaysActiveUsers() {
        return userHistoryServiceMapper.queryrecentThreeDaysActiveUsers();
    }

    @Override
    public List<UserWatchHistoryDTO> queryUserHistory(String userId) {
        return  userHistoryServiceMapper.queryUserHistory(userId);
    }

    @Override
    public List<UserVideoInteractDTO> queryUserInteract(String userId, List<String> videoIds) {
        return userHistoryServiceMapper.queryUserInteract(userId,videoIds);
    }
}
