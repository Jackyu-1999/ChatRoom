package common;

import java.io.Serializable;

public class Message implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = -8104648599710335924L;

    private String type;		// 消息类型
    private String from;		// 来源
    private String to;			// 目的
    private String title;		// 附件名
    private String content;		// 消息内容
    private byte[] file;		// 附件内容

    public Message() {

    }

    public Message(String type, String from, String to, String content) {
        this.set(type, from, to, null, content);
    }

    public Message(String type, String from, String to, String title, String content, byte[] file) {
        this.set(type, from, to, title, content);
        this.setFile(file);
    }

    public void set(String type, String from, String to, String title, String content) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.title = title;
        this.content = content;
    }

    public Message clone() {
        Message m = null;
        try {
            m = (Message) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return m;
    }

    public void show() {
        System.out.println("Message:");
        System.out.println("\t\tType: " + type);
        System.out.println("\t\tFrom: " + from);
        System.out.println("\t\tTo: " + to);
        System.out.println("\t\tTitle: " + title);
        System.out.println("\t\tContent: " + content);
        System.out.println();
    }

    public String toString() {
        return "type=" + type + " from=" + from + " to=" + to + " title=" + title + " content=" + content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

}
