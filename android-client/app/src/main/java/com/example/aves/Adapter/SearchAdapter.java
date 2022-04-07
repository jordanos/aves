package com.example.aves.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aves.Activity.DetailActivity;
import com.example.aves.Domain.ContentDomain;
import com.example.aves.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<ContentDomain> contentDomain;

    public SearchAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
//        void deleteItem(int position);
    }

    public void setOnItemClickListener(SearchAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public SearchAdapter(ArrayList<ContentDomain> contentDomain) {
        this.contentDomain = contentDomain;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_search, parent, false);

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
                Toast.makeText(holder.itemView.getContext(), content.getId(), Toast.LENGTH_SHORT).show();
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

        public ViewHolder(@NonNull View itemView, final SearchAdapter.OnItemClickListener listener, final Context context) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.textViewSearchTitle);
            likes = (TextView)itemView.findViewById(R.id.textViewSearchLikes);
            pic = (ImageView) itemView.findViewById(R.id.imageViewSearchThumbnail);
            contentHolder = (LinearLayout) itemView.findViewById(R.id.searchContentHolder);
        }
    }
}