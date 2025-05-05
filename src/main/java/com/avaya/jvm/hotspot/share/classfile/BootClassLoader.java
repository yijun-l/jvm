package com.avaya.jvm.hotspot.share.classfile;

import com.avaya.jvm.hotspot.share.oops.InstanceKlass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BootClassLoader {

    private static final Map<String, InstanceKlass> classLoaderData = new HashMap<>();

    public static InstanceKlass loadKlass(String name) throws IOException {
        return loadKlass(name, true);
    }

    public static InstanceKlass loadKlass(String name, boolean resolve) throws IOException {
        InstanceKlass klass = findLoadedKlass(name);
        if (klass != null){
            return klass;
        }

        klass = readAndParse(name);

        if (resolve){
            // TODO: implement symbol reference resolution (constant pool resolution)
        }

        return klass;
    }

    public static InstanceKlass findLoadedKlass(String name){
        return classLoaderData.get(name);
    }

    private static InstanceKlass readAndParse(String name) throws IOException {
        byte[] classBytes = readClassFile(toFilePath(name));
        InstanceKlass klass = ClassFileParser.parseClassFile(classBytes);

        classLoaderData.put(name, klass);
        return klass;
    }

    private static byte[] readClassFile(String filepath) throws IOException {
        return Files.readAllBytes(Path.of(filepath));
    }

    private static String toFilePath(String name){
        String searchPath = "target/classes/";
        String suffix = ".class";
        String tmpName = name.replace('.', '/');
        return searchPath + tmpName + suffix;
    }
}
