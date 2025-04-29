package com.avaya.jvm.hotspot.share.oops;

// CONSTANT_Fieldref	9

import lombok.Data;

@Data
public class ConstantFieldrefInfo extends ConstantInfo {
    private int classIndex;
    private int nameAndTypeIndex;

    public ConstantFieldrefInfo(int classIndex, int nameAndTypeIndex){
        this.tag = ConstantTag.JVM_CONSTANT_FIELDREF;
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }
}