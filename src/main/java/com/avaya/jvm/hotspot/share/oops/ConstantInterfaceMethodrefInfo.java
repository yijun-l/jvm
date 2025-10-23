package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

@Data
public class ConstantInterfaceMethodrefInfo extends ConstantInfo{
    private int classIndex;
    private int nameAndTypeIndex;

    public ConstantInterfaceMethodrefInfo(int classIndex, int nameAndTypeIndex){
        this.tag = ConstantTag.JVM_CONSTANT_INTERFACE_METHODREF;
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }


    public String resolveClassName(ConstantPool cp){
        if (!(cp.getEntries().get(classIndex) instanceof ConstantClassInfo classInfo)){
            throw new IllegalStateException("Class index " + classIndex + " is not ConstantClassInfo");
        }
        return classInfo.resolveName(cp);
    }

    public String resolveMethodName(ConstantPool cp){
        if (!(cp.getEntries().get(nameAndTypeIndex) instanceof ConstantNameAndTypeInfo nameAndTypeInfo)){
            throw new IllegalStateException("NameAndTypeIndex index " + nameAndTypeIndex + " is not ConstantClassInfo");
        }
        return nameAndTypeInfo.resolveName(cp);
    }

    public Descriptor resolveMethodDescriptor(ConstantPool cp){
        if (!(cp.getEntries().get(nameAndTypeIndex) instanceof ConstantNameAndTypeInfo nameAndTypeInfo)){
            throw new IllegalStateException("NameAndTypeIndex index " + nameAndTypeIndex + " is not ConstantClassInfo");
        }
        return nameAndTypeInfo.resolveDescriptor(cp);
    }
}

