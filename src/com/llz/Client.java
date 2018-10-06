package com.llz;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("请输入用户名：");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String name = br.readLine();
        if(name.equals("")){
            return;
        }
        Socket client = new Socket("localhost",8888);
        new Thread(new Send(client,name)).start();
        new Thread(new Receive(client)).start();
    }
}
