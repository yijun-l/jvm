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

    public String resolveString(ConstantPool cp){
        if (!(cp.getEntries().get(stringIndex) instanceof ConstantUtf8Info)){
            throw new IllegalStateException("String index " + stringIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) cp.getEntries().get(stringIndex);
        return utf8Info.getValue();
    }
}