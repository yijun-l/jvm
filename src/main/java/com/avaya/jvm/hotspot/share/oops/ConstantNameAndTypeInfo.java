package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

// CONSTANT_NameAndType	12
@Data
public class ConstantNameAndTypeInfo extends ConstantInfo {
    private int nameIndex;
    private int descriptorIndex;

    public ConstantNameAndTypeInfo(int nameIndex, int descriptorIndex){
        this.tag = ConstantTag.JVM_CONSTANT_NAME_AND_TYPE;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }
}