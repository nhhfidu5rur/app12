package com.excelmanager.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.excelmanager.app.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd, fabExport;
    private TextView tvEmpty, tvTitle;
    private DatabaseHelper dbHelper;
    private List<Category> categoryList;
    private CategoryAdapter adapter;
    private long fileId;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        fileId = getIntent().getLongExtra("file_id", -1);
        fileName = getIntent().getStringExtra("file_name");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_categories);
        fabAdd = findViewById(R.id.fab_add_category);
        fabExport = findViewById(R.id.fab_export);
        tvEmpty = findViewById(R.id.tv_empty_cat);
        tvTitle = findViewById(R.id.tv_cat_title);

        tvTitle.setText(fileName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter();
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddCategoryDialog());
        fabExport.setOnClickListener(v -> checkPermissionAndExport());

        loadCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategories();
    }

    private void loadCategories() {
        categoryList = dbHelper.getCategories(fileId);
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(categoryList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        final EditText etName = view.findViewById(R.id.et_category_name);

        builder.setView(view);
        builder.setTitle("إضافة قائمة جديدة (مثال: سحبات)");
        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                dbHelper.addCategory(fileId, name);
                loadCategories();
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void checkPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                exportFile();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                exportFile();
            }
        } else {
            exportFile();
        }
    }

    private void exportFile() {
        try {
            ExcelExporter exporter = new ExcelExporter(this);
            String path = exporter.exportToExcel(fileId, fileName);
            Toast.makeText(this, "تم تصدير ملف Excel بنجاح في:\n" + path, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category category = categoryList.get(position);
            holder.tvName.setText(category.getName());
            int count = dbHelper.getEntries(category.getId()).size();
            holder.tvCount.setText(count + " سجل");

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(CategoriesActivity.this, EntryActivity.class);
                intent.putExtra("category_id", category.getId());
                intent.putExtra("category_name", category.getName());
                startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                dbHelper.deleteCategory(category.getId());
                loadCategories();
            });
        }

        @Override
        public int getItemCount() { return categoryList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvCount;
            ImageButton btnDelete;
            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_category_name);
                tvCount = itemView.findViewById(R.id.tv_entry_count);
                btnDelete = itemView.findViewById(R.id.btn_delete_category);
            }
        }
    }
}