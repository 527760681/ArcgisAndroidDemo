package com.example.application1.DatabaseUtils;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

public class ItemPointBean {
    public static final String COLUMNNAME_ID = "id";
    public static final String COLUMNNAME_PROJECT_ID = "projectID";
    public static final String COLUMNNAME_ITEM_NAME = "itemName";
    public static final String COLUMNNAME_IMAGE_DIR = "imageDir";
    public static final String COLUMNNAME_X = "x";
    public static final String COLUMNNAME_Y = "y";
    public static final String COLUMNNAME_NOTE = "note";


    @DatabaseField(generatedId = true, columnName = COLUMNNAME_ID, useGetSet = true)
    private int id;

    @DatabaseField(columnName = COLUMNNAME_PROJECT_ID, useGetSet = true)
    private int projectID;

    @DatabaseField(columnName = COLUMNNAME_ITEM_NAME, useGetSet = true)
    private String itemName;

    @DatabaseField(columnName = COLUMNNAME_IMAGE_DIR, useGetSet = true)
    private String imageDir;

    @DatabaseField(columnName = COLUMNNAME_X, useGetSet = true)
    private double x;

    @DatabaseField(columnName = COLUMNNAME_Y, useGetSet = true)
    private double y;

    @DatabaseField(columnName = COLUMNNAME_NOTE, useGetSet = true)
    private String note;

    public ItemPointBean() {

    }

    public ItemPointBean(int projectID, String itemName, String imageDir, double x, double y, String note) {
        this.projectID = projectID;
        this.itemName = itemName;
        this.imageDir = imageDir;
        this.x = x;
        this.y = y;
        this.note = note;
    }

    @Override
    public String toString() {
        return "ItemPointBean {" +
                "id=" + id +
                ", projectID=" + projectID +
                ", itemName=" + itemName +
                ", imageDir=" + imageDir +
                ", x=" + x +
                ", y=" + y +
                ", note=" + note +
                "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getImageDir() {
        return imageDir;
    }

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
