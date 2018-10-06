package com.llz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<MyChannel> all = new ArrayList<MyChannel>();

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    public void start(){
        try {
            ServerSocket server = new ServerSocket(8888);
            while(true){
                Socket client = server.accept();
                MyChannel myChannel = new MyChannel(client);
                all.add(myChannel);
                new Thread(myChannel).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 一个客户端一条道路
     */
    private class MyChannel implements Runnable{
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isRunning = true;
        private String name;
        public MyChannel(Socket client){
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                this.name = dis.readUTF();
                send("欢迎进入聊天室");
                sendOthers(this.name + "进入了聊天室",true);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
                CloseUtil.closeAll(dis,dos);
            }
        }
        private String receive(){
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
                CloseUtil.closeAll(dis);
                all.remove(this);
            }
            return msg;
        }
        private void send(String msg){
            if(null != msg && !msg.equals("")){
                try {
                    dos.writeUTF(msg);
                    dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    isRunning = false;
                    CloseUtil.closeAll(dos);
                    all.remove(this);
                }
            }
        }

        /**
         * 发送给其他客户端
         */
        private void sendOthers(String msg,boolean systemInfo){
            //是否为私聊
            if(msg.startsWith("@") && msg.indexOf(":") > -1){
                String name = msg.substring(1,msg.indexOf(":"));
                String content = msg.substring(msg.indexOf(":") + 1);
                for(MyChannel other : all){
                    if(other.name.equals(name)){
                        other.send(this.name + "对您悄悄的说" + content);
                    }
                }
            }else {
                for (MyChannel other : all) {
                    if (other == this) {
                        continue;
                    }
                    //发送给其他客户端
                    if(systemInfo){
                        //是系统信息
                        other.send("系统消息：" + msg);
                    }else {
                        other.send(this.name + "对所有人说" + msg);
                    }
                }
            }
        }

        @Override
        public void run() {
            while(isRunning){
                sendOthers(receive(),false);
            }
        }
    }
}
