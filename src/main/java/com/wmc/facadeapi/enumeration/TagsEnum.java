package com.wmc.facadeapi.enumeration;

/**
 * @auther wangmingchang
 * @date 2019/2/21 16:57
 */
public enum  TagsEnum {
    TAGS_TEST("TAGS_TEST"),
    TAGS_REQUEST_LOG("TAGS_REQUEST_LOG"),
    TAGS_ALL("*");

    TagsEnum(String code){
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
