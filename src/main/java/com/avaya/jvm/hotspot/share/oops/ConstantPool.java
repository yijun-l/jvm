package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Data
public class ConstantPool {

    private List<ConstantInfo> entries;

    public ConstantPool(){
        this.entries = new ArrayList<>();
        this.entries.add(null);
    }

    public void add(ConstantInfo info){
        entries.add(info);
    }

    public void parse(int constantPoolCount, DataInputStream dis) throws IOException, IOException {

        for (int index = 1; index < constantPoolCount; index++){
            int tag = dis.readUnsignedByte();
            ConstantTag type = ConstantTag.fromValue(tag);
            switch(type){
                case JVM_CONSTANT_UTF8 -> {
                    int length = dis.readUnsignedShort();
                    byte[] buffer = new byte[length];
                    dis.readFully(buffer);
                    String value = new String(buffer, StandardCharsets.UTF_8);
                    add(new ConstantUtf8Info(value));
                }
                case JVM_CONSTANT_CLASS -> {
                    int nameIndex = dis.readUnsignedShort();
                    add(new ConstantClassInfo(nameIndex));
                }
                case JVM_CONSTANT_STRING -> {
                    int stringIndex = dis.readUnsignedShort();
                    add(new ConstantStringInfo(stringIndex));
                }
                case JVM_CONSTANT_FIELDREF -> {
                    int classIndex = dis.readUnsignedShort();
                    int nameAndTypeIndex = dis.readUnsignedShort();
                    add(new ConstantFieldrefInfo(classIndex, nameAndTypeIndex));
                }
                case JVM_CONSTANT_METHODREF -> {
                    int classIndex = dis.readUnsignedShort();
                    int nameAndTypeIndex = dis.readUnsignedShort();
                    add(new ConstantMethodrefInfo(classIndex, nameAndTypeIndex));
                }
                case JVM_CONSTANT_NAME_AND_TYPE -> {
                    int nameIndex = dis.readUnsignedShort();
                    int descriptorIndex = dis.readUnsignedShort();
                    add(new ConstantNameAndTypeInfo(nameIndex, descriptorIndex));
                }
                default -> throw new IOException("Unsupported constant pool tag: " + tag);
            }
        }
    }

}
