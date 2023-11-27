package com.zaviron.burgershotapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        products = new ArrayList<>();
        RecyclerView itemView = fragment.findViewById(R.id.recyclerView);
        ProductAdapter productAdapter = new ProductAdapter(products, getContext());
        GridLayoutManager gridLayoutManager =new GridLayoutManager(getContext(),2);
        itemView.setLayoutManager(gridLayoutManager);
        itemView.setAdapter(productAdapter);



        firestore.collection("items").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
         //  products.clear();
                for (DocumentChange change: value.getDocumentChanges()){
                    Product product = change.getDocument().toObject(Product.class);
                    switch (change.getType()){
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