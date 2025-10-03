package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

/**
 * Constant pool entry for {@code CONSTANT_Long_info}.
 *
 * Structure:
 * <pre>
 * CONSTANT_Long_info {
 *     u1 tag;
 *     u4 high_bytes;
 *     u4 low_bytes;
 * }
 * </pre>
 */
@Data
public class ConstantLongInfo extends ConstantInfo{
    private long value;

    public ConstantLongInfo(long value){
        this.tag = ConstantTag.JVM_CONSTANT_LONG;
        this.value = value;
    }
}
