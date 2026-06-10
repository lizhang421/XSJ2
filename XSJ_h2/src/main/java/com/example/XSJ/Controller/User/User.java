package com.example.XSJ.Controller.User;

import com.example.XSJ.Pojo.Result;

import com.example.XSJ.Pojo.System.HomeResources;
import com.example.XSJ.Pojo.User.Oothermessge;
import com.example.XSJ.Pojo.User.UserMessage;
import com.example.XSJ.Pojo.User.UserUpdateDTO;
import com.example.XSJ.Pojo.UserDTO.UserBaseDate;
import com.example.XSJ.Pojo.UserDTO.UserFollowDTO;
import com.example.XSJ.Pojo.UserDTO.UserInfoMessageDTO;

import com.example.XSJ.Pojo.Video.VideoPending;
import com.example.XSJ.Pojo.Video.VideoType;
import com.example.XSJ.Pojo.VideoDTo.VideoTagDTO;
import com.example.XSJ.ServiceImpl.UserServiceImpl.UserServiceImpl;
import com.example.XSJ.service.Manager.System.HomeResourcesService;
import com.example.XSJ.utils.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.XSJ.utils.RegexPatterns.ALLOWED_FIELDS;

/**
 * User用户登录注册接口
 *
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class User {
    @Autowired
    private UserServiceImpl  userserviceimpl;
    LocalAntiBrushService localAntiBrushService=new LocalAntiBrushService();

     @Autowired
     private ObjectMapper objectMapper;

     @Autowired
     private HomeResourcesService homeResources;


    @GetMapping("/captcha")
    public Result  getCaptcha(HttpServletRequest request, HttpServletResponse response )
    {
        String realIp = IpUtils.getRealIp(request);
        if (localAntiBrushService.checkDeviceCaptchaLimit(realIp)){
            return Result.error("验证码频繁，稍后再试");
        }
        try {
            HttpSession session=request.getSession();
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-cache, no-store");
            response.setHeader("Pragma","no-cache");
            response.setDateHeader("Expires",0);
            OutputStream out=response.getOutputStream();
           //生成验证码图片，返回验证码文本
            String captchaText = CaptchaUtils.generateCaptcha(out);
            session.setAttribute("captcha_code",captchaText);
            out.flush();
            out.close();
        //验证码获取设置
        }catch (Exception ex){
          throw new RuntimeException("获取验证码失败",ex);
        }
        return Result.success();
    }
    @PostMapping("/register")
    public Result register(
       @RequestParam(value = "phone",required = true)String phone,
       @RequestParam(value = "password",required = true)String password,
       @RequestParam(value = "rewpassword",required = true)String rewpassword,
       @RequestParam(value = "captchaCode",required = true)String captchaCode,
       HttpServletRequest request
            ){
        //防止恶意注册对某个ip限流访问
        String realIp = IpUtils.getRealIp(request);
        if (localAntiBrushService.checkIpLimit(realIp)){
            return  Result.error("频繁注册，请稍后再试");
        }
        HttpSession session=request.getSession();
        Object  captchacode =session.getAttribute("captcha_code");
        if (captchacode==null){
            return Result.error("验证码过期");
        }
         String storedCaptcha=captchacode.toString();
        if(storedCaptcha==null||!storedCaptcha.equals(captchaCode)){
           return Result.error("验证码错误");
        }
        //验证码技计数
        if (phone==null){
            return Result.error("手机号不能为空");
        }
        //取反了，返回false的时候才是验证通过手机号
        if (RegexUtils.isPhoneInvalid(phone)){
           return Result.error("手机号验证失败");
        }
        if(password==null){
            return Result.error("密码不能为空");
        }
        if (!rewpassword.equals(password)){
            return  Result.error("两次密码不正确");
        }
        if (RegexUtils.isPasswordInvalid(password)){
            return  Result.error("密码格式不正确");
        }
        //防止恶意注册
        //校验密码是否正确
        //查询是否存在当前账户
        //开启事务

        Boolean a= userserviceimpl.IsExitAmount(phone);
        if (a){
            session.removeAttribute("captcha_code");
            return Result.error("手机号已被注册");
        }
        //加密密码然后注册账号
        String password1=  PasswordEncoder.encode(password);
        Map<String,Object> map=Message();
        String userid= (String) map.get("userid");
        String encode=(String) map.get("encode");
        //插入数据库
        try {
            userserviceimpl.insertuser(userid,encode,phone,password1,realIp);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        session.removeAttribute("captcha_code");
        localAntiBrushService.clearIpCount(realIp);
        return  Result.success("注册成功");
    }

    //登录接口的实现
    @PostMapping("/login")
    public Result login(
            @RequestParam(value = "phone",required = true) String phone,
            @RequestParam(value = "password",required = true)String password,
            @RequestParam(value = "captchaCode",required = true)String captchaCode,
            HttpServletRequest request
    ){
        //登录ip限流 10分钟15次
        String realIp = IpUtils.getRealIp(request);
        if (localAntiBrushService.checkIpLimit(realIp)) {
            return Result.error("频繁注册，请稍后再试");
        }
        HttpSession session =request.getSession();
        Object  captchacode =session.getAttribute("captcha_code");
        if (captchacode==null){
            return Result.error("验证码过期");
        }
        String storedCaptcha=captchacode.toString();
        if (captchaCode==null||!captchaCode.equals(storedCaptcha)){
            return Result.error("验证码错误");
        }
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.error("手机号格式不正确");
        }
        Boolean a= userserviceimpl.IsExitAmount(phone);
        if (!a){
            session.removeAttribute("captcha_code");
            return Result.error("该账号暂未关联平台账号，如需使用请先完成注册");
        }
        if (localAntiBrushService.checkAccountPwdErrorLimit(phone)){
            session.removeAttribute("captcha_code");
            return Result.error("密码重试过多，稍后再试");
        }
       String encodedPassword= userserviceimpl.GetPasswordByPhone(phone);
        //验证密码
        Boolean b=PasswordEncoder.matches(encodedPassword,password);
        if (!b){
            //对某账号实现限流，防止暴力破解
            session.removeAttribute("captcha_code");
            return  Result.error("密码错误");
        }
        UserBaseDate date=userserviceimpl.GetUserBaseByPhone(phone);
        Map<String,Object>map=Message();
        map.put("EnCode",date.getEnCode());
        map.put("RoleBase",date.getRoleBase());
        String token=JwtUtil.genToken(map);
        localAntiBrushService.clearAccountPwdErrorCount(phone);
        localAntiBrushService.clearIpCount(realIp);
        session.removeAttribute("captcha_code");//移除验证码，防止盗用
        return  Result.success(token);
    }



    /**
     * 邮箱或者手机号的修改要单独通过验证码去修改
     * @param UpdateJson
     * @return
     */
    @PostMapping("/updateUserMessage")
    public Result UpdateUserMessage(@RequestParam(value = "UpdateJson") String UpdateJson) {

        if (UpdateJson == null || UpdateJson.trim().isEmpty()) {
            log.warn("用户修改个人信息接口未接收到更新数据");
            return Result.error("未接收到更新数据，请传入合法的修改信息");
        }

        try {
            Map<String, Object> updateData = objectMapper.readValue(
                    UpdateJson,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
            );


            Map<String, Object> threadMap = ThreadLocalUtil.get();
            if (threadMap == null || threadMap.get("encode") == null) {
                log.warn("用户编码为空，无法执行更新");
                return Result.error("用户未登录，无法修改信息");
            }

            String enCode = (String) threadMap.get("EnCode");

            Map<String, Object> filteredData = new HashMap<>();
            for (Map.Entry<String, Object> entry : updateData.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();
                if (ALLOWED_FIELDS.contains(field) && value != null) {
                    filteredData.put(field, value);
                }
            }
            if (filteredData.isEmpty()) {
                log.warn("用户{}传入的更新字段无合法项，字段列表：{}", enCode, updateData.keySet());
                return Result.error("无合法的修改字段（仅支持修改：姓名、年龄、学校等字段）");
            }

            int affectedRows = userserviceimpl.UpdateUserMessage(enCode, filteredData);
            if (affectedRows > 0) {
                return Result.success("个人信息修改成功");
            } else {
                log.warn("用户{}修改个人信息无数据更新（可能ID不存在/字段值未变化），更新字段：{}", enCode, filteredData.keySet());
                return Result.error("修改失败：无数据被更新（可能是修改的字段值与原数据一致，或用户信息不存在）");
            }

        } catch (JsonProcessingException e) {
            log.error("解析用户修改个人信息JSON失败，JSON内容：{}", UpdateJson, e);
            return Result.error("提交的信息格式错误，请检查后重新提交");
        } catch (RuntimeException e) {
            log.error("用户修改个人信息业务异常", e);
            return Result.error("无更内容");
        } catch (Exception e) {
            log.error("用户修改个人信息系统异常", e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }

    /**
     * 渲染首页数据
     * @return
     */
    @GetMapping("/home")
    public Result FirstHomeSourse(){
     //系统登入查询数据库刷新首页图片
        log.info("开始查询首页资源数据");
        Integer isUser=1;
        try {
            //查询数据库返回当前页面展示数据
            List<HomeResources> allResources=homeResources.selectAllEnableResourse(isUser);
            Map<String, List<HomeResources>> resourceMap = allResources.stream()
                    .collect(Collectors.groupingBy(HomeResources::getResourceAddress));

            Map<String, Object> responseData = new HashMap<>();
            List<Map<String, String>> carouselList = resourceMap.getOrDefault("首页轮播区", Collections.emptyList())
                    .stream()
                    .map(res -> {
                        Map<String, String> carouselItem = new HashMap<>();
                        carouselItem.put("type", res.getType()); // image/video
                        carouselItem.put("src", res.getAddress()); // 资源地址
                        return carouselItem;
                    })
                    .collect(Collectors.toList());

            responseData.put("carousel", carouselList);

            List<String> advantagesList = resourceMap.getOrDefault("首页优势区", Collections.emptyList())
                    .stream()
                    .map(HomeResources::getAddress)
                    .collect(Collectors.toList());
            responseData.put("advantages", advantagesList);
            String aiShowcase = getFirstResourceUrl(resourceMap.get("首页AI展示区"));
            responseData.put("aiShowcase", aiShowcase);

            List<String> avatarsList = resourceMap.getOrDefault("首页头像展示区", Collections.emptyList())
                    .stream()
                    .map(HomeResources::getAddress)
                    .collect(Collectors.toList());
            responseData.put("avatars", avatarsList);


            List<String> partnersList = resourceMap.getOrDefault("首页合作伙伴区", Collections.emptyList())
                    .stream()
                    .map(HomeResources::getAddress)
                    .collect(Collectors.toList());
            responseData.put("partners", partnersList);
            String contact = getFirstResourceUrl(resourceMap.get("首页联系区"));
            responseData.put("contact", contact);
            return Result.success(responseData);
        } catch (Exception e){
            log.error("查询首页资源数据异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }


    }
    private String getFirstResourceUrl(List<HomeResources> resourceList) {
        if (resourceList == null || resourceList.isEmpty()) {
            return ""; // 无资源时返回空字符串，避免前端报错
        }
        return resourceList.get(0).getAddress();
    }

   //获取用户的个人信息，前端获取到用户个人信息时候缓存在前端，包括用户的基础信息以及这个账号的相关信息，如果这个账号有被封禁前端新将限制用户行为
    @GetMapping("/date")
    public Result GetUserMessageDate(){
         //从当前的线程中获取用户的编码
        Map<String,Object> map= ThreadLocalUtil.get();
        String Encode=(String) map.get("EnCode");
        if (Encode==null){
            return Result.error("请先登入");
        }
        try {
            List<UserMessage> userMessage = userserviceimpl.GetUserMessageDate(Encode);
            return Result.success(userMessage);
        }catch (Exception e){
            log.error("获取用户信息异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }

    //修改语言，用户登录之后更换语言的时候会去修改用户系统中存储的语言
    @PostMapping("/updateLanguage")
    public Result updateLanguage(@RequestParam(value = "language") String  language){
        Map<String,Object> map= ThreadLocalUtil.get();
        String Encode=(String) map.get("EnCode");
        if (Encode==null){
            return Result.error("请先登入");
        }
        try {
            List<String>la=new ArrayList<>();
            la.add("中文");
            la.add("英文");
            la.add("日文");
            if (!la.contains(language)){
                return Result.error("请选择正确的语言");
            }
            int update = userserviceimpl.UpdateUserlanguage(Encode,language);
            if (update==1){
                return Result.success();
            }
            return Result.error("资源获取失败");

        }catch (Exception e){
            log.error("修改语言异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    //获取用户个人中心的详细信息

    @GetMapping("/user-info-message")
    public Result GetUserInfoMessage(){
        UserInfoMessageDTO dto = new UserInfoMessageDTO();
        //从当前线程中获取用户的登录信息
        Map<String,Object> map= ThreadLocalUtil.get();
        String Encode=(String) map.get("EnCode");
        if (Encode==null){
            return Result.error("请先登入");
        }
        //现在开始准备数据返回给前端
        try {
            //查询用户的关注
            List<UserFollowDTO> userFollowDTOS = userserviceimpl.FindUserFollow(Encode);
            //查询当前用户正在上传处在审核中的视频
            List<VideoPending>userVideoAudit = userserviceimpl.FindUserVideoAudit(Encode);
            //查询用户的发表的视频
            List<VideoTagDTO>userVideoPost = userserviceimpl.FindUserVideopost(Encode);
            //查询用户收藏的视频
            List<VideoTagDTO>userVideoCollect = userserviceimpl.FindUserVideoCollect(Encode);
            //查询用户喜欢的视频
            List<VideoTagDTO>userVideoLike = userserviceimpl.FindUserVideoLike(Encode);

            dto.setUserFollowDTOList(Objects.nonNull(userFollowDTOS) ? userFollowDTOS : new ArrayList<>());
            dto.setUserVideoPost(Objects.nonNull(userVideoPost) ? userVideoPost : new ArrayList<>());
            dto.setUserVideoCollect(Objects.nonNull(userVideoCollect) ? userVideoCollect : new ArrayList<>());
            dto.setUserVideoLike(Objects.nonNull(userVideoLike) ? userVideoLike : new ArrayList<>());
            dto.setUserVideoPending(Objects.nonNull(userVideoAudit) ? userVideoAudit : new ArrayList<>());
            return Result.success(dto);
        }catch (Exception e){
            log.error("获取用户信息异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }
    //进入到别人主页获取别人主页的信息
    @GetMapping("/other-home")
    public Result otherHome(@RequestParam(value = "encode") String encode){
        UserInfoMessageDTO dto = new UserInfoMessageDTO();

        if (encode==null||encode.isEmpty()){
            return Result.error("请选择正确的用户");
        }
        try {
            //查询用户的发表的视频
            List<VideoTagDTO>userVideoPost = userserviceimpl.FindUserVideopost(encode);
            //查询用户喜欢的视频
            List<VideoTagDTO>userVideoLike = userserviceimpl.FindUserVideoLike(encode);

            dto.setUserVideoPost(Objects.nonNull(userVideoPost) ? userVideoPost : new ArrayList<>());
            dto.setUserVideoLike(Objects.nonNull(userVideoLike) ? userVideoLike : new ArrayList<>());
            return Result.success(dto);

        }catch (Exception e){
            log.error("获取用户信息异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }

    @GetMapping("/other-data")
    public Result GetOtherMessageDate(@RequestParam(value = "encode") String encode){
        //从当前的线程中获取用户的编码

        if (encode==null){
            return Result.error("暂无信息");
        }
        try {
            Oothermessge userMessage = userserviceimpl.GetOtherMessageDate(encode);
            //查询是否是对方的好友和是否是关注
            Map<String,Object> map= ThreadLocalUtil.get();
            String userEncode=(String) map.get("EnCode");
            boolean isfiiend=userserviceimpl.isFriend(userEncode,encode);
            boolean isfollow=userserviceimpl.isFollow(encode,userEncode);
            Map<String,Object> result=new HashMap<>();
            result.put("isfiiend",isfiiend);
            result.put("isfollow",isfollow);
            result.put("userMessage",userMessage);

            return Result.success(result);
        }catch (Exception e){
            log.error("获取用户信息异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }
    //取消关注或关注别人
     @PostMapping("/cancelFollow")
     public Result cancelFollow(@RequestParam(value = "enCode") String enCode,
     @RequestParam(value = "status") int  status
     ){
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode=(String) map.get("EnCode");
        if (userEncode == null || userEncode.isEmpty()){
            return Result.error("信息失效");
        }
        try {
            int update = userserviceimpl.cancelFollow(userEncode,enCode,status);
            return update==1?Result.success():Result.error("取消关注失败");

        }catch (Exception  e){
            log.error("取消关注异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

     }

     /**
     *  获取当先系统接收的类型
     */
     @GetMapping("/gettypelist")
     public  Result getTypeList(){

         try {
             List<VideoType> videoTypes = userserviceimpl.getTypeList();
             return Result.success(videoTypes);
         }catch (Exception e){
             log.error("获取系统类型异常",e);
             return Result.error("系统繁忙，请稍后再试");
         }

     }


    /**
     * 用户修改个人信息的接口
     * @return
     */
    @PostMapping("/user-updateUserMessage")
    public Result updateusermassage(
            @Validated @RequestBody UserUpdateDTO UserUpdateDTO
    ){
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode=(String) map.get("EnCode");
        if (userEncode == null || userEncode.isEmpty()){
            return Result.error("信息失效");
        }
        try {
          Result result = userserviceimpl.UpdateUserInfoMessage(userEncode,UserUpdateDTO);
          return result;
        }catch (Exception e){
            log.error("修改用户信息异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }


    /**
     * 用户举报内容
     * @return
     */
    @PostMapping("/report")
    public Result report(String targetUserEncode, int ReportType, String TargetEncode,String Reason,String Remark){
        try {
            Result result = userserviceimpl.report(targetUserEncode,ReportType,TargetEncode,Reason, Remark);
            return result;
        }catch (Exception e){
            log.error("用户举报异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }


    }

    /**
     * 用户获取自己发出的举报和自己被举报的处理
     * 自己举报别人的全都都查询
     * 别人举报自己的只返回成功的
     * @return
     */
    @GetMapping("/getreport")
    public Result getreport(){
        try {

            Result result = userserviceimpl.getreport();
            return result;
        }catch (Exception e){
            log.error("用户获取自己发出的举报和自己被举报的处理异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }

    /**
     * 已读我的举报
     * @return
     */
    @PostMapping("/readreport")
    public Result readreport(
           @RequestBody List< String> reportId ){
        try {
            Result result = userserviceimpl.readreport(reportId);
            return result;
        }catch (Exception e){
            log.error("已读我的举报异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }

    }

    /**
     * 管理员和用户修改密码
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(
            @RequestParam(value = "newPassword") String newPassword
            ,
            @RequestParam(value = "oldPassword") String oldPassword
    ){
        Map<String,Object> map= ThreadLocalUtil.get();
        String userEncode=(String) map.get("EnCode");
        if (userEncode == null || userEncode.isEmpty()){
            return Result.error("信息失效");
        }
        try {
            //从数据库查询出老密码，然后和新密码进行比对

            Result result = userserviceimpl.updatePassword(userEncode,newPassword,oldPassword);

            return result;
        }catch (Exception e){
            log.error("管理员修改密码异常",e);
            return Result.error("系统繁忙，请稍后再试");
        }
    }








    //生成id和用户编码
    public Map Message(){
       //生成id
        String userid;
        String encode;
        String mixChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String middle="";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        do {
            String uuid = UUID.randomUUID().toString();
            userid="UI"+uuid;
            int code=  random.nextInt(10000,99999);
            for (int i=0;i<8;i++) {
                int randomindex=random.nextInt(mixChars.length());
                 middle=result.append(mixChars.charAt(randomindex)).toString();
            }
            encode="USER"+"_"+"PH"+middle+"_"+code;
        }while (!userserviceimpl.checkexit(userid));
        Map<String,String> map=new HashMap<>();
         map.put("userid",userid);
         map.put("encode",encode);
        return map;
    }
}
