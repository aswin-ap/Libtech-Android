package com.wetwo.librarymanagment.ui.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.AllRequestAdapter;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityListAllApprovedBinding;
import com.wetwo.librarymanagment.ui.book.ListBooksActivity;
import com.wetwo.librarymanagment.ui.user.MyrequestActivity;
import com.wetwo.librarymanagment.utils.NetworkManager;
import com.wetwo.librarymanagment.utils.OnClickListener;
import com.wetwo.librarymanagment.utils.ReturnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListAllApprovedActivity extends BaseActivity implements OnClickListener, ReturnClick {
    private ActivityListAllApprovedBinding binding;
    private SessionManager sessionManager;
    List<RequestModel> requestModelList = new ArrayList();
    private final FirebaseFirestore fb = getFireStoreInstance();
    AllRequestAdapter adapter;
    RequestModel mModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityListAllApprovedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(ListAllApprovedActivity.this);
        initUi();
    }

    private void initUi() {
        binding.allReqRecyclerView.setHasFixedSize(true);
        adapter = new AllRequestAdapter(this, requestModelList, ListAllApprovedActivity.this,ListAllApprovedActivity.this);
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
        if (NetworkManager.isNetworkAvailable(ListAllApprovedActivity.this)) {
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
                                    model.setBookId(Objects.requireNonNull(documentSnapshot.get("bookId")).toString());
                                    model.setDate(Objects.requireNonNull(documentSnapshot.get("date")).toString());
                                    model.setUserId(Objects.requireNonNull(documentSnapshot.get("userId")).toString());
                                    model.setDocID(documentSnapshot.getId());
                                    model.setBookIdR((Long) documentSnapshot.get("bookIdR"));
                                    model.setBookName(Objects.requireNonNull(documentSnapshot.get("bookName")).toString());
                                    model.setUserName(Objects.requireNonNull(documentSnapshot.get("userName")).toString());
                                    model.setStatus(Objects.requireNonNull(documentSnapshot.get("status")).toString());

                                    if (model.getStatus().equals("approved")) {
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
                                    showToast(ListAllApprovedActivity.this, "No Approved books available now");
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            Log.e("exception in request", String.valueOf(e));
                            showToast(ListAllApprovedActivity.this, getString(R.string.error));
                        }
                    });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));

    }

    @Override
    public void onItemClick(int position) {
        Log.e("hit","onItemClick");
//        RequestModel model = requestModelList.get(position);

    }



    private void uploadToHistory(RequestModel uploadInfo) {
        if (NetworkManager.isNetworkAvailable(ListAllApprovedActivity.this)) {
            showLoading(this);

            Map<String, Object> user = new HashMap<>();
            user.put("date", currentDate());
            user.put("userId", sessionManager.getUserId());
            user.put("bookId", uploadInfo.getBookId());
            user.put("bookName", uploadInfo.getBookName());
            user.put("userName", sessionManager.getUserName());
            user.put("bookIdR", uploadInfo.getBookIdR());
            user.put("status", "returned");


            FirebaseFirestore fireStoreInstance = getFireStoreInstance();
            fireStoreInstance.collection("History")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            mDelete(uploadInfo.getDocID());
                            showToast(ListAllApprovedActivity.this, "Returned Successfully");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast(ListAllApprovedActivity.this, getString(R.string.error));
                }
            });
        } else {
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void mDelete(String path){



        fb.collection("Request").document(path)
                .delete()
                .addOnCompleteListener((OnCompleteListener) task -> {
                    Log.e("completed","complet"+task.isComplete());
                    hideLoading();
                    requestModelList.clear();
                    getRequestedBooks();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception in request", String.valueOf(e));
                        showToast(ListAllApprovedActivity.this, getString(R.string.error));
                    }
                });

    }

    @Override
    public void onItemClick(int position, RequestModel model) {
        Log.e("hit","onItemClick");
        mModel=model;
        onDialogShow("Return","Are you sure to Return ?");
    }
    public void onDialogShow(String msg1, String msg2) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ListAllApprovedActivity.this);
        builder.setIcon(R.drawable.splash);
        builder.setTitle(msg1);
        builder.setMessage(msg2);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadToHistory(mModel);
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