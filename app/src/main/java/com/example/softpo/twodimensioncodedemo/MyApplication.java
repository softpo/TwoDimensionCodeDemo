package com.example.softpo.twodimensioncodedemo;

import android.app.Application;
import android.content.Intent;

public class MyApplication extends Application implements
            Thread.UncaughtExceptionHandler {  
        @Override  
        public void onCreate() {  
            super.onCreate();  
            //设置Thread Exception Handler  
            Thread.setDefaultUncaughtExceptionHandler(this);  
        }  
      
        @Override  
        public void uncaughtException(Thread thread, Throwable ex) {  
            System.out.println("uncaughtException");  
            System.exit(0);  
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  
            Intent.FLAG_ACTIVITY_NEW_TASK);  
            startActivity(intent);  
        }  
          
    }  