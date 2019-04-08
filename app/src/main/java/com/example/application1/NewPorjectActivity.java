package com.example.application1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.application1.DatabaseUtils.ProjectBean;
import com.example.application1.DatabaseUtils.ProjectDao;
import com.example.application1.FIleUtils.FileUtils;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.Date;
import java.util.List;

public class NewPorjectActivity extends AppCompatActivity {

    private ProjectBean projectData;
    private int projectID;
    private static String projectDirName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_porject);
        //初始化组件
        final EditText editText_projectName = findViewById(R.id.editText_projectName);
        final EditText editText_originMapPath = findViewById(R.id.editText_originMapPath);
        final Button btn_newProject_OK = findViewById(R.id.btn_newProject_OK);
        final Button btn_newProject_cancel = findViewById(R.id.btn_newProject_cancel);

        projectData = new ProjectBean();

        ActivityCompat.requestPermissions(NewPorjectActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//动态申请权限

        //设置不可编辑 选择底图图片
        editText_originMapPath.setFocusable(false);
        editText_originMapPath.setFocusableInTouchMode(false);
        editText_originMapPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChooserDialog(NewPorjectActivity.this)
                        .withFilter(false, false, "jpg")
                        .withStartFile("/sdcard")
                        .withChosenListener((path, pathFile) -> {
                            editText_originMapPath.setText(path);
                        })
                        .withOnCancelListener(dialog -> {
                            Log.d("CANCEL", "CANCEL");
                            dialog.cancel();
                        })
                        .build()
                        .show();
            }
        });

        btn_newProject_OK.setOnClickListener(v -> {
            if (TextUtils.isEmpty(editText_projectName.getText()) || TextUtils.isEmpty(editText_originMapPath.getText())) {
                Toast.makeText(NewPorjectActivity.this, "请填写项目信息", Toast.LENGTH_SHORT).show();
            } else {
                String projectName = editText_projectName.getText().toString();
                projectDirName = FileUtils.getProjectDirName();
                String projectDir = FileUtils.getProjectDir(projectDirName, NewPorjectActivity.this);
                String originMapPath = editText_originMapPath.getText().toString();
                String originMapFileName = new File(originMapPath).getName();
                String baseMapPath = projectDir + File.separator + getApplicationContext().getString(R.string.baseMapDir) + File.separator + originMapFileName;
                // proejctID\basemap\originMapFileName

                //ProjectBean projectData = new ProjectBean(new Date(), projectName, projectDir, baseMapPath);//插入数据库
                projectData.setBaseMapPath(baseMapPath);
                projectData.setCreateDate(new Date());
                projectData.setProjectName(projectName);
                projectData.setProjectFolderPath(projectDir);

                new ProjectDao(getApplicationContext()).insert(projectData);
                projectID = projectData.getId();

                try {
                    FileUtils.saveFileToCustomDir(originMapPath, projectDirName, getApplicationContext().getString(R.string.baseMapDir), originMapFileName, NewPorjectActivity.this);//保存文件至外部存储的项目文件夹
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(NewPorjectActivity.this, MainActivity.class);//跳转页面并传参
                intent.putExtra("ProjectID", projectID);
                startActivity(intent);
            }
        });

        btn_newProject_cancel.setOnClickListener(v -> {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
