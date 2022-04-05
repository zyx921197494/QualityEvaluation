package com.winkel.qualityevaluation.util;
/*
  @ClassName MailUtil
  @Description
  @Author winkel
  @Date 2022-03-27 19:37
  */

import com.winkel.qualityevaluation.exception.MailException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

@Service
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean sendEmail(String email) {

        String code = RandomUtil.randomNums(6);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message, true);
            helper.setSubject("验证码");
            helper.setText("你的验证码为：" + code + " ，请在 3 分钟内验证", false);
            helper.setTo(email);
            helper.setFrom("921197494@qq.com");
            redisTemplate.opsForValue().set(email, code, 180, TimeUnit.SECONDS);
            mailSender.send(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public RedisResult validateEmailCode(String email, String code) {
        String key = redisTemplate.opsForValue().get(email);
        if (key == null) {
            return new RedisResult().setStatus(Const.REDIS_CODE_TIMEOUT).setMsg("验证码已过期");
        }
        if (StringUtils.equals(key, code)) {
            return new RedisResult().setStatus(Const.REDIS_CODE_RIGHT).setMsg("验证码正确");
        }
        return new RedisResult().setStatus(Const.REDIS_CODE_ERROR).setMsg("验证码错误");
    }

}
