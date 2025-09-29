package com.avaya.jvm.hotspot.share.utilities;

public enum MemberAccessFlags {
    JVM_ACC_PUBLIC      (0x0001),
    JVM_ACC_PRIVATE     (0x0002),
    JVM_ACC_PROTECTED   (0x0004),
    JVM_ACC_STATIC      (0x0008),
    JVM_ACC_FINAL       (0x0010),
    JVM_ACC_SYNCHRONIZED(0x0020),
    JVM_ACC_VOLATILE    (0x0040),
    JVM_ACC_TRANSIENT   (0x0080),
    JVM_ACC_NATIVE      (0x0100),
    JVM_ACC_INTERFACE   (0x0200),
    JVM_ACC_ABSTRACT    (0x0400),
    JVM_ACC_STRICT      (0x0800);

    private final int value;

    MemberAccessFlags(int value){
        this.value = value;
    }

    int getValue(){
        return this.value;
    }

    public static String flagsToString(int flags){
        StringBuilder sb = new StringBuilder();
        for (MemberAccessFlags f : values()){
            if ((f.getValue() & flags) != 0){
                if (!sb.isEmpty()) sb.append(" | ");
                sb.append(f.name().substring(4));
            }
        }
        return sb.isEmpty() ? "NONE" : sb.toString();
    }
}
