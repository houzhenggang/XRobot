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

    //上传文件
    public static void uploadFile(Bitmap bitmap, int bitmapWidth, int bitmapHeight, String coordinateX, String coordinateY){
        HttpEngine.Param[] params = new HttpEngine.Param[]{
                new HttpEngine.Param("robotNumber", SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "")),
                new HttpEngine.Param("fileInfo", coordinateX + "_" + coordinateY + "_" + bitmapWidth + "_" + bitmapHeight),
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

    public static void saveFilePath(Bitmap bitmap){
        OutputStream stream = null;
        try {
            createFile();
            stream = new FileOutputStream(new File(getFilePath()));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
            Log.i("json", "saveFilePath  success");
        } catch (FileNotFoundException e) {
            Log.i("json", "saveFilePath  IOException");
        } catch (IOException e) {
            Log.i("json", "saveFilePath  IOException");
        }
    }

    private static String getFilePath(){
        String fileName = SharedPreferencesUtils.getInstance().getString(SharedPreferencesKeys.ROBOT_NUM, "") + "_map.png";
        String fileSrc = Environment.getExternalStorageDirectory().getAbsolutePath() +  File.separator + fileName;
        return fileSrc;
    }

    private static void createFile(){
        File file = new File(getFilePath());
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.i("json", "createFile  IOException");
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
