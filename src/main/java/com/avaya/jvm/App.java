package com.avaya.jvm;

import com.avaya.jvm.hotspot.share.classfile.ClassFileParser;
import com.avaya.jvm.hotspot.share.oops.AttributeInfo;
import com.avaya.jvm.hotspot.share.oops.FieldInfo;
import com.avaya.jvm.hotspot.share.oops.InstanceKlass;
import com.avaya.jvm.hotspot.share.oops.MethodInfo;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
            printKlass(klass);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void printKlass(InstanceKlass klass){

        System.out.printf("Magic: 0x%08X\n", klass.getMagic());
        System.out.println("Version: " + klass.getMajorVersion() + "." + klass.getMinorVersion());
        System.out.println("Constant Pool Size: " + klass.getConstantPoolCount());
        int fieldsCount = klass.getFieldsCount();
        System.out.println("Field Info count: " + fieldsCount);
        for (int i = 0; i < fieldsCount; i ++){
            FieldInfo fieldInfoEntry = klass.getFields().get(i);
            System.out.println("  Field#" + i + ": ");
            System.out.println("    accessFlags: " + fieldInfoEntry.getAccessFlags());
            System.out.println("    nameIndex: " + fieldInfoEntry.getNameIndex());
            System.out.println("    descriptorIndex: " + fieldInfoEntry.getDescriptorIndex());
            System.out.println("    attributeCount: " + fieldInfoEntry.getAttributesCount());
            printAttributes(fieldInfoEntry.getAttributesCount(), fieldInfoEntry.getAttributes());
        }
        int methodsCount = klass.getMethodsCount();
        System.out.println("Method Info count: " + methodsCount);
        for (int i = 0; i < methodsCount; i ++){
            MethodInfo methodInfoEntry = klass.getMethods().get(i);
            System.out.println("  Method#" + i + ": ");
            System.out.println("    accessFlags: " + methodInfoEntry.getAccessFlags());
            System.out.println("    nameIndex: " + methodInfoEntry.getNameIndex());
            System.out.println("    descriptorIndex: " + methodInfoEntry.getDescriptorIndex());
            System.out.println("    attributeCount: " + methodInfoEntry.getAttributesCount());
            printAttributes(methodInfoEntry.getAttributesCount(), methodInfoEntry.getAttributes());
        }
        int attributesCount = klass.getAttributesCount();
        System.out.println("Attribute Info count: " + attributesCount);
        printAttributes(attributesCount, klass.getAttributes());

    }

    private static void printAttributes(int attributesCount, List<AttributeInfo> attributes){
        for (int i = 0; i < attributesCount; i ++){
            AttributeInfo attributeInfoEntry = attributes.get(i);
            System.out.println("    attribute#" + i + ": ");
            System.out.println("      attributeNameIndex: " + attributeInfoEntry.getAttributeNameIndex());
            System.out.println("      attributeLength: " + attributeInfoEntry.getAttributeLength());
            byte[] info = attributeInfoEntry.getInfo();
            String hexString = String.format("%0" + (info.length * 2) + "X", new BigInteger(1, info));
            System.out.println("      info: " + hexString);
        }
    }
}
