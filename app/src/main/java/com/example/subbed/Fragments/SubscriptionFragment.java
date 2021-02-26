package com.example.subbed.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.os.FileUtils;
import org.apache.commons.io.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.subbed.AddSubActivity;
import com.example.subbed.R;
import com.example.subbed.SubsAdapter;
import com.example.subbed.Subscription;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.Serializable;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubscriptionFragment extends Fragment {

    public static final String TAG = "SubscriptionFragment";

    private RecyclerView rvSubs;
    protected SubsAdapter adapter;
    protected List<Subscription> allSubs;
    private FloatingActionButton addBtn;

    public SubscriptionFragment() {
        // Required empty public constructor
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

//        allSubs = new ArrayList<>();
        loadItems();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddSubActivity.class);
                startActivityForResult(i, 1);
            }
        });

        adapter = new SubsAdapter(getContext(), allSubs);

        // steps to use the recycler view:
        // 0. create layout for one row in the list
        // 1. create the adapter
        // 2. create the data source
        // 3. set the adapter on the recycler view
        rvSubs.setAdapter(adapter);
        // 4. set the layout manager on the recycler view
        rvSubs.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Subscription new_sub;
            new_sub = Parcels.unwrap(data.getParcelableExtra("A new subscription"));
            allSubs.add(new_sub);
            adapter.notifyDataSetChanged();
            saveItems();
        }
    }

//    private File getDataFile(){
//        return new File(getContext().getFilesDir(), "subs");
//    }

    // This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            FileInputStream fis = getContext().openFileInput("subs");
            ObjectInputStream is = new ObjectInputStream(fis);
            allSubs = (List<Subscription>) is.readObject();
            is.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            Log.e("MainActivity", "Error reading items", e);
            allSubs = new ArrayList<>();
        }
    }

    // This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileOutputStream fos = getContext().openFileOutput("subs", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(allSubs);
            os.close();
            fos.close();
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }
}