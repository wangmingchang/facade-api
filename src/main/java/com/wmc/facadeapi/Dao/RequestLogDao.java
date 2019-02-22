package com.wmc.facadeapi.Dao;

import com.wmc.facadeapi.model.po.RequestLogPo;

/**
 * @auther wangmingchang
 * @date 2019/2/22 16:50
 */
public interface  RequestLogDao {

    int deleteByPrimaryKey(Integer userId);

    int insert(RequestLogPo record);

    int insertSelective(RequestLogPo record);

    RequestLogPo selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(RequestLogPo record);

    int updateByPrimaryKey(RequestLogPo record);

}
