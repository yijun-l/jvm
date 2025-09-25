package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.utilities.ValueType;
import lombok.Data;

@Data
public class StackValue {
    private ValueType type;
    private Object value;

    public StackValue(ValueType type, Object value) {
        this.type = type;
        this.value = value;
    }
}
