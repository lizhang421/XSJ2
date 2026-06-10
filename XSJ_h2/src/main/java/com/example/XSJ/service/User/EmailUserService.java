package com.example.XSJ.service.User;

import com.example.XSJ.Mapper.UserMapper.EmailUserMapper;
import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import com.example.XSJ.ServiceImpl.UserServiceImpl.EmailUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailUserService implements EmailUserServiceImpl {
    @Autowired
    private EmailUserMapper emailUserMapper;

    @Override
    public boolean checkuserbyEmail(String toEmail) {
        int a =emailUserMapper.checkuser(toEmail);
        if (a==0){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkexit(String userid) {
        int a =emailUserMapper.checkid(userid);
        if (a==0){
            return true;
        }
        return false;
    }

    @Transactional(
            rollbackFor = Exception.class,
            propagation = Propagation.REQUIRED, // 显式声明默认传播行为
            timeout = 30 // 事务超时时间（避免长事务占用数据库连接）
    )
    @Override
    public int insertuser(String id, String encode, String toEmail, String realIp) {
        int  result= emailUserMapper.insertintoemuser(id,encode,toEmail,realIp);
        return result;
    }

    @Override
    public UserBaseDate GetUserBaseByEmil(String email) {
        return emailUserMapper.getbasedatebyemail(email);
    }

    @Override
    public void updatePassword(String toEmail, String password) {
        emailUserMapper.updatePassword(toEmail,password);
    }


}
