package chat;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server extends GlobalVisualInterface implements Runnable {
    private ServerSocket serverSocket;
    private BufferedReader readClient;
    //    private PrintWriter printMessage;
    private BufferedWriter printMessage;
    private Socket fromAcceptSocket;


    public static void main(String[] args) {
        new Thread(new Server()).start();
    }

    /**
     * 服务器的界面创建对象,直接从通用界面获取
     *
     * @param title 服务器
     * @throws HeadlessException 详见Frame的setTile()
     */
    public Server(String title) throws HeadlessException {
        super(title);
    }

    /**
     * 如果通过空参构造创建客户端会默认传递"服务器"的识别到父类构造
     */
    public Server() {
        super("服务器");
        init();
        try {
            serverSocket = new ServerSocket(8888);
            fromAcceptSocket = serverSocket.accept();
            if (fromAcceptSocket.isConnected()) {
                mainArea.append("客户端主机上线, Ip地址为: " + fromAcceptSocket.getInetAddress()
                        + " ,端口号: " + fromAcceptSocket.getPort() + "\n");
            }
            readClient = new BufferedReader(new InputStreamReader(fromAcceptSocket.getInputStream()));
            printMessage = new BufferedWriter(new OutputStreamWriter(fromAcceptSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        this.setLocation(500, 260);
        super.init();
        /*处理发送按钮的事件*/
        sendMsgBtn.addActionListener(e -> {
            String message = inputMsgArea.getText();
            String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
            String finalMsg = "[服务器]  " + time + "\n" + message + "\n";
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
     * 监听端口线程
     */
    @Override
    public void run() {
        String message;
        while (true) {
            try {
                /*从socket读取管道信息*/
                message = readClient.readLine();
                /*如果消息不为空再添加*/
                if (message != null && !"".equals(message.trim())) {
                    String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                    mainArea.append("[客户端]  " + time + "\n" + message + "\n");
                }
            } catch (IOException e) {
//                mainArea.append("连接出现异常!\n");
                e.printStackTrace();
                return;
            }
        }
    }


    /**
     * 用于"发送"按钮被点击时,获取输入框中的消息发送到客户端
     *
     * @param message 要发送的消息--输入框中的内容
     */
    public void sendMessage(String message) {
        if (!serverSocket.isBound()) {
            mainArea.append("客户端掉线!\n");
        }
        /*将消息发送到对方端*/
        try {
            printMessage = new BufferedWriter(new OutputStreamWriter(fromAcceptSocket.getOutputStream()));
            if (message != null && !"".equals(message.trim())) {
                String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                printMessage.write("[服务器]  " + time + "\n" + message + "\n");
            }

        } catch (IOException e) {
            mainArea.append("消息发送失败,请检查连接\n");
            e.printStackTrace();
        }
//        printMessage.write(message);
    }

    /**
     * 获取客户端发送来的消息
     *
     * @throws IOException 详见InputStream
     */
    public void acceptMessage() throws IOException {
        String message;
        fromAcceptSocket = serverSocket.accept();
        /*获取对方端发送来的消息*/
        readClient = new BufferedReader(new InputStreamReader(fromAcceptSocket.getInputStream()));
        while (true) {
            message = readClient.readLine();
            if (message != null && !"".equals(message.trim())) {
                String time = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                mainArea.append("[客户端]  " + time + "\n" + message + "\n");
            }
        }
    }


}
