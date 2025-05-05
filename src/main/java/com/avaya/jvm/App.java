package com.avaya.jvm;

import com.avaya.jvm.hotspot.share.classfile.BootClassLoader;
import com.avaya.jvm.hotspot.share.oops.InstanceKlass;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        BootClassLoader.loadKlass("com.avaya.jvm.example.HelloWorld");
        InstanceKlass klass = BootClassLoader.findLoadedKlass("com.avaya.jvm.example.HelloWorld");
        InstanceKlass.printKlass(klass);
    }

}
