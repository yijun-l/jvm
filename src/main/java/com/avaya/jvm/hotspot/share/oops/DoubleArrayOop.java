package com.avaya.jvm.hotspot.share.oops;

public class DoubleArrayOop extends ArrayOop{
    private double[] values;
    // TODO: implement Double Array Klass later.
    private static final Klass DOUBLE_ARRAY_KLASS = null;

    public DoubleArrayOop(int length){
        this.values = new double[length];
        this.length = length;
        this.klazz = DOUBLE_ARRAY_KLASS;
    }

    public double get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, double value){
        checkBounds(index);
        values[index] = value;
    }
}
