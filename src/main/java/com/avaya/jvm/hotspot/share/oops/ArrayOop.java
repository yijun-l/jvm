package com.avaya.jvm.hotspot.share.oops;

public abstract class ArrayOop extends OopDesc{
    protected int length;

    protected void checkBounds(int index){
        if (index < 0 || index >= length){
            throw new IndexOutOfBoundsException();
        }
    }

    public int getLength(){
        return length;
    }
}
