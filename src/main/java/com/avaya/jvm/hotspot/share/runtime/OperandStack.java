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

}
