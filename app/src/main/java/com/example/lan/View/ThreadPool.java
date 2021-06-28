package com.example.lan.View;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {

    public  ThreadPoolExecutor threadPoolExecutor;

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

    public  List<Runnable> ShutDownNowThreadPool(){
        if (!threadPoolExecutor.isShutdown()){
           return threadPoolExecutor.shutdownNow();
        }
        return null;
    }
}
