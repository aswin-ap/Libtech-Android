package com.wetwo.librarymanagment.ui.user;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.myRequestAdapter;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityMyrequestBinding;
import com.wetwo.librarymanagment.utils.NetworkManager;

import com.wetwo.librarymanagment.utils.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class MyrequestActivity extends BaseActivity implements OnItemClickListener {
    private ActivityMyrequestBinding binding;
    private SessionManager sessionManager;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    List<ImageUploadInfo> list = new ArrayList();
    List<ImageUploadInfo> mNewList = new ArrayList();
    private final FirebaseFirestore fb = getFireStoreInstance();
    String Database_Path = "All_Image_Uploads_Database";
    ProgressDialog progressDialog;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyrequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initUi();

    }

    private void initUi() {
        sessionManager = new SessionManager(MyrequestActivity.this);
        showLoading(MyrequestActivity.this);
        list.clear();
        getAllBooks();
        getRequestedBooks();
    }


    private void getAllBooks() {
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
                    imageUploadInfo.setFirebaseId(postSnapshot.getKey());
                    imageUploadInfo.setRequest(false);
                    list.add(imageUploadInfo);
                }

                Log.e("itt", "" + list);
                getRequestedBooks();
                // Hiding the progress dialog.
//                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("itt", "" + databaseError);
                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });
    }

    private void getRequestedBooks() {
        List<RequestModel> requestModelList = new ArrayList();
        if (NetworkManager.isNetworkAvailable(MyrequestActivity.this)) {
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

                                    requestModelList.add(model);

                                }

                                compareBooks(requestModelList);

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("exception in request", String.valueOf(e));
                            showToast(MyrequestActivity.this, getString(R.string.error));
                        }
                    });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));

    }

    private void compareBooks(List<RequestModel> requestList) {
        mNewList.clear();
        if (requestList.size() == 0) {
            hideLoading();
            binding.recyclerView.setVisibility(View.GONE);
            binding.ivNoData.setVisibility(View.VISIBLE);
            showToast(MyrequestActivity.this, "No books found!!!");
        } else {
            for (int i = 0; i < this.list.size(); i++) {
                for (int j = 0; j < requestList.size(); j++) {
                    if (list.get(i).getFirebaseId().equals(requestList.get(j).getBookId())) {
                        mNewList.add(this.list.get(i));
                        list.get(i).setBookBuyer(requestList.get(j).getDocID());
//                        this.list.remove(i);
                    } else {
//                    if(!list.get(i).getFirebaseId().equals(requestList.get(j).getDocID()))
//                        list.get(i).setFirebaseId(requestList.get(j).getDocID());
                    }
                }


            }
            if (mNewList.size() != 0) {
                hideLoading();
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.ivNoData.setVisibility(View.GONE);
                adapter = new myRequestAdapter(getApplicationContext(), mNewList, false, MyrequestActivity.this);
                binding.recyclerView.setAdapter(adapter);
            } else {
                hideLoading();
                binding.recyclerView.setVisibility(View.GONE);
                binding.ivNoData.setVisibility(View.VISIBLE);
                showToast(MyrequestActivity.this, "No books found!!!");
            }
        }
    }
    private void mDelete(String path){



        fb.collection("Request").document(path)
                .delete()
                .addOnCompleteListener((OnCompleteListener) task -> {
                    Log.e("completed","complet"+task.isComplete());
                    hideLoading();
                    list.clear();
                    getAllBooks();
                    getRequestedBooks();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception in request", String.valueOf(e));
                        showToast(MyrequestActivity.this, getString(R.string.error));
                    }
                });

    }


    @Override
    public void onItemClick(int position, String DocId) {
        showLoading(MyrequestActivity.this);
        Log.e("iddd", "firebase" + DocId);
        mDelete(DocId);

    }
}