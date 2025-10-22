package com.avaya.jvm.hotspot.share.utilities;

import lombok.Data;

@Data
public class TmpValue {
    int i;
    long j;
    float f;
    double d;
    Object l;

    public TmpValue(){
        i = 0;
        j = 0;
        f = 0;
        d = 0;
        l = null;
    }
}
