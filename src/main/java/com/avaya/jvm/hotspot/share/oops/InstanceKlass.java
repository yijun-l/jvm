package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class InstanceKlass extends Klass{

    private int magic;
    private int minorVersion;
    private int majorVersion;

    private int constantPoolCount;
    private ConstantPool constantPool;

    private int accessFlags;
    private int this_class;
    private int super_class;

    private int interfaceCount;
    private int[] interfaces;

    private int fieldsCount;
    private List<FieldInfo> fields;
    private FieldArray staticFields;

    private int methodsCount;
    private List<MethodInfo> methods;

    private int attributesCount;
    private List<AttributeInfo> attributes;

    public InstanceKlass(){
        constantPool = new ConstantPool();
    }

    public static void printKlass(InstanceKlass klass){

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
            System.out.println("    nameIndex: " + methodInfoEntry.getName());
            System.out.println("    descriptorIndex: " + methodInfoEntry.getDescriptor());
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
//            System.out.println("      attributeNameIndex: " + attributeInfoEntry.getAttributeNameIndex());
            System.out.println("      attributeLength: " + attributeInfoEntry.getAttributeLength());
            if (attributeInfoEntry instanceof UnknownAttribute) {
                byte[] info = ((UnknownAttribute) attributeInfoEntry).getInfo();
                String hexString = String.format("%0" + (info.length * 2) + "X", new BigInteger(1, info));
                System.out.println("      info: " + hexString);
            }
        }
    }
}
