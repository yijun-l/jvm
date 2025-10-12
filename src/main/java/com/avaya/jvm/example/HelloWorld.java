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

//        intCal();
//        longCal();
//        floatCal();
//        doubleCal();

//        intBit();
//        longBit();

//        intCompare();
//        otherCompare();

        initArray();
    }

    public static void initArray(){
        byte[] a = {1, 2, 3};

        for (int i = 0; i < a.length; i++){
            System.out.println(a[i]);
        }
    }

    // Compare
    public static void intCompare() {
        int a = 10;
        int b = 20;

        if (a > b){
            System.out.println("a > b");
        } else if (a == b){
            System.out.println("a == b");
        } else {
            System.out.println("a < b");
        }

        if (a != b){
            System.out.println("a != b");
        }

        if (a >= b){
            System.out.println("a >= b");
        }

        if (a <= b){
            System.out.println("a <= b");
        }
    }
    public static void otherCompare() {
        long la = 100;
        long lb = 200;

        if (la > lb){
            System.out.println("la > lb");
        }

        if (la > lb){
            System.out.println("la > lb");
        }
        else if (la == lb){
            System.out.println("la == lb");
        } else {
            System.out.println("la < lb");
        }

        float fa = 10.0f;
        float fb = 20.0f;

        if (fa > fb){
            System.out.println("fa > fb");
        }
        else if (fa == fb){
            System.out.println("fa == fb");
        } else {
            System.out.println("fa < fb");
        }

        double da = 100.0;
        double db = 200.0;

        if (da > db){
            System.out.println("da > db");
        }
        else if (da == db){
            System.out.println("da == db");
        } else {
            System.out.println("da < db");
        }

    }
    // Calculation

    public static void intBit(){
        int a = 0b10101010;
        int b = 0b11001100;
        System.out.println(a << 3);
        System.out.println(a >> 3);
        System.out.println(a >>> 3);
        System.out.println(a & b);
        System.out.println(a | b);
        System.out.println(a ^ b);
        System.out.println("\n");
    }

    public static void longBit(){
        long a = 0b10101010101010101010L;
        long b = 0b11001100110011001100L;
        System.out.println(a << 3);
        System.out.println(a >> 3);
        System.out.println(a >>> 3);
        System.out.println(a & b);
        System.out.println(a | b);
        System.out.println(a ^ b);
        System.out.println("\n");
    }

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
