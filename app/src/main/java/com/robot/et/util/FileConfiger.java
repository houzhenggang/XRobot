package com.robot.et.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.robot.et.config.UrlConfig;
import com.robot.et.core.software.okhttp.HttpEngine;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by houdeming on 2016/7/14.
 */
public class FileConfiger {
    private final static String FILENAME = "map.png";

    //上传文件
    public static void uploadFile(Bitmap bitmap){
        HttpEngine.Param[] params = new HttpEngine.Param[]{
                new HttpEngine.Param("robotNumber", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "")),
        };
        String[] fileKeys = new String[]{"file"};
        saveFilePath(bitmap);
        File[] files = getFiles(getFilePath());
        Log.i("json", "filePath====" + getFilePath());
        HttpEngine httpEngine = HttpEngine.getInstance();
        Request request = null;
        try {
            request = httpEngine.createRequest(UrlConfig.UPLOAD_FILE_PATH, files,fileKeys,params);
            Call call = httpEngine.createRequestCall(request);
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Request arg0, IOException arg1) {
                }
                @Override
                public void onResponse(Response response) throws IOException {
                    String result = response.body().string();
                    Log.i("json", "result====" + result);
                    if(GsonParse.isChangeStatusSuccess(result)){
                        Log.i("json", "上传成功");
                        deleteFile();
                    }
                }

            });
        } catch (IOException e) {
            Log.i("json", "uploadFile  IOException");
        }
    }

    public static File[] getFiles(String filePath){
        File[] files = null;
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            files = new File[]{file};
        }
        return files;
    }

    public static boolean saveFilePath(Bitmap bitmap){
        Bitmap.CompressFormat format= Bitmap.CompressFormat.PNG;
        int quality = 100;
        OutputStream stream = null;
        try {
            createFile();
            stream = new FileOutputStream(getFilePath());
        } catch (FileNotFoundException e) {
            Log.i("file", "saveFilePath  IOException");
        }
        return bitmap.compress(format, quality, stream);
    }

    private static String getFilePath(){
        String fileSrc = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "robotPicture" + File.separator + FILENAME;
        return fileSrc;
    }

    private static void createFile(){
        File file = new File(getFilePath());
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteFile(){
        File file = new File(getFilePath());
        if(file.exists()){
            file.delete();
        }
    }

}
