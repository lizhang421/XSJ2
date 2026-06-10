package com.example.XSJ.Pojo.UserDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {
    /**
     * 用户名：非空、长度2-20
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度需在2-20个字符之间")
    private String username;

    /**
     * 密码：非空、符合密码规则（字母+数字，6-16位）
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,16}$", message = "密码需为6-16位字母+数字组合")
    private String password;

    /**
     * 手机号：非空、符合手机号规则
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    /**
     * 验证码：非空、6位数字
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码需为6位数字")
    private String smsCode;


}
