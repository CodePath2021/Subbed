package com.example.subbed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;

public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.ViewHolder> {

    private Context context;
    private List<Subscription> subs;
    private Fragment fragment;

    public SubsAdapter(Context context, List<Subscription> subs, Fragment fragment) {
        this.context = context;
        this.subs = subs;
        this.fragment = fragment;
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
        private CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDays = itemView.findViewById(R.id.tvDays);
            card = itemView.findViewById(R.id.card);
        }

        public void bind(Subscription sub) {
            // Bind the post data to the view elements
            tvName.setText(sub.getName());
            tvPrice.setText("$" + sub.getPrice());
            tvDays.setText(sub.computeRemainingDays() + " Days");

            String hexColor = sub.getColor();
            card.setCardBackgroundColor(Color.parseColor(hexColor));

            // set the clicker on each item in the recycler view
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("subscription", Parcels.wrap(sub));

                    // some animation effect, not really useful
//                    ActivityOptionsCompat options = ActivityOptionsCompat.
//                            makeSceneTransitionAnimation((Activity)context, v, "profile");

                    fragment.startActivityForResult(i, 2);
                }
            });
        }
    }
}
