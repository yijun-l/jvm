package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class MethodInfo {

    private static Logger logger = LoggerFactory.getLogger(MethodInfo.class);

    private int accessFlags;
    private String name;
    private String descriptor;
    private int attributesCount;
    private List<AttributeInfo> attributes;

    public static MethodInfo parse(DataInputStream dis, ConstantPool cp, InstanceKlass klass) throws IOException {
        MethodInfo MethodInfoEntry = new MethodInfo();
        MethodInfoEntry.setAccessFlags(dis.readUnsignedShort());

        int nameIndex = dis.readUnsignedShort();
        if (cp.getEntries().get(nameIndex) instanceof ConstantUtf8Info){
            MethodInfoEntry.setName(((ConstantUtf8Info) cp.getEntries().get(nameIndex)).getValue());
        }

        int descriptorIndex = dis.readUnsignedShort();
        if (cp.getEntries().get(descriptorIndex) instanceof ConstantUtf8Info){
            MethodInfoEntry.setDescriptor(((ConstantUtf8Info) cp.getEntries().get(descriptorIndex)).getValue());
        }

        int attributeCount = dis.readUnsignedShort();
        MethodInfoEntry.setAttributesCount(attributeCount);
        logger.debug("│   ├── access: {}, name: {}, descriptor: {}", MethodInfoEntry.getAccessFlags(), MethodInfoEntry.getName(), MethodInfoEntry.getDescriptor());
        List<AttributeInfo> attributes = new ArrayList<>();
        parseAttributeInfo(attributeCount, attributes, dis, cp);
        MethodInfoEntry.setAttributes(attributes);
        for (AttributeInfo attr : MethodInfoEntry.getAttributes()) {
            if (attr instanceof CodeAttribute) {
                ((CodeAttribute) attr).getCode().setMethod(MethodInfoEntry);
                ((CodeAttribute) attr).getCode().setKlass(klass);
                break;
            }
        }
        return MethodInfoEntry;
    }

    private static void parseAttributeInfo(int attributeCount, List<AttributeInfo> attributes, DataInputStream dis, ConstantPool cp) throws IOException{
        for (int i = 0; i < attributeCount; i++){
            attributes.add(AttributeInfo.parseAttribute(dis, cp));
        }
    }
}
