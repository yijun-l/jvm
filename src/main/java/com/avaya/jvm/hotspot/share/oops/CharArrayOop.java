package com.avaya.jvm.hotspot.share.oops;

public class CharArrayOop extends ArrayOop{
    private char[] values;
    // TODO: implement Char Array Klass later.
    private static final Klass CHAR_ARRAY_KLASS = null;

    public CharArrayOop(int length){
        this.values = new char[length];
        this.length = length;
        this.klazz = CHAR_ARRAY_KLASS;
    }

    public char get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, char value){
        checkBounds(index);
        values[index] = value;
    }
}
