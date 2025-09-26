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

    public String resolveName(ConstantPool cp){
        if (!(cp.getEntries().get(nameIndex) instanceof ConstantUtf8Info)){
            throw new IllegalStateException("Name index " + nameIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) cp.getEntries().get(nameIndex);
        return utf8Info.getValue();
    }
}