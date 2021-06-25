package com.javaStudyWay.swingChat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends GlobalVisualInterface implements Runnable {
    private Socket clientSocket;
    private BufferedReader readServer;
    //    private PrintWriter printMessage;
    private BufferedWriter printMessage;
    private String address;

    public static void main(String[] args) {
        new Client().run();
    }

    /**
     * 客户端的界面创建对象,如果为客户端界面则显示输入框加入地址
     *
     * @param title 客户端,用于通用界面的精细化
     * @throws HeadlessException 详见Frame的setTile()
     */
    public Client(String title) throws HeadlessException {
        super(title);
    }

    /**
     * 如果通过空参构造创建客户端会默认传递"客户端"的识别到父类构造
     */
    public Client() {
        super("客户端");
        init();
        address = (String) JOptionPane.showInputDialog(this, "请输入服务器地址",
                "连接到服务器",
                JOptionPane.WARNING_MESSAGE, null, null, "127.0.0.1");
        try {
            clientSocket = new Socket(address, 8888);
            readServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            printMessage = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            mainArea.append("连接服务器失败!\n服务器地址: " + clientSocket.getInetAddress());
        }
        if (clientSocket != null && clientSocket.isConnected()) {
            mainArea.append("连接成功!  服务器ip: " + clientSocket.getInetAddress()
                    + "端口号: " + clientSocket.getPort() + "\n");
        }
    }

    @Override
    //TODO 接收的时候会多两行timeTitle
    public void init() {
        this.setLocation(610, 350);
        super.init();
        /*处理发送按钮的事件*/
        sendMsgBtn.addActionListener(e -> {
            String message = inputMsgArea.getText();
            String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
            String finalMsg = "[客户端]  " + time + "\n" + message + "\n";
            /*消息写入到流传输*/
            try {
                printMessage.write(finalMsg);
                printMessage.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            /*追加到主面板*/
            mainArea.append(finalMsg);
            /*清空输入框重新聚焦*/
            inputMsgArea.setText("");
            inputMsgArea.requestFocus();
        });
    }

    /**
     * 重写通用视图界面的组装方法
     * 在原有的组装过程之前加上连接提示框获取目标服务器地址
     * 通过获取到的服务器地址连接上服务器并创建Socket进行通讯
     * 在组装的基础上进行界面组件的监听事件组装
     */
    @Override
    public void run() {
        String message;
        while (true) {
            try {
                /*接收消息*/
                message = readServer.readLine();
                if (message != null && !"".equals(message.trim())) {
                    String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                    mainArea.append("[服务器]  " + time + "\n" + message + "\n");
                }
            } catch (IOException e) {
                mainArea.append("连接出现异常!\n");
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * 用于"发送"按钮被点击时,获取输入框中的消息发送到服务器端
     *
     * @param message 要发送的消息--输入框中的内容
     */
    public void sendMessage(String message) {
        if (!clientSocket.isConnected()) {
            mainArea.append("与服务器连接中断!\n");
        }
        /*将消息发送到对方端*/
        try {
            printMessage = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            if (message != null && !"".equals(message.trim())) {
                String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                printMessage.write("[客户端]  " + time + "\n" + message + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mainArea.append("消息发送失败,请检查连接\n");
        }

    }

    /**
     * 获取服务端发送来的消息
     *
     * @throws IOException 详见InputStream
     */
    public void acceptMessage() throws IOException {
        String message;
        if (!clientSocket.isConnected()) {
            mainArea.append("与服务器连接中断!\n");
        }
        /*获取对方端发送来的消息*/
        readServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        while (true) {
            message = readServer.readLine();
            if (message != null && !"".equals(message.trim())) {
                String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                mainArea.append("[服务器]  " + time + "\n" + message + "\n");
            }
        }
    }

    @Override
    public void setDefaultCloseOperation(int operation) {
        super.setDefaultCloseOperation(operation);
    }
}
