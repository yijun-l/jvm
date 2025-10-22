package com.avaya.jvm.hotspot.share.oops;

import com.avaya.jvm.hotspot.share.runtime.OperandStack;
import com.avaya.jvm.hotspot.share.utilities.FieldAccessFlags;
import com.avaya.jvm.hotspot.share.utilities.TmpValue;
import com.avaya.jvm.hotspot.share.utilities.ValueType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldArray {

    private List<FieldSlot> fieldArray;

    public FieldArray(InstanceKlass klass, boolean isStatic) {
        this.fieldArray = new ArrayList<>();

        List<FieldInfo> fieldList = klass.getFields();
        ConstantPool constantPool = klass.getConstantPool();
        int index = 0;

        if (fieldList == null){
//            fieldArray = null;
            return;
        }

        for (FieldInfo entry : fieldList){
            if (FieldAccessFlags.isStatic(entry.getAccessFlags()) == isStatic){
                String name = entry.resolveName(constantPool);
                ValueType type = resolveDescriptor(entry.resolveDescriptorName(constantPool));
                FieldSlot slot = klass.getStaticFields().getFieldArray().get(index);
                slot.setName(name);
                slot.setType(type);
                if (type == ValueType.T_DOUBLE || type == ValueType.T_LONG) {
                    index += 2;
                } else {
                    index++;
                }
            }
        }
    }

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

    // from fields to operand stack
    public void getValue(String name, OperandStack stack){
        for (int i = 0; i < fieldArray.size(); i++){
            FieldSlot entry = fieldArray.get(i);
            if (entry.getName().equals(name)){
                switch (entry.getType()){
                    case T_BOOLEAN, T_BYTE, T_CHAR, T_SHORT, T_INT -> stack.pushInt(entry.getNum());
                    case T_LONG -> {
                        int high = entry.getNum();
                        int low = fieldArray.get(++i).getNum();
                        stack.pushLong(((long) high << 32) | ((long) low & 0xFFFFFFFFL));
                    }
                    case T_FLOAT -> stack.pushFloat(Float.intBitsToFloat(entry.getNum()));
                    case T_DOUBLE -> {
                        int high = entry.getNum();
                        int low = fieldArray.get(++i).getNum();
                        long bits = ((long) high << 32) | ((long) low & 0xFFFFFFFFL);
                        stack.pushDouble(Double.longBitsToDouble(bits));
                    }
                    case T_OBJECT, T_ARRAY -> stack.pushRef(entry.getRef());
                }
            }
        }
    }

    // from operand stack to fields
    public void setValue(String name, ValueType type, OperandStack stack){
        boolean notFound = true;
        for (int i = 0; i < fieldArray.size(); i++){
            FieldSlot entry = fieldArray.get(i);
            if (entry.getName().equals(name)){
                notFound = false;
                switch (entry.getType()){
                    case T_BOOLEAN, T_BYTE, T_CHAR, T_SHORT, T_INT -> entry.setNum(stack.popInt());
                    case T_LONG -> {
                        long num = stack.popLong();
                        entry.setNum((int) (num & 0xFFFFFFFFL));
                        fieldArray.get(++i).setNum((int) (num >>> 32));
                    }
                    case T_FLOAT -> {
                        float num = stack.popFloat();
                       entry.setNum(Float.floatToRawIntBits(num));
                    }
                    case T_DOUBLE -> {
                        long bits = Double.doubleToLongBits(stack.popDouble());
                        entry.setNum((int) (bits & 0xFFFFFFFFL));
                        fieldArray.get(++i).setNum((int) (bits >>> 32));
                    }
                    case T_OBJECT, T_ARRAY -> entry.setRef(stack.popRef());
                }
            }
        }
        // add a new field to the Oop
        if (notFound){
            FieldSlot entry = new FieldSlot(name, type);
            fieldArray.add(entry);
            switch (type){
                case T_BOOLEAN, T_BYTE, T_CHAR, T_SHORT, T_INT -> entry.setNum(stack.popInt());
                case T_LONG -> {
                    long num = stack.popLong();
                    entry.setNum((int) (num & 0xFFFFFFFFL));
                    FieldSlot tmpEntry = new FieldSlot();
                    tmpEntry.setNum((int) (num >>> 32));
                    fieldArray.add(tmpEntry);
                }
                case T_FLOAT -> {
                    float num = stack.popFloat();
                    entry.setNum(Float.floatToRawIntBits(num));
                }
                case T_DOUBLE -> {
                    long bits = Double.doubleToLongBits(stack.popDouble());
                    entry.setNum((int) (bits & 0xFFFFFFFFL));
                    FieldSlot tmpEntry = new FieldSlot();
                    tmpEntry.setNum((int) (bits >>> 32));
                    fieldArray.add(tmpEntry);
                }
                case T_OBJECT, T_ARRAY -> entry.setRef(stack.popRef());
            }

        }
    }

    public static void oopSetValue(String name, ValueType type, OperandStack stack){
        TmpValue tmp = new TmpValue();
        switch (type){
            case T_BOOLEAN, T_BYTE, T_CHAR, T_SHORT, T_INT -> tmp.setI(stack.popInt());
            case T_LONG -> tmp.setJ(stack.popLong());
            case T_FLOAT -> tmp.setF(stack.popFloat());
            case T_DOUBLE -> tmp.setD(stack.popDouble());
            case T_OBJECT, T_ARRAY -> tmp.setL(stack.popRef());
        }
        InstanceOop oop = (InstanceOop) stack.popRef();
        switch (type){
            case T_BOOLEAN, T_BYTE, T_CHAR, T_SHORT, T_INT -> stack.pushInt(tmp.getI());
            case T_LONG -> stack.pushLong(tmp.getJ());
            case T_FLOAT -> stack.pushFloat(tmp.getF());
            case T_DOUBLE -> stack.pushDouble(tmp.getD());
            case T_OBJECT, T_ARRAY -> stack.pushRef(tmp.getL());
        }

        oop.getOopFields().setValue(name, type, stack);
    }
}
