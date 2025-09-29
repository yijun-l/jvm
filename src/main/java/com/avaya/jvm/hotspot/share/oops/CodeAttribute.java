package com.avaya.jvm.hotspot.share.oops;

import com.avaya.jvm.hotspot.share.interpreter.BytecodeStream;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;

/**
 * CodeAttribute corresponds to the Code_attribute structure in the JVM class file.
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.3
 *
 * Code_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 max_stack;
 *     u2 max_locals;
 *     u4 code_length;
 *     u1 code[code_length];
 *     u2 exception_table_length;
 *     {
 *         u2 start_pc;
 *         u2 end_pc;
 *         u2 handler_pc;
 *         u2 catch_type;
 *     } exception_table[exception_table_length];
 *     u2 attributes_count;
 *     attribute_info attributes[attributes_count];
 * }
 */

@Data
public class CodeAttribute extends AttributeInfo {

    private static Logger logger = LoggerFactory.getLogger(CodeAttribute.class);

    private int maxStack;
    private int maxLocals;

    private int codeLength;
    private BytecodeStream code;

    private int exceptionTableLength;
    private List<ExceptionTableEntry> exceptionTable = new ArrayList<>();

    private int attributeCount;
    private List<AttributeInfo> attributes = new ArrayList<>();

    @Data
    public static class ExceptionTableEntry {
        private int startPc;
        private int endPc;
        private int handlerPc;
        private int catchType;
    }

    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        this.attributeLength = dis.readInt();
        this.maxStack = dis.readUnsignedShort();
        this.maxLocals = dis.readUnsignedShort();
        this.codeLength = dis.readInt();
        byte[] tmp = new byte[this.codeLength];
        dis.read(tmp);
        this.code = new BytecodeStream(tmp, this);

        logger.debug("│   ├── Code Attribute parsing...");
        logger.debug("│   │   ├── Max Stack: {}, Max Locals: {}", this.maxStack, this.maxLocals);
        logger.debug("│   │   ├── Code: {}", String.format("%0" + (this.code.getCodes().length * 2) + "X", new BigInteger(1, this.code.getCodes())));
        logger.debug("│   │   ├── Exception Tables:");
        this.exceptionTableLength = dis.readUnsignedShort();
        for (int i = 0; i < this.exceptionTableLength; i++){
            ExceptionTableEntry entry = new ExceptionTableEntry();
            entry.startPc = dis.readUnsignedShort();
            entry.endPc = dis.readUnsignedShort();
            entry.handlerPc = dis.readUnsignedShort();
            entry.catchType = dis.readUnsignedShort();
            this.exceptionTable.add(entry);
            logger.debug("  [{}] start pc: {}, end pc: {}, handler pc: {}, catch type: {}", i, entry.startPc, entry.endPc, entry.handlerPc, entry.catchType);
        }
        this.attributeCount = dis.readUnsignedShort();


        for (int i = 0; i< this.attributeCount; i++){
            logger.debug("│   │   ├── Attributes#{}:", i);
            this.attributes.add(AttributeInfo.parseAttribute(dis, cp));
        }
    }

}
