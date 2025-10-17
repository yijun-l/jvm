package com.avaya.jvm.hotspot.share.prims;

import com.avaya.jvm.hotspot.share.interpreter.BytecodeInterpreter;
import com.avaya.jvm.hotspot.share.oops.*;
import com.avaya.jvm.hotspot.share.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class JavaNativeInterface {
    private static Logger logger = LoggerFactory.getLogger(JavaNativeInterface.class);

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

    public static void callStaticMethod(MethodInfo method) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
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



    public static void callInstanceMethod(MethodInfo method) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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
}
