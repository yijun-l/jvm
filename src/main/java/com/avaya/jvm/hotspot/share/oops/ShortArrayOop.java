package com.avaya.jvm.hotspot.share.oops;

public class ShortArrayOop extends ArrayOop{
    private short[] values;
    // TODO: implement Short Array Klass later.
    private static final Klass SHORT_ARRAY_KLASS = null;

    public ShortArrayOop(int length){
        this.values = new short[length];
        this.length = length;
        this.klazz = SHORT_ARRAY_KLASS;
    }

    public short get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, short value){
        checkBounds(index);
        values[index] = value;
    }
}
