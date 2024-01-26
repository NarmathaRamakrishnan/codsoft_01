package com.example.to_dolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button getstarted=findViewById(R.id.getstarted);
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getstartedClicked();
            }
        });
        }
        public void getstartedClicked(){
            Toast.makeText(this,"Get started is Clicked!",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }
    }