package com.avaya.jvm.hotspot.share.oops;

import com.avaya.jvm.hotspot.share.utilities.MemberAccessFlags;
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
    private Descriptor descriptor;
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

        Object entry = cp.getEntries().get(descriptorIndex);
        if (!(entry instanceof ConstantUtf8Info)) {
            throw new IllegalStateException("Descriptor index " + descriptorIndex + " is not ConstantUtf8Info");
        }
        ConstantUtf8Info utf8Info = (ConstantUtf8Info) entry;
        MethodInfoEntry.setDescriptor(new Descriptor(utf8Info.getValue()));

        int attributeCount = dis.readUnsignedShort();
        MethodInfoEntry.setAttributesCount(attributeCount);
        logger.debug("│   ├── access: {}, name: {}, descriptor: {}", MemberAccessFlags.flagsToString(MethodInfoEntry.getAccessFlags()), MethodInfoEntry.getName(), MethodInfoEntry.getDescriptor().methodToString());
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
