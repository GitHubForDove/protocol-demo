package com.patterncat.protocol;

/**
 * Created by patterncat on 2016-02-19.
 */
public enum EncodeEnum {
    UTF8("utf-8"),GBK("gbk");

    EncodeEnum(String desc){
        this.desc = desc;
    }

    String desc;

    public static EncodeEnum getByIdx(int idx){
        for(EncodeEnum encodeEnum:values()){
            if(encodeEnum.ordinal() == idx){
                return encodeEnum;
            }
        }
        return null;
    }
}
