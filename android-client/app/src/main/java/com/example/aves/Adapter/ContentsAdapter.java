package com.example.aves.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aves.Activity.DetailActivity;
import com.example.aves.Activity.MainActivity;
import com.example.aves.Activity.ShowDetailActivity;
import com.example.aves.Domain.ContentDomain;
import com.example.aves.Domain.FoodDomain;
import com.example.aves.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ViewHolder> {
    ArrayList<ContentDomain> contentDomain;

    public ContentsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
//        void deleteItem(int position);
    }

    public void setOnItemClickListener(ContentsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ContentsAdapter(ArrayList<ContentDomain> contentDomain) {
        this.contentDomain = contentDomain;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_content, parent, false);

        return new ViewHolder(inflate, mListener, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContentDomain content = contentDomain.get(position);
        holder.title.setText(content.getTitle());
        holder.likes.setText((content.getLikes()));
        String imageUrl = content.getPic();
        if(!imageUrl.equals("")){
            imageUrl = "https" + imageUrl.substring(4);
            Picasso.with(holder.itemView.getContext()).load(imageUrl).fit().centerInside().into(holder.pic);
        }

        holder.contentHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("contentId", content.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return contentDomain.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, likes;
        ImageView pic;
        LinearLayout contentHolder;

        public ViewHolder(@NonNull View itemView, final ContentsAdapter.OnItemClickListener listener, final Context context) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textViewTitle);
            likes = (TextView)itemView.findViewById(R.id.textViewLikes);
            pic = (ImageView) itemView.findViewById(R.id.imageViewThumbnail);
            contentHolder = (LinearLayout) itemView.findViewById(R.id.contentHolder);

        }
    }
}