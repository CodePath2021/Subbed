package com.example.subbed.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subbed.AddSubActivity;
import com.example.subbed.R;
import com.example.subbed.SubsAdapter;
import com.example.subbed.Subscription;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.util.List;

//import android.os.FileUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    public static final String TAG = "SubscriptionFragment";

    private RecyclerView rvSubs;
    protected SubsAdapter adapter;
    protected List<Subscription> allSubs;
    private FloatingActionButton addBtn;

    public SubscriptionFragment(List<Subscription> subs) {
        allSubs = subs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSubs = view.findViewById(R.id.rvSubs);
        addBtn = view.findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddSubActivity.class);
                startActivityForResult(i, 1);
            }
        });

        adapter = new SubsAdapter(getContext(), allSubs, this);

        // steps to use the recycler view:
        // 0. create layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvSubs.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        rvSubs.setLayoutManager(new LinearLayoutManager(getContext()));

        // attach the itemTouchHelper to the recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvSubs);
    }

    /**
     * This function is called when we come back from AddSubActivity or DetailActivity
     * @param requestCode - 1 for AddSub, 2 for Detail
     * @param resultCode - 1 for AddSub, 2 for Detail
     * @param data - the data that is sent back from AddSubActivity or DetailActivity
     */
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Subscription sub;

        // coming back from the AddSubActivity
        if (requestCode == 1 && data != null) {
            sub = Parcels.unwrap(data.getParcelableExtra("A new subscription"));
            allSubs.add(sub);
            adapter.notifyDataSetChanged();
        }

        // coming back from the DetailActivity
        else if (requestCode == 2 && data != null) {
            sub = Parcels.unwrap(data.getParcelableExtra("Update subscription"));
            for (int i = 0; i < allSubs.size(); i++) {
                Subscription curr_sub = allSubs.get(i);
                // find the sub that has been modified, update its attribute in the recycler view
                if (curr_sub.getName().equals(sub.getName())) {
                    curr_sub.setPrice(sub.getPrice());
                    curr_sub.setType(sub.getType());
                    curr_sub.setNextBillingDate(sub.getNextBillingYear(),
                            sub.getNextBillingMonth(), sub.getNextBillingDay());
                    curr_sub.setColor(sub.getColor());
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    // Swipe left or right to delete an item from the recycler view
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteSubscription(allSubs.get(viewHolder.getAdapterPosition()));
            allSubs.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
        }
    };

    private void deleteSubscription(Subscription sub) {
        sub.deleteInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Error while deleting", e);
                // TODO: error handling
            }
            Log.d(TAG, "Subscription delete was successful!");
        });
    }

    /**
     * We can use this method to send the updated subs back to MainActivity for uses on other screens
     * @return allSubs
     */
    public List<Subscription> getUpdatedSubs() {
        return allSubs;
    }

}