package com.zaviron.burgershotapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zaviron.burgershotapp.adapter.CartAdapter;
import com.zaviron.burgershotapp.adapter.ProductAdapter;
import com.zaviron.burgershotapp.model.Cart;
import com.zaviron.burgershotapp.model.Product;

import java.util.ArrayList;


public class CartFragment extends Fragment {

    public static final String TAG =Fragment.class.getName();

    private ArrayList<Cart> cartItems;
    private FirebaseFirestore firestore;
    private FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);
        cartItems = new ArrayList<>();
        RecyclerView recyclerView = fragment.findViewById(R.id.cartRecyclerView);
        CartAdapter cartAdapter = new CartAdapter(cartItems, getContext());
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String client_id =user.getUid();
        Log.i(TAG,client_id);
        firestore =FirebaseFirestore.getInstance();

        firestore.collection("cart").whereEqualTo("client_id",client_id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for (DocumentChange change: value.getDocumentChanges()){
                    Cart cart = change.getDocument().toObject(Cart.class);
                    switch (change.getType()){
                        case ADDED:
                            cartItems.add(cart);
                        case MODIFIED:
                            break;
                        case REMOVED:
                          cartItems.remove(cart);
                    }
                }

                cartAdapter.notifyDataSetChanged();
            }
        });

//        firestore.collection("cart").whereEqualTo("client_id",client_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                cartItems.clear();
//                for (QueryDocumentSnapshot snapshot : task.getResult()){
//                    Cart cart = snapshot.toObject(Cart.class);
//                    cartItems.add(cart);
//
//
//                }
//
//                cartAdapter.notifyDataSetChanged();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.out.println("Error");
//            }
//        });








    }
}