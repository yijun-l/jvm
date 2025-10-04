package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.utilities.ValueType;

/**
 * Operand stack of a JVM frame used to hold intermediate values during bytecode execution.
 * <p>
 * The operand stack is a LIFO structure, with a maximum depth determined at compile-time.
 * It stores values temporarily for computations, method argument passing, and return values.
 * Type-specific push and pop operations are provided for safety.
 */
public class OperandStack {
    private final StackValue[] stack;
    private int top = -1;

    public OperandStack(int maxSize) {
        this.stack = new StackValue[maxSize];
        for (int i = 0; i < maxSize; i++){
            stack[i] = new StackValue();
        }
    }

    public void pushRef(Object ref){
        top++;
        stack[top].setType(ValueType.T_OBJECT);
        stack[top].setRef(ref);
    }

    public Object popRef(){
        return stack[top--].getRef();
    }

    public void pushInt(int num){
        top++;
        stack[top].setType(ValueType.T_INT);
        stack[top].setNum(num);
    }

    public int popInt(){
        return stack[top--].getNum();
    }

    public void pushFloat(float num){
        top++;
        stack[top].setType(ValueType.T_FLOAT);
        stack[top].setNum(Float.floatToRawIntBits(num));
    }

    public float popFloat(){
        return Float.intBitsToFloat(stack[top--].getNum());
    }

    public void pushLong(long num){
        top++;
        stack[top].setType(ValueType.T_LONG);
        stack[top].setNum((int) (num & 0xFFFFFFFFL));
        top++;
        stack[top].setType(ValueType.T_LONG);
        stack[top].setNum((int) (num >>> 32));
    }

    public long popLong(){
        int high = stack[top--].getNum();
        int low = stack[top--].getNum();
        return ((long) high << 32) | ((long) low & 0xFFFFFFFFL);
    }

    public void pushDouble(double num){
        long bits = Double.doubleToLongBits(num);
        top++;
        stack[top].setType(ValueType.T_DOUBLE);
        stack[top].setNum((int) (bits & 0xFFFFFFFFL));
        top++;
        stack[top].setType(ValueType.T_DOUBLE);
        stack[top].setNum((int) (bits >>> 32));
    }

    public double popDouble(){
        int high = stack[top--].getNum();
        int low = stack[top--].getNum();
        long bits = ((long) high << 32) | ((long) low & 0xFFFFFFFFL);
        return Double.longBitsToDouble(bits);
    }

}
