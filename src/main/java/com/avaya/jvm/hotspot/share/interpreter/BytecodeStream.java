package com.avaya.jvm.hotspot.share.interpreter;

import com.avaya.jvm.hotspot.share.oops.CodeAttribute;
import com.avaya.jvm.hotspot.share.oops.InstanceKlass;
import com.avaya.jvm.hotspot.share.oops.MethodInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BytecodeStream extends BaseBytecodeStream{
    private MethodInfo method;
    private InstanceKlass klass;
    private final CodeAttribute code;

    public BytecodeStream(byte[] codes, CodeAttribute code) {
        super(codes);
        this.code = code;
    }
}
