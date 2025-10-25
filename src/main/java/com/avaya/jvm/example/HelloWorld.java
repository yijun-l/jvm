package com.avaya.jvm.example;

interface Greeter {
    void sayHello();
    void sayGoodbye();
}

interface Informer {
    void showInfo();
    void showStatus();
}

class SimpleGreeter implements Greeter, Informer {
    @Override
    public void sayHello() {
        System.out.println("hello");
    }

    @Override
    public void sayGoodbye() {
        System.out.println("goodbye");
    }

    @Override
    public void showInfo() {
        System.out.println("info");
    }

    @Override
    public void showStatus() {
        System.out.println("status");
    }
}


class Base {
    public int x = 1;

    public void hello() {
        System.out.println("Base::hello");
    }

    public final void nonvirtual() {
        System.out.println("Base::nonvirtual");
    }
}

class Derived extends Base {
    public static int y = 0;

    @Override
    public void hello() {
        System.out.println("Derived::hello");
    }
}

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

//        initArray();
//        HelloWorld test = new HelloWorld();

//        intRet();
//        longRet();
//        floatRet();
//        doubleRet();
//
//        printRet();

//        switchCase();

//        objectFields();

//        great();

        exceptionHandling();
    }

    public static void exceptionHandling(){
        try {
            //            int i = 0;
            throw new Exception("My own exception");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Finally always executes");
        }
    }
    
    public static void great(){
        Informer informer = new SimpleGreeter();
        Greeter greeter = new SimpleGreeter();
        greeter.sayHello();
        greeter.sayGoodbye();
        informer.showInfo();
        informer.showStatus();

        SimpleGreeter simpleGreeter = new SimpleGreeter();
        simpleGreeter.sayHello();
        simpleGreeter.sayGoodbye();
        simpleGreeter.showInfo();
        simpleGreeter.showStatus();
    }

    public static void objectFields() {
        Derived.y = 10;
        int a = Derived.y;
        System.out.println(a);

        Base p = new Derived();
        p.hello();
        p.nonvirtual();

        System.out.println(p.x);
        p.x = 5;
        System.out.println(p.x);
    }

    public static void switchCase() {
        int i = 5;
        switch (i) {
            case 1:
            case 2:
            case 3:
                System.out.println(i);
                break;
            case 4:
            case 5:
                System.out.println(i + 10);
                break;
            default:
                System.out.println(0);
        }

        int j = 1000;
        switch (j) {
            case 1000:
                System.out.println(j);
                break;
            case 1500:
                System.out.println(j + 500);
                break;
            case 2000:
                System.out.println(j + 1000);
                break;
            default:
                System.out.println(0);
        }

        int k = 7;
        switch (k) {
            case 5:
            case 7:
            case 9:
                System.out.println(k);
                break;
            default:
                System.out.println(-1);
        }
    }

    // TODO: want to test DUP/SWAP instruction, but not sure how to generate it with Java code

    public static void printRet(){
        int i = intRet();
        long l = longRet();
        float f = floatRet();
        double d = doubleRet();
        System.out.println(i);
        System.out.println(l);
        System.out.println(f);
        System.out.println(d);

    }

    public static int intRet(){
        int a = 20;
        int b = 10;
        return a + b;
    }

    public static long longRet(){
        long a = 20;
        long b = 10;
        return a + b;
    }

    public static float floatRet(){
        float a = 20.0f;
        float b = 10.0f;
        return a + b;
    }

    public static double doubleRet(){
        double a = 20.0;
        double b = 10.0;
        return a + b;
    }

    public static void initArray(){
        byte[] b = {1, 2, 33, 4, -5, 6};
        short[] s = {10, 20, -10, -50, -100};
        int[] i = {100, 200, -200, -300, -1000};
        long[] l = {1000, 2000, -200, -500};
        char[] c = {'a', 'b'};
        float[] f = {1.0f, 2.0f};
        double[] d = {100.0, 200.0};

        for (int index = 0; index < b.length; index++){
            System.out.println(b[index]);
        }

        for (int index = 0; index < s.length; index++){
            System.out.println(s[index]);
        }

        for (int index = 0; index < i.length; index++){
            System.out.println(i[index]);
        }

        for (int index = 0; index < l.length; index++){
            System.out.println(l[index]);
        }

        for (int index = 0; index < c.length; index++){
            System.out.println(c[index]);
        }

        for (int index = 0; index < f.length; index++){
            System.out.println(f[index]);
        }

        for (int index = 0; index < d.length; index++){
            System.out.println(d[index]);
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
