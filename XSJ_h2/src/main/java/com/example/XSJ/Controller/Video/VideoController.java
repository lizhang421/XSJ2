package com.example.XSJ.Controller.Video;

import com.example.XSJ.Pojo.Result;
import com.example.XSJ.ServiceImpl.VideoServiceImpl.VideoServiceImpl;
import com.example.XSJ.utils.AliOssUtil;
import com.example.XSJ.utils.VideoTranscodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jaxb.runtime.v2.runtime.output.Encoded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 *Video 相关的接口，首页根据用户喜好权重进行筛选推荐内容
 *
 */

@Slf4j
@RestController
@RequestMapping("/Video")
public class VideoController {

    /**
     * 封面上传阿里云
     */
    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private VideoServiceImpl  videoServiceImpl;

    /**
     * @param file
     * @return
     */

    //测试上传封面
    @PostMapping("/emage/upload")
    public Result uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            //TODO 这个位置海缺了很多处理步骤，上传文件还需要通过 管理员审核之后才会插入数据库
            String url = aliOssUtil.upload(file);
            System.out.println( file );
            return Result.success(url);
        } catch (Exception e) {
            return Result.error("上传失败");
        }

    }

    @PostMapping("/video")
    public  Result uploadvideomv(@RequestParam("file") MultipartFile file) {
        try {
            //调用解码的方法将三个视频解码之后上传到阿里云，上传成功之后从缓存区删除
            //获取视频id
           Map map = message();
           String videoid=map.get("VideoId").toString();
            VideoTranscodeUtil.transcodevideo(file,videoid);
            return Result.success("解码成功解码之后的地址为");
        }catch (Exception e){
            log.error("解析发生意外");
        }
        return  Result.error("解析失败");
    }


    /**
     * 用户上传视频配套资源
     * @return
     */




    public Map message(){
        Map<String,String> map=new HashMap<>();
       //生成唯一的视频表主键id 以及  视频表的编码
        String VideoId="";
        String VideoEnCode="";
        String mixChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String middle="";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        do {
            //生成Videoid 生成规则 ==》 VI+32位uuid
            String uuid =UUID.randomUUID().toString();
            VideoId="VI"+uuid;
            //生成视频的代码 这个属性值是可以透漏给用户的
            //生成VideoEnCodeguize ==》 模块名称+ 随机八位字符串加 4位数字id
            int code=  random.nextInt(10000,99999);
            for (int i =0 ; i<8 ; i++) {
                int randomindex=random.nextInt(mixChars.length());//获取一个在这个长度之间的随机数
                middle=result.append(mixChars.charAt(randomindex)).toString();
            }
            VideoEnCode="Video"+"_"+middle+"_"+code;
            map.put("VideoId",VideoId);
            map.put("VideoEnCode",VideoEnCode);
        }while (!videoServiceImpl.check(map) );
        return map;
    }

}
