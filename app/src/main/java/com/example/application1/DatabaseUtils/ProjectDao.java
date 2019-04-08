package com.example.application1.DatabaseUtils;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/*
* 操作Project数据表的Dao类，封装操作Project表的所有操作
* 通过DatabaseHelper类中的方法获取ORMLite内置的DAO类进行数据库中数据的操作
* 调用dao的create()方法向表中添加数据
* 调用dao的delete()方法删除表中的数据
* 调用dao的update()方法修改表中的数据
* 调用dao的queryForAll()方法查询表中的所有数据
* */
public class ProjectDao {
    private Context context;

    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<ProjectBean, Integer> dao;

    public ProjectDao(Context context) {
        this.context = context;
        try{
            this.dao = DatabaseHelper.getInstance(context).getDao(ProjectBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(ProjectBean data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(ProjectBean data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(ProjectBean data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ProjectBean> selectAll() {
        List<ProjectBean> projects = null;
        try {
            projects = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public ProjectBean queryById(int id) {
        ProjectBean project = null;
        try {
            project = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    public ProjectBean queryByProjectName(String projectName){
        ProjectBean project = null;
        try {
            QueryBuilder builder = dao.queryBuilder();
            builder.where().eq("projectName",projectName);
            List<ProjectBean> resultList = builder.query();//因为是unique字段，所以直接返回
            project = resultList.get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

}
