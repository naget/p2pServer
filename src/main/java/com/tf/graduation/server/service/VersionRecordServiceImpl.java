package com.tf.graduation.server.service;

import com.tf.graduation.server.Model.UserInfoOnLine;
import com.tf.graduation.server.dao.entity.VersionRecord;
import com.tf.graduation.server.dao.mapper.VersionRecordMapper;
import com.tf.graduation.server.enums.VersionRecordStateEnum;
import com.tf.graduation.server.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * created by tianfeng on 2020/4/2
 */
@Service
@Slf4j
public class VersionRecordServiceImpl {
    @Autowired
    VersionRecordMapper versionRecordMapper;
    public VersionRecord getLatest(int userId){
        return versionRecordMapper.getLatestRecord(userId);
    }

    public VersionRecord getLatestUpdated(int userId){
        return versionRecordMapper.getLatestUpdatedRecord(userId);
    }

    public synchronized VersionRecord apply(int userId, UserInfoOnLine userInfoOnLine){
        VersionRecord record = getLatest(userId);
        if (record==null||record.getState()==VersionRecordStateEnum.UPDATED.getCode()){
            VersionRecord newRecord = new VersionRecord();
            newRecord.setDeviceName(userInfoOnLine.getMacAddress());
            newRecord.setState(VersionRecordStateEnum.UPDATING.getCode());
            newRecord.setUserId(userId);
            newRecord.setUpdatedTime(DateUtil.getNowString());
            newRecord.setVersion(record==null?0:record.getVersion()+1);
            versionRecordMapper.insert(newRecord);
            return getLatest(userId);
        }
        return null;
    }

    public boolean updateState(int userId,UserInfoOnLine userInfoOnLine){
        //最新记录是此设备，并且处于更新中状态
        if (userInfoOnLine.getDeviceName().equals(getLatest(userId).getDeviceName())&&getLatest(userId).getState()==VersionRecordStateEnum.UPDATING.getCode()){
            versionRecordMapper.updateState(VersionRecordStateEnum.UPDATED.getCode(),getLatest(userId).getId());
            return true;
        }
        return false;
    }
}
