package com.patterncat.protocol;

import java.io.*;

/**
 * Created by patterncat on 2016-02-19.
 */
public class XProtocol {

    public static void encode(OutputStream outputStream,XMessage xMessage) throws IOException {
        //write encoding
        DataOutputStream dout = new DataOutputStream(outputStream);
        dout.writeInt(xMessage.getEncode().ordinal());
        //write content length
        dout.writeInt(xMessage.getLength());
        //write content
        dout.write(xMessage.getContent().getBytes(xMessage.getEncode().desc));
    }

    public static XMessage decode(InputStream input) throws IOException {
        DataInputStream din = new DataInputStream(input);
        XMessage xMessage = new XMessage();
        //get encoding
        int encode = din.readInt();
        EncodeEnum encodeEnum = EncodeEnum.getByIdx(encode);
        xMessage.setEncode(encodeEnum);
        //get content length
        int length = din.readInt();
        xMessage.setLength(length);
        //get content
        byte[] data = new byte[length];
        din.read(data);
        String content = new String(data,encodeEnum.desc);
        xMessage.setContent(content);
        return xMessage;
    }

}
