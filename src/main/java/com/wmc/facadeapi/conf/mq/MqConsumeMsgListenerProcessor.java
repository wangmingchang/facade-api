package com.wmc.facadeapi.conf.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.wmc.facadeapi.enumeration.TagsEnum;
import com.wmc.facadeapi.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * rocketMq的监听器
 * @auther wangmingchang
 * @date 2019/2/21 10:07
 */
@Component
public class MqConsumeMsgListenerProcessor implements MessageListenerConcurrently {
    private static final Logger logger = LoggerFactory.getLogger(MqConsumeMsgListenerProcessor.class);

    private List<String> classNams = new ArrayList<>();

    /**
     *  默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息<br/>
     *  不要抛异常，如果没有return CONSUME_SUCCESS ，consumer会重新消费该消息，直到return CONSUME_SUCCESS
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if(CollectionUtils.isEmpty(msgs)){
            logger.info("接受到的消息为空，不处理，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        ConsumeConcurrentlyStatus concurrentlyStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try{
            //根据Topic分组
            Map<String, List<MessageExt>> topicGroups = msgs.stream().collect(Collectors.groupingBy(MessageExt::getTopic));
            for (Map.Entry<String, List<MessageExt>> topicEntry : topicGroups.entrySet()) {
                String topic = topicEntry.getKey();
                //根据tags分组
                Map<String, List<MessageExt>> tagGroups = topicEntry.getValue().stream().collect(Collectors.groupingBy(MessageExt::getTags));
                for (Map.Entry<String, List<MessageExt>> tagEntry : tagGroups.entrySet()) {
                    String tag = tagEntry.getKey();
                    //消费某个主题下，tag的消息
                    this.consumeMsgForTag(topic,tag,tagEntry.getValue());
                }
            }
        }catch(Exception e){
            logger.error("处理消息失败",e);
            concurrentlyStatus = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        // 如果没有return success ，consumer会重新消费该消息，直到return success
        return concurrentlyStatus;
    }
    /**
     * 根据topic 和 tags路由，查找消费消息服务
     * @param topic
     * @param tag
     * @param messageExts
     * 2018年3月1日 zhaowg
     */
    private void consumeMsgForTag(String topic, String tag, List<MessageExt> messageExts) {
        //根据topic 和  tag查询具体的消费服务
        MqMsgProcessor imqMsgProcessor = selectConsumeService(topic, tag);
        try{
            if(imqMsgProcessor==null){
                logger.error(String.format("根据Topic：%s和Tag:%s 没有找到对应的处理消息的服务",topic,tag));
                throw new Exception(String.format("根据Topic：%s和Tag:%s 没有找到对应的处理消息的服务",topic,tag));
            }
            logger.info(String.format("根据Topic：%s和Tag:%s 路由到的服务为:%s，开始调用处理消息",topic,tag,imqMsgProcessor.getClass().getName()));
            //调用该类的方法,处理消息
            MqConsumeResult mqConsumeResult = imqMsgProcessor.handle(topic,tag,messageExts);
            if(mqConsumeResult==null){
                throw new Exception("没有消费返回");
            }
            if(mqConsumeResult.isSuccess()){
                logger.info("消息处理成功："+ JSON.toJSONString(mqConsumeResult));
            }else{
                throw new Exception(String.format("消息处理失败:%s",JSON.toJSONString(mqConsumeResult)));
            }
            if(mqConsumeResult.isSaveConsumeLog()){
                logger.debug("开始记录消费日志");
                //TODO 记录消费日志
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 根据topic和tag查询对应的具体的消费服务
     * @param topic
     * @param tag
     * @return
     * 2018年3月3日 zhaowg
     */
    private MqMsgProcessor selectConsumeService(String topic, String tag) {
        MqMsgProcessor imqMsgProcessor = null;
        if(null == classNams || classNams.size() <= 0){
            classNams = PackageUtil.getClassName("com.wmc.facadeapi.consumer");
        }


        for (String className : classNams) {
            Class<?> clzz = null;
            try {
                clzz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(clzz == null){
                logger.error("类："+className+"在项目中不存在");
                continue;
            }
            //获取service实现类上注解的topic和tags
            MqConsumeService consumeService = clzz.getAnnotation(MqConsumeService.class);
            if(consumeService == null){
                logger.error("消费者服务："+clzz.getName()+"上没有添加MQConsumeService注解");
                continue;
            }
            String annotationTopic = consumeService.topic().getCode();
            if(!annotationTopic.equals(topic)){
                continue;
            }
            TagsEnum[] tagsArr = consumeService.tags();
            for (TagsEnum tagsEnum : tagsArr){
                //"*"号表示订阅该主题下所有的tag
                if(tagsEnum.getCode().equals(TagsEnum.TAGS_ALL.getCode()) || tagsEnum.getCode().equals(tag)){
                    //获取该实例
                    try {
                        imqMsgProcessor = (MqMsgProcessor) clzz.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }
        return imqMsgProcessor;
    }

}
