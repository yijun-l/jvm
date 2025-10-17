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

    public void pop(){
        top--;
    }

    public void pop2(){
        top -= 2;
    }

    public void swap(){
        ValueType t1 = stack[top].getType();
        ValueType t2 = stack[top - 1].getType();
        if (t1 == ValueType.T_LONG || t1 == ValueType.T_DOUBLE || t2 == ValueType.T_LONG || t2 == ValueType.T_DOUBLE) {
            throw new IllegalStateException("swap not allowed for long/double");
        }
        StackValue tmp = new StackValue();
        // store top slot
        tmp.setType(stack[top].getType());
        tmp.setRef(stack[top].getRef());
        tmp.setNum(stack[top].getNum());

        // copy top-1 slot to top slot
        stack[top].setType(stack[top - 1].getType());
        stack[top].setRef(stack[top - 1].getRef());
        stack[top].setNum(stack[top - 1].getNum());

        // copy tmp slot to top-1 slot
        stack[top - 1].setType(tmp.getType());
        stack[top - 1].setRef(tmp.getRef());
        stack[top - 1].setNum(tmp.getNum());
    }

    /**
     * General implementation of JVM DUP/X instructions.
     *
     * @param numSlots    Number of operand stack slots to duplicate (1 for DUP, 2 for DUP2)
     * @param numElements Number of stack elements to skip down (_X1 = 1, _X2 = 2)
     */
    public void dupSlotsAcrossElements (int numSlots, int numElements){
        // Calculate the gap in slots to insert duplicated elements
        int insertSlots = 0;
        for (int i = 0; i < numElements; i++){
            insertSlots += getElementLength(top - numSlots - insertSlots);
        }

        // Move elements above the insertion point up to make room
        for (int i = 0; i < insertSlots + numSlots; i++ ){
            stack[top - i + numSlots].setType(stack[top - i].getType());
            stack[top - i + numSlots].setRef(stack[top - i].getRef());
            stack[top - i + numSlots].setNum(stack[top - i].getNum());
        }
        top += numSlots;

        // Copy the duplicated slots into the gap
        for (int i = 0; i < numSlots; i++ ){
            stack[top - i - insertSlots - numSlots].setType(stack[top - i ].getType());
            stack[top - i - insertSlots - numSlots].setRef(stack[top - i].getRef());
            stack[top - i - insertSlots - numSlots].setNum(stack[top - i].getNum());
        }
    }

    /**
     * Return number of slots occupied by the stack element at index.
     */
    private int getElementLength(int index){
        ValueType type = stack[index].getType();
        return (type == ValueType.T_LONG || type == ValueType.T_DOUBLE) ? 2 : 1;
    }

    // Reference
    public void pushRef(Object ref){
        top++;
        stack[top].setType(ValueType.T_OBJECT);
        stack[top].setRef(ref);
    }

    public Object popRef(){
        return stack[top--].getRef();
    }

    // Int
    public void pushInt(int num){
        top++;
        stack[top].setType(ValueType.T_INT);
        stack[top].setNum(num);
    }

    public int popInt(){
        return stack[top--].getNum();
    }

    // Float
    public void pushFloat(float num){
        top++;
        stack[top].setType(ValueType.T_FLOAT);
        stack[top].setNum(Float.floatToRawIntBits(num));
    }

    public float popFloat(){
        return Float.intBitsToFloat(stack[top--].getNum());
    }

    // Long
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

    // Double
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
