package com.example.application1.FIleUtils;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class FileUtils {
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDCardBaseDir() {
        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    public static void removeFile(String filePath) {
        if(filePath == null || filePath.length() == 0){
            return;
        }
        try {
            File file = new File(filePath);
            if(file.exists()){
                removeFile(file);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static void removeFile(File file){
        //如果是文件直接删除
        if(file.isFile()){
            file.delete();
            return;
        }
        //如果是目录，递归判断，如果是空目录，直接删除，如果是文件，遍历删除
        if(file.isDirectory()){
            File[] childFile = file.listFiles();
            if(childFile == null || childFile.length == 0){
                file.delete();
                return;
            }
            for(File f : childFile){
                removeFile(f);
            }
            file.delete();
        }
    }

    private static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static String getProjectDirName() {
        return DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + "";
    }

    public static String getPackageDir(Context context) {
        return getSDCardBaseDir() + File.separator + context.getPackageName();
    }

    public static String getProjectDir(String projectDirName, Context context) {
        return getPackageDir(context) + File.separator + projectDirName;
    }

    public static String getCustomDir(String projectDirName, String dir, Context context) {
        return getProjectDir(projectDirName, context) + File.separator + dir;
    }

    public static String getCustomDir(String projectDir, String dir) {
        return projectDir + File.separator + dir;
    }

    public static boolean saveFileToCustomDir(String filePath, String projectDirName, String dir,
                                              String fileName, Context context) {
        BufferedOutputStream bos = null;
        if (isSDCardMounted()) {
            File file = new File(getCustomDir(projectDirName, dir, context));
            if (!file.exists()) {
                file.mkdirs();// 递归创建自定义目录
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, fileName)));
                bos.write(getBytes(filePath));
                bos.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
