package com.avaya.jvm.hotspot.share.oops;

public class ByteArrayOop extends ArrayOop{
    private byte[] values;
    // TODO: implement Byte Array Klass later.
    private static final Klass BYTE_ARRAY_KLASS = null;

    public ByteArrayOop(int length){
        this.values = new byte[length];
        this.length = length;
        this.klazz = BYTE_ARRAY_KLASS;
    }

    public byte get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, byte value){
        checkBounds(index);
        values[index] = value;
    }
}
