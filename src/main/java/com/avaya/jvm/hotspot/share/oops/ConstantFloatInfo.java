package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_Float_info}.
 *
 * Structure:
 * <pre>
 * CONSTANT_Float_info {
 *     u1 tag;
 *     u4 bytes;
 * }
 * </pre>
 */
@Data
public class ConstantFloatInfo extends ConstantInfo{
    private float value;

    public ConstantFloatInfo(float value){
        this.tag = ConstantTag.JVM_CONSTANT_FLOAT;
        this.value = value;
    }
}
