<div align="center">

<img src="./doc/img/logo.png" alt="logo" width="300">

**a custom JVM developed during my time at Avaya**

<br>

[![GitHub License](https://img.shields.io/github/license/yijun-l/jvm)](./LICENSE)
![GitHub last commit](https://img.shields.io/github/last-commit/yijun-l/jvm)
![github-pullrequest](https://img.shields.io/github/issues-pr/yijun-l/jvm)
[![github-stars](https://img.shields.io/github/stars/yijun-l/jvm?style=flat&logo=github)](https://github.com/yijun-l/docker/stargazers)

</div>

## Overview

This project is a simplified **JVM (Java Virtual Machine)** implemented in pure Java, inspired by the design and source code of HotSpot.

It’s designed as a hands-on exercise to understand how the JVM works under the hood — including class loading, bytecode interpretation, method dispatch, inheritance, interfaces, and lambda expressions.

The project fully supports the entire Java bytecode set (202 opcodes), and can execute compiled Java programs directly by parsing .class files.

**Garbage Collection (GC)** is not yet implemented, as it would require complex memory management, **stop-the-world (STW)** coordination, and allocation strategies — which will be explored in future versions.

This JVM is not meant for production, but it’s a great way to explore JVM internals, especially for those learning about HotSpot design and runtime mechanisms.


## Architecture

This customizable JVM consists of several core subsystems, roughly following the structure of the HotSpot VM:

- **classfile**: Responsible for parsing .class files, including constant pools, methods, and attributes.

- **interpreter**: Implements the bytecode interpreter that executes each instruction sequentially.

- **memory**: Defines various memory object types (StackObj, CHeapObj, ResourceObj, etc.) to emulate HotSpot-style object lifetimes.

- **oops (ordinary object pointers)**: Represents all runtime objects such as classes, instances, arrays, and field data.

- **prims**: Implements primitive and native method interfaces (e.g., basic JNI emulation).

- **runtime**: Contains the JVM runtime components like thread management, stack frames, operand stacks, and local variables.

- **utilities**: Defines access flags, temporary value wrappers, and shared constants used across components.

## Documentation

The documentation includes the following chapters:

- [01 - Maven](./doc/01%20-%20maven.md)
- [02 - JVM Architecture](./doc/02%20-%20jvm%20arch.md)
- [03 - Klass](./doc/03%20-%20klass.md)
- [04 - Class File](./doc/04%20-%20class%20file.md)
- [05 - Class Loader](./doc/05%20-%20class%20loader.md)
- [06 - Memory Layout](./doc/06%20-%20memory%20layout.md)
- [07 - Constant Pool](./doc/07%20-%20constant%20pool.md)
- [08 - Methods Section](./doc/08%20-%20methods%20section.md)
- [09 - Java Stack](./doc/09%20-%20java%20stack.md)
- [10 - Java Function](./doc/10%20-%20java%20function.md)
- [11 - Primitive Data Types](./doc/11%20-%20primitive%20data%20types.md)
- [12 - Control Flow](./doc/12%20-%20control%20flow.md)
- [13 - Polymorphism](./doc/13%20-%20polymorphism.md)
- [14 - Interface](./doc/14%20-%20interface.md)
- [15 - Exception Handling](./doc/15%20-%20exception%20handling.md)

## Getting Started

This project can be built and run directly using IntelliJ IDEA or any Java IDE.
No external dependencies or native libraries are required — everything is implemented in pure Java.

#### 1. Clone the repository

```cmd
$ git clone https://github.com/yijun-l/jvm.git
```

#### 2. Open the project in IntelliJ IDEA (or any Java IDE).

Simply open the project as a Maven project.

All source files are under `src/main/java`.

#### 3. Add your own Java program

Place your Java file under the example directory, for example:

```src/main/java/com/avaya/jvm/example/MyTest.java```

#### 4. Update the entry class

Modify `App.java` to specify the entry, e.g.:

```java
InstanceKlass klass = BootClassLoader.loadKlass("com.avaya.jvm.example.MyTest");
```

Then, just run `App.java` in your IDE, and the custom JVM will interpret the class bytecode.