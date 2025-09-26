package com.avaya.jvm.hotspot.share.prims;

import com.avaya.jvm.hotspot.share.interpreter.BytecodeInterpreter;
import com.avaya.jvm.hotspot.share.oops.*;
import com.avaya.jvm.hotspot.share.runtime.JavaThread;
import com.avaya.jvm.hotspot.share.runtime.JavaVFrame;
import com.avaya.jvm.hotspot.share.runtime.Threads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class JavaNativeInterface {
    private static Logger logger = LoggerFactory.getLogger(JavaNativeInterface.class);

    public static MethodInfo getMain(InstanceKlass klass){
        logger.debug("Searching entry function...");
        for (MethodInfo method:klass.getMethods()){
            if (method.getName().equals("main")){
                logger.debug("  main() found.");
                return method;
            }
        }
        return null;
    }

    public static void callStaticMethod(MethodInfo method) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        logger.debug("function {} is called", method.getName());

        JavaThread thread = Threads.getCurrentThread();
        CodeAttribute code_attr = null;

        for (AttributeInfo attr : method.getAttributes()) {
            if (attr instanceof CodeAttribute code) {
                code_attr = code;
                break;
            }
        }

        JavaVFrame frame = new JavaVFrame(code_attr);
        thread.getStack().push(frame);

        BytecodeInterpreter.run(thread, code_attr.getCode());
    }
}
