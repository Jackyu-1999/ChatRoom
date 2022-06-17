package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import common.*;

public class Client extends Thread {
    public ClientUI ui; // 客户端界面
    public String name; // 用户名
    private Socket csocket; // 客户端套接字
    private ObjectInputStream in; // 输入流
    private ObjectOutputStream out; // 输出流

    public static void main(String[] args) {
        new Client(Config.IP, Config.PORT);
    }

    public Client(String server, int port) {
        try {
            csocket = new Socket(server, port);
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
        } catch (Exception e) {
            System.out.println(e.toString() + " 客户端初始化失败");
            this.close();
            System.exit(0);
        }
        this.start();
    }

    public void run() {
        try {
            // 获取用户名
            while (name == null || name.equals(""))
                name = JOptionPane.showInputDialog("请输入名字").trim();
            // 第一次运行，发送登陆消息
            send(new Message("login", name, null, null));
            // 显示界面
            ui = new ClientUI(this, name + " 的客户端");
            // 消息处理主循环
            while (true) {
                Message rcv = (Message) in.readObject(); // 服务器端发来的消息
                System.out.println(rcv.toString());
                new ClientMsgType(this, rcv).process();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " 连接中断");
            close();
        }
    }

    /**
     * 客户端发送消息给服务器
     *
     * @param msg
     *            消息对象
     */
    public void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            System.out.println(e.toString() + " 消息转换出错");
        }
    }

    /**
     * 关闭所有资源
     */
    public void close() {
        try {
            if (ui != null)
                ui.dispose();
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (csocket != null)
                csocket.close();
        } catch (IOException e) {
            System.out.println(e.toString() + " 资源无法释放");
        }
    }
}

/**
 * 客户端界面类
 *
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
class ClientUI extends Frame {
    JTextArea msgArea; // 显示消息文本
    JTextField msgField; // 输入消息文本
    JComboBox<String> clientList; // 在线用户列表
    JComboBox<String> msgType;	// 聊天文本或者文件
    JButton btn; // 发送消息按钮
    JLabel cntLabel; // 统计在线人数
    JScrollPane textAreaScrollPane;
    JPanel textFieldPanel = new JPanel();
    Client client;

    public ClientUI(Client client, String winname) {
        super(winname); // 继承父类的名字
        setSize(900, 600);
        this.client = client;
        // 消息文本显示区域
        msgArea = new JTextArea(400, 400);
        msgArea.setEditable(false);
        textAreaScrollPane = new JScrollPane(msgArea);
        add(textAreaScrollPane, BorderLayout.CENTER);
        // 发送消息区域
        textFieldPanel.setLayout(new FlowLayout(0, 10, 10));
        add(textFieldPanel, BorderLayout.SOUTH);

        clientList = new JComboBox<String>(); // 在线用户列表
        clientList.addItem("All");
        textFieldPanel.add(clientList);

        msgType = new JComboBox<String>();	// 聊天文本或者文件
        msgType.addItem("chat");
        msgType.addItem("file");
        textFieldPanel.add(msgType);

        msgField = new JTextField(20); // 输入消息文本
        textFieldPanel.add(msgField);

        btn = new JButton("发送"); // 发送消息按钮
        btn.setMnemonic(KeyEvent.VK_ENTER);
        textFieldPanel.add(btn);

        cntLabel = new JLabel("在线人数:1"); // 显示在线人数
        textFieldPanel.add(cntLabel);
        // 发送消息按钮监听器
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) msgType.getSelectedItem();
                String title = "";
                String content = getText().trim();
                byte[] fbyte = null;
                if (content.equals("")) {
                    JOptionPane.showMessageDialog(client.ui, "输入不能为空");
                    return;
                }
                if (type.equals("file")) {
                    // 获取客户端发送文件
                    JFileChooser dlg = new JFileChooser();
                    dlg.setDialogTitle("选择文件");
                    int result = dlg.showOpenDialog(client.ui);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File file = dlg.getSelectedFile();
                        fbyte = Common.file2Byte(file);
                        title = file.getName();
                    } else {
                        return;
                    }
                    append("TO " + getName() + ": " + title + "\n");
                }
                append("TO " + getName() + ": " + content + "\n");
                client.send(new Message(type, client.name, getName(), title, content, fbyte));
                clear();
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(client.ui, "确定退出吗?")) {
                    client.close();
                    System.exit(0);
                }
            }
        });
        setVisible(true);
    }

    /**
     * 获得输入框中的消息文本
     *
     * @return
     */
    public String getText() {
        return msgField.getText().trim();
    }

    /**
     * 设置cntLabel文本以更新在线用户数
     * @param txt
     */
    public void setText(String txt) {
        cntLabel.setText(txt);
    }

    /**
     * 获得选中的聊天对象的名字
     */
    public String getName() {
        return (String) clientList.getSelectedItem();
    }

    /**
     * 获得在线用户数
     *
     * @return
     */
    public int getClientCount() {
        return clientList.getItemCount();
    }

    /**
     * 添加在线用户
     *
     * @param name
     *            用户名
     */
    public void addClient(String name) {
        clientList.addItem(name);
    }

    /**
     * 删除在线用户
     *
     * @param name
     *            用户名
     */
    public void removeClient(String name) {
        clientList.removeItem(name);
    }

    /**
     * 清空输入框
     */
    public void clear() {
        msgField.setText(" ");
    }

    /**
     * 添加文本到消息显示区域
     *
     * @param txt
     *            消息文本
     */
    public void append(String txt) {
        msgArea.append(txt);
    }
}
