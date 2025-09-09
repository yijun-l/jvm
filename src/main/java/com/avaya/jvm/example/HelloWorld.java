package com.avaya.jvm.example;

public class HelloWorld {

    private int para = 10;

    public static void main(String[] args) {

        String s1 = "test";
        String s2 = new String("test");
        System.out.println(s1==s2);
    }
}
