package com.example.subbed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.ViewHolder> {

    private Context context;
    private List<Subscription> subs;

    public SubsAdapter(Context context, List<Subscription> subs) {
        this.context = context;
        this.subs = subs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sub,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription sub = subs.get(position);
        holder.bind(sub);
    }

    @Override
    public int getItemCount() {
        return subs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView ivImage;
        private TextView tvPrice;
        private TextView tvDays;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDays = itemView.findViewById(R.id.tvDays);
        }

        public void bind(Subscription sub) {
            // Bind the post data to the view elements
            tvName.setText(sub.getName());
            tvPrice.setText(sub.getPrice());
            tvDays.setText(sub.getDays());
        }

    }
}
