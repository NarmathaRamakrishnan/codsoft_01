package com.example.to_dolist;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.material.button.MaterialButton;


public class Signin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAuth=FirebaseAuth.getInstance();
        EditText username1=findViewById(R.id.username1);
        EditText password1=findViewById(R.id.password1);
        Button signinbtn=findViewById(R.id.signinbtn);
        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=username1.getText().toString();
                String pass=password1.getText().toString();
                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(Signin.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(Signin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Signin.this, "User registration successful!", Toast.LENGTH_SHORT).show();
                                finish();
                                Toast.makeText(Signin.this, "Again Login!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Signin.this, "User registration failed!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            username1.setText("");
                            password1.setText("");
                        }
                    });
                }
            }
        });

    }
}