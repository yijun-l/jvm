package com.avaya.jvm.hotspot.share.interpreter;

import com.avaya.jvm.hotspot.share.classfile.BootClassLoader;
import com.avaya.jvm.hotspot.share.oops.*;
import com.avaya.jvm.hotspot.share.runtime.JavaThread;
import com.avaya.jvm.hotspot.share.runtime.JavaVFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.avaya.jvm.hotspot.share.prims.JavaNativeInterface.callStaticMethod;


public class BytecodeInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(BytecodeInterpreter.class);

    public static void run(JavaThread thread, BytecodeStream bytecodeStream) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException {
        JavaVFrame frame = (JavaVFrame) thread.getStack().peek();
        ConstantPool constantPool = bytecodeStream.getKlass().getConstantPool();
        bytecodeStream.resetIndex();
        while (!bytecodeStream.end()){
            switch (Bytecodes.fromOpcode(bytecodeStream.getU1())){
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
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(bytecodeStream.getU1()));
                }
                // 22, load long from local variable (index specified by next byte) onto stack
                case LLOAD -> {
                    logger.debug("LLOAD >> ");
                    frame.getOperandStack().pushLong(frame.getLocals().getLong(bytecodeStream.getU1()));
                }
                // 23, load float from local variable (index specified by next byte) onto stack
                case FLOAD -> {
                    logger.debug("FLOAD >> ");
                    frame.getOperandStack().pushFloat(frame.getLocals().getFloat(bytecodeStream.getU1()));
                }
                // 24, load double from local variable (index specified by next byte) onto stack
                case DLOAD -> {
                    logger.debug("DLOAD >> ");
                    frame.getOperandStack().pushDouble(frame.getLocals().getDouble(bytecodeStream.getU1()));
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
                // 54, store int from stack into local variable (index specified by next byte)
                case ISTORE -> {
                    logger.debug("ISTORE >> ");
                    frame.getLocals().setInt(bytecodeStream.getU1(), frame.getOperandStack().popInt());
                }
                // 55, store long from stack into local variable (index specified by next byte)
                case LSTORE -> {
                    logger.debug("LSTORE >> ");
                    frame.getLocals().setLong(bytecodeStream.getU1(), frame.getOperandStack().popLong());
                }
                // 56, store float from stack into local variable (index specified by next byte)
                case FSTORE -> {
                    logger.debug("FSTORE >> ");
                    frame.getLocals().setFloat(bytecodeStream.getU1(), frame.getOperandStack().popFloat());
                }
                // 57, store double from stack into local variable (index specified by next byte)
                case DSTORE -> {
                    logger.debug("DSTORE >> ");
                    frame.getLocals().setDouble(bytecodeStream.getU1(), frame.getOperandStack().popDouble());
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
                    Class<?> clazz = Class.forName(className);
                    Field clazzField = clazz.getField(fieldName);
                    Object fieldObject = clazzField.get(null);
                    frame.getOperandStack().pushRef(fieldObject);
                }
                // 182
                case INVOKEVIRTUAL -> {
                    logger.debug("INVOKEVIRTUAL >> ");
                    ConstantMethodrefInfo methodref = (ConstantMethodrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String objectClassName = methodref.resolveClassName(constantPool);
                    String methodName = methodref.resolveMethodName(constantPool);
                    Descriptor methodDescriptor = methodref.resolveMethodDescriptor(constantPool);

                     // Handle JRE library classes (java.*).
                    if (objectClassName.startsWith("java")){
                        // Store parameter values and classes
                        List<Object> objectList = new ArrayList<>();
                        List<Class<?>> classList = new ArrayList<>();

                        // Get the descriptor of method parameters
                        String parameters = methodDescriptor.getField();
                        /**
                         * JVM operand stack push order for a method call:
                         * object reference > arg1 > arg2 > ... > argN
                         *
                         * When popping from the stack, the order is:
                         * argN > ... > arg2 > arg1 > object reference
                         *
                         * So we parse the descriptor in reverse and pop parameters accordingly.
                         */
                        for(int i = parameters.length() - 1 ; i >= 0; i-- ){
                            switch (parameters.charAt(i)){
                                case 'B' -> {
                                    classList.add(byte.class);
                                    objectList.add((byte)frame.getOperandStack().popInt());
                                }
                                case 'C' -> {
                                    classList.add(char.class);
                                    objectList.add((char)frame.getOperandStack().popInt());
                                }
                                case 'D' -> {
                                    classList.add(double.class);
                                    objectList.add(frame.getOperandStack().popDouble());
                                }
                                case 'F' -> {
                                    classList.add(float.class);
                                    objectList.add(frame.getOperandStack().popFloat());
                                }
                                case 'I' -> {
                                    classList.add(int.class);
                                    objectList.add(frame.getOperandStack().popInt());
                                }
                                case 'J' -> {
                                    classList.add(long.class);
                                    objectList.add(frame.getOperandStack().popLong());
                                }
                                case 'S' -> {
                                    classList.add(short.class);
                                    objectList.add((short)frame.getOperandStack().popInt());
                                }
                                case 'Z' -> {
                                    classList.add(boolean.class);
                                    objectList.add(frame.getOperandStack().popInt() != 0);
                                }
                                case ';' -> {
                                    int start = parameters.lastIndexOf('L', i);
                                    String className = parameters.substring(start + 1, i).replace('/', '.');
                                    classList.add(Class.forName(className));
                                    Object tmp = frame.getOperandStack().popRef();
                                    objectList.add(tmp);
                                    i = start;
                                }
                            }
                        }
                        // Reverse lists to match Java method call order: arg1, arg2, ..., argN
                        Collections.reverse(classList);
                        Collections.reverse(objectList);

                        // Pop the object reference owning this method
                        Object targetObject = frame.getOperandStack().popRef();
                        // Retrieve the Method object, and invoke it
                        Method method = targetObject.getClass().getMethod(methodName, classList.toArray(new Class<?>[0]));
                        method.invoke(targetObject, objectList.toArray(new Object[0]));
                    }

                    // TODO: handle self-defined Instance Methods
                }
                // 184
                case INVOKESTATIC -> {
                    logger.debug("INVOKESTATIC >> ");
                    ConstantMethodrefInfo methodref = (ConstantMethodrefInfo)(constantPool.getEntries().get(bytecodeStream.getU2()));
                    String objectClassName = methodref.resolveClassName(constantPool);
                    String methodName = methodref.resolveMethodName(constantPool);
                    Descriptor methodDescriptor = methodref.resolveMethodDescriptor(constantPool);
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
                        // static method without parameters
                        if (methodDescriptor.getField() == ""){
                            callStaticMethod(methodInfo);
                        }
                        // TODO: handle Static Methods with parameters
                    }
                }
            }
        }
        logger.debug("function completed.");

    }
}
