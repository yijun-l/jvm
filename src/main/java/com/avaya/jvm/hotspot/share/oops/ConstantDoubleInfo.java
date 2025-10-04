package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_Double_info}.
 *
 * Structure:
 * <pre>
 * CONSTANT_Double_info {
 *     u1 tag;
 *     u4 high_bytes;
 *     u4 low_bytes;
 * }
 * </pre>
 */
@Data
public class ConstantDoubleInfo extends ConstantInfo{
    private double value;

    public ConstantDoubleInfo(double value){
        this.tag = ConstantTag.JVM_CONSTANT_DOUBLE;
        this.value = value;
    }
}
