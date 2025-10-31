package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

@Data
public abstract class OopDesc {
    protected MarkWord markWord;
    protected Klass klazz;
}
