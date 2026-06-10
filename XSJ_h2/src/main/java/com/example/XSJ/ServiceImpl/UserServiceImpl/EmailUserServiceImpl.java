package com.example.XSJ.ServiceImpl.UserServiceImpl;

import com.example.XSJ.Pojo.UserDTO.UserBaseDate;

public interface EmailUserServiceImpl {
    boolean checkuserbyEmail(String toEmail);

    boolean checkexit(String userid);

    int insertuser(String id, String encode, String toEmail, String realIp);


    UserBaseDate GetUserBaseByEmil(String email);

    void updatePassword(String toEmail, String password);
}
