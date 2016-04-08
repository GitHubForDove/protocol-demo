package com.patterncat.protocol;

/**
 * Created by patterncat on 2016-02-19.
 */
public class XMessage {

    private EncodeEnum encode;

    private int length;

    private String content;

    public void writeUtf8(String msg){
        this.encode = EncodeEnum.UTF8;
        this.content = msg;
        this.length = msg.length();
    }

    public EncodeEnum getEncode() {
        return encode;
    }

    public void setEncode(EncodeEnum encode) {
        this.encode = encode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "XMessage{" +
                "encode=" + encode +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }
}
