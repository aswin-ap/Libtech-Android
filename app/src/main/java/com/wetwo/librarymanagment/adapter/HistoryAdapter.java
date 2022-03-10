package com.wetwo.librarymanagment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.wetwo.librarymanagment.R;
import com.wetwo.librarymanagment.data.model.RequestModel;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    Context context;
    List<RequestModel> requestModelList;



    public HistoryAdapter(Context context, List<RequestModel> TempList) {


        this.requestModelList = TempList;

        this.context = context;

    
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_history_layout, parent, false);

        HistoryAdapter.ViewHolder viewHolder = new HistoryAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RequestModel requestModel = requestModelList.get(position);


        //set data to textview
        holder.tvBookId.setText("Book ID: " + requestModel.getBookIdR());
        holder.tvBookName.setText(requestModel.getBookName());
        holder.tvUsername.setText("Requested by: " + requestModel.getUserName());
        holder.tvDate.setText("Requested on: " + requestModel.getDate());

    }

    @Override
    public int getItemCount() {

        return requestModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBookId, tvBookName, tvUsername, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvBookId = (TextView) itemView.findViewById(R.id.tv_bookId);

            tvBookName = (TextView) itemView.findViewById(R.id.tv_book_name);
            tvUsername = (TextView) itemView.findViewById(R.id.tv_user_name);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);

        }
    }
}