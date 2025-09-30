package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.utilities.ValueType;

/**
 * Local variable array of a JVM frame used to store method parameters and local variables.
 * <p>
 * The size of the array is determined at compile-time and supplied with the method's code.
 * Variables are addressed by index starting from 0. For instance methods, index 0 is 'this'.
 */
public class LocalVariableArray {
    private final StackValue[] locals;

    public LocalVariableArray(int maxSize) {
        this.locals = new StackValue[maxSize];
        for (int i = 0; i < maxSize; i++){
            locals[i] = new StackValue();
        }
    }

    public void setRef(int index, Object ref){
        this.locals[index].setType(ValueType.T_OBJECT);
        this.locals[index].setRef(ref);
    }

    public Object getRef(int index){
        return this.locals[index].getRef();
    }

}
