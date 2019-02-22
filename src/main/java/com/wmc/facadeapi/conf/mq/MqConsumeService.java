package com.wmc.facadeapi.conf.mq;

import com.wmc.facadeapi.enumeration.TagsEnum;
import com.wmc.facadeapi.enumeration.TopicEnum;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 此注解用于标注消费者服务
 * @author wangmingchang
 * @date 2019/2/21 10:20
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface MqConsumeService {
    /**
     * 消息主题
     */
    TopicEnum topic();

    /**
     * 消息标签,如果是该主题下所有的标签，使用“*”
     */
    TagsEnum[] tags();
}
