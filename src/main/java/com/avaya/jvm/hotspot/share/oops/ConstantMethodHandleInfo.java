package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_MethodHandle_info}.
 * <p>
 * Structure:
 * <pre>
 * CONSTANT_MethodHandle_info  {
 *     u1 tag;
 *     u1 reference_kind;
 *     u2 reference_index;
 * }
 * </pre>
 */
@Data
public class ConstantMethodHandleInfo extends ConstantInfo{
    private int referenceKind;
    private int referenceIndex;

    public ConstantMethodHandleInfo(int referenceKind, int referenceIndex){
        this.tag = ConstantTag.JVM_CONSTANT_METHOD_HANDLE;
        this.referenceKind = referenceKind;
        this.referenceIndex = referenceIndex;
    }
}
