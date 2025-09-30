package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.utilities.ValueType;
import lombok.Data;

/**
 * A slot in the local variable array or operand stack of a JVM frame.
 * <p>
 * Each StackValue stores a value along with its type. It can hold:
 * - Primitive types (int, float, etc.)
 * - Object references
 * <p>
 * According to the JVM specification:
 * - A single slot can store boolean, byte, char, short, int, float, reference, or returnAddress.
 * - Long and double occupy two consecutive slots, but each slot is represented by a separate StackValue.
 * <p>
 * Type-specific operations are handled in OperandStack and LocalVariableArray.
 */
@Data
public class StackValue {
    private ValueType type;
    private Object ref;
    private int num;

    public StackValue() {
        this.type = ValueType.T_ILLEGAL;
        this.ref = null;
        this.num = 0;
    }
}
