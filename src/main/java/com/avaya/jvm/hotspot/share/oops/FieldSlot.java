package com.avaya.jvm.hotspot.share.oops;

import com.avaya.jvm.hotspot.share.utilities.ValueType;
import lombok.Data;

@Data
public class FieldSlot {
    private ValueType type;
    private Object ref;
    private int num;
    private String name;

    public FieldSlot(String name, ValueType type) {
        this.type = type;
        this.name = name;
        this.ref = null;
        this.num = -1;
    }

    public FieldSlot() {
        this.type = ValueType.T_ILLEGAL;
        this.name = null;
        this.ref = null;
        this.num = -1;
    }
}
