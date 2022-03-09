package com.wetwo.librarymanagment.ui.admin;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityListAllRequestBinding;
import com.wetwo.librarymanagment.ui.user.MyrequestActivity;
import com.wetwo.librarymanagment.utils.NetworkManager;

import java.util.ArrayList;
import java.util.List;

public class ListAllRequestActivity extends BaseActivity {
     private SessionManager sessionManager;
    private ActivityListAllRequestBinding binding;
    List<RequestModel> requestModelList = new ArrayList();
    private final FirebaseFirestore fb = getFireStoreInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         binding=ActivityListAllRequestBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());
        sessionManager = new SessionManager(ListAllRequestActivity.this);

    }

    private void getRequestedBooks() {

        if (NetworkManager.isNetworkAvailable(ListAllRequestActivity.this)) {
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
                                    model.setBookIdR((Integer) documentSnapshot.get("bookIdR"));
                                    model.setBookName(documentSnapshot.get("bookName").toString());
                                    model.setUserName(documentSnapshot.get("userName").toString());

                                    requestModelList.add(model);

                                }



                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("exception in request", String.valueOf(e));
                            showToast(ListAllRequestActivity.this, getString(R.string.error));
                        }
                    });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));

    }
}