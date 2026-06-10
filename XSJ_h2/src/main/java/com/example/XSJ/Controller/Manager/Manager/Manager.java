package com.example.XSJ.Controller.Manager.Manager;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.example.XSJ.Pojo.Result;
import com.example.XSJ.Pojo.System.PoolExtractRuleUpdateDTO;
import com.example.XSJ.Pojo.System.SysScoewConfig;
import com.example.XSJ.Pojo.System.UserReview;
import com.example.XSJ.Pojo.User.User;
import com.example.XSJ.Pojo.User.UserUpdateDTO;
import com.example.XSJ.Pojo.Video.VideoAuditTag;
import com.example.XSJ.Pojo.VideoDTo.VideoPendingDTO;
import com.example.XSJ.ServiceImpl.Manager.Manager.ManagerServiceImpl;
import com.example.XSJ.utils.JwtUtil;
import com.example.XSJ.utils.PasswordEncoder;
import com.example.XSJ.utils.RegexUtils;
import com.example.XSJ.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.cert.TrustAnchor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * 管理员管理
 * 这个系统的登录注册没有采用和用户一样的登录注册前校验方式
 * 这里需要根据实际的业务进行实时的体哦癌症这里只根据token身份及逆行拦截器的校验
 * 这个地方有待完善
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class Manager {


    @Autowired
    private ManagerServiceImpl managerService;


    /**
     * 管理员登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return
     */

    @PostMapping("/login")
    public Result login(String phone, String password) {
        //验证账号是否存在
        if (phone.isEmpty() || password.isEmpty()) {
            return Result.error("手机号或密码不能为空");
        }
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.error("手机号格式错误");
        }
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.error("手机号格式不正确");
        }
        if (RegexUtils.isPasswordInvalid(password)) {
            return Result.error("密码格式错误");
        }
        try {
            //验证完成之后查询当前账号是否存在
            User user = managerService.findmanagermessagebyphone(phone);

            if (user == null) {
                return Result.error("用户不存在");
            }
            if (!PasswordEncoder.matches(user.getPassWord(), password)) {
                return Result.error("密码错误");
            }
            //密码正确返回给用户token
            Map<String, Object> map = new HashMap<>();
            map.put("EnCode", user.getEnCode());
            map.put("RoleBase", user.getRoleBase());
            String token = JwtUtil.genToken(map);
            return Result.success(token);

        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败");

        }
    }


    /**
     * 获取待审核视频
     */
    @GetMapping("/getAuditVideo")
    public Result getAuditVideo() {
        //这是只有管理员才会进入的系统
        try {
            List<VideoPendingDTO> videoPendingDTOList = managerService.getauditvideo();
            return Result.success(videoPendingDTOList);
        } catch (Exception e) {
            log.error("获取待审核视频失败", e);
            return Result.error("获取待审核视频失败");
        }
    }

    /**
     * 视频审核结果，分情况 如果视频审核成功通过了要吧视频解析解析然后存储到aliyun不同的分辨率
     * 以及处理用户上传的图片裁剪成Heloise的尺寸大小
     * 之后要吧视频放到video中
     * 如果视频不合格就直接没通过
     */
    @PostMapping("/auditVideo")
    public Result auditVideo(
            @RequestBody Map<String, Object> params) {
        String videoPendingId = (String) params.get("videoPendingId");
        Integer reviewStatus = (Integer) params.get("reviewStatus");
        List<String> videoAuditTagId = (List<String>) params.get("videoAuditTagId");
        if (StrUtil.isBlank(videoPendingId)) {
            return Result.error("视频ID不能为空");
        }
        if (reviewStatus != 0 && reviewStatus != 1) {
            return Result.error("审核状态不合法");
        }

        if (reviewStatus == 0) {
            if (CollUtil.isEmpty(videoAuditTagId)) {
                return Result.error("审核驳回必须选择标签");
            }
        }
        try {
            managerService.auditVideo(videoPendingId, reviewStatus, videoAuditTagId);
            return Result.success();
        } catch (Exception e) {
            log.error("视频审核失败", e);
            return Result.error("视频审核失败");
        }

    }


    /**
     * 获取审核结果选择的列表
     *
     * @return
     */

    @GetMapping("/GetauditVideoResult")
    public Result GetauditVideoResult() {
        try {
            List<VideoAuditTag> videoAuditTagList = managerService.getauditVideoResult();
            return Result.success(videoAuditTagList);

        } catch (Exception e) {
            log.error("获取不合格理由失败", e);
        }
        return Result.success();
    }


    /**
     * 添加审核结果
     * 审核用户上传的图片和头像
     *
     * @param
     * @param VideoAuditTagId
     * @return
     */
    @PostMapping("/uploadShenHeImageResult")
    public Result uploadShenHeImageResult(String userviewid, String VideoAuditTagId, int result) {
        try {
            managerService.uploadShenHeImageResult(userviewid, VideoAuditTagId, result);
            return Result.success();
        } catch (Exception e) {

            log.error("添加审核结果是失败", e);
            return Result.error("添加审核结果是失败");
        }
    }


    /**
     * 管理员获得待审核的内容
     */
    @PostMapping("/getShenHeImage")
    public Result getShenHeImage(
            @Validated @RequestBody List<Integer> status
    ) {
        List<UserReview> userReviewList = new ArrayList<>();
        try {
            userReviewList = managerService.getShenHeImage(status);
            return Result.success(userReviewList);
        } catch (Exception e) {
            log.error("获取待审核图片失败", e);
            return Result.error("获取待审核图片失败");
        }

    }


    /**
     * 管理员获取全部用户的的 信息
     */

    @GetMapping("/getUserList")
    public Result getUserList(int pageNum, int pageSize, Integer status, String key, Integer roleBase) {
        try {
            Result result = managerService.getUserList(pageNum, pageSize, status, key, roleBase);
            return result;

        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error();
        }
    }

    /**
     * 修改账号状态，管理员可以在这里进行账号装填=的修改
     */
    @PostMapping("/updateUserStatus")
    public Result updateUserStatus(String encode, Integer status) {
        try {
            Result result = managerService.updateUserStatus(encode, status);
            return result;
        } catch (Exception e) {
            log.error("修改用户状态失败", e);
            return Result.error();
        }
    }

    /**
     * 管理员获取当前账号的账号信息
     */
    @GetMapping("/getManagerMessage")
    public Result getManagerMessage() {
        try {
            Result result = managerService.getManagerMessage();
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取管理员信息失败", e);
            return Result.error();
        }
    }


    /**
     * 超级管理员获取配置信息
     */
    @GetMapping("/getSysScoreConfig")
    public Result getSysScoreConfig() {
        try {
            Result result = managerService.ManagerSysConfig();
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取配置信息失败", e);
            return Result.error();
        }
    }


    /**
     * 管理员修改视频得分算法
     */
    @PostMapping("/updateVideoSortWeight")
    public Result updatavideosortweight(@RequestBody SysScoewConfig SysScoewConfig) {
        try {
            Result result = managerService.updatesysSortWeight(SysScoewConfig);
            return result;
        } catch (Exception e) {
            log.error("修改视频得分算法失败", e);
            return Result.error();
        }
    }

    /**
     * 修改池内选取规则
     */
    @PostMapping("/updatePoolConfig")
    public Result updatePoolConfig(@RequestBody PoolExtractRuleUpdateDTO PoolExtractRuleUpdateDTO) {
        try {
            Result result = managerService.updatePoolConfig(PoolExtractRuleUpdateDTO);
            return result;
        } catch (Exception e) {
            log.error("修改池内选取规则失败", e);
            return Result.error();
        }
    }

    /**
     * 管理员增加池
     */
    @PostMapping("/addPool")
    public Result addPool(@RequestBody PoolExtractRuleUpdateDTO PoolExtractRuleUpdateDTO) {
        try {
            Result result = managerService.addPool(PoolExtractRuleUpdateDTO);
            return result;
        } catch (Exception e) {
            log.error("管理员增加池失败", e);
            return Result.error();
        }
    }

    /**
     * 删除池但是不能删除统配池
     */
    @PostMapping("/deletePool")
    public Result deletePool(Long poolId) {
        try {
            Result result = managerService.deletePool(poolId);
            return result;
        } catch (Exception e) {
            log.error("删除池失败", e);
            return Result.error();
        }


    }


    /**
     * 获取举报信息
     */
    @GetMapping("/getjubao")
    public Result getjubao(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime
    ) {
        try {
            Result result = managerService.getjubao(pageNum, pageSize, status, type, startTime, endTime);
            return result;
        } catch (Exception e) {
            log.error("获取举报信息失败", e);
            return Result.error();
        }
    }


    /**
     * 管理员解决举报问题
     */
    @PostMapping("/solvejubao")
    public Result solvejubao(
            String jubaoId, //举报表的id
            Integer jieguo,   // 举报结果是否成功
            Integer solvechufa   //处罚结果
    ) {
        try {
            Result result = managerService.solvejubao(jubaoId, jieguo, solvechufa);
            return result;

        } catch (Exception e) {
            log.error("管理员解决举报问题失败", e);
            return Result.error();
        }

    }

    /**
     * 管理员修改个人信息
     */
    @PostMapping("/updateUserMessage")
    public Result updateUserMessage(
            @Validated @RequestBody UserUpdateDTO UserUpdateDTO
    ) {
        try {
            Result result = managerService.updateUserMessage(UserUpdateDTO);
            return result;

        } catch (Exception e) {
            log.error("管理员修改个人信息失败", e);
            return Result.error();
        }
    }


    /**
     * 管理员获取系统资源配置
     */
    @GetMapping("/getSysResourceConfig")
    public Result getSysResourceConfig() {
        try {
            Result result = managerService.getSysResourceConfig();
            return result;
        } catch (Exception e) {
            log.error("管理员获取系统资源配置失败", e);
            return Result.error();
        }
    }

    @PostMapping("/updateSysResourceConfig")
    public Result updateSysResourceConfig(String Homeresourse, String url) {
        try {
            Result result = managerService.updateSysResourceConfig(Homeresourse,url);
            return result;
        } catch (Exception e) {
            log.error("管理员修改系统资源配置失败", e);
            return Result.error();
        }
    }


    /**
     * 获取可是化大屏幕数据的接口
     */

    @GetMapping("/getdataease")
    public Result getdataease() {
        try {
            Result result = managerService.getdataease();
            return result;
        } catch (Exception e) {
            log.error("获取可是化大屏幕数据失败", e);
            return Result.error();
        }
    }

    @PostMapping("/insertmanager")
    public Result insertmanager(
            String phone,
            String password,
            String  nickname,
            String realname
    ){
        try {
            Result result=   managerService.instermanager(phone,password,nickname,realname);
            return result;
        }catch (Exception e){
            log.error("管理员添加失败",e);
            return Result.error();
        }
    }





}
