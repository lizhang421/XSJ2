package com.example.XSJ.Mapper.UserMapper;

import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

@Mapper
public interface EmailUserMapper {
   @Select("select count(*) from user where Email=#{toEmail}")
    int checkuser(String toEmail);

   @Select("select count(*) from user where UserId=#{userid}")
    int checkid(String userid);

    @Insert("insert into user (UserId,EnCode,RealName,NickName,Email,CreatTime,UpdateTime,RegisterIp) values (#{id},#{encode},#{encode},#{encode},#{toEmail}," +
            "now(),now(),#{realIp})")
    int insertintoemuser(String id, String encode, String toEmail, String realIp);

    @Select("select * from user where Email=#{email}")
    UserBaseDate getbasedatebyemail(String email);

    @Update("update user set Password=#{password} where Email=#{toEmail}")
    void updatePassword(String toEmail, String password);
}
