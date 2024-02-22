package com.heweather.owp.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.heweather.owp.R;
import com.heweather.owp.crash.CrashHandler;


import org.apache.log4j.Level;
import org.apache.log4j.chainsaw.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class SplashActivity extends AppCompatActivity {
    public Logger log;

    final int REQUEST_PERMISSION_LOCATION = 10;
    final int REQUEST_PERMISSION_LOG = 11;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        权限申请
        initPermission();

    }

    private void initLogFile() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                        REQUEST_PERMISSION_LOCATION);
            }
        }

        // 定义目录路径
        String directoryPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + File.separator + "weather";

        // 将路径转换为File对象
        File directory = new File(directoryPath);

        // 检查目录是否存在，如果不存在则尝试创建
        if (!directory.exists()) {
            // 使用mkdirs()方法创建目录，因为它会递归创建多级目录
            boolean isCreated = directory.mkdirs();

            // 检查目录是否创建成功
            if (!isCreated) {
                boolean isCreated2 = directory.mkdirs();
                // 创建失败，打印错误信息
                Log.e("Directory Creation", "Failed to create directory: " + directoryPath+"  now:"+isCreated2);
                return;
            } else {
                // 创建成功，可以进行下一步操作
                Log.i("Directory Creation", "Successfully created directory: " + directoryPath);
            }
        } else {
            // 目录已经存在
            Log.i("Directory Existence", "Directory already exists: " + directoryPath);
        }

        // 现在可以尝试在该目录下创建文件
        try {
            String filePath = directoryPath + File.separator + "log4j.log";
            File file = new File(filePath);
            FileOutputStream ostream = new FileOutputStream(file, true); // true表示追加写入
            // ... 写入文件 ...
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void configLog() {
        try {
            final LogConfigurator logConfigurator = new LogConfigurator();

            logConfigurator.setFileName(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + File.separator + "weather" + File.separator + "crifanli_log4j.log");
            // Set the root log level
            logConfigurator.setRootLevel(Level.DEBUG);
            // Set log level of a specific logger
            logConfigurator.setLevel("org.apache", Level.ERROR);
            logConfigurator.configure();
            CrashHandler catchHandler = CrashHandler.getInstance();
            catchHandler.init(getApplicationContext());
        } catch (Exception e) {
            String TAG = "sky";
            Log.i(TAG, "configLog: " + e);
        }

        //gLogger = Logger.getLogger(this.getClass());
        log = Logger.getLogger("CrifanLiLog4jTest");
    }

    private void initPermission() {
//        FileProvider
        if (ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET
                    },
                    REQUEST_PERMISSION_LOCATION);
        } else {
            //创建日志文件的文件夹
            initLogFile();
            //初始化日志配置
            configLog();
//            startService(new Intent(this, LocationService.class));
            startIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                //创建日志文件的文件夹
                initLogFile();
                //初始化日志配置
                configLog();
//                startService(new Intent(this, LocationService.class));
                startIntent();
                break;
            case REQUEST_PERMISSION_LOG:
                //创建日志文件的文件夹
                initLogFile();
                //初始化日志配置
                configLog();
            default:
                startIntent();
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startIntent() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
