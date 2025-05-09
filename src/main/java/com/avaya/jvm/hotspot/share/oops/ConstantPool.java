package com.avaya.jvm.hotspot.share.oops;

import java.util.ArrayList;
import java.util.List;

public class ConstantPool {

    private List<ConstantInfo> entries;

    public ConstantPool(){
        this.entries = new ArrayList<>();
        this.entries.add(null);
    }

    public void add(ConstantInfo info){
        entries.add(info);
    }
}
