package com.avaya.jvm.hotspot.share.oops;

public enum AttributeType {
    CODE("Code"),
    LINE_NUMBER_TABLE("LineNumberTable"),
    LOCAL_VARIABLE_TABLE("LocalVariableTable"),
    SOURCE_FILE("SourceFile"),
    UNKNOWN("Unknown");

    private final String name;

    AttributeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static AttributeType fromName(String name) {
        for (AttributeType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }

}
