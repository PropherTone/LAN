package com.example.lan.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lan.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    PrintWriter printWriter;
    Handler mainHandler;
    private ImageView imageView;
    private EditText editText1;
    private EditText editText;
    public String TAG = "Main";
    private String msgs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainHandler = new MyHandler(Looper.getMainLooper(),this);
        editText = findViewById(R.id.editTextNumber);
        editText1 = findViewById(R.id.editTextNumber2);
        imageView = findViewById(R.id.imageView);
        Button button = findViewById(R.id.button);
        new Thread(new LANThread()).start();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new SendThread()).start();
            }
        });
    }

    class MyHandler extends Handler{

        WeakReference<MainActivity> mActivity;

        public MyHandler(Looper looper,MainActivity activity) {
            super(looper);
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x10){
                editText.setText(msgs);
            }
        }
    }

    class LANThread implements Runnable{
        @Override
        public void run() {

                try {
                    BufferedReader bufferedReader;
                    while(true){
                    socket = new Socket("192.168.4.1", 8080);
                        if (socket.isConnected()){
                            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            printWriter = new PrintWriter(socket.getOutputStream(),true);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                            });
                            break;
                        }
                    }

                    while (socket!=null&&socket.isConnected()){
                            Log.d(TAG,"RM in");
                        char[] read = new char[256];
                        int count = 0;
                           if ((count = bufferedReader.read(read))>0){
                               msgs = GetBufferedReader(read,count);
                               Message message = new Message();
                               message.what = 0x10;
                               mainHandler.sendMessage(message);
                           }
                    }
                } catch (IOException ignored) {
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

    class SendThread implements  Runnable{
        @Override
        public void run() {
            if (socket!=null && socket.isConnected()){
                    printWriter.println(editText1.getText().toString());
            }
        }
    }
    private void WifiBroadCastReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(new Wifi(),intentFilter);
    }

    public static class Wifi extends BroadcastReceiver{
        private int watcher = 0;
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    watcher+=1;
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    watcher+=1;
                }
            }
        }
    }
}