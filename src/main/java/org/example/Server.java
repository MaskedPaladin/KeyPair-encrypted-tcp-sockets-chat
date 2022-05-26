package org.example;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

public class Server {
    private static int clientCount = 0;
    private static ArrayList<InetAddress> addresses = new ArrayList<InetAddress>();
    private static ArrayList<PublicKey> publicKeys = new ArrayList<PublicKey>();

    public static void main(String[] args) {
        ServerSocket server = null;
        try{
            server = new ServerSocket(25565);
            server.setReuseAddress(true);
            while(true){
                Socket client = server.accept();
                addresses.add(client.getInetAddress());
                clientCount++;
                System.out.println("New client >> " + client.getInetAddress().getHostAddress()+" has been connected");
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static class ClientHandler extends EncryptedChat implements Runnable {
        private final Socket socket;
        public ClientHandler(Socket socket) throws NoSuchAlgorithmException {
            this.socket = socket;
        }
        private static InputStream in;
        private static ObjectInputStream ois;
        private static OutputStream os;
        private static ObjectOutputStream oos;
        @Override
        public void run() {
            KeyPair kp = null;
            try {
                kp = genKeyPair(2048);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            PublicKey publicKey;
            Message message;
            try {
                in = socket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                ois = new ObjectInputStream(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                publicKey = (PublicKey) ois.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println(publicKey);
            publicKeys.add(publicKey);
            try {
                os = socket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                oos = new ObjectOutputStream(os);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                oos.writeObject(kp.getPublic());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while(true){
                try {
                    message = (Message) ois.readObject();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("New message");
                String msg = null;
                try {
                    msg = new String(getDecryptedMsg(kp.getPrivate(),message.getMessageCrypt()));
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(msg);
                    if(msg.equals("!exit")){
                        break;
                    }
                    Message confirmationSend = new Message();
                try {
                    confirmationSend.setResponseCrypt(getCryptedMsg(publicKey, "ok".getBytes()));
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                }
                try {
                    os = socket.getOutputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    oos.writeObject(confirmationSend);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}

