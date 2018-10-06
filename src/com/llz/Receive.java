package com.llz;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receive implements Runnable{
    private DataInputStream dis;
    private boolean isRunning = true;
    public Receive() {
    }

    public Receive(Socket socket){
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
    }

    public String receive(){
        String msg = "";
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
            CloseUtil.closeAll(dis);
        }
        return msg;
    }
    @Override
    public void run() {
        while(isRunning){
            System.out.println(receive());
        }
    }
}
