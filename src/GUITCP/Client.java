package GUITCP;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

@SuppressWarnings("all")
public class Client extends JFrame {

    //GUI配置信息
    private final JTextArea textArea = new JTextArea(10, 20);
    private final JScrollPane jScrollPane = new JScrollPane(textArea);
    private final JTextField textField = new JTextField(20);

    //客户端连接服务器信息
    private Socket socket = null;
    private final String conIP = "192.168.131.1";
    private final int conPort = 8888;
    private boolean flag = false;
    //客户端发送信息
    private DataOutputStream dos = null;

    {
        this.setTitle("客户端聊天窗口");                      //设置聊天框的Title
        this.add(jScrollPane, BorderLayout.CENTER);           //添加组件
        this.add(textField, BorderLayout.SOUTH);           //添加组件
        this.setBounds(300, 300, 300, 400); //设置长宽高

        textArea.setEditable(false);                          //设置查看文本框不能编辑
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //GUI关闭后程序结束


        //监听输入文本框的数据
        textField.addActionListener((e) -> {
            String textFieldSend = textField.getText();   //得到数据
            if (textFieldSend.trim().length() == 0)       //只输入空格的时候
                return;
            textField.setText("");                        //将输入文本框置空
            send(textFieldSend);

        });

        //连接服务器
        conection();

        this.setVisible(true);
        new Thread(new receive()).start();
    }

    //客户端连接服务器方法
    public void conection() {
        try {
            socket = new Socket(conIP, conPort);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //客户端发送信息方法
    public void send(String str) {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class receive implements Runnable {
        @Override
        public void run() {
            try {
                while (flag == true) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    textArea.append(dis.readUTF() + '\n');
                }
            } catch (SocketException e) {
                textArea.append("服务器意外终止!!!\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client();
        new Client();
        new Client();
        new Client();
        new Client();
    }

}
