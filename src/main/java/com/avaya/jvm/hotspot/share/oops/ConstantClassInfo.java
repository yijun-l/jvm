package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

// CONSTANT_Class	    7
@Data
public class ConstantClassInfo extends ConstantInfo {
    private int nameIndex;

    public ConstantClassInfo(int nameIndex){
        this.tag = ConstantTag.JVM_CONSTANT_CLASS;
        this.nameIndex = nameIndex;
    }
}