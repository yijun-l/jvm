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

    public String resolveName(ConstantPool cp){
        if (!(cp.getEntries().get(nameIndex) instanceof ConstantUtf8Info)){
            throw new IllegalStateException("Name index " + nameIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) cp.getEntries().get(nameIndex);
        return utf8Info.getValue();
    }
}