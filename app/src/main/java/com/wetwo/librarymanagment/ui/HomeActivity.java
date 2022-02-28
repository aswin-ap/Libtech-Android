package com.wetwo.librarymanagment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityAddBookBinding;
import com.wetwo.librarymanagment.databinding.ActivityHomeBinding;
import com.wetwo.librarymanagment.ui.book.ListBooksActivity;

public class HomeActivity extends BaseActivity {
    private SessionManager sessionManager;
    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(HomeActivity.this);
        btnClick();
    }

    private void btnClick() {
        binding.cardBook.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
           startActivity(new Intent(HomeActivity.this, ListBooksActivity.class));
        }});
        binding.cardStudents.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }});
        binding.btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sessionManager.clear();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }});

    }
}