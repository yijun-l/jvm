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

    public void setInt(int index, int num){
        this.locals[index].setType(ValueType.T_INT);
        this.locals[index].setNum(num);
    }

    public int getInt(int index) {
        return this.locals[index].getNum();
    }

    public void setFloat(int index, float num){
        this.locals[index].setType(ValueType.T_FLOAT);
        this.locals[index].setNum(Float.floatToRawIntBits(num));
    }

    public float getFloat(int index) {
        return Float.intBitsToFloat(this.locals[index].getNum());
    }

    public void setLong(int index, long num){
        this.locals[index].setType(ValueType.T_LONG);
        this.locals[index].setNum((int) (num & 0xFFFFFFFFL));
        this.locals[index+1].setType(ValueType.T_LONG);
        this.locals[index+1].setNum((int) (num >>> 32));
    }

    public long getLong(int index) {
        int low = this.locals[index].getNum();
        int high = this.locals[index+1].getNum();
        return ((long)low & 0xFFFFFFFFL) | ((long)high << 32);
    }
}
