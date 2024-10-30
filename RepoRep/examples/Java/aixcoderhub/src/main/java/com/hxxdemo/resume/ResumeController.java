package com.hxxdemo.resume;

import com.hxxdemo.config.Globals;
import com.hxxdemo.index.service.QemailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wyb
 * @date 2024/6/13
 */
@Controller
@RequestMapping(value = "/resume")
public class ResumeController {

    @Autowired
    private QemailService qemailService;

    @Value("${resume.receiver:huangyan@aixcoder.com}")
    private String resumeReceiver;

    // 投递简历
    @RequestMapping(value = "send")
    @ResponseBody
    public Map<String,Object> sendResume(
            @ModelAttribute ResumeInfo resumeInfo,
            @RequestParam("files") MultipartFile[] files) {
        Map<String,Object> result = new HashMap<>();
        String name = resumeInfo.getName();
        String phone = resumeInfo.getPhone();
        if (StringUtils.isEmpty(name)) {
            result.put("errorcode", Globals.ERRORCODE8004);
            result.put("errormessage", "姓名不能为空");
            return result;
        }

        if (StringUtils.isEmpty(phone)) {
            result.put("errorcode", Globals.ERRORCODE1013);
            result.put("errormessage", "联系方式不能为空");
            return result;
        }

        if (files.length == 0) {
            result.put("errorcode", Globals.ERRORCODE1014);
            result.put("errormessage", "请上传简历");
            return result;
        }
        String subject = "收到"+ name +"投递的新简历！";
        String text = "姓名 : " + name +
                "\n联系方式 : " + phone +
                "\n邮箱 : " + resumeInfo.getEmail() + "\n\n详细简历请查看附件！";

        qemailService.sendWithAttach(subject, text, resumeReceiver, files);
        result.put("errorcode", Globals.ERRORCODE0);
        result.put("errormessage", Globals.ERRORMESSAGE0);
        return  result;
    }
}
