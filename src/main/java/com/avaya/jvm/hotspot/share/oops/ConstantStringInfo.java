package com.avaya.jvm.hotspot.share.oops;

// CONSTANT_String	    8

import lombok.Data;

@Data
public class ConstantStringInfo extends ConstantInfo {
    private int stringIndex;

    public ConstantStringInfo(int stringIndex){
        this.tag = ConstantTag.JVM_CONSTANT_STRING;
        this.stringIndex = stringIndex;
    }
}