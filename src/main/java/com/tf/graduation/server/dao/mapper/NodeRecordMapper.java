package com.tf.graduation.server.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.graduation.server.dao.entity.NodeRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by tianfeng on 2020/4/3
 */
@Mapper
public interface NodeRecordMapper extends BaseMapper<NodeRecord> {
    @Select("select device_name from node_record where mac_address=#{macAddress} and active=1 and user_id=#{userId}")
    List<String> getDeviceNames(@Param("macAddress") String macAddress, @Param("userId") int userId);

}
