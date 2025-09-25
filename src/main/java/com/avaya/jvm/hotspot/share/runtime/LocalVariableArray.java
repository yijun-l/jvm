package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.utilities.ValueType;

public class LocalVariableArray {
    private final StackValue[] locals;

    public LocalVariableArray(int maxSize) {
        this.locals = new StackValue[maxSize];
        for (int i = 0; i < maxSize; i++){
            locals[i] = new StackValue(ValueType.OBJECT, null);
        }
    }

    public void setValue(int index, Object value){
        locals[index].setValue(value);
    }

    public StackValue get(int index){
        return locals[index];
    }
}
