package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

@Data
public class AttributeInfo {
    private int attributeNameIndex;
    private int attributeLength;
    private byte[] info;
}
