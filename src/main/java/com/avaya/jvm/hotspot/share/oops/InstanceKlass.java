package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

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
//    field_info  fields[fieldsCount];

    private int methodsCount;
//    method_info methods[methodsCount];

    private int attributesCount;
//    attribute_info attribute[attributesCount];

    public InstanceKlass(){
        constantPool = new ConstantPool();
    }
}
