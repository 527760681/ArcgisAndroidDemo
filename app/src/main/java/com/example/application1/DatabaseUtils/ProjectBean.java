package com.example.application1.DatabaseUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "project")
public class ProjectBean {
    public static final String COLUMNNAME_ID = "id";
    public static final String COLUMNNAME_CREATE_DATA = "createDate";
    public static final String COLUMNNAME_PROJECT_NAME = "projectName";
    public static final String COLUMNNAME_PROJECT_FOLDER_PATH = "projectFolderPath";
    public static final String COLUMNNAME_base_MAP_PATH = "baseMapPath";


    @DatabaseField(generatedId = true, columnName = COLUMNNAME_ID, useGetSet = true,allowGeneratedIdInsert = true)
    private int id;

    @DatabaseField(columnName = COLUMNNAME_CREATE_DATA, useGetSet = true)
    private Date createDate;

    @DatabaseField(columnName = COLUMNNAME_PROJECT_NAME, useGetSet = true)
    private String projectName;

    @DatabaseField(columnName = COLUMNNAME_PROJECT_FOLDER_PATH, useGetSet = true)
    private String projectFolderPath;

    @DatabaseField(columnName = COLUMNNAME_base_MAP_PATH, useGetSet = true)
    private String baseMapPath;

    public ProjectBean() {
    }

    public ProjectBean(Date createDate, String projectName, String projectFolderPath, String baseMapPath) {
        this.createDate = createDate;
        this.projectName = projectName;
        this.projectFolderPath = projectFolderPath;
        this.baseMapPath = baseMapPath;
    }


    @Override
    public String toString() {
        return "ProjectBean {" +
                "id=" + id +
                ", createDate=" + createDate +
                ", projectName=" + projectName +
                ", projectFolderPath=" + projectFolderPath +
                ", baseMapPath=" + baseMapPath +
                "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getProjectFolderPath() {
        return projectFolderPath;
    }

    public void setProjectFolderPath(String projectFolderPath) {
        this.projectFolderPath = projectFolderPath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBaseMapPath() {
        return baseMapPath;
    }

    public void setBaseMapPath(String baseMapPath) {
        this.baseMapPath = baseMapPath;
    }

}























