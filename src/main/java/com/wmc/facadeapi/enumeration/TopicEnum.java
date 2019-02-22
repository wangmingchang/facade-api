package com.wmc.facadeapi.enumeration;

/**
 * @auther wangmingchang
 * @date 2019/2/21 10:27
 */
public enum TopicEnum {
    TOPIC_TEST("TOPIC_TEST"),
    TOPIC_FACADE_API("TOPIC_FACADE_API");

    TopicEnum(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
