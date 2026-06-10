package com.example.XSJ.Controller.User;

import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import com.example.XSJ.ServiceImpl.UserServiceImpl.EmailUserServiceImpl;
import com.example.XSJ.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.hibernate.validator.internal.metadata.aggregated.rule.ReturnValueMayOnlyBeMarkedOnceAsCascadedPerHierarchyLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.example.XSJ.utils.RegexUtils.isEmailInvalid;

/**
 * 邮箱账号的登录注册
 */
@Slf4j
@RestController
@RequestMapping("/email")
public class Email {
    @Autowired
    private EmailVerifyUtils emailVerifyUtils;

    @Autowired
    private EmailUserServiceImpl emailUserServiceImpl;

    LocalAntiBrushService localAntiBrushService = new LocalAntiBrushService();

    /**
     * 邮箱发送验证码接口
     *
     * @param toEmail
     * @return
     */
    @PostMapping("/send")
    public Result sendVerifyCode(@RequestParam String toEmail,
                                 @RequestParam(value = "type", required = true) int type  //1是登录 2是注册 3是忘记密码
    ) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }

        if (isEmailInvalid(toEmail)) {
            return Result.error("请输入正确的邮箱格式");
        }

        boolean check = emailUserServiceImpl.checkuserbyEmail(toEmail);

        try {
            // 加入缓存设置防刷机制
            if (type == 1) {
                // 登录防刷机制，防止有人暴力破解
                if (localAntiBrushService.checkEmailLoginCodeLimit(toEmail)) {
                    return Result.error("您已多次获取验证码，请稍后再试。");
                }
                var result1 = emailVerifyUtils.sendVerifyCode(toEmail, type);
                return Result.success(result1);

            } else if (type == 2) {
                // 注册：邮箱不存在才能发
                if (!check) {
                    return Result.error("账号已存在");
                }
                // 注册防刷缓存
                if (localAntiBrushService.checkEmailRegisterCodeLimit(toEmail)) {
                    return Result.error("您已多次获取验证码，请稍后再试。");
                }
                var result1 = emailVerifyUtils.sendVerifyCode(toEmail, type);
                return Result.success(result1);

            }

            else if (type == 3) {
                // 忘记密码：邮箱必须存在才能找回
                if (check) {
                    return Result.error("账号不存在，无法找回密码");
                }

                if (localAntiBrushService.checkEmailForgetPwdCodeLimit(toEmail)) {
                    return Result.error("您已多次获取验证码，请稍后再试。");
                }
                var result1 = emailVerifyUtils.sendVerifyCode(toEmail, type);
                return Result.success(result1);
            }

        } catch (Exception e) {
            return Result.error("验证码发送失败，请稍后在试");
        }

        return Result.success("验证码已发送");
    }





    /**
     * 通过邮箱修改密码的接口
     */
    @PostMapping("/updatePasswordbyem")
    public Result updatePasswordbyem(
            @RequestParam(value = "toEmail", required = true) String toEmail,
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "type", required = true) int type,
            @RequestParam(value = "newPassword", required = true) String newPassword,
            HttpServletRequest request
    ) {
        try {

            String realIp = IpUtils.getRealIp(request);
            if (localAntiBrushService.checkIpLimit(realIp)) {
                return Result.error("频繁请求，请稍后再试");
            }


            if (toEmail == null || toEmail.trim().isEmpty()) {
                return Result.error("邮箱不能为空");
            }
            if (isEmailInvalid(toEmail)) {
                return Result.error("邮箱格式不正确");
            }
            if (RegexUtils.isPasswordInvalid(newPassword)) {
                return Result.error("密码格式不正确");
            }
            if (code == null || code.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }

            if (type != 3) {
                return Result.error("系统异常");
            }


            boolean emailExists = emailUserServiceImpl.checkuserbyEmail(toEmail);
            if (emailExists) {
                return Result.error("账号不存在，无法找回密码");
            }

            boolean verifySuccess = emailVerifyUtils.verifyCode(toEmail, code, type);
            if (!verifySuccess) {
                return Result.error("验证码错误或已过期");
            }


            String encodedPassword = PasswordEncoder.encode(newPassword);
            emailUserServiceImpl.updatePassword(toEmail, encodedPassword);

            localAntiBrushService.clearEmailForgetPwdCaptchaCount(toEmail);

            return Result.success("密码修改成功");

        } catch (Exception e) {
            log.error("邮箱找回密码接口异常，邮箱：{}，错误：{}", toEmail, e.getMessage());
            return Result.error("系统繁忙，请稍后重试");
        }
    }



    /**
     * 邮箱注册接口使用邮箱注册，如果用户信息不完善的话要提示用户完善个人信息
     *
     * @param toEmail
     * @param code
     * @return
     */
    @PostMapping("/register")
    public Result sendCode(
            @RequestParam(value = "toEmail", required = true) String toEmail,
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "type", required = true) int type,
            HttpServletRequest request
    ) {
        try {
            //防止恶意注册对某个ip限流访问
            String realIp = IpUtils.getRealIp(request);

            if (localAntiBrushService.checkIpLimit(realIp)) {
                return Result.error("频繁注册，请稍后再试");
            }

            if (toEmail == null || toEmail.trim().isEmpty()) {
                return Result.error("邮箱不能为空");
            }
            if (isEmailInvalid(toEmail)) {
                return Result.error("邮箱格式不正确");
            }
            if (code == null || code.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }
            if (type != 2) {
                return Result.error("系统异常");
            }
            //查询数据库是否存在当前注册邮箱
            boolean emailuser = emailUserServiceImpl.checkuserbyEmail(toEmail);
            boolean result2 = emailVerifyUtils.verifyCode(toEmail, code, type);
            if (!result2) {
                return Result.error("验证码错误");
            }
            if (emailuser) {
                //开始注册逻辑
                //生成encode
                Map map = Message();
                String id = map.get("userid").toString();
                String encode = map.get("encode").toString();
                //提交参数
                int resu = emailUserServiceImpl.insertuser(id, encode, toEmail, realIp);
                if (resu > 0) {
                    //清除注册缓存
                    localAntiBrushService.clearEmailRegisterCaptchaCount(toEmail);
                    return Result.success("用户注册成功");
                }

            } else {
                return Result.error("邮箱已存在");
            }
        } catch (Exception e) {
            log.error("注册接口异常，邮箱：{}，IP：{}", toEmail, e);

            return Result.error("系统繁忙，请稍后重试");
        }
        return Result.success();
    }


    @PostMapping("/login")
    public Result login(
            @RequestParam(value = "email", required = true) String email,
            @RequestParam(value = "code", required = true) String code,
            @RequestParam(value = "type", required = true) int type
    ) {
        //登录接口
        if (email == null || email.trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (isEmailInvalid(email)) {
            return Result.error("请你输入正确格式的邮箱");
        }
        if (code == null || code.trim().isEmpty()) {
            return Result.error("验证码不能为空");
        }
        boolean type1 = type == 1 ? true : false;

        if (!type1) {
            return Result.error("系统异常");
        }

        try {
            if (localAntiBrushService.checkEmailLoginCodeLimit(email)) {
                return Result.error("验证码获取频繁，请检查您的邮箱，请稍后再试");
            }
            //查询数据库只有存在当前账号的时候才会给当前账号发送验证码
            boolean result2 = emailVerifyUtils.verifyCode(email, code, type);
            if (!result2) {
                return Result.error("验证码错误");
            }
            boolean check = emailUserServiceImpl.checkuserbyEmail(email);
            if (check) {
                return Result.error("该账号暂未关联平台账号，如需使用请先完成注册");
            }
            //校验通过
            //登录成功给他token
            UserBaseDate date = emailUserServiceImpl.GetUserBaseByEmil(email);
            Map<String, Object> map = new HashMap<>();
            map.put("EnCode", date.getEnCode());
            map.put("RoleBase", date.getRoleBase());
            String token = JwtUtil.genToken(map);
            //清除缓存
            localAntiBrushService.clearEmailLoginCaptchaCount(email);
            return Result.success(token);
        } catch (Exception e) {
            return Result.error("系统异常");
        }


    }


    //生成id和用户编码
    public Map Message() {
        //生成id
        String userid;
        String encode;
        String mixChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String middle = "";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        do {
            String uuid = UUID.randomUUID().toString();
            userid = "UI" + uuid;
            int code = random.nextInt(10000, 99999);
            for (int i = 0; i < 8; i++) {
                int randomindex = random.nextInt(mixChars.length());
                middle = result.append(mixChars.charAt(randomindex)).toString();
            }
            encode = "USER" + "_" + "EM" + "_" + middle + "_" + code;
        } while (!emailUserServiceImpl.checkexit(userid));
        Map<String, String> map = new HashMap<>();
        map.put("userid", userid);
        map.put("encode", encode);
        return map;
    }


}
