package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LocalVariableTable corresponds to the LocalVariableTable_attribute in the JVM class file.
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.13
 *
 * LocalVariableTable_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 local_variable_table_length;
 *     {
 *         u2 start_pc;
 *         u2 length;
 *         u2 name_index;
 *         u2 descriptor_index;
 *         u2 index;
 *     } local_variable_table[local_variable_table_length];
 * }
 */

@Data
public class LocalVariableTable extends AttributeInfo {
    private static Logger logger = LoggerFactory.getLogger(LocalVariableTable.class);

    private int localVariableTableLength;
    private List<LocalVariableEntry> entries = new ArrayList<>();

    @Data
    public static class LocalVariableEntry {
        private int startPc;
        private int length;
        private String name;
        private Descriptor descriptor;
        private int index;
    }

    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        this.setAttributeLength(dis.readInt());
        this.setLocalVariableTableLength(dis.readUnsignedShort());
        logger.debug("│   │   ├── Local Variable Table Attribute parsing:");
        for(int i = 0; i < this.getLocalVariableTableLength(); i++) {
            LocalVariableEntry entry = new LocalVariableEntry();
            entry.setStartPc(dis.readUnsignedShort());
            entry.setLength(dis.readUnsignedShort());
            ConstantInfo name = cp.getEntries().get(dis.readUnsignedShort());
            if (name instanceof ConstantUtf8Info){
                entry.setName(((ConstantUtf8Info) name).getValue());
            }
            ConstantInfo descriptor = cp.getEntries().get(dis.readUnsignedShort());
            if (descriptor instanceof ConstantUtf8Info){
                entry.setDescriptor(new Descriptor(((ConstantUtf8Info) descriptor).getValue()));
            }
            entry.setIndex(dis.readUnsignedShort());
            this.getEntries().add(entry);
            logger.debug("│   │   │   ├── [{}] start pc: {}, length: {}. name: {}, descriptor: {}, index: {}", i, entry.startPc, entry.length, entry.name, entry.descriptor.fieldToString(), entry.index);
        }
    }
}
