package com.excelmanager.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.excelmanager.app.models.ExcelFile;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private List<ExcelFile> fileList;
    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_files);
        fabAdd = findViewById(R.id.fab_add_file);
        tvEmpty = findViewById(R.id.tv_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileList = new ArrayList<>();
        adapter = new FileAdapter();
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddFileDialog());
        loadFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFiles();
    }

    private void loadFiles() {
        fileList = dbHelper.getAllFiles();
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(fileList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_file, null);
        final EditText etName = view.findViewById(R.id.et_file_name);

        builder.setView(view);
        builder.setTitle("إضافة ملف Excel جديد");
        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
                dbHelper.addFile(name, date);
                loadFiles();
            } else {
                Toast.makeText(this, "يرجى كتابة اسم الملف", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ExcelFile file = fileList.get(position);
            holder.tvName.setText(file.getName());
            holder.tvDate.setText(file.getCreatedDate());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(FileListActivity.this, CategoriesActivity.class);
                intent.putExtra("file_id", file.getId());
                intent.putExtra("file_name", file.getName());
                startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(FileListActivity.this, R.style.CustomDialog)
                    .setTitle("حذف الملف")
                    .setMessage("هل أنت متأكد من حذف " + file.getName() + "؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        dbHelper.deleteFile(file.getId());
                        loadFiles();
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
            });
        }

        @Override
        public int getItemCount() { return fileList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate;
            ImageButton btnDelete;
            ViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_file_name);
                tvDate = itemView.findViewById(R.id.tv_file_date);
                btnDelete = itemView.findViewById(R.id.btn_delete_file);
            }
        }
    }
}