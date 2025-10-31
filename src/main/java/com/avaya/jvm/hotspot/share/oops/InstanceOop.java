package com.avaya.jvm.hotspot.share.oops;

import com.avaya.jvm.hotspot.share.classfile.BootClassLoader;
import lombok.Data;

import java.io.IOException;

@Data
public class InstanceOop extends OopDesc{

    private final FieldArray oopFields;

    public InstanceOop(String className) throws IOException {
        this.klazz = BootClassLoader.loadKlass(className.replace('/', '.'));
        this.oopFields = new FieldArray((InstanceKlass)this.klazz, false);
        this.markWord = new MarkWord();
    }

    public InstanceKlass getKlass(){
        return (InstanceKlass) this.klazz;
    }
}
