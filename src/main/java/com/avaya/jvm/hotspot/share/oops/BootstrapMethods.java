package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * BootstrapMethods corresponds to the BootstrapMethods_attribute in the JVM class file.
 * <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.7.23">
 *     The BootstrapMethods Attribute
 * </a>
 * <p>
 * The BootstrapMethods attribute is a variable-length attribute in the attributes table
 * of a ClassFile structure. It records bootstrap method specifiers that are referenced by
 * {@code invokedynamic} instructions in the class file. Each bootstrap method entry
 * specifies a bootstrap method (typically used to create dynamic call sites and lambdas)
 * and its static arguments.
 * </p>
 *
 * <p>
 * There must be exactly one BootstrapMethods attribute in the attributes table of a
 * ClassFile structure if the constant pool contains at least one
 * {@code CONSTANT_InvokeDynamic_info} entry. Otherwise, this attribute may be omitted.
 * There may be at most one BootstrapMethods attribute in a ClassFile.
 * </p>
 *
 * <p>
 * The format of the attribute is as follows:
 * </p>
 *
 * <pre>
 * BootstrapMethods_attribute {
 *     u2 attribute_name_index;
 *     u4 attribute_length;
 *     u2 num_bootstrap_methods;
 *     {
 *         u2 bootstrap_method_ref;
 *         u2 num_bootstrap_arguments;
 *         u2 bootstrap_arguments[num_bootstrap_arguments];
 *     } bootstrap_methods[num_bootstrap_methods];
 * }
 * </pre>
 */
@Data
public class BootstrapMethods extends AttributeInfo{
    private int numBootstrapMethods;
    private List<BootstrapMethods.BootstrapMethodsEntry> methodsTable = new ArrayList<>();

    @Data
    public static class BootstrapMethodsEntry {
        private int bootstrapMethodRef;
        private int numBootstrapArguments;
        private List<ConstantInfo> argumentsTable = new ArrayList<>();
    }

    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        this.setAttributeLength(dis.readInt());
        this.setNumBootstrapMethods(dis.readShort());
        for (int i = 0; i < getNumBootstrapMethods(); i++){
            BootstrapMethodsEntry entry = new BootstrapMethodsEntry();
            entry.setBootstrapMethodRef(dis.readShort());
            entry.setNumBootstrapArguments(dis.readShort());
            for (int j = 0; j < entry.getNumBootstrapArguments(); j++){
                ConstantInfo infoEntry = cp.getEntries().get(dis.readShort());
                entry.getArgumentsTable().add(infoEntry);
            }
            this.getMethodsTable().add(entry);
        }
    }
}
