package com.excelmanager.app.models;

public class Category {
    private long id;
    private long fileId;
    private String name;

    public Category() {}
    public Category(long id, long fileId, String name) {
        this.id = id;
        this.fileId = fileId;
        this.name = name;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getFileId() { return fileId; }
    public void setFileId(long fileId) { this.fileId = fileId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}