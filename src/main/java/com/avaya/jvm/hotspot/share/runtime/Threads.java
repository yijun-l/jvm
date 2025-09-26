package com.avaya.jvm.hotspot.share.runtime;

import com.avaya.jvm.hotspot.share.memory.AllStatic;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Threads extends AllStatic {
    private static List<JavaThread> threadList = new ArrayList<>();
    @Getter
    @Setter
    private static JavaThread currentThread;

    public static void addThread(JavaThread t) {
        threadList.add(t);
    }

}
