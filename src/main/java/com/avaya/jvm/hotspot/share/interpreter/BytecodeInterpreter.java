package com.avaya.jvm.hotspot.share.interpreter;

import com.avaya.jvm.hotspot.share.oops.*;
import com.avaya.jvm.hotspot.share.runtime.JavaThread;
import com.avaya.jvm.hotspot.share.runtime.JavaVFrame;
import com.avaya.jvm.hotspot.share.runtime.StackValue;
import com.avaya.jvm.hotspot.share.utilities.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BytecodeInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(BytecodeInterpreter.class);

    public static void run(JavaThread thread, BytecodeStream bytecodeStream) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        JavaVFrame frame = (JavaVFrame) thread.getStack().peek();
        ConstantPool constantPool = bytecodeStream.getKlass().getConstantPool();
        while (!bytecodeStream.end()){
            switch (Bytecodes.fromOpcode(bytecodeStream.getU1())){
                // 16
                case BIPUSH -> {
                    logger.debug("BIPUSH >> ");
                    frame.getOperandStack().pushInt(bytecodeStream.getU1());
                }
                // 18
                case LDC -> {
                    logger.debug("LDC >> ");
                    ConstantInfo constantEntry = constantPool.getEntries().get(bytecodeStream.getU1());
                    switch (constantEntry.getTag()){
                        case JVM_CONSTANT_STRING -> {
                            String stringValue = ((ConstantStringInfo)constantEntry).resolveString(constantPool);
                            frame.getOperandStack().pushRef(stringValue);
                        }
                        default -> {
                            //TODO: other types for LDC
                            logger.debug("other types");
                        }
                    }

                }

                // 27
                case ILOAD_1 -> {
                    logger.debug("ISTORE_1 >> ");
                    frame.getOperandStack().pushInt(frame.getLocals().getInt(1));
                }

                // 60
                case ISTORE_1 -> {
                    logger.debug("ISTORE_1 >> ");
                    frame.getLocals().setInt(1, frame.getOperandStack().popInt());
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
                                case 'I' -> {
                                    classList.add(int.class);
                                    objectList.add(frame.getOperandStack().popInt());
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
                }
            }
        }
        logger.debug("function completed.");

    }
}
