package org.example;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;

public class Client extends EncryptedChat{
    private static KeyPair kp;

    static {
        try {
            kp = genKeyPair(2048);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Client() throws NoSuchAlgorithmException {
    }

    public static void main(String[] args)
    {
        try (Socket socket = new Socket("localhost", 25565)) {
            //send
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(kp.getPublic());
            //recv
            InputStream in = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(in);
            PublicKey recvPublicKey = (PublicKey) ois.readObject();
            System.out.println(recvPublicKey);
            while (true) {
                System.out.print(">> ");
                Scanner sc = new Scanner(System.in);
                String toSend = sc.next();
                Message msg = new Message();
                msg.setMessageCrypt(getCryptedMsg(recvPublicKey, toSend.getBytes()));
                oos.writeObject(msg);

                Message recv = (Message) ois.readObject();
                String toPrint = new String(getDecryptedMsg(kp.getPrivate(),recv.getResponseCrypt()));
                System.out.println(toPrint);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
