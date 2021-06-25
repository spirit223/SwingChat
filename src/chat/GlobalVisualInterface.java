package chat;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * 客户端/服务器通用视图界面
 */
public class GlobalVisualInterface extends JFrame {
    /**
     * 创建一个通用界面时需要指明该界面是客户端还是服务器
     *
     * @param title 指定客户端/服务器
     * @throws HeadlessException 详见Frame的setTile()
     */
    public GlobalVisualInterface(String title) throws HeadlessException {
        super(title);
    }

    /*菜单栏*/
    JMenuBar menuBar = new JMenuBar();
    /*菜单项*/
    JMenu menu = new JMenu("记录功能");
    /*菜单功能选项*/
    JMenuItem saveMessageMenuItem = new JMenuItem("保存聊天记录");
    JMenuItem showMessageMenuItem = new JMenuItem("查看聊天记录");
    /*主界面*/
    JScrollPane mainAreaScroll = new JScrollPane();
    JTextArea mainArea = new JTextArea(15, 50);
    /*容纳底部的输入框和发送按钮的容器*/
    JScrollPane inputAreaScroll = new JScrollPane();
    JPanel inputAndSendPanel = new JPanel(new BorderLayout());
    JTextArea inputMsgArea = new JTextArea(3, 43);
    JButton sendMsgBtn = new JButton("发送");
    /*分割面板*/
    JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

    /**
     * 组装所有组件,每次使用这个界面都需要先调用或者重写该方法
     */
    public void init() {
        /*分割容器*/
        jsp.setLeftComponent(mainAreaScroll);
        jsp.setRightComponent(inputAndSendPanel);
        /*随拖动改变大小*/
        jsp.setContinuousLayout(true);
        /*分割条位置*/
        jsp.setDividerLocation(0.2);
        /*设置分割条大小*/
        jsp.setDividerSize(2);

        /*组装菜单*/
        menu.add(saveMessageMenuItem);
        menu.addSeparator();
        menu.add(showMessageMenuItem);
        menuBar.add(menu);
        /*处理聊天显示界面和滚动*/
        mainArea.setEditable(false);
        mainArea.setLineWrap(true);
        mainAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainAreaScroll.setViewportView(mainArea);
        /*底部消息输入和发送*/
        inputMsgArea.setLineWrap(true);
        inputAreaScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        inputAreaScroll.setViewportView(inputMsgArea);
        inputAndSendPanel.add(inputAreaScroll, BorderLayout.CENTER);
        inputAndSendPanel.add(sendMsgBtn, BorderLayout.EAST);
        /*组装主界面*/
        setJMenuBar(menuBar);
        add(jsp);

        pack();
        loadListener();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * 对所有组件进行事件加载
     */
    public void loadListener() {
        /*菜单功能*/
        //保存聊天记录
        saveMessageMenuItem.addActionListener((actionEvent) -> {
            /*获取到聊天界面的所有消息并转换成可序列化的消息对象*/
            String text = mainArea.getText();
            Message message = new Message(text);
            File historyMsgToTXT = new File(new File("").getAbsolutePath(), "historyMessage.txt");
            File historyMsgToDat = new File(new File("").getAbsolutePath(), "historyMessage.dat");
            FileWriter writeToTXT = null;
            ObjectOutputStream writeToDat = null;
            /*如果文件不存在则创建*/
            try {
                if (!historyMsgToDat.exists()) {
                    historyMsgToDat.createNewFile();
                }
                if (!historyMsgToTXT.exists()) {
                    historyMsgToTXT.createNewFile();
                }
            } catch (IOException e) {
                /*文件创建失败*/
                mainArea.append("\n\t消息文件创建失败!\n");
            }
            /*保存记录的两个对象*/
            try {
                mainArea.append("\n正在保存聊天记录...\n");
                /*true -- 追加末尾方式写入*/
                writeToTXT = new FileWriter(historyMsgToTXT, true);
                /*序列化对象*/
                writeToDat = new ObjectOutputStream(new FileOutputStream(historyMsgToDat));
                writeToTXT.append(text);
                writeToDat.writeObject(message);
                mainArea.append("\n保存聊天记录成功!\n");
            } catch (IOException e) {
                e.printStackTrace();
                mainArea.append("文件打开失败或写入发生错误!\n\t聊天记录保存失败!\n");
            } finally {
                try {
                    if (writeToDat != null && writeToTXT != null) {
                        writeToTXT.flush();
                        writeToDat.flush();
                    }
                    writeToDat.close();
                    writeToTXT.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //查看聊天记录
        showMessageMenuItem.addActionListener((actionEvent) -> {
            try {
                File historyInDat = new File(new File("").getAbsolutePath(), "historyMessage.dat");
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(historyInDat));
                Message hisMes = (Message) ois.readObject();
                mainArea.append("\n-------历史消息--------\n" + hisMes.getMessage() + "\n" + "==========以上为历史消息==========\n");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
