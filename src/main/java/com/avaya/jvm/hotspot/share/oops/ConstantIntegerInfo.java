package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_Integer_info}.
 *
 * Structure:
 * <pre>
 * CONSTANT_Integer_info {
 *     u1 tag;
 *     u4 bytes;
 * }
 * </pre>
 */
@Data
public class ConstantIntegerInfo extends ConstantInfo{
    private int value;

    public ConstantIntegerInfo(int value){
        this.tag = ConstantTag.JVM_CONSTANT_INTEGER;
        this.value = value;
    }
}
