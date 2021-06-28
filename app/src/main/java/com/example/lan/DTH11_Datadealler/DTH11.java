package com.example.lan.DTH11_Datadealler;

public class DTH11 {

    public static String TEMPERATURE = "temperature";
    public static String HUMIDITY = "humidity";

    public static String GetData(String data,String get){
        String callBack = "N";
        if (get.equals(TEMPERATURE)){
            callBack = data.substring(0,data.indexOf(","));
        }else if (get.equals(HUMIDITY)){
            callBack = data.substring(data.indexOf(",")+1);
        }
        return callBack;
    }
}
