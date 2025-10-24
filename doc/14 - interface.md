# Interface

In Java, **an interface defines a set of methods that represent a capability or behavior**, without providing any implementation. Unlike a class, an interface does not describe what something is, but rather what something can do.

Interfaces were introduced to solve a design problem: sometimes different classes need to share common behavior, even if they are not related by inheritance.

```java
class Dog implements Runnable { } // Dog can run
class Car implements Runnable { } // Car can run
```

## How an Interface Method works

Consider this example:

```java
interface Greeter {
    void sayHello();
}

class SimpleGreeter implements Greeter {
    public void sayHello() {
        System.out.println("hello");
    }
}

public class Interface {
    public static void main(String[] args) {
        Greeter g = new SimpleGreeter();
        g.sayHello();
    }
}
```

Compile and run it:

```
cmd > javac Interface.java

cmd > java Interface
hello
```

Check the class files:

```
cmd > javap -c Greeter
Compiled from "Interface.java"
interface Greeter {
  public abstract void sayHello();
}

cmd > javap -c SimpleGreeter
Compiled from "Interface.java"
class SimpleGreeter implements Greeter {
  SimpleGreeter();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public void sayHello();
    Code:
       0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #13                 // String hello
       5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
}

cmd > javap -c Interface
Compiled from "Interface.java"
public class Interface {
  public Interface();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #7                  // class SimpleGreeter
       3: dup
       4: invokespecial #9                  // Method SimpleGreeter."<init>":()V
       7: astore_1
       8: aload_1
       9: invokeinterface #10,  1           // InterfaceMethod Greeter.sayHello:()V
      14: return
}
```

When the JVM executes `g.sayHello()` in `main()`, it follows these steps:
- Recognize the call as an interface method and use the `invokeinterface` instruction.
- Look up the method in the `interface` (`Greeter.class`) and get its method slot from the **interface method table (i-table)**.
- Pop the reference (the actual object, `SimpleGreeter`) from the operand stack and access its class metadata (`InstanceKlass`).
- Use the method slot (obtained in step 2) to locate the real implementation of `sayHello()` in the i-table of `SimpleGreeter`.
- Jump to and execute that method.

## v-table and i-table in HotSpot JVM

In HotSpot, **v-tables** and **i-tables** are stored for maximum speed using a “base + offset” memory layout.

The idea is simple: the v-table and i-table data are placed immediately after the `InstanceKlass` object in memory, forming a single, continuous block.

```c++
// https://github.com/openjdk/jdk/blob/master/src/hotspot/share/oops/instanceKlass.hpp

class InstanceKlass: public Klass {
  // embedded Java vtable follows here
  // embedded Java itables follows here
}
```

The `klassVtable` and `klassItable` classes are not the tables themselves. They are temporary "helper" tools that know how to find and read the table data within that single memory block.

### v-table

The v-table is a simple array of `vtableEntry` objects, where each entry just holds a pointer to a method. 

`klassVtable` finds it using an offset from the start of the `Klass` object.

```c++
// https://github.com/openjdk/jdk/blob/master/src/hotspot/share/oops/klassVtable.hpp

// v-table
class klassVtable {
    Klass*       _klass;            // my klass
    int          _tableOffset;      // offset of start of vtable data within klass
    int          _length;           // length of vtable (number of entries)
}

class vtableEntry {
    Method* _method;
}
```

### i-table

The i-table is a two-level structure used for classes implementing multiple interfaces.

First, it finds the interface in the offset table (`itableOffsetEntry`). Then, it uses this offset to locate the actual method table (`itableMethodEntry`).

```c++
// https://github.com/openjdk/jdk/blob/master/src/hotspot/share/oops/klassVtable.hpp
// i-table
//
// Format of an itable
//
//    ---- offset table ---
//    Klass* of interface 1             \
//    offset to vtable from start of oop  / offset table entry
//    ...
//    Klass* of interface n             \
//    offset to vtable from start of oop  / offset table entry
//    --- vtable for interface 1 ---
//    Method*                             \
//    compiler entry point                / method table entry
//    ...
//    Method*                             \
//    compiler entry point                / method table entry
//    -- vtable for interface 2 ---
//    ...

class klassItable {
    InstanceKlass*       _klass;             // my klass
    int                  _table_offset;      // offset of start of itable data within klass (in words)
    int                  _size_offset_table; // size of offset table (in itableOffset entries)
    int                  _size_method_table; // size of methodtable (in itableMethodEntry entries)
}

class itableOffsetEntry {
    InstanceKlass* _interface;
    int      _offset;
}

class itableMethodEntry {
    Method* _method;
}
```