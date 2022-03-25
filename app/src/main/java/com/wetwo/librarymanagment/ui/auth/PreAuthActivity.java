package com.wetwo.librarymanagment.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import com.wetwo.librarymanagment.databinding.ActivityPreAuthBinding;

public class PreAuthActivity extends AppCompatActivity {
     private ActivityPreAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPreAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnAdmin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreAuthActivity.this, LoginActivity.class);
                startActivity(intent);
            }});
        binding.btnStudent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreAuthActivity.this, LoginActivity.class);
                startActivity(intent);
            }});
        binding.btnTeacher.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreAuthActivity.this, LoginActivity.class);
                startActivity(intent);
            }});

    }
}