package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.oops.CodeAttribute;
import lombok.Getter;

@Getter
public class JavaVFrame extends VFrame{
    private final LocalVariableArray locals;
    private final OperandStack operandStack;

    public JavaVFrame(CodeAttribute code){
        this.locals = new LocalVariableArray(code.getMaxLocals());
        this.operandStack = new OperandStack(code.getMaxStack());
    }
}
