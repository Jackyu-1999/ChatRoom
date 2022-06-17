package server;

import java.io.*;

import java.util.*;

import common.*;

import java.net.*;

public class Server {

    public Server(int port) {
        try {
            @SuppressWarnings("resource")
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket conn = server.accept(); // �����ͻ����׽���
                new Thread(new ServerHandler(conn)).start(); // �½��̺߳Ϳͻ��˽���ȫ˫��ͨ��
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        new Server(Config.PORT);
    }
}

class ServerHandler implements Runnable {
    private ObjectInputStream in; // ������
    private ObjectOutputStream out; // �����
    private String name; // �û���
    private static ArrayList<ServerHandler> clientList = new ArrayList<ServerHandler>(); // ���û��̷߳����������
    private static HashMap<String, ServerHandler> clientMap = new HashMap<>(); // ���û����������̹߳������ڲ���

    public ServerHandler(Socket socket) {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.toString() + " ����˳�ʼ��ʧ��");
        }
    }

    public void run() {
        if (!loginVerify())
            return; // ��½У��
        try {
            addClient(); // �ͻ��˾�����½���� ��ӵ��б�
            sendMessage(new Message("online", name, "All", "������!")); // �㲥�û��б�
            while (true) {
                Message rcv = (Message) in.readObject(); // �ȴ��ͻ�����Ϣ
                sendMessage(rcv);
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " �ͻ������˳�");
        } finally {
            removeClient();
            sendMessage(new Message("offline", name, "All", "�����ˣ�")); // �㲥����֪ͨ
        }
    }

    /**
     * ��½У��
     *
     * @return
     */
    private Boolean loginVerify() {
        try {
            Message rcv = (Message) in.readObject();
            System.out.println(rcv.toString());
            this.name = rcv.getFrom();
            if (clientMap.containsKey(name)) {
                // �û����ظ�
                sendToClient(this, new Message("login", null, name, "�û����Ѵ���"));
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "��½����ʧ��");
            return false;
        }
        return true;
    }

    /**
     * ����û����б�
     *
     * @return
     */
    private Boolean addClient() {
        if (clientMap.containsKey(name))
            return false;
        clientList.add(this);
        clientMap.put(name, this);
        return true;
    }

    /**
     * �Ƴ���ǰ�û�
     *
     * @return
     */
    private Boolean removeClient() {
        clientList.remove(this);
        clientMap.remove(name);
        return true;
    }

    /**
     * ������Ϣ
     *
     * @param msg
     * @throws IOException
     */
    public void sendMessage(Message msg) {
        new ServerMsgType(this, msg).process();
    }

    /**
     * ������Ϣ�������û�,�ų�name
     *
     * @param msg
     */
    public void sendAllClient(Message msg) {
        String name = msg.getFrom();
        ServerHandler sh = clientMap.get(name);
        synchronized (clientList) {
            for (ServerHandler client : clientList) {
                if (client != sh) {
                    sendToClient(client, msg);
                }
            }
        }
    }

    /**
     * ������Ϣ��ָ���û�
     *
     * @param msg
     */
    public void sendToClient(Message msg) {
        String name = msg.getTo();
        ServerHandler client = clientMap.get(name);
        sendToClient(client, msg);
    }

    /**
     * ������Ϣ��ָ���û��߳�
     *
     * @param client
     * @param msg
     */
    public void sendToClient(ServerHandler client, Message msg) {
        try {
            client.out.writeObject(msg);
            client.out.flush();
            System.out.println("send to client: " + msg);
        } catch (IOException e) {
            System.out.print(e.toString() + " ����˷�����Ϣʧ��");
//			client.interrupt();
        }
    }

    /**
     * �����û��б�
     *
     * @param msg
     */
    public synchronized void sendClientList(Message msg) {
        // �����ߵ��û�
        ServerHandler newclient = clientMap.get(msg.getFrom());
        for (ServerHandler client : clientList) {
            if (client == newclient)
                continue;
            // ���������û������б�
            sendToClient(newclient, new Message(msg.getType(), client.name, newclient.name, null));
            // �������û�
            sendToClient(client, new Message(msg.getType(), newclient.name, client.name, msg.getContent()));
        }
    }

}
