package com.avaya.jvm.hotspot.share.prims;

import com.avaya.jvm.hotspot.share.interpreter.BytecodeInterpreter;
import com.avaya.jvm.hotspot.share.oops.*;
import com.avaya.jvm.hotspot.share.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaNativeInterface {
    private static final Logger logger = LoggerFactory.getLogger(JavaNativeInterface.class);

    public static MethodInfo getMain(InstanceKlass klass){
        logger.info("Searching entry function...");
        for (MethodInfo method:klass.getMethods()){
            if (method.getName().equals("main")){
                logger.debug("    main() found.");
                return method;
            }
        }
        return null;
    }

    public static void callStaticMethod(MethodInfo method) throws Throwable {
        logger.debug("function {}() is called", method.getName());

        JavaThread thread = Threads.getCurrentThread();
        CodeAttribute code_attr = null;

        for (AttributeInfo attr : method.getAttributes()) {
            if (attr instanceof CodeAttribute code) {
                code_attr = code;
                break;
            }
        }

        JavaVFrame newFrame = new JavaVFrame(code_attr);

        if (!method.getName().equals("main")){
            JavaVFrame oldFrame = (JavaVFrame) thread.getStack().peek();
            transferArguments(oldFrame, newFrame, method.getDescriptor().parseDescriptor(), false);
        }
        thread.getStack().push(newFrame);

        BytecodeInterpreter.run(thread, code_attr.getCode());
    }

    public static void callJavaNativeMethod(ConstantMethodrefInfo methodref, ConstantPool constantPool) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String methodName = methodref.resolveMethodName(constantPool);
        Descriptor methodDescriptor = methodref.resolveMethodDescriptor(constantPool);
        logger.debug("jre native method {}() is called", methodName);

        // Store parameter values and classes
        List<Object> objectList = new ArrayList<>();
        List<Class<?>> classList = new ArrayList<>();

        JavaThread thread = Threads.getCurrentThread();
        JavaVFrame frame  = (JavaVFrame) thread.getStack().peek();

        transferJavaArguments(frame,objectList, classList, methodDescriptor.parseDescriptor());

        // Pop the object reference owning this method
        Object targetObject = frame.getOperandStack().popRef();
        // Retrieve the Method object, and invoke it
        if (methodName.equals("<init>")){
            // do nothing as already created and initialized in NEW bytecode
            Class<?> clazz = Class.forName(methodref.resolveClassName(constantPool).replace('/', '.'));
            Constructor<?> constructor = clazz.getDeclaredConstructor(classList.toArray(new Class<?>[0]));
            constructor.setAccessible(true);
            Object obj = constructor.newInstance(objectList.toArray(new Object[0]));
            frame.getOperandStack().popN(objectList.size());
            frame.getOperandStack().pushRef(obj);
        } else {
            Method method = targetObject.getClass().getMethod(methodName, classList.toArray(new Class<?>[0]));
            if (methodDescriptor.getReturnType().equals("V")){
                // void
                method.invoke(targetObject, objectList.toArray(new Object[0]));
            } else{
                // only implement Object return type
                // TODO: create a function for different return type based on methodDescriptor.getReturnType()
                Object obj = method.invoke(targetObject, objectList.toArray(new Object[0]));
                frame.getOperandStack().pushRef(obj);
            }
        }
    }

    public static void callPolyInstanceMethod(MethodInfo method) throws Throwable {
        logger.debug("instance polymorphism method {}() is called", method.getName());
        JavaThread thread = Threads.getCurrentThread();
        CodeAttribute code_attr = null;

        for (AttributeInfo attr : method.getAttributes()) {
            if (attr instanceof CodeAttribute code) {
                code_attr = code;
                break;
            }
        }
        if (code_attr == null){
            int TMP_MAX_LENGTH = 100;
            code_attr = new CodeAttribute();
            code_attr.setMaxLocals(TMP_MAX_LENGTH);
            code_attr.setMaxStack(TMP_MAX_LENGTH);
        }
        JavaVFrame oldFrame = (JavaVFrame) thread.getStack().peek();
        JavaVFrame tmpFrame = new JavaVFrame(code_attr);
        transferArguments(oldFrame, tmpFrame, method.getDescriptor().parseDescriptor(), true);

        Object obj = tmpFrame.getLocals().getRef(0);
        // check whether it's a lambda object
        if (!(obj instanceof InstanceOop)){
            Method LambdaMethod = obj.getClass().getMethods()[0];
            LambdaMethod.setAccessible(true);
            LambdaMethod.invoke(obj);
            return;
        }
        // polymorphism
        InstanceOop oop = (InstanceOop) obj;

        // Obtain MethodInfo
        InstanceKlass oopKlass = oop.getKlass();

        MethodInfo methodInfo = method;
        for (int i = 0; i < oopKlass.getMethods().size(); i++) {
            MethodInfo poly_method = oopKlass.getMethods().get(i);
            if (poly_method.getName().equals(method.getName())) {
                methodInfo = poly_method;
                break;
            }
        }

        for (AttributeInfo attr : methodInfo.getAttributes()) {
            if (attr instanceof CodeAttribute code) {
                code_attr = code;
                break;
            }
        }

        JavaVFrame newFrame = new JavaVFrame(code_attr);
        newFrame.setLocals(tmpFrame.getLocals());

        thread.getStack().push(newFrame);
        BytecodeInterpreter.run(thread, code_attr.getCode());
    }

    public static void callInstanceMethod(MethodInfo method) throws Throwable {
        logger.debug("instance method {}() is called", method.getName());
        JavaThread thread = Threads.getCurrentThread();
        CodeAttribute code_attr = null;

        for (AttributeInfo attr : method.getAttributes()) {
            if (attr instanceof CodeAttribute code) {
                code_attr = code;
                break;
            }
        }
        JavaVFrame oldFrame = (JavaVFrame) thread.getStack().peek();
        JavaVFrame newFrame = new JavaVFrame(code_attr);
        transferArguments(oldFrame, newFrame, method.getDescriptor().parseDescriptor(), true);

        thread.getStack().push(newFrame);
        BytecodeInterpreter.run(thread, code_attr.getCode());
    }

    /*
     * ===============================================
     * JVM Instance Method Call Explanation
     * ===============================================
     *
     * Suppose we call an instance method:
     *      obj.foo(arg1, arg2)
     *
     * 1. Caller frame operand stack before the call:
     *
     *   operand stack (bottom -> top):
     *   --------------------------------
     *   | ... previous values          |
     *   | objectref (this)             |
     *   | arg1                         |
     *   | arg2                         |  <- top
     *   --------------------------------
     *
     * 2. During callInstanceMethod:
     *    - Pop this and arguments from the stack
     *    - JVM specification:
     *        locals[0] = this
     *        locals[1..N] = method parameters
     *
     * 3. New frame (callee frame) initialization:
     *
     *   locals:
     *   --------------------------------
     *   | locals[0] = this              |
     *   | locals[1] = arg1              |
     *   | locals[2] = arg2              |
     *   --------------------------------
     *
     *   operand stack (initially empty):
     *   --------------------------------
     *   |                              |
     *   --------------------------------
     */

    public static void transferArguments(JavaVFrame oldFrame, JavaVFrame newFrame, List<String> argSeq, boolean isInstance){
        OperandStack oldOperandStack = oldFrame.getOperandStack();
        LocalVariableArray newLocals = newFrame.getLocals();

        if (isInstance){
            // instance function, has "this" pointer
            // index N, ..., 2, 1
            for (int i = argSeq.size(); i > 0; i--){
                switch (argSeq.get(i-1).charAt(0)){
                    case 'Z', 'B', 'C', 'S', 'I' -> {
                        newLocals.setInt(i, oldOperandStack.popInt());
                    }
                    case 'D' -> {
                        newLocals.setDouble(i, oldOperandStack.popDouble());
                    }
                    case 'F' -> {
                        newLocals.setFloat(i, oldOperandStack.popFloat());
                    }
                    case 'J' -> {
                        newLocals.setLong(i, oldOperandStack.popLong());
                    }
                    case 'L', '[' -> {
                        newLocals.setRef(i, oldOperandStack.popRef());
                    }
                }
            }
            newLocals.setRef(0, oldOperandStack.popRef());
        } else{
            // static function
            // index N - 1, ... , 1, 0
            for (int i = argSeq.size() - 1; i >= 0; i--){
                switch (argSeq.get(i).charAt(0)){
                    case 'Z', 'B', 'C', 'S', 'I' -> {
                        newLocals.setInt(i, oldOperandStack.popInt());
                    }
                    case 'D' -> {
                        newLocals.setDouble(i, oldOperandStack.popDouble());
                    }
                    case 'F' -> {
                        newLocals.setFloat(i, oldOperandStack.popFloat());
                    }
                    case 'J' -> {
                        newLocals.setLong(i, oldOperandStack.popLong());
                    }
                    case 'L', '[' -> {
                        newLocals.setRef(i, oldOperandStack.popRef());
                    }
                }
            }
        }
    }

    public static void transferJavaArguments(JavaVFrame frame, List<Object> objectList, List<Class<?>> classList, List<String> argSeq) throws ClassNotFoundException {
        for (int i = argSeq.size(); i > 0; i--) {
            switch (argSeq.get(i - 1).charAt(0)) {
                case 'B' -> {
                    classList.add(byte.class);
                    objectList.add((byte) frame.getOperandStack().popInt());
                }
                case 'C' -> {
                    classList.add(char.class);
                    objectList.add((char) frame.getOperandStack().popInt());
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
                    objectList.add((short) frame.getOperandStack().popInt());
                }
                case 'Z' -> {
                    classList.add(boolean.class);
                    objectList.add(frame.getOperandStack().popInt() != 0);
                }
                case 'L', '[' -> {
                    String rawString = argSeq.get(i - 1);
                    String className = rawString.substring(1, rawString.length() - 1).replace('/', '.');
                    classList.add(Class.forName(className));
                    objectList.add(frame.getOperandStack().popRef());
                }
            }
        }
        Collections.reverse(classList);
        Collections.reverse(objectList);
    }

    public static Object callDynamicMethod (ConstantInvokeDynamicInfo dynamicInfo, InstanceKlass klass) throws Throwable {
        ConstantPool constantPool = klass.getConstantPool();
        // parse ConstantNameAndTypeInfo from ConstantInvokeDynamicInfo
        int nameTypeIndex = dynamicInfo.getNameAndTypeIndex();
        ConstantNameAndTypeInfo nameAndType = (ConstantNameAndTypeInfo)constantPool.getEntries().get(nameTypeIndex);
        // get the SAM (Single Abstract Method) name and return type
        String samMethodName = nameAndType.resolveName(constantPool);
        String returnType = nameAndType.resolveDescriptor(constantPool).getReturnType();
        Class<?> returnClazz = Class.forName(returnType.substring(1, returnType.length() - 1).replace('/', '.'));

        // get bootstrap method from ConstantInvokeDynamicInfo
        int bootstrapIndex = dynamicInfo.getBootstrapMethodAttrIndex();
        BootstrapMethods.BootstrapMethodsEntry bootstrapMethod = null;
        for (AttributeInfo attributeInfo : klass.getAttributes()){
            if (attributeInfo.getAttributeType() == AttributeType.BOOTSTRAP_METHODS){
                bootstrapMethod = ((BootstrapMethods)attributeInfo).getMethodsTable().get(bootstrapIndex);
            }
        }
        // get the bootstrap method handle and its referenced method
        ConstantMethodHandleInfo methodHandleInfo = (ConstantMethodHandleInfo)constantPool.getEntries().get(bootstrapMethod.getBootstrapMethodRef());

        // get the first bootstrap argument: the private lambda method
        ConstantMethodHandleInfo privateLambdaHandleInfo = (ConstantMethodHandleInfo)bootstrapMethod.getArgumentsTable().get(1);
        ConstantMethodrefInfo privateLambdamethodRef = (ConstantMethodrefInfo)constantPool.getEntries().get(privateLambdaHandleInfo.getReferenceIndex());

        // Resolve the class that contains the lambda method
        Class<?> callerClazz = Class.forName(privateLambdamethodRef.resolveClassName(constantPool).replace('/', '.'));

        // Create a Lookup object with private access to the lambda class
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(callerClazz, MethodHandles.lookup());

        // Get a MethodHandle for the private lambda method (no parameters)
        // TODO: extend this to support lambdas with parameters
        Method method = callerClazz.getDeclaredMethod(privateLambdamethodRef.resolveMethodName(constantPool));
        MethodHandle mh = lookup.unreflect(method);

        // MethodType of the lambda method
        MethodType type = mh.type();

        // Factory type: the return type is the SAM interface
        MethodType factoryType = MethodType.methodType(returnClazz);

        // use LambdaMetafactory to create a CallSite for the invokedynamic
        CallSite callSite = LambdaMetafactory.metafactory(lookup, samMethodName, factoryType, type, mh, type);

        // invoke the CallSite to obtain the lambda object implementing the SAM interface
        return callSite.getTarget().invoke();
    }
}
