package com.tf.graduation.server.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tf.graduation.server.dao.entity.VersionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * created by tianfeng on 2020/4/2
 */
@Mapper
public interface VersionRecordMapper extends BaseMapper<VersionRecord> {
    @Select("select * from version_record where user_id = #{userId} and state=2 order by version desc limit 1")
    public VersionRecord getLatestUpdatedRecord(int userId);
    @Select("select * from version_record where user_id = #{userId} order by version desc limit 1")
    public VersionRecord getLatestRecord(int userId);
    @Update("update version_record set state = #{state} where id = #{id}")
    public void updateState(@Param("state") int state, @Param("id") int id);
}
