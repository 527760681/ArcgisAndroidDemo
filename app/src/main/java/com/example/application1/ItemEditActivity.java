package com.example.application1;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.application1.DatabaseUtils.ItemPointBean;
import com.example.application1.DatabaseUtils.ItemPointDao;
import com.example.application1.DatabaseUtils.ProjectDao;
import com.example.application1.FIleUtils.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class ItemEditActivity extends AppCompatActivity {

    private int projectID;
    private final int CAMERA_REQUEST_CODE = 20;
    private File outputImagePath;
    private String imageDir;

    private EditText editText_itemName;
    private EditText editText_note;
    private Button button_takePhoto;
    private LinearLayout linearLayout;
    private Button btn_itemEdit_OK;
    private Button btn_itemEdit_cancle;
    private ItemPointBean itemPointBean;
    private String itemName;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);
        ActivityCompat.requestPermissions(ItemEditActivity.this, new String[]{Manifest.permission.CAMERA}, 1);//动态申请权限
        editText_itemName = findViewById(R.id.editText_itemName);
        editText_note = findViewById(R.id.editText_note);
        button_takePhoto = findViewById(R.id.button_takePhoto);
        linearLayout = findViewById(R.id.linearLayout);
        btn_itemEdit_OK = findViewById(R.id.btn_itemEdit_OK);
        btn_itemEdit_cancle = findViewById(R.id.btn_itemEdit_cancle);

        Intent intent = getIntent();
        if (intent.getStringExtra("type").equals("save")) {
            itemPointBean = new ItemPointBean();

            double x = intent.getDoubleExtra("x", -1);
            double y = intent.getDoubleExtra("y", -1);
            projectID = intent.getIntExtra("ProjectID", -1);
            String projectDir = new ProjectDao(this).queryById(projectID).getProjectFolderPath();

            imageDir = FileUtils.getCustomDir(projectDir, "ItemPoint_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)));

            button_takePhoto.setOnClickListener(v -> openSysCamera(imageDir, "IMG_"
                    + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))
                    + ".jpg"));

            btn_itemEdit_OK.setOnClickListener(v -> {
                if (TextUtils.isEmpty(editText_itemName.getText()) || TextUtils.isEmpty(editText_note.getText())) {
                    Toast.makeText(ItemEditActivity.this, "请填写标记点信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                //保存数据
                itemName = editText_itemName.getText().toString();
                note = editText_note.getText().toString();
//                ItemPointBean itemPointBean = new ItemPointBean(projectID, itemName, imageDir, x, y, note);
                itemPointBean.setProjectID(projectID);
                itemPointBean.setImageDir(imageDir);
                itemPointBean.setItemName(itemName);
                itemPointBean.setX(x);
                itemPointBean.setY(y);
                itemPointBean.setNote(note);

                new ItemPointDao(ItemEditActivity.this).insert(itemPointBean);

                //提示
                Toast.makeText(ItemEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                //跳转回地图界面
                Intent backIntent = new Intent(ItemEditActivity.this, MainActivity.class);
                backIntent.putExtra("ProjectID", projectID);
                startActivity(backIntent);
                ItemEditActivity.this.finish();
            });
            btn_itemEdit_cancle.setOnClickListener(v -> {
                if (new File(imageDir).exists()) {
                    new File(imageDir).delete();
                }
                Intent backIntent = new Intent(ItemEditActivity.this, MainActivity.class);
                backIntent.putExtra("ProjectID", projectID);
                startActivity(backIntent);
                this.finish();
            });
        }
        if (intent.getStringExtra("type").equals("edit")) {
            itemPointBean = new ItemPointDao(this).queryById(intent.getIntExtra("id", -1));
            editText_itemName.setText(itemPointBean.getItemName());
            editText_note.setText(itemPointBean.getNote());

            imageDir = itemPointBean.getImageDir();
            File[] images = new File(imageDir).listFiles();
            for (File image : images) {
                linearLayout.addView(setThumbnailIV(image));
            }
            button_takePhoto.setOnClickListener(v -> openSysCamera(imageDir, "IMG_"
                    + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA))
                    + ".jpg"));

            btn_itemEdit_OK.setOnClickListener(v -> {
                if (TextUtils.isEmpty(editText_itemName.getText()) || TextUtils.isEmpty(editText_note.getText())) {
                    Toast.makeText(ItemEditActivity.this, "请填写标记点信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                //保存数据
                itemName = editText_itemName.getText().toString();
                note = editText_note.getText().toString();
                itemPointBean.setItemName(itemName);
                itemPointBean.setNote(note);

                new ItemPointDao(ItemEditActivity.this).update(itemPointBean);

                //提示
                Toast.makeText(ItemEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

                //跳转回地图界面
                Intent backIntent = new Intent(ItemEditActivity.this, MainActivity.class);
                backIntent.putExtra("ProjectID", projectID);
                startActivity(backIntent);
                ItemEditActivity.this.finish();
            });
            btn_itemEdit_cancle.setOnClickListener(v -> {
                Intent backIntent = new Intent(ItemEditActivity.this, MainActivity.class);
                backIntent.putExtra("ProjectID", projectID);
                startActivity(backIntent);
                this.finish();
            });
        }
    }


    private void openSysCamera(String outputDir, String imageName) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputImagePath = new File(outputDir, imageName);
        if (!outputImagePath.getParentFile().exists()) {
            outputImagePath.getParentFile().mkdirs();
        }
        Uri uri = FileProvider.getUriForFile(ItemEditActivity.this, getPackageName() + ".fileprovider", outputImagePath);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {//-1
                ImageView thumbnailIV = setThumbnailIV(outputImagePath);
                linearLayout.addView(thumbnailIV, 0);
            } else if (resultCode == RESULT_CANCELED) {//0
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    @NotNull
    private ImageView setThumbnailIV(File image) {
        // 生成缩略图
        Bitmap tempBitMap = BitmapFactory.decodeFile(image.getAbsolutePath());
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(tempBitMap, 400, 200);

        //添加缩略图imageView
        ImageView thumbnailIV = new ImageView(ItemEditActivity.this);
        thumbnailIV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        thumbnailIV.setImageBitmap(thumbnail);

        //设置删除
        thumbnailIV.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("删除这张照片？")
                .setPositiveButton("确定", (dialog, which) -> {
                    linearLayout.removeView(thumbnailIV);
                    image.delete();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show());
        return thumbnailIV;
    }
}
