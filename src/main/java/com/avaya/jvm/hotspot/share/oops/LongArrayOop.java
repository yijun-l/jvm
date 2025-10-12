package com.avaya.jvm.hotspot.share.oops;

public class LongArrayOop extends ArrayOop{
    private long[] values;
    // TODO: implement Long Array Klass later.
    private static final Klass LONG_ARRAY_KLASS = null;

    public LongArrayOop(int length){
        this.values = new long[length];
        this.length = length;
        this.klazz = LONG_ARRAY_KLASS;
    }

    public long get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, long value){
        checkBounds(index);
        values[index] = value;
    }
}
