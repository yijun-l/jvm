package com.avaya.jvm.hotspot.share.oops;

import lombok.Data;

import java.io.DataInputStream;
import java.io.IOException;

@Data
public class UnknownAttribute extends AttributeInfo {
    private byte[] info;

    @Override
    public void parse(DataInputStream dis, ConstantPool cp) throws IOException {
        this.setAttributeLength(dis.readInt());
        this.info = new byte[this.attributeLength];
        dis.read(this.info);
    }
}
