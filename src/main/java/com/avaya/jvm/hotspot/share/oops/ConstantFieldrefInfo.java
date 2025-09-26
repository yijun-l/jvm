package com.avaya.jvm.hotspot.share.oops;

// CONSTANT_Fieldref	9

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class ConstantFieldrefInfo extends ConstantInfo {
    private static final Logger logger = LoggerFactory.getLogger(ConstantFieldrefInfo.class);
    private int classIndex;
    private int nameAndTypeIndex;

    public ConstantFieldrefInfo(int classIndex, int nameAndTypeIndex){
        this.tag = ConstantTag.JVM_CONSTANT_FIELDREF;
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

    public String resolveFieldName(ConstantPool cp){
        if (!(cp.getEntries().get(nameAndTypeIndex) instanceof ConstantNameAndTypeInfo)){
            throw new IllegalStateException("NameAndTypeIndex index " + nameAndTypeIndex + " is not ConstantClassInfo");
        }
        ConstantNameAndTypeInfo nameAndTypeInfo = (ConstantNameAndTypeInfo) cp.getEntries().get(nameAndTypeIndex);
        return nameAndTypeInfo.resolveName(cp);
    }
}