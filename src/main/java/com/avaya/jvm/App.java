package com.avaya.jvm;

import com.avaya.jvm.hotspot.share.classfile.BootClassLoader;
import com.avaya.jvm.hotspot.share.oops.InstanceKlass;
import com.avaya.jvm.hotspot.share.oops.MethodInfo;
import com.avaya.jvm.hotspot.share.prims.JavaNativeInterface;
import com.avaya.jvm.hotspot.share.runtime.JavaThread;
import com.avaya.jvm.hotspot.share.runtime.Threads;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

    public static void main(String[] args) throws IOException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        InstanceKlass klass = BootClassLoader.loadKlass("com.avaya.jvm.example.HelloWorld");
        MethodInfo main = JavaNativeInterface.getMain(klass);

        JavaThread thread = new JavaThread();
        Threads.addThread(thread);
        Threads.setCurrentThread(thread);

        JavaNativeInterface.callStaticMethod(main);
    }

}
