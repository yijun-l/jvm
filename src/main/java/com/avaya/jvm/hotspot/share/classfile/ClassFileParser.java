package com.avaya.jvm.hotspot.share.classfile;

import com.avaya.jvm.hotspot.share.oops.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassFile corresponds to the ClassFile structure in the JVM class file format.
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1
 *
 * ClassFile {
 *     u4             magic;
 *     u2             minor_version;
 *     u2             major_version;
 *     u2             constant_pool_count;
 *     cp_info        constant_pool[constant_pool_count-1];
 *     u2             access_flags;
 *     u2             this_class;
 *     u2             super_class;
 *     u2             interfaces_count;
 *     u2             interfaces[interfaces_count];
 *     u2             fields_count;
 *     field_info     fields[fields_count];
 *     u2             methods_count;
 *     method_info    methods[methods_count];
 *     u2             attributes_count;
 *     attribute_info attributes[attributes_count];
 * }
 *
 * The ClassFile structure is the top-level representation of a compiled
 * Java class or interface. It defines the class version, constant pool,
 * access permissions, class hierarchy, and includes field, method,
 * and attribute information.
 */

public class ClassFileParser {

    private static Logger logger = LoggerFactory.getLogger(ClassFileParser.class);

    public static InstanceKlass parseClassFile(byte[] content) throws IOException {

        logger.debug("Start parsing Class file");
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
        int fieldsCount = dis.readUnsignedShort();
        klass.setFieldsCount(fieldsCount);
        // field_info fields[fields_count];
        List<FieldInfo> fields = new ArrayList<>();
        parseFieldInfo(fieldsCount, fields, dis, klass.getConstantPool());
        klass.setFields(fields);

        // u2 methods_count;
        int methodsCount = dis.readUnsignedShort();
        klass.setMethodsCount(methodsCount);
        // method_info    methods[methods_count];
        List<MethodInfo> methods = new ArrayList<>();
        parseMethodInfo(methodsCount, methods, dis, klass.getConstantPool());
        klass.setMethods(methods);

        // u2 attributes_count;
        int attributesCount = dis.readUnsignedShort();
        klass.setAttributesCount(attributesCount);
        // attribute_info attributes[attributes_count];
        List<AttributeInfo> attributes = new ArrayList<>();
        parseAttributeInfo(attributesCount, attributes, dis, klass.getConstantPool());
        klass.setAttributes(attributes);

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

    private static void parseFieldInfo(int fieldsCount, List<FieldInfo> fields, DataInputStream dis, ConstantPool cp) throws IOException {
        for (int i = 0; i < fieldsCount; i++){
            FieldInfo fieldInfoEntry = new FieldInfo();
            fieldInfoEntry.setAccessFlags(dis.readUnsignedShort());
            fieldInfoEntry.setNameIndex(dis.readUnsignedShort());
            fieldInfoEntry.setDescriptorIndex(dis.readUnsignedShort());
            int attributeCount = dis.readUnsignedShort();
            fieldInfoEntry.setAttributesCount(attributeCount);
            List<AttributeInfo> attributes = new ArrayList<>();
            parseAttributeInfo(attributeCount, attributes, dis, cp);
            fieldInfoEntry.setAttributes(attributes);
            fields.add(fieldInfoEntry);
        }
    }
// parseMethodInfo(methodsCount, methods, dis);
    private static void parseMethodInfo(int methodsCount, List<MethodInfo> methods, DataInputStream dis, ConstantPool cp) throws IOException {
        for (int i = 0; i < methodsCount; i++){
            MethodInfo MethodInfoEntry = new MethodInfo();
            MethodInfoEntry.setAccessFlags(dis.readUnsignedShort());
            MethodInfoEntry.setNameIndex(dis.readUnsignedShort());
            MethodInfoEntry.setDescriptorIndex(dis.readUnsignedShort());
            int attributeCount = dis.readUnsignedShort();
            MethodInfoEntry.setAttributesCount(attributeCount);
            List<AttributeInfo> attributes = new ArrayList<>();
            parseAttributeInfo(attributeCount, attributes, dis, cp);
            MethodInfoEntry.setAttributes(attributes);
            methods.add(MethodInfoEntry);
        }
    }


    private static void parseAttributeInfo(int attributeCount, List<AttributeInfo> attributes, DataInputStream dis, ConstantPool cp) throws IOException{
        for (int i = 0; i < attributeCount; i++){
            attributes.add(AttributeInfo.parseAttribute(dis, cp));
        }
    }
}
