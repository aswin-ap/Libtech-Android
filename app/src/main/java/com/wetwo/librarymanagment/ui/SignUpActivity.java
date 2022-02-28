package com.wetwo.librarymanagment.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.databinding.ActivityLoginBinding;
import com.wetwo.librarymanagment.databinding.ActivitySignUpBinding;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}