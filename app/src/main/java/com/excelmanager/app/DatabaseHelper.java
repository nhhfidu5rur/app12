package com.excelmanager.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.excelmanager.app.models.Category;
import com.excelmanager.app.models.Entry;
import com.excelmanager.app.models.ExcelFile;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "excel_manager.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE files (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, created_date TEXT NOT NULL)");
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT, file_id INTEGER NOT NULL, name TEXT NOT NULL, FOREIGN KEY(file_id) REFERENCES files(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE entries (id INTEGER PRIMARY KEY AUTOINCREMENT, category_id INTEGER NOT NULL, content TEXT NOT NULL, date TEXT NOT NULL, FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS entries");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS files");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public long addFile(String name, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("created_date", date);
        long id = db.insert("files", null, values);
        db.close();
        return id;
    }

    public List<ExcelFile> getAllFiles() {
        List<ExcelFile> files = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM files ORDER BY id DESC", null);
        if (cursor.moveToFirst()) {
            do {
                files.add(new ExcelFile(cursor.getLong(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return files;
    }

    public ExcelFile getFile(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM files WHERE id=?", new String[]{String.valueOf(id)});
        ExcelFile file = null;
        if (cursor.moveToFirst()) {
            file = new ExcelFile(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
        }
        cursor.close();
        db.close();
        return file;
    }

    public void deleteFile(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("files", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public long addCategory(long fileId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("file_id", fileId);
        values.put("name", name);
        long id = db.insert("categories", null, values);
        db.close();
        return id;
    }

    public List<Category> getCategories(long fileId) {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM categories WHERE file_id=? ORDER BY id ASC", new String[]{String.valueOf(fileId)});
        if (cursor.moveToFirst()) {
            do {
                categories.add(new Category(cursor.getLong(0), cursor.getLong(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public void deleteCategory(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("categories", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public long addEntry(long categoryId, String content, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_id", categoryId);
        values.put("content", content);
        values.put("date", date);
        long id = db.insert("entries", null, values);
        db.close();
        return id;
    }

    public List<Entry> getEntries(long categoryId) {
        List<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM entries WHERE category_id=? ORDER BY id DESC", new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            do {
                entries.add(new Entry(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    public void deleteEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("entries", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}