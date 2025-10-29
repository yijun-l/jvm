package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.io.DataInputStream;
import java.io.IOException;

@Data
public abstract class AttributeInfo {
    protected int attributeLength;
//    protected int attributeNameIndex;
    
    public abstract void parse(DataInputStream dis, ConstantPool cp) throws IOException;

    public static AttributeInfo parseAttribute(DataInputStream dis, ConstantPool cp) throws IOException {
        ConstantInfo name = cp.getEntries().get(dis.readUnsignedShort());
        AttributeType attrType = AttributeType.UNKNOWN;
        if (name instanceof ConstantUtf8Info){
            attrType = AttributeType.fromName(((ConstantUtf8Info) name).getValue());
        }

        AttributeInfo attr;
        switch (attrType) {
            case CODE -> attr = new CodeAttribute();
            case LINE_NUMBER_TABLE -> attr = new LineNumberTable();
            case LOCAL_VARIABLE_TABLE -> attr = new LocalVariableTable();
            case STACK_MAP_TABLE -> attr = new StackMapTable();
            case SOURCE_FILE -> attr = new SourceFile();
            case BOOTSTRAP_METHODS -> attr = new BootstrapMethods();
            default -> attr = new UnknownAttribute();
        }
        attr.parse(dis, cp);
        return attr;
    }
}