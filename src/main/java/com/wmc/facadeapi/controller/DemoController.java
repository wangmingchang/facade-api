package com.wmc.facadeapi.controller;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.wmc.facadeapi.enumeration.TagsEnum;
import com.wmc.facadeapi.enumeration.TopicEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试controll
 * @auther wangmingchang
 * @date 2019/2/20 13:59
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private static Logger logger = LoggerFactory.getLogger(DemoController.class);
    /**使用RocketMq的生产者*/
    @Autowired
    private DefaultMQProducer defaultMQProducer;


    /**
     * 默认方法
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(){
        String str = "hi,index!!!";
        logger.info(str);
        return str;
    }

    @RequestMapping(value = "/testMq", method = RequestMethod.GET)
    public String testMq() throws Exception{
        String msg = "demo msg test";
        logger.info("开始发送消息："+msg);
        Message sendMsg = new Message(TopicEnum.TOPIC_FACADE_API.getCode(), TagsEnum.TAGS_TEST.getCode(),msg.getBytes());
        //默认3秒超时
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        logger.info("消息发送响应信息："+sendResult.toString());
        return null;
    }
}
