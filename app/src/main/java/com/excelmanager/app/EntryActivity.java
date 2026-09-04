package com.excelmanager.app;

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
import com.excelmanager.app.models.Entry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EntryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TextView tvEmpty, tvTitle;
    private DatabaseHelper dbHelper;
    private List<Entry> entryList;
    private EntryAdapter adapter;
    private long categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        categoryId = getIntent().getLongExtra("category_id", -1);
        categoryName = getIntent().getStringExtra("category_name");

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recycler_entries);
        fabAdd = findViewById(R.id.fab_add_entry);
        tvEmpty = findViewById(R.id.tv_empty_entry);
        tvTitle = findViewById(R.id.tv_entry_title);

        tvTitle.setText(categoryName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entryList = new ArrayList<>();
        adapter = new EntryAdapter();
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddEntryDialog());
        loadEntries();
    }

    private void loadEntries() {
        entryList = dbHelper.getEntries(categoryId);
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(entryList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_entry, null);
        final EditText etContent = view.findViewById(R.id.et_entry_content);
        final TextView tvDate = view.findViewById(R.id.tv_entry_date);

        String today = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
        tvDate.setText("التاريخ التلقائي: " + today);

        builder.setView(view);
        builder.setTitle("إضافة بيان جديد");
        builder.setPositiveButton("حفظ", (dialog, which) -> {
            String content = etContent.getText().toString().trim();
            if (!content.isEmpty()) {
                dbHelper.addEntry(categoryId, content, today);
                loadEntries();
            } else {
                Toast.makeText(this, "يرجى كتابة البيان", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Entry entry = entryList.get(position);
            holder.tvContent.setText(entry.getContent());
            holder.tvDate.setText("📅 " + entry.getDate());
            holder.btnDelete.setOnClickListener(v -> {
                dbHelper.deleteEntry(entry.getId());
                loadEntries();
            });
        }

        @Override
        public int getItemCount() { return entryList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvContent, tvDate;
            ImageButton btnDelete;
            ViewHolder(View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_entry_content);
                tvDate = itemView.findViewById(R.id.tv_entry_date);
                btnDelete = itemView.findViewById(R.id.btn_delete_entry);
            }
        }
    }
}