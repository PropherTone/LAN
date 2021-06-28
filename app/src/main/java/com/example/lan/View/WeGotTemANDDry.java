package com.example.lan.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lan.DTH11_Datadealler.DTH11;
import com.example.lan.R;
import com.example.lan.Service.MsgService;
import com.example.lan.ViewModel.LanViewModel;

import java.io.IOException;

public class WeGotTemANDDry extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private TextView tem,hum,custom_IP_address;
    private ImageView connect_cover;
    private ImageView connect,confirm;
    private RelativeLayout cover;
    private EditText ip,port;
    private LinearLayout IP_address;
    private String TAG = "Main";
    public LanViewModel lanViewModel;
    private boolean isCustom = false;

    public String IP,PORT;

//    public MyHandler myHandler = new MyHandler(Looper.getMainLooper(),this);
    private Intent startIntent;
    private boolean connecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_got_tem_anddry);
        lanViewModel = new ViewModelProvider(this).get(LanViewModel.class);
        InitView();
        Connecting();
        ReceiveMsg();
    }

    private void InitView() {
        tem = findViewById(R.id.tem);
        hum = findViewById(R.id.dry);
        cover = findViewById(R.id.cover);
        connect = findViewById(R.id.connect);
        connect_cover = findViewById(R.id.connect_cover);
        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        IP_address = findViewById(R.id.IP_address);
        confirm = findViewById(R.id.confirm_address);
        custom_IP_address = findViewById(R.id.custom_IP_address);
        ImageView connect_forward = findViewById(R.id.connect_forward);
        ImageView connect_back = findViewById(R.id.connect_back);
        connect.setOnClickListener(this);
        connect.setOnLongClickListener(this);
        connect_forward.setOnClickListener(this);
        connect_back.setOnClickListener(this);
        confirm.setOnClickListener(this);
        custom_IP_address.setOnClickListener(this);
    }

    private void StartAnimator(){
        ObjectAnimator connect_coverScaleX = ObjectAnimator.ofFloat(connect_cover,"scaleX",0f,10f);
        ObjectAnimator connect_coverScaleY = ObjectAnimator.ofFloat(connect_cover,"scaleY",0f,10f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(connect_coverScaleX,connect_coverScaleY);
        animatorSet.setDuration(300);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cover.setBackground(null);
                connect_cover.setVisibility(View.INVISIBLE);
            }
        });
        StartConnectAnimator();
    }

    private void StopAnimator(){
        cover.setBackgroundColor(getColor(R.color.blue));
        connect_cover.setVisibility(View.VISIBLE);
        ObjectAnimator connect_coverScaleX = ObjectAnimator.ofFloat(connect_cover,"scaleX",10f,0f);
        ObjectAnimator connect_coverScaleY = ObjectAnimator.ofFloat(connect_cover,"scaleY",10f,0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(connect_coverScaleX,connect_coverScaleY);
        animatorSet.setDuration(300);
        animatorSet.start();
        StopConnectAnimator();
    }

    private void ConnectFailedAnimator(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofArgb(cover,"backgroundColor",getColor(R.color.blue),getColor(R.color.red));
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofArgb(cover,"backgroundColor",getColor(R.color.red),getColor(R.color.blue));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(objectAnimator,objectAnimator2);
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    private void StartConnectAnimator(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        connect.setBackground(ContextCompat.getDrawable(this,R.drawable.radius_deepblue));
        IP_address.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        custom_IP_address.setVisibility(View.INVISIBLE);
        ObjectAnimator connectScaleX = ObjectAnimator.ofFloat(connect,"scaleX",1f,8f);
        ObjectAnimator connectScaleY = ObjectAnimator.ofFloat(connect,"scaleY",1f,8f);
        ObjectAnimator connectTransY = ObjectAnimator.ofFloat(connect,"translationY",0,((float)displayMetrics.heightPixels/2));
        ObjectAnimator connectAlpha = ObjectAnimator.ofFloat(connect,"Alpha",1f,0f);
        connectScaleX.setDuration(500);
        connectScaleY.setDuration(500);
        connectTransY.setDuration(500);
        connectAlpha.setDuration(400);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(connectScaleX,connectScaleY,connectTransY,connectAlpha);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                cover.setVisibility(View.INVISIBLE);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                connect.setClickable(false);
            }
        });
    }

    private void StopConnectAnimator(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cover.setVisibility(View.VISIBLE);
        ObjectAnimator connectScaleX = ObjectAnimator.ofFloat(connect,"scaleX",8f,1f);
        ObjectAnimator connectScaleY = ObjectAnimator.ofFloat(connect,"scaleY",8f,1f);
        ObjectAnimator connectTransY = ObjectAnimator.ofFloat(connect,"translationY",((float)displayMetrics.heightPixels/2),0);
        ObjectAnimator connectAlpha = ObjectAnimator.ofFloat(connect,"Alpha",0f,1f);
        connectScaleX.setDuration(500);
        connectScaleY.setDuration(500);
        connectTransY.setDuration(400);
        connectAlpha.setDuration(400);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(connectScaleX,connectScaleY,connectTransY,connectAlpha);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                connect.setBackground(ContextCompat.getDrawable(WeGotTemANDDry.this,R.drawable.connect_wave));
                connect.setClickable(true);
                connecting = false;
            }
        });
    }

    private void ShowInput(){
        IP_address.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);
        ObjectAnimator IP_addressAlpha = ObjectAnimator.ofFloat(IP_address,"alpha",0f,1f);
        ObjectAnimator IP_addressTranslationY = ObjectAnimator.ofFloat(IP_address,"translationY",10,0);
        ObjectAnimator confirmAlpha = ObjectAnimator.ofFloat(confirm,"alpha",0f,1f);
        ObjectAnimator confirmTranslationY = ObjectAnimator.ofFloat(confirm,"translationY",10,0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(IP_addressAlpha,IP_addressTranslationY,confirmAlpha,confirmTranslationY);
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    private void HideInput(){
        IP_address.setVisibility(View.INVISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        custom_IP_address.setVisibility(View.VISIBLE);
        ObjectAnimator IP_addressAlpha = ObjectAnimator.ofFloat(IP_address,"alpha",1f,0f);
        ObjectAnimator IP_addressTranslationY = ObjectAnimator.ofFloat(IP_address,"translationY",0,10);
        ObjectAnimator confirmAlpha = ObjectAnimator.ofFloat(confirm,"alpha",1f,0f);
        ObjectAnimator confirmTranslationY = ObjectAnimator.ofFloat(confirm,"translationY",0,10);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(IP_addressAlpha,IP_addressTranslationY,confirmAlpha,confirmTranslationY);
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.connect){
            if (!connecting){
                connecting = true;
                if (isCustom){
                    lanViewModel.ThreadPoooool().execute(lanViewModel.LANThread(IP,PORT));
                }else {
                    lanViewModel.ThreadPoooool().execute(lanViewModel.LANThread());
                }
            }
        }else if (id == R.id.connect_forward){
            lanViewModel.ThreadPoooool().execute(lanViewModel.SendThread("go"));
        }else if (id == R.id.connect_back){
            lanViewModel.ThreadPoooool().execute(lanViewModel.SendThread("back"));
        }else if (id == R.id.confirm_address){
            IP = ip.getText().toString();
            PORT = port.getText().toString();
            if (IP.equals("")){
                IP = "127.0.0.1";
            }
            if (PORT.equals("")){
                PORT = "8080";
            }
            isCustom = true;
            String s = IP+":"+PORT;
            custom_IP_address.setText(s);
            HideInput();
        }else if (id == R.id.custom_IP_address){
            isCustom = false;
            custom_IP_address.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == R.id.connect){
            ShowInput();
        }
        return true;
    }

//    class MyHandler extends Handler {
//
//        WeakReference<WeGotTemANDDry> mActivity;
//
//        public MyHandler(Looper looper, WeGotTemANDDry activity) {
//            super(looper);
//            mActivity = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 0x10){
//                Bundle data = msg.getData();
//                tem.setText(data.getString("MSG"));
//            }
//        }
//    }

    private void Connecting(){
        lanViewModel.msgs.observe(WeGotTemANDDry.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("isConnected")){
//                    startIntent = new Intent(WeGotTemANDDry.this, MsgService.class);
//                    startService(startIntent);
                    lanViewModel.ThreadPoooool().execute(lanViewModel.ReceiveMsg());
                    StartAnimator();
                }else if (s.equals("Connect TimeOut")){
                    connecting = false;
                    Log.d(TAG,"connecting"+connecting);
                    ConnectFailedAnimator();
                }else if (s.equals("Connect lose")){
                    StopAnimator();
                }else {
                    Toast.makeText(WeGotTemANDDry.this,"正在尝试第"+s+"次连接",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ReceiveMsg(){
        lanViewModel.msgGet.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tem.setText(DTH11.GetData(s,DTH11.TEMPERATURE));
                hum.setText(DTH11.GetData(s,DTH11.HUMIDITY));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            lanViewModel.socket.close();
//            stopService(startIntent);
        } catch (IOException ignored) {
        }
    }


}