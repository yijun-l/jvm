package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.util.List;

@Data
public class FieldInfo {
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    List<AttributeInfo> attributes;

    public Descriptor resolveDescriptor(ConstantPool constantPool){
        Object entry = constantPool.getEntries().get(descriptorIndex);
        if (!(entry instanceof ConstantUtf8Info)) {
            throw new IllegalStateException("Descriptor index " + descriptorIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) entry;
        return new Descriptor(utf8Info.getValue());
    }
}
