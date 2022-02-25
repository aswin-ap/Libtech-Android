package com.wetwo.librarymanagment;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity {

    // This is for showing the progress
    Dialog mLoadingDialog;



    public void showLoading(Context context) {
        if (mLoadingDialog == null) {
            callLoader(context);
        } else {
            hideLoading();
            callLoader(context);
        }
    }

    private void callLoader(Context context) {
        mLoadingDialog = new Dialog(context);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setContentView(R.layout.dialog_loading_layout);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        mLoadingDialog.show();
    }

    // This is for hiding the progress
    public void hideLoading() {
        if (mLoadingDialog != null)
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.cancel();
                mLoadingDialog.getWindow().closeAllPanels();
            }
    }


    public  void showSnackBar(View snackBarView, String message) {
        Snackbar sb;
        sb = Snackbar.make(snackBarView, message, Snackbar.LENGTH_SHORT);
        sb.show();
    }

    public FirebaseFirestore getFireStoreInstance() {
        return FirebaseFirestore.getInstance();
    }

    public void showToast(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy hh.mm aa");
        String formattedDate = dateFormat.format(new Date()).toString();
        return formattedDate;
    }
}
