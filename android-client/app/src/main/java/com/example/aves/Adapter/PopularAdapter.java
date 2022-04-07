package com.example.aves.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aves.Activity.ShowDetailActivity;
import com.example.aves.Domain.FoodDomain;
import com.example.aves.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
    ArrayList<FoodDomain> foodDomains;

    public PopularAdapter(ArrayList<FoodDomain> FoodDomains) {
        this.foodDomains = FoodDomains;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_popular, parent, false);

        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(foodDomains.get(position).getTitle());
        holder.fee.setText(String.valueOf(foodDomains.get(position).getFee()));
        Picasso.with(holder.itemView.getContext()).load(foodDomains.get(position).getPic()).fit().centerInside().into(holder.pic);
        holder.popularholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ShowDetailActivity.class);
                intent.putExtra("object", foodDomains.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return foodDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, fee;
        ImageView pic;
        TextView addBtn;
        ConstraintLayout popularholder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            fee = (TextView)itemView.findViewById(R.id.fee);
            pic = (ImageView) itemView.findViewById(R.id.pic);
            addBtn = (TextView) itemView.findViewById(R.id.addBtn);
            popularholder = (ConstraintLayout) itemView.findViewById(R.id.popular_holder);
        }
    }
}
