package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * In the JVM class file format, a descriptor_index exists only in:
 * 1. CONSTANT_NameAndType_info (for name and type of field/method references)
 * 2. method_info (for the method's parameters and return type)
 * 3. field_info (for the field's type)
 *
 */
@Data
public class Descriptor {
    private final String raw;
    private String field;
    private String returnType;

    public Descriptor(String raw) {
        this.raw = raw;
        if (raw.startsWith("(")) {
            // method descriptor
            int end = raw.indexOf(')');
            this.field = raw.substring(1, end);
            this.returnType = raw.substring(end + 1);
        } else {
            // field descriptor
            this.field = raw;
            this.returnType = null;
        }
    }

    public boolean isMethod(){
        return returnType != null;
    }

    public boolean isField(){
        return returnType == null;
    }

    public String methodToString(){
        return returnType + " (" + field + ")";
    }

    public String fieldToString(){
        return field;
    }

    public List<String> parseDescriptor(){
        String descriptorString = this.field;
        List<String> paraTypes = new ArrayList<>();
        for (int i = 0; i < descriptorString.length(); i++){
            char c = descriptorString.charAt(i);

            if ( c == 'L'){
                // object, Ljava/lang/String;
                int end = descriptorString.indexOf(';', i);
                paraTypes.add(descriptorString.substring(i, end + 1));
                i = end;
            } else if (c == '['){
                // array, [[I or [[[java/lang/String;
                int start = i;
                while (descriptorString.charAt(i) == '['){
                    i++;
                }
                if (descriptorString.charAt(i) == 'L'){
                    int end = descriptorString.indexOf(';', i);
                    paraTypes.add(descriptorString.substring(start, end + 1));
                    i = end;
                } else {
                    paraTypes.add(descriptorString.substring(start, i + 1));
                }
            } else {
                // base type, I
                paraTypes.add(String.valueOf(c));
            }
        }
        return paraTypes;
    }
}
