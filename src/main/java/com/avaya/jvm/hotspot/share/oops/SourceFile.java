package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * SourceFile corresponds to the SourceFile_attribute in the JVM class file.
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.10
 *
 * SourceFile_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 sourcefile_index;
 * }
 */

@Data
public class SourceFile extends AttributeInfo{

    private static Logger logger = LoggerFactory.getLogger(SourceFile.class);

    private String sourceFile;
    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        logger.debug("  Source File Attribute parsing:");
        this.attributeLength = dis.readInt();
        ConstantInfo sourceFile = cp.getEntries().get(dis.readUnsignedShort());
        if (sourceFile instanceof ConstantUtf8Info){
            this.sourceFile = ((ConstantUtf8Info) sourceFile).getValue();
        }
        logger.debug("    source file: {}", this.sourceFile);
    }
}
