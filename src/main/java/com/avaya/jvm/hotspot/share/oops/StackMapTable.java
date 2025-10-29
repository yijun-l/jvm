package com.avaya.jvm.hotspot.share.oops;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * StackMapTable corresponds to the StackMapTable_attribute in the JVM class file.
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.4">The StackMapTable Attribute</a>
 * <p>
 * The StackMapTable attribute is a variable-length attribute of the Code attribute
 * and is used during bytecode verification by type checking.
 * <p>
 * There may be at most one StackMapTable attribute in the attributes table of a
 * Code attribute. For class files with version 50.0 or above, if a method's Code
 * attribute does not include a StackMapTable attribute, the method is treated as if
 * it has an implicit empty StackMapTable (with number_of_entries equal to zero).
 * <p>
 * StackMapTable_attribute {
 *     u2                 attribute_name_index;
 *     u4                 attribute_length;
 *     u2                 number_of_entries;
 *     stack_map_frame    entries[number_of_entries];
 * }
 */

public class StackMapTable extends AttributeInfo{
    private byte[] info;

    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        this.setAttributeType(AttributeType.STACK_MAP_TABLE);
        this.setAttributeLength(dis.readInt());
        this.info = new byte[this.attributeLength];
        dis.read(this.info);
    }
}
