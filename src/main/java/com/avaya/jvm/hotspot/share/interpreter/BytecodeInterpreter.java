package com.avaya.jvm.hotspot.share.interpreter;

import com.avaya.jvm.hotspot.share.classfile.BootClassLoader;
import com.avaya.jvm.hotspot.share.oops.*;
import com.avaya.jvm.hotspot.share.runtime.JavaThread;
import com.avaya.jvm.hotspot.share.runtime.JavaVFrame;
import com.avaya.jvm.hotspot.share.utilities.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static com.avaya.jvm.hotspot.share.prims.JavaNativeInterface.*;

/*
 * See details in:
 * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-6.html
 */
public class BytecodeInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(BytecodeInterpreter.class);

    public static void run(JavaThread thread, BytecodeStream bytecodeStream) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException, InstantiationException {
        JavaVFrame frame = (JavaVFrame) thread.getStack().peek();
        ConstantPool constantPool = bytecodeStream.getKlass().getConstantPool();
        bytecodeStream.resetIndex();
        boolean isWide = false;
        while (!bytecodeStream.end()){
            switch (Bytecodes.fromOpcode(bytecodeStream.getU1())){
                // 0, do nothing
                case NOP -> {
                    logger.debug("NOP >> ");
                }
                // 1, push null onto the operand stack
                case ACONST_NULL -> {
                    logger.debug("ACONST_NULL >> ");
                    frame.getOperandStack().pushRef(null);
                }
                // 2, push constant -1 onto the operand stack
                case ICONST_M1 -> {
                    logger.debug("ICONST_M1 >> ");
                    frame.getOperandStack().pushInt(-1);
                }
                // 3, push constant 0 onto the operand stack
                case ICONST_0 -> {
                    logger.debug("ICONST_0 >> ");
                    frame.getOperandStack().pushInt(0);
                }
                // 4
                case ICONST_1 -> {
                    logger.debug("ICONST_1 >> ");
                    frame.getOperandStack().pushInt(1);
                }
                // 5
                case ICONST_2 -> {
                    logger.debug("ICONST_2 >> ");
                    frame.getOperandStack().pushInt(2);
                }
                // 6
                case ICONST_3 -> {
                    logger.debug("ICONST_3 >> ");
                    frame.getOperandStack().pushInt(3);
                }
                // 7
                case ICONST_4 -> {
                    logger.debug("ICONST_4 >> ");
                    frame.getOperandStack().pushInt(4);
                }
                // 8
                case ICONST_5 -> {
                    logger.debug("ICONST_5 >> ");
                    frame.getOperandStack().pushInt(5);
                }
                // 9, push long constant 0 onto the operand stack
                case LCONST_0 -> {
                    logger.debug("LCONST_0 >> ");
                    frame.getOperandStack().pushLong(0);
                }
                // 10
                case LCONST_1 -> {
                    logger.debug("LCONST_1 >> ");
                    frame.getOperandStack().pushLong(1);
                }
                // 11, push float constant 0.0f onto the operand stack
                case FCONST_0 -> {
                    logger.debug("FCONST_0 >> ");
                    frame.getOperandStack().pushFloat(0.0f);
                }
                // 12
                case FCONST_1 -> {
                    logger.debug("FCONST_1 >> ");
                    frame.getOperandStack().pushFloat(1.0f);
                }
                // 13
                case FCONST_2 -> {
                    logger.debug("FCONST_2 >> ");
                    frame.getOperandStack().pushFloat(2.0f);
                }
                // 14, push double constant 0.0 onto the operand stack
                case DCONST_0 -> {
                    logger.debug("DCONST_0 >> ");
                    frame.getOperandStack().pushDouble(0.0);
                }
                // 15
                case DCONST_1 -> {
                    logger.debug("DCONST_1 >> ");
                    frame.getOperandStack().pushDouble(1.0);
                }
                // 16, push a byte constant (from next byte in bytecode) onto the operand stack
                case BIPUSH -> {
                    logger.debug("BIPUSH >> ");
                    byte value = (byte)bytecodeStream.getU1();
                    frame.getOperandStack().pushInt(value);
                }
                // 16, push a short constant (from next byte in bytecode) onto the operand stack
                case SIPUSH -> {
                    logger.debug("SIPUSH >> ");
                    int high = bytecodeStream.getU1();
                    int low = bytecodeStream.getU1();
                    short value = (short)((high << 8) | (low & 0xFF));
                    frame.getOperandStack().pushInt(value);
                }
                // 18
                case LDC -> {
                    logger.debug("LDC >> ");
                    ConstantInfo constantEntry = constantPool.getEntries().get(bytecodeStream.getU1());
                    switch (constantEntry.getTag()){
                        case JVM_CONSTANT_INTEGER -> {
                            int intValue = ((ConstantIntegerInfo)constantEntry).getValue();
                            frame.getOperandStack().pushInt(intValue);
                        }
                        case JVM_CONSTANT_FLOAT -> {
                            float floatValue = ((ConstantFloatInfo)constantEntry).getValue();
                            frame.getOperandStack().pushFloat(floatValue);
                        }
                        case JVM_CONSTANT_STRING -> {
                            String stringValue = ((ConstantStringInfo)constantEntry).resolveString(constantPool);
                            frame.getOperandStack().pushRef(stringValue);
                        }
                        default -> {
                            //TODO: other types for LDC
                            logger.debug("Other type in LDC.");
                        }
                    }
                }
                // 19, load 1-slot type from constant pool (index specified by next 2 bytes) onto stack
                case LDC_W -> {
                    logger.debug("LDC2_W >> ");
                    ConstantInfo constantEntry = constantPool.getEntries().get(bytecodeStream.getU2());
                    switch (constantEntry.getTag()){
                        case JVM_CONSTANT_INTEGER -> {
                            int iValue = ((ConstantIntegerInfo)constantEntry).getValue();
                            frame.getOperandStack().pushInt(iValue);
                        }
                        case JVM_CONSTANT_FLOAT -> {
                            float fValue = ((ConstantFloatInfo)constantEntry).getValue();
                            frame.getOperandStack().pushFloat(fValue);
                        }
                        case JVM_CONSTANT_STRING -> {
                            String sValue = ((ConstantStringInfo)constantEntry).resolveString(constantPool);
                            frame.getOperandStack().pushRef(sValue);
                        }
                        default -> {
                            //TODO: other types for LDC_W
                            logger.debug("Other type in LDC_W.");
                        }
                    }
                }
                // 20, load long or double from constant pool (index specified by next 2 bytes) onto stack
                case LDC2_W -> {
                    logger.debug("LDC2_W >> ");
                    ConstantInfo constantEntry = constantPool.getEntries().get(bytecodeStream.getU2());
                    switch (constantEntry.getTag()){
                        case JVM_CONSTANT_LONG -> {
                            long longValue = ((ConstantLongInfo)constantEntry).getValue();
                            frame.getOperandStack().pushLong(longValue);
                        }
                        case JVM_CONSTANT_DOUBLE -> {
                            double doubleValue = ((ConstantDoubleInfo)constantEntry).getValue();
                            frame.getOperandStack().pushDouble(doubleValue);
                        }
                        default -> {
                            logger.debug("Other type in LDC2_W.");
                        }
                    }
                }
                // 21, load int from local variable (index specified by next byte) onto stack
                case ILOAD -> {
                    logger.debug("ILOAD >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(index));
                }
                // 22, load long from local variable (index specified by next byte) onto stack
                case LLOAD -> {
                    logger.debug("LLOAD >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getOperandStack().pushLong(frame.getLocals().getLong(index));
                }
                // 23, load float from local variable (index specified by next byte) onto stack
                case FLOAD -> {
                    logger.debug("FLOAD >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getOperandStack().pushFloat(frame.getLocals().getFloat(index));
                }
                // 24, load double from local variable (index specified by next byte) onto stack
                case DLOAD -> {
                    logger.debug("DLOAD >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getOperandStack().pushDouble(frame.getLocals().getDouble(index));
                }
                // 25, load array from local variable (index specified by next byte) onto stack
                case ALOAD -> {
                    logger.debug("ALOAD >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getOperandStack().pushRef(frame.getLocals().getRef(index));
                }
                // 26, load int from local variable 0 onto stack
                case ILOAD_0 -> {
                    logger.debug("ILOAD_0 >> ");
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(0));
                }
                // 27
                case ILOAD_1 -> {
                    logger.debug("ILOAD_1 >> ");
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(1));
                }
                // 28
                case ILOAD_2 -> {
                    logger.debug("ILOAD_2 >> ");
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(2));
                }
                // 29
                case ILOAD_3 -> {
                    logger.debug("ILOAD_3 >> ");
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(3));
                }
                // 30, load long from local variable 0 onto stack
                case LLOAD_0 -> {
                    logger.debug("LLOAD_0 >> ");
                    frame.getOperandStack().pushLong(frame.getLocals().getLong(0));
                }
                // 31
                case LLOAD_1 -> {
                    logger.debug("LLOAD_1 >> ");
                    frame.getOperandStack().pushLong(frame.getLocals().getLong(1));
                }
                // 32
                case LLOAD_2 -> {
                    logger.debug("LLOAD_2 >> ");
                    frame.getOperandStack().pushLong(frame.getLocals().getLong(2));
                }
                // 33
                case LLOAD_3 -> {
                    logger.debug("LLOAD_3 >> ");
                    frame.getOperandStack().pushLong(frame.getLocals().getLong(3));
                }
                // 34, load float from local variable 0 onto stack
                case FLOAD_0 -> {
                    logger.debug("FLOAD_0 >> ");
                    frame.getOperandStack().pushFloat(frame.getLocals().getFloat(0));
                }
                // 35
                case FLOAD_1 -> {
                    logger.debug("FLOAD_1 >> ");
                    frame.getOperandStack().pushFloat(frame.getLocals().getFloat(1));
                }
                // 36
                case FLOAD_2 -> {
                    logger.debug("FLOAD_2 >> ");
                    frame.getOperandStack().pushFloat(frame.getLocals().getFloat(2));
                }
                // 37
                case FLOAD_3 -> {
                    logger.debug("FLOAD_3 >> ");
                    frame.getOperandStack().pushFloat(frame.getLocals().getFloat(3));
                }
                // 38, load double from local variable 0 onto stack
                case DLOAD_0 -> {
                    logger.debug("DLOAD_0 >> ");
                    frame.getOperandStack().pushDouble(frame.getLocals().getDouble(0));
                }
                // 39
                case DLOAD_1 -> {
                    logger.debug("DLOAD_1 >> ");
                    frame.getOperandStack().pushDouble(frame.getLocals().getDouble(1));
                }
                // 40
                case DLOAD_2 -> {
                    logger.debug("DLOAD_2 >> ");
                    frame.getOperandStack().pushDouble(frame.getLocals().getDouble(2));
                }
                // 41
                case DLOAD_3 -> {
                    logger.debug("DLOAD_3 >> ");
                    frame.getOperandStack().pushDouble(frame.getLocals().getDouble(3));
                }
                // 42
                case ALOAD_0 -> {
                    logger.debug("ALOAD_0 >> ");
                    frame.getOperandStack().pushRef(frame.getLocals().getRef(0));
                }
                // 43
                case ALOAD_1 -> {
                    logger.debug("ALOAD_1 >> ");
                    frame.getOperandStack().pushRef(frame.getLocals().getRef(1));
                }
                // 44
                case ALOAD_2 -> {
                    logger.debug("ALOAD_2 >> ");
                    frame.getOperandStack().pushRef(frame.getLocals().getRef(2));
                }
                // 45
                case ALOAD_3 -> {
                    logger.debug("ALOAD_3 >> ");
                    frame.getOperandStack().pushRef(frame.getLocals().getRef(3));
                }
                // 46
                case IALOAD -> {
                    logger.debug("IALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    IntArrayOop array = (IntArrayOop)frame.getOperandStack().popRef();
                    int value = array.get(index);
                    frame.getOperandStack().pushInt(value);
                }
                // 47
                case LALOAD -> {
                    logger.debug("LALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    LongArrayOop array = (LongArrayOop)frame.getOperandStack().popRef();
                    long value = array.get(index);
                    frame.getOperandStack().pushLong(value);
                }
                // 48
                case FALOAD -> {
                    logger.debug("FALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    FloatArrayOop array = (FloatArrayOop)frame.getOperandStack().popRef();
                    float value = array.get(index);
                    frame.getOperandStack().pushFloat(value);
                }
                // 49, DALOAD
                case DALOAD -> {
                    logger.debug("DALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    DoubleArrayOop array = (DoubleArrayOop)frame.getOperandStack().popRef();
                    double value = array.get(index);
                    frame.getOperandStack().pushDouble(value);
                }
                // 50, AALOAD
                case AALOAD -> {
                    logger.debug("AALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    Object array = frame.getOperandStack().popRef();
                    Object value;
                    if (array instanceof ObjectArrayOop){
                        value = ((ObjectArrayOop)array).get(index);
                    } else{
                        value = Array.get(array, index);
                    }
                    frame.getOperandStack().pushRef(value);
                }
                // 51
                case BALOAD -> {
                    logger.debug("BALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    ByteArrayOop array = (ByteArrayOop)frame.getOperandStack().popRef();
                    byte value = array.get(index);
                    frame.getOperandStack().pushInt(value);
                }
                // 52
                case CALOAD -> {
                    logger.debug("CALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    CharArrayOop array = (CharArrayOop)frame.getOperandStack().popRef();
                    char value = array.get(index);
                    frame.getOperandStack().pushInt(value);
                }
                // 53
                case SALOAD -> {
                    logger.debug("SALOAD >> ");
                    int index = frame.getOperandStack().popInt();
                    ShortArrayOop array = (ShortArrayOop)frame.getOperandStack().popRef();
                    short value = array.get(index);
                    frame.getOperandStack().pushInt(value);
                }
                // 54, store int from stack into local variable (index specified by next byte)
                case ISTORE -> {
                    logger.debug("ISTORE >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getLocals().setInt(index, frame.getOperandStack().popInt());
                }
                // 55, store long from stack into local variable (index specified by next byte)
                case LSTORE -> {
                    logger.debug("LSTORE >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getLocals().setLong(index, frame.getOperandStack().popLong());
                }
                // 56, store float from stack into local variable (index specified by next byte)
                case FSTORE -> {
                    logger.debug("FSTORE >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getLocals().setFloat(index, frame.getOperandStack().popFloat());
                }
                // 57, store double from stack into local variable (index specified by next byte)
                case DSTORE -> {
                    logger.debug("DSTORE >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getLocals().setDouble(index, frame.getOperandStack().popDouble());
                }
                // 58, store array from stack into local variable (index specified by next byte)
                case ASTORE -> {
                    logger.debug("ASTORE >> ");
                    int index;
                    if (isWide){
                        index = bytecodeStream.getU2();
                        isWide = false;
                    } else {
                        index = bytecodeStream.getU1();
                    }
                    frame.getLocals().setRef(index, frame.getOperandStack().popRef());
                }
                // 59, store int from stack into local variable 0
                case ISTORE_0 -> {
                    logger.debug("ISTORE_0 >> ");
                    frame.getLocals().setInt(0, frame.getOperandStack().popInt());
                }
                // 60
                case ISTORE_1 -> {
                    logger.debug("ISTORE_1 >> ");
                    frame.getLocals().setInt(1, frame.getOperandStack().popInt());
                }
                // 61
                case ISTORE_2 -> {
                    logger.debug("ISTORE_2 >> ");
                    frame.getLocals().setInt(2, frame.getOperandStack().popInt());
                }
                // 62
                case ISTORE_3 -> {
                    logger.debug("ISTORE_3 >> ");
                    frame.getLocals().setInt(3, frame.getOperandStack().popInt());
                }
                // 63, store long from stack into local variable 0
                case LSTORE_0 -> {
                    logger.debug("LSTORE_0 >> ");
                    frame.getLocals().setLong(0, frame.getOperandStack().popLong());
                }
                // 64
                case LSTORE_1 -> {
                    logger.debug("LSTORE_1 >> ");
                    frame.getLocals().setLong(1, frame.getOperandStack().popLong());
                }
                // 65
                case LSTORE_2 -> {
                    logger.debug("LSTORE_2 >> ");
                    frame.getLocals().setLong(2, frame.getOperandStack().popLong());
                }
                // 66
                case LSTORE_3 -> {
                    logger.debug("LSTORE_3 >> ");
                    frame.getLocals().setLong(3, frame.getOperandStack().popLong());
                }
                // 67, store float from stack into local variable 0
                case FSTORE_0 -> {
                    logger.debug("FSTORE_0 >> ");
                    frame.getLocals().setFloat(0, frame.getOperandStack().popFloat());
                }
                // 68
                case FSTORE_1 -> {
                    logger.debug("FSTORE_1 >> ");
                    frame.getLocals().setFloat(1, frame.getOperandStack().popFloat());
                }
                // 69
                case FSTORE_2 -> {
                    logger.debug("FSTORE_2 >> ");
                    frame.getLocals().setFloat(2, frame.getOperandStack().popFloat());
                }
                // 70
                case FSTORE_3 -> {
                    logger.debug("FSTORE_3 >> ");
                    frame.getLocals().setFloat(3, frame.getOperandStack().popFloat());
                }
                // 71, store double from stack into local variable 0
                case DSTORE_0 -> {
                    logger.debug("DSTORE_0 >> ");
                    frame.getLocals().setDouble(0, frame.getOperandStack().popDouble());
                }
                // 72
                case DSTORE_1 -> {
                    logger.debug("DSTORE_1 >> ");
                    frame.getLocals().setDouble(1, frame.getOperandStack().popDouble());
                }
                // 73
                case DSTORE_2 -> {
                    logger.debug("DSTORE_2 >> ");
                    frame.getLocals().setDouble(2, frame.getOperandStack().popDouble());
                }
                // 74
                case DSTORE_3 -> {
                    logger.debug("DSTORE_3 >> ");
                    frame.getLocals().setDouble(3, frame.getOperandStack().popDouble());
                }
                // 75
                case ASTORE_0 -> {
                    logger.debug("ASTORE_0 >> ");
                    frame.getLocals().setRef(0, frame.getOperandStack().popRef());
                }
                // 76
                case ASTORE_1 -> {
                    logger.debug("ASTORE_1 >> ");
                    frame.getLocals().setRef(1, frame.getOperandStack().popRef());
                }
                // 77
                case ASTORE_2 -> {
                    logger.debug("ASTORE_2 >> ");
                    frame.getLocals().setRef(2, frame.getOperandStack().popRef());
                }
                // 78
                case ASTORE_3 -> {
                    logger.debug("ASTORE_3 >> ");
                    frame.getLocals().setRef(3, frame.getOperandStack().popRef());
                }
                // 79
                case IASTORE -> {
                    logger.debug("IASTORE >> ");
                    int value = frame.getOperandStack().popInt();
                    int index = frame.getOperandStack().popInt();
                    IntArrayOop array = (IntArrayOop)frame.getOperandStack().popRef();
                    array.set(index, value);
                }
                // 80
                case LASTORE -> {
                    logger.debug("LASTORE >> ");
                    long value = frame.getOperandStack().popLong();
                    int index = frame.getOperandStack().popInt();
                    LongArrayOop array = (LongArrayOop)frame.getOperandStack().popRef();
                    array.set(index, value);
                }
                // 81
                case FASTORE -> {
                    logger.debug("FASTORE >> ");
                    float value = frame.getOperandStack().popFloat();
                    int index = frame.getOperandStack().popInt();
                    FloatArrayOop array = (FloatArrayOop)frame.getOperandStack().popRef();
                    array.set(index, value);
                }
                // 82
                case DASTORE -> {
                    logger.debug("DASTORE >> ");
                    double value = frame.getOperandStack().popDouble();
                    int index = frame.getOperandStack().popInt();
                    DoubleArrayOop array = (DoubleArrayOop)frame.getOperandStack().popRef();
                    array.set(index, value);
                }
                // 83
                case AASTORE -> {
                    logger.debug("AASTORE >> ");
                    Object value = frame.getOperandStack().popRef();
                    int index = frame.getOperandStack().popInt();
                    Object array = frame.getOperandStack().popRef();
                    if (value instanceof InstanceOop && array instanceof ObjectArrayOop){
                        ((ObjectArrayOop)array).set(index, (InstanceOop)value);
                    } else {
                        Array.set(array, index, value);
                    }
                }
                // 84
                case BASTORE -> {
                    logger.debug("BASTORE >> ");
                    int value = frame.getOperandStack().popInt();
                    int index = frame.getOperandStack().popInt();
                    ByteArrayOop array = (ByteArrayOop)frame.getOperandStack().popRef();
                    array.set(index, (byte)value);
                }
                // 85
                case CASTORE -> {
                    logger.debug("CASTORE >> ");
                    int value = frame.getOperandStack().popInt();
                    int index = frame.getOperandStack().popInt();
                    CharArrayOop array = (CharArrayOop)frame.getOperandStack().popRef();
                    array.set(index, (char)value);
                }
                // 86
                case SASTORE -> {
                    logger.debug("SASTORE >> ");
                    int value = frame.getOperandStack().popInt();
                    int index = frame.getOperandStack().popInt();
                    ShortArrayOop array = (ShortArrayOop)frame.getOperandStack().popRef();
                    array.set(index, (short)value);
                }
                // 87
                case POP -> {
                    logger.debug("POP >> ");
                    frame.getOperandStack().pop();
                }
                // 88
                case POP2 -> {
                    logger.debug("POP2 >> ");
                    frame.getOperandStack().pop2();
                }
                // 89
                case DUP -> {
                    logger.debug("DUP >> ");
                    frame.getOperandStack().dupSlotsAcrossElements(1, 0);
                }
                // 90
                case DUP_X1 -> {
                    logger.debug("DUP_X1 >> ");
                    frame.getOperandStack().dupSlotsAcrossElements(1, 1);
                }
                // 91
                case DUP_X2 -> {
                    logger.debug("DUP_X2 >> ");
                    frame.getOperandStack().dupSlotsAcrossElements(1, 2);
                }
                // 92
                case DUP2 -> {
                    logger.debug("DUP2 >> ");
                    frame.getOperandStack().dupSlotsAcrossElements(2, 1);
                }
                // 93
                case DUP2_X1 -> {
                    logger.debug("DUP2_X1 >> ");
                    frame.getOperandStack().dupSlotsAcrossElements(2, 1);
                }
                // 94
                case DUP2_X2 -> {
                    logger.debug("DUP2_X2 >> ");
                    frame.getOperandStack().dupSlotsAcrossElements(2, 2);
                }
                // 95,
                case SWAP -> {
                    logger.debug("SWAP >> ");
                    frame.getOperandStack().swap();
                }
                // 96, Add: Pop valueB, pop valueA, push (valueA + valueB)
                case IADD -> {
                    logger.debug("IADD >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(valueA + valueB);
                }
                // 97
                case LADD -> {
                    logger.debug("LADD >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(valueA + valueB);
                }
                // 98
                case FADD -> {
                    logger.debug("FADD >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushFloat(valueA + valueB);
                }
                // 99
                case DADD -> {
                    logger.debug("DADD >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushDouble(valueA + valueB);
                }
                // 100, Subtract: Pop valueB, pop valueA, push (valueA - valueB)
                case ISUB -> {
                    logger.debug("ISUB >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(valueA - valueB);
                }
                // 101
                case LSUB -> {
                    logger.debug("LSUB >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(valueA - valueB);
                }
                // 102
                case FSUB -> {
                    logger.debug("FSUB >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushFloat(valueA - valueB);
                }
                // 103
                case DSUB -> {
                    logger.debug("DSUB >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushDouble(valueA - valueB);
                }
                // 104, Multiply: Pop valueB, pop valueA, push (valueA * valueB)
                case IMUL -> {
                    logger.debug("IMUL >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(valueA * valueB);
                }
                // 105
                case LMUL -> {
                    logger.debug("LMUL >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(valueA * valueB);
                }
                // 106
                case FMUL -> {
                    logger.debug("FMUL >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushFloat(valueA * valueB);
                }
                // 107
                case DMUL -> {
                    logger.debug("DMUL >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushDouble(valueA * valueB);
                }
                // 108, Divide: Pop valueB, pop valueA, push (valueA / valueB)
                case IDIV -> {
                    logger.debug("IDIV >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueB == 0) throw new ArithmeticException("/ by zero");
                    frame.getOperandStack().pushInt(valueA / valueB);
                }
                // 109
                case LDIV -> {
                    logger.debug("LDIV >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    if (valueB == 0) throw new ArithmeticException("/ by zero");
                    frame.getOperandStack().pushLong(valueA / valueB);
                }
                // 110
                case FDIV -> {
                    logger.debug("FDIV >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    if (valueB == 0) throw new ArithmeticException("/ by zero");
                    frame.getOperandStack().pushFloat(valueA / valueB);
                }
                // 111
                case DDIV -> {
                    logger.debug("DDIV >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    if (valueB == 0) throw new ArithmeticException("/ by zero");
                    frame.getOperandStack().pushDouble(valueA / valueB);
                }
                // 112, Remainder: Pop valueB, pop valueA, push (valueA % valueB)
                case IREM -> {
                    logger.debug("IREM >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueB == 0) throw new ArithmeticException("% by zero");
                    frame.getOperandStack().pushInt(valueA % valueB);
                }
                // 113
                case LREM -> {
                    logger.debug("LREM >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    if (valueB == 0) throw new ArithmeticException("% by zero");
                    frame.getOperandStack().pushLong(valueA % valueB);
                }
                // 114
                case FREM -> {
                    logger.debug("FREM >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    if (valueB == 0) throw new ArithmeticException("% by zero");
                    frame.getOperandStack().pushFloat(valueA % valueB);
                }
                // 115
                case DREM -> {
                    logger.debug("DREM >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    if (valueB == 0) throw new ArithmeticException("% by zero");
                    frame.getOperandStack().pushDouble(valueA % valueB);
                }
                // 116, Negate: Pop valueA, push (-valueA)
                case INEG -> {
                    logger.debug("INEG >> ");
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(-valueA);
                }
                // 117
                case LNEG -> {
                    logger.debug("LNEG >> ");
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(-valueA);
                }
                // 118
                case FNEG -> {
                    logger.debug("FNEG >> ");
                    float valueA = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushFloat(-valueA);
                }
                // 119
                case DNEG -> {
                    logger.debug("DNEG >> ");
                    double valueA = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushDouble(-valueA);
                }
                // 120
                case ISHL -> {
                    logger.debug("ISHL >> ");
                    int shift = frame.getOperandStack().popInt();
                    int value = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(value << (shift & 0x1F));
                }
                // 121
                case LSHL -> {
                    logger.debug("LSHL >> ");
                    int shift = frame.getOperandStack().popInt();
                    long value = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(value << (shift & 0x3F));
                }
                // 122
                case ISHR -> {
                    logger.debug("ISHR >> ");
                    int shift = frame.getOperandStack().popInt();
                    int value = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(value >> (shift & 0x1F));
                }
                // 123
                case LSHR -> {
                    logger.debug("LSHR >> ");
                    int shift = frame.getOperandStack().popInt();
                    long value = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(value >> (shift & 0x3F));
                }
                // 124
                case IUSHR -> {
                    logger.debug("IUSHR >> ");
                    int shift = frame.getOperandStack().popInt();
                    int value = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(value >>> (shift & 0x1F));
                }
                // 125
                case LUSHR -> {
                    logger.debug("LUSHR >> ");
                    int shift = frame.getOperandStack().popInt();
                    long value = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(value >>> (shift & 0x3F));
                }
                // 126
                case IAND -> {
                    logger.debug("IAND >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(valueA & valueB);
                }
                // 127
                case LAND -> {
                    logger.debug("LAND >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(valueA & valueB);
                }
                // 128
                case IOR -> {
                    logger.debug("IOR >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(valueA | valueB);
                }
                // 129
                case LOR -> {
                    logger.debug("LOR >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(valueA | valueB);
                }
                // 130
                case IXOR -> {
                    logger.debug("IXOR >> ");
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt(valueA ^ valueB);
                }
                // 131
                case LXOR -> {
                    logger.debug("LXOR >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushLong(valueA ^ valueB);
                }
                // 132, read local index (1 byte) and signed increment (1 byte), add to variable, store back.
                case IINC -> {
                    logger.debug("IINC >> ");
                    int index;
                    int increment;
                    if (isWide){
                        // wide: index (2 bytes), increment (2 bytes, signed)
                        index = bytecodeStream.getU2();
                        increment = (short) bytecodeStream.getU2();
                        // reset value after use
                        isWide = false;
                    } else {
                        // normal: index (1 byte), increment (1 byte, signed)
                        index = bytecodeStream.getU1();
                        increment = (byte) bytecodeStream.getU1();
                    }
                    frame.getLocals().setInt(index, frame.getLocals().getInt(index) + increment);
                }
                // 133, int to long
                case I2L -> {
                    logger.debug("I2L >> ");
                    int intValue  = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushLong((long)intValue);
                }
                // 134, int to float
                case I2F -> {
                    logger.debug("I2F >> ");
                    int intValue  = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushFloat((float)intValue);
                }
                // 135, int to double
                case I2D -> {
                    logger.debug("I2D >> ");
                    int intValue  = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushDouble((double)intValue);
                }
                // 136, long to int
                case L2I -> {
                    logger.debug("L2I >> ");
                    long longValue  = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushInt((int)longValue);
                }
                // 137, long to float
                case L2F -> {
                    logger.debug("L2F >> ");
                    long longValue  = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushFloat((float)longValue);
                }
                // 138, long to double
                case L2D -> {
                    logger.debug("L2D >> ");
                    long longValue  = frame.getOperandStack().popLong();
                    frame.getOperandStack().pushDouble((double)longValue);
                }
                // 139, float to int
                case F2I -> {
                    logger.debug("F2I >> ");
                    float floatValue  = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushInt((int)floatValue);
                }
                // 140, float to long
                case F2L -> {
                    logger.debug("F2L >> ");
                    float floatValue  = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushLong((long)floatValue);
                }
                // 141, float to double
                case F2D -> {
                    logger.debug("F2D >> ");
                    float floatValue  = frame.getOperandStack().popFloat();
                    frame.getOperandStack().pushDouble((double)floatValue);
                }
                // 142, double to int
                case D2I -> {
                    logger.debug("D2I >> ");
                    double doubleValue  = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushInt((int)doubleValue);
                }
                // 143, double to long
                case D2L -> {
                    logger.debug("D2L >> ");
                    double doubleValue  = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushLong((long)doubleValue);
                }
                // 144, double to float
                case D2F -> {
                    logger.debug("D2F >> ");
                    double doubleValue  = frame.getOperandStack().popDouble();
                    frame.getOperandStack().pushFloat((float)doubleValue);
                }
                // 145, int to byte (signed, with sign extension)
                case I2B -> {
                    logger.debug("I2B >> ");
                    int intValue  = frame.getOperandStack().popInt();
                    intValue  = (intValue  << 24) >> 24;
                    frame.getOperandStack().pushInt(intValue);
                }
                // 146, int to char (unsigned, high 16 bits cleared)
                case I2C -> {
                    logger.debug("I2C >> ");
                    int intValue  = frame.getOperandStack().popInt();
                    intValue  = intValue  & 0xFFFF;
                    frame.getOperandStack().pushInt(intValue);
                }
                // 147, int to short (unsigned, high 16 bits cleared)
                case I2S -> {
                    logger.debug("I2S >> ");
                    int intValue  = frame.getOperandStack().popInt();
                    frame.getOperandStack().pushInt((short) intValue);
                }
                // 148, compare two long
                case LCMP -> {
                    logger.debug("LCMP >> ");
                    long valueB = frame.getOperandStack().popLong();
                    long valueA = frame.getOperandStack().popLong();
                    int result;
                    if (valueA > valueB) {
                        result = 1;
                    } else if (valueA == valueB) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                    frame.getOperandStack().pushInt(result);
                }
                // 149, compare two float
                case FCMPL -> {
                    logger.debug("FCMPL >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    int result;
                    if (Float.isNaN(valueA) || Float.isNaN(valueB)) {
                        result = -1;
                    }else if (valueA > valueB) {
                        result = 1;
                    } else if (valueA == valueB) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                    frame.getOperandStack().pushInt(result);
                }
                // 150
                case FCMPG -> {
                    logger.debug("FCMPG >> ");
                    float valueB = frame.getOperandStack().popFloat();
                    float valueA = frame.getOperandStack().popFloat();
                    int result;
                    if (Float.isNaN(valueA) || Float.isNaN(valueB)) {
                        result = 1;
                    }else if (valueA > valueB) {
                        result = 1;
                    } else if (valueA == valueB) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                    frame.getOperandStack().pushInt(result);
                }
                // 151, compare two double
                case DCMPL -> {
                    logger.debug("DCMPL >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    int result;
                    if (Double.isNaN(valueA) || Double.isNaN(valueB)) {
                        result = -1;
                    }else if (valueA > valueB) {
                        result = 1;
                    } else if (valueA == valueB) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                    frame.getOperandStack().pushInt(result);
                }
                // 152,
                case DCMPG -> {
                    logger.debug("DCMPG >> ");
                    double valueB = frame.getOperandStack().popDouble();
                    double valueA = frame.getOperandStack().popDouble();
                    int result;
                    if (Double.isNaN(valueA) || Double.isNaN(valueB)) {
                        result = 1;
                    }else if (valueA > valueB) {
                        result = 1;
                    } else if (valueA == valueB) {
                        result = 0;
                    } else {
                        result = -1;
                    }
                    frame.getOperandStack().pushInt(result);
                }
                // 153,
                case IFEQ -> {
                    logger.debug("IFEQ >> ");
                    int offset = bytecodeStream.getU2();
                    int value = frame.getOperandStack().popInt();
                    if (value == 0){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 154,
                case IFNE -> {
                    logger.debug("IFNE >> ");
                    int offset = bytecodeStream.getU2();
                    int value = frame.getOperandStack().popInt();
                    if (value != 0){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 155,
                case IFLT -> {
                    logger.debug("IFLT >> ");
                    int offset = bytecodeStream.getU2();
                    int value = frame.getOperandStack().popInt();
                    if (value == -1){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 156,
                case IFGE -> {
                    logger.debug("IFGE >> ");
                    int offset = bytecodeStream.getU2();
                    int value = frame.getOperandStack().popInt();
                    if (value != -1){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 157
                case IFGT -> {
                    logger.debug("IFGT >> ");
                    int offset = bytecodeStream.getU2();
                    int value = frame.getOperandStack().popInt();
                    if (value == 1){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 158,
                case IFLE -> {
                    logger.debug("IFLE >> ");
                    int offset = bytecodeStream.getU2();
                    int value = frame.getOperandStack().popInt();
                    if (value != 1){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 159,
                case IF_ICMPEQ -> {
                    logger.debug("IF_ICMPEQ >> ");
                    int offset = bytecodeStream.getU2();
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueA == valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 160,
                case IF_ICMPNE -> {
                    logger.debug("IF_ICMPNE >> ");
                    int offset = bytecodeStream.getU2();
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueA != valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 161,
                case IF_ICMPLT -> {
                    logger.debug("IF_ICMPLT >> ");
                    int offset = bytecodeStream.getU2();
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueA < valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 162,
                case IF_ICMPGE -> {
                    logger.debug("IF_ICMPGE >> ");
                    int offset = bytecodeStream.getU2();
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueA >= valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 163,
                case IF_ICMPGT -> {
                    logger.debug("IF_ICMPGT >> ");
                    int offset = bytecodeStream.getU2();
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueA > valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 164,
                case IF_ICMPLE -> {
                    logger.debug("IF_ICMPLE >> ");
                    int offset = bytecodeStream.getU2();
                    int valueB = frame.getOperandStack().popInt();
                    int valueA = frame.getOperandStack().popInt();
                    if (valueA <= valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 165,
                case IF_ACMPEQ -> {
                    logger.debug("IF_ACMPEQ >> ");
                    int offset = bytecodeStream.getU2();
                    InstanceOop valueB = (InstanceOop)frame.getOperandStack().popRef();
                    InstanceOop valueA = (InstanceOop)frame.getOperandStack().popRef();
                    if (valueA == valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 166,
                case IF_ACMPNE -> {
                    logger.debug("IF_ACMPNE >> ");
                    int offset = bytecodeStream.getU2();
                    InstanceOop valueB = (InstanceOop)frame.getOperandStack().popRef();
                    InstanceOop valueA = (InstanceOop)frame.getOperandStack().popRef();
                    if (valueA != valueB){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 167
                case GOTO -> {
                    logger.debug("GOTO >> ");
                    int offset = bytecodeStream.getU2();
                    bytecodeStream.conditionalJump(offset);
                }
                // 167
                case JSR -> {
                    logger.debug("JSR >> ");
                    // officially deprecated in the JVM specification.
                }
                // 167
                case RET -> {
                    logger.debug("RET >> ");
                    // officially deprecated in the JVM specification.
                }
                // 170
                case TABLESWITCH -> {
                    logger.debug("TABLESWITCH >> ");
                    int currentIndex = bytecodeStream.index;
                    int condition = frame.getOperandStack().popInt();
                    // Padding 0 - 3
                    bytecodeStream.index += (4 - bytecodeStream.index % 4) % 4;
                    int offset = bytecodeStream.getU4();
                    int low = bytecodeStream.getU4();
                    int high = bytecodeStream.getU4();
                    for (int i = low; i <= high; i++){
                        if (condition == i){
                            offset = bytecodeStream.getU4();
                        } else{
                            bytecodeStream.getU4();
                        }
                    }
                    bytecodeStream.index = currentIndex;
                    bytecodeStream.unconditionalJump(offset);
                }
                // 171
                case LOOKUPSWITCH -> {
                    logger.debug("LOOKUPSWITCH >> ");
                    int currentIndex = bytecodeStream.index;
                    int condition = frame.getOperandStack().popInt();
                    // Padding 0 - 3
                    bytecodeStream.index += (4 - bytecodeStream.index % 4) % 4;
                    int offset = bytecodeStream.getU4();
                    int npairs = bytecodeStream.getU4();
                    for (int i = 0; i < npairs; i++){
                        if (condition ==  bytecodeStream.getU4()){
                            offset = bytecodeStream.getU4();
                        } else {
                            bytecodeStream.getU4();
                        }
                    }
                    bytecodeStream.index = currentIndex;
                    bytecodeStream.unconditionalJump(offset);
                }
                // 172
                case IRETURN -> {
                    logger.debug("IRETURN >> ");
                    int ret = frame.getOperandStack().popInt();
                    thread.getStack().pop();
                    frame = (JavaVFrame) thread.getStack().peek();
                    frame.getOperandStack().pushInt(ret);
                }
                // 173
                case LRETURN -> {
                    logger.debug("LRETURN >> ");
                    long ret = frame.getOperandStack().popLong();
                    thread.getStack().pop();
                    frame = (JavaVFrame) thread.getStack().peek();
                    frame.getOperandStack().pushLong(ret);
                }
                // 174
                case FRETURN -> {
                    logger.debug("FRETURN >> ");
                    float ret = frame.getOperandStack().popFloat();
                    thread.getStack().pop();
                    frame = (JavaVFrame) thread.getStack().peek();
                    frame.getOperandStack().pushFloat(ret);
                }
                // 175
                case DRETURN -> {
                    logger.debug("DRETURN >> ");
                    double ret = frame.getOperandStack().popDouble();
                    thread.getStack().pop();
                    frame = (JavaVFrame) thread.getStack().peek();
                    frame.getOperandStack().pushDouble(ret);
                }
                // 176
                case ARETURN -> {
                    logger.debug("ARETURN >> ");
                    Object ret = frame.getOperandStack().popRef();
                    thread.getStack().pop();
                    frame = (JavaVFrame) thread.getStack().peek();
                    frame.getOperandStack().pushRef(ret);
                }
                // 177
                case RETURN -> {
                    logger.debug("RETURN >> ");
                    thread.getStack().pop();
                }

                // 178
                case GETSTATIC -> {
                    logger.debug("GETSTATIC >> ");
                    ConstantFieldrefInfo fieldref = (ConstantFieldrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String className = fieldref.resolveClassName(constantPool).replace('/', '.');
                    String fieldName = fieldref.resolveFieldName(constantPool);
                    if (className.startsWith("java")){
                        Class<?> clazz = Class.forName(className);
                        Field clazzField = clazz.getField(fieldName);
                        Object fieldObject = clazzField.get(null);
                        frame.getOperandStack().pushRef(fieldObject);
                    } else if (className.startsWith("com.avaya.jvm")) {
                        InstanceKlass klass = BootClassLoader.loadKlass(className);
                        klass.getStaticFields().getValue(fieldName, frame.getOperandStack());
                    }

                }
                // 179
                case PUTSTATIC -> {
                    logger.debug("PUTSTATIC >> ");
                    ConstantFieldrefInfo fieldref = (ConstantFieldrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String className = fieldref.resolveClassName(constantPool).replace('/', '.');
                    String fieldName = fieldref.resolveFieldName(constantPool);
                    ValueType fieldType = fieldref.resolveFieldType(constantPool);
                    if (className.startsWith("java")){
                        // TODO: implement it later
                    } else if (className.startsWith("com.avaya.jvm")) {
                        InstanceKlass klass = BootClassLoader.loadKlass(className);
                        klass.getStaticFields().setValue(fieldName, fieldType, frame.getOperandStack());
                    }
                }
                // 180
                case GETFIELD -> {
                    logger.debug("GETFIELD >> ");
                    ConstantFieldrefInfo fieldref = (ConstantFieldrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String className = fieldref.resolveClassName(constantPool).replace('/', '.');
                    String fieldName = fieldref.resolveFieldName(constantPool);
                    InstanceOop oop = (InstanceOop)frame.getOperandStack().popRef();
                    if (className.startsWith("java")){
                        // TODO: implement it later
                    } else if (className.startsWith("com.avaya.jvm")) {
                        oop.getOopFields().getValue(fieldName, frame.getOperandStack());
                    }
                }
                // 181
                case PUTFIELD -> {
                    logger.debug("PUTFIELD >> ");
                    ConstantFieldrefInfo fieldref = (ConstantFieldrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String className = fieldref.resolveClassName(constantPool).replace('/', '.');
                    String fieldName = fieldref.resolveFieldName(constantPool);
                    ValueType fieldType = fieldref.resolveFieldType(constantPool);
                    if (className.startsWith("java")){
                        // TODO: implement it later
                    } else if (className.startsWith("com.avaya.jvm")) {
                        FieldArray.oopSetValue(fieldName, fieldType, frame.getOperandStack());
                    }
                }
                // 182
                case INVOKEVIRTUAL -> {
                    logger.debug("INVOKEVIRTUAL >> ");
                    ConstantMethodrefInfo methodref = (ConstantMethodrefInfo) (constantPool.getEntries().get(bytecodeStream.getU2()));
                    String objectClassName = methodref.resolveClassName(constantPool);
                    String methodName = methodref.resolveMethodName(constantPool);
                    Descriptor methodDescriptor = methodref.resolveMethodDescriptor(constantPool);

                    // Handle JRE library classes (java.*).
                    if (objectClassName.startsWith("java")) {
                        callJavaNativeMethod(methodref, constantPool);
                    } else if (objectClassName.startsWith("com/avaya/jvm")) {
                        InstanceKlass klass = BootClassLoader.loadKlass(objectClassName.replace('/', '.'));
                        MethodInfo methodInfo = null;
                        for (int i = 0; i < klass.getMethods().size(); i++) {
                            MethodInfo method = klass.getMethods().get(i);
                            if (method.getName().equals(methodName)) {
                                methodInfo = method;
                                break;
                            }
                        }
                        callPolyInstanceMethod(methodInfo);
                    }
                }
                // 183
                case INVOKESPECIAL -> {
                    logger.debug("INVOKESPECIAL >> ");
                    ConstantMethodrefInfo methodref = (ConstantMethodrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String objectClassName = methodref.resolveClassName(constantPool);
                    String methodName = methodref.resolveMethodName(constantPool);
                    Descriptor methodDescriptor = methodref.resolveMethodDescriptor(constantPool);
                    if (objectClassName.startsWith("java")) {
                        // TODO: handle Static JRE Library Methods
                        if (objectClassName.equals("java/lang/Object")){
                            // do nothing for the root class, but pop THIS
                            frame.getOperandStack().popRef();
                        } else {
                            // normal instanceMethod
                            callJavaNativeMethod(methodref, constantPool);
                        }
                    } else if (objectClassName.startsWith("com/avaya/jvm")){
                        // Self-defined classes (com.avaya.jvm.*)
                        InstanceKlass klass = BootClassLoader.loadKlass(objectClassName.replace('/', '.'));
                        MethodInfo methodInfo = null;
                        for (int i = 0; i < klass.getMethods().size(); i++){
                            MethodInfo method = klass.getMethods().get(i);
                            if (method.getName().equals(methodName)){
                                methodInfo = method;
                                break;
                            }
                        }
                        callInstanceMethod(methodInfo);
                    }
                }
                // 184
                case INVOKESTATIC -> {
                    logger.debug("INVOKESTATIC >> ");
                    ConstantMethodrefInfo methodref = (ConstantMethodrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String objectClassName = methodref.resolveClassName(constantPool);
                    String methodName = methodref.resolveMethodName(constantPool);
                    // Handle JRE library classes (java.*).
                    if (objectClassName.startsWith("java")) {
                        // TODO: handle Static JRE Library Methods
                    }
                    // Self-defined classes (com.avaya.jvm.*)
                    else if (objectClassName.startsWith("com/avaya/jvm")){
                        InstanceKlass klass = BootClassLoader.loadKlass(objectClassName.replace('/', '.'));
                        MethodInfo methodInfo = null;
                        for (int i = 0; i < klass.getMethods().size(); i++){
                            MethodInfo method = klass.getMethods().get(i);
                            if (method.getName().equals(methodName)){
                                methodInfo = method;
                                break;
                            }
                        }
                        callStaticMethod(methodInfo);
                    }
                }
                // 185
                case INVOKEINTERFACE -> {
                    logger.debug("INVOKEINTERFACE >> ");
                    ConstantInterfaceMethodrefInfo interfaceMethodref = (ConstantInterfaceMethodrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String objectClassName = interfaceMethodref.resolveClassName(constantPool);
                    String methodName = interfaceMethodref.resolveMethodName(constantPool);
                    Descriptor methodDescriptor = interfaceMethodref.resolveMethodDescriptor(constantPool);
                    int countByte = bytecodeStream.getU1();
                    int zeroByte = bytecodeStream.getU1();

                    // Handle JRE library classes (java.*).
                    if (objectClassName.startsWith("java")) {
                        // TODO: implement it later
                    } else if (objectClassName.startsWith("com/avaya/jvm")) {
                        // TODO: currently, it is a simple one without i-table, will do it later
                        InstanceKlass klass = BootClassLoader.loadKlass(objectClassName.replace('/', '.'));
                        MethodInfo methodInfo = null;
                        for (int i = 0; i < klass.getMethods().size(); i++) {
                            MethodInfo method = klass.getMethods().get(i);
                            if (method.getName().equals(methodName)) {
                                methodInfo = method;
                                break;
                            }
                        }
                        // interface behavior as polymorphism...
                        // We can use this function since I don't implement v-table...
                        callPolyInstanceMethod(methodInfo);
                    }

                }
                // 187
                case NEW -> {
                    logger.debug("NEW >> ");
                    ConstantClassInfo classInfo = (ConstantClassInfo)constantPool.getEntries().get(bytecodeStream.getU2());
                    String objectClassName = classInfo.resolveName(constantPool);
                    if (objectClassName.startsWith("java")) {
                        // JRE Library Classes
                        // Do nothing and will directly new this object in constructor <init>
                        frame.getOperandStack().pushRef(null);

                    } else {
                        // user-defined class
                        InstanceOop oop = new InstanceOop(objectClassName);
                        frame.getOperandStack().pushRef(oop);
                    }
                }
                // 188
                case NEWARRAY -> {
                    logger.debug("NEWARRAY >> ");
                    // Pop array length from the operand stack
                    int length = frame.getOperandStack().popInt();

                    // Read atype operand from bytecode stream and parse to ValueType
                    ValueType type = ValueType.atype2BasicType(bytecodeStream.getU1());

                    // Create the corresponding array object based on ValueType
                    Object arrayOop;
                    switch (type){
                        case T_BYTE -> arrayOop = new ByteArrayOop(length);
                        case T_CHAR -> arrayOop = new CharArrayOop(length);
                        case T_SHORT -> arrayOop = new ShortArrayOop(length);
                        case T_INT -> arrayOop = new IntArrayOop(length);
                        case T_LONG -> arrayOop = new LongArrayOop(length);
                        case T_FLOAT -> arrayOop = new FloatArrayOop(length);
                        case T_DOUBLE -> arrayOop = new DoubleArrayOop(length);
                        default -> throw new IllegalArgumentException("Invalid array type: " + type);
                    }
                    // Push the newly created array reference onto the operand stack
                    frame.getOperandStack().pushRef(arrayOop);

                }
                // 190
                case ANEWARRAY -> {
                    logger.debug("ANEWARRAY >> ");
                    // Pop array length from the operand stack
                    int length = frame.getOperandStack().popInt();

                    ConstantClassInfo classInfo = (ConstantClassInfo)constantPool.getEntries().get(bytecodeStream.getU2());
                    String className = classInfo.resolveName(constantPool);

                    Object array = null;

                    if (className.startsWith("java")) {
                        // For standard Java classes, use reflection
                        Class<?> clazz = Class.forName(className.replace('/', '.'));
                        array = java.lang.reflect.Array.newInstance(clazz, length);
                    } else if (className.startsWith("com/avaya/jvm")) {
                        // TODO: create proper ArrayKlass based on the element's instanceKlass
                        array = new ObjectArrayOop(length);
                    }
                    frame.getOperandStack().pushRef(array);
                }
                // 190
                case ARRAYLENGTH -> {
                    logger.debug("ARRAYLENGTH >> ");
                    Object array = frame.getOperandStack().popRef();
                    int length = 0;
                    if (array instanceof ArrayOop){
                        length = ((ArrayOop)array).getLength();
                    } else {
                        length = java.lang.reflect.Array.getLength(array);
                    }
                    frame.getOperandStack().pushInt(length);
                }
                // 191
                case ATHROW -> {
                    logger.debug("ATHROW >> ");
                    // pop the Exception object to thread
                    Throwable exception = (Throwable) frame.getOperandStack().popRef();
                    thread.setCurrentException(exception);
                    // get current pc
                    int currentIndex = bytecodeStream.getIndex() - 1;
                    // get Exception Table from CodeAttribute
                    List<CodeAttribute.ExceptionTableEntry> exceptionTable = bytecodeStream.getCode().getExceptionTable();
                    boolean notCatched = true;
                    for (CodeAttribute.ExceptionTableEntry entry : exceptionTable){
                        if (currentIndex >= entry.getStartPc() || currentIndex < entry.getEndPc()){
                            // catch
                            ConstantClassInfo targetClass = (ConstantClassInfo) constantPool.getEntries().get(entry.getCatchType());
                            String targetClassName = targetClass.resolveName(constantPool);
                            if(exception.getClass().getName().equals(targetClassName.replace('/', '.'))){
                                bytecodeStream.index = entry.getHandlerPc();
                                frame.getOperandStack().pushRef(exception);
                                notCatched = false;
                                break;
                            }
                        }
                    }
                    // no catch for this exception, directly go to previous frame
                    if (notCatched) {
                        thread.getStack().pop();
                    }
                }
                // 192
                case CHECKCAST -> {
                    logger.debug("CHECKCAST >> ");
                    int index = bytecodeStream.getU2();
                    Object obj = frame.getOperandStack().popRef();
                    boolean match = false; // default to false
                    if (obj != null){
                        ConstantClassInfo classInfo = (ConstantClassInfo) constantPool.getEntries().get(index);
                        String className = classInfo.resolveName(constantPool);

                        if (className.startsWith("java")) {
                            // For standard Java classes, use reflection
                            Class<?> clazz = Class.forName(className.replace('/', '.'));
                            if (clazz.isInstance(obj)){
                                match = true;
                            }
                        } else if (className.startsWith("com/avaya/jvm")) {
                            // TODO: implement parent class and interface checking later
                            if (((InstanceOop)obj).getKlass() == BootClassLoader.loadKlass(className.replace('/', '.')) ){
                                match = true;
                            }
                        }
                    } else {
                        match = true;
                    }

                    if (match){
                        // Type matches or null
                        frame.getOperandStack().pushRef(obj);
                    } else {
                        // TODO: generate ClassCastException later
                    }
                }
                // 193
                case INSTANCEOF -> {
                    logger.debug("INSTANCEOF >> ");
                    int index = bytecodeStream.getU2();
                    Object obj = frame.getOperandStack().popRef();
                    int result = 0; // default to false (0)
                    if (obj != null){
                        ConstantClassInfo classInfo = (ConstantClassInfo) constantPool.getEntries().get(index);
                        String className = classInfo.resolveName(constantPool);

                        if (className.startsWith("java")) {
                            // For standard Java classes, use reflection
                            Class<?> clazz = Class.forName(className.replace('/', '.'));
                            if (clazz.isInstance(obj)){
                                result = 1;
                            }
                        } else if (className.startsWith("com/avaya/jvm")) {
                            // TODO: implement parent class and interface checking later
                            if (((InstanceOop)obj).getKlass() == BootClassLoader.loadKlass(className.replace('/', '.')) ){
                                result = 1;
                            }
                        }
                    }
                    frame.getOperandStack().pushInt(result);
                }
                // 197
                case MULTIANEWARRAY -> {
                    logger.debug("MULTIANEWARRAY >> ");

                    ConstantClassInfo classInfo = (ConstantClassInfo)constantPool.getEntries().get(bytecodeStream.getU2());
                    // e.g. className : "[[Ljava/lang/String;", dimensions : 2
                    String className = classInfo.resolveName(constantPool);
                    int dimensions = bytecodeStream.getU1();


                    int[] dimensionList = new int[dimensions];
                    // get each dimension from operand stack
                    for (int i = 0; i < dimensions; i++){
                        dimensionList[i] = frame.getOperandStack().popInt();
                    }
                    // swap it
                    for (int i = 0; i < dimensions / 2; i++) {
                        int temp = dimensionList[i];
                        dimensionList[i] = dimensionList[dimensions - 1 - i];
                        dimensionList[dimensions - 1 - i] = temp;
                    }

                    Object array = null;
                    Class<?> type = null;
                    boolean isJavaType = true;

                    char c = className.charAt(dimensions);

                    switch (c){
                        case 'B' -> {
                            type = byte.class;
                        }
                        case 'C' -> {
                            type = char.class;
                        }
                        case 'D' -> {
                            type = double.class;
                        }
                        case 'F' -> {
                            type = float.class;
                        }
                        case 'I' -> {
                            type = int.class;
                        }
                        case 'J' -> {
                            type = long.class;
                        }
                        case 'S' -> {
                            type = short.class;
                        }
                        case 'Z' -> {
                            type = boolean.class;
                        }
                        case 'L' -> {
                            String memberClassName = className.substring(dimensions + 1, className.length() - 1).replace('/', '.');
                            if (memberClassName.startsWith("java")) {
                                type = Class.forName(memberClassName);
                            } else if (className.startsWith("com.avaya.jvm")) {
                                isJavaType = false;
                                // TODO: self-defined class
                            }
                        }
                    }

                    if (isJavaType){
                        array = Array.newInstance(type, dimensionList);
                    } else {
                        // TODO: multi-dimentional array for self-defined class
                        array = null;
                    }

                    frame.getOperandStack().pushRef(array);

                }
                // 198,
                case WIDE -> {
                    logger.debug("WIDE >> ");
                    // WIDE applies to the following instructions:
                    //   ILOAD, FLOAD, ALOAD, LLOAD, DLOAD
                    //   ISTORE, FSTORE, ASTORE, LSTORE, DSTORE
                    //   IINC
                    // RET is not supported in this implementation
                    isWide = true;
                }
                // 198,
                case IFNULL -> {
                    logger.debug("IFNULL >> ");
                    int offset = bytecodeStream.getU2();
                    InstanceOop value = (InstanceOop)frame.getOperandStack().popRef();
                    if (value == null){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 199,
                case IFNONNULL -> {
                    logger.debug("IFNONNULL >> ");
                    int offset = bytecodeStream.getU2();
                    InstanceOop value = (InstanceOop)frame.getOperandStack().popRef();
                    if (value != null){
                        bytecodeStream.conditionalJump(offset);
                    }
                }
                // 200
                case GOTO_W -> {
                    logger.debug("GOTO_W >> ");
                    int currentIndex = bytecodeStream.getIndex();
                    int offset = bytecodeStream.getU4();
                    bytecodeStream.index = currentIndex;
                    bytecodeStream.unconditionalJump(offset);
                }
                // 201， officially deprecated in JVM specification
                case JSR_W -> {
                    logger.debug("JSR_W >> ");
                    // no implementation needed for modern Java
                }
                // 202， reserved for debuggers
                case BREAKPOINT -> {
                    logger.debug("BREAKPOINT >> ");
                    // not generated by javac, only used by debuggers
                }
            }
        }
        logger.debug("function completed.");

    }
}
