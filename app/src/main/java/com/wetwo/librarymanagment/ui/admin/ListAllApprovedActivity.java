package com.wetwo.librarymanagment.ui.admin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityListAllApprovedBinding;
import com.wetwo.librarymanagment.utils.NetworkManager;
import com.wetwo.librarymanagment.utils.OnClickListener;
import com.wetwo.librarymanagment.utils.ReturnClick;
import com.wetwo.librarymanagment.utils.ReturnRequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ListAllApprovedActivity extends BaseActivity implements OnClickListener, ReturnClick, ReturnRequestListener {
    private ActivityListAllApprovedBinding binding;
    private SessionManager sessionManager;
    List<RequestModel> requestModelList = new ArrayList();
    private final FirebaseFirestore fb = getFireStoreInstance();
    AllRequestAdapter adapter;
    RequestModel mModel;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Boolean messagePermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListAllApprovedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(ListAllApprovedActivity.this);
        initUi();
    }

    private void initUi() {
        checkPermission();
        binding.allReqRecyclerView.setHasFixedSize(true);
        adapter = new AllRequestAdapter(this, requestModelList, ListAllApprovedActivity.this, ListAllApprovedActivity.this, ListAllApprovedActivity.this);
        binding.allReqRecyclerView.setAdapter(adapter);
        getRequestedBooks();
        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            messagePermission = true;
        }
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
        Log.e("hit", "onItemClick");
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

    private void mDelete(String path) {


        fb.collection("Request").document(path)
                .delete()
                .addOnCompleteListener((OnCompleteListener) task -> {
                    Log.e("completed", "complet" + task.isComplete());
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
        Log.e("hit", "onItemClick");
        mModel = model;
        onDialogShow("Return", "Are you sure to Return ?");
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

    @Override
    public void onClickReturnRequestListener(int position) {
        RequestModel model = requestModelList.get(position);
        sendMessage(model, sessionManager.getMobile());
    }

    private void sendMessage(RequestModel model, String mobile) {
        if (messagePermission) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(mobile, null, "This is from Lib-Tech. Please return your book named:" + model.getBookName() + "\n Thanks", null, null);
                Toast.makeText(getApplicationContext(), "Message Sent",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                messagePermission = true;
            } else {
                messagePermission = false;
                Toast.makeText(getApplicationContext(),
                        "Please enable permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}