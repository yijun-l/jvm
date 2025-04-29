package com.avaya.jvm.hotspot.share.oops;

public enum ConstantTag {
    JVM_CONSTANT_UTF8(1),
    JVM_CONSTANT_UNICODE(2), // unused
    JVM_CONSTANT_INTEGER(3),
    JVM_CONSTANT_FLOAT(4),
    JVM_CONSTANT_LONG(5),
    JVM_CONSTANT_DOUBLE(6),
    JVM_CONSTANT_CLASS(7),
    JVM_CONSTANT_STRING(8),
    JVM_CONSTANT_FIELDREF(9),
    JVM_CONSTANT_METHODREF(10),
    JVM_CONSTANT_INTERFACE_METHODREF(11),
    JVM_CONSTANT_NAME_AND_TYPE(12),
    JVM_CONSTANT_METHOD_HANDLE(15), // JSR 292
    JVM_CONSTANT_METHOD_TYPE(16),   // JSR 292
    JVM_CONSTANT_DYNAMIC(17),
    JVM_CONSTANT_INVOKE_DYNAMIC(18),
    JVM_CONSTANT_MODULE(19),
    JVM_CONSTANT_PACKAGE(20),
    JVM_CONSTANT_EXTERNAL_MAX(20);

    private final int value;

    ConstantTag(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ConstantTag fromValue(int value) {
        for (ConstantTag tag : values()) {
            if (tag.getValue() == value) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid constant tag: " + value);
    }
}
