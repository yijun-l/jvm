# Class File Structure

A Java `.class` file is the result of compiling a `.java` source file. It is a binary file that contains all the necessary information for the JVM to understand, load, and execute the class.

The Java class file format is strictly defined:

```cpp
ClassFile {
    u4             magic;
    u2             minor_version;
    u2             major_version;
    u2             constant_pool_count;
    cp_info        constant_pool[constant_pool_count-1];
    u2             access_flags;
    u2             this_class;
    u2             super_class;
    u2             interfaces_count;
    u2             interfaces[interfaces_count];
    u2             fields_count;
    field_info     fields[fields_count];
    u2             methods_count;
    method_info    methods[methods_count];
    u2             attributes_count;
    attribute_info attributes[attributes_count];
}
```

### Magic Number (4 bytes)

The magic number is a fixed value `0xCAFEBABE` that identifies the file as a valid Java class file.

### Version Information (4 bytes)

The version section records the Java compiler version:

- `minor_version` (2 bytes)

- `major_version` (2 bytes)

This ensures that the JVM can verify compatibility before loading the class.

### Constant Pool

The constant pool is a table of constants used throughout the class, including:

* Literals (e.g., string values, integers)
* Class names
* Field names
* Method names
* Type descriptors

### Access Flags (2 bytes)

The access flags define the properties of the class, such as:

* Public
* Final
* Abstract
* Interface

### This Class and Superclass (2 bytes each)

- `this_class` contains an index into the constant pool that points to the class name.
- `super_class` contains an index into the constant pool that points to the superclass name.

### Interfaces

Interfaces implemented by the class are listed as an array of constant pool indices. 

Each entry refers to an interface type that the class claims to implement.

### Fields

The fields section lists all the variables declared in the class. For each field, the information includes:

* Access flags (such as `private`, `static`)
* Name index
* Descriptor index (type information)
* Additional attributes (such as `ConstantValue`)

### Methods
The methods section provides metadata for each method, including:

* Access flags
* Name index
* Descriptor index (parameter and return types)
* Attributes (such as the method’s bytecode instructions)

Constructors are also included in this section as special methods named `<init>`.

### Attributes

Attributes offer additional metadata about the class, methods, and fields. Common examples include:

* `Code` (containing bytecode for methods)
* `LineNumberTable` (used for debugging)
* `SourceFile` (indicating the original `.java` file)

Each attribute has a name index, a length, and specific content.