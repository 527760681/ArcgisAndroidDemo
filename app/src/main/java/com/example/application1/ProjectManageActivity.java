package com.example.application1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.application1.DatabaseUtils.ProjectBean;
import com.example.application1.DatabaseUtils.ProjectDao;
import com.example.application1.FIleUtils.FileUtils;

import java.util.List;

public class ProjectManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manage);

        List<ProjectBean> projectBeanList = new ProjectDao(this).selectAll();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter(this, projectBeanList));
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<ProjectBean> mProjectBeanList;
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public MyAdapter(Context mContext, List<ProjectBean> projectBeanList) {
            this.mContext = mContext;
            mLayoutInflater = LayoutInflater.from(mContext);
            mProjectBeanList = projectBeanList;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyViewHolder(mLayoutInflater.inflate(R.layout.layout_project_manage_item, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder viewHolder, int i) {
            ProjectBean projectBean = mProjectBeanList.get(i);
            viewHolder.TV_project_item_createDate.setText("项目创建日期：" + projectBean.getCreateDate().toString());
            viewHolder.TV_project_item_Id.setText("项目编号：" + Integer.toString(projectBean.getId()));
            viewHolder.TV_project_item_projectName.setText("项目名称：" + projectBean.getProjectName());
            viewHolder.Btn_detail.setOnClickListener(v -> {
                Intent intent = new Intent(mContext,MainActivity.class);
                intent.putExtra("ProjectID",projectBean.getId());
                mContext.startActivity(intent);
            });
            viewHolder.Btn_delete.setOnClickListener(v -> {
                new AlertDialog.Builder(mContext)
                        .setTitle("确定删除该项目吗？该操作不可恢复，且项目文件也会一同删除！")
                        .setPositiveButton("确定", (dialog, which) -> {
                            FileUtils.removeFile(projectBean.getProjectFolderPath());
                            new ProjectDao(mContext).delete(projectBean);
                            mProjectBeanList.remove(projectBean);
                            notifyItemRemoved(i);
                            dialog.dismiss();
                        }).setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).show();
            });
        }

        @Override
        public int getItemCount() {
            return mProjectBeanList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView TV_project_item_Id;
            TextView TV_project_item_createDate;
            TextView TV_project_item_projectName;
            Button Btn_delete;
            Button Btn_detail;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                TV_project_item_Id = itemView.findViewById(R.id.TV_project_item_Id);
                TV_project_item_createDate = itemView.findViewById(R.id.TV_project_item_createDate);
                TV_project_item_projectName = itemView.findViewById(R.id.TV_project_item_projectName);
                Btn_delete = itemView.findViewById(R.id.Btn_delete);
                Btn_detail = itemView.findViewById(R.id.Btn_detail);

            }
        }
    }

}
