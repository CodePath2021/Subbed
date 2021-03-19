package com.example.subbed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconPack;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SubsAdapter extends RecyclerView.Adapter<SubsAdapter.ViewHolder> implements Filterable {

    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    public OnLongClickListener longClickListener;
    private Context context;
    private List<Subscription> subs;
    private Fragment fragment;      // reference to the current fragment used for starting the update subscription activity

    private List<Subscription> subsCopy;    // a copy of subs, used for filtering the list by searching

    public SubsAdapter(Context context, List<Subscription> subs, Fragment fragment, OnLongClickListener longClickListener) {
        this.context = context;
        this.subs = subs;
        this.fragment = fragment;
        this.longClickListener = longClickListener;
        subsCopy = new ArrayList<>(subs);
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
            tvPrice.setText(String.format("$%.2f", sub.getPrice()));
            if(sub.computeRemainingDays() <= 1)
                tvDays.setText(sub.computeRemainingDays() + " Day");
            else
                tvDays.setText(sub.computeRemainingDays() + " Days");

            App newApp = new App(context);
            IconPack pack = newApp.getIconPack();
            Icon myIcon = pack.getIcon(sub.getIconId());
            ivImage.setImageDrawable(myIcon.getDrawable());
            ivImage.setColorFilter(0xFFFFFF);   // want to make the icon white, but failed

            String hexColor = sub.getColor();
            card.setCardBackgroundColor(Color.parseColor(hexColor));

            // set the clicker on each item in the recycler view
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("subscription", Parcels.wrap(sub));
                    fragment.startActivityForResult(i, 2);
                }
            });
            // set the long clicker on each item in the recycler view
            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Notify the listener which position was long pressed
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }

    /**
     * Get the subscription filter that we create below
     * @return
     */
    @Override
    public Filter getFilter() {
        return subFilter;
    }

    private Filter subFilter = new Filter() {
        /**
         *Perform the filtering
         * @param constraint - the user input
         * @return
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Subscription> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subsCopy);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Subscription sub : subsCopy) {
                    if (sub.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(sub);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            subs.clear();
            subs.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };
}
