package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ServiceChat {

    private final static int port = 8888;
    private boolean flag = false;          //服务器启动的标志

    //服务器内置客户端连接集合
    private final Collection<client> clients = Collections.synchronizedCollection(new ArrayList<>());

    {
        start();
    }


    public void start() {
        System.out.println("服务器正在启动......");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("服务器启动失败!!!");
            System.exit(0);
        }
        flag = true;
        System.out.println("服务器启动成功!!!");
        while (flag) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();       //阻塞性方法
            } catch (IOException e) {
                System.out.println("服务器连接客户端异常");
                System.exit(0);
            }
            System.out.println(socket.getInetAddress() + "|" + socket.getPort() + "|   连接成功");
            clients.add(new client(socket));       //添加该连接     启动线程
        }
    }

    class client implements Runnable {
        private final Socket socket;

        public client(Socket socket) {
            this.socket = socket;
            new Thread(this).start();    //构造对象启动线程
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (flag) {
                    String str = dis.readUTF(); //服务器接受消息
                    System.out.println(socket.getInetAddress() + "|" + socket.getPort() + ":         " + str);                     //转发给所有客户端
                    clients.forEach((e) -> {   //服务器转发消息
                        if (e.socket != this.socket)
                            e.send(e.socket.getInetAddress() + "|" + e.socket.getPort() + ":         " + str);
                    });
                }
            } catch (SocketException e) {
                System.out.println(socket.getInetAddress() + "|" + socket.getPort() + "|用户下线");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //服务器转发消息
        private void send(String str) {
            try {
                DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
                dos.writeUTF(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ServiceChat();
    }

}
