package com.wmc.facadeapi.model.po;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 请求日志po
 * @auther wangmingchang
 * @date 2019/2/21 15:35
 */
@Getter
@Setter
@ToString
public class RequestLogPo {
    /**主键*/
    private Long id;
    /**ip地址*/
    private String ipAddress;
    /**请求路径*/
    private String requestURI;
    /**请求类型*/
    private String requestType;
    /**请求参数*/
    private String parameterBody;
    /**请求时间*/
    private Date requestTime;
    /**创建时间*/
    private Date createTime;

}
