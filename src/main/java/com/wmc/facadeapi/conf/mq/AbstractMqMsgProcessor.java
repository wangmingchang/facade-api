package com.wmc.facadeapi.conf.mq;

import com.alibaba.rocketmq.common.message.MessageConst;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * 消息处理器
 * @auther wangmingchang
 * @date 2019/2/21 10:30
 */
public abstract class AbstractMqMsgProcessor implements MqMsgProcessor {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractMqMsgProcessor.class);

    /**
     * 消息处理<br/>
     * 如果没有return true ，consumer会重新消费该消息，直到return true<br/>
     * consumer可能重复消费该消息，请在业务端自己做是否重复调用处理，该接口设计为幂等接口
     *
     * @param topic 消息主题
     * @param tag   消息标签
     * @param msgs  消息
     * @return
     */
    @Override
    public MqConsumeResult handle(String topic, String tag, List<MessageExt> msgs) {
        MqConsumeResult mqConsumeResult = new MqConsumeResult();
        /**可以增加一些其他逻辑*/

        for (MessageExt messageExt : msgs) {
            //消费具体的消息，抛出钩子供真正消费该消息的服务调用
            mqConsumeResult = this.consumeMessage(tag,messageExt.getKeys()==null?null: Arrays.asList(messageExt.getKeys().split(MessageConst.KEY_SEPARATOR)),messageExt);
        }

        /**可以增加一些其他逻辑*/
        return mqConsumeResult;
    }
    /**
     * 消息某条消息
     * @param tag 标签
     * @param keys 消息关键字
     * @param messageExt
     * @return
     */
    protected abstract MqConsumeResult consumeMessage(String tag, List<String> keys, MessageExt messageExt);


}
