package com.example.trippacking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(openFileOutput("lists.txt", MODE_APPEND)));
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, NavActivity.class);
            startActivity(intent);
        }

        Button login = findViewById(R.id.id_login_btn);
        Button register = findViewById(R.id.id_register_btn);
        Button gmail = findViewById(R.id.id_gmail_btn);
        Button guest = findViewById(R.id.id_guest_btn);

        EditText email = findViewById(R.id.id_edit_email);
        EditText password = findViewById(R.id.id_edit_password);

        View.OnClickListener signInListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnCompleteListener<AuthResult> authResultListener = new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, NavActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Failed with message: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                };

                if (v.getId() == guest.getId()) {
                    mAuth.signInAnonymously().addOnCompleteListener(authResultListener);
                    return;
                }

                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();

                if (emailStr.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please provide an email.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (passwordStr.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please provide a password.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (v.getId() == register.getId()) mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(authResultListener);
                else if (v.getId() == login.getId()) mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(authResultListener);

            }
        };

        register.setOnClickListener(signInListener);
        login.setOnClickListener(signInListener);
        guest.setOnClickListener(signInListener);
        gmail.setVisibility(View.GONE);
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}