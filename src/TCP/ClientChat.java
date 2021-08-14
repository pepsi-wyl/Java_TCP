package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientChat {

    private final static String IP = "127.0.0.1";
    private final static int Port = 8888;
    private Socket socket = null;
    private boolean flag = false;

    {
        connection();
        new Thread(new send()).start();        //输入线程
        new Thread(new receive()).start();     //输出线程
    }

    public void connection() {
        System.out.println("正在连接服务器......");
        try {
            socket = new Socket(IP, Port);
        } catch (IOException e) {
            System.out.println("服务器连接失败!!!");
            System.exit(0);
        }
        flag = true;
        System.out.println("连接服务器成功!!!");
    }

    class send implements Runnable {
        @Override
        public void run() {
            try {
                while (flag) {
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(new Scanner(System.in).next());
                }
            } catch (SocketException e) {
                System.out.println("服务器意外终止!!!");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class receive implements Runnable {
        @Override
        public void run() {
            try {
                while (flag) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    System.out.println(dis.readUTF());
                }
            } catch (SocketException e) {
                System.out.println("服务器意外终止!!!");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
