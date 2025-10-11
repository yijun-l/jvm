# Control Flow Statements

Control flow statements are essential elements in any programming language. They determine **how and in what order** different parts of a program are executed. In other words, control flow defines the path a program takes as it runs.

These statements allow programmers to:

- Execute code conditionally based on certain criteria.
- Repeat a block of code multiple times.
- Jump to or exit specific parts of the program when necessary.

| Type                   | Statement                              | Description                                                                                                                                                                                                           |
|:-----------------------|:---------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| 
| Conditional Statements | `if-else`<br/>`switch-case`                | Execute specific code blocks depending on given conditions.<br/>These statements are the foundation of decision-making in programs.                                                                                   |
| Looping Statements     | `for`<br/>`while`<br/>`do-while`             | Also known as iteration or repetition statements, they repeatedly execute a block of code.<br/>They are commonly used for iterating over lists, reading data, or performing an operation a fixed number of times.     |
| Jump Statements        | `break`<br/>`continue`<br/>`return`<br/>`goto` | Alter the normal flow of execution.<br/>They allow transferring control to other parts of the program when specific conditions are met.                                                                                    |



## Control Flow in C

Let's look at a simple C example:

```c
// control_flow.c

int main(){
    int counter = 0;

    // if
    if (counter < 1) {
        counter++;
    }

    // switch
    switch (counter) {
        case 1:
            counter++;
            break;
    }

    // while loop
    while (counter < 3) {
        counter++;
    }

    // for loop
    for (; counter < 5; counter++) {
    }
    return 0;
}
```

To examine how this program behaves at a lower level, we can compile it and view its assembly output in AT&T syntax:

```shell
cmd > gcc control_flow.c -o control_flow -g
cmd > gdb control_flow
    (gdb) disassemble /s main
    Dump of assembler code for function main:
    control_flow.c:
    1       int main(){
       0x0000000000400536 <+0>:     push   %rbp
       0x0000000000400537 <+1>:     mov    %rsp,%rbp
    
    2           int counter = 0;
       0x000000000040053a <+4>:     movl   $0x0,-0x4(%rbp)
    
    3
    4           // if
    5           if (counter < 1) {
       0x0000000000400541 <+11>:    cmpl   $0x0,-0x4(%rbp)
       0x0000000000400545 <+15>:    jg     0x40054b <main+21>
    
    6               counter++;
       0x0000000000400547 <+17>:    addl   $0x1,-0x4(%rbp)
    
    7           }
    8
    9           // switch
    10          switch (counter) {
       0x000000000040054b <+21>:    cmpl   $0x1,-0x4(%rbp)
       0x000000000040054f <+25>:    jne    0x40055c <main+38>
    
    11              case 1:
    12                  counter++;
       0x0000000000400551 <+27>:    addl   $0x1,-0x4(%rbp)
    
    13                  break;
       0x0000000000400555 <+31>:    nop
    
    14          }
    15
    16          // while loop
    17          while (counter < 3) {
       0x0000000000400556 <+32>:    jmp    0x40055c <main+38>
    
    18              counter++;
       0x0000000000400558 <+34>:    addl   $0x1,-0x4(%rbp)
    
    17          while (counter < 3) {
       0x000000000040055c <+38>:    cmpl   $0x2,-0x4(%rbp)
       0x0000000000400560 <+42>:    jle    0x400558 <main+34>
    
    19          }
    20
    21          // for loop
    22          for (; counter < 5; counter++) {
       0x0000000000400562 <+44>:    jmp    0x400568 <main+50>
       0x0000000000400564 <+46>:    addl   $0x1,-0x4(%rbp)
       0x0000000000400568 <+50>:    cmpl   $0x4,-0x4(%rbp)
       0x000000000040056c <+54>:    jle    0x400564 <main+46>
    
    23          }
    24          return 0;
       0x000000000040056e <+56>:    mov    $0x0,%eax
    
    25      }
       0x0000000000400573 <+61>:    pop    %rbp
       0x0000000000400574 <+62>:    retq
    End of assembler dump.
```

From the assembly output, it’s clear that control flow is essentially a combination of conditional checks and jumps.

The CPU compares values, sets flags, and decides whether to jump to another instruction depending on the result.

Here are some common conditional jump instructions used in assembly:

| Instruction    | Flag               | Description                       |
|:---------------|:-------------------|:----------------------------------|
| `je`  / `jz`   | ZF = 1             | jump if equal                     | 
| `jne` / `jz`   | ZF = 0             | jump if no equal                  | 
| `jg`  / `jnle` | ZF = 0 and SF = OF | jump if greater (signed)          | 
| `jge` / `jnl`  | SF = OF            | jump if greater or equal (signed) | 
| `jl`  / `jnge` | SF ≠ OF            | jump if less (signed)             | 
| `jle` / `jng`  | ZF = 1 or SF ≠ OF  | jump if less or equal(signed)     | 

# Control Flow in Java

The concept of control flow in Java is almost identical to C, though the implementation differs at the bytecode level.

Here’s the same example written in Java:

```java
// HelloWorld.java

public static void controlFlow(){
    int counter = 0;

    // if
    if (counter < 1) {
        counter++;
    }

    // switch
    switch (counter) {
        case 1:
            counter++;
            break;
    }

    // while loop
    while (counter < 3) {
        counter++;
    }

    // for loop
    for (; counter < 5; counter++) {
    }
}
```

After compiling, we can use `javap` to disassemble the bytecode:

```shell
cmd > javap -c HelloWorld

  public static void controlFlow();
    Code:
       0: iconst_0
       1: istore_0
       2: iload_0
       3: iconst_1
       4: if_icmpge     10
       7: iinc          0, 1
      10: iload_0
      11: lookupswitch  { // 1
                     1: 28
               default: 31
          }
      28: iinc          0, 1
      31: iload_0
      32: iconst_3
      33: if_icmpge     42
      36: iinc          0, 1
      39: goto          31
      42: iload_0
      43: iconst_5
      44: if_icmpge     53
      47: iinc          0, 1
      50: goto          42
      53: return
```

At the JVM level, control flow is also implemented through conditional comparisons followed by jump instructions, quite similar to CPU assembly.

Here’s a comparison between common assembly and JVM bytecode jump instructions:

| Assembly Instruction    | Java Bytecode         | Description                             |
|:------------------------|:----------------------|:----------------------------------------|
| `je`  / `jz`            | `ifeq` / `if_icmpeq`  | jump if equal                           | 
| `jne` / `jz`            | `ifne` / `if_icmpne`  | jump if no equal                        | 
| `jg`  / `jnle`          | `ifgt` / `if_icmpgt`  | jump if greater (signed)                | 
| `jge` / `jnl`           | `ifge` / `if_icmpge`  | jump if greater or equal (signed)       | 
| `jl`  / `jnge`          | `iflt` / `if_icmplt`  | jump if less (signed)                   | 
| `jle` / `jng`           | `ifle` / `if_icmple`  | jump if less or equal(signed)           | 