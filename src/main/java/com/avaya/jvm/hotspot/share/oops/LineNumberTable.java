package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LineNumberTable corresponds to the LineNumberTable_attribute in the JVM class file.
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.12
 *
 * LineNumberTable_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 line_number_table_length;
 *     {
 *         u2 start_pc;
 *         u2 line_number;
 *     } line_number_table[line_number_table_length];
 * }
 */

@Data
public class LineNumberTable extends AttributeInfo {
    private static Logger logger = LoggerFactory.getLogger(LineNumberTable.class);

    private int lineNumberTableLength;
    private List<LineNumberEntry> entries = new ArrayList<>();

    @Data
    public static class LineNumberEntry {
        private int startPc;
        private int lineNumber;
    }

    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        this.setAttributeLength(dis.readInt());
        this.setLineNumberTableLength(dis.readUnsignedShort());
        logger.debug("│   │   ├── Line Number Table Attribute parsing:");
        for (int i = 0; i < this.getLineNumberTableLength(); i++){
            LineNumberEntry entry = new LineNumberEntry();
            entry.setStartPc(dis.readUnsignedShort());
            entry.setLineNumber(dis.readUnsignedShort());
            this.getEntries().add(entry);
            logger.debug("│   │   │   ├── [{}] start pc: {}, line number: {}", i, entry.startPc, entry.lineNumber);
        }
    }
}
