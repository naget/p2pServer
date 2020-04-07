package com.tf.graduation.server.enums;

/**
 * created by tianfeng on 2020/4/2
 */
public enum VersionRecordStateEnum {
    UPDATING(1,"更新中"),
    UPDATED(2,"更新完毕");
    private int code;
    private String desc;
    private VersionRecordStateEnum(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getDescByCode(int code){
        for (VersionRecordStateEnum e:VersionRecordStateEnum.values()){
            if (e.code==code){
                return e.desc;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
