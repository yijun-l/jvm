package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

@Data
public class MarkWord {
    Thread lockOwner = null;
}
