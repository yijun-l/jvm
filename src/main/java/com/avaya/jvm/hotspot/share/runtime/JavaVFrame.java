package com.avaya.jvm.hotspot.share.runtime;

import lombok.Getter;

@Getter
public class JavaVFrame extends VFrame{
    private final LocalVariableArray locals;
    private final OperandStack operandStack;

    public JavaVFrame(LocalVariableArray locals, OperandStack operandStack) {
        this.locals = locals;
        this.operandStack = operandStack;
    }
}
