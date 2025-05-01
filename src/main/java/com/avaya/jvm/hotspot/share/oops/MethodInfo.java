package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.util.List;

@Data
public class MethodInfo {
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    private List<AttributeInfo> attributes;
}
