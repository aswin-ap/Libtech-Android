package com.wetwo.librarymanagment.ui.admin;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.AllRequestAdapter;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityListAllRequestBinding;
import com.wetwo.librarymanagment.utils.NetworkManager;
import com.wetwo.librarymanagment.utils.OnClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListAllRequestActivity extends BaseActivity implements OnClickListener {
    private SessionManager sessionManager;
    private ActivityListAllRequestBinding binding;
    List<RequestModel> requestModelList = new ArrayList();
    private final FirebaseFirestore fb = getFireStoreInstance();
    AllRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListAllRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(ListAllRequestActivity.this);
        initUi();
    }

    private void initUi() {
        binding.allReqRecyclerView.setHasFixedSize(true);
        adapter = new AllRequestAdapter(this, requestModelList, ListAllRequestActivity.this);
        binding.allReqRecyclerView.setAdapter(adapter);
        getRequestedBooks();
    }

    private void getRequestedBooks() {
        if (NetworkManager.isNetworkAvailable(ListAllRequestActivity.this)) {
            if (!requestModelList.isEmpty()) {
                requestModelList.clear();
            }
            showLoading(this);
            binding.containerNoInternet.setVisibility(View.GONE);
            fb.collection("Request")
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

                                    if (model.getStatus().equals("request")) {
                                        requestModelList.add(model);
                                    }

                                }
                                hideLoading();
                                if (requestModelList.size() > 0) {
                                    binding.allReqRecyclerView.setVisibility(View.VISIBLE);
                                    binding.ivNoData.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    binding.allReqRecyclerView.setVisibility(View.GONE);
                                    binding.ivNoData.setVisibility(View.VISIBLE);
                                    showToast(ListAllRequestActivity.this, "No requests available now");
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            Log.e("exception in request", String.valueOf(e));
                            showToast(ListAllRequestActivity.this, getString(R.string.error));
                        }
                    });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));

    }

    @Override
    public void onItemClick(int position) {
        RequestModel model = requestModelList.get(position);
        updateTable(model);
    }

    private void updateTable(RequestModel model) {
        if (NetworkManager.isNetworkAvailable(this)) {
            binding.containerNoInternet.setVisibility(View.GONE);
            showLoading(this);
            String date = getCalculatedDate(model.getDate(), "dd-MMM-yy hh.mm aa", 21);
            fb.collection("Request")
                    .document(model.getDocID())
                    .update("status", "approved", "date", date)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            hideLoading();
                            showToast(ListAllRequestActivity.this, "Approved Successfully");
                            getRequestedBooks();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    Log.e("exception in request", String.valueOf(e));
                    showToast(ListAllRequestActivity.this, getString(R.string.error));
                }
            });
        } else {
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        }
    }
}