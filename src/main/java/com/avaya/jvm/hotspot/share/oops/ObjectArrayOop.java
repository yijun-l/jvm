package com.avaya.jvm.hotspot.share.oops;

public class ObjectArrayOop extends ArrayOop{
    private InstanceOop[] values;
    // TODO: implement Object Array Klass later.
    private static final Klass OBJECT_ARRAY_KLASS = null;

    public ObjectArrayOop(int length){
        this.values = new InstanceOop[length];
        this.length = length;
        this.klazz = OBJECT_ARRAY_KLASS;
    }

    public InstanceOop get(int index){
        checkBounds(index);
        return values[index];
    }

    public void set(int index, InstanceOop value){
        checkBounds(index);
        values[index] = value;
    }
}
