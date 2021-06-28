 package com.example.lan.ViewModel;

import android.os.Handler;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LanViewModel extends ViewModel {

    public  ThreadPoolExecutor threadPoolExecutor;
    public Socket socket;
    public PrintWriter printWriter;

    public Handler mainHandler;
    public BufferedReader bufferedReader = null;

    public MutableLiveData<String> msgs = new MutableLiveData<>();
    public MutableLiveData<String> msgGet = new MutableLiveData<>();

    private String TAG = "ViewModel";

    public  ThreadPoolExecutor ThreadPoooool(){
        if (threadPoolExecutor==null){
            threadPoolExecutor = new ThreadPoolExecutor(5,
                    10,
                    1,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(100),
                    new ThreadPoolExecutor.AbortPolicy());
        }
        return threadPoolExecutor;
    }

    public  void ShutDownThreadPool(){
        if (!threadPoolExecutor.isShutdown()){
            threadPoolExecutor.shutdown();
        }
    }

    public List<Runnable> ShutDownNowThreadPool(){
        if (!threadPoolExecutor.isShutdown()){
            return threadPoolExecutor.shutdownNow();
        }
        return null;
    }


    public Runnable LANThread(){
        return new LANThread();
    }

    public Runnable LANThread(String ip,String port){
        return new LANThread(ip,port);
    }

    public Runnable SendThread(String s){
        return new SendThread(s);
    }

    public Runnable ReceiveMsg(){
        return new ReceiveMsg();
    }

    class LANThread implements Runnable{

        String IP,PORT;
        boolean custom = false;

        public LANThread() {
            custom = false;
            Log.d(TAG,"LAN in"+custom);
        }

        public LANThread(String IP, String PORT) {
            this.IP = IP;
            this.PORT = PORT;
            this.custom = true;
            Log.d(TAG,"LAN in"+custom);
        }

        @Override
        public void run() {
                int count = 0;
            Log.d(TAG,"LAN in");
//                BufferedReader bufferedReader;
                while(true) {
                    Log.d(TAG,"3"+count);
                    try {
                    if (custom){Log.d(TAG,"custom"+count);
                        socket = new Socket();
                        SocketAddress socketAddress = new InetSocketAddress(IP, Integer.parseInt(PORT));
                        socket.connect(socketAddress,3000);
                    }else { Log.d(TAG,"normal"+count);
                        socket = new Socket();
                        SocketAddress socketAddress = new InetSocketAddress("192.168.4.1", 8080);
                        socket.connect(socketAddress,3000);
                    }
                        Log.d(TAG,"2"+count);
                    if (socket.isConnected()) {
                        printWriter = new PrintWriter(socket.getOutputStream(), true);
                        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        msgs.postValue("isConnected");
                        break;
                    }
                    } catch (IOException ignored){
                        count++;
                        msgs.postValue(""+count);
                        Log.d(TAG,"count"+count);
                        if (count >3){
                            msgs.postValue("Connect TimeOut");
                            break;
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    class ReceiveMsg implements Runnable{
        @Override
        public void run() {
            while (true){
                    char[] read = new char[256];
                    int count = 0;
                try {
                    if ((count = bufferedReader.read(read))>0){
                        msgGet.postValue(GetBufferedReader(read,count));
                        Log.d(TAG,"RM in");
                    }
                } catch (IOException e) {
                    msgs.postValue("Connect lose");
                }
            }
        }
    }

    class SendThread implements  Runnable{

        public String s;

        public SendThread(String string) {
            this.s = string;
        }

        @Override
        public void run() {
            if (socket!=null && socket.isConnected()){
                printWriter.println(s);
                Log.d(TAG,"printer in");
            }
        }
    }

    private String GetBufferedReader(char[] read,int count) {
        char[] chars = new char[count];
        for (int i = 0; i < count; i++) {
            chars[i]=read[i];
        }
        return new String(chars);
    }
}
