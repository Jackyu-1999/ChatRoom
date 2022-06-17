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
            System.out.println(e.toString() + " 使用了未定义的操作");
            try {
                Method method = this.getClass().getDeclaredMethod("chat");
                method.invoke(this);
            } catch (Exception e1) {
                System.out.println(e1.toString() + " 默认处理失败");
            }

        }
    }

    public void chat() {
        if (msg.getTo().equals("All")) {
            // 给所有人发送消息
            server.sendAllClient(msg);
        } else {
            // 发送消息给指定的人
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
