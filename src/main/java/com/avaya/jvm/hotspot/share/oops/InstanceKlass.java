package com.avaya.jvm.hotspot.share.oops;

public class InstanceKlass extends Klass{

    private int magic;
    private short minorVersion;
    private short majorVersion;

    private short constantPoolCount;
//    cp_info   constant_pool[constant_pool_count-1];

    private short accessFlags;
    private short this_class;
    private short super_class;

    private short interfaceCount;
//    u2  interface[interfaceCount]

    private short fieldsCount;
//    field_info  fields[fieldsCount];

    private short methodsCount;
//    method_info methods[methodsCount];

    private short attributesCount;
//    attribute_info attribute[attributesCount];

    public InstanceKlass(){

    }
}
