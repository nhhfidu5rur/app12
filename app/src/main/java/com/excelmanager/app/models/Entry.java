package com.excelmanager.app.models;

public class Entry {
    private long id;
    private long categoryId;
    private String content;
    private String date;

    public Entry() {}
    public Entry(long id, long categoryId, String content, String date) {
        this.id = id;
        this.categoryId = categoryId;
        this.content = content;
        this.date = date;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}