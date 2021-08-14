package GUITCP;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

@SuppressWarnings("all")
public class Service extends JFrame {

    //GUI配置信息
    private final JTextArea serviceTextArea = new JTextArea();
    private final JScrollPane jScrollPane = new JScrollPane(serviceTextArea);
    private final JPanel buttonTool = new JPanel();         //管理按钮
    private final JButton startButton = new JButton("启动");
    private final JButton stopButton = new JButton("停止");

    //服务器配置信息
    private final int port = 8888;
    private ServerSocket serverSocket = null;
    Socket socket = null;
    private boolean flag = false;

    //服务器内置客户端连接集合
    private final ArrayList<client> clients = new ArrayList<>();


    {
        this.setTitle("服务器端");
        this.add(jScrollPane, BorderLayout.CENTER);  //添加组件
        buttonTool.add(startButton);
        buttonTool.add(stopButton);
        this.add(buttonTool, BorderLayout.SOUTH);        //添加组件
        this.setBounds(0, 0, 400, 400);     //设置长宽高
        serviceTextArea.setEditable(false);              //设置查看文本框不能编辑
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //GUI关闭后程序结束

        this.setVisible(true);

        startButton.addActionListener((e) -> {           //监听按钮 启动服务器
            flag = true;
            serviceTextArea.append("服务器正在启动......\n");
        });

        stopButton.addActionListener((e) -> {            //监听按钮  关闭服务器
            try {
                stopService();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        });

        try {
            startService();
        } catch (Exception f) {
            f.printStackTrace();
        }

    }

    //服务器启动
    public void startService() throws Exception {
        serviceTextArea.append("警告!!!服务器没有启动!!!\n");
        while (flag != true) {
            Thread.sleep(10);     //进行阻塞
        }
        serverSocket = new ServerSocket(port);
        serviceTextArea.append("服务器启动成功!!!\n");
        try {
            while (flag == true) {
                socket = serverSocket.accept();
                clients.add(new client(socket));
                serviceTextArea.append("" + socket.getInetAddress() + socket.getPort() + "连接服务器\n");
            }
        } catch (SocketException e) {
        }
    }

    //服务器关闭
    public void stopService() throws Exception {
        flag = false;
        if (socket != null)
            socket.close();
        if (serverSocket != null)
            serverSocket.close();
        System.exit(0);
    }


    //内置内部类
    class client implements Runnable {
        Socket socket = null;

        public client(Socket socket) {
            this.socket = socket;
            new Thread(this).start();          //启动该线程
        }

        @Override
        public void run() {
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (flag = true) {
                    String news = socket.getInetAddress() + "|" + socket.getPort() + "|说|" + dis.readUTF() + '\n';  //readUTF()阻塞性方法
                    serviceTextArea.append(news);
                    clients.forEach((e) -> e.send(news));
                }
            } catch (SocketException e) {
                serviceTextArea.append(socket.getInetAddress() + "|" + socket.getPort() + " 客户端下线\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //服务器转发雄消息
        public void send(String news) {
            try {
                DataOutputStream dos = new DataOutputStream(this.socket.getOutputStream());
                dos.writeUTF(news);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Service();
    }


}
