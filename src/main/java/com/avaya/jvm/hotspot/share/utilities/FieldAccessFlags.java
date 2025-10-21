package com.avaya.jvm.hotspot.share.utilities;

public enum FieldAccessFlags {
    JVM_ACC_PUBLIC      (0x0001),
    JVM_ACC_PRIVATE     (0x0002),
    JVM_ACC_PROTECTED   (0x0004),
    JVM_ACC_STATIC      (0x0008),
    JVM_ACC_FINAL       (0x0010),
    JVM_ACC_VOLATILE    (0x0040),
    JVM_ACC_TRANSIENT   (0x0080),
    JVM_ACC_SYNTHETIC   (0x1000),
    JVM_ACC_ENUM        (0x4000);

    private final int value;

    FieldAccessFlags(int value){
        this.value = value;
    }

    public static boolean isStatic(int accessFlags){
        return (accessFlags & JVM_ACC_STATIC.value) != 0;
    }
}
