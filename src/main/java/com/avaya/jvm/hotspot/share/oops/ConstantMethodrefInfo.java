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

    public String resolveClassName(ConstantPool cp){
        if (!(cp.getEntries().get(classIndex) instanceof ConstantClassInfo)){
            throw new IllegalStateException("Class index " + classIndex + " is not ConstantClassInfo");
        }
        ConstantClassInfo classInfo = (ConstantClassInfo) cp.getEntries().get(classIndex);
        return classInfo.resolveName(cp);
    }

    public String resolveMethodName(ConstantPool cp){
        if (!(cp.getEntries().get(nameAndTypeIndex) instanceof ConstantNameAndTypeInfo)){
            throw new IllegalStateException("NameAndTypeIndex index " + nameAndTypeIndex + " is not ConstantClassInfo");
        }
        ConstantNameAndTypeInfo nameAndTypeInfo = (ConstantNameAndTypeInfo) cp.getEntries().get(nameAndTypeIndex);
        return nameAndTypeInfo.resolveName(cp);
    }
}
