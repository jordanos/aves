package com.example.aves.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aves.Domain.OrderDomain;
import com.example.aves.R;

import java.util.ArrayList;



public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {
    private ArrayList<OrderDomain> orderDomains;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public BalanceAdapter(ArrayList<OrderDomain> OrderDomains) {
        this.orderDomains = OrderDomains;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
        return new ViewHolder(inflate, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDomain order = orderDomains.get(position);
        holder.textID.setText("ORDER ID " + Integer.toString(order.getOrder_id()));
        holder.textItems.setText(Integer.toString(order.getItems_count()) + " Items");
        holder.textTotal.setText("ETB " + Double.toString(order.getTotal_price()) );
        holder.textStatus.setText(order.getOrder_status());
    }


    @Override
    public int getItemCount() {
        return orderDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textID, textItems, textTotal, textStatus;
        RelativeLayout relativeOrder;
        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textID = (TextView)itemView.findViewById(R.id.text_id);
            textItems = (TextView)itemView.findViewById(R.id.text_items);
            textTotal = (TextView)itemView.findViewById(R.id.text_total);
            textStatus = (TextView)itemView.findViewById(R.id.text_status);
            relativeOrder = (RelativeLayout)itemView.findViewById(R.id.relative_order);

            relativeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.OnItemClick(position);
                        }
                    }
                }
            });

        }
    }
}

