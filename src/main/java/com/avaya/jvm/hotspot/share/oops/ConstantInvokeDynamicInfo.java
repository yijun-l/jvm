package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_InvokeDynamic_info}.
 * <p>
 * Structure:
 * <pre>
 * CONSTANT_InvokeDynamic_info  {
 *     u1 tag;
 *     u2 bootstrap_method_attr_index;;
 *     u2 name_and_type_index;
 * }
 * </pre>
 */
@Data
public class ConstantInvokeDynamicInfo extends ConstantInfo{
    private int bootstrapMethodAttrIndex;
    private int nameAndTypeIndex;

    public ConstantInvokeDynamicInfo(int bootstrapMethodAttrIndex,int nameAndTypeIndex ){
        this.tag = ConstantTag.JVM_CONSTANT_INVOKE_DYNAMIC;
        this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }
}
