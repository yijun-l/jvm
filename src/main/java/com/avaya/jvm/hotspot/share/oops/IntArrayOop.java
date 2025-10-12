package com.avaya.jvm.hotspot.share.oops;

public class IntArrayOop extends ArrayOop{
    private int[] values;
    // TODO: implement Int Array Klass later.
    private static final Klass INT_ARRAY_KLASS = null;

    public IntArrayOop(int length){
        this.values = new int[length];
        this.length = length;
        this.klazz = INT_ARRAY_KLASS;
    }

    public int get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, int value){
        checkBounds(index);
        values[index] = value;
    }
}
