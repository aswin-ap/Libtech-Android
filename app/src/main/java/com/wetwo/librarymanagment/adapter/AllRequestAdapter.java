package com.wetwo.librarymanagment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.model.RequestModel;
import com.wetwo.librarymanagment.utils.OnClickListener;

import java.util.List;

public class AllRequestAdapter extends RecyclerView.Adapter<AllRequestAdapter.ViewHolder> {

    Context context;
    List<RequestModel> requestModelList;
    OnClickListener listener;


    public AllRequestAdapter(Context context, List<RequestModel> TempList, OnClickListener listener) {


        this.requestModelList = TempList;

        this.context = context;

        this.listener = listener;
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


        //set data to textview
        holder.tvBookId.setText("Book ID: " + requestModel.getBookIdR());
        holder.tvBookName.setText(requestModel.getBookName());
        holder.tvUsername.setText("Requested by: " + requestModel.getUserName());
        holder.tvDate.setText("Requested on: " + requestModel.getDate());

        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {

        return requestModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBookId, tvBookName, tvUsername, tvDate;
        public MaterialButton approveButton;

        public ViewHolder(View itemView) {
            super(itemView);

            tvBookId = (TextView) itemView.findViewById(R.id.tv_bookId);

            tvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);

            approveButton = (MaterialButton) itemView.findViewById(R.id.btn_approve);
        }
    }
}