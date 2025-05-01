package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.util.List;

@Data
public class InstanceKlass extends Klass{

    private int magic;
    private int minorVersion;
    private int majorVersion;

    private int constantPoolCount;
    private ConstantPool constantPool;

    private int accessFlags;
    private int this_class;
    private int super_class;

    private int interfaceCount;
    private int[] interfaces;

    private int fieldsCount;
    private List<FieldInfo> fields;

    private int methodsCount;
    private List<MethodInfo> methods;

    private int attributesCount;
    private List<AttributeInfo> attributes;

    public InstanceKlass(){
        constantPool = new ConstantPool();
    }
}
