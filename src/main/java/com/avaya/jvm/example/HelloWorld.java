package com.avaya.jvm.example;

public class HelloWorld {

    public static void main(String[] args) {

//        byte b = 3;
//        System.out.println(b);
//
//        char c = 69;
//        System.out.println(c);
//
//        int i = 1200;
//        System.out.println(i);
//
//        short s = 100;
//        System.out.println(s);
//
//        boolean v = true;
//        System.out.println(v);
//
//        float f = 3.14f;
//        System.out.println(f);

//        long j = 8888;
//        System.out.println(j);
//
//        double d = 3.1415926;
//        System.out.println(d);
//
//        System.out.println("hello, world");

//        int2Long();
//        int2Float();
//        int2Double();
//
//        long2Int();
//        long2Float();
//        long2Double();
//
//        float2Int();
//        float2Long();
//        float2Double();
//
//        double2Int();
//        double2Long();
//        double2Float();

        intCal();
        longCal();
        floatCal();
        doubleCal();
    }

    // Calculation

    public static void intCal(){
        int a = 20;
        int b = 10;
        System.out.println(a + b);
        System.out.println(a - b);
        System.out.println(a * b);
        System.out.println(a / b);
        System.out.println(a % b);
        System.out.println(-a);
        System.out.println(a++);
        System.out.println(++a);
        System.out.println(a--);
        System.out.println(--a);
        System.out.println("\n");
    }

    public static void longCal(){
        long a = 20;
        long b = 10;
        System.out.println(a + b);
        System.out.println(a - b);
        System.out.println(a * b);
        System.out.println(a / b);
        System.out.println(a % b);
        System.out.println(-a);
        System.out.println("\n");
    }

    public static void floatCal(){
        float a = 20.0f;
        float b = 10.0f;
        System.out.println(a + b);
        System.out.println(a - b);
        System.out.println(a * b);
        System.out.println(a / b);
        System.out.println(a % b);
        System.out.println(-a);
        System.out.println("\n");
    }

    public static void doubleCal(){
        double a = 20.0;
        double b = 10.0;
        System.out.println(a + b);
        System.out.println(a - b);
        System.out.println(a * b);
        System.out.println(a / b);
        System.out.println(a % b);
        System.out.println(-a);
        System.out.println("\n");
    }

    // Int casting
    public static void int2Long(){
        int i = 1200;
        System.out.println((long)i);
    }

    public static void int2Float(){
        int i = 1200;
        System.out.println((float)i);
    }

    public static void int2Double(){
        int i = 1200;
        System.out.println((double)i);
    }

    // Long casting
    public static void long2Int(){
        long j = 8888;
        System.out.println((int)j);
    }

    public static void long2Float(){
        long j = 8888;
        System.out.println((float)j);
    }

    public static void long2Double(){
        long j = 8888;
        System.out.println((double)j);
    }

    // Float casting

    public static void float2Int(){
        float f = 3.14f;
        System.out.println((int)f);
    }

    public static void float2Long(){
        float f = 3.14f;
        System.out.println((long)f);
    }

    public static void float2Double(){
        float f = 3.14f;
        System.out.println((double)f);
    }

    // Double casting

    public static void double2Int(){
        double d = 3.1415926;
        System.out.println((int)d);
    }

    public static void double2Long(){
        double d = 3.1415926;
        System.out.println((long)d);
    }

    public static void double2Float(){
        double d = 3.1415926;
        System.out.println((float)d);
    }

}
