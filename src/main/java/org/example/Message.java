package org.example;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    public Message() {
    }
    private byte[] responseCrypt;
    private byte[] messageCrypt;

    public byte[] getResponseCrypt() {
        return responseCrypt;
    }

    public void setResponseCrypt(byte[] responseCrypt) {
        this.responseCrypt = responseCrypt;
    }

    public byte[] getMessageCrypt() {
        return messageCrypt;
    }

    public void setMessageCrypt(byte[] messageCrypt) {
        this.messageCrypt = messageCrypt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "responseCrypt=" + Arrays.toString(responseCrypt) +
                ", messageCrypt=" + Arrays.toString(messageCrypt) +
                '}';
    }
}
