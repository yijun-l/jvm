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


public class BytecodeInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(BytecodeInterpreter.class);

    public static void run(JavaThread thread, BytecodeStream bytecodeStream) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        JavaVFrame frame = (JavaVFrame) thread.getStack().peek();
        ConstantPool constantPool = bytecodeStream.getKlass().getConstantPool();
        while (!bytecodeStream.end()){
            switch (Bytecodes.fromOpcode(bytecodeStream.getU1())){
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
                    // TODO: will check parameter number later after completed Descriptor class
                    Object argument = frame.getOperandStack().popRef();
                    Object targetObject = frame.getOperandStack().popRef();
                    Class<?> targetClass = targetObject.getClass();
                    Method method = targetClass.getMethod(methodName, argument.getClass());
                    method.invoke(targetObject, argument);
                }
            }
        }
        logger.debug("function completed.");

    }
}
