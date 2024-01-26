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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sigupClicked();

            }
        });
        TextView forgetpassword=findViewById(R.id.forgetpassword);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetClicked();
            }
        });
        mAuth= FirebaseAuth.getInstance();
        EditText username=findViewById(R.id.username);
        EditText password=findViewById(R.id.password);
        Button login=findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user=username.getText().toString();
                String pass=password.getText().toString();
                mAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Login is successful!", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Login.this,Home.class);
                            intent.putExtra("Username",user);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(Login.this, "Login failed!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        username.setText("");
                        password.setText("");
                    }
                });
            }
        });
    }
    public void sigupClicked(){
        Toast.makeText(this, "Sign up clicked", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Login.this,Signin.class);
        startActivity(intent);
    }
    public void forgetClicked(){
        Toast.makeText(this, "Forget password clicked", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(Login.this,ForgetPassword.class);
        startActivity(intent);
    }
}