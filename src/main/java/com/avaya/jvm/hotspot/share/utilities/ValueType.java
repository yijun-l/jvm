package com.avaya.jvm.hotspot.share.utilities;

public enum ValueType {
    T_BOOLEAN,
    T_CHAR,
    T_FLOAT,
    T_DOUBLE,
    T_BYTE,
    T_SHORT,
    T_INT,
    T_LONG,
    T_OBJECT,
    T_ARRAY,
    T_VOID,
    T_ADDRESS,
    T_NARROWOOP,
    T_METADATA,
    T_NARROWKLASS,
    T_CONFLICT,
    T_ILLEGAL;

    public static ValueType atype2BasicType(int atype){
        return switch (atype) {
            case 4 -> T_BOOLEAN;
            case 5 -> T_CHAR;
            case 6 -> T_FLOAT;
            case 7 -> T_DOUBLE;
            case 8 -> T_BYTE;
            case 9 -> T_SHORT;
            case 10 -> T_INT;
            case 11 -> T_LONG;
            default -> throw new IllegalArgumentException("Invalid atype: " + atype);
        };
    }
}