package com.avaya.jvm.hotspot.share.runtime;

import lombok.Data;

import java.util.Stack;
@Data
public class JavaThread extends Thread{
    private Stack<VFrame> stack = new Stack<>();
    /**
     * The currently active exception for this thread.
     *
     * Each thread can have only one active exception at a time,
     * so this field tracks it during throw, catch, or propagation.
     */
    private Throwable currentException = null;
}
