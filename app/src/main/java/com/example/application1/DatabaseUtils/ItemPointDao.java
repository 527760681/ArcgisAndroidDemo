package com.example.application1.DatabaseUtils;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class ItemPointDao {
    private Context context;

    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<ItemPointBean, Integer> dao;

    public ItemPointDao(Context context) {
        this.context = context;
        try{
            this.dao = DatabaseHelper.getInstance(context).getDao(ItemPointBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(ItemPointBean data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(ItemPointBean data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(ItemPointBean data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ItemPointBean> selectAll() {
        List<ItemPointBean> projects = null;
        try {
            projects = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public ItemPointBean queryById(int id) {
        ItemPointBean project = null;
        try {
            project = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    public List<ItemPointBean> queryByProjectID(int projectID){
        List<ItemPointBean> resultList = null;
        try{
            QueryBuilder builder = dao.queryBuilder();
            builder.where().eq("projectID",projectID);
            resultList = builder.query();//因为是unique字段，所以直接返回
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultList;
    }
}
