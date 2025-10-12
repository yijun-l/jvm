package com.avaya.jvm.hotspot.share.oops;

public class FloatArrayOop extends ArrayOop{
    private float[] values;
    // TODO: implement Float Array Klass later.
    private static final Klass FLOAT_ARRAY_KLASS = null;

    public FloatArrayOop(int length){
        this.values = new float[length];
        this.length = length;
        this.klazz = FLOAT_ARRAY_KLASS;
    }

    public float get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, float value){
        checkBounds(index);
        values[index] = value;
    }
}
