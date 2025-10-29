package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_MethodType_info}.
 * <p>
 * Structure:
 * <pre>
 * CONSTANT_MethodType_info {
 *     u1 tag;
 *     u2 descriptor_index;
 * }
 * </pre>
 */
@Data
public class ConstantMethodTypeInfo extends ConstantInfo{
    private int descriptorIndex;

    ConstantMethodTypeInfo(int descriptorIndex){
        this.tag = ConstantTag.JVM_CONSTANT_METHOD_TYPE;
        this.descriptorIndex = descriptorIndex;
    }
}
