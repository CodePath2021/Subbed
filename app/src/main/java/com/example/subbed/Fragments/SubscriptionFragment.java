package com.example.subbed.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subbed.AddSubActivity;
import com.example.subbed.R;
import com.example.subbed.SimpleFragmentPagerAdapter;
import com.example.subbed.SubsAdapter;
import com.example.subbed.Subscription;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    public static final String TAG = "SubscriptionFragment";

    // instances for recycler view
    private RecyclerView rvSubs;
    protected SubsAdapter adapter;
    protected List<Subscription> allSubs;

    // to notify ViewPager of data changes
    private SimpleFragmentPagerAdapter pagerAdapter;

    // other stuff
    private FloatingActionButton addBtn;
    SubsAdapter.OnLongClickListener onLongClickListener;

    /**
     * Constructor for the subscription fragment
     * @param subs - the global subscription list
     */
    public SubscriptionFragment(List<Subscription> subs, SimpleFragmentPagerAdapter simpleFragmentPagerAdapter) {
        allSubs = subs;
        this.pagerAdapter = simpleFragmentPagerAdapter;
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

        setHasOptionsMenu(true);

        rvSubs = view.findViewById(R.id.rvSubs);
        addBtn = view.findViewById(R.id.addBtn);
        // add a new subscription
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddSubActivity.class);
                startActivityForResult(i, 1);
            }
        });

        // attach the onLongClickListener to the adapter: long click to delete a subscription
        onLongClickListener =  new SubsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // ask for users' confirmation
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Deleting " + allSubs.get(position).getName())
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // notify the backend
                                deleteSubscription(allSubs.get(position));
                                // delete the item from the model
                                allSubs.remove(position);
                                // notify the adapter
                                adapter.notifyItemRemoved(position);
                                pagerAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("No", null).show();
            }
        };

        adapter = new SubsAdapter(getContext(), allSubs, this, onLongClickListener);

        // steps to use the recycler view:
        // 0. create layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvSubs.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        rvSubs.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Menu bar
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar.
        inflater.inflate(R.menu.menu_subscription, menu);
        // find the search icon
        MenuItem searchItem = menu.findItem(R.id.search_icon);
        // initialize the search view
        SearchView searchView = (SearchView) searchItem.getActionView();
        // remove the little search icon at the bottom right of the phone keyboard
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
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
            saveSubscription(sub);
            allSubs.add(sub);
            adapter.notifyDataSetChanged();
            pagerAdapter.notifyDataSetChanged();
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
                    curr_sub.setIconId(sub.getIconId());
                    updateSubscription(curr_sub);
                }
            }
            adapter.notifyDataSetChanged();
            pagerAdapter.notifyDataSetChanged();
        }
    }

    // All the interactions with the parse backend: create, delete, update

    private void saveSubscription(Subscription new_sub) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        new_sub.setUser(currentUser);
        new_sub.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Error while saving", e);
                // TODO: error handling
            }
            Log.d(TAG, "Subscription save was successful!");
        });
    }

    private void deleteSubscription(Subscription sub) {
        Log.i(TAG, "object ID: " + sub.getObjectId());
        sub.deleteInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Error while deleting", e);
                // TODO: error handling
            }
            Log.d(TAG, "Subscription delete was successful!");
        });
    }

    private void updateSubscription(Subscription sub) {
        sub.saveInBackground(e -> {
            if(e != null) {
                Log.e(TAG, "Error while updating", e);
                // TODO: error handling
            }
            Log.d(TAG, "Subscription update was successful!");
        });
    }

    /**
     * Several filters for the recycler view.
     * Sort the list of subscriptions based on the filter selected.
     * @param item - several filters
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {

            case R.id.byLowPrice:
                if (allSubs.size() > 0) {
                    Collections.sort(allSubs, new Comparator<Subscription>() {
                        @Override
                        public int compare(final Subscription object1, final Subscription object2) {
                            if (object1.getPrice() < object2.getPrice()) {
                                return -1;
                            } else if (object1.getPrice() > object2.getPrice()) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                adapter.notifyDataSetChanged();
                return true;
            case R.id.byHighPrice:
                if (allSubs.size() > 0) {
                    Collections.sort(allSubs, new Comparator<Subscription>() {
                        @Override
                        public int compare(final Subscription object1, final Subscription object2) {
                            if (object1.getPrice() > object2.getPrice()) {
                                return -1;
                            } else if (object1.getPrice() < object2.getPrice()) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                adapter.notifyDataSetChanged();
                return true;
            case R.id.byLowDays:
                if (allSubs.size() > 0) {
                    Collections.sort(allSubs, new Comparator<Subscription>() {
                        @Override
                        public int compare(final Subscription object1, final Subscription object2) {
                            if (object1.computeRemainingDays() < object2.computeRemainingDays()) {
                                return -1;
                            } else if (object1.computeRemainingDays() > object2.computeRemainingDays()) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                adapter.notifyDataSetChanged();
                return true;
            case R.id.byLateDate:
                if (allSubs.size() > 0) {
                    Collections.sort(allSubs, new Comparator<Subscription>() {
                        @Override
                        public int compare(final Subscription object1, final Subscription object2) {
                            if (object1.getCreatedAt().after(object2.getCreatedAt())) {
                                return -1;
                            } else if (object1.getCreatedAt().before(object2.getCreatedAt())) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                adapter.notifyDataSetChanged();
                return true;
            case R.id.byEarlyDate:
                if (allSubs.size() > 0) {
                    Collections.sort(allSubs, new Comparator<Subscription>() {
                        @Override
                        public int compare(final Subscription object1, final Subscription object2) {
                            if (object1.getCreatedAt().before(object2.getCreatedAt())) {
                                return -1;
                            } else if (object1.getCreatedAt().after(object2.getCreatedAt())) {
                                return 1;
                            }
                            return 0;
                        }
                    });
                }
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}