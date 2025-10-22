package com.avaya.jvm.hotspot.share.oops;

// CONSTANT_Fieldref	9

import com.avaya.jvm.hotspot.share.utilities.ValueType;
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
        if (!(cp.getEntries().get(classIndex) instanceof ConstantClassInfo classInfo)){
            throw new IllegalStateException("Class index " + classIndex + " is not ConstantClassInfo");
        }
        return classInfo.resolveName(cp);
    }

    public String resolveFieldName(ConstantPool cp){
        if (!(cp.getEntries().get(nameAndTypeIndex) instanceof ConstantNameAndTypeInfo nameAndTypeInfo)){
            throw new IllegalStateException("NameAndTypeIndex index " + nameAndTypeIndex + " is not ConstantClassInfo");
        }
        return nameAndTypeInfo.resolveName(cp);
    }

    public ValueType resolveFieldType(ConstantPool cp){
        if (!(cp.getEntries().get(nameAndTypeIndex) instanceof ConstantNameAndTypeInfo nameAndTypeInfo)){
            throw new IllegalStateException("NameAndTypeIndex index " + nameAndTypeIndex + " is not ConstantClassInfo");
        }
        String typeName = nameAndTypeInfo.resolveDescriptor(cp).getField();
        ValueType type = ValueType.T_ILLEGAL;
        switch (typeName.charAt(0)){
            case 'B' -> type = ValueType.T_BYTE;
            case 'C' -> type = ValueType.T_CHAR;
            case 'D' -> type = ValueType.T_DOUBLE;
            case 'F' -> type = ValueType.T_FLOAT;
            case 'I' -> type = ValueType.T_INT;
            case 'J' -> type = ValueType.T_LONG;
            case 'S' -> type = ValueType.T_SHORT;
            case 'Z' -> type = ValueType.T_BOOLEAN;
            case 'L' -> type = ValueType.T_OBJECT;
            case '[' -> type = ValueType.T_ARRAY;
        }
        return type;
    }
}