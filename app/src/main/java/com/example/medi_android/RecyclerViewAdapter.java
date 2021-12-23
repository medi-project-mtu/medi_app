package com.example.medi_android;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<String> profileDataTitle;
    private final List<String> profileDataContent;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final int cardViewType;

    public RecyclerViewAdapter(Activity context, List<String> profileDataTitle, List<String> profileDataContent, int cardViewType) {
        this.mInflater = LayoutInflater.from(context);
        this.profileDataContent = profileDataContent;
        this.profileDataTitle = profileDataTitle;
        this.cardViewType = cardViewType;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(cardViewType, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String title = profileDataTitle.get(position);
        String content = profileDataContent.get(position);
        holder.profileDataTitleTV.setText(title);
        holder.profileDataContentTV.setText(content);
    }


    @Override
    public int getItemCount() {
        return profileDataTitle.size();
    }

    String getItem(int id) {
        return profileDataTitle.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView profileDataTitleTV;
        TextView profileDataContentTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileDataContentTV = itemView.findViewById(R.id.profile_data_content);
            profileDataTitleTV = itemView.findViewById(R.id.profile_data_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
