package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

// CONSTANT_Methodref	10
@Data
public class ConstantMethodrefInfo extends ConstantInfo {
    private int classIndex;
    private int nameAndTypeIndex;

    public ConstantMethodrefInfo(int classIndex, int nameAndTypeIndex){
        this.tag = ConstantTag.JVM_CONSTANT_METHODREF;
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }
}
