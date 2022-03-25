package com.wetwo.librarymanagment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.utils.OnClickListener;
import com.wetwo.librarymanagment.utils.ReturnClick;
import com.wetwo.librarymanagment.utils.ReturnRequestListener;

import java.util.List;

public class AllRequestAdapter extends RecyclerView.Adapter<AllRequestAdapter.ViewHolder> {

    Context context;
    List<RequestModel> requestModelList;
    OnClickListener listener;
    ReturnClick returnClick;
    ReturnRequestListener returnRequestListener;


    public AllRequestAdapter(Context context, List<RequestModel> TempList, OnClickListener listener, ReturnClick returnClick, ReturnRequestListener returnRequestListener) {


        this.requestModelList = TempList;

        this.context = context;

        this.listener = listener;
        this.returnClick = returnClick;
        this.returnRequestListener = returnRequestListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_request_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RequestModel requestModel = requestModelList.get(position);
        if (requestModel.getStatus().equals("approved")) {
            holder.approveButton.setText("Returned");
            holder.btnRequestReturn.setVisibility(View.VISIBLE);
        }

        //set data to textview
        holder.tvBookId.setText("Book ID: " + requestModel.getBookIdR());
        holder.tvBookName.setText(requestModel.getBookName());
        holder.tvUsername.setText("Requested by: " + requestModel.getUserName());
        holder.tvDate.setText("Requested on: " + requestModel.getDate());

        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("req", requestModel.getStatus());
                if (requestModel.getStatus().equals("approved")) {
                    returnClick.onItemClick(position, requestModel);
                } else {
                    listener.onItemClick(position);
                }
            }
        });
        holder.btnRequestReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               returnRequestListener.onClickReturnRequestListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {

        return requestModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBookId, tvBookName, tvUsername, tvDate;
        public MaterialButton approveButton, btnRequestReturn;

        public ViewHolder(View itemView) {
            super(itemView);

            tvBookId = (TextView) itemView.findViewById(R.id.tv_bookId);

            tvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);

            approveButton = (MaterialButton) itemView.findViewById(R.id.btn_approve);
            btnRequestReturn = (MaterialButton) itemView.findViewById(R.id.btn_request_return);
        }
    }
}