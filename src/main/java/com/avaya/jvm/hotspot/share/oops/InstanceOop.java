package com.avaya.jvm.hotspot.share.oops;

import com.avaya.jvm.hotspot.share.classfile.BootClassLoader;
import com.avaya.jvm.hotspot.share.utilities.ValueType;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class InstanceOop extends OopDesc{
    // Long and Double will consume 2 FieldSlot, other types consume 1 FieldSlot
    private List<FieldSlot> fields;

    public InstanceOop(String className) throws IOException {
        this.klazz = BootClassLoader.loadKlass(className.replace('/', '.'));
        fields = new ArrayList<>();
        initialFields();
    }

    private void initialFields(){
        List<FieldInfo> fieldList = ((InstanceKlass)this.klazz).getFields();
        for (FieldInfo entry : fieldList){
            FieldSlot slot = createFieldSlot(entry);
            this.fields.add(slot);
            // Long and Double occupy 2 slots, add placeholder
            if (slot.getType() == ValueType.T_DOUBLE || slot.getType() == ValueType.T_LONG){
                this.fields.add(new FieldSlot());
            }
        }
    }

    // Create FieldSlot from FieldInfo
    private FieldSlot createFieldSlot(FieldInfo entry){
        ConstantPool constantPool = ((InstanceKlass) this.klazz).getConstantPool();
        String name = entry.resolveName(constantPool);
        ValueType type = resolveDescriptor(entry.resolveDescriptorName(constantPool));
        return new FieldSlot(name, type);
    }

    // Resolve type from descriptor string
    private ValueType resolveDescriptor(String descriptorName){
        ValueType type = ValueType.T_ILLEGAL;
        switch (descriptorName.charAt(0)){
            case 'B' -> type = ValueType.T_BYTE;
            case 'C' -> type = ValueType.T_CHAR;
            case 'D' -> type = ValueType.T_DOUBLE;
            case 'F' -> type = ValueType.T_FLOAT;
            case 'I' -> type = ValueType.T_INT;
            case 'J' -> type = ValueType.T_LONG;
            case 'S' -> type = ValueType.T_SHORT;
            case 'Z' -> type = ValueType.T_BOOLEAN;
            case 'L' -> type = ValueType.T_OBJECT;
            case '[' -> type = ValueType.T_ARRAY;
        }
        return type;
    }

    public InstanceKlass getKlass(){
        return (InstanceKlass) this.klazz;
    }
}
