package com.llz;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Send implements Runnable{
    private BufferedReader console;
    private DataOutputStream dos;
    private boolean isRunning = true;
    private String name;        //用户名
    public Send() {
        console = new BufferedReader(new InputStreamReader(System.in));
    }
    public Send(Socket client,String name){
        this();
        try {
            this.name = name;
            dos = new DataOutputStream(client.getOutputStream());
            send(this.name);
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
            CloseUtil.closeAll(dos,console);
        }
    }

    /**
     * 从控制台接收数据
     * @return
     */
    private String getMsgFromConsole(){
        try {
            return console.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送数据
     */
    public void send(String msg){
        if(null != msg && !msg.equals("")){
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
                CloseUtil.closeAll(dos,console);
            }
        }
    }

    @Override
    public void run() {
        while(isRunning){
            send(getMsgFromConsole());
        }
    }
}
