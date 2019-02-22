package com.wmc.facadeapi.conf.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.wmc.facadeapi.enumeration.TagsEnum;
import com.wmc.facadeapi.enumeration.TopicEnum;
import com.wmc.facadeapi.model.po.RequestLogPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义拦截器
 *
 * @auther wangmingchang
 * @date 2019/2/20 15:34
 */
@Component
public class CustomInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(CustomInterceptor.class);
    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String pathInfo = request.getPathInfo();
        String requestURI = request.getRequestURI();
        String ipAddress = getIpAddress(request);
        String method = request.getMethod();
        Map<String, String> allRequestParam = getAllRequestParam(request);
        logger.info("allRequestParam:{}", JSONObject.toJSONString(allRequestParam));
        RequestLogPo requestLogPo = new RequestLogPo();
        requestLogPo.setIpAddress(ipAddress);
        requestLogPo.setRequestType(method);
        requestLogPo.setRequestURI(requestURI);
        requestLogPo.setParameterBody(JSON.toJSONString(allRequestParam));
        Message message = new Message(TopicEnum.TOPIC_FACADE_API.getCode(), TagsEnum.TAGS_REQUEST_LOG.getCode(),JSONObject.toJSONString(requestLogPo).getBytes());
        //默认3秒超时
        SendResult sendResult = defaultMQProducer.send(message);
        logger.info("消息发送响应信息："+sendResult.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * 获取客户端的ip
     * @param request
     * @return
     */
    private String getIpAddress(javax.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    /**
     * 获取客户端请求参数中所有的信息
     *
     * @param request
     * @return
     */
    private Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
            }
        }
        return res;
    }

}
