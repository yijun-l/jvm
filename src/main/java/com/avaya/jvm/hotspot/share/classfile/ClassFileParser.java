package com.avaya.jvm.hotspot.share.classfile;

import com.avaya.jvm.hotspot.share.oops.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ClassFileParser {

    public static InstanceKlass parseClassFile(byte[] content) throws IOException {
        InstanceKlass klass = new InstanceKlass();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(content));

        // u4 magic
        klass.setMagic(dis.readInt());

        // u2 minor_version
        klass.setMinorVersion(dis.readUnsignedShort());
        // u2 major_version
        klass.setMajorVersion(dis.readUnsignedShort());

        // u2 constant_pool_count
        klass.setConstantPoolCount(dis.readUnsignedShort());
        // cp_info constant_pool[constant_pool_count-1];
        parseConstantPool(klass.getConstantPoolCount(), dis, klass.getConstantPool());

        // u2 access_flags
        klass.setAccessFlags(dis.readUnsignedShort());

        // u2 this_class
        klass.setThis_class(dis.readUnsignedShort());

        // u2 super_class
        klass.setSuper_class(dis.readUnsignedShort());

        // u2 interfaces_count
        int interfaceCount = dis.readUnsignedShort();
        klass.setInterfaceCount(interfaceCount);
        // u2 interfaces[interfaces_count]
        int[] interfaces = new int[interfaceCount];
        for(int i = 0; i < interfaceCount; i++){
            interfaces[i] = dis.readUnsignedShort();
        }
        klass.setInterfaces(interfaces);

        // u2 fields_count
        klass.setFieldsCount(dis.readUnsignedShort());
        // field_info fields[fields_count];
        // u2 methods_count;
        // method_info    methods[methods_count];
        // u2 attributes_count;
        // attribute_info attributes[attributes_count];

        System.out.printf("Magic: 0x%08X\n", klass.getMagic());
        System.out.println("Version: " + klass.getMajorVersion() + "." + klass.getMinorVersion());
        System.out.println("Constant Pool Size: " + klass.getConstantPoolCount());
        System.out.println("Field Info count: " + klass.getFieldsCount());

        return klass;
    }

    private static void parseConstantPool(int constantPoolCount, DataInputStream dis, ConstantPool constantPool) throws IOException {

        for (int index = 1; index < constantPoolCount; index++){
            int tag = dis.readUnsignedByte();
            ConstantTag type = ConstantTag.fromValue(tag);
            switch(type){
                case JVM_CONSTANT_UTF8 -> {
                    int length = dis.readUnsignedShort();
                    byte[] buffer = new byte[length];
                    dis.readFully(buffer);
                    String value = new String(buffer, StandardCharsets.UTF_8);
                    constantPool.add(new ConstantUtf8Info(value));
                }
                case JVM_CONSTANT_CLASS -> {
                    int nameIndex = dis.readUnsignedShort();
                    constantPool.add(new ConstantClassInfo(nameIndex));
                }
                case JVM_CONSTANT_STRING -> {
                    int stringIndex = dis.readUnsignedShort();
                    constantPool.add(new ConstantStringInfo(stringIndex));
                }
                case JVM_CONSTANT_FIELDREF -> {
                    int classIndex = dis.readUnsignedShort();
                    int nameAndTypeIndex = dis.readUnsignedShort();
                    constantPool.add(new ConstantFieldrefInfo(classIndex, nameAndTypeIndex));
                }
                case JVM_CONSTANT_METHODREF -> {
                    int classIndex = dis.readUnsignedShort();
                    int nameAndTypeIndex = dis.readUnsignedShort();
                    constantPool.add(new ConstantMethodrefInfo(classIndex, nameAndTypeIndex));
                }
                case JVM_CONSTANT_NAME_AND_TYPE -> {
                    int nameIndex = dis.readUnsignedShort();
                    int descriptorIndex = dis.readUnsignedShort();
                    constantPool.add(new ConstantNameAndTypeInfo(nameIndex, descriptorIndex));
                }
                default -> throw new IOException("Unsupported constant pool tag: " + tag);
            }
        }
    }
}
