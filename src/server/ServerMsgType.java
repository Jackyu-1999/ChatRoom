package server;

import java.lang.reflect.Method;

import common.*;

public class ServerMsgType {
    ServerHandler server;
    Message msg;

    public ServerMsgType(ServerHandler server, Message msg) {
        this.server = server;
        this.msg = msg;
    }

    public void process() {
        try {
            Method method = this.getClass().getDeclaredMethod(msg.getType());
            method.invoke(this);
        } catch (Exception e) {
            System.out.println(e.toString() + " ʹ����δ����Ĳ���");
            try {
                Method method = this.getClass().getDeclaredMethod("chat");
                method.invoke(this);
            } catch (Exception e1) {
                System.out.println(e1.toString() + " Ĭ�ϴ���ʧ��");
            }

        }
    }

    public void chat() {
        if (msg.getTo().equals("All")) {
            // �������˷�����Ϣ
            server.sendAllClient(msg);
        } else {
            // ������Ϣ��ָ������
            server.sendToClient(msg);
        }
    }

    public void online() {
        server.sendClientList(msg);
    }

    public void offline() {
        server.sendAllClient(msg);
    }

    public void login() {
        server.sendToClient(msg);
    }
}
