# Exception Handling

Robust code must gracefully handle errors. In programming, this is often done using **error codes** or a more modern **exception-handling** system.

## The Traditional Approach: Error Codes

Before modern exception handling became widespread, developers often relied on return codes to signal errors. Let's look at a simple network server in C to see this in action. 

Each critical function call, like `socket()`, `bind()`, or `listen()`, returns a value. We must manually check this value after every call to see if an error occurred.

```c
// server.c

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

int main() {
    int sockfd, clientfd;
    struct sockaddr_in addr;
    char buf[100];
    int ret;

    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) { 
        perror("socket"); 
        exit(1); 
    }

    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_port = htons(8080);

    ret = bind(sockfd, (struct sockaddr*)&addr, sizeof(addr));
    if (ret < 0) { 
        perror("bind"); 
        close(sockfd); 
        exit(1); 
    }

    ret = listen(sockfd, 1);
    if (ret < 0) { 
        perror("listen"); 
        close(sockfd); 
        exit(1); 
    }

    clientfd = accept(sockfd, NULL, NULL);
    if (clientfd < 0) { 
        perror("accept"); 
        close(sockfd); 
        exit(1); 
    }

    ret = read(clientfd, buf, sizeof(buf)-1);
    if (ret < 0) {
        perror("read");
    } else {
        printf("Client says: %s\n", buf);
    }

    ret = write(clientfd, "Hi\n", 3);
    if (ret < 0) {
        perror("write");
    }

    close(clientfd);
    close(sockfd);
    return 0;
}
```

Compile and test:

```shell
## server
cmd > gcc server.c -o server
cmd > ./server &
  [1] 3659
cmd > ss -lnp | grep 8080
  tcp LISTEN  0 1 0.0.0.0:8080  0.0.0.0:* users:(("server",pid=3659,fd=3))
  
  Client says: message

## client 
cmd > nc 192.168.70.47 8080
  message
  Hi
```

## The Modern Approach: Exception-handling

Java introduced a more elegant and powerful mechanism: **exception handling**. It separates the error-handling code from the main program logic, making the code cleaner and more robust.

Let's implement the same server in Java:


```java
import java.io.*;
import java.net.*;

public class SimpleServer {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8080);
             Socket client = server.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            String msg = in.readLine();
            System.out.println("Client says: " + msg);
            out.println("Hi");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

Compile and test:

```shell
## server
cmd > javac Server.java
cmd > java Server &
  [1] 40081
cmd > ss -lnp | grep 8080
  tcp LISTEN  0 50  *:8080  *:* users:(("java",pid=40081,fd=6))
  
  Client says: message

## client
cmd > nc 192.168.70.47 8080
  message
  Hi
```

## Java Exception Handling

In Java, an exception is an object that represents an error or an abnormal condition. When such a condition occurs, an exception is "thrown." This disrupts the normal flow of the program and transfers control to a dedicated block of code called an exception handler.

### The try, catch, and finally Blocks

- `try`: You place the code that might throw an exception inside the `try` block.

- `catch`: This block follows a `try` block. It "catches" an exception of a specific type and contains the code to handle it. You can have multiple `catch` blocks to handle different types of exceptions.

- `finally`: This block is optional. The code inside `finally` is always executed, regardless of whether an exception was thrown or caught. It's typically used for cleanup tasks, like closing files or releasing database connections (though **try-with-resources** is often better for this).

### How it works inside JVM

Consider a simple example:

```java
public class Exception {
    public static void main(String[] args) {
        try {
            throw new RuntimeException("This is a manually thrown exception"); 
        } catch (RuntimeException e) {
            System.out.println("Caught exception: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed");
        }
    }
}
```

The JVM uses an "Exception Table" associated with the method's bytecode.

```shell
cmd > javac Exception.java
cmd > java Exception
  Caught exception: This is a manually thrown exception
  Finally block executed


cmd > javap -v Exception.class
  Code:
      stack=3, locals=3, args_size=1
         0: new           #2                  // class java/lang/RuntimeException
         3: dup
         4: ldc           #3                  // String This is a manually thrown exception
         6: invokespecial #4                  // Method java/lang/RuntimeException."<init>":(Ljava/lang/String;)V
         9: athrow
        10: astore_1
        11: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
        14: aload_1
        15: invokevirtual #6                  // Method java/lang/RuntimeException.getMessage:()Ljava/lang/String;
        18: invokedynamic #7,  0              // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
        23: invokevirtual #8                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        26: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
        29: ldc           #9                  // String Finally block executed
        31: invokevirtual #8                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        34: goto          48
        37: astore_2
        38: getstatic     #5                  // Field java/lang/System.out:Ljava/io/PrintStream;
        41: ldc           #9                  // String Finally block executed
        43: invokevirtual #8                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        46: aload_2
        47: athrow
        48: return
      Exception table:
         from    to  target type
             0    10    10   Class java/lang/RuntimeException
             0    26    37   any
```

Here is a simplified breakdown of the process:
1. **Exception Thrown**: When an `ATHROW` instruction is executed, the JVM stops normal execution.
2. **Check Exception Table**: The JVM looks at the current method's Exception Table. This table has entries defining a code range (**from**, **to**), a handler location (**target**), and the exception type (**type**).
3. **Find a Match**: The JVM checks if the instruction pointer's current location is within a **from**-**to** range and if the thrown exception matches the **type** in that entry.
4. **Jump to Handler**: If a match is found, the program counter jumps to the **target** address, and execution resumes inside the `catch` block. The `finally` block's logic is also managed through this table to ensure its execution.
5. **Unwind the Stack**: If no match is found in the current method, the JVM "unwinds" the call stack. It pops the current method's stack frame and repeats the process in the calling method.
6. **Program Termination**: If the exception travels all the way up the call stack to the `main` method and is still not handled, the thread terminates, and the exception's stack trace is printed to the console.