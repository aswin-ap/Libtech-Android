package com.wetwo.librarymanagment.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.AllRequestAdapter;
import com.wetwo.librarymanagment.adapter.HistoryAdapter;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityHistoryBinding;
import com.wetwo.librarymanagment.utils.NetworkManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {
    private ActivityHistoryBinding binding;
    private SessionManager sessionManager;
     private final FirebaseFirestore fb = getFireStoreInstance();
    HistoryAdapter adapter;
    List<RequestModel> mRequestModelList = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(HistoryActivity.this);
        initUi();
    }

    private void initUi() {
        binding.allReqRecyclerView.setHasFixedSize(true);
        adapter = new HistoryAdapter(this, mRequestModelList);
        binding.allReqRecyclerView.setAdapter(adapter);
        getRequestedBooks();
    }

    private void getRequestedBooks() {
        if (NetworkManager.isNetworkAvailable(HistoryActivity.this)) {
            if (!mRequestModelList.isEmpty()) {
                mRequestModelList.clear();
            }
            showLoading(this);
            binding.containerNoInternet.setVisibility(View.GONE);
            fb.collection("History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    RequestModel model = new RequestModel();
                                    model.setBookId(documentSnapshot.get("bookId").toString());
                                    model.setDate(documentSnapshot.get("date").toString());
                                    model.setUserId(documentSnapshot.get("userId").toString());
                                    model.setDocID(documentSnapshot.getId());
                                    model.setBookIdR((Long) documentSnapshot.get("bookIdR"));
                                    model.setBookName(documentSnapshot.get("bookName").toString());
                                    model.setUserName(documentSnapshot.get("userName").toString());
                                    model.setStatus(documentSnapshot.get("status").toString());

                                        mRequestModelList.add(model);


                                }
                                hideLoading();
                                if (mRequestModelList.size() > 0) {
                                    binding.allReqRecyclerView.setVisibility(View.VISIBLE);
                                    binding.ivNoData.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    binding.allReqRecyclerView.setVisibility(View.GONE);
                                    binding.ivNoData.setVisibility(View.VISIBLE);
                                    showToast(HistoryActivity.this, "No requests available now");
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            Log.e("exception in request", String.valueOf(e));
                            showToast(HistoryActivity.this, getString(R.string.error));
                        }
                    });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));

    }
}