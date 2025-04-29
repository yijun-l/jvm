package com.avaya.jvm.hotspot.share.oops;

// CONSTANT_Utf8	    1

import lombok.Data;

@Data
public class ConstantUtf8Info extends ConstantInfo {
    private String value;

    public ConstantUtf8Info(String value){
        this.tag = ConstantTag.JVM_CONSTANT_UTF8;
        this.value = value;
    }
}