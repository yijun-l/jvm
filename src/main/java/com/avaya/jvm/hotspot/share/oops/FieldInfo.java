package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.util.List;

@Data
public class FieldInfo {
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    List<AttributeInfo> attributes;
}
