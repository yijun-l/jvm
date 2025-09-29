package com.avaya.jvm.hotspot.share.classfile;

import com.avaya.jvm.hotspot.share.oops.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.avaya.jvm.hotspot.share.utilities.ClassAccessFlags;
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

    private static final Logger logger = LoggerFactory.getLogger(ClassFileParser.class);

    public static InstanceKlass parseClassFile(byte[] content) throws IOException {

        logger.info("Start class file parsing...");
        InstanceKlass klass = new InstanceKlass();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(content));

        // u4 magic
        klass.setMagic(dis.readInt());

        // u2 minor_version
        klass.setMinorVersion(dis.readUnsignedShort());
        // u2 major_version
        klass.setMajorVersion(dis.readUnsignedShort());
        logger.debug("├── magic: 0x{}", Integer.toHexString(klass.getMagic()).toUpperCase());
        logger.debug("├── version: minor {}, major {}", klass.getMinorVersion(), klass.getMajorVersion());

        // u2 constant_pool_count
        klass.setConstantPoolCount(dis.readUnsignedShort());
        // cp_info constant_pool[constant_pool_count-1];
        klass.getConstantPool().parse(klass.getConstantPoolCount(), dis);
        logger.debug("├── constant pool count: {}", klass.getConstantPoolCount());

        // u2 access_flags
        klass.setAccessFlags(dis.readUnsignedShort());

        // u2 this_class
        klass.setThis_class(dis.readUnsignedShort());

        // u2 super_class
        klass.setSuper_class(dis.readUnsignedShort());
        logger.debug("├── access flag: {}", ClassAccessFlags.flagsToString(klass.getAccessFlags()) );

        ConstantClassInfo thisClass = (ConstantClassInfo) klass.getConstantPool().getEntries().get(klass.getThis_class());
        logger.debug("├── this: {}", thisClass.resolveName(klass.getConstantPool()));

        ConstantClassInfo superClass = (ConstantClassInfo) klass.getConstantPool().getEntries().get(klass.getSuper_class());
        logger.debug("├── super: {}", superClass.resolveName(klass.getConstantPool()));

        // u2 interfaces_count
        int interfaceCount = dis.readUnsignedShort();
        klass.setInterfaceCount(interfaceCount);
        // u2 interfaces[interfaces_count]
        int[] interfaces = new int[interfaceCount];
        for(int i = 0; i < interfaceCount; i++){
            interfaces[i] = dis.readUnsignedShort();
        }
        klass.setInterfaces(interfaces);
        logger.debug("├── interfaces count: {}", klass.getInterfaceCount());

        // u2 fields_count
        int fieldsCount = dis.readUnsignedShort();
        klass.setFieldsCount(fieldsCount);
        // field_info fields[fields_count];
        List<FieldInfo> fields = new ArrayList<>();
        parseFieldInfo(fieldsCount, fields, dis, klass.getConstantPool());
        klass.setFields(fields);
        logger.debug("├── fields count: {}", klass.getFieldsCount());

        // u2 methods_count;
        int methodsCount = dis.readUnsignedShort();
        klass.setMethodsCount(methodsCount);
        logger.debug("├── methods count: {}", klass.getMethodsCount());
        // method_info    methods[methods_count];
        List<MethodInfo> methods = new ArrayList<>();
        parseMethodInfo(methodsCount, methods, dis, klass.getConstantPool(), klass);
        klass.setMethods(methods);


        // u2 attributes_count;
        int attributesCount = dis.readUnsignedShort();
        klass.setAttributesCount(attributesCount);
        logger.debug("├── attributes count: {}", klass.getAttributesCount());
        // attribute_info attributes[attributes_count];
        List<AttributeInfo> attributes = new ArrayList<>();
        parseAttributeInfo(attributesCount, attributes, dis, klass.getConstantPool());
        klass.setAttributes(attributes);
        logger.info("Complete class file parsing.");

        return klass;
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

    private static void parseMethodInfo(int methodsCount, List<MethodInfo> methods, DataInputStream dis, ConstantPool cp, InstanceKlass klass) throws IOException {
        for (int i = 0; i < methodsCount; i++){
            logger.debug("│   ├── method#{}: ", i);
            methods.add(MethodInfo.parse(dis, cp, klass));
        }
    }

    private static void parseAttributeInfo(int attributeCount, List<AttributeInfo> attributes, DataInputStream dis, ConstantPool cp) throws IOException{
        for (int i = 0; i < attributeCount; i++){
            logger.debug("│   ├── attribute#{}: ", i);
            attributes.add(AttributeInfo.parseAttribute(dis, cp));
        }
    }
}
