package com.excelmanager.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvError;
    private static final String CORRECT_PASSWORD = "000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvError = findViewById(R.id.tv_error);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();
                if (password.equals(CORRECT_PASSWORD)) {
                    tvError.setVisibility(View.GONE);
                    Intent intent = new Intent(LoginActivity.this, FileListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("كلمة المرور غير صحيحة! (جرب: 000)");
                    etPassword.setText("");
                }
            }
        });
    }
}