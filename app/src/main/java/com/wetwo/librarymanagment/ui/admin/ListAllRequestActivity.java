package com.wetwo.librarymanagment.ui.admin;


import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.AllRequestAdapter;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityListAllRequestBinding;
import com.wetwo.librarymanagment.ui.book.ListBooksActivity;
import com.wetwo.librarymanagment.utils.NetworkManager;
import com.wetwo.librarymanagment.utils.OnClickListener;
import com.wetwo.librarymanagment.utils.ReturnClick;

import java.util.ArrayList;
import java.util.List;

public class ListAllRequestActivity extends BaseActivity implements OnClickListener, ReturnClick {
    private SessionManager sessionManager;
    private ActivityListAllRequestBinding binding;
    List<RequestModel> requestModelList = new ArrayList();
    private final FirebaseFirestore fb = getFireStoreInstance();
    AllRequestAdapter adapter;
    int mPosition=0;

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
        adapter = new AllRequestAdapter(this, requestModelList, ListAllRequestActivity.this,ListAllRequestActivity.this);
        binding.allReqRecyclerView.setAdapter(adapter);
        getRequestedBooks();
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        mPosition=position;
        onDialogShow("Approve","Are sure to Approve ?");


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

    @Override
    public void onItemClick(int position, RequestModel model) {

    }
    public void onDialogShow(String msg1, String msg2) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ListAllRequestActivity.this);
        builder.setTitle(msg1);
        builder.setMessage(msg2);
        builder.setIcon(R.drawable.splash);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RequestModel model = requestModelList.get(mPosition);
                updateTable(model);
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