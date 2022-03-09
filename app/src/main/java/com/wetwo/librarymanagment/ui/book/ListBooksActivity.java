package com.wetwo.librarymanagment.ui.book;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wetwo.librarymanagment.BaseActivity;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.adapter.bookListingAdapter;
import com.wetwo.librarymanagment.data.model.ImageUploadInfo;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.data.prefrence.SessionManager;
import com.wetwo.librarymanagment.databinding.ActivityListBooksBinding;
import com.wetwo.librarymanagment.utils.NetworkManager;
import com.wetwo.librarymanagment.utils.OnClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListBooksActivity extends BaseActivity implements OnClickListener {
    private ActivityListBooksBinding binding;
    String Storage_Path = "library_book/";
    // Root Database Name for Firebase Database.
    String Database_Path = "All_Image_Uploads_Database";
    private SessionManager sessionManager;
    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;
    private final FirebaseFirestore fb = getFireStoreInstance();

    ProgressDialog progressDialog;
    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList();
    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter;

    Boolean adminStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBooksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUi();
        btnClick();
    }

    private void initUi() {

        showLoading(this);
        sessionManager = new SessionManager(ListBooksActivity.this);
        Log.e("sees name", sessionManager.getUserName());
        if (sessionManager.getUserName().equals("admin")) {
            binding.btnAdd.setVisibility(View.VISIBLE);
            adminStatus = true;
        } else {
            binding.btnAdd.setVisibility(View.GONE);
            adminStatus = false;

        }

        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        list.clear();
        getAllBooks();
    }

    private void btnClick() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  Log.e("list size", String.valueOf(list.size()));
                                                  Intent i = new Intent(ListBooksActivity.this,
                                                          AddBookActivity.class);
                                                  i.putExtra("id", list.size());
                                                  startActivity(i);
                                              }
                                          }
        );
    }

    private void getIm(String img_name) {
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mImageStorage.child(Storage_Path)
                .child(img_name);

        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    String imageUrl = downUri.toString();
                    Toast.makeText(ListBooksActivity.this, imageUrl, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListBooksActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getAllBooks() {

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        // Adding Add Value Event Listener to databaseReference.
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

    private void compareBooks(List<RequestModel> requestList) {
        for (int i = 0; i < this.list.size(); i++) {
            for (int j = 0; j < requestList.size(); j++) {
                if (list.get(i).getFirebaseId().equals(requestList.get(j).getBookId())) {

                    list.get(i).setRequest(true);
                }
            }
        }
        if (list.size() != 0) {
            hideLoading();
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.ivNoData.setVisibility(View.GONE);
            adapter = new bookListingAdapter(getApplicationContext(), list, adminStatus, ListBooksActivity.this);
            binding.recyclerView.setAdapter(adapter);
        } else {
            hideLoading();
            binding.recyclerView.setVisibility(View.GONE);
            binding.ivNoData.setVisibility(View.VISIBLE);
            showToast(ListBooksActivity.this, "No books found!!!");
        }
    }

    @Override
    public void onItemClick(int position) {
        ImageUploadInfo uploadInfo = list.get(position);
        uploadToRequest(uploadInfo);
    }

    /**
     * This method uploads the requested book to 'Request' table in firebase
     *
     * @param uploadInfo
     */
    private void uploadToRequest(ImageUploadInfo uploadInfo) {
        if (NetworkManager.isNetworkAvailable(ListBooksActivity.this)) {
            showLoading(this);

            Map<String, Object> user = new HashMap<>();
            user.put("date", currentDate());
            user.put("userId", sessionManager.getUserId());
            user.put("bookId", uploadInfo.getFirebaseId());
            user.put("bookName", uploadInfo.getBookName());
            user.put("userName", sessionManager.getUserName());
            user.put("bookIdR", uploadInfo.getBookID());
            user.put("status", "request");


            FirebaseFirestore fireStoreInstance = getFireStoreInstance();
            fireStoreInstance.collection("Request")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            hideLoading();
                            showToast(ListBooksActivity.this, "Requested Successfully");
                            getAllBooks();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideLoading();
                    showToast(ListBooksActivity.this, getString(R.string.error));
                }
            });
        } else {
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        }
    }

    private void getRequestedBooks() {
        List<RequestModel> requestModelList = new ArrayList();
        if (NetworkManager.isNetworkAvailable(ListBooksActivity.this)) {
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

                                    requestModelList.add(model);
                                }

                                compareBooks(requestModelList);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("exception in request", String.valueOf(e));
                    showToast(ListBooksActivity.this, getString(R.string.error));
                }
            });
        } else
            binding.containerNoInternet.setVisibility(View.VISIBLE);
        // showSnackBar(binding.getRoot(), getString(R.string.check_internet));

    }
}
