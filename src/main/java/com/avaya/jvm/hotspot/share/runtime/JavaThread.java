package com.avaya.jvm.hotspot.share.runtime;

import lombok.Data;

import java.util.Stack;
@Data
public class JavaThread extends Thread{
    private Stack<VFrame> stack = new Stack<>();
}
