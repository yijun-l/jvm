package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

// CONSTANT_NameAndType	12
@Data
public class ConstantNameAndTypeInfo extends ConstantInfo {
    private final int nameIndex;
    private final int descriptorIndex;

    public ConstantNameAndTypeInfo(int nameIndex, int descriptorIndex){
        this.tag = ConstantTag.JVM_CONSTANT_NAME_AND_TYPE;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
    }

    public String resolveName(ConstantPool constantPool){
        Object entry = constantPool.getEntries().get(nameIndex);
        if (!(entry instanceof ConstantUtf8Info)) {
            throw new IllegalStateException("Name index " + nameIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) entry;
        return utf8Info.getValue();
    }

    public Descriptor resolveDescriptor(ConstantPool constantPool){
        Object entry = constantPool.getEntries().get(descriptorIndex);
        if (!(entry instanceof ConstantUtf8Info)) {
            throw new IllegalStateException("Descriptor index " + descriptorIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) entry;
        return new Descriptor(utf8Info.getValue());
    }
}