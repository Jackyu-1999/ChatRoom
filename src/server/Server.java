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
                Socket conn = server.accept(); // 建立客户端套接字
                new Thread(new ServerHandler(conn)).start(); // 新建线程和客户端建立全双工通信
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
    private ObjectInputStream in; // 输入流
    private ObjectOutputStream out; // 输出流
    private String name; // 用户名
    private static ArrayList<ServerHandler> clientList = new ArrayList<ServerHandler>(); // 把用户线程放入对象数组
    private static HashMap<String, ServerHandler> clientMap = new HashMap<>(); // 把用户名和连接线程关联便于查找

    public ServerHandler(Socket socket) {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.toString() + " 服务端初始化失败");
        }
    }

    public void run() {
        if (!loginVerify())
            return; // 登陆校验
        try {
            addClient(); // 客户端经过登陆检验 添加到列表
            sendMessage(new Message("online", name, "All", "上线了!")); // 广播用户列表
            while (true) {
                Message rcv = (Message) in.readObject(); // 等待客户端消息
                sendMessage(rcv);
            }
        } catch (Exception e) {
            System.out.println(e.toString() + " 客户端已退出");
        } finally {
            removeClient();
            sendMessage(new Message("offline", name, "All", "下线了！")); // 广播下线通知
        }
    }

    /**
     * 登陆校验
     *
     * @return
     */
    private Boolean loginVerify() {
        try {
            Message rcv = (Message) in.readObject();
            System.out.println(rcv.toString());
            this.name = rcv.getFrom();
            if (clientMap.containsKey(name)) {
                // 用户名重复
                sendToClient(this, new Message("login", null, name, "用户名已存在"));
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.toString() + "登陆检验失败");
            return false;
        }
        return true;
    }

    /**
     * 添加用户到列表
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
     * 移除当前用户
     *
     * @return
     */
    private Boolean removeClient() {
        clientList.remove(this);
        clientMap.remove(name);
        return true;
    }

    /**
     * 发送消息
     *
     * @param msg
     * @throws IOException
     */
    public void sendMessage(Message msg) {
        new ServerMsgType(this, msg).process();
    }

    /**
     * 发送消息给所有用户,排除name
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
     * 发送消息给指定用户
     *
     * @param msg
     */
    public void sendToClient(Message msg) {
        String name = msg.getTo();
        ServerHandler client = clientMap.get(name);
        sendToClient(client, msg);
    }

    /**
     * 发送消息给指定用户线程
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
            System.out.print(e.toString() + " 服务端发送消息失败");
//			client.interrupt();
        }
    }

    /**
     * 更新用户列表
     *
     * @param msg
     */
    public synchronized void sendClientList(Message msg) {
        // 新上线的用户
        ServerHandler newclient = clientMap.get(msg.getFrom());
        for (ServerHandler client : clientList) {
            if (client == newclient)
                continue;
            // 给新上线用户所有列表
            sendToClient(newclient, new Message(msg.getType(), client.name, newclient.name, null));
            // 更新老用户
            sendToClient(client, new Message(msg.getType(), newclient.name, client.name, msg.getContent()));
        }
    }

}
