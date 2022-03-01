package com.wetwo.librarymanagment.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityMainHomeBinding;
import com.wetwo.librarymanagment.ui.book.ListBooksActivity;

public class MainHomeActivity extends BaseActivity {
    private SessionManager sessionManager;
    private ActivityMainHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityMainHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(MainHomeActivity.this);
        btnClick();
    }

    private void btnClick() {
        binding.cardBook.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainHomeActivity.this, ListBooksActivity.class));
            }});

        binding.btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                onLogOutPress();
            }});
    }
    public void onLogOutPress() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainHomeActivity.this);
        builder.setTitle("Logout ?");
        builder.setMessage("Are you sure want to Logout ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sessionManager.clear();
                Intent intent = new Intent(MainHomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                dialogInterface.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();

    }
}