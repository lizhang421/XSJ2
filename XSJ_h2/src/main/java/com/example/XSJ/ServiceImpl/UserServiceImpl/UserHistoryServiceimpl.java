package com.example.XSJ.ServiceImpl.UserServiceImpl;

import com.example.XSJ.Pojo.User.UserVideoInteractDTO;
import com.example.XSJ.service.User.UserWatchHistoryDTO;

import java.util.List;

public interface UserHistoryServiceimpl {


    List<String> queryRecentThreeDaysActiveUsers();

    List<UserWatchHistoryDTO> queryUserHistory(String userId);

    List<UserVideoInteractDTO> queryUserInteract(String userId, List<String> videoIds);
}
