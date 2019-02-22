package com.wmc.facadeapi.model.vo;

import lombok.Data;

/**
 * 基础响应vo
 * @auther: wangmingchang
 * @date: 2019/2/20 14:08
 */
@Data
public class BaseResponseVo {

    /**响应编码*/
    private String code;
    /**响应编码说明*/
    private String message;
    /**业务数据*/
    private Object data;

}
