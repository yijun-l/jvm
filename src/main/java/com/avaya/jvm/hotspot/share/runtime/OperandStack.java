package com.avaya.jvm.hotspot.share.runtime;

public class OperandStack {
    private final StackValue[] stack;
    private int top = -1;

    public OperandStack(int maxSize) {
        stack = new StackValue[maxSize];
    }

    public void push(StackValue value){
        stack[++top] = value;
    }

    public StackValue pop(){
        return stack[top--];
    }

    public StackValue peek(){
        return stack[top];
    }
}
