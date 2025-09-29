package com.avaya.jvm.hotspot.share.utilities;

public enum ClassAccessFlags {
    JVM_ACC_PUBLIC      (0x0001),
    JVM_ACC_FINAL       (0x0010),
    JVM_ACC_SUPER       (0x0020),
    JVM_ACC_INTERFACE   (0x0200),
    JVM_ACC_ABSTRACT    (0x0400),
    JVM_ACC_SYNTHETIC   (0x1000),
    JVM_ACC_ANNOTATION  (0x2000),
    JVM_ACC_ENUM        (0x4000);

    private final int value;

    ClassAccessFlags(int value){
        this.value = value;
    }

    int getValue(){
        return this.value;
    }

    public static String flagsToString(int flags){
        StringBuilder sb = new StringBuilder();
        for (ClassAccessFlags f: values()){
            if ((f.getValue() & flags) != 0){
                if (!sb.isEmpty()) sb.append(" | ");
                sb.append(f.name().substring(4));
            }
        }
        return sb.isEmpty() ? "NONE" : sb.toString();
    }

}
