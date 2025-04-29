package com.avaya.jvm;

import com.avaya.jvm.hotspot.share.classfile.ClassFileParser;
import com.avaya.jvm.hotspot.share.oops.InstanceKlass;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Hello world!
 */

public class App {

    private static final String classFile = "src/main/java/com/avaya/jvm/Test.class";


    public static byte[] readClassFile(String filepath) throws IOException{
        return Files.readAllBytes(Path.of(filepath));
    }

    public static void main(String[] args) {
        InstanceKlass klass;
        try{
            byte[] classBytes = readClassFile(classFile);
            klass = ClassFileParser.parseClassFile(classBytes);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
