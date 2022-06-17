package client;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import common.*;

public class Client extends Thread {
    public ClientUI ui; // �ͻ��˽���
    public String name; // �û���
    private Socket csocket; // �ͻ����׽���
    private ObjectInputStream in; // ������
    private ObjectOutputStream out; // �����

    public static void main(String[] args) {
        new Client(Config.IP, Config.PORT);
    }

    public Client(String server, int port) {
        try {
            csocket = new Socket(server, port);
            out = new ObjectOutputStream(csocket.getOutputStream());
            in = new ObjectInputStream(csocket.getInputStream());
        } catch (Exception e) {
            System.out.println(e.toString() + " �ͻ��˳�ʼ��ʧ��");
            this.close();
            System.exit(0);
        }
        this.start();
    }

    public void run() {
        try {
            // ��ȡ�û���
            while (name == null || name.equals(""))
                name = JOptionPane.showInputDialog("����������").trim();
            // ��һ�����У����͵�½��Ϣ
            send(new Message("login", name, null, null));
            // ��ʾ����
            ui = new ClientUI(this, name + " �Ŀͻ���");
            // ��Ϣ������ѭ��
            while (true) {
                Message rcv = (Message) in.readObject(); // �������˷�������Ϣ
                System.out.println(rcv.toString());
                new ClientMsgType(this, rcv).process();
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " �����ж�");
            close();
        }
    }

    /**
     * �ͻ��˷�����Ϣ��������
     *
     * @param msg
     *            ��Ϣ����
     */
    public void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            System.out.println(e.toString() + " ��Ϣת������");
        }
    }

    /**
     * �ر�������Դ
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
            System.out.println(e.toString() + " ��Դ�޷��ͷ�");
        }
    }
}

/**
 * �ͻ��˽�����
 *
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
class ClientUI extends Frame {
    JTextArea msgArea; // ��ʾ��Ϣ�ı�
    JTextField msgField; // ������Ϣ�ı�
    JComboBox<String> clientList; // �����û��б�
    JComboBox<String> msgType;	// �����ı������ļ�
    JButton btn; // ������Ϣ��ť
    JLabel cntLabel; // ͳ����������
    JScrollPane textAreaScrollPane;
    JPanel textFieldPanel = new JPanel();
    Client client;

    public ClientUI(Client client, String winname) {
        super(winname); // �̳и��������
        setSize(900, 600);
        this.client = client;
        // ��Ϣ�ı���ʾ����
        msgArea = new JTextArea(400, 400);
        msgArea.setEditable(false);
        textAreaScrollPane = new JScrollPane(msgArea);
        add(textAreaScrollPane, BorderLayout.CENTER);
        // ������Ϣ����
        textFieldPanel.setLayout(new FlowLayout(0, 10, 10));
        add(textFieldPanel, BorderLayout.SOUTH);

        clientList = new JComboBox<String>(); // �����û��б�
        clientList.addItem("All");
        textFieldPanel.add(clientList);

        msgType = new JComboBox<String>();	// �����ı������ļ�
        msgType.addItem("chat");
        msgType.addItem("file");
        textFieldPanel.add(msgType);

        msgField = new JTextField(20); // ������Ϣ�ı�
        textFieldPanel.add(msgField);

        btn = new JButton("����"); // ������Ϣ��ť
        btn.setMnemonic(KeyEvent.VK_ENTER);
        textFieldPanel.add(btn);

        cntLabel = new JLabel("��������:1"); // ��ʾ��������
        textFieldPanel.add(cntLabel);
        // ������Ϣ��ť������
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = (String) msgType.getSelectedItem();
                String title = "";
                String content = getText().trim();
                byte[] fbyte = null;
                if (content.equals("")) {
                    JOptionPane.showMessageDialog(client.ui, "���벻��Ϊ��");
                    return;
                }
                if (type.equals("file")) {
                    // ��ȡ�ͻ��˷����ļ�
                    JFileChooser dlg = new JFileChooser();
                    dlg.setDialogTitle("ѡ���ļ�");
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
                if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(client.ui, "ȷ���˳���?")) {
                    client.close();
                    System.exit(0);
                }
            }
        });
        setVisible(true);
    }

    /**
     * ���������е���Ϣ�ı�
     *
     * @return
     */
    public String getText() {
        return msgField.getText().trim();
    }

    /**
     * ����cntLabel�ı��Ը��������û���
     * @param txt
     */
    public void setText(String txt) {
        cntLabel.setText(txt);
    }

    /**
     * ���ѡ�е�������������
     */
    public String getName() {
        return (String) clientList.getSelectedItem();
    }

    /**
     * ��������û���
     *
     * @return
     */
    public int getClientCount() {
        return clientList.getItemCount();
    }

    /**
     * ��������û�
     *
     * @param name
     *            �û���
     */
    public void addClient(String name) {
        clientList.addItem(name);
    }

    /**
     * ɾ�������û�
     *
     * @param name
     *            �û���
     */
    public void removeClient(String name) {
        clientList.removeItem(name);
    }

    /**
     * ��������
     */
    public void clear() {
        msgField.setText(" ");
    }

    /**
     * ����ı�����Ϣ��ʾ����
     *
     * @param txt
     *            ��Ϣ�ı�
     */
    public void append(String txt) {
        msgArea.append(txt);
    }
}
