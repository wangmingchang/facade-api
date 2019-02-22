package com.wmc.facadeapi.conf.mq;

import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @auther wangmingchang
 * @date 2019/2/21 10:29
 */
public interface MqMsgProcessor {
    /**
     * 消息处理<br/>
     * 如果没有return true ，consumer会重新消费该消息，直到return true<br/>
     * consumer可能重复消费该消息，请在业务端自己做是否重复调用处理，该接口设计为幂等接口
     * @param topic 消息主题
     * @param tag 消息标签
     * @param msgs 消息
     * @return
     */
    MqConsumeResult handle(String topic, String tag, List<MessageExt> msgs);
}
