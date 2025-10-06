package com.avaya.jvm.hotspot.share.interpreter;

import com.avaya.jvm.hotspot.share.memory.StackObj;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Base bytecode stream class.
 * Responsible only for traversing a byte array, without method or CodeAttribute context.
 */
@Getter
public class BaseBytecodeStream extends StackObj {

    protected int index;
    protected final byte[] codes;

    public BaseBytecodeStream(byte[] codes) {
        this.codes = codes;
        this.index = 0;
    }

    public int getU1() {
        ensureAvailable(1);
        return Byte.toUnsignedInt(codes[index++]);
    }

    public int getU2(){
        ensureAvailable(2);
        int value =  ((codes[index] & 0xFF) << 8) | (codes[index + 1] & 0xFF);
        index += 2;
        return value;
    }

    public int getU4(){
        ensureAvailable(4);
        int value = ByteBuffer.wrap(codes, index, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        index += 4;
        return value;
    }

    /** Check if there are at least 'n' bytes available to read */
    protected void ensureAvailable(int n){
        if (index + n > codes.length){
            throw new IndexOutOfBoundsException(
                    "Bytecode index out of bounds: index = " + index + ", need=" + n
            );
        }
    }

    public boolean end(){
        return index >= codes.length;
    }

    public void resetIndex(){
        this.index = 0;
    }
}
