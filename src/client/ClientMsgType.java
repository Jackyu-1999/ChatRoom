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
     * ������յ�����Ϣ
     */
    public void process() {
        try {
            Method method = this.getClass().getDeclaredMethod(msg.getType()); // �����ȡָ������
            method.invoke(this);
            client.ui.setText("��������:" + (client.ui.getClientCount())); // ��̬������������
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * ������Ϣ����
     */
    public void chat() {
        String toAll = "[public]";
        if (msg.getTo().equals(client.name))
            toAll = "[private]";
        client.ui.append(toAll + msg.getFrom() + ": " + msg.getContent() + "\n");
    }

    /**
     * �ļ����� �����ֽ�������Դ��������ļ�
     */
    public void file() {
        String toAll = "[public]";
        if (msg.getTo().equals(client.name))
            toAll = "[private]";
        client.ui.append(toAll + msg.getFrom() + ": " + msg.getContent() + "\n");
        int confirm = JOptionPane.showConfirmDialog(client.ui,
                "�յ�������" + msg.getFrom() + "���ļ�:" + msg.getTitle() + "����Ҫ������");
        if (confirm != JOptionPane.YES_OPTION)
            return;
        // ��ȡ����·��
        JFileChooser dlg = new JFileChooser();
        dlg.setDialogTitle("ѡ�񱣴�·��");
        dlg.setSelectedFile(new File(msg.getTitle()));
        int result = dlg.showSaveDialog(client.ui);
        if (result != JFileChooser.APPROVE_OPTION)
            return;
        File file = dlg.getSelectedFile();
        if (file.exists()) {
            int copy = JOptionPane.showConfirmDialog(null, "�Ƿ�Ҫ���ǵ�ǰ�ļ���", "����", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (copy == JOptionPane.YES_OPTION) {
                dlg.approveSelection();
            }
        } else {
            dlg.approveSelection();
        }
        if (Common.byte2File(msg.getFile(), file)) {
            JOptionPane.showMessageDialog(client.ui, "�ļ�����ɹ�");
        } else {
            JOptionPane.showMessageDialog(client.ui, "�ļ�����ʧ��");
        }
    }

    /**
     * �������Ѵ���
     */
    public void online() {
        client.ui.addClient(msg.getFrom());
        if (msg.getContent() != null)
            client.ui.append(msg.getFrom() + msg.getContent() + "\n");
    }

    /**
     * �������Ѵ���
     */
    public void offline() {
        client.ui.removeClient(msg.getFrom());
        if (msg.getContent() != null)
            client.ui.append(msg.getFrom() + msg.getContent() + "\n");
    }

    /**
     * ��½������
     */
    public void login() {
        client.ui.setVisible(false);
        JOptionPane.showMessageDialog(client.ui, msg.getContent());
        System.out.println(msg.getContent());
        client.close();
        new Client(Config.IP, Config.PORT);
    }
}
