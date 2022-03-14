package com.wetwo.librarymanagment.ui.user;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityProfileBinding;
import com.wetwo.librarymanagment.utils.NetworkManager;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding binding;
    private SessionManager sessionManager;
    private FirebaseFirestore fb;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setupObserver();
    }

    private void setupObserver() {
        if (NetworkManager.isNetworkAvailable(ProfileActivity.this)) {
            showLoading(this);
            fb.collection("User")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                hideLoading();
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    if (documentSnapshot.get("userid").toString().equals(currentUserId)) {
                                        binding.editName.setText(documentSnapshot.get("username").toString());
                                        binding.editEmail.setText(documentSnapshot.get("email").toString());
                                        binding.textMobile.setText(documentSnapshot.get("mobile").toString());
                                        binding.tvPassword.setText(documentSnapshot.get("password").toString());
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast(ProfileActivity.this, e.getMessage());
                }
            });
        } else {
            showSnackBar(binding.getRoot(), getString(R.string.check_internet));
        }
    }

    private void initView() {
        fb = getFireStoreInstance();
        sessionManager = new SessionManager(ProfileActivity.this);
        currentUserId = sessionManager.getUserId();
        binding.imageClose.setOnClickListener(v -> onBackPressed());
        binding.ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkManager.isNetworkAvailable(ProfileActivity.this)) {
                    setupEditFields();
                    binding.layoutConfirmPassword.setVisibility(View.VISIBLE);
                } else
                    showSnackBar(binding.getRoot(), getString(R.string.check_internet));
            }
        });
        binding.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tvConfirmPassword != null && !binding.tvConfirmPassword.getText().toString().isEmpty()) {
                    if (binding.tvPassword.getText().toString().equals(binding.tvConfirmPassword.getText().toString())) {
                        updateProfile();
                    } else {
                        showToast(ProfileActivity.this, "Password validation failed");
                    }
                } else {
                    updateProfile();
                }
            }
        });
    }

    private void updateProfile() {
        if (NetworkManager.isNetworkAvailable(ProfileActivity.this)) {
            showLoading(this);
            Map<String, Object> user = new HashMap<>();
            user.put("name", binding.editName.getText().toString());
            user.put("email", binding.editEmail.getText().toString());
            user.put("password", binding.tvPassword.getText().toString());
            user.put("mobile", binding.textMobile.getText().toString());

            FirebaseFirestore fireStoreInstance = getFireStoreInstance();
            fireStoreInstance.collection("user")
                    .document(sessionManager.getDocumentId())
                    .update(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            hideLoading();
                            sessionManager.setUserName(binding.editName.getText().toString());
                            showToast(ProfileActivity.this, getString(R.string.update_success));
                            makeNotEditable();
                            setupObserver();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast(ProfileActivity.this, getString(R.string.error));
                }
            });
        } else {
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void setupEditFields() {
        makeEditable(binding.editName);
        makeEditable(binding.editEmail);
        makeEditable(binding.textMobile);
        makeEditable(binding.tvPassword);
        binding.buttonUpdate.setVisibility(View.VISIBLE);
    }

    private void makeNotEditable() {
        makeNonEditable(binding.editName);
        makeNonEditable(binding.editEmail);
        makeNonEditable(binding.textMobile);
        makeNonEditable(binding.tvPassword);
        binding.buttonUpdate.setVisibility(View.GONE);
        binding.layoutConfirmPassword.setVisibility(View.GONE);

    }

    private void makeEditable(TextInputEditText view) {
        view.setEnabled(true);
        view.setFocusable(true);
    }

    private void makeNonEditable(TextInputEditText view) {
        view.setEnabled(false);
        view.setFocusable(false);
    }
}