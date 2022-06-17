package client;

import java.io.File;
import java.lang.reflect.Method;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import common.*;

public class ClientMsgType {
    Client client;
    Message msg;

    public ClientMsgType(Client client, Message msg) {
        this.client = client;
        this.msg = msg;
    }

    /**
     * 处理接收到的消息
     */
    public void process() {
        try {
            Method method = this.getClass().getDeclaredMethod(msg.getType()); // 反射获取指定方法
            method.invoke(this);
            client.ui.setText("在线人数:" + (client.ui.getClientCount())); // 动态更新在线人数
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * 聊天消息处理
     */
    public void chat() {
        String toAll = "[public]";
        if (msg.getTo().equals(client.name))
            toAll = "[private]";
        client.ui.append(toAll + msg.getFrom() + ": " + msg.getContent() + "\n");
    }

    /**
     * 文件处理 利用字节数组可以处理所有文件
     */
    public void file() {
        String toAll = "[public]";
        if (msg.getTo().equals(client.name))
            toAll = "[private]";
        client.ui.append(toAll + msg.getFrom() + ": " + msg.getContent() + "\n");
        int confirm = JOptionPane.showConfirmDialog(client.ui,
                "收到了来自" + msg.getFrom() + "的文件:" + msg.getTitle() + "，需要保存吗？");
        if (confirm != JOptionPane.YES_OPTION)
            return;
        // 获取保存路径
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("选择保存路径");
        dlg.setSelectedFile(new File(msg.getTitle()));
        int result = dlg.showSaveDialog(client.ui);
        if (result != JFileChooser.APPROVE_OPTION)
            return;
        File file = dlg.getSelectedFile();
        if (file.exists()) {
            int copy = JOptionPane.showConfirmDialog(null, "是否要覆盖当前文件？", "保存", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (copy == JOptionPane.YES_OPTION) {
                dlg.approveSelection();
            }
        } else {
            dlg.approveSelection();
        }
        if (Common.byte2File(msg.getFile(), file)) {
            JOptionPane.showMessageDialog(client.ui, "文件保存成功");
        } else {
            JOptionPane.showMessageDialog(client.ui, "文件保存失败");
        }
    }

    /**
     * 在线提醒处理
     */
    public void online() {
        client.ui.addClient(msg.getFrom());
        if (msg.getContent() != null)
            client.ui.append(msg.getFrom() + msg.getContent() + "\n");
    }

    /**
     * 下线提醒处理
     */
    public void offline() {
        client.ui.removeClient(msg.getFrom());
        if (msg.getContent() != null)
            client.ui.append(msg.getFrom() + msg.getContent() + "\n");
    }

    /**
     * 登陆出错处理
     */
    public void login() {
        client.ui.setVisible(false);
        JOptionPane.showMessageDialog(client.ui, msg.getContent());
        System.out.println(msg.getContent());
        client.close();
        new Client(Config.IP, Config.PORT);
    }
}
