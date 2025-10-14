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

    public String resolveName(ConstantPool constantPool){
        Object entry = constantPool.getEntries().get(nameIndex);
        if (!(entry instanceof ConstantUtf8Info)) {
            throw new IllegalStateException("Name index " + nameIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) entry;
        return utf8Info.getValue();
    }

    public String resolveDescriptorName(ConstantPool constantPool){
        Object entry = constantPool.getEntries().get(descriptorIndex);
        if (!(entry instanceof ConstantUtf8Info)) {
            throw new IllegalStateException("Descriptor index " + descriptorIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) entry;
        return utf8Info.getValue();
    }

    public Descriptor resolveDescriptor(ConstantPool constantPool){
        String descriptorName = resolveDescriptorName(constantPool);
        return new Descriptor(descriptorName);
    }
}
