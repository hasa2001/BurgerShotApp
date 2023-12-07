package com.zaviron.burgershotapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.zaviron.burgershotapp.adapter.ProductAdapter;
import com.zaviron.burgershotapp.model.Product;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private ArrayList<Product> products;
    private SearchView searchView;
    private ProductAdapter productAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        searchView = fragment.findViewById(R.id.searchViewBar);
        searchView.clearFocus();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        products = new ArrayList<>();
        RecyclerView itemView = fragment.findViewById(R.id.recyclerView);
         productAdapter = new ProductAdapter(products, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        itemView.setLayoutManager(gridLayoutManager);
        itemView.setAdapter(productAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                search(text);
                return false;
            }
        });

        firestore.collection("items").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                //  products.clear();
                for (DocumentChange change : value.getDocumentChanges()) {
                    Product product = change.getDocument().toObject(Product.class);
                    switch (change.getType()) {
                        case ADDED:
                            products.add(product);

                        case MODIFIED:
                            break;
                        case REMOVED:
                            products.remove(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
        });
    }



    public void search(String text){
        firestore.collection("items").whereGreaterThanOrEqualTo("name",text).addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                 products.clear();
                for (DocumentChange change : value.getDocumentChanges()) {
                    Product product = change.getDocument().toObject(Product.class);
                    switch (change.getType()) {
                        case ADDED:
                            products.add(product);
                        case MODIFIED:
                            break;
                        case REMOVED:
                            products.remove(product);
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
        });
    }
}